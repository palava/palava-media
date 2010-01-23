/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
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

package de.cosmocode.palava.jobs.assets;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.call.MissingArgumentException;
import de.cosmocode.palava.core.protocol.DataCall;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.protocol.content.PhpContent;
import de.cosmocode.palava.core.server.Server;
import de.cosmocode.palava.core.session.HttpSession;
import de.cosmocode.palava.legacy.UpdateResult;
import de.cosmocode.palava.services.media.Asset;
import de.cosmocode.palava.services.media.ImageManager;
import de.cosmocode.palava.services.media.ImageStore;
import de.cosmocode.palava.services.persistence.hibernate.HibernateJob;

public class editAsset extends HibernateJob {

    @SuppressWarnings("unchecked")
    @Override
    public void process(Call req, Response resp, HttpSession session,
            Server server, Map<String, Object> caddy,
            org.hibernate.Session hibSession) throws Exception {
        
        ImageStore as = server.getServiceManager().lookup(ImageStore.class);

        DataCall request = (DataCall) req;
        Map<String, String> map = request.getStringedArguments();

        String assetId = map.get("assetId");
        if ( assetId == null ) throw new MissingArgumentException(this,"assetId");
        map.remove("assetId");

        String title = map.get("title");
        if ( title == null ) throw new MissingArgumentException(this,"title");
        map.remove("title");

        String description = map.get("description");
        if ( description == null ) throw new MissingArgumentException(this,"description");
        map.remove("description");
        
        String exDate = map.get("expirationDate"); 
        map.remove("expirationDate");

        ImageManager am = as.createImageManager(hibSession);

        UpdateResult ur = new UpdateResult();
        try {
            Asset asset = am.getAsset( Long.parseLong(assetId), false ) ;

            if ( asset == null ) throw new NullPointerException("asset") ;

            asset.setTitle( title ) ;
            asset.setDescription( description );
                   
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date date = null;
            
            try {
                format.setLenient(false);
                if (!exDate.equals("")) date = format.parse(exDate);
            } catch (Exception e) {
                ur.addError(UpdateResult.ERR_FORMAT, "expirationDate");
            }
        
            asset.setExpirationDate(date);
            asset.setExpiresNever(map.containsKey("expiresNever") && map.get("expiresNever").equals("true"));
            map.remove("expiresNever");
            
            asset.fillMetaData(map);

            if (ur.isError()) {
                ur.setResult(asset);
                resp.setContent(new PhpContent(ur));
                return;
            }
            
            am.updateAsset(asset);
            ur.setResult(asset);
        } catch ( Exception e ) {
            ur.setException(e);
        }
        resp.setContent(new PhpContent(ur));
    }
}