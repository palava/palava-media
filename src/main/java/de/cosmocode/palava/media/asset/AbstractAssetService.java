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

import java.io.IOException;
import java.io.InputStream;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.entity.AbstractEntityService;
import de.cosmocode.palava.entity.EntityService;
import de.cosmocode.palava.jpa.Transactional;
import de.cosmocode.palava.store.Store;

/**
 * Abstract implementation of the {@link EntityService} interface for {@link AssetBase}
 * which uses a {@link Store} to store binary data.
 *
 * @since 2.0
 * @author Willi Schoenborn
 * @param <T> the generic asset type
 */
public abstract class AbstractAssetService<T extends AssetBase> extends AbstractEntityService<T>
    implements AssetBaseService<T>, Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAssetService.class);

    private final AssetCreateEvent createEvent;
    private final AssetCreatedEvent createdEvent;
    private final AssetUpdateEvent updateEvent;
    private final AssetUpdatedEvent updatedEvent;
    
    @Inject
    public AbstractAssetService(Registry registry) {
        Preconditions.checkNotNull(registry, "Registry");
        
        this.createEvent = registry.proxy(AssetCreateEvent.class);
        this.createdEvent = registry.proxy(AssetCreatedEvent.class);
        this.updateEvent = registry.proxy(AssetUpdateEvent.class);
        this.updatedEvent = registry.proxy(AssetUpdatedEvent.class);
    }

    /**
     * Provides the store to use.
     * 
     * @return the corresponding binary store
     */
    protected abstract Store getStore();
    
    @Transactional
    @Override
    public T create(final T entity) {
        createEvent.eventAssetCreate(entity);
        
        final Store store = getStore();
        
        final String identifier;
        final InputStream stream = entity.getStream();
        
        try {
            identifier = store.create(stream);
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
        
        entity.setStoreIdentifier(identifier);
        
        try {
            final T returnValue = super.create(entity);
            createdEvent.eventAssetCreated(entity);
            return returnValue;
        /* CHECKSTYLE:OFF */
        } catch (RuntimeException e) {
        /* CHECKSTYLE:ON */
            entity.setStoreIdentifier(null);
            try {
                LOG.warn("Saving asset {} failed. Removing binary data from store", entity);
                store.delete(identifier);
            } catch (IOException inner) {
                LOG.warn("Unable to delete binary data from store for " + identifier, inner);
            }
            throw e;
        }
    };
    
    @Override
    public void readStream(T asset) throws PersistenceException {
        Preconditions.checkNotNull(asset, "Asset");
        if (asset.hasStream()) return;
        final InputStream stream;
        
        try {
            stream = getStore().read(asset.getStoreIdentifier());
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
        
        asset.setStream(stream);
    };
    
    @Override
    public T update(T entity) {
        updateEvent.eventAssetUpdate(entity);
        final T returnValue = super.update(entity);
        updatedEvent.eventAssetUpdated(entity);
        return returnValue;
    };

}
