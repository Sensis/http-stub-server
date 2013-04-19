package au.com.sensis.stubby.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import au.com.sensis.stubby.model.StubMessage;

public class HttpMessageUtilsTest {

    @Test
    public void testUpperCaseHeader() {
        Assert.assertEquals("Header", HttpMessageUtils.upperCaseHeader("header"));
        Assert.assertEquals("Header-Name", HttpMessageUtils.upperCaseHeader("header-name"));
        Assert.assertEquals("X-Header-Name", HttpMessageUtils.upperCaseHeader("x-header-name"));
        Assert.assertEquals("-X-Header-Name", HttpMessageUtils.upperCaseHeader("-x-header-name"));
    }
    
    @Test
    public void testIsText() {
        StubMessage message = new StubMessage() { };
        
        message.setHeader("Content-Type", "text/plain");
        assertTrue(HttpMessageUtils.isText(message));
        
        message.setHeader("Content-Type", "text/anything; charset=UTF-8");
        assertTrue(HttpMessageUtils.isText(message));
    }
    
    @Test
    public void testIsText_notText() {
        StubMessage message = new StubMessage() { };
        
        message.setHeader("Content-Type", "application/xml");
        assertFalse(HttpMessageUtils.isText(message));
    }
    
    @Test
    public void testIsText_noHeader() {
        StubMessage message = new StubMessage() { };

        assertFalse(HttpMessageUtils.isText(message));
    }
    
    @Test
    public void testIsJson() {
        StubMessage message = new StubMessage() { };
        
        message.setHeader("Content-Type", "application/json");
        assertTrue(HttpMessageUtils.isJson(message));
        
        message.setHeader("Content-Type", "application/json; charset=UTF-8");
        assertTrue(HttpMessageUtils.isJson(message));
    }
    
    @Test
    public void testIsJson_notJson() {
        StubMessage message = new StubMessage() { };
        
        message.setHeader("Content-Type", "application/xml");
        assertFalse(HttpMessageUtils.isJson(message));
    }
    
    @Test
    public void testIsJson_noHeader() {
        StubMessage message = new StubMessage() { };

        assertFalse(HttpMessageUtils.isJson(message));
    }
    
    @Test
    public void testBodyAsText() {
        StubMessage message = new StubMessage() { };
        message.setBody("text");
        assertEquals("text", HttpMessageUtils.bodyAsText(message));
    }
    
    @Test(expected=RuntimeException.class)
    public void testBodyAsText_unknown() {
        StubMessage message = new StubMessage() { };
        message.setBody(Arrays.asList("foo"));
        
        HttpMessageUtils.bodyAsText(message);
    }
    
    @Test
    public void testBodyAsJson_string() {
        StubMessage message = new StubMessage() { };
        message.setBody("[\"foo\",\"bar\"]");
        assertEquals(Arrays.asList("foo", "bar"), HttpMessageUtils.bodyAsJson(message));
    }
    
    @Test
    public void testBodyAsJson_list() {
        StubMessage message = new StubMessage() { };
        message.setBody(Arrays.asList("foo", "bar"));
        assertEquals(Arrays.asList("foo", "bar"), HttpMessageUtils.bodyAsJson(message));
    }
    
    @Test
    public void testBodyAsJson_map() {
        Map<String,String> map = new HashMap<String,String>();
        map.put("foo", "bar");
        
        StubMessage message = new StubMessage() { };
        message.setBody(map);
        
        assertEquals(new HashMap<String,String>(map), HttpMessageUtils.bodyAsJson(message));
    }
    
    @Test(expected=RuntimeException.class)
    public void testBodyAsJson_unknown() {
        StubMessage message = new StubMessage() { };
        message.setBody(new Integer(666));
        
        HttpMessageUtils.bodyAsJson(message);
    }
    
}
