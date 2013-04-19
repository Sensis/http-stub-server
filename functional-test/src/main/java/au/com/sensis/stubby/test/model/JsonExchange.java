package au.com.sensis.stubby.test.model;

import org.codehaus.jackson.annotate.JsonIgnore;

public class JsonExchange {

    public JsonRequest request;
    public JsonResponse response;
    public Long delay;
    public String script;
    
    @JsonIgnore
    public JsonRequest request() {
        if (request == null) {
            request = new JsonRequest();
        }
        return request;
    }
    
    @JsonIgnore
    public JsonResponse response() {
        if (response == null) {
            response = new JsonResponse();
        }
        return response;
    }
    
}
