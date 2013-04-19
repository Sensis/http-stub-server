package au.com.sensis.stubby.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.junit.Test;

import au.com.sensis.stubby.test.support.TestBase;

@SuppressWarnings("rawtypes")
public class JsonBodyPatternTest extends TestBase {
    
    @Test
    public void emptyPattern() {
        Map pattern = new HashMap();
        
        builder().setRequestPath("/foo").setResponseStatus(200).setRequestBody(pattern).stub();
        
        assertOk(client.executePost("/foo", "{}", ContentType.APPLICATION_JSON));
    }
    
    @Test
    public void withPatternMatch() {
        Map pattern = new HashMap();
        pattern.put("key_a", "value_a");
        
        builder().setRequestPath("/foo").setResponseStatus(200).setRequestBody(pattern).stub();
        
        assertOk(client.executePost("/foo", "{\"key_a\":\"value_a\", \"key_b\":\"value_b\"}", ContentType.APPLICATION_JSON));
    }
    
    @Test
    public void withPatternNotMatch() {
        Map pattern = new HashMap();
        pattern.put("key_a", "incorrect");
        
        builder().setRequestPath("/foo").setResponseStatus(200).setRequestBody(pattern).stub();
        
        assertNotFound(client.executePost("/foo", "{\"key_a\":\"value_a\", \"key_b\":\"value_b\"}", ContentType.APPLICATION_JSON));
    }
        
    @Test
    public void notJson() {
        Map pattern = new HashMap();
        
        builder().setRequestPath("/foo").setResponseStatus(200).setRequestBody(pattern).stub();
        
        assertNotFound(client.executePost("/foo", "{}", ContentType.TEXT_PLAIN));
    }

}