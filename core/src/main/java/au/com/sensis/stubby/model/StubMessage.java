package au.com.sensis.stubby.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import au.com.sensis.stubby.utils.DeepCopyUtils;

public abstract class StubMessage {

    private List<StubParam> headers;
    private Object body;

    protected StubMessage() { }
    
    protected StubMessage(StubMessage other) { // copy constructor
        this.body = (other.body != null) ? DeepCopyUtils.deepCopy(other.body) : null;
        if (other.headers != null) {
            this.headers = new ArrayList<StubParam>();
            for (StubParam param : other.headers) {
                headers.add(new StubParam(param));
            }
        }
    }
    
    public List<StubParam> getHeaders() {
        return headers;
    }

    public void setHeaders(List<StubParam> headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
    
    @JsonIgnore
    public String getHeader(String name) { // get first, case insensitive lookup
        if (headers != null) {
            for (StubParam header : headers) {
                if (header.getName().equalsIgnoreCase(name)) {
                    return header.getValue();
                }
            }
        }
        return null; // not found
    }
    
    @JsonIgnore
    public void removeHeader(String name) { // case insensitive lookup
        if (headers != null) {
            Iterator<StubParam> iter = headers.iterator();
            while (iter.hasNext()) {
                if (iter.next().getName().equalsIgnoreCase(name)) {
                    iter.remove();
                }
            }
        }
    }
    
    @JsonIgnore
    public void setHeader(String name, String value) { // replace value, case insensitive lookup
        removeHeader(name);
        if (headers == null) {
            headers = new ArrayList<StubParam>();
        }
        headers.add(new StubParam(name, value));        
    }
    
    @JsonIgnore
    public List<String> getHeaders(String name) { // get all, case insensitive lookup
        List<String> result = new ArrayList<String>();
        if (headers != null) {
            for (StubParam header : headers) {
                if (header.getName().equalsIgnoreCase(name)) {
                    result.add(header.getValue());
                }
            }
        }
        return result; // empty list if not found
    }
    
}
