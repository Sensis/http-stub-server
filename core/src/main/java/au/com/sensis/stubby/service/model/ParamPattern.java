package au.com.sensis.stubby.service.model;

import java.util.regex.Pattern;

public class ParamPattern {
    
    private String name;
    private Pattern pattern;
    
    public ParamPattern(String name, Pattern pattern) {
        this.name = name;
        this.pattern = pattern;
    }
    
    public ParamPattern(ParamPattern other) { // copy constructor
        this.name = other.name;
        this.pattern = other.pattern;
    }

    public String getName() {
        return name;
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return name + " =~ m/" + pattern.pattern() + "/";
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ParamPattern)
                && ((ParamPattern)obj).name.equals(name)
                && ((ParamPattern)obj).pattern.pattern().equals(pattern.pattern());
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + name.hashCode();
        result = 31 * result + pattern.pattern().hashCode();
        return result;
    }

}
