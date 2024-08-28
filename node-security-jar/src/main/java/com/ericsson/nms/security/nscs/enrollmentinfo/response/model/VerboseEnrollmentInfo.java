/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.enrollmentinfo.response.model;

import java.io.Serializable;

import javax.xml.bind.annotation.*;

/**
 * <p>
 *  This class holds the information about Verbose Enrollment Info.
 * </p>
 *
 * @author xkihari
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verboseEnrollmentInfo")
public class VerboseEnrollmentInfo implements Serializable {

	private static final long serialVersionUID = 8404176232354374411L;

	protected String certificateType;

	@XmlElement(required = true)
	protected EnrollmentCmpConfig enrollmentCmpConfig;

	@XmlElement(required = true)
	protected TrustedCertificates trustedCertificates;

	@XmlElement(required = true)
	protected TrustCategories trustCategories;

	/**
	 * @return the certificateType
	 */
	public String getCertificateType() {
		return certificateType;
	}

	/**
	 * @param certificateType
	 *        The certificateType to set
	 */
	public void setCertificateType(final String certificateType) {
		this.certificateType = certificateType;
	}

	/**
	 * @return the enrollmentCmpConfig
	 */
	public EnrollmentCmpConfig getEnrollmentCmpConfig() {
		return enrollmentCmpConfig;
	}

	/**
	 * @param enrollmentCmpConfig
	 *        The enrollmentCmpConfig to set.
	 */
	public void setEnrollmentCmpConfig(
			final EnrollmentCmpConfig enrollmentCmpConfig) {
		this.enrollmentCmpConfig = enrollmentCmpConfig;
	}

	/**
	 * @return the trustedCertificates
	 */
	public TrustedCertificates getTrustedCertificates() {
		return trustedCertificates;
	}

	/**
	 * @param trustedCertificates
	 *        The trustedCertificates to set.
	 */
	public void setTrustedCertificates(
			final TrustedCertificates trustedCertificates) {
		this.trustedCertificates = trustedCertificates;
	}

	/**
	 * @return the trustCategories
	 */
	public TrustCategories getTrustCategories() {
		return trustCategories;
	}

	/**
	 * @param trustCategories
	 *        The trustCategories to set.
	 */
	public void setTrustCategories(final TrustCategories trustCategories) {
		this.trustCategories = trustCategories;
	}
}
