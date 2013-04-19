package au.com.sensis.stubby.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.sensis.stubby.model.StubParam;
import au.com.sensis.stubby.model.StubRequest;

public class RequestFilterBuilder {
    
    private static final String METHOD_PARAM = "method";
    private static final String PATH_PARAM = "path";
    private static final Pattern PARAM_PATTERN = Pattern.compile("^param\\[(.+)\\]$");
    private static final Pattern HEADER_PATTERN = Pattern.compile("^header\\[(.+)\\]$");

    private StubRequest filter;
    
    public RequestFilterBuilder() {
        this.filter = new StubRequest();
        this.filter.setParams(new ArrayList<StubParam>());
        this.filter.setHeaders(new ArrayList<StubParam>());
    }
    
    public RequestFilterBuilder fromParams(List<StubParam> params) {
        for (StubParam param : params) {
            addParam(param.getName(), param.getValue());
        }
        return this;
    }

    private void addParam(String name, String value) {
        if (METHOD_PARAM.equals(name)) {
            filter.setMethod(value);
            return;
        }
        if (PATH_PARAM.equals(name)) {
            filter.setPath(value);
            return;
        }
        Matcher matcher = PARAM_PATTERN.matcher(name);
        if (matcher.matches()) {
            String param = matcher.group(1);
            filter.getParams().add(new StubParam(param, value));
            return;
        }
        matcher = HEADER_PATTERN.matcher(name);
        if (matcher.matches()) {
            String header = matcher.group(1);
            filter.getHeaders().add(new StubParam(header, value));
            return;
        }
    }
    
    public StubRequest getFilter() {
        return filter;
    }
    
}
