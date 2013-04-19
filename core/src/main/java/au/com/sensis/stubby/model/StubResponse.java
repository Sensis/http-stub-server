package au.com.sensis.stubby.model;

public class StubResponse extends StubMessage {

    private Integer status;

    public StubResponse() { }
    
    public StubResponse(StubResponse other) { // copy constructor
        super(other);
        this.status = other.status;
    }
    
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
