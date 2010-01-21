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

import java.util.Map;

import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.protocol.ConnectionLostException;
import de.cosmocode.palava.core.protocol.DataCall;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.protocol.content.PhpContent;
import de.cosmocode.palava.core.server.Server;
import de.cosmocode.palava.core.session.HttpSession;
import de.cosmocode.palava.jobs.hib.HibJob;
import de.cosmocode.palava.services.media.Asset;
import de.cosmocode.palava.services.media.ImageManager;
import de.cosmocode.palava.services.media.ImageStore;

public class download extends HibJob {

    @Override
    public void process(Call request, Response response, HttpSession session,
            Server server, Map<String, Object> caddy, org.hibernate.Session hibSession)
            throws ConnectionLostException, Exception {
        
        ImageStore ist = server.getServiceManager().lookup(ImageStore.class);

        if ( hibSession == null ) hibSession = createHibSession(server,caddy);

        DataCall req = (DataCall) request;
        final Map<String, String> map = req.getArguments();

        String filterName = map.get("filter");
        Long id = null;
        try {
            id = Long.parseLong(map.get("id"));
        } catch ( Exception e ) {
        }
        if ( id == null ) throw new MissingArgumentException(this,"id");

        ImageManager im = ist.createImageManager(hibSession);

        Asset asset = null;

        asset = im.getImage(id,filterName);

        if ( asset != null )
            response.setContent( asset.getContent());
        else
            response.setContent( PhpContent.NOT_FOUND );
        


    }

}
