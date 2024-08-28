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
package com.ericsson.nms.security.nscs.api.instrumentation;

/**
 * Auxiliary enumeration containing the types of monitored entities managed by instrumentation.
 */
public enum NscsMonitoredEntityTypes {
    ISCF,
    CPP_SL2_ACTIVATE,
    CPP_SL2_DEACTIVATE,
    CPP_IPSEC_ACTIVATE,
    CPP_IPSEC_DEACTIVATE,
    SSH_KEY,
    CPP_CERTIFICATE_ENROLLMENT,
    ECIM_CERTIFICATE_ENROLLMENT,
    EOI_CERTIFICATE_ENROLLMENT,
    CPP_TRUST_DISTRIBUTE,
    ECIM_TRUST_DISTRIBUTE,
    EOI_TRUST_DISTRIBUTE,
    CPP_TRUST_REMOVE,
    ECIM_TRUST_REMOVE,
    EOI_TRUST_REMOVE,
    ECIM_LDAP_CONFIGURE,
    EOI_LDAP_CONFIGURE,
    CPP_CRLCHECK,
    ECIM_CRLCHECK,
    CPP_ON_DEMAND_CRL_DOWNLOAD,
    ECIM_ON_DEMAND_CRL_DOWNLOAD,
    SET_CIPHERS,
    CPP_RTSEL_ACTIVATE,
    CPP_RTSEL_DEACTIVATE,
    CPP_RTSEL_DELETE,
    CPP_HTTPS_ACTIVATE,
    CPP_HTTPS_DEACTIVATE,
    CPP_HTTPS_GET,
    ECIM_FTPES_ACTIVATE,
    ECIM_FTPES_DEACTIVATE,
    CPP_LAAD_DISTRIBUTE,
    CPP_NTP_CONFIGURE,
    CPP_NTP_REMOVE,
    COM_CONFIGURE_LDAP,
    COMECIM_NTP_REMOVE,
    // Please add new entries before this comment line
    UNDEFINED
}
