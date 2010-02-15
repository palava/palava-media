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

import com.google.inject.Inject;

import de.cosmocode.palava.bridge.Server;
import de.cosmocode.palava.bridge.call.Arguments;
import de.cosmocode.palava.bridge.call.Call;
import de.cosmocode.palava.bridge.command.Response;
import de.cosmocode.palava.bridge.content.PhpContent;
import de.cosmocode.palava.bridge.session.HttpSession;
import de.cosmocode.palava.services.media.ImageManager;
import de.cosmocode.palava.services.media.ImageStore;
import de.cosmocode.palava.services.persistence.hibernate.HibernateJob;

public class addDirectory extends HibernateJob {

    @Inject
    private ImageStore imageStore;
    
    @Override
    public void process(Call call, Response resp, HttpSession session,
            Server server, Map<String, Object> caddy,
            org.hibernate.Session hibSession) throws Exception {
        
        final Arguments map = call.getArguments();
        map.require("id", "assetId");

        final String dirId = map.getString("id");
        final String name = map.getString("name", null);
        final String assetId = map.getString("assetId");

        final ImageManager manager = imageStore.createImageManager(hibSession);

        final Long resultId = manager.addAssetToDirectory(
            dirId == null ? null : Long.parseLong(dirId), name, Long.parseLong(assetId)
        ).getId();

        resp.setContent(new PhpContent(resultId));
    }

}
