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
    
    @Override
    public boolean hasStream() {
        return stream != null;
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
    public void setExpired(boolean expired) {
        this.expired = expired;
    }
    
    @Override
    public boolean isExpired() {
        return expired;
    }
    
    @Override
    public boolean isUnexpiring() {
        return isExpired() && (!isExpirable() || (isExpirable() && getExpiresAt().after(new Date())));
    }
    
}
