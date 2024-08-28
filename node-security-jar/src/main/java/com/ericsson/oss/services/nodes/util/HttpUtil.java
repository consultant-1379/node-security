/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nodes.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.exception.UserNotFoundException;

/**
 * Utility class that extracts attrubute from Http objects
 * @author egicass 01/02/2016
 *
 */

public class HttpUtil {
	
	

	 /**
     * Gets the value for the TOR User identified with "X-Tor-UserID" in the
     * header from the passed HttpRequest
     *
     * @param req
     *            The HttpRequest to get the TOR user from
     * @return String userId value found in the header.
	 * @throws Exception 
     * @throws UserNotFoundException
     *             If userId in header is null
     */
    public static String getUserIdFromHeader(final HttpServletRequest req) throws Exception {
        final String key = "X-Tor-UserID";
        final String userId = req.getHeader(key);
        if (userId == null) {
            LoggerFactory.getLogger(HttpUtil.class).error("*** userId not in HEADER: {0}", key);
            //TODO handle the exception in a proper way
            throw new UserNotFoundException("User not loggedn in");
        }
        return userId;
    }
	
	
}
