package de.cosmocode.palava.media.asset;

import javax.persistence.PersistenceException;

import de.cosmocode.palava.entity.EntityService;

/**
 * Extension of the {@link EntityService} interface for {@link AssetBase}s.
 *
 * @since 2.0
 * @author Willi Schoenborn
 * @param <T> the generic asset type
 */
public interface AssetBaseService<T extends AssetBase> extends EntityService<T> {

    /**
     * Reads the binary data of the specified asset's store identifier
     * from the associated store. Calling this method on an asset
     * which already contains a valid stream will do nothing.
     * 
     * @since 2.0
     * @param asset the asset
     * @throws NullPointerException if asset is null
     * @throws PersistenceException if reading the stream failed
     */
    void readStream(T asset) throws PersistenceException;
    
}
