package au.com.sensis.stubby.test;

import static org.junit.Assert.assertEquals;

import org.apache.http.client.methods.HttpPost;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.stubby.test.model.JsonRequestList;
import au.com.sensis.stubby.test.support.TestBase;

public class FindRequestTest extends TestBase {
    
    @Before
    public void makeRequests() {
        client.executeGet("/test1");
        client.executeGet("/test2?foo=bar");
        client.executeGet("/test3?foo=bar1&foo=bar2");
        client.executeDelete("/test4");
        
        HttpPost test5 = new HttpPost(client.makeUri("/test5"));
        test5.addHeader("X-Foo", "bar");
        client.execute(test5);
        
        HttpPost test6 = new HttpPost(client.makeUri("/test6"));
        test6.addHeader("X-Foo", "bar1");
        test6.addHeader("X-Foo", "bar2");
        client.execute(test6);
    }
    
    @Test
    public void testFindAll() {
        JsonRequestList requests = client.findReqests("");
        assertEquals(6, requests.size());
        assertEquals("/test6", requests.get(0).path); // assert most recent first
        assertEquals("/test1", requests.get(5).path);
    }

    @Test
    public void testFindPath() {
        JsonRequestList requests = client.findReqests("path=/test1");
        assertEquals(1, requests.size());
        assertEquals("/test1", requests.get(0).path);
    }
    
    @Test
    public void testFindMethod() {
        JsonRequestList requests = client.findReqests("method=DELETE");
        assertEquals(1, requests.size());
        assertEquals("/test4", requests.get(0).path);
    }
    
    @Test
    public void testFindParam() {
        JsonRequestList requests = client.findReqests("param[foo]=bar");
        assertEquals(1, requests.size());
        assertEquals("/test2", requests.get(0).path);
    }
    
    @Test
    public void testFindParams() {
        JsonRequestList requests = client.findReqests("param[foo]=bar1&param[foo]=bar2");
        assertEquals(1, requests.size());
        assertEquals("/test3", requests.get(0).path);
    }
    
    @Test
    public void testFindHeader() {
        JsonRequestList requests = client.findReqests("header[X-FOO]=bar"); // test case insensitivity
        assertEquals(1, requests.size());
        assertEquals("/test5", requests.get(0).path);
    }
    
    @Test
    public void testFindHeaders() {
        JsonRequestList requests = client.findReqests("header[X-Foo]=bar1&header[X-Foo]=bar2");
        assertEquals(1, requests.size());
        assertEquals("/test6", requests.get(0).path);
    }
    
}
