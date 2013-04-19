package au.com.sensis.stubby.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import au.com.sensis.stubby.service.StubService;

public class StubContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        event.getServletContext().setAttribute(AbstractStubServlet.SERVICE_CONTEXT_KEY, new StubService());
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        event.getServletContext().removeAttribute(AbstractStubServlet.SERVICE_CONTEXT_KEY);        
    }

}
