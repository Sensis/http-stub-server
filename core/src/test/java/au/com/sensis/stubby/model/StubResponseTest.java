package au.com.sensis.stubby.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

public class StubResponseTest {
    
    @SuppressWarnings("serial")
    public static class TestBean implements Serializable { }
    
    private TestBean body;    
    private StubResponse response;

    @Before
    public void before() {        
        body = new TestBean();
        
        response = new StubResponse();
        response.setStatus(503);
        response.setBody(body);
    }
        
    @Test
    public void testCopyConstructor() {
        StubResponse copy = new StubResponse(response);

        assertEquals(new Integer(503), copy.getStatus());
        
        assertTrue(copy.getBody() != null); // assert calls parent constructor
        assertTrue(copy.getBody() != body); 
    }
    
    @Test
    public void testJsonSerialize() {
        // TODO: assert Jackson serializes/deserializes correctly
    }
}
