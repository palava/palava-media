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

import de.cosmocode.palava.MimeType;
import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.protocol.ConnectionLostException;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.protocol.content.PhpContent;
import de.cosmocode.palava.core.protocol.content.StreamContent;
import de.cosmocode.palava.core.server.Server;
import de.cosmocode.palava.core.session.HttpSession;
import de.cosmocode.palava.services.media.Asset;
import de.cosmocode.palava.services.media.ImageManager;
import de.cosmocode.palava.services.media.ImageStore;
import de.cosmocode.palava.services.persistence.hibernate.HibernateJob;

public class upload extends HibernateJob {

    @Override
    public void process(Call request, Response response, HttpSession session,
            Server server, Map<String, Object> caddy, org.hibernate.Session hibSession)
            throws ConnectionLostException, Exception {
        
        ImageStore ist = server.getServiceManager().lookup(ImageStore.class);


        Asset asset = (Asset) caddy.get("asset");
        if ( asset == null ) throw new NullPointerException("asset == null");
        MimeType mimetype = (MimeType) caddy.get("mimetype");

        // just use the request data as the content
        asset.setContent(new StreamContent(
            request.getInputStream(), request.getHeader().getContentLength(), mimetype)
        );

        ImageManager im = ist.createImageManager(hibSession);

        im.createAsset(asset);

        response.setContent( new PhpContent(asset.getId()) );
    }

}
