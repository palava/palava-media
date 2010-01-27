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

package de.cosmocode.palava.services.media;

import java.io.File;

import org.hibernate.Session;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.services.store.ContentStore;

/**
 * 
 *
 * @author Willi Schoenborn
 */
public final class DefaultImageStore implements ImageStore, Initializable {

    private final ContentStore store;
    
    private final File filterDirectory;
    
    @Inject
    public DefaultImageStore(
        @ImageContentStore ContentStore store,
        @Named("imagestore.filterDirectory") File filterDirectory) {
        this.store = Preconditions.checkNotNull(store, "Store");
        this.filterDirectory = Preconditions.checkNotNull(filterDirectory);
    }

    @Override
    public File getFile(String storeKey, String filterName) {
        return new File(filterDirectory, filterName + "/" + storeKey);
    }
    
    @Override
    public ImageManager createImageManager(Session session) {
        return new ImageManager(this, store, session);
    }
    
    @Override
    public void initialize() {
        filterDirectory.mkdirs();
    }


}
