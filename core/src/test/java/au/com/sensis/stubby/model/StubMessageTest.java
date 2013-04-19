package au.com.sensis.stubby.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class StubMessageTest {

    @SuppressWarnings("serial")
    public static class TestBean implements Serializable {
        private List<String> items = new ArrayList<String>();
        public List<String> getItems() {
            return items;
        }
    }
    
    private TestBean testBean;
    private List<StubParam> headers;
    private StubMessage message;

    @Before
    public void before() {
        headers = new ArrayList<StubParam>();
        headers.add(new StubParam("Content-Type", "text/plain"));
        headers.add(new StubParam("Content-Type", "application/json"));
        
        testBean = new TestBean();
        testBean.getItems().add("one");
        
        message = new StubMessage() { };
        message.setBody(testBean);
        message.setHeaders(headers);
    }
        
    @Test
    public void testCopyConstructor() {
        StubMessage copy = new StubMessage(message) { };
        
        assertEquals("one", ((TestBean)copy.getBody()).getItems().get(0));
        assertEquals("Content-Type", copy.getHeaders().get(0).getName());
        assertEquals("text/plain", copy.getHeaders().get(0).getValue());

        assertTrue(copy.getHeaders() != headers); // assert deep copy
        assertTrue(copy.getBody() != testBean);
    }
    
    @Test
    public void testGetHeaderFirst() {
        assertEquals("text/plain", message.getHeader("content-type")); // ensure case-insensitive
    }
    
    @Test
    public void testGetHeaders() {
        assertEquals(
                Arrays.asList("text/plain", "application/json"),
                message.getHeaders("content-type")); // ensure case-insensitive
    }
    
    @Test
    public void testRemoteHeader() {
        message.removeHeader("content-type"); // ensure case-insensitive
        assertTrue(message.getHeaders("content-type").isEmpty());
    }
    
    @Test
    public void testJsonSerialize() {
        // TODO: assert Jackson serializes/deserializes correctly
    }
}
