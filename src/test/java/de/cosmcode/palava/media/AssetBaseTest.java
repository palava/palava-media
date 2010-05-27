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

package de.cosmcode.palava.media;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.media.asset.AssetBase;

/**
 * Abstract test for {@link AssetBase}s.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
public abstract class AssetBaseTest implements UnitProvider<AssetBase> {
    
    private Date future() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }
    
    private Date past() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }
    
    /**
     * Tests {@link AssetBase#setExpiresAt(Date)} with null.
     */
    @Test
    public void setExpiresNull() {
        final AssetBase unit = unit();
        final Date expiresAt = null;
        unit.setExpiresAt(expiresAt);
        Assert.assertSame(expiresAt, unit.getExpiresAt());
        Assert.assertNull(unit.getExpiresAt());
    }

    /**
     * Tests {@link AssetBase#setExpiresAt(Date)} with a date in the future.
     */
    @Test
    public void setExpiresFuture() {
        final AssetBase unit = unit();
        final Date expiresAt = future();
        unit.setExpiresAt(expiresAt);
        Assert.assertSame(expiresAt, unit.getExpiresAt());
    }
    
    /**
     * Tests {@link AssetBase#setExpiresAt(Date)} with a date in the past.
     */
    @Test
    public void setExpiresPast() {
        final AssetBase unit = unit();
        final Date expiresAt = past();
        unit.setExpiresAt(expiresAt);
        Assert.assertSame(expiresAt, unit.getExpiresAt());
    }
    
    /**
     * Tests {@link AssetBase#isExpirable()} with a null expiresAt date.
     */
    @Test
    public void isExpirableNull() {
        final AssetBase unit = unit();
        unit.setExpiresAt(null);
        Assert.assertFalse(unit.isExpirable());
    }
    
    /**
     * Tests {@link AssetBase#isExpirable()} with a future expiresAt date.
     */
    @Test
    public void isExpirableFuture() {
        final AssetBase unit = unit();
        unit.setExpiresAt(future());
        Assert.assertTrue(unit.isExpirable());
    }
    
    /**
     * Tests {@link AssetBase#isExpirable()} with a past expiresAt date.
     */
    @Test
    public void isExpirablePast() {
        final AssetBase unit = unit();
        unit.setExpiresAt(past());
        Assert.assertTrue(unit.isExpirable());
    }
    
    /**
     * Tests {@link AssetBase#isExpiring()} with expired and null expiresAt date.
     */
    @Test
    public void isExpiringExpiredNull() {
        final AssetBase unit = unit();
        unit.setExpired(true);
        Assert.assertTrue(unit.isExpired());
        unit.setExpiresAt(null);
        Assert.assertFalse(unit.isExpiring());
    }

    /**
     * Tests {@link AssetBase#isExpiring()} with expired and a future expiresAt date.
     */
    @Test
    public void isExpiringExpiredFuture() {
        final AssetBase unit = unit();
        unit.setExpired(true);
        Assert.assertTrue(unit.isExpired());
        unit.setExpiresAt(future());
        Assert.assertFalse(unit.isExpiring());
    }

    /**
     * Tests {@link AssetBase#isExpiring()} with expired and a past expiresAt date.
     */
    @Test
    public void isExpiringExpiredPast() {
        final AssetBase unit = unit();
        unit.setExpired(true);
        Assert.assertTrue(unit.isExpired());
        unit.setExpiresAt(past());
        Assert.assertFalse(unit.isExpiring());
    }

    /**
     * Tests {@link AssetBase#isExpiring()} with not expired and null expiresAt date.
     */
    @Test
    public void isExpiringNotExpiredNull() {
        final AssetBase unit = unit();
        unit.setExpired(false);
        Assert.assertFalse(unit.isExpired());
        unit.setExpiresAt(null);
        Assert.assertFalse(unit.isExpiring());
    }

    /**
     * Tests {@link AssetBase#isExpiring()} with not expired and a future expiresAt date.
     */
    @Test
    public void isExpiringNotExpiredFuture() {
        final AssetBase unit = unit();
        unit.setExpired(false);
        Assert.assertFalse(unit.isExpired());
        unit.setExpiresAt(future());
        Assert.assertFalse(unit.isExpiring());
    }

    /**
     * Tests {@link AssetBase#isExpiring()} with not expired and a past expiresAt date.
     */
    @Test
    public void isExpiringNotExpiredPast() {
        final AssetBase unit = unit();
        unit.setExpired(false);
        Assert.assertFalse(unit.isExpired());
        unit.setExpiresAt(past());
        Assert.assertTrue(unit.isExpiring());
    }
    
    /**
     * Tests {@link AssetBase#setExpired()}.
     */
    @Test
    public void setExpired() {
        final AssetBase unit = unit();
        Assert.assertFalse(unit.isExpired());
        unit.setExpired(true);
        Assert.assertTrue(unit.isExpired());
    }
    
    /**
     * Tests {@link AssetBase#isUnexpiring()} with expired and a null expiresAt date.
     */
    @Test
    public void isUnexpiringExpiredNull() {
        final AssetBase unit = unit();
        unit.setExpired(true);
        Assert.assertTrue(unit.isExpired());
        unit.setExpiresAt(null);
        Assert.assertTrue(unit.isUnexpiring());
    }

    /**
     * Tests {@link AssetBase#isUnexpiring()} with expired and a future expiresAt date.
     */
    @Test
    public void isUnexpiringExpiredFuture() {
        final AssetBase unit = unit();
        unit.setExpired(true);
        Assert.assertTrue(unit.isExpired());
        unit.setExpiresAt(future());
        Assert.assertTrue(unit.isUnexpiring());
    }

    /**
     * Tests {@link AssetBase#isUnexpiring()} with expired and a past expiresAt date.
     */
    @Test
    public void isUnexpiringExpiredPast() {
        final AssetBase unit = unit();
        unit.setExpired(true);
        Assert.assertTrue(unit.isExpired());
        unit.setExpiresAt(past());
        Assert.assertFalse(unit.isUnexpiring());
    }

    /**
     * Tests {@link AssetBase#isUnexpiring()} with not expired and a null expiresAt date.
     */
    @Test
    public void isUnexpiringNotExpiredNull() {
        final AssetBase unit = unit();
        unit.setExpired(false);
        Assert.assertFalse(unit.isExpired());
        unit.setExpiresAt(null);
        Assert.assertFalse(unit.isUnexpiring());
    }

    /**
     * Tests {@link AssetBase#isUnexpiring()} with not expired and a future expiresAt date.
     */
    @Test
    public void isUnexpiringNotExpiredFuture() {
        final AssetBase unit = unit();
        unit.setExpired(false);
        Assert.assertFalse(unit.isExpired());
        unit.setExpiresAt(future());
        Assert.assertFalse(unit.isUnexpiring());
    }

    /**
     * Tests {@link AssetBase#isUnexpiring()} with not expired and a past expiresAt date.
     */
    @Test
    public void isUnexpiringNotExpiredPast() {
        final AssetBase unit = unit();
        unit.setExpired(false);
        Assert.assertFalse(unit.isExpired());
        unit.setExpiresAt(past());
        Assert.assertFalse(unit.isUnexpiring());
    }
    
}
