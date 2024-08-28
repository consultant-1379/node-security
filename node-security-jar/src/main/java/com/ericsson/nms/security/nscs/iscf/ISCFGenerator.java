/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.iscf;

import com.ericsson.nms.security.nscs.api.iscf.IscfResponse;

/**
 * Generates ISCF XML content for auto integration of nodes. Supported use cases are
 *
 * <ul>
 *     <li>Security Level 2</li>
 *     <li>IPSec Traffic</li>
 *     <li>IPSec O&M</li>
 *     <li>IPSec Traffic and IPSec O&M</li>
 *     <li>Security Level2, IPSec Traffic, and IPSec O&M</li>
 * </ul>
 *
 * @author ealemca
 */
interface IscfGenerator {

    /**
     * Generate ISCF XML content, an RBS Integrity Code (RIC)
     * and a Security Configuration Checksum (SCC)
     *
     * @return IscfResponse A response object containing the XML content,
     *         the RIC and the SCC
     * @throws IscfServiceException
     */
    IscfResponse generate();

}
