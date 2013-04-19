package au.com.sensis.stubby.service.model;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

import au.com.sensis.stubby.service.model.MatchField.FieldType;

public class MatchFieldTest {

    @Test
    public void ensurePatternsMatch() {
        MatchField field1 = new MatchField(FieldType.BODY, "foo", Pattern.compile(".*")).asMatchFailure("bar");
        MatchField field2 = new MatchField(FieldType.BODY, "foo", Pattern.compile(".*")).asMatchFailure("bar");
        
        assertEquals(field1, field2);
    }
    
    @Test
    public void ensurePatternsMatchString() {
        MatchField field1 = new MatchField(FieldType.BODY, "foo", Pattern.compile(".*")).asMatchFailure("bar");
        MatchField field2 = new MatchField(FieldType.BODY, "foo", ".*").asMatchFailure("bar");
        
        assertEquals(field1, field2);
    }
    
    @Test
    public void ensureStringMatchesPattern() {
        MatchField field1 = new MatchField(FieldType.BODY, "foo", ".*").asMatchFailure("bar");
        MatchField field2 = new MatchField(FieldType.BODY, "foo", Pattern.compile(".*")).asMatchFailure("bar");
        
        assertEquals(field1, field2);
    }
    
}
