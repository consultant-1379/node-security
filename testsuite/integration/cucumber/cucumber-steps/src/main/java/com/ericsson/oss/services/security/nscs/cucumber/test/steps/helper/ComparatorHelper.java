/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2018
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/

package com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ComparatorHelper {

    public static boolean equalLists(final List<String> expected, final List<String> actual) {
        if (expected == null && actual == null) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        if (expected.size() != actual.size()) {
            return false;
        }
        final List<String> exp = new ArrayList<String>(expected);
        final List<String> act = new ArrayList<String>(actual);
        Collections.sort(exp);
        Collections.sort(act);
        return exp.equals(act);
    }

    public static boolean equalMapOfLists(final Map<String, List<String>> expected, final Map<String, List<String>> actual) {
        if (expected == null && actual == null) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        if (expected.size() != actual.size()) {
            return false;
        }
        if (!expected.keySet().equals(actual.keySet())) {
            return false;
        }
        for (final String key : expected.keySet()) {
            if (!equalLists(expected.get(key), actual.get(key))) {
                return false;
            }
        }
        return true;
    }

    private static boolean equalMaps(final Map<String, ?> expected, final Map<String, ?> actual) {
        if ((expected == null) && (actual == null)) {
            return true;
        }
        if ((expected == null) || (actual == null) || (expected.size() != actual.size())
                || (!expected.keySet().equals(actual.keySet()))) {
            return false;
        }
        for (Map.Entry<String, ?> entry : expected.entrySet()) {
            if (entry.getValue() instanceof List<?>) {
                if (!equalLists((List<String>) entry.getValue(), (List<String>) actual.get(entry.getKey()))) {
                    return false;
                }
            } else if (entry.getValue() instanceof String) {
                if (!entry.getValue().equals(actual.get(entry.getKey()))) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static boolean equalValue(final Object expected, final Object actual) {
        if (expected == null) {
            if (actual != null) {
                return false;
            }
        } else if (!expected.equals(actual)) {
            if (expected instanceof List<?>) {
                if (!ComparatorHelper.equalLists((List<String>) expected, (List<String>) actual)) {
                    return false;
                }
            } else if (expected instanceof Map<?, ?>) {
                if (!ComparatorHelper.equalMaps((Map<String, ?>) expected, (Map<String, ?>) actual)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

}
