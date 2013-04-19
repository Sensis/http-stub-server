package au.com.sensis.stubby.service;

import java.io.InputStream;
import java.io.OutputStream;

import au.com.sensis.stubby.model.StubExchange;
import au.com.sensis.stubby.model.StubRequest;
import au.com.sensis.stubby.utils.JsonUtils;

public class JsonServiceInterface { // interface using serialized JSON strings/streams

    private StubService service;

    public JsonServiceInterface(StubService service) {
        this.service = service;
    }
    
    public void addResponse(String exchange) {
        service.addResponse(JsonUtils.deserialize(exchange, StubExchange.class));
    }
    
    public void addResponse(InputStream stream) {
        service.addResponse(JsonUtils.deserialize(stream, StubExchange.class));
    }

    public String getResponse(int index) {
        return JsonUtils.serialize(service.getResponse(index));
    }
    
    public void getResponse(OutputStream stream, int index) {
        JsonUtils.serialize(stream, service.getResponse(index));
    }

    public String getResponses() {
        return JsonUtils.serialize(service.getResponses());
    }
    
    public void getResponses(OutputStream stream) {
        JsonUtils.serialize(stream, service.getResponses());
    }

    public void deleteResponse(int index) throws NotFoundException {
        service.deleteResponse(index);
    }

    public void deleteResponses() {
        service.deleteResponses();
    }

    public String getRequest(int index) throws NotFoundException {
        return JsonUtils.serialize(service.getRequest(index));
    }
    
    public void getRequest(OutputStream stream, int index) throws NotFoundException {
        JsonUtils.serialize(stream, service.getRequest(index));
    }

    public String getRequests() {
        return JsonUtils.serialize(service.getRequests());
    }
    
    public void getRequests(OutputStream stream) {
        JsonUtils.serialize(stream, service.getRequests());
    }
    
    public String findRequest(StubRequest filter) throws NotFoundException {
        return JsonUtils.serialize(service.findRequests(filter));
    }
    
    public void findRequest(OutputStream stream, StubRequest filter) throws NotFoundException {
        JsonUtils.serialize(stream, service.findRequests(filter));
    }

    public void deleteRequest(int index) throws NotFoundException {
        service.deleteRequest(index);
    }

    public void deleteRequests() {
        service.deleteRequests();
    }

}
