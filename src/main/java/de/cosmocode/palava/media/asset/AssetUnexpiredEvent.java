package de.cosmocode.palava.media.asset;

import de.cosmocode.palava.media.AssetBase;

/**
 * Event interface for asset unexpired.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
public interface AssetUnexpiredEvent {

    /**
     * Event callback.
     *
     * @param asset the asset
     */
    void eventAssetUnexpired(AssetBase asset);
    
}
