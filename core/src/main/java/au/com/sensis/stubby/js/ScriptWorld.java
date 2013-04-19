package au.com.sensis.stubby.js;

import au.com.sensis.stubby.model.StubExchange;
import au.com.sensis.stubby.model.StubRequest;
import au.com.sensis.stubby.model.StubResponse;

public class ScriptWorld { // the world as JavaScript sees it

    private StubRequest request;
    private StubResponse response;
    private Long delay;
    
    public ScriptWorld(StubRequest request, StubExchange response) { // copy everything so the script can't change the server state
        this.request = new StubRequest(request);
        this.response = new StubResponse(response.getResponse());
        this.delay = response.getDelay();
    }

    public StubRequest getRequest() {
        return request;
    }

    public StubResponse getResponse() { // allow this to be changed for current request
        return response;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {  // allow this to be changed for current request
        this.delay = delay;
    }
    
}
