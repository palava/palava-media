package de.cosmcode.palava.media;

import de.cosmocode.palava.media.AssetBase;

/**
 * Tests {@link ConcreteAsset}.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
public final class ConcreteAssetTest extends AssetBaseTest {

    @Override
    public AssetBase unit() {
        return new ConcreteAsset();
    }
    
}
