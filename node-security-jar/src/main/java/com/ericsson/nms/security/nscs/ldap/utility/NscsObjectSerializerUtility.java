/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ldap.utility;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult;

/**
 * This class is responsible for Serializing the properties.
 * 
 * @author xsrirko
 * 
 */
public class NscsObjectSerializerUtility {

    @Inject
    Logger logger;

    /**
     * This Method to Serialize the Output parameters.
     * 
     * @param outputParams
     * @return
     */
    public String serializeResult(final Map<String, Serializable> outputParams) {
        String encodedWfQueryTaskResult = null;

        WorkflowQueryTaskResult wfQueryTaskResult = new WorkflowQueryTaskResult(outputParams);

        try {
            encodedWfQueryTaskResult = NscsObjectSerializer.writeObject(wfQueryTaskResult);
        } catch (IOException e) {
            String errorMessageSerialize = String.format("Caught exception[%s] while serializing object for node", e.getMessage());
            logger.error(errorMessageSerialize);
            throw new UnexpectedErrorException(errorMessageSerialize);
        }
        return encodedWfQueryTaskResult;
    }
}
