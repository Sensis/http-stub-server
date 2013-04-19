package au.com.sensis.stubby.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import au.com.sensis.stubby.service.model.StubServiceResult;

/*
 * Generic stubbed controller that can be used to stub any URL
 * Stubbed URLs can contain wildcards, eg: /path/*?param=*
 */
@SuppressWarnings("serial")
public class MatcherServlet extends AbstractStubServlet {

    private static final Logger LOGGER = Logger.getLogger(MatcherServlet.class);

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            StubServiceResult result = service().findMatch(Transformer.fromServletRequest(request));
            if (result.matchFound()) {
                Long delay = result.getDelay();
                if (delay != null && delay > 0) {
                    LOGGER.info("Delayed request, sleeping for " + delay + " ms...");
                    Thread.sleep(delay);
                }
                Transformer.populateServletResponse(result.getResponse(), response);
            } else {
                returnNotFound(response, "No stubbed method matched request");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
            try {
                returnError(response, e.getMessage());
            } catch (IOException ex) {
                LOGGER.error(ex);
                ex.printStackTrace();
            }
        }
    }

}
