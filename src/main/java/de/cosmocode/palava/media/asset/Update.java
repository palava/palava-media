/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.media.asset;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.PersistenceException;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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
@Description("Updates an asset in the database. Default values will overwrite values from the datastore.")
@Params({
    @Param(name = AssetConstants.ASSET_ID, description = "The identifier of the asset"),
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
@Return(name = AssetConstants.ASSET, description = "The updated asset")
@Throws({
    @Throw(name = NullPointerException.class, description = "If metaData contains null keys"),
    @Throw(name = PersistenceException.class, description = "If no asset with the given id exists or update failed")
})
@Singleton
public final class Update implements IpcCommand {

    private static final Logger LOG = LoggerFactory.getLogger(Update.class);

    private final EntityService<AssetBase> service;
    
    @Inject
    public Update(EntityService<AssetBase> service) {
        this.service = Preconditions.checkNotNull(service, "Service");
    }

    @RequiresPermissions(MediaPermissions.ASSET_UPDATE)
    @Transactional
    @Override
    public void execute(IpcCall call, Map<String, Object> result) throws IpcCommandExecutionException {
        final IpcArguments arguments = call.getArguments();

        final long assetId = arguments.getLong(AssetConstants.ASSET_ID);

        final AssetBase asset = service.reference(assetId);

        final String title = arguments.getString(AssetConstants.TITLE, null);
        final String description = arguments.getString(AssetConstants.DESCRIPTION, null);
        final Map<Object, Object> metaData = arguments.getMap(AssetConstants.META_DATA, null);
        final Date expiresAt = arguments.getDate(AssetConstants.EXPIRES_AT, null);

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
        
        result.put(AssetConstants.ASSET, asset);
    }

}
