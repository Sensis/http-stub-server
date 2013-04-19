package au.com.sensis.stubby.service.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.sensis.stubby.service.model.MatchField.MatchType;

@RunWith(MockitoJUnitRunner.class)
public class MatchResultTest {

    @Mock
    private MatchField field1;
    @Mock
    private MatchField field2;
    @Mock
    private MatchField field3;
    
    private MatchResult result1;
    private MatchResult result2;
    
    @Before
    public void before() {
        when(field1.score()).thenReturn(1);
        when(field2.score()).thenReturn(2);
        when(field3.score()).thenReturn(3);
        
        when(field1.getMatchType()).thenReturn(MatchType.MATCH_FAILURE);
        when(field2.getMatchType()).thenReturn(MatchType.MATCH);
        when(field3.getMatchType()).thenReturn(MatchType.MATCH);
        
        result1 = new MatchResult();
        result2 = new MatchResult();
        
        result1.add(field1);
        
        result2.add(field2);
        result2.add(field3);
    }
    
    @Test
    public void testOrder() { // best score first
        List<MatchResult> fields = new ArrayList<MatchResult>(Arrays.asList(result1, result2));
        
        Collections.sort(fields);
        
        assertEquals(5, fields.get(0).score()); // assert adds up score
        assertEquals(1, fields.get(1).score());
    }
    
    @Test
    public void testMatches() {
        assertTrue(result2.matches());
    }
    
    @Test
    public void testDoesntMatch() {
        assertFalse(result1.matches());
    }
    
}
