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
package com.ericsson.nms.security.nscs.enrollmentinfo.request;

import java.io.StringReader;

import javax.inject.Inject;
import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetailsList;

/**
 * Utility class to unMarshall the User provided NodeDetails into Java entities.
 * 
 * @author tcsmave
 *
 */
public class NodeDetailsUnMarshaller {

    @Inject
    private Logger logger;

    /**
     * Method to UnMarshall the User provided NodeDetails into Java entities.
     * 
     * @param nodeDetailsXml
     * @return
     * @throws InvalidInputXMLFileException
     */
    public NodeDetailsList buildNodeDetailsFromXmlContent(final String nodeDetailsXml) throws InvalidInputXMLFileException {
        logger.info("Entered into buildNodeDetailsFromXMLContent:::::");

        NodeDetailsList nodeDetails = null;
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(NodeDetailsList.class);
            Unmarshaller jaxbUnmarshaller = null;
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            nodeDetails = (NodeDetailsList) jaxbUnmarshaller.unmarshal(new StreamSource(new StringReader(nodeDetailsXml)));

        } catch (final JAXBException e) {
            throw new InvalidInputXMLFileException(NscsErrorCodes.INVALID_INPUT_XML_FILE);
        }
        logger.info("End of buildNodeDetailsFromXMLContent:::::");

        return nodeDetails;
    }

}
