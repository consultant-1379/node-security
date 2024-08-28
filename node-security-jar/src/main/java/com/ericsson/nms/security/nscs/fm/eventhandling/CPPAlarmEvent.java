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
package com.ericsson.nms.security.nscs.fm.eventhandling;

import java.util.HashMap;
import java.util.Map;

/**
 * ENUM to represent the CPP specific alarms and events and map them to workflow specific messages.
 * 
 * The event/alarm sent by the CPP node does NOT contain the alarmID/eventID, so we need to find out the ID based on the "specificProblem" field.
 * 
 * @author egbobcs
 *
 */
public enum CPPAlarmEvent {
	
	//Alarms
	CPPAlarm167CredentialsEnrollmentFault, 
	CPPAlarm168CredentialsValidityFault,
	CPPAlarm206IPsecCertificateExpiry,
	CPPAlarm207IPsecCertificateFault,
	CPPAlarm235LicenseKeyforFeatureMissing,
	CPPAlarm271LocalAADBFault,
	CPPAlarm45LocalAADBInstallation,
	CPPAlarm73PasswordFileFault,
	CPPAlarm102SecurityLevelFault,
	CPPAlarm237SignedSoftwareProblem,
	CPPAlarm268TrustedCertificateFault,
	CPPAlarm128TrustedCertificateInstallationFault, 
	CPPAlarm226UserDefinedProfilesFileError,
	
	//Events	
	CPPEvent126CertOKWithoutRevocationCheck, 
	CPPEvent128CRLDownloadFailure,
	CPPEvent7DownloadOfLocalAADatabaseCompleted,
	CPPEvent8DownloadOfLocalAADatabaseFailed,
	CPPEvent9DownloadOfTrustedCertificatesCompleted,
	CPPEvent10DownloadOfTrustedCertificatesFailed,
	CPPEvent88IKESAnegotiation,
	CPPEvent26NodeCredentialsExpiring,
	CPPEvent27NodeCredentialsInstalled,
	CPPEvent28NodeCredentialsRolledBack,
	CPPEvent127PeerCertificateFaultInfo,
	CPPEvent125TrustedCertificateFaultInfo,
	;

	private static final Map<String, CPPAlarmEvent> specificProblemMap = new HashMap<>();
		
	static {
		
		//Mapping specificProblem Strings to Alarms/Events 
		
		//Alarms
		specificProblemMap.put("Credentials Enrollment Fault", CPPAlarm167CredentialsEnrollmentFault);				
		specificProblemMap.put("Credentials Validity Fault", CPPAlarm168CredentialsValidityFault);
		specificProblemMap.put("IPsec Certificate Expiry", CPPAlarm206IPsecCertificateExpiry);
		specificProblemMap.put("IPsec Certificate Fault", CPPAlarm207IPsecCertificateFault);
		specificProblemMap.put("License Key for Feature Missing", CPPAlarm235LicenseKeyforFeatureMissing);
		specificProblemMap.put("Local AA DB Fault", CPPAlarm271LocalAADBFault);
		specificProblemMap.put("Local AA DB Installation Fault", CPPAlarm45LocalAADBInstallation);
		specificProblemMap.put("Password File Fault", CPPAlarm73PasswordFileFault);
		specificProblemMap.put("Security Level Fault", CPPAlarm102SecurityLevelFault);
		specificProblemMap.put("Signed Software Problem", CPPAlarm237SignedSoftwareProblem);
		specificProblemMap.put("Trusted Certificate Fault", CPPAlarm268TrustedCertificateFault);
		specificProblemMap.put("Trusted Certificate Installation Fault", CPPAlarm128TrustedCertificateInstallationFault);
		specificProblemMap.put("User-Defined Profiles File Error",CPPAlarm226UserDefinedProfilesFileError);
		
		//Events
		specificProblemMap.put("Cert OK without revocation check", CPPEvent126CertOKWithoutRevocationCheck);
		specificProblemMap.put("User-Defined Profiles File Error", CPPAlarm226UserDefinedProfilesFileError);
		specificProblemMap.put("Download of Local AA Database completed", CPPEvent7DownloadOfLocalAADatabaseCompleted);
		specificProblemMap.put("Download of Local AA Database failed", CPPEvent8DownloadOfLocalAADatabaseFailed);
		specificProblemMap.put("Download of Trusted Certificates completed", CPPEvent9DownloadOfTrustedCertificatesCompleted);
		specificProblemMap.put("Download of Trusted Certificates failed", CPPEvent10DownloadOfTrustedCertificatesFailed);
		specificProblemMap.put("IKE SA negotiation", CPPEvent88IKESAnegotiation);
		specificProblemMap.put("Node Credentials expiring", CPPEvent26NodeCredentialsExpiring);
		specificProblemMap.put("Node Credentials installed", CPPEvent27NodeCredentialsInstalled);
		specificProblemMap.put("Node Credentials rolled back", CPPEvent28NodeCredentialsRolledBack);
		specificProblemMap.put("Peer Certificate Fault info", CPPEvent127PeerCertificateFaultInfo);
		specificProblemMap.put("Trusted Certificate Fault info", CPPEvent125TrustedCertificateFaultInfo);
	}

	public static CPPAlarmEvent getFromSpecificProblem(final String specificProblem) {
		return specificProblemMap.get(specificProblem);
	}
}
