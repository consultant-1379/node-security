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

import com.ericsson.nms.security.nscs.api.enums.SnmpAuthProtocol;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author ebarmos , emelant
 */
public class SnmpAuthnopriv extends NscsNodeCommand {

    private static final long serialVersionUID = -7687876413437473598L;

    public static final String AUTH_ALGO_PARAM = "auth_algo";
    public static final String AUTH_PWD_PARAM = "auth_password";

    private static final List<String> authProtocolList = new LinkedList<>();

    static {

        authProtocolList.add(SnmpAuthProtocol.MD5.toString());
        authProtocolList.add(SnmpAuthProtocol.SHA1.toString());
        authProtocolList.add(SnmpAuthProtocol.NONE.toString());

    }

    public static List<String> getAuthProtocolList() {
        return authProtocolList;
    }

    /**
     * 
     * @return auth_algo parameter
     */
    public String getAuthAlgo() {
        return getValueString(AUTH_ALGO_PARAM);
    }

    /**
     * 
     * @return auth_password parameter
     */
    public String getAuthPwd() {
        return getValueString(AUTH_PWD_PARAM);
    }

}
