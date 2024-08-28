package com.ericsson.oss.services.security.nscs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NscsEnumUtils {
    private static Logger logger = LoggerFactory.getLogger(NscsEnumUtils.class);
    private NscsEnumUtils() {
        throw new IllegalStateException("NscsEnumUtils class wrong called");
    }
    public static <T extends Enum<T>> boolean isValidEnum(Class<T> enumType, String strName) {
        return getEnum(enumType, strName) != null;
    }

    public static <T extends Enum<T>> T getEnum(Class<T> enumType, String strName) {
        if (strName == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumType, strName);
        } catch (IllegalArgumentException excp) {
            logger.info("Enum: {}, msg: {}", excp.getClass().getCanonicalName(), excp.getMessage());
            return null;
        }
    }
}
