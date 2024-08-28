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
import com.ericsson.oss.itpf.sdk.core.annotation.EService;
import java.net.StandardProtocolFamily;
import java.util.Set;
import javax.ejb.Remote;

@EService
@Remote
public interface IscfServiceDelegate {

     /**
     * @param logicalName BSIM name associated with the NE integration. Used by the NE at integration.
     * @param nodeFdn The FDN of the node undergoing auto-integration
     * @param wantedSecLevel The desired security level the node should be set to after auto-integration
     * @param minimumSecLevel The minimum security level the node should be left at, should there be a
     *                        problem during auto-integration, or LEVEL_NOT_SUPPORTED for COM/ECIM
     * @param wantedEnrollmentMode The desired enrollment mode for the node that will be written in the 
     *                             ISCF file or LEVEL_NOT_SUPPORTED for COM/ECIM
     * @param modelInfo The Node Model Information
     * @return IscfResponse
     * @throws IscfServiceException
     *           - the exception throws by IscfService
     */
    IscfResponse generate(
        String logicalName,
        String nodeFdn,
        SecurityLevel wantedSecLevel,
        SecurityLevel minimumSecLevel,
        EnrollmentMode wantedEnrollmentMode,
        NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
      * @param logicalName BSIM name associated with the NE integration. Used by the NE at integration.
     * @param nodeFdn The FDN of the node undergoing auto-integration
     * @param ipsecUserLabel The user label
     * @param ipsecSubjectAltName the Subject Alternative Name
     * @param wantedIpSecAreas A collection of unique IpsecArea types indicating what areas of IPSec
     *                          is required
     * @param wantedEnrollmentMode The desired enrollment mode for the node that will be written in the 
     *                             ISCF file
     * @param modelInfo The Node Model Information
     * @return IscfResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    IscfResponse generate(
        String logicalName,
        String nodeFdn,
        String ipsecUserLabel,
        SubjectAltNameParam ipsecSubjectAltName,
        Set<IpsecArea> wantedIpSecAreas,
        EnrollmentMode wantedEnrollmentMode,
        NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
     * @param logicalName BSIM name associated with the NE integration. Used by the NE at integration.
     * @param nodeFdn The FDN of the node undergoing auto-integration
     * @param wantedSecLevel The desired security level the node should be set to after auto-integration
     * @param minimumSecLevel The minimum security level the node should be left at should there be a
     *                        problem during auto-integration
     * @param ipsecUserLabel The user label
     * @param ipsecSubjectAltName the Subject Alternative Name
     * @param wantedIpSecAreas A collection of unique IpsecArea types indicating what areas of IPSec
     *                          is required
     * @param wantedEnrollmentMode The desired enrollment mode for the node that will be written in the 
     *                             ISCF file
     * @param modelInfo The Node Model Information
     * @return IscfResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    IscfResponse generate(
        String logicalName,
        String nodeFdn,
        SecurityLevel wantedSecLevel,
        SecurityLevel minimumSecLevel,
        String ipsecUserLabel,
        SubjectAltNameParam ipsecSubjectAltName,
        Set<IpsecArea> wantedIpSecAreas,
        EnrollmentMode wantedEnrollmentMode,
        NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
     * Cleanup needed if auto-integration is canceled for this node. This
     * removes any end entities created in PKI for the node
     *
     * @param fdn The FDN of the node
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    void cancel(String fdn) throws IscfServiceException;

    /**
     * Generate OAM auto-integration Security Data for a node, without Subject Alternative Name.
     * @param nodeId The identifier information of the node undergoing auto-integration
     * @param wantedEnrollmentMode The desired enrollment mode for the node
     * @param modelInfo The Node Model Information
     * @param ipVersion
     *            The IP version (INET / INET6) to select the suitable enrollment server URI. If null, IP version is retrieved from  ConnectivityInformation
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    SecurityDataResponse generateSecurityDataOam(
            NodeIdentifier nodeId,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo,
            StandardProtocolFamily ipVersion
    );

    /**
     * Generate OAM auto-integration Security Data for a node, specifying Subject Alternative Name parameter.
     *
     * @param nodeId
     *            The identifier information of the node undergoing auto-integration.
     * @param subjectAltName
     *            The Subject Alternative Name. Mandatory parameter (must be not null.)
     * @param wantedEnrollmentMode
     *            The desired enrollment mode for the node. If null, default value is got from Capability Model.
     * @param modelInfo
     *            The Node Model Information
     * @param ipVersion
     *            The IP version (INET / INET6) to select the suitable enrollment server URI. If null, IP version is retrieved from  ConnectivityInformation
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    SecurityDataResponse generateSecurityDataOam(
            NodeIdentifier nodeId,
            SubjectAltNameParam subjectAltName,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo,
            StandardProtocolFamily ipVersion
    );

    /**
     * Generate IPSEC auto-integration Security Data for a node
     * @param nodeId The identifier information of the node undergoing auto-integration
     * @param ipsecSubjectAltName The Subject Alternative Name
     * @param wantedEnrollmentMode The desired enrollment mode for the node
     * @param modelInfo The Node Model Information
     * @param ipVersion
     *            The IP version (INET / INET6) to select the suitable enrollment server URI. If null, IP version is retrieved from  ConnectivityInformation
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    SecurityDataResponse generateSecurityDataIpsec(
            NodeIdentifier nodeId,
            SubjectAltNameParam ipsecSubjectAltName,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo,
            StandardProtocolFamily ipVersion
    );

    /**
     * Generate OAM and IPSEC auto-integration Security Data for a node
     * @param nodeId The identifier information of the node undergoing auto-integration
     * @param ipsecSubjectAltName The Subject Alternative Name
     * @param wantedEnrollmentMode The desired enrollment mode for the node
     * @param modelInfo The Node Model Information
     * @param ipVersion
     *            The IP version (INET / INET6) to select the suitable enrollment server URI. If null, IP version is retrieved from  ConnectivityInformation
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    SecurityDataResponse generateSecurityDataCombo(
            NodeIdentifier nodeId,
            SubjectAltNameParam ipsecSubjectAltName,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo,
            StandardProtocolFamily ipVersion
    );
}
