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

package de.cosmocode.palava.media.assets;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.cosmocode.palava.entity.EntityService;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.ipc.IpcCommand.Description;
import de.cosmocode.palava.ipc.IpcCommand.Param;
import de.cosmocode.palava.ipc.IpcCommand.Params;
import de.cosmocode.palava.ipc.IpcCommand.Throw;
import de.cosmocode.palava.ipc.IpcCommand.Throws;
import de.cosmocode.palava.jpa.Transactional;
import de.cosmocode.palava.media.AssetBase;

/**
 * See below.
 *
 * @author Willi Schoenborn
 */
@Description("Updates an asset in the database. Default values will overwrite values from the datastore.")
@Params({
    @Param(name = Update.ASSET_ID, description = "The identifier of the asset"),
    @Param(
        name = Update.TITLE,
        type = "string",
        description = "The asset's title",
        optional = true,
        defaultValue = "null"
    ),
    @Param(
        name = Update.DESCRIPTION,
        type = "string",
        description = "The asset's description",
        optional = true,
        defaultValue = "null"
    ),
    @Param(
        name = Update.META_DATA,
        type = "map of strings",
        description = "Flat meta data structure associated with the specified asset. Null keys are not permitted",
        optional = true,
        defaultValue = "null"
    ),
    @Param(
        name = Update.EXPIRES_AT,
        type = "java timestamp (ms)",
        description = "The date the specified asset will expire",
        optional = true,
        defaultValue = "null"
    )
})
@Throws({
    @Throw(name = NullPointerException.class, description = "If metaData contains null keys"),
    @Throw(name = PersistenceException.class, description = "If no asset with the given id exists or update failed")
})
@Transactional
@Singleton
public final class Update implements IpcCommand {

    public static final String ASSET_ID = "assetId";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String META_DATA = "metaData";
    public static final String EXPIRES_AT = "expiresAt";

    private static final Logger LOG = LoggerFactory.getLogger(Update.class);

    private final EntityService<AssetBase> service;

    @Inject
    public Update(EntityService<AssetBase> service) {
        this.service = Preconditions.checkNotNull(service, "Service");
    }

    @Override
    public void execute(IpcCall call, Map<String, Object> result) throws IpcCommandExecutionException {
        final IpcArguments arguments = call.getArguments();

        final long assetId = arguments.getLong(ASSET_ID);

        final AssetBase asset = service.read(assetId);

        final String title = arguments.getString(TITLE, null);
        final String description = arguments.getString(DESCRIPTION, null);
        final Map<Object, Object> metaData = arguments.getMap(META_DATA, null);
        final Date expiresAt = arguments.getDate(EXPIRES_AT, null);

        asset.setTitle(title);
        asset.setDescription(description);

        asset.getMetaData().clear();

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

        service.update(asset);
    }

}
