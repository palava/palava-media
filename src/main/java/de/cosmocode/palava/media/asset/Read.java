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

import java.util.Map;

import javax.persistence.PersistenceException;

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
import de.cosmocode.palava.ipc.IpcCommand.Return;
import de.cosmocode.palava.ipc.IpcCommand.Throw;
import de.cosmocode.palava.jpa.Transactional;
import de.cosmocode.palava.media.AssetBase;

/**
 * See below.
 *
 * @author Willi Schoenborn
 */
@Description("Reads an asset from the database")
@Param(name = AssetConstants.ASSET_ID, description = "The identifier of the asset")
@Return(name = AssetConstants.ASSET, description = "The found asset")
@Throw(name = PersistenceException.class, description = "If there is no asset with the given identifier")
@Singleton
public final class Read implements IpcCommand {

    private final EntityService<AssetBase> service;

    @Inject
    public Read(EntityService<AssetBase> service) {
        this.service = Preconditions.checkNotNull(service, "Service");
    }

    @Transactional
    @Override
    public void execute(IpcCall call, Map<String, Object> result) throws IpcCommandExecutionException {
        final IpcArguments arguments = call.getArguments();
        final long assetId = arguments.getLong(AssetConstants.ASSET_ID);
        final AssetBase asset = service.read(assetId);
        result.put(AssetConstants.ASSET, asset);
    }

}
