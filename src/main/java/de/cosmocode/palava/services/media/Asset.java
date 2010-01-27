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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.extension.JSONConstructor;
import org.json.extension.JSONEncoder;

import de.cosmocode.palava.bridge.MimeType;
import de.cosmocode.palava.bridge.content.ContentConverter;
import de.cosmocode.palava.bridge.content.ConversionException;
import de.cosmocode.palava.bridge.content.Convertible;
import de.cosmocode.palava.bridge.content.KeyValueState;
import de.cosmocode.palava.bridge.content.StreamContent;

@Entity
public class Asset implements JSONEncoder, Convertible {
    
    
    public static class ByCreationDateComparator implements Comparator<Asset> {
        public static final ByCreationDateComparator INSTANCE = new ByCreationDateComparator();
        public int compare( Asset a, Asset b ) {
            // null is less than 
            if (a == null && b == null) return 0;
            if (a == null) return -1;
            if (b == null) return 1;
            return - a.creationDate.compareTo(b.creationDate);
        }

        public boolean equals( Object o ) {
            return (o instanceof ByCreationDateComparator);
        }
    };

    @Id
    @GeneratedValue(generator = "entity_id_gen", strategy = GenerationType.TABLE)
    private Long id;
    
    private String storeKey;
    private String mime;
    private long length;
    /*
    @Lob
    @Type(type="blob")
    private Blob blb; 
     */
    private transient StreamContent content;
    private String name;
    private String title;
    private String description;
    private Date creationDate;
    private Date modificationDate;
    
    private Date expirationDate;
    
    @Column(nullable=false)
    private boolean expiresNever;

    /**
     * JSON Object:
     * {
     *  "key" : "value",
     *  "key" : "value"
     * }
     */
    private String metaData;

    public Asset() {
        creationDate = new Date();
        modificationDate = new Date();
        expiresNever = false;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public StreamContent getContent() {
        return content;
    }
    
    public void setContent( StreamContent content ) throws SQLException, IOException {
        this.content = content;
        
        this.mime = content.getMimeType() == null ? MimeType.IMAGE.toString() : content.getMimeType().toString();
        this.length = content.getLength();
        /*
        byte [] buffer = new byte[(int) length];
        
        content.getInputStream().read(buffer,0,buffer.length);

        blb = Hibernate.createBlob(buffer);
*/
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStoreKey() {
        return storeKey;
    }

    public void setStoreKey(String storeKey) {
        this.storeKey = storeKey;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isExpiresNever() {
        return expiresNever;
    }

    public boolean getExpiresNever() {
        return expiresNever;
    }

    public void setExpiresNever(boolean expiresNever) {
        this.expiresNever = expiresNever;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public void fillMetaData(Map<String, String> map) throws JSONException {
        if (metaData == null) metaData = "{}";
        
        JSONObject json = new JSONObject(metaData);
        for (String key : map.keySet()) {
            json.put(key, map.get(key));
        }
        
        metaData = json.toString();
    }
    
    public boolean isExpired() {
        if (expirationDate == null) {
            return !expiresNever;
        } else {
            return expiresNever ? false : expirationDate.before(new Date());
        }
    }
    
    public boolean isExpired(boolean oldStyleCheck) {
        if (oldStyleCheck) {
            return expirationDate != null && expirationDate.before(new Date());
        } else {
            return isExpired();
        }
    }

    /**
     * object() and endObject() moved to parent context
     */
    public void encodeJSON(JSONConstructor json) throws JSONException 
    {
        json.key("id").value(id);
        json.key("storeKey").value(storeKey);
        json.key("creationDate").value(creationDate == null ? null : creationDate.getTime() / 1000);
        json.key("modificationDate").value(modificationDate == null ? null : modificationDate.getTime() / 1000);
        if ( name != null ) json.key("name").value(name);
        if ( title != null ) json.key("title").value(title);
        if ( description != null ) json.key("description").value(description);
        json.key("mimetype").value(mime);       
        json.key("size").value(length);
        
        json.key("expirationDate").value(expirationDate == null ? null : expirationDate.getTime() / 1000);
        json.key("expired").value(isExpired(true));        
        json.key("expiresNever").value(expiresNever);
        
        if (metaData != null) json.key("metaData").plain(metaData);
    }

    public void convert( StringBuffer buf, ContentConverter converter ) throws ConversionException
    {
        converter.convertKeyValue (buf, "id", id, KeyValueState.START);
        converter.convertKeyValue (buf, "storeKey", storeKey, KeyValueState.INSIDE);
        converter.convertKeyValue (buf, "creationDate", creationDate == null ? null : creationDate.getTime() / 1000, KeyValueState.INSIDE);
        converter.convertKeyValue (buf, "modificationDate", modificationDate == null ? null : modificationDate.getTime() / 1000, KeyValueState.INSIDE);
        converter.convertKeyValue (buf, "name", name, KeyValueState.INSIDE);
        converter.convertKeyValue (buf, "title", title, KeyValueState.INSIDE);
        converter.convertKeyValue (buf, "expirationDate", expirationDate == null ? null : expirationDate.getTime() / 1000, KeyValueState.INSIDE);
        converter.convertKeyValue(buf, "expired", isExpired(true), KeyValueState.INSIDE);
        converter.convertKeyValue(buf, "expiresNever", expiresNever, KeyValueState.INSIDE);
        converter.convertKeyValue(buf, "metaData", metaData, KeyValueState.INSIDE);
        converter.convertKeyValue (buf, "description", description, KeyValueState.LAST);
    }

    @Override
    public boolean equals (Object object) {
        if (object instanceof Asset) {
            Asset asset = (Asset) object;
            return asset.getId() == this.id || super.equals(object);
        }
        else return super.equals (object);
    }
    
    @Override
    public String toString() {
        return "[" + getId() + "] " + name;
    }


}
