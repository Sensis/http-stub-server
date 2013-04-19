package au.com.sensis.stubby.test;

import org.apache.http.entity.ContentType;
import org.junit.Test;

import au.com.sensis.stubby.test.support.TestBase;

public class TextBodyPatternTest extends TestBase {
    
    @Test
    public void wildcardPattern() {        
        builder().setRequestPath("/foo").setResponseStatus(200).setRequestBody(".*").stub();
        
        assertOk(client.executePost("/foo", "test", ContentType.TEXT_PLAIN));
    }
    
    @Test
    public void withPatternMatch() {        
        builder().setRequestPath("/foo").setResponseStatus(200).setRequestBody("foo b.+").stub();
        
        assertOk(client.executePost("/foo", "foo bar", ContentType.TEXT_PLAIN));
    }
    
    @Test
    public void withPatternNotMatch() {        
        builder().setRequestPath("/foo").setResponseStatus(200).setRequestBody("foo b.+").stub();
        
        assertNotFound(client.executePost("/foo", "incorrect", ContentType.TEXT_PLAIN));
    }
        
    @Test
    public void notText() {
        builder().setRequestPath("/foo").setResponseStatus(200).setRequestBody(".*").stub();
        
        assertNotFound(client.executePost("/foo", "test", ContentType.APPLICATION_JSON));
    }

}