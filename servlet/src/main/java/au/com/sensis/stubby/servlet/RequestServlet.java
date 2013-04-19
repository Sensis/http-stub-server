package au.com.sensis.stubby.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.com.sensis.stubby.service.NotFoundException;

/*
 * Handles operations on requests (eg, 'GET /_control/requests/<index>')
 */
@SuppressWarnings("serial")
public class RequestServlet extends AbstractStubServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            returnJson(response, service().getRequest(getId(request)));
        } catch (NotFoundException e) {
            returnNotFound(response, e.getMessage());
        }
    }

}
