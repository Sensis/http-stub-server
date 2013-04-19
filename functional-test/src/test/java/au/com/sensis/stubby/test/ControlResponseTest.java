package au.com.sensis.stubby.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import au.com.sensis.stubby.test.model.JsonExchange;
import au.com.sensis.stubby.test.model.JsonPair;
import au.com.sensis.stubby.test.model.JsonRequest;
import au.com.sensis.stubby.test.model.JsonResponse;
import au.com.sensis.stubby.test.model.JsonStubbedExchangeList;
import au.com.sensis.stubby.test.support.TestBase;

public class ControlResponseTest extends TestBase {
    
    private MessageBuilder builder1() {
        return builder().setRequestPath("/test/1").setResponseStatus(200);
    }
    
    private MessageBuilder builder2() {
        return builder()
            .setRequestMethod("PUT")
            .setRequestPath("/test/2")
            .addRequestParam("foo", "bar1")
            .addRequestParam("foo", "bar2")    
            .addRequestHeader("X-Request-Foo", "bar1")
            .addRequestHeader("X-Request-Foo", "bar2")
            .setRequestBody("request body")
            .addResponseHeader("X-Response-Foo", "bar1")
            .addResponseHeader("X-Response-Foo", "bar2")
            .setResponseStatus(201)
            .setResponseBody("response body")
            .setScript("script();")
            .setDelay(1234L);
    }
    
    @Test
    public void testGetPatternDetails() {
        builder2().stub();
        
        JsonExchange exchange = client.getResponse(0).exchange;
        JsonRequest pattern = exchange.request;
        
        assertEquals("PUT", pattern.method);
        assertEquals("/test/2", pattern.path);
        assertTrue(pattern.params.contains(new JsonPair("foo", "bar1")));
        assertTrue(pattern.params.contains(new JsonPair("foo", "bar2")));
        assertHasHeader(pattern, "X-Request-Foo", "bar1");
        assertHasHeader(pattern, "X-Request-Foo", "bar2");
        assertEquals("request body", pattern.body);
    }
    
    @Test
    public void testGetResponseDetails() {
        builder2().stub();
        
        JsonExchange exchange = client.getResponse(0).exchange;
        JsonResponse response = exchange.response;
        
        assertEquals(new Integer(201), response.status);
        assertHasHeader(response, "X-Response-Foo", "bar1");
        assertHasHeader(response, "X-Response-Foo", "bar2");
        assertEquals("response body", response.body);
    }
    
    @Test
    public void testGetOtherDetails() {
        builder2().stub();
        
        JsonExchange exchange = client.getResponse(0).exchange;
        
        assertEquals(new Long(1234), exchange.delay);
        assertEquals("script();", exchange.script);
    }
    
    @Test
    public void testGetResponseOrder() {
        builder1().stub();
        builder2().stub();
        
        assertEquals("/test/2", client.getResponse(0).exchange.request.path);
        assertEquals("/test/1", client.getResponse(1).exchange.request.path);
    }
    
    @Test
    public void testGetResponsesOrder() {
        builder1().stub();
        builder2().stub();
        
        JsonStubbedExchangeList responses = client.getResponses();
        
        assertEquals(2, responses.size());
        assertEquals("/test/2", responses.get(0).exchange.request.path);
        assertEquals("/test/1", responses.get(1).exchange.request.path);
    }
    
    @Test
    public void testGetResponseNotFound() {
        builder1().stub();
        
        assertEquals(200, client.executeGet("/_control/responses/0").getStatus());
        assertEquals(404, client.executeGet("/_control/responses/1").getStatus());
    }
    
    @Test
    public void testDeleteResponses() {
        builder1().stub();
        assertEquals(1, client.getResponses().size());
        
        client.deleteResponses();
        assertEquals(0, client.getResponses().size());
    }
    
    /*
    @Test
    public void testDeleteSingleResponse() {
        builder1().stub();
        builder2().stub();
        
        assertEquals("/test/2", client.getResponse(0).exchange.request.path);
        
        client.deleteResponse(0);
        assertEquals("/test/1", client.getResponse(1).exchange.request.path);
        
        client.deleteResponse(0);
        assertEquals(0, client.getResponses().size());
    }
    
    @Test
    public void testDeleteNotFound() {
        builder1().stub();
        
        assertEquals(404, client.executeDelete("/_control/responses/1").getStatus());
        assertEquals(200, client.executeDelete("/_control/responses/0").getStatus());
    }
    */
}
