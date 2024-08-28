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
package com.ericsson.nms.security.nscs.cpp.model;

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;


/**
 * ScepEnrollmentInfo holding data to call <i>initCertEnrollment</i> MO action on a CPP node
 * 
 * New fields and methods can be introduced later for example: int getKeyLength();//CPP default is 1024...support for 2048 if needed
 * 
 * @author egbobcs
 */
public interface ScepEnrollmentInfo {

    /**
     * Gets the distinguishedName
     * 
     * @return distinguishedName
     */
    String getDistinguishedName();

    /**
     * Gets the Entity Name
     * 
     * @return Name
     */
    String getName();

   /**
     * Gets the challengePassword
     * 
     * @return challengePassword
     */
    String getChallengePassword();

    /**
     * Gets the serverURL
     * 
     * @return serverURL
     */
    String getServerURL();

    /**
     * Sets the serverURL
     * 
     * @param serverURL
     */
    void setServerURL(String serverUrl);

    /**
     * Gets the serverCertFingerPrint
     * 
     * @return serverCertFingerPrint
     */
    byte[] getServerCertFingerPrint();

    /**
     * Gets the serverCertFingerPrint
     * 
     * @param fp   fingerPrint value
     */
    void setServerCertFingerPrint(final byte[] fp);
    
    /**
     * Gets the PkiRootCertFingerPrint
     * 
     * @return PkiRootCertFingerPrint
     */
    byte[] getPkiRootCertFingerPrint();
    
    /**
     * Gets the PkiRootCertFingerPrint
     * 
     * @param fp   fingerPrint value
     */
    void setPkiRootCertFingerPrint(final byte[] fp);

    /**
     * Gets the Digest algorithm used to compute CA fingerprint
     * 
     * @return DigestAlgorithm
     */
    DigestAlgorithm getFingerPrintAlgorithm();

    /**
     * Gets the rollbackTimeout
     * 
     * @return rollbackTimeout
     */
    int getRollbackTimeout();

	/**
	 * @param timeout
	 */
	void setRollbackTimeout(final int timeout);

	/**
	 * Gets the EnrollmentProtocol
	 * @return enrollmentProtocol
	 */
	String getEnrollmentProtocol();

	/**
	 * Sets the Enrollment Protocol
	 * @param value
	 */
	void setEnrollmentProtocol(final String value);

	/**
	 * Gets the Key Size
	 * @return keySize
	 */
	String getKeySize();

	/**
	 * Sets the Key Size
	 * @param value
	 */
	void setKeySize(final String value);
	
	/**
	 * Sets the Enrollment Mode
	 * @param enrollmentMode
	 */
    public void setEnrollmentMode(final EnrollmentMode enrollmentMode);
    
    /**
     * Gets the Enrollment Mode
     * @return
     */
    public EnrollmentMode getEnrollmentMode();

	/**
	 * Gets the Certificate Authority DN
	 * @return keySize
	 */
	String getCertificateAuthorityDn();
	

	/**
	 * Sets the Certificate Authority DN
	 * @param value
	 */
	void setCertificateAuthorityDn(final String value);
	
	/**
	 * Gets the Pki Root Certificate Authority DN
	 * @return keySize
	 */
	String getPkiRootCertificateAuthorityDn();
	
	/**
	 * Sets the Pki Root Certificate Authority DN
	 * @param value
	 */
	void setPkiRootCertificateAuthorityDn(final String value);

	/**
	 * Returns boolean value true if Key Size and Enrollment Mode fields are supported
	 * @return
	 */
	boolean isKSandEMSupported();

	/**
	 * Sets boolean value  if Key Size and Enrollment Mode fields are supported
	 * @return
	 */
	void setKSandEMSupported(final boolean isSupported);

    /**
     * Returns boolean value true if CertificateAuthorityDn field is supported
     * @return
     */
    boolean isCertificateAuthorityDnSupported();

    /**
     * Sets boolean value  if CertificateAuthorityDn field is supported
     * @return
     */
    void setCertificateAuthorityDnSupported(final boolean isSupported);
    
    /**
     * @return The Subject Alternative Name
     */
    BaseSubjectAltNameDataType getSubjectAltName();
    
    /**
     * @return The Subject Alternative Name Type
     */
    SubjectAltNameFormat getSubjectAltNameType();
    
    /**
     * @return the entity
     */
    Entity getEntity();
    
    /**
     *  Gets the Enrollment CA Name
     * @return enrollmentCAName
     */
    String getEnrollmentCaName();

    /**
     * Sets the Enrollment CA Name
     * @param caName
     */
    void setEnrollmentCaName(final String caName);

}
