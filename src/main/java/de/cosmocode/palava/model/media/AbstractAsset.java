package de.cosmocode.palava.model.media;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.google.common.collect.Maps;

import de.cosmocode.commons.TrimMode;
import de.cosmocode.json.JSONRenderer;
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
        return
            super.renderAsMap(renderer).
            key("name").value(getName()).
            key("title").value(getTitle()).
            key("description").value(getDescription()).
            key("metaData").value(getMetaData()).
            key("expiresAt").value(getExpiresAt()).
            key("isExpirable").value(isExpirable()).
            key("isExpired").value(isExpired());
    }
    
}
