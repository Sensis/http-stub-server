package au.com.sensis.stubby.service;

@SuppressWarnings("serial")
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
    
}
