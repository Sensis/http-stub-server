package au.com.sensis.stubby.test.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class JsonPair {

    public String name;
    public String value;

    public JsonPair() { } // for Jackson

    public JsonPair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

}
