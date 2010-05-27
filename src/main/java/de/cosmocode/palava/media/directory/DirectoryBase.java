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

import java.util.List;

import de.cosmocode.palava.media.asset.AssetBase;
import de.cosmocode.palava.model.base.EntityBase;

/**
 * Interface definition for directories.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
public interface DirectoryBase extends EntityBase {

    /**
     * Returns an ordered list of all assets in this directory.
     * 
     * @param <A> generic asset type
     * @return a list of all assets
     */
    <A extends AssetBase> List<A> getAssets();
    
}
