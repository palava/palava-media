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

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

import com.google.common.base.Preconditions;

import de.cosmocode.palava.services.media.Asset;

/**
 * An helper entity required by jpa to allow
 * mapping of {@link Map}s.
 * 
 * @author Tobias Sarnowski
 */
@Entity
@IdClass(AssetMetaData.AssetMetaDataId.class)
final class AssetMetaData {

    /**
     * Composite primary key for {@link AssetMetaData}.
     *
     * @author Willi Schoenborn
     */
    public static class AssetMetaDataId implements Serializable {

        private static final long serialVersionUID = 8691605576719374637L;

        @ManyToOne(fetch = FetchType.EAGER, optional = false)
        private Asset asset;

        @Column(name = "meta_key", nullable = false)
        private String key;


        public Asset getAsset() {
            return asset;
        }

        public void setAsset(Asset asset) {
            this.asset = asset;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final AssetMetaDataId that = (AssetMetaDataId) o;

            if (asset != null ? !asset.equals(that.asset) : that.asset != null) return false;
            if (key != null ? !key.equals(that.key) : that.key != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = asset != null ? asset.hashCode() : 0;
            result = 31 * result + (key != null ? key.hashCode() : 0);
            return result;
        }
    }

    @Id
    private String value;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private AssetBase asset;

    @Column(name = "meta_key", nullable = false)
    private String key;

    public AssetMetaData(AssetBase asset, String key, String value) {
        this.asset = Preconditions.checkNotNull(asset, "Asset");
        this.key = Preconditions.checkNotNull(key, "Key");
        this.value = value;
    }

    public AssetBase getAsset() {
        return asset;
    }

    public void setAsset(AssetBase asset) {
        this.asset = asset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
