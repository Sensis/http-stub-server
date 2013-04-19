package au.com.sensis.stubby.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

import au.com.sensis.stubby.utils.JsonUtils;

public class GenericClientResponse {

    private HttpResponse response;
    private String body;

    public GenericClientResponse(HttpResponse response) {
        this.response = response;
        this.body = consumeBody(response); // always read body in to memory
    }

    private static String consumeBody(HttpResponse response) {
        try {
            if (response.getEntity() != null && response.getEntity().getContent() != null) {
                return EntityUtils.toString(response.getEntity(), "UTF-8"); // releases connection
            } else {
                return null; // no body
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading response body", e);
        }
    }

    public boolean hasBody() {
        return body != null;
    }

    public int getStatus() {
        return response.getStatusLine().getStatusCode();
    }
    
    public String getHeader(String name) {
        if (response.getFirstHeader(name) != null) {
            return response.getFirstHeader(name).getValue();
        } else {
            return null;
        }
    }
    
    public List<String> getHeaders(String name) {
        List<String> result = new ArrayList<String>();
        for (Header header : response.getHeaders(name)) {
            result.add(header.getValue());
        }
        return result;
    }

    public GenericClientResponse assertOk() {
        if (!isOk()) {
            throw new RuntimeException("Server returned " + getStatus());
        }
        return this;
    }
    
    public GenericClientResponse assertBody() {
        if (!hasBody()) {
            throw new RuntimeException("Response body expected");
        }
        return this;
    }

    public boolean isOk() {
        return getStatus() == HttpStatus.SC_OK;
    }

    public <T> T getJson(Class<T> type) {
        assertBody();
        return JsonUtils.deserialize(body, type);
    }

    public String getText() {
        assertBody();
        return body;
    }

}
