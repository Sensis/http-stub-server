package au.com.sensis.stubby.test;

import org.apache.http.entity.ContentType;

import au.com.sensis.stubby.test.model.JsonExchange;
import au.com.sensis.stubby.test.model.JsonRequest;
import au.com.sensis.stubby.test.model.JsonRequestList;
import au.com.sensis.stubby.test.model.JsonStubbedExchange;
import au.com.sensis.stubby.test.model.JsonStubbedExchangeList;
import au.com.sensis.stubby.utils.JsonUtils;

public class Client extends GenericClient {

    public Client(String baseUri) {
        super(baseUri);
    }

    public void postMessage(JsonExchange message) {
        executePost("/_control/responses", JsonUtils.serialize(message), ContentType.APPLICATION_JSON).assertOk();
    }

    public void postMessage(String message) {
        executePost("/_control/responses", message, ContentType.APPLICATION_JSON).assertOk();
    }

    public JsonStubbedExchangeList getResponses() {
        return executeGet("/_control/responses").assertOk().getJson(JsonStubbedExchangeList.class);
    }

    public JsonRequestList getRequests() {
        return executeGet("/_control/requests").assertOk().getJson(JsonRequestList.class);
    }

    public JsonRequestList findRequests(String query) {
        return executeGet("/_control/requests?" + query).assertOk().getJson(JsonRequestList.class);
    }
    
    public JsonStubbedExchange getResponse(int index) {
        return executeGet("/_control/responses/" + index).assertOk().getJson(JsonStubbedExchange.class);
    }

    public JsonRequest getRequest(int index) {
        return executeGet("/_control/requests/" + index).assertOk().getJson(JsonRequest.class);
    }

    public void deleteResponses() {
        executeDelete("/_control/responses").assertOk();
    }

    public void deleteRequests() {
        executeDelete("/_control/requests").assertOk();
    }

    public void reset() {
        deleteResponses();
        deleteRequests();
    }

}