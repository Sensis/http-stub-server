package au.com.sensis.stubby.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import au.com.sensis.stubby.model.StubExchange;
import au.com.sensis.stubby.model.StubParam;
import au.com.sensis.stubby.model.StubRequest;
import au.com.sensis.stubby.model.StubResponse;
import au.com.sensis.stubby.service.model.StubServiceExchange;
import au.com.sensis.stubby.service.model.StubServiceResult;

import static org.junit.Assert.*;

public class StubServiceTest {

    private static final Integer OK = 200;
    private static final Integer CREATED = 201;
    private static final Integer SERVER_ERROR = 500;

    private StubRequest request; // incoming request
    private StubExchange exchange; // stubbed exchange
    private StubService service;

    @Before
    public void before() {
        service = new StubService();

        request = new StubRequest();

        exchange = new StubExchange();
        exchange.setRequest(new StubRequest());
        exchange.setResponse(new StubResponse());

        givenDefaultRequest();
        givenDefaultExchange();
        givenDefaultService();
    }

    private void givenDefaultRequest() {
        request.setMethod("GET");
        request.setPath("/foo");
    }

    private void givenDefaultExchange() {
        exchange.getRequest().setMethod("GET");
        exchange.getRequest().setPath("/foo");
        exchange.getResponse().setStatus(OK);
    }

    private void givenDefaultService() {
        exchange.getResponse().setStatus(OK);
        exchange.getRequest().setMethod("G.T");
        service.addResponse(new StubExchange(exchange)); // create copy

        exchange.getResponse().setStatus(CREATED);
        exchange.getRequest().setMethod("GE."); // make sure patterns differ (or they will overwrite eachother)
        service.addResponse(new StubExchange(exchange));

        assertEquals(2, service.getResponses().size());
    }

    @Test
    public void testMatch() {
        StubServiceResult result = service.findMatch(request);

        assertTrue(result.matchFound());
        assertEquals(CREATED, result.getResponse().getStatus()); // most recent stubbed first
        assertEquals(1, result.getAttempts().size()); // ensure attempts returned
    }
    
    @Test
    public void testAttemptRecorded() {
        service.findMatch(request);

        StubServiceExchange response = service.getResponse(0);
        assertEquals(1, response.getAttempts().size());
        assertTrue(response.getAttempts().get(0).matches());
    }

    @Test
    public void testNoMatch() {
        service.addResponse(exchange);
        request.setPath("/not/found");

        assertFalse(service.getResponses().isEmpty());
        assertFalse(service.findMatch(request).matchFound());
    }

    @Test
    public void testDeleteResponses() {
        service.deleteResponses();

        assertFalse(service.findMatch(request).matchFound());
    }

    @Test
    public void testDeleteResponse() {
        service.deleteResponse(0); // delete first

        StubServiceResult result = service.findMatch(request);

        assertTrue(result.matchFound());
        assertEquals(OK, result.getResponse().getStatus());
    }

    @Test
    public void testGetResponses() {
        assertEquals(CREATED, service.getResponses().get(0).getExchange().getResponse().getStatus()); // most recent first
        assertEquals(OK, service.getResponses().get(1).getExchange().getResponse().getStatus());
    }

    @Test
    public void testRequestsRecorded() {
        request.setPath("/foo");
        assertTrue(service.findMatch(new StubRequest(request)).matchFound());

        request.setPath("/not/found");
        assertFalse(service.findMatch(new StubRequest(request)).matchFound()); // ensure even failed matches recorded

        assertEquals("/not/found", service.getRequests().get(0).getPath()); // most recent first
        assertEquals("/foo", service.getRequests().get(1).getPath());
    }

    @Test
    public void testDelay() {
        exchange.setDelay(1234L);
        service.addResponse(exchange);

        StubServiceResult result = service.findMatch(request);

        assertTrue(result.matchFound());
        assertEquals(new Long(1234), result.getDelay());
    }

    @Test
    public void testScriptExecuted() {
        exchange.setScript("exchange.response.status = 500; exchange.delay = 666; exchange.response.body = exchange.request.path");
        service.addResponse(exchange);

        StubServiceResult result = service.findMatch(request);

        assertTrue(result.matchFound());
        assertEquals(SERVER_ERROR, result.getResponse().getStatus());
        assertEquals(new Long(666), result.getDelay());
        assertEquals("/foo", result.getResponse().getBody());
    }

    @Test
    public void testDuplicatePatternRemoved() {
        service.deleteResponses();

        exchange.getResponse().setStatus(OK);
        service.addResponse(new StubExchange(exchange)); // create copies

        exchange.getResponse().setStatus(CREATED);
        service.addResponse(new StubExchange(exchange));

        assertEquals(1, service.getResponses().size());
        assertEquals(CREATED, // ensure last stubbed request is kept
                service.getResponses().get(0).getExchange().getResponse().getStatus());
    }
    
    @Test
    public void testRequestFilterEmpty() {
        request.setPath("/test");
        service.findMatch(new StubRequest(request));
        assertEquals(1, service.findRequests(new StubRequest()).size()); // empty filter
    }
    
    @Test
    public void testRequestFilter() {
        request.setPath("/test");
        service.findMatch(new StubRequest(request));
        
        request.setPath("/test");
        request.setParams(Arrays.asList(new StubParam("foo", "bar")));
        service.findMatch(new StubRequest(request));
        
        assertEquals(2, service.getRequests().size());
        
        StubRequest filter = new StubRequest();
        filter.setParams(Arrays.asList(new StubParam("foo", "b.r")));
        assertEquals(1, service.findRequests(filter).size()); // should only match one of the requests
    }

    @Test
    public void testRequestFilterWaitNotFound() {
        request.setPath("/test");
        service.findMatch(new StubRequest(request));

        StubRequest filter = new StubRequest();
        filter.setPath("/foo");

        assertEquals(0, service.findRequests(new StubRequest(filter), 100).size());
    }

    @Test
    public void testRequestFilterWait() {
        request.setPath("/test1");
        service.findMatch(new StubRequest(request));

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000); // attempt to make 'findMatch' execute after parent thread starts waiting
                    request.setPath("/test2");
                    service.findMatch(new StubRequest(request));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        StubRequest filter = new StubRequest();
        filter.setPath("/test2"); // wait for second request

        List<StubRequest> result = service.findRequests(new StubRequest(filter), 5000);
        assertEquals(1, result.size());
    }

}
