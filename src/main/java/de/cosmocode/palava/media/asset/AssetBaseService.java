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

import javax.persistence.PersistenceException;

import de.cosmocode.palava.entity.EntityService;

/**
 * Extension of the {@link EntityService} interface for {@link AssetBase}s.
 *
 * @since 2.0
 * @author Willi Schoenborn
 * @param <T> the generic asset type
 */
public interface AssetBaseService<T extends AssetBase> extends EntityService<T> {

    /**
     * Reads the binary data of the specified asset's store identifier
     * from the associated store. Calling this method on an asset
     * which already contains a valid stream will do nothing.
     * 
     * @since 2.0
     * @param asset the asset
     * @throws NullPointerException if asset is null
     * @throws PersistenceException if reading the stream failed
     */
    void readStream(T asset) throws PersistenceException;
    
}
