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

package de.cosmocode.palava.media.asset.expire;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.lifecycle.Executable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import de.cosmocode.palava.entity.EntityService;
import de.cosmocode.palava.jpa.Transactional;
import de.cosmocode.palava.media.asset.AssetBase;
import de.cosmocode.palava.media.asset.AssetExpiredEvent;
import de.cosmocode.palava.media.asset.AssetUnexpiredEvent;
import de.cosmocode.palava.scope.UnitOfWork;

/**
 * A service which checks periodically for expired assets.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
final class AssetExpirationService implements Initializable, Executable, Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AssetExpirationService.class);

    private final EntityService<AssetBase> service;
    
    private final AssetExpiredEvent expiredEvent;
    
    private final AssetUnexpiredEvent unexpiredEvent;
    
    private final Provider<EntityManager> provider;

    @Inject
    public AssetExpirationService(EntityService<AssetBase> service, Registry registry,
        Provider<EntityManager> provider) {
        this.service = Preconditions.checkNotNull(service, "Service");
        Preconditions.checkNotNull(registry, "Registry");
        this.expiredEvent = registry.proxy(AssetExpiredEvent.class);
        this.unexpiredEvent = registry.proxy(AssetUnexpiredEvent.class);
        this.provider = Preconditions.checkNotNull(provider, "Provider");
    }
    
    @UnitOfWork
    @Override
    public void initialize() throws LifecycleException {
        try {
            checkQuery(AssetBase.EXPIRING);
            checkQuery(AssetBase.UNEXPIRING);
        } catch (IllegalArgumentException e) {
            throw new LifecycleException(e);
        }
    }
    
    private void checkQuery(String name) {
        LOG.debug("Checking for presence of named query {}", name);
        provider.get().createNamedQuery(name);
    }
    
    @Override
    public void execute() throws LifecycleException {
        run();
    }
    
    @Transactional
    @Override
    public void run() {
        checkExpiring();
        checkUnexpiring();
    }
    
    private void checkExpiring() {
        final List<AssetBase> assets = service.list(AssetBase.EXPIRING);
        assert Iterables.all(assets, AssetBase.IS_EXPIRING) : "Expected all assets to be expiring";
        
        LOG.info("Found {} expiring assets", assets.size());
        
        for (AssetBase asset : assets) {
            expiredEvent.eventAssetExpired(asset);
        }
        
//        assert Iterables.all(assets, AssetBase.IS_EXPIRED) : "Expected all assets to be expired";
    }
    
    private void checkUnexpiring() {
        final List<AssetBase> assets = service.list(AssetBase.UNEXPIRING);
        assert Iterables.all(assets, AssetBase.IS_UNEXPIRING) : "Expected all assets to be unexpiring";

        LOG.info("Found {} unexpiring assets", assets.size());
        
        for (AssetBase asset : assets) {
            unexpiredEvent.eventAssetUnexpired(asset);
        }
        
//        assert Iterables.all(assets, AssetBase.NOT_EXPIRED) : "Expected all assets to be not expired";
    }
    
}
