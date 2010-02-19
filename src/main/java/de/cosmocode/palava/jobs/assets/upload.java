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

package de.cosmocode.palava.jobs.assets;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.bridge.MimeType;
import de.cosmocode.palava.bridge.Server;
import de.cosmocode.palava.bridge.call.Call;
import de.cosmocode.palava.bridge.command.Response;
import de.cosmocode.palava.bridge.content.PhpContent;
import de.cosmocode.palava.bridge.content.StreamContent;
import de.cosmocode.palava.bridge.session.HttpSession;
import de.cosmocode.palava.jpa.hibernate.HibernateJob;
import de.cosmocode.palava.services.media.Asset;
import de.cosmocode.palava.services.media.ImageManager;
import de.cosmocode.palava.services.media.ImageStore;

public class upload extends HibernateJob {

    @Inject
    private ImageStore imageStore;
    
    @Override
    public void process(Call call, Response response, HttpSession session,
        Server server, Map<String, Object> caddy, org.hibernate.Session hibSession) throws Exception {
        
        final Asset asset = call.getHttpRequest().get("asset");
        Preconditions.checkNotNull(asset, "Asset");
        final MimeType mimetype = call.getHttpRequest().get("mimetype");

        // just use the request data as the content
        asset.setContent(new StreamContent(
            call.getInputStream(), call.getHeader().getContentLength(), mimetype
        ));

        final ImageManager im = imageStore.createImageManager(hibSession);

        im.createAsset(asset);

        response.setContent(new PhpContent(asset.getId()));
    }

}
