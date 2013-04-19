package au.com.sensis.stubby.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class DeepCopyUtilsTest {
    
    @SuppressWarnings("serial")
    public static class TestBean implements Serializable {
        private List<String> items = new ArrayList<String>();
        public List<String> getItems() {
            return items;
        }
    }
    
    @Test
    public void testDeepCopy() {
        TestBean original = new TestBean();
        original.getItems().add("one");
        
        TestBean copy = DeepCopyUtils.deepCopy(original);
        copy.getItems().add("two");
        
        assertTrue(original != copy);
        assertEquals(Arrays.asList("one"), original.getItems()); // ensure deep copy
        assertEquals(Arrays.asList("one", "two"), copy.getItems());
    }
    
}
