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

import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CertM;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential;
import com.ericsson.nms.security.nscs.data.ModelDefinition.RbsConfiguration;
import com.ericsson.nms.security.nscs.data.ModelDefinition.RealTimeSecLog;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;
import com.ericsson.nms.security.nscs.data.ModelDefinition.TimeSetting;

/**
 * MoActionWithParameter represents all the MO actions that are needed for
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
 * In that case such actions MUST be performed using the {@link MOActionService#performMOActionByMoFdn(String, MoActionWithParameter, com.ericsson.nms.security.nscs.data.moaction.param.MoParams)}
 * In that case {@link #getFDN(String)} will return null 
 *  
 * For details of the COM/ECIM MOs see
 * http://pdu-nam-tools.lmera.ericsson.se/imftool/cpi/cpiIndex.html
 * 
 * @author egbobcs
 */
public enum MoActionWithParameter {

	// SL2 (MO is Security)
	Security_initCertEnrollment(
			Model.ME_CONTEXT.managedElement.systemFunctions.security,
			Security.INIT_CERT_ENROLLMENT),
	Security_installTrustedCertificates(
			Model.ME_CONTEXT.managedElement.systemFunctions.security,
			Security.INSTALL_TRUSTED_CERTIFICATES),
	//Remove Trust Certificate
	Security_removeTrustedCert(
			Model.ME_CONTEXT.managedElement.systemFunctions.security,
			Security.REMOVE_TRUSTED_CERTIFICATES),
	// Add External Server
	RealTimeSecLog_addExternalServer(
		        Model.ME_CONTEXT.managedElement.systemFunctions.security.realTimeSecLog,
		        RealTimeSecLog.ADD_EXTERNAL_SERVER),
       // Delete External Server
       RealTimeSecLog_deleteExternalServer(
		        Model.ME_CONTEXT.managedElement.systemFunctions.security.realTimeSecLog,
		        RealTimeSecLog.DELETE_EXTERNAL_SERVER),
        // SL3 to come in 15B
	// IPSEC to come in 15B
	IpSec_initCertEnrollment(
			Model.ME_CONTEXT.managedElement.ipSystem.ipSec,
			IpSec.INIT_CERT_ENROLLMENT),
	IpSec_installTrustedCertificates(
			Model.ME_CONTEXT.managedElement.ipSystem.ipSec,
			IpSec.INSTALL_TRUSTED_CERTIFICATES),
	//Remove Trust Certificate
	IpSec_removeTrustedCert(
			Model.ME_CONTEXT.managedElement.ipSystem.ipSec,
			IpSec.REMOVE_TRUSTED_CERTIFICATES),

	// To Support old node
	// The RbsConfiguration MO under ENodeBFunction is deprecated since L13A and
	// will be removed. The new parent MO is NodeManagementFunction.
	IpSec_changeIpForOamSetting(
			Model.ME_CONTEXT.managedElement.nodeManagementFunction.rbsConfiguration,
			RbsConfiguration.CHANGE_IP_FOR_OAM_SETTING),
	IpSec_changeIpForOamSetting_old(
			Model.ME_CONTEXT.managedElement.eNodeBFunction.rbsConfiguration,
			RbsConfiguration.CHANGE_IP_FOR_OAM_SETTING),

	// COM/ECIM
	// CertM installTrustedCertFromUri action
	ComEcim_CertM_installTrustedCertFromUri(
			null,
			CertM.INSTALL_TRUSTED_CERT_FROM_URI),

	// CertM removeTrustedCert action
	ComEcim_CertM_removeTrustedCert(
			null,
			CertM.REMOVE_TRUSTED_CERT),

	// NodeCredential installCredentialFromUri action
	ComEcim_NodeCredential_installCredentialFromUri(
			null,
			NodeCredential.INSTALL_CREDENTIAL_FROM_URI),

	// NodeCredential startOfflineCsrEnrollment action
	ComEcim_NodeCredential_startOfflineCsrEnrollment(
			null,
			NodeCredential.START_OFFLINE_CSR_ENROLLMENT),

	// NodeCredential startOnlineEnrollment action
	ComEcim_NodeCredential_startOnlineEnrollment(
			null,
			NodeCredential.START_ONLINE_ENROLLMENT),

    // asymmetric-keys cmp start-cmp action
    CBPOI_ASYMMETRIC_KEYS_CMP_START_CMP(null, ModelDefinition.ASYMMETRIC_KEYS_CMP_START_CMP),

    // asymmetric-key cmp renew-cmp action
    CBPOI_ASYMMETRIC_KEY_CMP_RENEW_CMP(null, ModelDefinition.ASYMMETRIC_KEY_CMP_RENEW_CMP),

        //LAAD Distribution
        SECURITY_INSTALL_LOCAL_AA_DATABASE(Model.ME_CONTEXT.managedElement.systemFunctions.security, Security.INSTALL_LOCAL_AA_DATABASE),
        CPP_REMOVE_NTP_KEYS(Model.ME_CONTEXT.managedElement.systemFunctions.timeSetting, TimeSetting.REMOVE_NTP_KEYS),

        //Install Ntp Keys
        CPP_INSTALL_NTP_KEYS(Model.ME_CONTEXT.managedElement.systemFunctions.timeSetting, TimeSetting.INSTALL_NTP_KEYS_ACTION),

    // CbpOi certificates install-certificate-pem
    CBPOI_CERTIFICATES_INSTALL_CERTIFICATE_PEM(null, ModelDefinition.CERTIFICATES_INSTALL_CERTIFICATE_PEM);

	private final String action;
	private final ModelDefinition.Mo mo;

	MoActionWithParameter(final ModelDefinition.Mo mo, final String action) {
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
	
	public ModelDefinition.Mo getMo() {
		return this.mo;
	}
}
