package au.com.sensis.stubby.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.sensis.stubby.js.Script;
import au.com.sensis.stubby.js.ScriptWorld;
import au.com.sensis.stubby.model.StubExchange;
import au.com.sensis.stubby.model.StubRequest;
import au.com.sensis.stubby.service.model.MatchResult;
import au.com.sensis.stubby.service.model.RequestPattern;
import au.com.sensis.stubby.service.model.StubServiceExchange;
import au.com.sensis.stubby.service.model.StubServiceResult;
import au.com.sensis.stubby.utils.JsonUtils;

public class StubService {

    private static final Logger LOGGER = Logger.getLogger(StubService.class);

    private LinkedList<StubServiceExchange> responses = new LinkedList<StubServiceExchange>();
    private LinkedList<StubRequest> requests = new LinkedList<StubRequest>();

    public synchronized void addResponse(StubExchange exchange) {
        LOGGER.debug("Adding response: " + JsonUtils.prettyPrint(exchange));
        StubServiceExchange internal = new StubServiceExchange(exchange);
        responses.remove(internal); // remove existing stubed request (ie, will never match anymore)
        responses.addFirst(internal); // ensure most recent match first   
    }

    public synchronized StubServiceResult findMatch(StubRequest request) {
        try {
            LOGGER.trace("Got request: " + JsonUtils.prettyPrint(request));
            requests.addFirst(request);
            List<MatchResult> attempts = new ArrayList<MatchResult>();
            for (StubServiceExchange response : responses) {
                MatchResult matchResult = response.matches(request);
                attempts.add(matchResult);
                if (matchResult.matches()) {
                    LOGGER.info("Matched: " + request.getPath() + "");
                    StubExchange exchange = response.getExchange();
                    if (exchange.getScript() != null) {
                        ScriptWorld world = new ScriptWorld(request, exchange); // creates deep copies of objects
                        new Script(exchange.getScript()).execute(world);
                        return new StubServiceResult(
                                attempts, world.getResponse(), world.getDelay());
                    } else {
                        return new StubServiceResult(
                                attempts, exchange.getResponse(), exchange.getDelay());
                    }
                }
            }
            LOGGER.info("Didn't match: " + request.getPath());
            this.notifyAll(); // inform any waiting threads that a new request has come in
            return new StubServiceResult(attempts); // no match (empty list)
        } catch (Exception e) {
            throw new RuntimeException("Error matching request", e);
        }
    }

    public synchronized StubServiceExchange getResponse(int index) throws NotFoundException {
        try {
            return responses.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new NotFoundException("Response does not exist: " + index);
        }
    }

    public synchronized List<StubServiceExchange> getResponses() {
        return responses;
    }

    public synchronized void deleteResponse(int index) throws NotFoundException {
        LOGGER.trace("Deleting response: " + index);
        try {
            responses.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Response does not exist: " + index);
        }
    }

    public synchronized void deleteResponses() {
        LOGGER.trace("Deleting all responses");
        responses.clear();
    }

    public synchronized StubRequest getRequest(int index) throws NotFoundException {
        try {
            return requests.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new NotFoundException("Response does not exist: " + index);
        }
    }

    public synchronized List<StubRequest> findRequests(StubRequest filter, long timeout) { // blocking call
        long remaining = timeout;
        while (remaining > 0) {
            List<StubRequest> result = findRequests(filter);
            if (result.isEmpty()) {
                try {
                    long start = System.currentTimeMillis();
                    this.wait(remaining); // wait for a request to come in, or time to expire
                    remaining -= System.currentTimeMillis() - start;
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted while waiting for request");
                }
            } else {
                return result;
            }
        }
        return Collections.emptyList();
    }

    public synchronized List<StubRequest> findRequests(StubRequest filter) {
        RequestPattern pattern = new RequestPattern(filter);
        List<StubRequest> result = new ArrayList<StubRequest>();
        for (StubRequest request : requests) {
            if (pattern.match(request).matches()) {
                result.add(request);
            }
        }
        return result;
    }

    public synchronized List<StubRequest> getRequests() {
        return requests;
    }

    public synchronized void deleteRequest(int index) throws NotFoundException {
        LOGGER.trace("Deleting request: " + index);
        try {
            requests.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw new NotFoundException("Request does not exist: " + index);
        }
    }

    public synchronized void deleteRequests() {
        LOGGER.trace("Deleting all requests");
        requests.clear();
    }

}
