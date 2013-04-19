package au.com.sensis.stubby.service.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import au.com.sensis.stubby.model.StubExchange;
import au.com.sensis.stubby.model.StubRequest;

public class StubServiceExchangeTest {

    private StubServiceExchange instance1;
    private StubServiceExchange instance2;
    
    @Before
    public void before() {
        StubExchange exchange1 = new StubExchange();
        StubExchange exchange2 = new StubExchange();
        
        exchange1.setRequest(new StubRequest());
        exchange2.setRequest(new StubRequest());
        
        instance1 = new StubServiceExchange(exchange1);
        instance2 = new StubServiceExchange(exchange2);
    }
    
    @Test
    public void testEquality() {
        assertEquals(instance1, instance2);
    }
    
    @Test
    public void testHashCode() {
        assertEquals(instance1.hashCode(), instance2.hashCode());
    }
    
}
