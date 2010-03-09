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

package de.cosmocode.palava.media;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.*;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import de.cosmocode.commons.TrimMode;
import de.cosmocode.json.JSONRenderer;
import de.cosmocode.json.RenderLevel;
import de.cosmocode.palava.model.base.AbstractEntity;

/**
 * Abstract base implementation of the {@link AssetBase} interface
 * which uses {@link AssetMetaData} to provide JPA compliant
 * mapping functionality.
 *
 * @author Willi Schoenborn
 */
@MappedSuperclass
public abstract class AbstractAsset extends AbstractEntity implements AssetBase {

    private static final Function<Entry<String, AssetMetaData>, Entry<String, String>> FUNCTION =
        new Function<Entry<String, AssetMetaData>, Entry<String, String>>() {
            
            @Override
            public Entry<String, String> apply(Entry<String, AssetMetaData> from) {
                return Maps.immutableEntry(from.getKey(), from.getValue().getValue());
            }
            
        };
    
    private String name;

    private String title;

    // TODO text/mediumtext
    private String description;

    @Column(name = "expires_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;

    // hidden adapter for metaData
    @Transient
    private final transient Map<String, String> adapter = new MetaDataAdapter();

    protected abstract Map<String, AssetMetaData> getInternalMetaData();
    
    protected abstract AssetMetaData newAssetMetaData(AssetBase asset, String key, String value);
    
    /**
     * A map adapter for {@link AbstractAsset#metaData} which translates
     * from String to {@link AssetMetaData} and vice versa.
     *
     * @author Willi Schoenborn
     */
    private class MetaDataAdapter extends AbstractMap<String, String> {
        
        @Override
        public Set<Entry<String, String>> entrySet() {
            return new AbstractSet<Entry<String, String>>() {

                @Override
                public Iterator<Entry<String, String>> iterator() {
                    return Iterators.transform(getInternalMetaData().entrySet().iterator(), FUNCTION);
                }

                @Override
                public int size() {
                    return getInternalMetaData().size();
                }
                
            };
        }
        
        @Override
        public String put(String key, String value) {
            final AssetMetaData old = getInternalMetaData().get(key);
            if (old == null) {
                final AssetMetaData data = newAssetMetaData(AbstractAsset.this, key, value);
                getInternalMetaData().put(key, data);
                return null;
            } else {
                final String oldValue = old.getValue();
                old.setValue(value);
                return oldValue;
            }
        }
        
    };
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = TrimMode.NULL.apply(name);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = TrimMode.NULL.apply(title);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = TrimMode.NULL.apply(description);
    }

    @Override
    public Date getExpiresAt() {
        return expiresAt;
    }

    @Override
    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean isExpirable() {
        return getExpiresAt() != null;
    }

    @Override
    public boolean isExpired() {
        return isExpirable() && getExpiresAt().getTime() < System.currentTimeMillis();
    }
    
    @Override
    public Map<String, String> getMetaData() {
        return adapter;
    }

    @Override
    public JSONRenderer renderAsMap(JSONRenderer renderer) {
        super.renderAsMap(renderer);

        if (renderer.eq(RenderLevel.TINY)) {
            renderer.
                key("name").value(getName()).
                key("title").value(getTitle());
        }
        if (renderer.eq(RenderLevel.MEDIUM)) {
            renderer.
                key("description").value(getDescription()).
                key("expiresAt").value(getExpiresAt()).
                key("isExpirable").value(isExpirable()).
                key("isExpired").value(isExpired());
        }
        if (renderer.eq(RenderLevel.LONG)) {
            renderer.
                key("metaData").object(getMetaData());
        }

        return renderer;
    }

}
