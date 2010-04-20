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
    implements Disposable {

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
    public T update(T entity) {
        updateEvent.eventAssetUpdate(entity);
        final T returnValue = super.update(entity);
        updatedEvent.eventAssetUpdated(entity);
        return returnValue;
    };

}
