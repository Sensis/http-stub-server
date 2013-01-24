package au.com.sensis.stubby.http;

import java.util.List;

import au.com.sensis.stubby.FlatParam;

public class HttpRequest extends HttpMessage {

    private String method; // always upper-case
    private String path;
    private HttpParamSet params;

    public HttpRequest() {
        this.params = new HttpParamSet(false); // case-sensitive map
    }
    
    public HttpRequest(HttpRequest other) { // copy constructor
        super(other);
        this.method = other.method;
        this.path = other.path;
        this.params = new HttpParamSet(other.params);
    }

    public void setQueryParams(List<FlatParam> flattened) {
        params.addAll(flattened);
    }

    public List<FlatParam> getParams() {
        return params.flatten();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

//    @JsonIgnore
//    public Map<String, HttpParam> getParams() {
//        return params.getMap();
//    }

    public void setParams(HttpParamSet params) {
        this.params = params;
    }
    
}
