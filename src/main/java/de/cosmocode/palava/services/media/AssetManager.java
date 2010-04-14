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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.palava.bridge.Content;
import de.cosmocode.palava.bridge.content.StreamContent;
import de.cosmocode.palava.services.store.ContentStore;

public class AssetManager {
    
    private final ContentStore store;
    private final Session session;
    
    private static final Logger log = LoggerFactory.getLogger(AssetManager.class);

    public AssetManager(ContentStore store, Session session) {
        this.store = store;
        this.session = session;
    }

    public Asset getAsset( Long id, boolean withContent ) throws Exception {
        Asset asset = (Asset) session.load(Asset.class, id);

        if ( asset != null && withContent ) {
            StreamContent content = store.load(asset.getStoreKey());

            if ( content == null ) {
                log.error("content not found: {}", asset.getStoreKey());
                return null;
            } else
                asset.setContent(content);
        }
        
        return asset;
    }
    
    public Asset loadAssetContent (final Asset asset) throws Exception {
        if (asset != null) {
            final StreamContent content = store.load(asset.getStoreKey());

            if ( content == null ) {
                log.error("content not found: {}", asset.getStoreKey());
                return null;
            } else {
                asset.setContent(content);
            }
        }
        
        return asset;
    }

    public void updateAsset( Asset asset ) throws Exception {
        Transaction tx = session.beginTransaction();

        try {
            asset.setModificationDate(new Date());
            session.save(asset);
            session.flush();
            tx.commit();
        } catch ( Exception e) {
            tx.rollback();

            throw e;
        }
    }

    public void createAsset( Asset asset ) throws Exception {
        Content content = asset.getContent();
        if ( content == null ) throw new NullPointerException("content");

        String key = store.store(content);

        Transaction tx = session.beginTransaction();
        
        try {
            asset.setStoreKey(key);
            session.save(asset);            
            session.flush();
            tx.commit();
        } catch (HibernateException e) {
            log.error("Saving asset failed", e);
            store.remove(key);
            asset.setStoreKey(null);
            tx.rollback();
            throw e;
        }
    }

