package au.com.sensis.stubby.test.model;

public abstract class JsonMessage {

    public JsonPairList headers;
    public Object body;
    
    public void setHeader(String name, String value) {
        if (headers == null) {
            headers = new JsonPairList();
        }
        headers.setIgnoreCase(name, value); // header names are case insensitive
    }

    public void addHeader(String name, String value) {
        if (headers == null) {
            headers = new JsonPairList();
        }
        headers.add(name, value);
    }
    
}
