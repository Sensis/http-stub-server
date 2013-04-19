package au.com.sensis.stubby.js;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import au.com.sensis.stubby.model.StubExchange;
import au.com.sensis.stubby.model.StubParam;
import au.com.sensis.stubby.model.StubRequest;
import au.com.sensis.stubby.model.StubResponse;

public class ScriptTest {
    
    @SuppressWarnings("serial")
    public static class TestBean implements Serializable {
        private List<String> items = new ArrayList<String>();
        public List<String> getItems() {
            return items;
        }
    }

    private StubExchange exchange;
    private StubRequest request;
    private StubResponse response;
    private TestBean testBean;
    private ScriptWorld world;
    
    @Before
    public void before() {
        givenTestBean();
        givenDefaultRequest();
        givenDefaultResponse();
        givenDefaultExchange();
    }
    
    private void givenTestBean() {
        testBean = new TestBean();
        testBean.getItems().add("one");
        testBean.getItems().add("two");
    }
    
    private void givenDefaultRequest() {
        request = new StubRequest();
        request.setMethod("POST");
        request.setPath("/request/path");
        request.setParams(new ArrayList<StubParam>());
        request.getParams().add(new StubParam("foo", "bar"));
        request.setHeaders(new ArrayList<StubParam>());
        request.getHeaders().add(new StubParam("Content-Type", "text/plain"));
        request.setBody("request body");
    }
    
    private void givenJsonRequestBody() {
        request.setBody(testBean);
    }
    
    private void givenJsonResponseBody() {
        response.setBody(testBean);
    }
    
    private void givenDefaultResponse() {
        response = new StubResponse();
        response.setHeaders(new ArrayList<StubParam>());
        response.getHeaders().add(new StubParam("Content-Type", "application/json"));
        response.setBody("response body");
        response.setStatus(200);
    }
    
    private void givenDefaultExchange() {
        exchange = new StubExchange();
        exchange.setRequest(request);
        exchange.setResponse(response);
        exchange.setDelay(1234L);
    }
    
    private Object execute(String script) {
        world = new ScriptWorld(request, exchange);
        return new Script(script).execute(world);
    }
    
    @Test
    public void testEmptyScript() {
        execute("");
    }
    
    @Test
    public void testSimpleExpr() {
        assertEquals(new Double(3), execute("var a = 1; var b = 2; a + b;"));
    }

    @Test
    public void testGetDelay() {
        assertEquals(new Long(1234), execute("exchange.delay"));
    }
    
    @Test
    public void setSetDelay() {
        execute("exchange.delay = 666");
        assertEquals(new Long(666), world.getDelay());
    }
    
    @Test
    public void testGetRequestFields() {
        assertEquals("POST", execute("exchange.request.method"));
        assertEquals("/request/path", execute("exchange.request.path"));
        assertEquals("bar", execute("exchange.request.getParams('foo').get(0)"));
        assertEquals("bar", execute("exchange.request.getParam('foo')"));
        assertEquals("text/plain", execute("exchange.request.getHeader('content-type')")); // ensure case insensitive
        assertEquals("request body", execute("exchange.request.body"));
    }
 
    @Test
    public void testGetResponseFields() {
        assertEquals(new Integer(200), execute("exchange.response.status"));
        assertEquals("application/json", execute("exchange.response.getHeader('content-type')")); // ensure case insensitive
        assertEquals("response body", execute("exchange.response.body"));
    }
    
    @Test
    public void testSetResponseFields() {
        execute("exchange.response.status = 501");
        assertEquals(new Integer(501), world.getResponse().getStatus());
        
        execute("exchange.response.setHeader('Content-Type', 'text/xml')");
        assertEquals("text/xml", world.getResponse().getHeader("content-type")); // ensure case insensitive
        
        execute("exchange.response.removeHeader('Content-Type')");
        assertNull(world.getResponse().getHeader("content-type"));
        
        execute("exchange.response.body = 'foo'");
        assertEquals("foo", world.getResponse().getBody());
    }
    
    @Test
    public void testGetRequestJsonBody() {
        givenJsonRequestBody();
        assertEquals("one", execute("exchange.request.body.items.get(0)"));
        assertEquals("two", execute("exchange.request.body.items.get(1)"));
    }
    
    @Test
    public void testSetRequestJsonBody() {
        givenJsonRequestBody();
        execute("exchange.request.body.items.add('three')");
        assertEquals(2, testBean.getItems().size()); // assert original not changed
        assertEquals(3, ((TestBean)world.getRequest().getBody()).getItems().size()); 
    }
    
    @Test
    public void testGetResponseJsonBody() {
        givenJsonResponseBody();
        assertEquals("one", execute("exchange.response.body.items.get(0)"));
        assertEquals("two", execute("exchange.response.body.items.get(1)"));
    }
    
    @Test
    public void testSetResponseJsonBody() {
        givenJsonResponseBody();
        execute("exchange.response.body.items.add('three')"); 
        assertEquals(2, testBean.getItems().size()); // assert original not changed
        assertEquals(3, ((TestBean)world.getResponse().getBody()).getItems().size()); 
    }
    
}
