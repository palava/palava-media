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

import java.util.Map;

import javax.persistence.PersistenceException;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.cosmocode.palava.entity.EntityService;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommand.Description;
import de.cosmocode.palava.ipc.IpcCommand.Param;
import de.cosmocode.palava.ipc.IpcCommand.Throw;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.jpa.Transactional;
import de.cosmocode.palava.media.MediaPermissions;

/**
 * See below.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
@Description("Deletes a directory from the database. Assets in the given directory will NOT be deleted.")
@Param(name = DirectoryConstants.DIRECTORY_ID, description = "The identifier of the directory")
@Throw(name = PersistenceException.class, description = "If there is no directory with the given id or deletion failed")
@Singleton
public final class Delete implements IpcCommand {

    private final EntityService<DirectoryBase> service;
    
    @Inject
    public Delete(EntityService<DirectoryBase> service) {
        this.service = Preconditions.checkNotNull(service, "Service");
    }

    @RequiresPermissions(MediaPermissions.DIRECTORY_DELETE)
    @Transactional
    @Override
    public void execute(IpcCall call, Map<String, Object> result) throws IpcCommandExecutionException {
        final IpcArguments arguments = call.getArguments();
        final long directoryId = arguments.getLong(DirectoryConstants.DIRECTORY_ID);
        final DirectoryBase directory = service.reference(directoryId);
        service.delete(directory);
    }

}
