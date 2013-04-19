package au.com.sensis.stubby.service.model;

import java.util.List;

import au.com.sensis.stubby.model.StubResponse;

public class StubServiceResult { // returned by the 'findMatch' method

    private List<MatchResult> attempts;
    private StubResponse response;
    private Long delay;

    public StubServiceResult(List<MatchResult> attempts) {
        this.attempts = attempts;
    }

    public StubServiceResult(List<MatchResult> attempts, StubResponse response, Long delay) {
        this.attempts = attempts;
        this.response = response;
        this.delay = delay;
    }

    public boolean matchFound() {
        for (MatchResult attempt : attempts) {
            if (attempt.matches()) {
                return true;
            }
        }
        return false;
    }

    public StubResponse getResponse() {
        return response;
    }

    public Long getDelay() {
        return delay;
    }

    public List<MatchResult> getAttempts() {
        return attempts;
    }

}
