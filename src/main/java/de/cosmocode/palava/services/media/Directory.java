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

package de.cosmocode.palava.services.media;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.IndexColumn;
import org.json.JSONException;
import org.json.extension.JSONConstructor;
import org.json.extension.JSONEncoder;

import com.google.inject.internal.Lists;

import de.cosmocode.json.JSON;
import de.cosmocode.json.JSONRenderer;
import de.cosmocode.palava.bridge.content.ContentConverter;
import de.cosmocode.palava.bridge.content.ConversionException;
import de.cosmocode.palava.bridge.content.Convertible;
import de.cosmocode.palava.bridge.content.KeyValueState;
import de.cosmocode.palava.media.AbstractDirectory;
import de.cosmocode.palava.media.DirectoryBase;

/**
 * A directory is just a collection/list of Asset Ids.
 * Asset ids may be contained in different directories at the same time.
 * 
 * @deprecated use {@link DirectoryBase} or {@link AbstractDirectory} instead
 * 
 * @author huettemann
 * @author Willi Schoenborn
 */
@Deprecated
@NamedQueries({
    @NamedQuery(
        name = Directory.BY_ASSET_ID,
        query = 
            "select " +
                "d.id, " +
                "d.name " +
            "from " +
                "Directory d " +
            "inner join " +
                "d.assets a " +
            "where " +
                "a.id = :assetId"
    )
})
@Entity
public class Directory implements DirectoryBase, JSONEncoder, Convertible, Iterable<Asset> {

    public static final String BY_ASSET_ID = "Directory by Asset id";
    
    @Id
    @GeneratedValue(generator = "entity_id_gen", strategy = GenerationType.TABLE)
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    @IndexColumn(name = "dirIdx", nullable = false, base = 0)
    private List<Asset> assets = Lists.newArrayList();

    @Override
    public long getId() {
        return id;
    }
    
    @Override
    public Date getCreatedAt() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setCreatedAt(Date createdAt) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setCreated() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Date getModifiedAt() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setModifiedAt(Date modifiedAt) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setModified() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Date getDeletedAt() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setDeletedAt(Date deletedAt) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setDeleted() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isDeleted() {
        return false;
    }
    
    @Override
    public List<Asset> getAssets() {
        // TODO forward list and intercept add methods or use UniqueList
        return assets;
    }
    
    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }
    
    /**
     * @deprecated use {@link Directory#getAssets()} and {@link List#add(Object)} instead
     * 
     * @param asset
     */
    @Deprecated
    public void addAsset(Asset asset) {
        // only add the asset to this directory if it doesn't already contain it
        if (!this.assets.contains(asset))
            this.assets.add(asset);
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void sort(Comparator<Asset> comparator) {
        Collections.sort(assets, comparator);
    }

    @Override
    public void encodeJSON(JSONConstructor json) throws JSONException {
        json.array();            
        for (Asset asset : assets) {
            json.object();
            asset.encodeJSON(json);
            json.endObject();
        }
        json.endArray();
    }
    
    @Override
    public void convert(StringBuffer buf, ContentConverter converter) throws ConversionException {
        converter.convertKeyValue(buf, "name", name, KeyValueState.START);
        converter.convertKeyValue(buf, "assets", assets, KeyValueState.LAST);
    }
    
    @Override
    public JSONRenderer renderAsMap(JSONRenderer renderer) {
        try {
            encodeJSON(JSON.asJSONConstructor(renderer));
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
        return renderer;
    }
    
    public int getSize() {
        return assets != null ? assets.size() : 0;
    }
    
    public boolean isEmpty() {
        return getSize() == 0;
    }
    
    @Override
    public Iterator<Asset> iterator() {
        return assets == null ? new LinkedList<Asset>().iterator() : assets.iterator();
    }
    
}
