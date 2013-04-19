package au.com.sensis.stubby.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;

import au.com.sensis.stubby.service.JsonServiceInterface;
import au.com.sensis.stubby.service.StubService;
import au.com.sensis.stubby.utils.JsonUtils;

/*
 * Base servlet class that provides access to a 'StubService' instance
 */
@SuppressWarnings("serial")
public abstract class AbstractStubServlet extends HttpServlet {

    public static final String SERVICE_CONTEXT_KEY = "stubby.StubService";

    private ObjectMapper mapper = JsonUtils.defaultMapper();
    
    protected StubService service() {
        StubService service = (StubService) getServletContext().getAttribute(SERVICE_CONTEXT_KEY);
        if (service != null) {
            return service;
        } else {
            throw new IllegalStateException("Service not created"); // ensure StubContextListener was invoked first
        }
    }
    
    protected JsonServiceInterface jsonService() {
        return new JsonServiceInterface(service());
    }
    
    protected void returnOk(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.SC_OK); 
        response.getOutputStream().close(); // no body
    }
    
    protected void returnString(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/plain");
        Writer writer = response.getWriter();
        writer.write(message);
        writer.close();
    }
    
    protected void returnNotFound(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.SC_NOT_FOUND);
        returnString(response, message);
    }
    
    protected void returnError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        returnString(response, message);
    }
    
    protected void returnJson(HttpServletResponse response, Object model) throws IOException {
        response.setStatus(HttpStatus.SC_OK);
        response.setHeader("Content-Type", "application/json");
        OutputStream stream = response.getOutputStream();
        mapper.writeValue(stream, model);
        stream.close();
    }
    
    /*
     * Get everything after the servlet path and convert to string (eg, '/_control/requests/99' => 99)
     */
    protected int getId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1); // trim slash
        }
        return Integer.parseInt(pathInfo);
    }
    
//    protected <T> T parseJsonBody(HttpServletRequest request, Class<T> bodyClass) throws IOException {
//        String contentType = request.getHeader("Content-Type");
//        if (contentType != null && contentType.startsWith("application/json")) {
//            InputStream stream = request.getInputStream();
//            try {
//                return mapper.readValue(stream, bodyClass);
//            } finally {
//                stream.close();
//            }
//        } else {
//            throw new RuntimeException("Unexpected content type");
//        }
//    }

}
