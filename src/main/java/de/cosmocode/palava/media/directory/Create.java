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
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.cosmocode.palava.entity.EntityService;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommand.Description;
import de.cosmocode.palava.ipc.IpcCommand.Return;
import de.cosmocode.palava.ipc.IpcCommand.Throw;
import de.cosmocode.palava.ipc.IpcCommand.Throws;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.jpa.Transactional;
import de.cosmocode.palava.media.MediaPermissions;

/**
 * See below.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
@Description("Creates a new directory")
@Return(name = DirectoryConstants.DIRECTORY, description = "The created directory")
@Throws({
    @Throw(name = PersistenceException.class, description = "If creating failed")
})
@Singleton
public final class Create implements IpcCommand {
    
    private final EntityService<DirectoryBase> service;
    private final Provider<DirectoryBase> provider;
    
    @Inject
    public Create(EntityService<DirectoryBase> service, Provider<DirectoryBase> provider) {
        this.service = Preconditions.checkNotNull(service, "Service");
        this.provider = Preconditions.checkNotNull(provider, "Provider");
    }

    @RequiresPermissions(MediaPermissions.DIRECTORY_CREATE)
    @Transactional
    @Override
    public void execute(IpcCall call, Map<String, Object> result) throws IpcCommandExecutionException {
        final DirectoryBase directory = provider.get();
        service.create(directory);
        result.put(DirectoryConstants.DIRECTORY, directory);
    }

}
