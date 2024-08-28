/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.data.moaction;

import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CertM;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;

/**
 * MoActionWithoutParameter represents all the MO actions that are needed for
 * security related MO operations.
 * 
 * The two CPP MOs that contain security operations are: Security and IpSec.
 * 
 * For details of the CPP MO see
 * https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc
 * /CPP-LSV127-gen9-complete_vs_LSV125/Security.html
 * https://cpp-mom.rnd.ki.sw.ericsson
 * .se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/IpSec.html
 * 
 * The COM/ECIM MOs that contain security operations are: CertM and
 * NodeCredential. Please note that for COM/ECIM the MO MUST be null, 
 * because it's not possible to calculate in a static way the correct fdn, 
 * since the COM ECIM nodes may have/have not MeContext MO and also MOs 
 * may have different namespaces [eg SGSN-MME, Radio Node].
 * In that case such actions MUST be performed using the {@link MOActionService#performMOActionByMoFdn(String, MoActionWithoutParameter)}
 * In that case {@link #getFDN(String)} will return null
 * 
 * For details of the COM/ECIM MOs see
 * http://pdu-nam-tools.lmera.ericsson.se/imftool/cpi/cpiIndex.html
 * 
 * @author egbobcs
 */
public enum MoActionWithoutParameter {

	CMFunction_sync(Model.NETWORK_ELEMENT.cmFunction, CmFunction.SYNC),

	// SL2 (MO is Security)
	Security_cancelCertEnrollment(
			Model.ME_CONTEXT.managedElement.systemFunctions.security,
			Security.CANCEL_CERT_ENROLLMENT), Security_cancelInstallTrustedCertificates(
			Model.ME_CONTEXT.managedElement.systemFunctions.security,
			Security.CANCEL_INSTALL_TRUSTED_CERTIFICATES), Security_adaptSecurityLevel(
			Model.ME_CONTEXT.managedElement.systemFunctions.security,
			Security.ADAPT_SECURITY_LEVEL), Security_confirmNewCreds(
			Model.ME_CONTEXT.managedElement.systemFunctions.security,
			Security.CONFIRM_NEW_CREDS),

	// SL3 to come in 15B
	// IPSEC to come in 15B
	IpSec_cancelCertEnrollment(Model.ME_CONTEXT.managedElement.ipSystem.ipSec,
			IpSec.CANCEL_CERT_ENROLLMENT), IpSec_cancelInstallTrustedCertificates(
			Model.ME_CONTEXT.managedElement.ipSystem.ipSec,
			IpSec.CANCEL_INSTALL_TRUSTED_CERTIFICATES),

	// COM/ECIM
	// CertM cancel action
	ComEcim_CertM_cancel(null, CertM.CANCEL),

	// CertM downloadCrl action
	ComEcim_CertM_downloadCrl(null, CertM.DOWNLOAD_CRL),

	// NodeCredential cancelEnrollment action
	ComEcim_NodeCredential_cancelEnrollment(null, NodeCredential.CANCEL_ENROLLMENT),

        // Local RBAC (MO is Security)
        SECURITY_CANCEL_INSTALL_LOCAL_AA_DATABASE(Model.ME_CONTEXT.managedElement.systemFunctions.security, Security.CANCEL_INSTALL_LOCAL_AA_DATABASE);

	private final String action;
	private final ModelDefinition.Mo mo;

	MoActionWithoutParameter(final ModelDefinition.Mo mo, final String action) {
		this.mo = mo;
		this.action = action;
	}

	/**
	 * Gets the action name as a String. Example: "cancelCertEnrollment"
	 * 
	 * @return action name
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Gets the FDN of the MO on which the action is performed according to the
	 * given NE Name or to the FDN of the node root MO.
	 * 
	 * @param neNameOrNodeRootFdn
	 *            name of the NE or FDN of the node root MO.
	 * @return FDN of the MO on which the action is performed
	 */
	public String getFDN(final String neNameOrNodeRootFdn) {
		if (mo != null) {
			return mo.withNames(neNameOrNodeRootFdn).fdn();
		} else {
			return null;
		}
	}
}
