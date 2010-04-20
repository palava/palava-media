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
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.Maps;

import de.cosmocode.palava.media.asset.AbstractAsset;
import de.cosmocode.palava.media.asset.AssetBase;

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
    @SuppressWarnings("unchecked")
    public Set<ConcreteDirectory> getDirectories() {
        return ImmutableSet.of();
    }
    
    @Override
    public Map<String, String> getMetaData() {
        return Maps.newHashMap();
    }

}
