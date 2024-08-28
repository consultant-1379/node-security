/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.command.enrollmentinfo;

import java.io.Serializable;
import java.util.Objects;

/**
 * Auxiliary class containing OTP configuration parameters.
 */
public class OtpConfigurationParameters implements Serializable {

    private static final long serialVersionUID = -725693492177389326L;

    private Integer otpCount;
    private Integer otpValidityPeriodInMinutes;

    /**
     * @param otpCount
     *            the OTP count.
     * @param otpValidityPeriodInMinutes
     *            the OTP validity period in minutes.
     */
    public OtpConfigurationParameters(final Integer otpCount, final Integer otpValidityPeriodInMinutes) {
        super();
        this.otpCount = otpCount;
        this.otpValidityPeriodInMinutes = otpValidityPeriodInMinutes;
    }

    /**
     * @return the otpCount
     */
    public Integer getOtpCount() {
        return otpCount;
    }

    /**
     * @return the otpValidityPeriodInMinutes
     */
    public Integer getOtpValidityPeriodInMinutes() {
        return otpValidityPeriodInMinutes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(otpCount, otpValidityPeriodInMinutes);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OtpConfigurationParameters other = (OtpConfigurationParameters) obj;
        return Objects.equals(otpCount, other.otpCount) && Objects.equals(otpValidityPeriodInMinutes, other.otpValidityPeriodInMinutes);
    }

}
