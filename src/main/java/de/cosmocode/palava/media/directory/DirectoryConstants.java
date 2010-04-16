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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.cosmocode.palava.media.directory;

import de.cosmocode.palava.media.DirectoryBase;

/**
 * Constants for {@link DirectoryBase} commands.
 *
 * @author Willi Schoenborn
 */
public final class DirectoryConstants {

    // parameters
    public static final String DIRECTORY_ID = "directoryId";
    public static final String INDEX = "index";

    // return values
    public static final String DIRECTORY = "directory";
    public static final String CURRENT_INDEX = "currentIndex";
    
    private DirectoryConstants() {
        
    }

}