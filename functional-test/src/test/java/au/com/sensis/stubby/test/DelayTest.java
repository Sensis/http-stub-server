package au.com.sensis.stubby.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import au.com.sensis.stubby.test.support.TestBase;

public class DelayTest extends TestBase {
    
    @Test
    public void delayTest() {
        long millis = 2000;

        builder().setRequestPath("/foo").setResponseStatus(200).setDelay(millis).stub();
        
        long started = now();
        assertOk(client.executeGet("/foo"));
        long ended = now();
        
        assertTimeTaken(started, ended, millis); // assert delay within tolerance
    }

}