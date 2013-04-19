package au.com.sensis.stubby.utils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.sensis.stubby.model.StubMessage;

public class HttpMessageUtils {

    //private static final Logger LOGGER = Logger.getLogger(MessageUtils.class);

    private static final String H_CONTENT_TYPE = "Content-Type";

    private static final Pattern TEXT_CONTENT_TYPE = Pattern.compile("text/.+");
    private static final Pattern JSON_CONTENT_TYPE = Pattern.compile("application/json(;.+)?");

    public static String getReasonPhrase(int statusCode) {
        switch (statusCode) {
        case 200:
            return "OK";
        case 201:
            return "Created";
        case 202:
            return "Accepted";
        case 301:
            return "Moved Permanently";
        case 302:
            return "Found";
        case 304:
            return "Not Modified";
        case 400:
            return "Bad Request";
        case 401:
            return "Unauthorized";
        case 403:
            return "Forbidden";
        case 404:
            return "Not Found";
        case 406:
            return "Not Acceptable";
        case 415:
            return "Unsupported Media Type";
        case 422:
            return "Unprocessable Entity";
        case 500:
            return "Internal Server Error";
        case 503:
            return "Service Unavailable";
        default:
            return null;
        }
    }

    public static String upperCaseHeader(String name) {
        Pattern pattern = Pattern.compile("\\-.|^.");
        Matcher matcher = pattern.matcher(name);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group().toUpperCase());
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public static boolean isText(StubMessage message) {
        String contentType = message.getHeader(H_CONTENT_TYPE);
        return contentType != null
                && TEXT_CONTENT_TYPE.matcher(contentType).matches();
    }

    public static boolean isJson(StubMessage message) {
        String contentType = message.getHeader(H_CONTENT_TYPE);
        return contentType != null
                && JSON_CONTENT_TYPE.matcher(contentType).matches();
    }
    
    public static String bodyAsText(StubMessage message) {
        Object body = message.getBody();
        if (body instanceof String) {
            return (String)body;
        } else {
            throw new RuntimeException("Unexpected body type: " + body.getClass());
        }
    }
    
    public static Object bodyAsJson(StubMessage message) {
        Object body = message.getBody();
        if (body instanceof String) {
            return JsonUtils.deserialize((String)body, Object.class); // support object or array as top-level
        } else if (body instanceof Map
                || body instanceof List) {
            return body; // assume already parsed
        } else {
            throw new RuntimeException("Unexpected body type: " + body.getClass());
        }
    }

    //    public String formatBody(HttpMessage message) {
    //        String contentType = message.getContentType();
    //        Object body = message.getBody();
    //        if (contentType != null && contentType.startsWith("application/json")) {
    //            ObjectMapper mapper = JsonUtils.defaultMapper();
    //            ObjectWriter writer = JsonUtils.prettyWriter();
    //            if (message.getBody() instanceof String) {
    //                try {
    //                    return writer.writeValueAsString(mapper.readValue(body.toString(), Map.class)); // read-and-write pretty
    //                } catch (Exception e) {
    //                    LOGGER.debug("Error attempting to parse JSON content", e); // might be malformed
    //                }
    //            } else {
    //                try {
    //                    return writer.writeValueAsString(body); // write pretty
    //                } catch (Exception e) {
    //                    LOGGER.debug("Error attempting to write JSON content", e); // don't really care
    //                }
    //            }
    //        }
    //        return body.toString(); // if all else fails, return as-is.
    //    }

}
