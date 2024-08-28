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

import java.util.LinkedList;
import java.util.List;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.api.enums.CertificateType;

/**
 * GetCertEnrollStateCommand class for the get certEnrollState related command.
 * 
 * @author enmadmin
 */

public class GetCertEnrollStateCommand extends NscsNodeCommand {
	
	private static final long serialVersionUID = 3088320921340455454L;
	
	public static final String CERT_TYPE_PROPERTY = "certtype";

	/**
	 * This method will return the wanted certificate type for which to perform 
     * trust distribution operation.
     * 
	 * @return the getCertType
	 */
	public String getCertType() {
		return getValueString(CERT_TYPE_PROPERTY);
	}

}
