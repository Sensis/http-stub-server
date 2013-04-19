package au.com.sensis.stubby.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import au.com.sensis.stubby.test.support.TestBase;

public class DelayTest extends TestBase {
    
    @Test
    public void delayTest() {
        assumeNotTravisCi(); // skip test: Travis CI is really slow, and this test usually fails.
        
        long millis = 2000;
        long tolerance = 500;
        
        builder().setRequestPath("/foo").setResponseStatus(200).setDelay(millis).stub();
        
        long started = System.currentTimeMillis();
        assertOk(client.executeGet("/foo"));
        long ended = System.currentTimeMillis();
        
        assertEquals(millis, Math.abs(ended-started), tolerance); // assert delay within tolerance
    }

}