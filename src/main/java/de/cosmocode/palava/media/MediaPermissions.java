/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.media;

import de.cosmocode.palava.security.Permissions;

/**
 * Static class for permissions constants.
 *
 * @since 2.0
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
