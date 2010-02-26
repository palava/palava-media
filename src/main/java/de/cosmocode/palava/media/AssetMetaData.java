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

import de.cosmocode.palava.services.media.Asset;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Locale;


/**
 * @author Tobias Sarnowski
 */
@Entity
@IdClass(AssetMetaData.AssetMetaDataId.class)
public class AssetMetaData {

    public static class AssetMetaDataId implements Serializable {

        @ManyToOne(fetch = FetchType.EAGER, optional = false)
        private Asset asset;

        @Column(nullable = false)
        private String metaKey;


        public Asset getAsset() {
            return asset;
        }

        public void setAsset(Asset asset) {
            this.asset = asset;
        }

        public String getMetaKey() {
            return metaKey;
        }

        public void setMetaKey(String metaKey) {
            this.metaKey = metaKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final AssetMetaDataId that = (AssetMetaDataId) o;

            if (asset != null ? !asset.equals(that.asset) : that.asset != null) return false;
            if (metaKey != null ? !metaKey.equals(that.metaKey) : that.metaKey != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = asset != null ? asset.hashCode() : 0;
            result = 31 * result + (metaKey != null ? metaKey.hashCode() : 0);
            return result;
        }
    }

    @Id
    private String value;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Asset asset;

    @Column(nullable = false)
    private String metaKey;

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

}
