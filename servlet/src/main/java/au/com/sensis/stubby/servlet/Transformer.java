package au.com.sensis.stubby.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import au.com.sensis.stubby.model.StubParam;
import au.com.sensis.stubby.model.StubRequest;
import au.com.sensis.stubby.model.StubResponse;
import au.com.sensis.stubby.utils.JsonUtils;

/*
 * Transform between stubby & Servlet HTTP structures
 */
public class Transformer {

    @SuppressWarnings("unchecked")
    public static List<StubParam> fromServletHeaders(HttpServletRequest request) {
        List<StubParam> result = new  ArrayList<StubParam>();
        Enumeration<String> headers = (Enumeration<String>)request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = headers.nextElement();
            Enumeration<String> headerValues = (Enumeration<String>)request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                result.add(new StubParam(headerName, headerValues.nextElement()));
            }
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public static List<StubParam> fromServletParams(HttpServletRequest request) {
        List<StubParam> result = new ArrayList<StubParam>();
        Enumeration<String> params = (Enumeration<String>)request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            for (String value : request.getParameterValues(paramName)) {
                result.add(new StubParam(paramName, value));
            }
        }
        return result;
    }

    public static StubRequest fromServletRequest(HttpServletRequest request) {
        StubRequest result = new StubRequest();
        try {
            result.setPath(new URI(request.getRequestURL().toString()).getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String method = request.getMethod().toUpperCase(); // method should always be upper-case
        result.setMethod(method); 
        if (!method.equals("GET")) { // GET requests don't (shouldn't) have a body or content type
            try {
                result.setBody(IOUtils.toString(request.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        result.setParams(fromServletParams(request));
        result.setHeaders(fromServletHeaders(request));
        return result;
    }

    public static void populateServletResponse(StubResponse message, HttpServletResponse response) throws IOException {
        for (StubParam header : message.getHeaders()) {
            response.setHeader(header.getName(), header.getValue());
        }
        response.setStatus(message.getStatus());
        if (message.getBody() instanceof String) {
            IOUtils.write(message.getBody().toString(), response.getOutputStream());
        } else {
            JsonUtils.serialize(response.getOutputStream(), message.getBody()); // assume deserialised JSON (ie, a Map) 
        }
    }

}
