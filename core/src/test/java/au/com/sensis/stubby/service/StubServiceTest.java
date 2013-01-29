package au.com.sensis.stubby.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import au.com.sensis.stubby.model.StubExchange;
import au.com.sensis.stubby.model.StubRequest;
import au.com.sensis.stubby.model.StubResponse;
import au.com.sensis.stubby.service.model.StubServiceResult;

public class StubServiceTest {

    private static final Integer OK = 200;
    private static final Integer NOT_FOUND = 404;
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

        exchange.getResponse().setStatus(NOT_FOUND);
        exchange.getRequest().setMethod("GE."); // make sure patterns differ (or they will overwrite eachother)
        service.addResponse(new StubExchange(exchange));

        assertEquals(2, service.getResponses().size());
    }

    @Test
    public void testMatch() {
        service.addResponse(exchange);

        StubServiceResult result = service.findMatch(request);

        assertTrue(result.matchFound());
        assertEquals(NOT_FOUND, result.getResponse().getStatus());
    }

    @Test
    public void testNoMatch() {
        service.addResponse(exchange);
        request.setPath("/not/found");

        assertFalse(service.getResponses().isEmpty());
        assertFalse(service.findMatch(request).matchFound());
    }

    @Test
    public void testMatchOrder() {
        StubServiceResult result = service.findMatch(request);

        assertTrue(result.matchFound());
        assertEquals(NOT_FOUND, result.getResponse().getStatus()); // most recent stubbed first
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
        assertEquals(NOT_FOUND, service.getResponses().get(0).getExchange().getResponse().getStatus()); // most recent first
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
        exchange.setScript("exchange.response.status = 500; exchange.delay = 666");
        service.addResponse(exchange);

        StubServiceResult result = service.findMatch(request);

        assertTrue(result.matchFound());
        assertEquals(SERVER_ERROR, result.getResponse().getStatus());
        assertEquals(new Long(666), result.getDelay());
    }

    @Test
    public void testDuplicatePatternRemoved() {
        service.deleteResponses();

        exchange.getResponse().setStatus(OK);
        service.addResponse(new StubExchange(exchange)); // create copies

        exchange.getResponse().setStatus(NOT_FOUND);
        service.addResponse(new StubExchange(exchange));

        assertEquals(1, service.getResponses().size());
        assertEquals(NOT_FOUND, // ensure last stubbed request is kept
                service.getResponses().get(0).getExchange().getResponse().getStatus());
    }

}
