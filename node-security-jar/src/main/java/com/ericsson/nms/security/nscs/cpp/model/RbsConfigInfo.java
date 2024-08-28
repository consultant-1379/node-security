package com.ericsson.nms.security.nscs.cpp.model;

import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;

import java.io.Serializable;

/**
 * Used to call MO action on RBS configuration.
 * 
 * @author ediniku
 */
public class RbsConfigInfo implements Serializable {

    private static final long serialVersionUID = -8834355946339141738L;

    private final String smrsUserId;
    private final String smrsPassword;
    private final String summaryFileUrl;
    private final String summaryFileHash;
    private final String rbsIntegrationCode;
    private final String smrsHostAddress;

    /**
     * Constructs {@link RbsConfigInfo}.
     * 
     * @param smrsHostAddress
     *            SMRS host address.
     * @param smrsUserId
     *            SMRS user ID.
     * @param smrsPassword
     *            SMRS password.
     * @param summaryFileUrl
     *            summary file URL
     * @param summaryFileHash
     *            summary file hashcode
     * @param rbsIntegrationCode
     *            RBS integration code.
     */
    public RbsConfigInfo(final String smrsHostAddress, final String smrsUserId, final String smrsPassword, final String summaryFileUrl,
                         final String summaryFileHash, final String rbsIntegrationCode) {
        this.smrsHostAddress = smrsHostAddress;
        this.smrsUserId = smrsUserId;
        this.smrsPassword = smrsPassword;
        this.summaryFileUrl = summaryFileUrl;
        this.summaryFileHash = summaryFileHash;
        this.rbsIntegrationCode = rbsIntegrationCode;
    }

    /**
     * Gets SMRS host address.
     * 
     * @return SMRS host address.
     */
    public String getSmrsHostAddress() {
        return this.smrsHostAddress;
    }

    /**
     * Gets SMRS user ID.
     * 
     * @return SMRS user ID.
     */
    public String getSmrsUserId() {
        return this.smrsUserId;
    }

    /**
     * Gets SMRS password.
     * 
     * @return SMRS password.
     */
    public String getSmrsPassword() {
        return this.smrsPassword;
    }

    /**
     * Gets summary file hashcode.
     * 
     * @return summary file hashcode.
     */
    public String getSummaryFileHash() {
        return this.summaryFileHash;
    }

    /**
     * Gets summary file URL.
     * 
     * @return summary file URL.
     */

    public String getSummaryFileUrl() {
        return this.summaryFileUrl;
    }

    /**
     * Gets RBS integration code.
     * 
     * @return RBS integration code.
     */
    public String getRbsIntegrationCode() {
        return this.rbsIntegrationCode;
    }

    /**
     * Gets all properties as MO parameters.
     * 
     * @return MO parameters.
     */
    public MoParams toMoParams() {
        return toMoParams(getSmrsUserId(), getSmrsPassword(), getSummaryFileUrl(), getSummaryFileHash(), getRbsIntegrationCode());
    }

    /**
     * Returns the MoParams representation of the supplied values.
     * 
     * @param smrsUserId
     *            SMRS user id.
     * @param smrsPassword
     *            SMRS password.
     * @param summaryFileUrl
     *            URL for summary file.
     * @param summaryFileHash
     *            hash code of summary file.
     * @param rbsIntegrationCode
     *            integration code for RBS.
     * @return object for MO action parameter.
     */
    public static MoParams toMoParams(final String smrsUserId, final String smrsPassword, final String summaryFileUrl, final String summaryFileHash,
                                      final String rbsIntegrationCode) {
        final MoParams params = new MoParams();
        params.addParam("smrsUserId", smrsUserId);
        params.addParam("smrsPassword", smrsPassword);
        params.addParam("summaryFileUrl", summaryFileUrl);
        params.addParam("summaryFileHash", summaryFileHash);
        params.addParam("rbsIntegrationCode", rbsIntegrationCode);
        return params;
    }
}