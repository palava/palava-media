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

package de.cosmocode.palava.media.asset;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Ordering;

import de.cosmocode.commons.TrimMode;
import de.cosmocode.palava.media.directory.DirectoryBase;
import de.cosmocode.palava.model.base.EntityBase;
import de.cosmocode.palava.store.Store;

/**
 * Interface definition for assets.
 * 
 * <div>
 *   There are several expiration states an asset can cycle througout
 *   it's lifespan.
 *   <ol>
 *     <li>
 *       <h2>Expirable</h2>
 *       An expirable asset has an expiresAt date set.
 *     </li>
 *     <li>
 *       <h2>Expiring</h2>
 *       An expiring asset reached it expiresAt date but is not
 *       yet expired.
 *     </li>
 *     <li>
 *       <h2>Expired</h2>
 *       An expires asset has been marked as expired by using 
 *       {@link AssetBase#setExpired()}. This does not require 
 *       expiresAt to be set.
 *     </li>
 *     <li>
 *       <h2>Unexpiring</h2>
 *       An unexpiring asset is currently marked as expired
 *       but it's expiresAt date is set to null or a date in
 *       the future.
 *     </li>
 *   </ol>
 * </div>
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
public interface AssetBase extends EntityBase {
    
    /**
     * Abstract query name for retrieving all assets which are currently
     * in expiring state. See {@link AssetBase#isExpiring()} for details.
     */
    String EXPIRING = "AssetBase.EXPIRING";
    
    /**
     * Abstract query name for retrieving all assets which are currently
     * in unexpiring state. See {@link AssetBase#isUnexpiring()} for details.
     */
    String UNEXPIRING = "AssetBase.UNEXPIRING";

    /**
     * Allows case insensitive ordering by name.
     */
    Ordering<AssetBase> ORDER_BY_NAME = Ordering.from(String.CASE_INSENSITIVE_ORDER).nullsLast().onResultOf(
        new Function<AssetBase, String>() {
           
            @Override
            public String apply(AssetBase from) {
                return from.getName();
            }
           
        });
    
    /**
     * Allows ordering by expiration date, which will move the expired assets to the top.
     */
    Ordering<AssetBase> ORDER_BY_EXPIRATION = Ordering.natural().nullsLast().onResultOf(
        new Function<AssetBase, Date>() {

            @Override
            public Date apply(AssetBase from) {
                return from.getExpiresAt();
            }

        });
    
    Predicate<AssetBase> IS_EXPIRED = new Predicate<AssetBase>() {
        
        @Override
        public boolean apply(AssetBase input) {
            return input.isExpired();
        }
        
    };
    
    Predicate<AssetBase> NOT_EXPIRED = Predicates.not(IS_EXPIRED);
    
    Predicate<AssetBase> IS_EXPIRING = new Predicate<AssetBase>() {
        
        @Override
        public boolean apply(AssetBase input) {
            return input.isExpiring();
        }
        
    };
    
    Predicate<AssetBase> IS_UNEXPIRING = new Predicate<AssetBase>() {
        
        @Override
        public boolean apply(AssetBase input) {
            return input.isUnexpiring();
        }
        
    };

    /**
     * Provides the name of this asset. This usually maps
     * directly to the filename.
     * 
     * @return the name of this asset.
     */
    String getName();

    /**
     * Sets the name of this asset. The given name will be trimmed
     * using {@link TrimMode#NULL}.
     * 
     * @param name the new name
     */
    void setName(String name);
    
    /**
     * Provides the binary data of this asset.
     * 
     * @return the binary data as a stream
     * @throws IllegalStateException if the binary data has not
     *         yet been initialized
     */
    InputStream getStream();
    
    /**
     * Sets the inputstream of this asset.
     * 
     * @param stream the binary data as a stream
     * @throws NullPointerException if stream is null
     */
    void setStream(InputStream stream);
    
    /**
     * Provides the store identifier of this asset.
     * This identifier represents the binary data in the
     * corresponding {@link Store}.
     * 
     * @return the current store identifier or null if
     *         this asset is not yet associated with a store
     */
    String getStoreIdentifier();
    
    /**
     * Sets the store identifier of this asset.
     * 
     * @param storeIdentifier the new store identifier
     */
    void setStoreIdentifier(String storeIdentifier);

    /**
     * Provides the title of this asset which can be seen as
     * a public name.
     * 
     * @return the current title
     */
    String getTitle();

    /**
     * Sets the title of this asset. The given title will be trimmed
     * using {@link TrimMode#NULL}.
     * 
     * @param title the new title
     */
    void setTitle(String title);

    /**
     * Provides the description of this asset.
     * 
     * @return the current description
     */
    String getDescription();

    /**
     * Sets the description of this asses. The given description will be trimmed
     * using {@link TrimMode#NULL}.
     * 
     * @param description the new description
     */
    void setDescription(String description);

    /**
     * Provides the meta data of this asset. The returned map
     * does not support null keys.
     * 
     * @return the meta data of this asses
     */
    Map<String, String> getMetaData();

    /**
     * Provides all directories this asset is contained in.
     * 
     * @param <D> generic directory type
     * @return a set of all directories containing this asset
     */
    <D extends DirectoryBase> Set<D> getDirectories();

    /**
     * Provides the expiration date of this asset.
     * <p>
     *   The member returned by this accessor method is one out of two members provided to
     *   support proper asset expiration. The expiresAt date just addresses
     *   a single point on the timeline when this asset has been expired
     *   a will expire (when the date is in the past or future respectively).
     * </p>
     * 
     * @return the expiration date or null, if this asset does not expire
     */
    Date getExpiresAt();

    /**
     * Sets the expiration date of this asset.
     * 
     * @param expiresAt the expiration date or null if this
     *        asset should not expire
     */
    void setExpiresAt(Date expiresAt);

    /**
     * Provides the expirability status of this asset. An asset is expirable
     * if and only if an expiresAt date is set.
     * 
     * @return true if this asset is expirable, false otherwise
     */
    boolean isExpirable();

    /**
     * Provides the expiring status of this asset. An asset is expiring
     * if and only if it is not yet expired but expiresAt points to a date
     * in the past.
     * 
     * @return true if this asset is expiring, false otherwise.
     */
    boolean isExpiring();
    
    /**
     * Sets the expiration status to the specified value.
     * 
     * @param expired the new expired state
     */
    void setExpired(boolean expired);
    
    /**
     * Provides the epxired status of this asset.
     * <p>
     *   The member returned by this accessor method is one out of two members provided to
     *   support proper asset expiration. The expired field defines whether this
     *   asset is currently expired.
     * </p>
     * <p>
     *   Note: An expiresAt date in the past does not necessarily mark an asset as expired
     *   as it could be expiring at the moment. For a detailed explanation take a look
     *   at the documentation of {@link AssetBase#isExpiring()}.
     * </p>
     * 
     * @return true if this asset is expired, false otherwise
     */
    boolean isExpired();
    
    /**
     * Provides the *unexpiring* status of this asset. An asset is unexpiring
     * if and only if it is expired but expiresAt is null or 
     * points to a date in the future.
     * 
     * @return true if this asset is unexpiring, false otherwise
     */
    boolean isUnexpiring();

}
