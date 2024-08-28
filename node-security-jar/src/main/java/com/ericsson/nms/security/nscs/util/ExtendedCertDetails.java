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
package com.ericsson.nms.security.nscs.util;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.OamTrustCategory;

/**
 * Utility class containing generic certificate info.
 * 
 */
public class ExtendedCertDetails extends CertDetails {

    private static final String CERTIFICATE_SUBJECT_ALT_NAME = "subjectAltName";

    private final String subjectAltName;

    /**
     * @param certDetails
     */
    public ExtendedCertDetails(final Map<String, Object> certDetails) {
        super(certDetails);
        this.subjectAltName = (String) certDetails.get(CERTIFICATE_SUBJECT_ALT_NAME);
    }

    /**
     * @return the subjectAltName
     */
    public String getSubjectAltName() {
        return subjectAltName;
    }

    /**
     * Factory method to create an instance of extended certificate details
     * containing the given values.
     * 
     * @param issuer
     * @param serial
     * @param subject
     * @param subjectAltName
     * @return
     */
    public static CertDetails certDetailsFactory(final String issuer, final String serial, final String subject, final String subjectAltName) {
        Map<String, Object> certDetails = new HashMap<String, Object>();
        certDetails.put(CERTIFICATE_ISSUER, issuer);
        certDetails.put(CERTIFICATE_SERIAL, serial);
        certDetails.put(CERTIFICATE_SUBJECT, subject);
        certDetails.put(CERTIFICATE_SUBJECT_ALT_NAME, subjectAltName);
        return new ExtendedCertDetails(certDetails);
    }

    
    
    public static CertDetails certDetailsFactory(final String issuer, final String serial, final String subject, final String subjectAltName,final String category) {
        Map<String, Object> certDetails = new HashMap<String, Object>();
        certDetails.put(CERTIFICATE_ISSUER, issuer);
        certDetails.put(CERTIFICATE_SERIAL, serial);
        certDetails.put(CERTIFICATE_SUBJECT, subject);
        certDetails.put(CERTIFICATE_SUBJECT_ALT_NAME, subjectAltName);
        certDetails.put(CERTFICATE_CATEGORY, category);
        return new ExtendedCertDetails(certDetails);
    }
    /**
     * Factory method to create a default empty instance of extended certificate
     * details.
     * 
     * @param issuer
     * @param serial
     * @param subject
     * @param subjectAltName
     * @return
     */
    public static CertDetails certDetailsFactory() {
        Map<String, Object> certDetails = new HashMap<String, Object>();
        return new ExtendedCertDetails(certDetails);
    }
}