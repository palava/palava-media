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

package de.cosmocode.palava.model.media;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import de.cosmocode.json.JSONMapable;
import de.cosmocode.palava.model.base.EntityBase;

public interface AssetBase extends EntityBase, JSONMapable {
    
    /**
     * Allows ordering by expiration date, which will move the expired assets to the top.
     */
    Ordering<AssetBase> ORDER_BY_EXPIRATION = Ordering.natural().onResultOf(new Function<AssetBase, Date>() {
        
        @Override
        public Date apply(AssetBase from) {
            return from.getExpiresAt();
        }
        
    }).nullsLast();

    String getName();
    
    void setName(String name);
    
    String getTitle();
    
    void setTitle(String title);
    
    String getDescription();
    
    void setDescription(String description);

    /**
     * Left to the implementation if changes to the returned
     * map will affect internal state.
     * 
     * @return
     */
    Map<String, String> getMetaData();

    /**
     * Left to the implementation if changes to the
     * returned set will affect the internal association.
     * 
     * @return
     */
    Set<? extends DirectoryBase> getDirectories();

    Date getExpiresAt();
    
    void setExpiresAt(Date expiresAt);
    
    boolean isExpirable();
    
    boolean isExpired();
    
}