    public Boolean removeAssetById (Long id) throws Exception {
        Asset asset = this.getAsset (id, false);
        
        log.debug("found asset: {} for id {}", asset, id);
        
        Set<Long> dirIds = this.getDirectoryIdsForAsset (id).keySet();
        for (Long dirId : dirIds) {
            // if deletion on any directory that contains this asset fails for any reason,
            // then abort the whole deletion of this asset at once
            log.info("Currently removing asset with id {} from directory with id {}", id, dirId);
            Directory directory = getDirectory (dirId);
            if ( ! directory.getAssets().remove(asset) ) {
                log.warn("Could not remove asset {} from directory {}", asset, dirId);
                return Boolean.FALSE;
            }
            session.saveOrUpdate (directory);
        }

        Transaction tx = session.beginTransaction();
        try {
            this.removeAsset (asset);
            session.flush();
            tx.commit();
        } catch ( Exception e) {
            log.error("Unable to delete asset", e);
            tx.rollback();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
            

    public void removeAsset( Asset asset ) {
        session.delete(asset);
        store.remove(asset.getStoreKey());
    }

    public void createDirectory( Directory dir ) throws Exception {
        session.save(dir);
    }

    public void updateDirectory( Directory dir ) throws Exception {
        session.update(dir);
    }

    public Directory getDirectory( Long id ) throws Exception {
        return (Directory) session.load(Directory.class, id);
    }
    
    /**
     * get a list of all directories containing a given asset.
     * 
     * @param assetId the id of the asset this method is looking for
     * @return an id/name-list of all directories containing
     * the asset with the given assetID
     * @author schoenborn@cosmocode.de
     */
    @SuppressWarnings("unchecked")
    public Map<Long, String> getDirectoryIdsForAsset(Long assetId) {
        
        final Map<Long, String> map = new HashMap<Long, String>();
        
        final Query query = session.getNamedQuery(Directory.BY_ASSET_ID).setLong("assetId", assetId);
        
        final List<Object[]> list = query.list();
        
        for (final Object[] array : list) {
            map.put((Long) array[0], (String) array[1]);
        }
        
        return map;
    }
    
    public void removeDirectory ( Directory dir ) {
        session.delete(dir);
    }

    /** removes an asset from directory
     *
     * @return true if asset was found and deleted, false if not found
     */
    public Boolean removeAssetFromDirectory ( Long directoryId, Long assetId) throws Exception {
        Directory directory = (Directory) session.load(Directory.class, directoryId);
        Asset asset = (Asset) session.load(Asset.class, assetId);
        if (!directory.getAssets().remove(asset)) {
            log.warn("Could not remove asset {} from directory {}", asset, directoryId);
            return Boolean.FALSE;
        }
        

        Transaction tx = session.beginTransaction();

        try {
            session.save(directory);
            session.flush();
            tx.commit();
        } catch ( Exception e) {
            tx.rollback();
            throw e;
        }

        return Boolean.TRUE;
    }
    
    public Directory addAssetToDirectory ( long directoryId, long assetId) throws Exception {

        Directory directory = (Directory) session.load(Directory.class, directoryId);

        if (directory == null) 
            throw new NullPointerException("Directory not found");

        Asset asset = (Asset) session.get(Asset.class, assetId);
        if (asset == null)  throw new NullPointerException ("Asset not found");  //createAsset( asset );

        directory.addAsset(asset);

        final Transaction tx = session.beginTransaction();

        try {
            session.saveOrUpdate(directory);
            session.flush();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        }

        return directory;
    }
    
    public Directory addAssetToDirectory(Long directoryId, String name, Long assetId) throws Exception {
        Directory directory = null;
        if (directoryId != null)
            directory = (Directory) session.load(Directory.class, directoryId);

        final boolean newDir = directory == null;
        
        if (newDir) {
            directory = new Directory();
            directory.setName(name);
        }

        final Asset asset = (Asset) session.get(Asset.class, assetId);
        if (asset == null)  throw new NullPointerException("Asset not found");

        directory.addAsset(asset);

        final Transaction tx = session.beginTransaction();

        try {
            session.saveOrUpdate(directory);
            if (newDir) {
            }
            session.flush();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        }

        return directory;
    }

    /**
     * Fill a directory with all assets, defined
     * by those IDs in the list. Afterwards, the
     * directory asset list will only consist of
     * the given entries.
     *
     * @author  Tobias Sarnowski
     * @param   assetIds    a list of all Ids of assets, the directory should hold
     * @param   directory   the directory to fill in the new Ids
     * @return  the used directory
     * @see     useAssetlistForDirectory(java.util.List, long)
     * @see     useAssetlistForDirectory(java.util.List, java.lang.String)
     * @throws  HibernateException if one of the assets specified by the `assetIds` could not be loaded
     */
    public Directory useAssetlistForDirectory(List<Long> assetIds, Directory directory) throws HibernateException
    {
        final List<Asset> assets = new LinkedList<Asset>();
        for (final Iterator<Long> i = assetIds.iterator(); i.hasNext(); ) {
            assets.add((Asset) session.load(Asset.class, i.next()));
        }
        directory.setAssets(assets);
        final Transaction tx = session.beginTransaction();
        try {
            session.save(directory);
            session.flush();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        }
        return directory;
    }

    /**
     * Use the directory, specified by the Id to fill in the list.
     *
     * @author  Tobias Sarnowski
     * @param   assetIds    a list of all Ids of assets, the directory should hold
     * @param   directoryId the directory id of the directory to fill in the new Ids
     * @return  the used directory
     * @see     useAssetlistForDirectory(java.util.List, de.cosmocode.palava.components.assets.Directory)
     * @throws  HibernateException if the directory or one of the assets could not be loaded from the database
     */
    public Directory useAssetlistForDirectory(List<Long> assetIds, long directoryId) throws HibernateException
    {
        final Directory directory = (Directory) session.load(Directory.class, directoryId);
        return this.useAssetlistForDirectory(assetIds, directory);
    }

    /**
     * Use the name to create a new directory and fill in the list.
     *
     * @author  Tobias Sarnowski
     * @param   assetIds    a list of all Ids of assets, the directory should hold
     * @param   directoryName   the directory name of the new directory
     * @return  the used directory
     * @see     useAssetlistForDirectory(java.util.List, de.cosmocode.palava.components.assets.Directory)
     */
    public Directory useAssetlistForDirectory(List<Long> assetIds, String directoryName) {
        final Directory directory = new Directory();
        directory.setName(directoryName);
        final Transaction tx = session.beginTransaction();
        try {
            session.save(directory);
            session.flush();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        }
        return this.useAssetlistForDirectory(assetIds, directory);
    }


}
