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
 * IpSec command class for ipsec related command.
 * 
 * @author emehsau
 */

public class NscsIpSecCommand extends NscsNodeCommand {

    private static final long serialVersionUID = -5503835498731301863L;

    public static final String XML_FILE_PROPERTY = "xmlfile";

    /**
     * This method will return the location of input xml file which contain
     * details required for IpSec enable/disable operation.
     * 
     * @return inputFile
     */
    public String getXmlInputFile() {
        return getValueString(XML_FILE_PROPERTY);
    }

}
