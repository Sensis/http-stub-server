package au.com.sensis.stubby.standalone;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;

import au.com.sensis.stubby.model.StubParam;
import au.com.sensis.stubby.model.StubRequest;
import au.com.sensis.stubby.service.JsonServiceInterface;
import au.com.sensis.stubby.service.NotFoundException;
import au.com.sensis.stubby.service.StubService;
import au.com.sensis.stubby.service.model.StubServiceResult;
import au.com.sensis.stubby.utils.JsonUtils;
import au.com.sensis.stubby.utils.RequestFilterBuilder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ServerHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class);

    private StubService service;
    private JsonServiceInterface jsonService;
    private Thread shutdownHook; // if set, use for graceful shutdown

    public ServerHandler() {
        this.service = new StubService();
        this.jsonService = new JsonServiceInterface(service);
    }

    public void setShutdownHook(Thread shutdownHook) {
        this.shutdownHook = shutdownHook;
    }

    public void handle(HttpExchange exchange) {
        long start = System.currentTimeMillis();
        try {
            // force clients to close connection to work around issue with Keep-Alive in HttpServer (Java 7)
            // see: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=8009548
            // TODO: should probably use another HTTP server (not the 'com.sun' one)
            exchange.getResponseHeaders().set("Connection", "close");

            String path = exchange.getRequestURI().getPath();
            if (path.startsWith("/_control/")) {
                handleControl(exchange);
            } else {
                handleMatch(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
            try {
                returnError(exchange, e.getMessage());
            } catch (IOException ex) {
                LOGGER.error(ex);
                ex.printStackTrace();
            }
        } finally {
            exchange.close();
        }
        LOGGER.trace("Server handle processing time(ms): " + (System.currentTimeMillis() - start));
    }

    private void handleMatch(HttpExchange exchange) throws Exception {
        StubServiceResult result = service.findMatch(Transformer.fromExchange(exchange));
        if (result.matchFound()) {
            Long delay = result.getDelay();
            if (delay != null && delay > 0) {
                LOGGER.info("Delayed request, sleeping for " + delay + " ms...");
                Thread.sleep(delay);
            }
            Transformer.populateExchange(result.getResponse(), exchange);
        } else {
            returnNotFound(exchange, "No stubbed method matched request");
        }
    }

    private void handleControl(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Pattern pattern = Pattern.compile("^/_control/(.+?)(/(\\d+))?$");
        Matcher matcher = pattern.matcher(path);
        if (matcher.matches()) {
            String object = matcher.group(1);
            String indexStr = matcher.group(3);
            if (indexStr != null) {
                int index = Integer.parseInt(indexStr);
                if (object.equals("requests")) {
                    handleRequest(exchange, index);
                } else if (object.equals("responses")) {
                    handleResponse(exchange, index);
                } else {
                    throw new RuntimeException("Unknown object: " + object);
                }
            } else {
                if (object.equals("requests")) {
                    handleRequests(exchange);
                } else if (object.equals("responses")) {
                    handleResponses(exchange);
                } else if (object.equals("shutdown")) {
                    handleShutdown(exchange);
                } else {
                    throw new RuntimeException("Unknown object: " + object);
                }
            }
        }
    }

    private void returnOk(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpStatus.SC_OK, -1); // no body
        exchange.getResponseBody().close();
    }

    private void returnNotFound(HttpExchange exchange, String message) throws IOException {
        exchange.sendResponseHeaders(HttpStatus.SC_NOT_FOUND, 0); // unknown body length
        exchange.getResponseBody().write(message.getBytes());
        exchange.getResponseBody().close();
    }

    private void returnError(HttpExchange exchange, String message) throws IOException {
        exchange.sendResponseHeaders(HttpStatus.SC_INTERNAL_SERVER_ERROR, 0); // unknown body length
        exchange.getResponseBody().write(message.getBytes());
        exchange.getResponseBody().close();
    }

    private void returnJson(HttpExchange exchange, Object model) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(HttpStatus.SC_OK, 0); // unknown body length
        JsonUtils.serialize(exchange.getResponseBody(), model);
        exchange.getResponseBody().close();
    }

    private void handleResponses(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("POST")) {
            jsonService.addResponse(exchange.getRequestBody());
            returnOk(exchange);
        } else if (method.equals("DELETE")) {
            service.deleteResponses();
            returnOk(exchange);
        } else if (method.equals("GET")) {
            returnJson(exchange, service.getResponses());
        } else {
            throw new RuntimeException("Unsupported method: " + method);
        }
    }
    
    private void handleResponse(HttpExchange exchange, int index) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            try {
                returnJson(exchange, service.getResponse(index));
            } catch (NotFoundException e) {
                returnNotFound(exchange, e.getMessage());
            }
        } else {
            throw new RuntimeException("Unsupported method: " + method);
        }
    }

    private void handleRequests(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("DELETE")) {
            service.deleteRequests();
            returnOk(exchange);
        } else if (method.equals("GET")) {
            returnJson(exchange, service.findRequests(createFilter(exchange)));
        } else {
            throw new RuntimeException("Unsupported method: " + method);
        }
    }

    private void handleRequest(HttpExchange exchange, int index) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            try {
                returnJson(exchange, service.getRequest(index));
            } catch (NotFoundException e) {
                returnNotFound(exchange, e.getMessage());
            }
        } else {
            throw new RuntimeException("Unsupported method: " + method);
        }
    }
    
    private StubRequest createFilter(HttpExchange exchange) {
        List<StubParam> params = Transformer.fromExchangeParams(exchange);
        return new RequestFilterBuilder().fromParams(params).getFilter();
    }
    
    private void handleShutdown(HttpExchange exchange) throws IOException {
        if (shutdownHook != null) {
            LOGGER.info("Received shutdown request, attempting to shutdown gracefully...");
            returnOk(exchange);
            shutdownHook.start(); // attempt graceful shutdown
        } else {
            LOGGER.error("Received shutdown request, but don't know how to shutdown gracefully! (ignoring)");
            returnError(exchange, "Graceful shutdown not supported");
        }
    }

}
