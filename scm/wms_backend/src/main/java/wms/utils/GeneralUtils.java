package wms.utils;

public class GeneralUtils {
    public static String generateCodeFromSysTime() {
        return String.valueOf(System.currentTimeMillis()).substring(1, 9);
    }
}
