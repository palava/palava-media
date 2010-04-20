/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.media.asset;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.PersistenceException;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.cosmocode.palava.entity.EntityService;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.ipc.IpcCommand.Description;
import de.cosmocode.palava.ipc.IpcCommand.Param;
import de.cosmocode.palava.ipc.IpcCommand.Params;
import de.cosmocode.palava.ipc.IpcCommand.Return;
import de.cosmocode.palava.ipc.IpcCommand.Throw;
import de.cosmocode.palava.ipc.IpcCommand.Throws;
import de.cosmocode.palava.jpa.Transactional;
import de.cosmocode.palava.media.MediaPermissions;

/**
 * See below.
 * 
 * @since 2.0
 * @author Willi Schoenborn
 */
@Description("Creates an asset in the database.")
@Params({
    @Param(
        name = AssetConstants.NAME,
        type = "name of the asset",
        description = "The (file) name of the asset."
    ),
    @Param(
        name = AssetConstants.BINARY,
        type = "base64 encoded string",
        description = "The binary data in base64 format."
    ),
    @Param(
        name = AssetConstants.TITLE,
        type = "string",
        description = "The asset's title",
        optional = true,
        defaultValue = "null"
    ),
    @Param(
        name = AssetConstants.DESCRIPTION,
        type = "string",
        description = "The asset's description",
        optional = true,
        defaultValue = "null"
    ),
    @Param(
        name = AssetConstants.META_DATA,
        type = "map of strings",
        description = "Flat meta data structure associated with the specified asset. Null keys are not permitted",
        optional = true,
        defaultValue = "null"
    ),
    @Param(
        name = AssetConstants.EXPIRES_AT,
        type = "java timestamp (ms)",
        description = "The date the specified asset will expire",
        optional = true,
        defaultValue = "null"
    )
})
@Return(name = AssetConstants.ASSET, description = "The new asset")
@Throws({
    @Throw(name = NullPointerException.class, description = "If metaData contains null keys"),
    @Throw(name = PersistenceException.class, description = "If no asset with the given id exists or update failed")
})
@Singleton
public final class Create implements IpcCommand {

    private static final Logger LOG = LoggerFactory.getLogger(Create.class);

    private final Provider<AssetBase> provider;
    private final EntityService<AssetBase> service;

    @Inject
    public Create(Provider<AssetBase> provider, EntityService<AssetBase> service) {
        this.provider = Preconditions.checkNotNull(provider, "Provider");
        this.service = Preconditions.checkNotNull(service, "Service");
    }

    @RequiresPermissions(MediaPermissions.ASSET_CREATE)
    @Transactional
    @Override
    public void execute(IpcCall call, Map<String, Object> result) throws IpcCommandExecutionException {
        final IpcArguments arguments = call.getArguments();

        final AssetBase asset = provider.get();

        final String name = arguments.getString(AssetConstants.NAME);
        final byte[] binary = arguments.getString(AssetConstants.BINARY).getBytes(Charset.forName("UTF-8"));
        final String title = arguments.getString(AssetConstants.TITLE, null);
        final String description = arguments.getString(AssetConstants.DESCRIPTION, null);
        final Map<Object, Object> metaData = arguments.getMap(AssetConstants.META_DATA, null);
        final Date expiresAt = arguments.getDate(AssetConstants.EXPIRES_AT, null);

        asset.setName(name);
        
        final InputStream stream = new Base64InputStream(new ByteArrayInputStream(binary));
        asset.setStream(stream);
        
        asset.setTitle(title);
        asset.setDescription(description);

        if (metaData == null) {
            LOG.debug("No meta data received");
        } else {
            LOG.debug("Adding new metaData {} to {}", metaData, asset);
            for (Entry<Object, Object> entry : metaData.entrySet()) {
                final String key = Preconditions.checkNotNull(
                    entry.getKey(), "Key with value {} is null", entry.getValue()
                ).toString();
                final String value = entry.getValue() == null ? null : entry.getValue().toString();
                asset.getMetaData().put(key, value);
            }
        }

        asset.setExpiresAt(expiresAt);

        try {
            service.create(asset);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                throw new IpcCommandExecutionException(e);
            }
        }
        
        result.put(AssetConstants.ASSET, asset);
    }

}
