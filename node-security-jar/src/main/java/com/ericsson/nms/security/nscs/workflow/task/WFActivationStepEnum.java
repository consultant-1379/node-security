/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task;

/**
 * 
 * This enum contains the activation steps of each workflow subscript involved in the SL2 and IPSEC activation/deactivation
 * 
 */
public enum WFActivationStepEnum {
    ActivateSecuredCorba("Activate secured corba"), InstallTrustedCertificates("Install trusted certificates"), InitializeCertificateEnrollment("Initialize certificate enrollment"), ActivateIpSec(
            "Activate IpSec"), InstallTrustedCertificatesIpsec("Install trusted certificates Ipsec"), InitializeCertificateEnrollmentIpSec("Initialize certificate enrollment IpSec"), DeactivateIpSec(
                    "Deactivate IpSec"), DeActivateCorbaSecurity("DeActivate Corba Security"), CPP_Issue_certificate("CPP Issue certificate"), CPP_Issue_Trusted_Certificates(
                            "CPP Issue Trusted Certificates"), COM_ECIM_Issue_Certificate("COM ECIM Issue certificate"), COM_ECIM_Revoke_certificate("COM ECIM Revoke certificate"), Revoke_certificate(
                                    "Revoke certificate"), UNKNOWN("Unknown");

    private final String name;

    private WFActivationStepEnum(final String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static WFActivationStepEnum getActivationStep(final String name) {
        for (final WFActivationStepEnum activationStep : WFActivationStepEnum.values()) {
            if (activationStep.getName().equals(name)) {
                return activationStep;
            }
        }
        return WFActivationStepEnum.UNKNOWN;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
