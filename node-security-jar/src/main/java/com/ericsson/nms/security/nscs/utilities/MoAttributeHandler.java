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

package com.ericsson.nms.security.nscs.utilities;

import com.ericsson.nms.security.nscs.api.exception.CouldNotReadMoAttributeException;
import com.ericsson.nms.security.nscs.handler.command.utility.WebServerStatus;

/**
 * Created by ekrzsia on 9/27/17.
 */
public interface MoAttributeHandler {

    String getMOAttributeValue(final String nodeFdn, final String moType, final String namespace,
            final String attributeName) throws CouldNotReadMoAttributeException;

    String match(final WebServerStatus webServerStatus, final Boolean httpsAttributeInCppConnectivityInfo);

}
