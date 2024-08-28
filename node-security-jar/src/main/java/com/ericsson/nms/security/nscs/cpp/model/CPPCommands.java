/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.cpp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum contain a list of CPP nodes commands used during node security administration
 */
public enum CPPCommands {

    SECMODE_F_S("secmode -f s"),
    SECMODE_F_U("secmode -f u"),
    SECMODE_L_1("secmode -l 1"),
    SECMODE_L_2("secmode -l 2"),
    SECMODE_W_S("secmode -w s"),
    SECMODE_W_U("secmode -w u"),
    SECMODE_S("secmode -s");


    private final String text;

    private static final Map<String, CPPCommands> textToEnumMap = new HashMap<String, CPPCommands>(5) {
        {
            put(SECMODE_F_S.text, SECMODE_F_S);
            put(SECMODE_F_U.text, SECMODE_F_U);
            put(SECMODE_L_1.text, SECMODE_L_1);
            put(SECMODE_L_2.text, SECMODE_L_2);
            put(SECMODE_W_S.text, SECMODE_W_S);
            put(SECMODE_W_U.text, SECMODE_W_U);
            put(SECMODE_S.text, SECMODE_S);
        }
    };

    private CPPCommands(final String value) {
        this.text = value;
    }

    @Override
    public String toString() {
        return this.text;
    }

    /**
     * Returns the corresponding CPPCommands enumeration based on the command line provided
     * 
     * @param command
     *            String containing the actual command text
     * @return corresponding CPPCommands enumeration
     */
    public static CPPCommands fromCommandString(final String command) {
        return textToEnumMap.get(command);
    }
}
