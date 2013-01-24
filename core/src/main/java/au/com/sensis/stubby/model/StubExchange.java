package au.com.sensis.stubby.model;

public class StubExchange {

    private StubRequest request;
    private StubResponse response;
    private Long delay;
    private String script;
    
    public StubExchange() { }
    
    public StubExchange(StubExchange other) { // copy constructor
        this.request = new StubRequest(other.request);
        this.response = new StubResponse(other.response);
        this.delay = other.delay;
        this.script = other.script;
    }

    public StubRequest getRequest() {
        return request;
    }

    public void setRequest(StubRequest request) {
        this.request = request;
    }

    public StubResponse getResponse() {
        return response;
    }

    public void setResponse(StubResponse response) {
        this.response = response;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

}
