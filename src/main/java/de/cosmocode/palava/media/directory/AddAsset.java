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

package de.cosmocode.palava.media.directory;

import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.cosmocode.palava.core.Registry;
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
@Description("Adds the specified asset to the given directory")
@Params({
    @Param(name = DirectoryConstants.DIRECTORY_ID, description = "The identifier of the directory"),
    @Param(name = AssetConstants.ASSET_ID, description = "The identifier of the asset"),
    @Param(
        name = DirectoryConstants.INDEX, type = "positive int",  
        description = "The desired index of the asset in the directory. A value of -1 adds the asset to the end.",
        optional = true, defaultValue = "-1" 
    )
})
@Return(name = DirectoryConstants.CURRENT_INDEX, description = "The current index of the asset")
@Throw(
    name = PersistenceException.class, 
    description = "If directory does not exist or updating failed"
)
@Singleton
public final class AddAsset implements IpcCommand {
    
    private final EntityService<DirectoryBase> directoryService;
    private final EntityService<AssetBase> assetService;
    
    private final DirectoryAddAssetEvent addAssetEvent;
    private final DirectoryAddedAssetEvent addedAssetEvent;
    
    @Inject
    public AddAsset(EntityService<DirectoryBase> directoryService, EntityService<AssetBase> assetService,
        Registry registry) {
        this.directoryService = Preconditions.checkNotNull(directoryService, "DirectoryService");
        this.assetService = Preconditions.checkNotNull(assetService, "AssetService");
        Preconditions.checkNotNull(registry, "Registry");
        this.addAssetEvent = registry.proxy(DirectoryAddAssetEvent.class);
        this.addedAssetEvent = registry.proxy(DirectoryAddedAssetEvent.class);
    }

    @RequiresPermissions(MediaPermissions.DIRECTORY_ADD_ASSET)
    @Transactional
    @Override
    public void execute(IpcCall call, Map<String, Object> result) throws IpcCommandExecutionException {
        final IpcArguments arguments = call.getArguments();
        
        final long directoryId = arguments.getLong(DirectoryConstants.DIRECTORY_ID);
        final long assetId = arguments.getLong(AssetConstants.ASSET_ID);
        final int index = arguments.getInt(DirectoryConstants.INDEX, -1);
        
        final DirectoryBase directory = directoryService.read(directoryId);
        final AssetBase asset = assetService.reference(assetId);
        
        addAssetEvent.eventDirectoryAddAsset(directory, asset);
        
        final List<AssetBase> assets = directory.getAssets();
        
        final int currentIndex;
        
        if (index == -1) {
            currentIndex = assets.size();
            assets.add(asset);
        } else {
            currentIndex = index;
            assets.add(index, asset);
        }
        
        directoryService.update(directory);
        
        addedAssetEvent.eventDirectoryAddedAsset(directory, asset);
        
        result.put(DirectoryConstants.CURRENT_INDEX, currentIndex);
    }

}
