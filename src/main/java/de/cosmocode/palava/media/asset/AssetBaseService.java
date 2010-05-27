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
