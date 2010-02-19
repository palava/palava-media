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

    public static final String CREATE_DIRECTORY = "directory:create";
    public static final String UPDATE_DIRECTORY = "directory:update";
    public static final String READ_DIRECTORY = "directory:read";
    public static final String DELETE_DIRECTORY = "directory:delete";
    
    public static final String CREATE_ASSET = "asset:create";
    public static final String UPDATE_ASSET = "asset:update";
    public static final String READ_ASSET = "asset:read";
    public static final String DELETE_ASSET = "asset:delete";
    public static final String DOWNLOAD_ASSET = "asset:download";
    
    private MediaPermissions() {
        
    }

}
