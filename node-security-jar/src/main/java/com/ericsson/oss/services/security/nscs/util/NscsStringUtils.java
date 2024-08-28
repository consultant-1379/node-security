package com.ericsson.oss.services.security.nscs.util;

public class NscsStringUtils {
    private NscsStringUtils() {
        throw new IllegalStateException("NscsStringUtils class wrong called");
    }
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
