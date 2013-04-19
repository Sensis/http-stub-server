package au.com.sensis.stubby.test;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.Test;

import au.com.sensis.stubby.test.support.TestBase;

public class MatchingTest extends TestBase {
    
    @Test
    public void emptyServer() {
        assertNotFound(client.executeGet("/foo/bar"));
    }
    
    @Test
    public void matchPattern() {
        builder().setRequestPath("/foo/.*").setResponseStatus(200).stub();
        assertOk(client.executeGet("/foo/bar"));
    }
    
    @Test
    public void notMatchPattern() {
        builder().setRequestPath("/foo/.*").setResponseStatus(200).stub();
        assertNotFound(client.executeGet("/wrong/bar"));
    }

    @Test
    public void matchQueryParameter() {
        builder().setRequestPath("/.*").setResponseStatus(200).addRequestParam("foo", "b..").stub();  // ensure regular expressions supported
        assertOk(client.executeGet("/test?foo=bar&FOO=bar")); // ensure ignores extra parameters, and case sensitive
    }

    @Test
    public void matchBlankQueryParameter() {
        builder().setRequestPath("/.*").setResponseStatus(200).addRequestParam("foo", ".*").stub();
        assertOk(client.executeGet("/test?foo=")); // should match empty parameter
        assertNotFound(client.executeGet("/test")); // should not match if parameter not given
    }

    @Test
    public void matchMissingParameter() {
        builder().setRequestPath("/.*").setResponseStatus(200).addRequestParam("foo", "b..").stub();
        assertNotFound(client.executeGet("/test?blah=wrong"));
    }
    
    @Test
    public void matchHeader() {
        builder().setRequestPath("/.*").setResponseStatus(200).addRequestHeader("X-Foo", "b..").stub(); // ensure regular expressions supported
        
        HttpGet request = new HttpGet(makeUri("/test"));
        request.setHeader("x-foo", "bar"); // assert case-insensitive
        request.setHeader("ignore", "blah");
        
        assertOk(client.execute(request));
    }
    
    @Test
    public void matchMissingHeader() {
        builder().setRequestPath("/.*").setResponseStatus(200).addRequestHeader("X-Foo", "b..").stub();
        
        HttpGet request = new HttpGet(makeUri("/test"));
        request.setHeader("ignore", "blah");
        
        assertNotFound(client.execute(request));
    }
    
    @Test
    public void matchMethod() {
        builder().setRequestPath("/.*").setRequestMethod("P.+").setResponseStatus(200).stub();
                
        assertOk(client.execute(new HttpPut(makeUri("/test"))));  // ensure regular expressions supported
        assertOk(client.execute(new HttpPost(makeUri("/test"))));
    }
    
}