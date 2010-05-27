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

package de.cosmcode.palava.media.directory;

import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;

import de.cosmocode.palava.core.DefaultRegistryModule;
import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.entity.EntityService;
import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.media.asset.AssetBase;
import de.cosmocode.palava.media.asset.AssetConstants;
import de.cosmocode.palava.media.directory.DirectoryBase;
import de.cosmocode.palava.media.directory.DirectoryConstants;
import de.cosmocode.palava.media.directory.SetAsset;

/**
 * Tests {@link SetAsset}.
 *
 * @author Willi Schoenborn
 */
public final class SetAssetTest {
    
    private void execute(int currentIndex, int index) throws IpcCommandExecutionException {
        final AssetBase asset = EasyMock.createMock("asset", AssetBase.class);
        
        @SuppressWarnings("unchecked")
        final EntityService<AssetBase> as = EasyMock.createMock("as", EntityService.class);
        EasyMock.expect(as.reference(1L)).andReturn(asset);
        
        EasyMock.replay(asset, as);
        
        final AssetBase asset1 = EasyMock.createMock("asset1", AssetBase.class);
        final AssetBase asset2 = EasyMock.createMock("asset2", AssetBase.class);
        final AssetBase asset3 = EasyMock.createMock("asset3", AssetBase.class);
        final AssetBase asset4 = EasyMock.createMock("asset4", AssetBase.class);
        
        EasyMock.replay(asset1, asset2, asset3, asset4);
        
        final List<AssetBase> mocks = Lists.newArrayList(asset1, asset2, asset3, asset4);
        
        final List<AssetBase> assets = Lists.newArrayList();
        for (AssetBase mock : mocks) {
            if (assets.size() == currentIndex) {
                assets.add(asset);
            }
            assets.add(mock);
        }
        
        if (assets.size() == mocks.size()) {
            assets.add(asset);
        }
        
        Assert.assertEquals(mocks.size() + 1, assets.size());
        
        final DirectoryBase directory = EasyMock.createMock("directory", DirectoryBase.class);
        EasyMock.expect(directory.getAssets()).andReturn(assets);
        
        @SuppressWarnings("unchecked")
        final EntityService<DirectoryBase> ds = EasyMock.createMock("ds", EntityService.class);
        EasyMock.expect(ds.read(1L)).andReturn(directory);
        
        EasyMock.replay(directory, ds);
        
        final Registry registry = Guice.createInjector(new DefaultRegistryModule()).getInstance(Registry.class);
        
        final SetAsset unit = new SetAsset(ds, as, registry);
        
        final IpcCall call = EasyMock.createMock("call", IpcCall.class);
        final IpcArguments arguments = EasyMock.createMock("arguments", IpcArguments.class);
        EasyMock.expect(call.getArguments()).andReturn(arguments);
        EasyMock.expect(arguments.getLong(DirectoryConstants.DIRECTORY_ID)).andReturn(1L);
        EasyMock.expect(arguments.getLong(AssetConstants.ASSET_ID)).andReturn(1L);
        EasyMock.expect(arguments.getInt(DirectoryConstants.INDEX)).andReturn(index);
        
        EasyMock.replay(call, arguments);
        
        final Map<String, Object> result = Maps.newHashMap();
        
        unit.execute(call, result);
        
        EasyMock.verify(directory, ds);
        EasyMock.verify(asset, as);
        EasyMock.verify(call, arguments);
        
        Assert.assertSame(asset, assets.get(index));
        
        final List<AssetBase> expected = Lists.newArrayList();
        for (AssetBase mock : mocks) {
            if (expected.size() == index) {
                expected.add(asset);
            }
            expected.add(mock);
        }
        
        if (expected.size() == mocks.size()) {
            expected.add(asset);
        }
        
        Assert.assertEquals(expected, assets);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute00() throws IpcCommandExecutionException {
        execute(0, 0);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute01() throws IpcCommandExecutionException {
        execute(0, 1);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute02() throws IpcCommandExecutionException {
        execute(0, 2);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute03() throws IpcCommandExecutionException {
        execute(0, 3);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute04() throws IpcCommandExecutionException {
        execute(0, 4);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute10() throws IpcCommandExecutionException {
        execute(1, 0);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute11() throws IpcCommandExecutionException {
        execute(1, 1);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute12() throws IpcCommandExecutionException {
        execute(1, 2);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute13() throws IpcCommandExecutionException {
        execute(1, 3);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute14() throws IpcCommandExecutionException {
        execute(1, 4);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute20() throws IpcCommandExecutionException {
        execute(2, 0);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute21() throws IpcCommandExecutionException {
        execute(2, 1);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute22() throws IpcCommandExecutionException {
        execute(2, 2);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute23() throws IpcCommandExecutionException {
        execute(2, 3);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute24() throws IpcCommandExecutionException {
        execute(2, 4);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute30() throws IpcCommandExecutionException {
        execute(3, 0);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute31() throws IpcCommandExecutionException {
        execute(3, 1);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute32() throws IpcCommandExecutionException {
        execute(3, 2);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute33() throws IpcCommandExecutionException {
        execute(3, 3);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute34() throws IpcCommandExecutionException {
        execute(3, 4);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute40() throws IpcCommandExecutionException {
        execute(4, 0);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute41() throws IpcCommandExecutionException {
        execute(4, 1);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute42() throws IpcCommandExecutionException {
        execute(4, 2);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute43() throws IpcCommandExecutionException {
        execute(4, 3);
    }

    /**
     * Tests {@link SetAsset#execute(IpcCall, Map)} with indices between 0 and 4.
     * 
     * @throws IpcCommandExecutionException should not happen 
     */
    @Test
    public void execute44() throws IpcCommandExecutionException {
        execute(4, 4);
    }
    
}
