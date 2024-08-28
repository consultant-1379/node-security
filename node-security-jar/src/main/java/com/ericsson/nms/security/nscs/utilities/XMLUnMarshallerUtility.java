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

import java.io.StringReader;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;

import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;

public class XMLUnMarshallerUtility {
	
   /**
     * Method to UnMarshall the user input xml file content into Java
     * entities.
     * 
     * @param xmlContent
     *            xmlContent of user input file.
     * @return {@link unmarshalledClass}
     */
    @SuppressWarnings("unchecked")
    public <T> T xMLUnmarshaller(final String xmlContent,final Class<T> unmarshallerClass) throws InvalidInputXMLFileException {
        T unmarshalledClass = null;
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(unmarshallerClass);
            Unmarshaller jaxbUnmarshaller = null;
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            unmarshalledClass = (T) jaxbUnmarshaller.unmarshal(new StreamSource(new StringReader(xmlContent)));

        } catch (final JAXBException e) {
            throw new InvalidInputXMLFileException(NscsErrorCodes.INVALID_INPUT_XML_FILE);
        }
        return unmarshalledClass;
    }
}
