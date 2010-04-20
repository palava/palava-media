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

import java.io.InputStream;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import de.cosmocode.commons.TrimMode;
import de.cosmocode.json.JSONRenderer;
import de.cosmocode.json.RenderLevel;
import de.cosmocode.palava.model.base.AbstractEntity;

/**
 * Abstract base implementation of the {@link AssetBase} interface.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
@MappedSuperclass
public abstract class AbstractAsset extends AbstractEntity implements AssetBase {

    private String name;

    @Transient
    private transient InputStream stream;
    
    @Column(name = "store_identifier", updatable = false)
    private String storeIdentifier;
    
    private String title;

    @Lob
    @Column(columnDefinition = "clob")
    private String description;

    @Column(name = "expires_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;
    
    private boolean expired;

    @Override
    public InputStream getStream() {
        return stream;
    }
    
    @Override
    public void setStream(InputStream stream) {
        this.stream = stream;
    }
    
    @PrePersist
    @Override
    public void setCreated() {
        Preconditions.checkState(StringUtils.isNotBlank(getStoreIdentifier()), "Store identifier is not set");
        super.setCreated();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = TrimMode.NULL.apply(name);
    }

    @Override
    public String getStoreIdentifier() {
        return storeIdentifier;
    }
    
    @Override
    public void setStoreIdentifier(String storeIdentifier) {
        Preconditions.checkArgument(StringUtils.isNotBlank(storeIdentifier), "StoreIdentifier must not be blank");
        this.storeIdentifier = storeIdentifier;
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
    public boolean isExpiring() {
        return !isExpired() && (isExpirable() && getExpiresAt().before(new Date()));
    }
    
    @Override
    public void setExpired() {
        this.expired = true;
    }
    
    @Override
    public boolean isExpired() {
        return expired;
    }
    
    @Override
    public boolean isUnexpiring() {
        return isExpired() && (!isExpirable() || (isExpirable() && getExpiresAt().after(new Date())));
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
                key("isExpiring").value(isExpiring()).
                key("isExpired").value(isExpired()).
                key("isUnexpiring").value(isUnexpiring());
        }
        if (renderer.eq(RenderLevel.LONG)) {
            renderer.
                key("metaData").object(getMetaData());
        }

        return renderer;
    }

}
