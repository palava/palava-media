package de.cosmocode.palava.model.media;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import de.cosmocode.json.JSONMapable;
import de.cosmocode.palava.model.base.EntityBase;

public interface AssetBase extends EntityBase, JSONMapable {
    
    /**
     * Allows ordering by expiration date, which will move the expired assets to the top.
     */
    Ordering<AssetBase> ORDER_BY_EXPIRATION = Ordering.natural().onResultOf(new Function<AssetBase, Date>() {
        
        @Override
        public Date apply(AssetBase from) {
            return from.getExpiresAt();
        }
        
    }).nullsLast();

    String getName();
    
    void setName(String name);
    
    String getTitle();
    
    void setTitle(String title);
    
    String getDescription();
    
    void setDescription(String description);

    /**
     * Left to the implementation if changes to the returned
     * map will affect internal state.
     * 
     * @return
     */
    Map<String, String> getMetaData();

    /**
     * Left to the implementation if changes to the
     * returned set will affect the internal association.
     * 
     * @return
     */
    Set<? extends DirectoryBase> getDirectories();

    Date getExpiresAt();
    
    void setExpiresAt(Date expiresAt);
    
    boolean isExpirable();
    
    boolean isExpired();
    
}
