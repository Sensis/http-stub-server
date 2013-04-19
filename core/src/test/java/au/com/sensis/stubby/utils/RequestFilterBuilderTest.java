package au.com.sensis.stubby.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import au.com.sensis.stubby.model.StubParam;
import au.com.sensis.stubby.model.StubRequest;

public class RequestFilterBuilderTest {

    private List<StubParam> params;
    private RequestFilterBuilder builder;
    
    @Before
    public void before() {
        params = new ArrayList<StubParam>();
        builder = new RequestFilterBuilder();
    }
    
    @Test
    public void testMethod() {
        params.add(new StubParam("method", "G.T"));
                
        StubRequest filter = builder.fromParams(params).getFilter();
        
        assertEquals("G.T", filter.getMethod());
    }
    
    @Test
    public void testPath() {
        params.add(new StubParam("path", "/foo/.*"));
                
        StubRequest filter = builder.fromParams(params).getFilter();
        
        assertEquals("/foo/.*", filter.getPath());
    }
    
    @Test
    public void testParams() {
        params.add(new StubParam("param[foo]", "bar1"));
        params.add(new StubParam("param[foo]", "bar2"));
                
        StubRequest filter = builder.fromParams(params).getFilter();
        
        assertEquals(Arrays.asList("bar1", "bar2"), filter.getParams("foo"));
    }
    
    @Test
    public void testHeaders() {
        params.add(new StubParam("header[X-Foo]", "bar1"));
        params.add(new StubParam("header[X-Foo]", "bar2"));
                
        StubRequest filter = builder.fromParams(params).getFilter();
        
        assertEquals(Arrays.asList("bar1", "bar2"), filter.getHeaders("X-Foo"));
    }
    
}
