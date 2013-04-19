package au.com.sensis.stubby.test;

import au.com.sensis.stubby.test.model.JsonExchange;

public class MessageBuilder {

    private Client client;
    private JsonExchange exchange;

    public MessageBuilder(Client client) {
        this.client = client;
        this.exchange = new JsonExchange();
    }
 
    public MessageBuilder setDelay(Long delay) {
        exchange.delay = delay;
        return this;
    }
    
    public MessageBuilder setScript(String script) {
        exchange.script = script;
        return this;
    }
    
    public MessageBuilder setRequestMethod(String method) {
        exchange.request().method = method;
        return this;
    }

    public MessageBuilder setRequestPath(String path) {
        exchange.request().path = path;
        return this;
    }

    public MessageBuilder setResponseStatus(Integer status) {
        exchange.response().status = status;
        return this;
    }

    public MessageBuilder setResponseBody(Object body) {
        exchange.response().body = body;
        return this;
    }
    
    public MessageBuilder setRequestBody(Object body) {
        exchange.request().body = body;
        return this;
    }

    public MessageBuilder addRequestParam(String name, String value) {
        exchange.request().addParam(name, value);
        return this;
    }

    public MessageBuilder setRequestParam(String name, String value) {
        exchange.request().setParam(name, value);
        return this;
    }

    public MessageBuilder setRequestHeader(String name, String value) {
        exchange.request().setHeader(name, value);
        return this;
    }

    public MessageBuilder addRequestHeader(String name, String value) {
        exchange.request().addHeader(name, value);
        return this;
    }

    public MessageBuilder setResponseHeader(String name, String value) {
        exchange.response().setHeader(name, value);
        return this;
    }

    public MessageBuilder addResponseHeader(String name, String value) {
        exchange.response().addHeader(name, value);
        return this;
    }

    public MessageBuilder stub() {
        client.postMessage(exchange); 
        return this;
    }

}
