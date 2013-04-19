package au.com.sensis.stubby.utils;

public class EqualsUtils {
    
    public static boolean safeEquals(Object a, Object b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }
    
}
