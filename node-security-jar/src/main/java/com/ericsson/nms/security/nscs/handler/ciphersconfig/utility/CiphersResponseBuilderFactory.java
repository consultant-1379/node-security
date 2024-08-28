/*------------------------------------------------------------------------------
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

package com.ericsson.nms.security.nscs.handler.ciphersconfig.utility;

import javax.inject.Inject;

public class CiphersResponseBuilderFactory {

    @Inject
    private CiphersSshProtocolManager sshProtocolManager;

    @Inject
    private CiphersTlsProtocolManager tlsProtocolManager;

    public CiphersProtocolManager getCiphersmanager(final String protocol) {
        if (CiphersConstants.PROTOCOL_TYPE_TLS.equalsIgnoreCase(protocol)) {
            return tlsProtocolManager;
        } else {
            return sshProtocolManager;
        }
    }
}
