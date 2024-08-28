/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.types;

import com.ericsson.nms.security.nscs.api.enums.SnmpPrivProtocol;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author ebarmos, emelant
 */
public class SnmpAuthpriv extends SnmpAuthnopriv {

    private static final long serialVersionUID = 7885149141880466130L;

    public static final String PRIV_ALGO_PARAM = "priv_algo";
    public static final String PRIV_PWD_PARAM = "priv_password";

    private static final List<String> privProtocolList = new LinkedList<>();

    static {

        privProtocolList.add(SnmpPrivProtocol.AES128.toString());
        privProtocolList.add(SnmpPrivProtocol.DES.toString());
        privProtocolList.add(SnmpPrivProtocol.NONE.toString());

    }

    public static List<String> getPrivProtocolList() {
        return privProtocolList;
    }

    /**
     * 
     * @return priv_algo parameter
     */
    public String getPrivAlgo() {
        return getValueString(PRIV_ALGO_PARAM);
    }

    /**
     * 
     * @return priv_password parameter
     */
    public String getPrivPwd() {
        return getValueString(PRIV_PWD_PARAM);
    }

}
