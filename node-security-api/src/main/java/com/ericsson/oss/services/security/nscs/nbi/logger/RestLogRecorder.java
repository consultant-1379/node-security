/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi.logger;

import java.io.Serializable;

/**
 * Auxiliary class to record all parameters needed to log a REST operation.
 */
public class RestLogRecorder extends GenericLogRecorder implements Serializable {

    private static final long serialVersionUID = -240297606694916960L;

    private String method;
    private String urlFile;
    private String urlPath;
    private String requestPayload;
    private String jobId;
    private CompactAuditLogAdditionalInfo additionalInfo;

    public RestLogRecorder() {
        super();
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setMethod(final String method) {
        this.method = method;
    }

    /**
     * @return the urlFile
     */
    public String getUrlFile() {
        return urlFile;
    }

    /**
     * @param urlFile
     *            the urlFile to set
     */
    public void setUrlFile(final String urlFile) {
        this.urlFile = urlFile;
    }

    /**
     * @return the urlPath
     */
    public String getUrlPath() {
        return urlPath;
    }

    /**
     * @param urlPath
     *            the urlPath to set
     */
    public void setUrlPath(final String urlPath) {
        this.urlPath = urlPath;
    }

    /**
     * @return the requestPayload
     */
    public String getRequestPayload() {
        return requestPayload;
    }

    /**
     * @param requestPayload
     *            the requestPayload to set
     */
    public void setRequestPayload(final String requestPayload) {
        this.requestPayload = requestPayload;
    }

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId
     *            the jobId to set
     */
    public void setJobId(final String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the additionalInfo
     */
    public CompactAuditLogAdditionalInfo getAdditionalInfo() {
        if (additionalInfo == null) {
            additionalInfo = new CompactAuditLogAdditionalInfo();
        }
        return additionalInfo;
    }

}
