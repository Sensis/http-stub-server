package au.com.sensis.stubby.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import au.com.sensis.stubby.test.support.TestBase;

public class LoadTest extends TestBase {
    
    private static final int NUM_THREADS = 10;
    private static final int NUM_ITERATIONS = 500;

    private ThreadPoolExecutor executor;
    private AtomicInteger passes;
    
    private void givenExecutor() {
        executor = new ThreadPoolExecutor(NUM_THREADS, NUM_THREADS, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        passes = new AtomicInteger();
    }
    
    @Test
    public void loadTest() throws Exception {
        givenExecutor();
        
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            executor.execute(new TestAction(i));
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        
        assertEquals(NUM_ITERATIONS, passes.get()); // ensure every task executed as passed
    }
    
    public class TestAction implements Runnable {

        private int index;
        
        public TestAction(int index) {
           this.index = index;
        }
        
        @Override
        public void run() {
            String path =  "/loadTest/" + index;
            String body = "Test No. " + index;
            
            builder().setRequestPath(path).setResponseBody(body).setResponseStatus(200).stub();
            
            String actual = client.executeGet(path).assertOk().getText();
            if (body.equals(actual)) {
                passes.incrementAndGet();
            } else {
                throw new RuntimeException(String.format("Expected body '%s', got '%s'", body, actual));
            }
        }

    }
    
}
