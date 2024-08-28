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
package com.ericsson.nms.security.nscs.api.cert.issue;

public class CertIssueWfParams {

	private String certType;
	private String entityProfileName;
	private String subjAltName;
	private String subjAltNameType;
	private String enrollmentMode;
	private String keySize;
	private String commonName;
	
	/**
	 * 
	 */
	public CertIssueWfParams() {
		super();
	}

	/**
	 * @param certType
	 * 			the certType to set
	 * @param entityProfileName
	 * 			the entityProfileName to set
	 * @param subjAltName
	 * 			the subjAltName to set
	 * @param subjAltNameType
	 * 			the subjAltNameType to set
	 * @param enrollmentMode
	 *  		the enrollmentMode to set
	 * @param keySize
	 * 			the keySize to set
	 * @param commonName
	 * 			the commonName to set
	 */
	public CertIssueWfParams(String certType, String entityProfileName, String subjAltName,
			String subjAltNameType, String enrollmentMode, String keySize, String commonName) {
		super();
		this.certType = certType;
		this.entityProfileName = entityProfileName;
		this.subjAltName = subjAltName;
		this.subjAltNameType = subjAltNameType;
		this.enrollmentMode = enrollmentMode;
		this.keySize = keySize;
		this.commonName = commonName;
	}

	/**
	 * @return the certType
	 */
	public String getCertType() {
		return certType;
	}

	/**
	 * @param certType the certType to set
	 */
	public void setCertType(String certType) {
		this.certType = certType;
	}
	
	/**
	 * @return the entityProfileName
	 */
	public String getEntityProfileName() {
		return entityProfileName;
	}

	/**
	 * @param entityProfileName the entityProfileName to set
	 */
	public void setEntityProfileName(String entityProfileName) {
		this.entityProfileName = entityProfileName;
	}

	/**
	 * @return the subjAltName
	 */
	public String getSubjAltName() {
		return subjAltName;
	}

	/**
	 * @param subjAltName the subjAltName to set
	 */
	public void setSubjAltName(String subjAltName) {
		this.subjAltName = subjAltName;
	}

	/**
	 * @return the subjAltNameType
	 */
	public String getSubjAltNameType() {
		return subjAltNameType;
	}

	/**
	 * @param subjAltNameType the subjAltNameType to set
	 */
	public void setSubjAltNameType(String subjAltNameType) {
		this.subjAltNameType = subjAltNameType;
	}
	
	/**
	 * @return the enrollmentMode
	 */
	public String getEnrollmentMode() {
		return enrollmentMode;
	}

	/**
	 * @param enrollmentMode the enrollmentMode to set
	 */
	public void setEnrollmentMode(String enrollmentMode) {
		this.enrollmentMode = enrollmentMode;
	}

	/**
	 * @return the keySize
	 */
	public String getKeySize() {
		return keySize;
	}

	/**
	 * @param keySize the keySize to set
	 */
	public void setKeySize(String keySize) {
		this.keySize = keySize;
	}

    /**
     * @return the commonName
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * @param commonName the commonName to set
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
	
}
