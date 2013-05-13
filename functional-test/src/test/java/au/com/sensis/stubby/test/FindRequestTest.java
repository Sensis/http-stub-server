package au.com.sensis.stubby.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import au.com.sensis.stubby.model.StubRequest;
import org.apache.http.client.methods.HttpPost;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.stubby.test.model.JsonRequestList;
import au.com.sensis.stubby.test.support.TestBase;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
        JsonRequestList requests = client.findRequests("");
        assertEquals(6, requests.size());
        assertEquals("/test6", requests.get(0).path); // assert most recent first
        assertEquals("/test1", requests.get(5).path);
    }

    @Test
    public void testFindPath() {
        JsonRequestList requests = client.findRequests("path=/test1");
        assertEquals(1, requests.size());
        assertEquals("/test1", requests.get(0).path);
    }
    
    @Test
    public void testFindMethod() {
        JsonRequestList requests = client.findRequests("method=DELETE");
        assertEquals(1, requests.size());
        assertEquals("/test4", requests.get(0).path);
    }
    
    @Test
    public void testFindParam() {
        JsonRequestList requests = client.findRequests("param[foo]=bar");
        assertEquals(1, requests.size());
        assertEquals("/test2", requests.get(0).path);
    }
    
    @Test
    public void testFindParams() {
        JsonRequestList requests = client.findRequests("param[foo]=bar1&param[foo]=bar2");
        assertEquals(1, requests.size());
        assertEquals("/test3", requests.get(0).path);
    }
    
    @Test
    public void testFindHeader() {
        JsonRequestList requests = client.findRequests("header[X-FOO]=bar"); // test case insensitivity
        assertEquals(1, requests.size());
        assertEquals("/test5", requests.get(0).path);
    }
    
    @Test
    public void testFindHeaders() {
        JsonRequestList requests = client.findRequests("header[X-Foo]=bar1&header[X-Foo]=bar2");
        assertEquals(1, requests.size());
        assertEquals("/test6", requests.get(0).path);
    }

    @Test
    public void testWait_noResults() {
        client.reset();

        long started = now();
        JsonRequestList requests = client.findRequests("wait=2000");
        long ended = now();

        assertEquals(0, requests.size());
        assertTimeTaken(started, ended, 2000); // expected approx. 2 seconds
    }

    @Test
    public void testWait_zeroWait() {
        client.reset();

        long started = now();
        JsonRequestList requests = client.findRequests("wait=0");
        long ended = now();

        assertEquals(0, requests.size());
        assertTimeTaken(started, ended, 0); // expect result instantly
    }

    @Test
    public void testWait_withResults() {
        final AtomicReference<Exception> threadError = new AtomicReference<Exception>();

        client.reset();
        client.executeGet("/request1");

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(2000); // attempt to make 'find' execute after parent thread starts waiting
                    client.executeGet("/request2");
                } catch (Exception e) {
                    threadError.set(e);
                }
            }
        }).start();

        long started = now();
        JsonRequestList requests = client.findRequests("path=/request2&wait=5000");
        long ended = now();

        assertEquals(1, requests.size());
        assertEquals("/request2", requests.get(0).path);

        if (threadError.get() != null) {
            throw new RuntimeException("Thread threw an exception", threadError.get());
        }

        assertTimeTaken(started, ended, 2000); // expected approx. 2 seconds
    }

}
