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

/**
 * Created by ekrzsia on 9/27/17.
 */

import java.util.Collection;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.CouldNotReadMoAttributeException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.handler.command.utility.WebServerStatus;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

public class MoAttributeHandlerImpl implements MoAttributeHandler {

    @Inject
    private NscsCMReaderService reader;

    private static final String MISMATCH = "MISMATCH";
    private static final String MATCH = "MATCH";

    /**
     * Gets value for a given attribute of Managed Object.
     *
     * @param nodeFdn
     * @param moType
     * @param namespace
     * @param attributeName
     * @return
     */
    public String getMOAttributeValue(final String nodeFdn, final String moType, final String namespace,
            final String attributeName) throws CouldNotReadMoAttributeException {

        Collection<CmObject> cmObjects;

        CmResponse mirrorResponse = reader.getMOAttribute(nodeFdn, moType, namespace, attributeName);
        cmObjects = mirrorResponse.getCmObjects();

        if (cmObjects.iterator().hasNext()) {
            final Object value = cmObjects.iterator().next().getAttributes().get(attributeName);
            if(value != null) {
                return value.toString();
            } else {
                return null;
            }
        }
        throw new CouldNotReadMoAttributeException(attributeName);
    }

    public String match (final WebServerStatus webServerStatus, final Boolean httpsAttributeInCppConnectivityInfo) {
        if ((WebServerStatus.HTTPS.equals(webServerStatus) && httpsAttributeInCppConnectivityInfo) || (WebServerStatus.HTTP.equals(webServerStatus) && !httpsAttributeInCppConnectivityInfo)) {
            return MATCH;
        }
        else {
            return MISMATCH;
        }
    }
}
