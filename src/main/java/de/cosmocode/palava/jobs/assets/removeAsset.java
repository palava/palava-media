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

import de.cosmocode.palava.core.bridge.call.Call;
import de.cosmocode.palava.core.bridge.call.DataCall;
import de.cosmocode.palava.core.bridge.call.MissingArgumentException;
import de.cosmocode.palava.core.bridge.command.Response;
import de.cosmocode.palava.core.bridge.session.HttpSession;
import de.cosmocode.palava.core.bridge.simple.content.PhpContent;
import de.cosmocode.palava.legacy.server.Server;
import de.cosmocode.palava.services.media.ImageManager;
import de.cosmocode.palava.services.media.ImageStore;
import de.cosmocode.palava.services.persistence.hibernate.HibernateJob;

public class removeAsset extends HibernateJob {

    @Override
    public void process(Call req, Response resp, HttpSession session,
            Server server, Map<String, Object> caddy,
            org.hibernate.Session hibSession) throws Exception {
        
        ImageStore ist = server.getServiceManager().lookup(ImageStore.class);

        DataCall request = (DataCall) req;
        final Map<String, String> map = request.getStringedArguments();

        String assetId = map.get("assetId");
        if ( assetId == null ) throw new MissingArgumentException(this,"assetId");

        ImageManager im = ist.createImageManager(hibSession);

        resp.setContent (new PhpContent (im.removeAssetById( new Long(assetId))));
    }

    

}
