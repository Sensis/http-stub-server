package au.com.sensis.stubby.model;

import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

public class StubParamTest {
    
    @Test
    public void testCopyConstructor() {
        StubParam original = new StubParam("foo", "bar");
        StubParam copy = new StubParam(original);
        
        ReflectionAssert.assertReflectionEquals(original, copy); // guard against extra fields
    }
    
}
