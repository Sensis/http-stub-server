package au.com.sensis.stubby.service.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class ParamPatternTest {

    private ParamPattern instance1;
    private ParamPattern instance2;

    @Before
    public void before() {
        instance1 = new ParamPattern("foo", Pattern.compile("bar"));
        instance2 = new ParamPattern("foo", Pattern.compile("bar"));
    }

    @Test
    public void testEquality() {
        assertEquals(instance1, instance2);
    }

    @Test
    public void testHashCode() {
        assertEquals(instance1.hashCode(), instance2.hashCode());
    }
    
    @Test
    public void testCopyConstructor() {
        instance2 = new ParamPattern(instance1);
        
        assertEquals(instance1, instance2);
        assertTrue(instance1 != instance2); // ensure copied
    }
    
}
