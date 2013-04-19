package au.com.sensis.stubby.model;

public class StubParam {

    private String name;
    private String value;
    
    public StubParam() { }

    public StubParam(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public StubParam(StubParam other) { // copy constructor
        this.name = other.name;
        this.value = other.value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return name + "=" + value;
    }

}
