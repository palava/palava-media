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

package de.cosmocode.palava.services.media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import de.cosmocode.palava.legacy.ContentConverter;
import de.cosmocode.palava.legacy.ConversionException;
import de.cosmocode.palava.legacy.Convertible;
import de.cosmocode.palava.legacy.KeyValueState;

/** a directory is just a collection/list of Asset Ids.
 * Asset ids may be contained in different directories at the same time.
 * @author huettemann
 *
 */
@NamedQueries({
    @NamedQuery(
        name="directoriesByAssetId",
        query="select d.id, d.name from Directory d inner join d.assets a where (a.id = :assetId)")
})

@Entity
public class Directory implements JSONEncoder, Convertible, Iterable<Asset> {

    @Id
    @GeneratedValue(generator = "entity_id_gen", strategy = GenerationType.TABLE)
    private Long id;

    String name;

    @ManyToMany (fetch=FetchType.LAZY)
    @JoinColumn (unique=true)
    @IndexColumn (name="dirIdx", nullable=false, base=0)
    List<Asset> assets = new ArrayList<Asset>();
    
    public List<Asset> getAssets() {
        return assets;
    }
    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }
    public void addAsset(Asset asset) {
        if (this.assets == null)
            this.assets = new ArrayList<Asset>();
        
        // only add the asset to this directory if it doesn't already contain it
        if (! this.assets.contains (asset))
            this.assets.add(asset);
    }
    public boolean removeAsset(Asset asset) {
        if (this.assets != null) {
            return this.assets.remove(asset);
        }
        return false;

    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setName( String name ) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void sort( Comparator<Asset> comparator ) {
        Collections.sort( assets,  comparator ) ;
    }

    public void encodeJSON(JSONConstructor json) throws JSONException 
    {
        json.array();            
        for(Asset asset : assets) {
            json.object();
            asset.encodeJSON(json);
            json.endObject();
        }
        json.endArray();
    }
    
    public void convert( StringBuffer buf, ContentConverter converter ) throws ConversionException
    {
        converter.convertKeyValue (buf, "name", name, KeyValueState.START);
        converter.convertKeyValue (buf, "assets", assets, KeyValueState.LAST);
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
