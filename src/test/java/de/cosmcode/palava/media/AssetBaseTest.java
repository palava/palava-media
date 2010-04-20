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

package de.cosmcode.palava.media;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.media.AssetBase;

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
        unit.setExpired();
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
        unit.setExpired();
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
        unit.setExpired();
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
        unit.setExpired();
        Assert.assertTrue(unit.isExpired());
    }
    
    /**
     * Tests {@link AssetBase#isUnexpiring()} with expired and a null expiresAt date.
     */
    @Test
    public void isUnexpiringExpiredNull() {
        final AssetBase unit = unit();
        unit.setExpired();
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
        unit.setExpired();
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
        unit.setExpired();
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
        Assert.assertFalse(unit.isExpired());
        unit.setExpiresAt(past());
        Assert.assertFalse(unit.isUnexpiring());
    }
    
}
