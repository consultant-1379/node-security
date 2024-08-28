/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.util;

import java.util.Arrays;
import java.util.List;

import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException;

/**
 * Auxiliary class to manage conversions between common REST DTO and Internal NSCS format.
 */
public final class CommonDtoHelper {

    private CommonDtoHelper() {
    }

    /**
     * Converts a given string containing a list of nodes as a string separated with "&" (REST DTO format) to a list of node names or FDNs (NSCS
     * internal format).
     * 
     * @param nodeList
     *            the string containing a list of nodes separated with "&".
     * @return the list of node names or FDNs.
     * @throws NscsBadRequestException
     *             if conversion fails.
     */
    public static List<String> fromNodeListDto(final String nodeList) {
        if (nodeList == null || nodeList.isEmpty()) {
            final String errorMessage = "Null or empty REST DTO node list";
            throw new NscsBadRequestException(errorMessage);
        }
        final String[] nodeArray = nodeList.split("&");
        return Arrays.asList(nodeArray);
    }
}
