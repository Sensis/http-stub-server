package au.com.sensis.stubby.service.model;

import java.util.ArrayList;
import java.util.List;

public class MatchResult implements Comparable<MatchResult> {

    private List<MatchField> fields;

    public MatchResult() {
        this.fields = new ArrayList<MatchField>();
    }

    public void add(MatchField field) {
        fields.add(field);
    }

    public List<MatchField> getFields() {
        return fields;
    }

    public boolean matches() {
        for (MatchField field : fields) {
            if (field.getMatchType() != MatchField.MatchType.MATCH) {
                return false; // found a failure
            }
        }
        return true; // no failures
    }

    public int score() {
        int result = 0;
        for (MatchField field : fields) {
            result += field.score();
        }
        return result;
    }

    @Override
    public int compareTo(MatchResult other) {
        return new Integer(score()).compareTo(other.score()) * -1; // highest score first
    }
    
    @Override
    public String toString() {
        return fields.toString();
    }

}
