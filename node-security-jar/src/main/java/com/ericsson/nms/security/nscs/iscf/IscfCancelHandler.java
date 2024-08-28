package com.ericsson.nms.security.nscs.iscf;

import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import java.util.logging.Level;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * Cancels IscfSCEP Enrollment for a node, both Ipsec and Corba
 * Used to cancel <code>IscfServiceBean</code> generate()
 *
 * @author emacgma
 * Date: 08/07/14
 *
 */
public class IscfCancelHandler {

    @Inject
    private Logger log;

    @Inject
    private CppSecurityService cpp;

    /**
     * Cancels SCEPEnrollment (both Corba and Ipsec)of a node
     *
     * @param fdn Name of the node to cancel the
     */
    public void cancel(final String fdn) {
        log.debug("Cancelling ISCF SCEP Enrollment data for {}", fdn);
        try {
            cpp.cancelSCEPEnrollment(fdn);
        } catch (CppSecurityServiceException ex) {
            log.error("Error calling cancelSCEPEnrollment()");
        }
    }
}
