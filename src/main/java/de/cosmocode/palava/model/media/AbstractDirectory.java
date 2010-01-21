package de.cosmocode.palava.model.media;

import de.cosmocode.json.JSONRenderer;

public abstract class AbstractDirectory implements DirectoryBase {

    @Override
    public JSONRenderer renderAsMap(JSONRenderer renderer) {
        return renderer.
            key("assets").array(getAssets());
    }

}
