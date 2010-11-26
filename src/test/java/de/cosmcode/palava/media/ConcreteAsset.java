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

package de.cosmcode.palava.media;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.Maps;

import de.cosmocode.palava.media.asset.AbstractAsset;
import de.cosmocode.palava.media.asset.AssetBase;
import de.cosmocode.rendering.Renderer;
import de.cosmocode.rendering.RenderingException;
import de.cosmocode.rendering.RenderingLevel;

/**
 * Dummy implementation of the {@link AssetBase} interface
 * which is used to check "compilability".
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
public final class ConcreteAsset extends AbstractAsset {

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public Set<ConcreteDirectory> getDirectories() {
        return ImmutableSet.of();
    }
    
    @Override
    public Map<String, String> getMetaData() {
        return Maps.newHashMap();
    }

    @Override
    public void render(Renderer renderer, RenderingLevel level) throws RenderingException {
        
    }
    
}
