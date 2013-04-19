package au.com.sensis.stubby.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import au.com.sensis.stubby.test.support.TestBase;

public class ScriptTest extends TestBase {

    private static final String TEST_SCRIPT = 
            "if (request.getParam('run') == 'true') { exchange.response.status = 202; exchange.response.body = exchange.request.getParam('run'); }";
            
    private void givenTestScript() {
        builder().setRequestPath("/script/bar").setResponseStatus(201).setResponseBody("original").setScript(TEST_SCRIPT).stub();
    }
    
    @Test
    public void testRunFalse() {
        givenTestScript();
        
        GenericClientResponse result = client.executeGet("/script/bar?run=false");
        assertEquals(201, result.getStatus());
        assertEquals("original", result.getText());
    }
    
    @Test
    public void testRunTrue() {
        givenTestScript();
        
        GenericClientResponse result = client.executeGet("/script/bar?run=true");
        assertEquals(202, result.getStatus());
        assertEquals("true", result.getText());
    }

}