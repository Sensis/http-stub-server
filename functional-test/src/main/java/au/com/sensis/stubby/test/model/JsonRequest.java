package au.com.sensis.stubby.test.model;

public class JsonRequest extends JsonMessage {

    public String method;
    public String path;
    public JsonPairList params;
    
    public void setParam(String name, String value) {
        if (params == null) {
            params = new JsonPairList();
        }
        params.set(name, value);
    }

    public void addParam(String name, String value) {
        if (params == null) {
            params = new JsonPairList();
        }
        params.add(name, value);
    }
    
}
