/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2018
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.cucumber.test.steps.helper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class NscsTargetDefinition extends NscsTarget implements Serializable {

    private static final long serialVersionUID = -3729016656772175101L;
    private String platform;
    // releases for the supported target model identities
    private Map<String, List<String>> targetReleases;

    public NscsTargetDefinition() {
        super();
    }

    /**
     *
     * @param targetCategory
     *            the target category
     * @param targetType
     *            the target type
     * @param targetModelIdentities
     *            the supported target model identities
     * @param platform
     *            the platform
     * @param targetReleases
     *            the releases for any supported target model identity
     */
    public NscsTargetDefinition(final String targetCategory, final String targetType, final List<String> targetModelIdentities, final String platform,
            final Map<String, List<String>> targetReleases) {
        super(targetCategory, targetType, targetModelIdentities);
        this.platform = platform;
        this.targetReleases = targetReleases;
    }

    /**
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @param platform
     *            the platform to set
     */
    public void setPlatform(final String platform) {
        this.platform = platform;
    }

    /**
     * @return the target releases for any supported target model identity
     */
    public Map<String, List<String>> getTargetReleases() {
        return targetReleases;
    }

    /**
     * @param targetReleases
     *            the targetReleases to set
     */
    public void setTargetReleases(final Map<String, List<String>> targetReleases) {
        this.targetReleases = targetReleases;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((platform == null) ? 0 : platform.hashCode());
        result = prime * result + ((targetReleases == null) ? 0 : targetReleases.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NscsTargetDefinition other = (NscsTargetDefinition) obj;
        if (platform != null && !platform.equals(other.platform)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NscsTargetDefinition [" + super.toString() + " platform=" + platform + ", targetReleases=" + targetReleases + "]";
    }

}
