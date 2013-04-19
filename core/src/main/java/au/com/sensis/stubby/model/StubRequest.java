package au.com.sensis.stubby.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class StubRequest extends StubMessage {

    private String method;
    private String path;
    private List<StubParam> params;

    public StubRequest() { }
    
    public StubRequest(StubRequest other) { // copy constructor
        super(other);
        this.method = other.method;
        this.path = other.path;
        if (other.params != null) {
            this.params = new ArrayList<StubParam>();
            for (StubParam param : other.params) {
                this.params.add(new StubParam(param));
            }
        }
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

    public List<StubParam> getParams() {
        return params;
    }

    public void setParams(List<StubParam> params) {
        this.params = params;
    }
    
    @JsonIgnore
    public String getParam(String name) { // get first, case sensitive lookup
        if (params != null) {
            for (StubParam param : params) {
                if (param.getName().equals(name)) {
                    return param.getValue();
                }
            }
        }
        return null; // not found
    }

    @JsonIgnore
    public List<String> getParams(String name) { // get all, case sensitive lookup
        List<String> result = new ArrayList<String>();
        if (params != null) {
            for (StubParam param : params) {
                if (param.getName().equals(name)) {
                    result.add(param.getValue());
                }
            }
        }
        return result; // empty list if not found
    }

}
