package de.cosmocode.palava.media.asset;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

import de.cosmocode.palava.store.Store;

/**
 * Binding annotation for the {@link Store} used by {@link AbstractAssetService}.
 *
 * @author Willi Schoenborn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD, ElementType.PARAMETER
})
@BindingAnnotation
public @interface AssetStore {

}
