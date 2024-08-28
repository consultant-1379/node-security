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
package com.ericsson.nms.security.nscs.data.moget.param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.util.ExtendedCertDetails;

/**
 * Class containing certificate state info related to both certificate
 * enrollment and trust install. In the former case the list of certificates
 * only contains the certificate itself, in the latter case the list of
 * certificates contains the list of trusted certificates.
 * 
 */
public class CertStateInfo {

    public static final String NOT_APPLICABLE = "Not Applicable";
    public static final String NOT_AVAILABLE = "N/A";

    private String nodeName;
    private String state;
    private String errorMsg;
    private List<CertDetails> certificates;

    /**
     * @param nodeName
     */
    public CertStateInfo(String nodeName) {
        if (nodeName != null && !nodeName.isEmpty()) {
            this.nodeName = nodeName;
        } else {
            this.nodeName = NOT_AVAILABLE;
        }
        this.state = NOT_AVAILABLE;
        this.errorMsg = NOT_AVAILABLE;
        this.certificates = Arrays.asList(ExtendedCertDetails.certDetailsFactory());
    }

    /**
     * @param nodeName
     * @param state
     * @param errorMsg
     * @param certDetails
     */
    public CertStateInfo(String nodeName, String state, String errorMsg, CertDetails certDetails) {
        if (nodeName != null && !nodeName.isEmpty()) {
            this.nodeName = nodeName;
        } else {
            this.nodeName = NOT_AVAILABLE;
        }
        if (state != null && !state.isEmpty()) {
            this.state = state;
        } else {
            this.state = NOT_AVAILABLE;
        }
        if (errorMsg != null) {
            this.errorMsg = errorMsg;
        } else {
            this.errorMsg = NOT_AVAILABLE;
        }
        if (certDetails != null) {
            this.certificates = Arrays.asList(certDetails);
        } else {
            this.certificates = Arrays.asList(ExtendedCertDetails.certDetailsFactory());
        }
    }

    /**
     * @param nodeName
     * @param state
     * @param errorMsg
     * @param certificates
     */
    public CertStateInfo(String nodeName, String state, String errorMsg, List<CertDetails> certificates) {
        if (nodeName != null && !nodeName.isEmpty()) {
            this.nodeName = nodeName;
        } else {
            this.nodeName = NOT_AVAILABLE;
        }
        if (state != null && !state.isEmpty()) {
            this.state = state;
        } else {
            this.state = NOT_AVAILABLE;
        }
        if (errorMsg != null) {
            this.errorMsg = errorMsg;
        } else {
            this.errorMsg = NOT_AVAILABLE;
        }
        if (certificates != null) {
            this.certificates = new ArrayList<CertDetails>();
            this.certificates.addAll(certificates);
        } else {
            this.certificates = Arrays.asList(ExtendedCertDetails.certDetailsFactory());
        }
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName
     *            the nodeName to set
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * @param errorMsg
     *            the errorMsg to set
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * @return the certificates
     */
    public List<CertDetails> getCertificates() {
        return certificates;
    }

    /**
     * @param certificates
     *            the certificates to set
     */
    public void setCertificates(List<CertDetails> certificates) {
        this.certificates.clear();
        this.certificates.addAll(certificates);
    }

    /**
     * Return if current certificate state info correspond to a not available
     * instance (e.g. as consequence of a failed read on DPS).
     * 
     * @return
     */
    public boolean isNotAvailable() {
        if (this.certificates == null) {
            return true;
        }
        if (this.certificates.size() == 1) {
            CertDetails certDetails = certificates.iterator().next();
            if (certDetails.isNotAvailable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return if current certificate state info correspond to an invalid
     * instance (e.g. as consequence of a failed read on DPS).
     * 
     * It only applies to certificate (NOT to trusted certificates) since only
     * the first certificate in the certificate list is checked. For this the
     * only serial number is checked!
     * 
     * @return
     */
    public boolean isInvalid() {
        if (this.certificates == null) {
            return true;
        }
        if (this.certificates.size() == 1) {
            CertDetails certDetails = certificates.iterator().next();
            if (certDetails.getSerial() == null) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("name=%s", this.nodeName));
        sb.append(String.format(",state=%s", this.state));
        sb.append(String.format(",error=%s", this.errorMsg));
        sb.append(", certificates={");
        Iterator<CertDetails> it = this.certificates.iterator();
        while (it.hasNext()) {
            CertDetails certDetails = it.next();
            sb.append(String.format("{subject=%s", certDetails.getSubject()));
            sb.append(String.format(",serial=%s", certDetails.getSerial()));
            sb.append(String.format(",issuer=%s", certDetails.getIssuer()));
            if (certDetails instanceof ExtendedCertDetails) {
                sb.append(String.format(",subjectAltName=%s", ((ExtendedCertDetails) certDetails).getSubjectAltName()));
            }
            sb.append("}");
        }
        sb.append("}");

        return sb.toString();
    }
}
