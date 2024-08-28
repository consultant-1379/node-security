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
package com.ericsson.nms.security.nscs.ldap.utility;

import java.io.StringReader;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;

import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.ldap.entities.LdapConfigurations;

public class LdapConfigurationUnMarshaller {

    /**
     * Method to UnMarshall the User provided Ldap Configuration into Java
     * entities.
     * 
     * @param ldapConfigurationXml
     *            : Content of user input file.
     * @return {@link ldapConfigurationForASetOfNodes}
     */
    public LdapConfigurations buildLdapConfigurationFromXMLContent(final String ldapConfigurationXml) throws InvalidInputXMLFileException {
        LdapConfigurations ldapConfigurationForASetOfNodes = null;
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(LdapConfigurations.class);
            Unmarshaller jaxbUnmarshaller = null;
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            ldapConfigurationForASetOfNodes = (LdapConfigurations) jaxbUnmarshaller.unmarshal(new StreamSource(new StringReader(ldapConfigurationXml)));

        } catch (final JAXBException e) {
            throw new InvalidInputXMLFileException(NscsErrorCodes.INVALID_INPUT_XML_FILE);
        }
        return ldapConfigurationForASetOfNodes;
    }

}
