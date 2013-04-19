package au.com.sensis.stubby.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class StubRequestTest {

    @SuppressWarnings("serial")
    public static class TestBean implements Serializable { }
    
    private TestBean body;
    private List<StubParam> params;
    private StubRequest request;

    @Before
    public void before() {
        body = new TestBean();
        
        params = new ArrayList<StubParam>();
        params.add(new StubParam("foo", "bar1"));
        params.add(new StubParam("foo", "bar2"));
        
        request = new StubRequest();
        request.setParams(params);
        request.setMethod("GET");
        request.setPath("/request/path");
        request.setBody(body);
    }

    @Test
    public void testCopyConstructor() {
        StubRequest copy = new StubRequest(request);
        
        assertEquals("GET", copy.getMethod());
        assertEquals("/request/path", copy.getPath());
        assertEquals("foo", copy.getParams().get(0).getName());
        assertEquals("bar1", copy.getParams().get(0).getValue());
        
        assertTrue(copy.getParams() != params); // assert deep copy

        assertTrue(copy.getBody() != null); // assert calls parent constructor
        assertTrue(copy.getBody() != body); 
    }

    @Test
    public void testGetParamsFound() {
        assertEquals(
                Arrays.asList("bar1", "bar2"),
                request.getParams("foo"));
    }
    
    @Test
    public void getGetParamsNotFound() {
        assertEquals(Arrays.asList(), request.getParams("FOO")); // ensure case-sensitive
    }
    
    @Test
    public void testGetParamFound() {
        assertEquals("bar1", request.getParam("foo"));
    }
    
    @Test
    public void getGetParamNotFound() {
        assertNull(request.getParam("FOO")); // ensure case-sensitive
    }
    
    @Test
    public void testJsonSerialize() {
        // TODO: assert Jackson serializes/deserializes correctly
    }
}
