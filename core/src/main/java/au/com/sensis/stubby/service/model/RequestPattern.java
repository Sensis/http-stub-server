package au.com.sensis.stubby.service.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.stubby.model.StubMessage;
import au.com.sensis.stubby.model.StubParam;
import au.com.sensis.stubby.model.StubRequest;
import au.com.sensis.stubby.service.model.MatchField.FieldType;
import au.com.sensis.stubby.utils.EqualsUtils;

public class RequestPattern {

    public static final Pattern DEFAULT_PATTERN = Pattern.compile(".*");

    private Pattern method;
    private Pattern path;
    private Set<ParamPattern> params;
    private Set<ParamPattern> headers;
    private BodyPattern body;

    public RequestPattern(StubRequest request) {
        this.method = toPattern(request.getMethod());
        this.path = toPattern(request.getPath());
        this.params = toPattern(request.getParams());
        this.headers = toPattern(request.getHeaders());
        this.body = toBodyPattern(request.getBody());
    }
    
    private Pattern toPattern(String value) {
        return (value != null) ? Pattern.compile(value) : DEFAULT_PATTERN;
    }
    
    private Set<ParamPattern> toPattern(List<StubParam> params) {
        Set<ParamPattern> pattern = new HashSet<ParamPattern>();
        if (params != null) {
            for (StubParam param : params) {
                pattern.add(new ParamPattern(param.getName(), toPattern(param.getValue())));
            }
        }
        return pattern;
    }
    
    private BodyPattern toBodyPattern(Object object) {
        if (object != null) {
            if (object instanceof String) {
                return new TextBodyPattern(object.toString());
            } else if (object instanceof Map
                    || object instanceof List) {
                return new JsonBodyPattern(object);
            } else {
                throw new RuntimeException("Unexpected body type: " + object);
            }
        } else {
            return null; // don't match body
        }
    }

    public MatchResult match(StubRequest message) {
        MatchResult result = new MatchResult();

        MatchField methodField = new MatchField(MatchField.FieldType.METHOD, "method", method);
        if (method != null) {
            if (method.matcher(message.getMethod()).matches()) {
                result.add(methodField.asMatch(message.getMethod()));
            } else {
                result.add(methodField.asMatchFailure(message.getMethod()));
            }
        }

        MatchField pathField = new MatchField(FieldType.PATH, "path", path);
        if (path.matcher(message.getPath()).matches()) {
            result.add(pathField.asMatch(message.getPath()));
        } else {
            result.add(pathField.asMatchFailure(message.getPath()));
        }

        for (ParamPattern paramPattern : params) {
            result.add(matchParam(message, paramPattern));
        }

        for (ParamPattern headerPattern : headers) {
            result.add(matchHeader(message, headerPattern));
        }

        if (body != null) {
            if (message.getBody() != null) {
                result.add(body.matches(message));
            } else {
                result.add(new MatchField(FieldType.BODY, "body", "<pattern>").asNotFound());
            }
        }

        return result;
    }

    private MatchField matchParam(StubRequest message, ParamPattern pattern) {
        MatchField field = new MatchField(FieldType.QUERY_PARAM, pattern.getName(), pattern.getPattern());
        List<String> values = message.getParams(pattern.getName());
        if (!values.isEmpty()) {
            for (String value : values) {
                if (pattern.getPattern().matcher(value).matches()) {
                    return field.asMatch(value);
                }
            }
            return field.asMatchFailure(values.size() > 1 ? values : values.get(0)); // don't wrap in array if only single value
        } else {
            return field.asNotFound();
        }
    }

    private MatchField matchHeader(StubMessage message, ParamPattern pattern) {
        MatchField field = new MatchField(FieldType.HEADER, pattern.getName(), pattern.getPattern());
        List<String> values = message.getHeaders(pattern.getName()); // case insensitive lookup
        if (!values.isEmpty()) {
            for (String value : values) {
                if (pattern.getPattern().matcher(value).matches()) {
                    return field.asMatch(value);
                }
            }
            return field.asMatchFailure(values.size() > 1 ? values : values.get(0)); // don't wrap in array if only single value
        } else {
            return field.asNotFound();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((method == null) ? 0 : method.pattern().hashCode());
        result = prime * result + ((path == null) ? 0 : path.pattern().hashCode());
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        result = prime * result + ((headers == null) ? 0 : headers.hashCode());
        result = prime * result + ((params == null) ? 0 : params.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof RequestPattern)
                && ((RequestPattern) obj).method.pattern().equals(method.pattern())
                && ((RequestPattern) obj).path.pattern().equals(path.pattern())
                && ((RequestPattern) obj).params.equals(params)
                && ((RequestPattern) obj).headers.equals(headers)
                && EqualsUtils.safeEquals(((RequestPattern) obj).body, body);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Pattern getMethod() {
        return method;
    }

    public Pattern getPath() {
        return path;
    }

    public BodyPattern getBody() {
        return body;
    }

    public Set<ParamPattern> getParams() {
        return params;
    }

    public Set<ParamPattern> getHeaders() {
        return headers;
    }

}
