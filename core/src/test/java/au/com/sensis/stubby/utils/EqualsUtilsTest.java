package au.com.sensis.stubby.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EqualsUtilsTest {

    @Test
    public void testSafeEquals() {
        assertTrue(EqualsUtils.safeEquals(null, null));
        assertTrue(EqualsUtils.safeEquals("foo", "foo"));
        assertFalse(EqualsUtils.safeEquals("foo", null));
        assertFalse(EqualsUtils.safeEquals(null, "foo"));
        assertFalse(EqualsUtils.safeEquals("foo", "bar"));
    }
    
}
