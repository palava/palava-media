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

package de.cosmcode.palava.media;

import java.util.Map;

import com.google.common.collect.ImmutableSet;

import de.cosmocode.palava.media.AbstractAsset;
import de.cosmocode.palava.media.AssetBase;
import de.cosmocode.palava.media.AssetMetaData;

/**
 * Dummy implementation of the {@link AssetBase} interface
 * which is used to check "compilability".
 *
 * @author Willi Schoenborn
 */
public final class ConcreteAsset extends AbstractAsset {

    @Override
    public long getId() {
        return 0;
    }
    
    @Override
    public ImmutableSet<ConcreteDirectory> getDirectories() {
        return ImmutableSet.of();
    }

    @Override
    protected Map<String, AssetMetaData> getInternalMetaData() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected AssetMetaData newAssetMetaData(AssetBase asset, String key, String value) {
        throw new UnsupportedOperationException();
    }
    
    

}
