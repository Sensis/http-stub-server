package au.com.sensis.stubby.utils;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

public class JsonUtilsTest {

    private static final String JSON = "{\"foo\":\"bar\"}";
    
    public static class TestBean {

        private String foo;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

    }

    private TestBean bean;

    @Before
    public void before() {
        bean = new TestBean();
        bean.setFoo("bar");
    }

    @Test
    public void testPrettyPrint() {
        assertEquals("{\n  \"foo\" : \"bar\"\n}", JsonUtils.prettyPrint(bean));
    }
    
    @Test
    public void testSerializeString() {
        assertEquals(JSON, JsonUtils.serialize(bean));
    }

    @Test
    public void testSerializeStream() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonUtils.serialize(stream, bean);
        assertEquals(JSON, stream.toString());
    }
    
    @Test
    public void testDeserializeString() {
        assertEquals("bar", JsonUtils.deserialize(JSON, TestBean.class).getFoo());
    }

    @Test
    public void testDeserializeStream() {
        ByteArrayInputStream stream = new ByteArrayInputStream(JSON.getBytes());
        assertEquals("bar", JsonUtils.deserialize(stream, TestBean.class).getFoo());
    }
    
}
