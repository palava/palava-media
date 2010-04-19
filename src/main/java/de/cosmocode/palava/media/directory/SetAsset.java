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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
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
import de.cosmocode.palava.media.DirectoryBase;
import de.cosmocode.palava.media.MediaPermissions;
import de.cosmocode.palava.media.asset.AssetConstants;

/**
 * See below.
 *
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
    
    @Inject
    public SetAsset(EntityService<DirectoryBase> directoryService, 
        EntityService<AssetBase> assetService) {
        this.directoryService = Preconditions.checkNotNull(directoryService, "DirectoryService");
        this.assetService = Preconditions.checkNotNull(assetService, "AssetService");
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
        
        LOG.trace("New state of assets: {}", assets);
    }

}
