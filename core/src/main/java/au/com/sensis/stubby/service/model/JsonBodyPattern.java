package au.com.sensis.stubby.service.model;

import java.util.List;
import java.util.Map;

import au.com.sensis.stubby.model.StubMessage;
import au.com.sensis.stubby.service.model.MatchField.FieldType;
import au.com.sensis.stubby.utils.HttpMessageUtils;
import au.com.sensis.stubby.utils.JsonUtils;

public class JsonBodyPattern extends BodyPattern {
    
    public static class MatchResult { // internal match result type
        
        private boolean match;
        private String path;
        private String reason;
        
        public MatchResult(boolean match, String path, String reason) {
            this.match = match;
            this.path = path;
            this.reason = reason;
        }

        public boolean isMatch() {
            return match;
        }

        public String getPath() {
            return path;
        }
        
        public String getReason() {
            return reason;
        }        
        
        public String message() {
            return String.format("%s (at '%s')", reason, path);
        }
        
    }

    private Object pattern;

    public JsonBodyPattern(Object pattern) {
        this.pattern = pattern;
    }
    
    public Object getPattern() {
        return pattern;
    }
    
    @Override
    public MatchField matches(StubMessage request) {
        String expected = JsonUtils.prettyPrint(pattern);
        String actual = JsonUtils.prettyPrint(request.getBody());
        MatchResult result = matchResult(request);
        MatchField field = new MatchField(FieldType.BODY, "body", expected);
        if (result.isMatch()) {
            return field.asMatch(actual);
        } else {
            return field.asMatchFailure(actual, result.message());
        }
    }

    /*
     * For each property that exists in the pattern, ensure a matching
     * property in the request body. All fields in pattern are assumed to
     * be regular expressions. All strings are converted to strings for matching.
     */
    private MatchResult matchResult(StubMessage request) {
        if (HttpMessageUtils.isJson(request)) { // require a JSON body
            return matchValue(pattern, HttpMessageUtils.bodyAsJson(request), ""); // root could be any type (eg, an array)
        } else {
            return matchFailure("Expected content type: application/json", ".");
        }
    }

    /*
     * For each property in the pattern object, a property in the request must exist with exactly the 
     * same name and it's value must match (see 'matchValue()' for value matching rules). Properties
     * in the request object that are not in the pattern are ignored.
     * 
     * An empty object pattern matches any request object.
     */
    private MatchResult matchObject(Map<String, Object> pattern, Map<String, Object> request, String path) {
        for (String key : pattern.keySet()) {
            MatchResult result = matchValue(pattern.get(key), request.get(key), path + "." + key);
            if (!result.isMatch()) {
                return result; // abort on first failure
            }
        }
        return matchSuccess(); // empty pattern matches any object
    }

    /*
     * Arrays does not have to match exactly, but the pattern order is important. 
     * For example, pattern [b,d] matches [a,b,c,d] because 'b' and 'd' exist and 'b' appears before 'd'.
     * (see 'matchValue()' for value matching rules)
     * 
     * An empty array pattern matches any request array.
     */
    private MatchResult matchArray(List<Object> pattern, List<Object> request, String path) {
        int r = 0; // current search position in request array
        for (int p = 0; p < pattern.size(); p++) {
            String patternPath = path + "[" + p + "]";
            while (r < request.size() && !matchValue(pattern.get(p), request.get(r), patternPath).isMatch()) {
                r++;
            }
            if (r == request.size()) { // reached end of request and no match
                return matchFailure("Matching array element not found", patternPath);
            }
        }
        return matchSuccess(); // empty pattern or all matched
    }

    /*
     * Matching rules:
     *  - A null value in a pattern only matches a null or missing request value
     *  
     *  - A string value in a pattern is treated as a regular expression and can match strings, 
     *    booleans and numbers in the request (they are first converted to strings). 
     *  
     *  - A number value in a pattern can only match numbers in the request and must match exactly.
     *  
     *  - A boolean value in a pattern can only match boolean in the request and must match exactly.
     *  
     *  - An array in a pattern can only match an array in the request. See 'matchArray()' for more detail.
     *    
     *  - An object in the pattern can only match an object in the request. See 'matchObject()' for more detail.
     */
    @SuppressWarnings("unchecked")
    private MatchResult matchValue(Object pattern, Object request, String path) { // TODO: add better debugging information
        if (pattern == null) {
            if (request == null) { 
                return matchSuccess(); // only match if both are null
            } else {
                return matchFailure("Expected null value", path);
            }
        } else if (pattern instanceof String) {
            if (request instanceof String // allow regexp to match any scalar value
                    || request instanceof Number
                    || request instanceof Boolean) {
                if (request.toString().matches(pattern.toString())) { // assume pattern is a regular expression
                    return matchSuccess();
                } else {
                    return matchFailure(String.format("Expected '%s' to match '%s'", request, pattern), path);
                }
            } else {
                return matchFailure("Scalar value (string, number or boolean) expected", path);
            }
        } else if (pattern instanceof Number) {
            if (request instanceof Number) {
                if (pattern.equals(request)) {
                    return matchSuccess();
                } else {
                    return matchFailure(String.format("Expected %s, was %s", pattern, request), path);
                }
            } else {
                return matchFailure("Number expected", path);
            }
        } else if (pattern instanceof Boolean) {
            if (request instanceof Boolean) {
                if (pattern.equals(request)) {
                    return matchSuccess();
                } else {
                    return matchFailure(String.format("Expected %s, was %s", pattern, request), path);
                }
            } else {
                return matchFailure("Boolean expected", path);
            }
        } else if (pattern instanceof List) {
            if (request instanceof List) {
                return matchArray((List<Object>) pattern, (List<Object>) request, path);
            } else {
                return matchFailure("Array expected", path);
            }
        } else if (pattern instanceof Map) {
            if (request instanceof Map) {
                return matchObject((Map<String, Object>) pattern, (Map<String, Object>) request, path); // recursively match objects
            } else {
                return matchFailure("Object expected", path);
            }
        } else {
            throw new RuntimeException("Unexpected type in pattern: " + pattern.getClass());
        }
    }
        
    private MatchResult matchFailure(String reason, String path) {
        return new MatchResult(false, path, reason);
    }
    
    private MatchResult matchSuccess() {
        return new MatchResult(true, null, null); // message & path ignored for success
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof JsonBodyPattern)
                && ((JsonBodyPattern) obj).pattern.equals(pattern);
    }
    
    @Override
    public int hashCode() {
        return pattern.hashCode();
    }

}
