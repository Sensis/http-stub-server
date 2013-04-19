package au.com.sensis.stubby.service.model;

import java.util.regex.Pattern;

import au.com.sensis.stubby.model.StubMessage;
import au.com.sensis.stubby.service.model.MatchField.FieldType;
import au.com.sensis.stubby.utils.HttpMessageUtils;

public class TextBodyPattern extends BodyPattern {

    private Pattern pattern;
    
    public TextBodyPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public MatchField matches(StubMessage request) {
        String actual = HttpMessageUtils.bodyAsText(request);
        MatchField field = new MatchField(FieldType.BODY, "body", pattern.pattern());
        if (HttpMessageUtils.isText(request)) { // require text body
            if (pattern.matcher(actual).matches()) { // match pattern against entire body
                return field.asMatch(actual);
            } else {
                return field.asMatchFailure(actual);
            }
        } else {
            return field.asMatchFailure(actual, "Expected content type: text/*"); 
        }
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof TextBodyPattern)
                && ((TextBodyPattern)obj).pattern.pattern().equals(pattern.pattern());
    }
    
    @Override
    public int hashCode() {
        return pattern.pattern().hashCode();
    }

    public Pattern getPattern() {
        return pattern;
    }

}
