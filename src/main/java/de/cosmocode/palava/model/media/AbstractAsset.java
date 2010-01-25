/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
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

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.google.common.collect.Maps;

import de.cosmocode.commons.TrimMode;
import de.cosmocode.json.JSONRenderer;
import de.cosmocode.json.RenderLevel;
import de.cosmocode.palava.model.base.AbstractEntity;

@MappedSuperclass
public abstract class AbstractAsset extends AbstractEntity implements AssetBase {

    private String name;
    
    private String title;

    // TODO text/mediumtext   
    private String description;
    
    // TODO annotation!
    private Map<String, String> metaData = Maps.newHashMap();

    @Column(name = "expires_at")
    private Date expiresAt;
    
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
    public Map<String, String> getMetaData() {
        return metaData;
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
        return isExpirable() && expiresAt.getTime() < System.currentTimeMillis();
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
                key("metaData").value(getMetaData()).
                key("expiresAt").value(getExpiresAt()).
                key("isExpirable").value(isExpirable()).
                key("isExpired").value(isExpired());
        }
        
        return renderer;
    }
    
}
