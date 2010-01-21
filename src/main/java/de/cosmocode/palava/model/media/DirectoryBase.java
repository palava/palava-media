package de.cosmocode.palava.model.media;

import java.util.List;

import de.cosmocode.json.JSONMapable;
import de.cosmocode.palava.model.base.EntityBase;

public interface DirectoryBase extends EntityBase, JSONMapable {

    /**
     * ORDERED!
     * 
     * @return
     */
    List<? extends AssetBase> getAssets();
    
}
