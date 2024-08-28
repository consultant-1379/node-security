/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.types;

/**
 * IssueCertificateCommand class for issue related command.
 * 
 * @author enmadmin
 */
public class CertificateIssueCommand extends NscsNodeCommand {

    private static final long serialVersionUID = 4440765751514402345L;

    public static final String CERT_TYPE_PROPERTY = "certtype";
    public static final String XML_FILE_PROPERTY = "xmlfile";
    public static final String EXTERNAL_CA_PROPERTY = "extca";

    public String getCertType() {
        return getValueString(CERT_TYPE_PROPERTY);
    }

    /**
     * This method will return the location of input xml file which contain information required for Issue certificate operation.
     *
     * @return inputFile
     */
    public String getXmlInputFile() {
        return getValueString(XML_FILE_PROPERTY);
    }

    /**
     * This method will return the extca property value from the command.
     *
     * @return extca property value
     */
    public String getExternalCA() {
        return getValueString(EXTERNAL_CA_PROPERTY);
    }
}
