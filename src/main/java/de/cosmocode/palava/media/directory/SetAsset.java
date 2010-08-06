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

package de.cosmocode.palava.media.directory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.entity.EntityService;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommand.Description;
import de.cosmocode.palava.ipc.IpcCommand.Param;
import de.cosmocode.palava.ipc.IpcCommand.Params;
import de.cosmocode.palava.ipc.IpcCommand.Throw;
import de.cosmocode.palava.ipc.IpcCommand.Throws;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.jpa.Transactional;
import de.cosmocode.palava.media.MediaPermissions;
import de.cosmocode.palava.media.asset.AssetBase;
import de.cosmocode.palava.media.asset.AssetConstants;

/**
 * See below.
 * 
 * @since 2.0
 * @author Willi Schoenborn
 */
@Description(
    "Sets an asset in a directory to a specified index. " +
    "All assets left of the new position will be shifted one place to the right."
)
@Params({
    @Param(name = DirectoryConstants.DIRECTORY_ID, description = "The identifier of the directory"),
    @Param(name = AssetConstants.ASSET_ID, description = "The identifier of the asset"),
    @Param(
        name = DirectoryConstants.INDEX, type = "positive int", 
        description = "The desired index of the asset in the directory."
    )
})
@Throws({
    @Throw(name = PersistenceException.class, description = "If asset or directory does not exist or updating failed"),
    @Throw(name = IllegalStateException.class, description = "If the specified asset does not belong the directory.")
})
@Singleton
public final class SetAsset implements IpcCommand {

    private static final Logger LOG = LoggerFactory.getLogger(SetAsset.class);
    
    private final EntityService<DirectoryBase> directoryService;
    private final EntityService<AssetBase> assetService;
    
    private final DirectoryPreSetAssetEvent preSetAssetEvent;
    private final DirectoryPostSetAssetEvent postSetAssetEvent;
    
    @Inject
    public SetAsset(EntityService<DirectoryBase> directoryService, 
        EntityService<AssetBase> assetService, Registry registry) {
        this.directoryService = Preconditions.checkNotNull(directoryService, "DirectoryService");
        this.assetService = Preconditions.checkNotNull(assetService, "AssetService");
        Preconditions.checkNotNull(registry, "Registry");
        this.preSetAssetEvent = registry.proxy(DirectoryPreSetAssetEvent.class);
        this.postSetAssetEvent = registry.proxy(DirectoryPostSetAssetEvent.class);
    }

    @RequiresPermissions(MediaPermissions.DIRECTORY_SET_ASSET)
    @Transactional
    @Override
    public void execute(IpcCall call, Map<String, Object> result) throws IpcCommandExecutionException {
        final IpcArguments arguments = call.getArguments();
        
        final long directoryId = arguments.getLong(DirectoryConstants.DIRECTORY_ID);
        final long assetId = arguments.getLong(AssetConstants.ASSET_ID);
        final int index = arguments.getInt(DirectoryConstants.INDEX);
        Preconditions.checkArgument(index >= 0, "index must not be negative, but was", index);
        
        final DirectoryBase directory = directoryService.read(directoryId);
        final AssetBase asset = assetService.reference(assetId);
        
        final List<AssetBase> assets = directory.getAssets();
        Preconditions.checkState(assets.contains(asset), "%s is not contained in %s", asset, directory);
        
        LOG.trace("Setting index of {} in {} to {}", new Object[] {
            asset, assets, index
        });
        
        preSetAssetEvent.eventDirectoryPreSetAsset(directory, asset);
        
        final int currentIndex = assets.indexOf(asset);
        
        if (currentIndex == index) {
            LOG.trace("{} already is at index {}", asset, index);
            return;
        } else if (currentIndex < index) {
            // rotate to the left
            Collections.rotate(assets.subList(currentIndex, index + 1), -1);
        } else {
            // rotate to the right
            Collections.rotate(assets.subList(index, currentIndex + 1), 1);
        }
        
        postSetAssetEvent.eventDirectoryPostSetAsset(directory, asset);
        
        LOG.trace("New state of assets: {}", assets);
    }

}
