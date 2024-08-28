/*
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.nms.security.nscs.ejb.iscf;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.IscfServiceException;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.IscfResponse;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.IscfService;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import java.net.StandardProtocolFamily;
import java.util.Set;
import javax.ejb.Stateless;

@Stateless
public class IscfServiceDelegateImpl implements IscfServiceDelegate {

    @EServiceRef
    IscfService iscfService;

    @Override
    public IscfResponse generate(String logicalName, String nodeFdn, SecurityLevel wantedSecLevel, 
                                SecurityLevel minimumSecLevel, EnrollmentMode wantedEnrollmentMode, 
                                NodeModelInformation modelInfo) throws IscfServiceException {
        return iscfService.generate(logicalName, nodeFdn, wantedSecLevel, minimumSecLevel, wantedEnrollmentMode, modelInfo);
    }

    @Override
    public IscfResponse generate(String logicalName, String nodeFdn, String ipsecUserLabel, 
                                 SubjectAltNameParam ipsecSubjectAltName, Set<IpsecArea> wantedIpSecAreas, 
                                 EnrollmentMode wantedEnrollmentMode, NodeModelInformation modelInfo) throws IscfServiceException {
        return iscfService.generate(logicalName, nodeFdn, "UserLabel", ipsecSubjectAltName,
                                     wantedIpSecAreas, wantedEnrollmentMode, modelInfo);
    }

    @Override
    public IscfResponse generate(String logicalName, String nodeFdn, SecurityLevel wantedSecLevel, 
                                 SecurityLevel minimumSecLevel, String ipsecUserLabel, 
                                 SubjectAltNameParam ipsecSubjectAltName, Set<IpsecArea> wantedIpSecAreas, 
                                 EnrollmentMode wantedEnrollmentMode, NodeModelInformation modelInfo) throws IscfServiceException {
        return iscfService.generate(logicalName, nodeFdn, wantedSecLevel, minimumSecLevel, "UserLabel", ipsecSubjectAltName,
                    wantedIpSecAreas, wantedEnrollmentMode, modelInfo);
    }

    @Override
    public void cancel(String fdn) throws IscfServiceException {
        iscfService.cancel(fdn);
    }

    @Override
    public SecurityDataResponse generateSecurityDataOam(NodeIdentifier nodeId, EnrollmentMode wantedEnrollmentMode,
                                NodeModelInformation modelInfo, StandardProtocolFamily ipVersion) {
        return iscfService.generateSecurityDataOam(nodeId, wantedEnrollmentMode, modelInfo, ipVersion);
    }

    @Override
    public SecurityDataResponse generateSecurityDataOam(NodeIdentifier nodeId, SubjectAltNameParam subjectAltName,
                                EnrollmentMode wantedEnrollmentMode, NodeModelInformation modelInfo, StandardProtocolFamily ipVersion) {
        return iscfService.generateSecurityDataOam(nodeId, subjectAltName, wantedEnrollmentMode, modelInfo, ipVersion);
    }

    @Override
    public SecurityDataResponse generateSecurityDataIpsec(NodeIdentifier nodeId, SubjectAltNameParam ipsecSubjectAltName,
                                EnrollmentMode wantedEnrollmentMode, NodeModelInformation modelInfo, StandardProtocolFamily ipVersion) {
        return iscfService.generateSecurityDataIpsec(nodeId, ipsecSubjectAltName, wantedEnrollmentMode, modelInfo, ipVersion);
    }

    @Override
    public SecurityDataResponse generateSecurityDataCombo(NodeIdentifier nodeId, SubjectAltNameParam ipsecSubjectAltName,
                                EnrollmentMode wantedEnrollmentMode, NodeModelInformation modelInfo, StandardProtocolFamily ipVersion) {
        return iscfService.generateSecurityDataCombo(nodeId, ipsecSubjectAltName, wantedEnrollmentMode, modelInfo, ipVersion);
    }
}
