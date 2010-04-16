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

package de.cosmocode.palava.media;

import de.cosmocode.palava.security.Permissions;

/**
 * Static class for permissions constants.
 *
 * @author Willi Schoenborn
 */
@Permissions
public final class MediaPermissions {

    public static final String DIRECTORY_CREATE = "directory:create";
    public static final String DIRECTORY_READ = "directory:read";
    public static final String DIRECTORY_UPDATE = "directory:update";
    public static final String DIRECTORY_DELETE = "directory:delete";
    public static final String DIRECTORY_ADD_ASSET = "directory:addAsset";
    public static final String DIRECTORY_SET_ASSET = "directory:setAsset";
    public static final String DIRECTORY_REMOVE_ASSET = "directory:removeAsset";
    
    public static final String ASSET_CREATE = "asset:create";
    public static final String ASSET_READ = "asset:read";
    public static final String ASSET_UPDATE = "asset:update";
    public static final String ASSET_DELETE = "asset:delete";
    
    private MediaPermissions() {
        
    }

}
