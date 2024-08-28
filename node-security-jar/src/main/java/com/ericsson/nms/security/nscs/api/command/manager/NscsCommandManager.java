/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.NodeEnrollmentDetails;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.IpSecRequest;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.TrustedCAInformation;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CipherJobInfo;
import com.ericsson.nms.security.nscs.ldap.entities.NodeSpecificLdapConfiguration;
import com.ericsson.nms.security.nscs.ssh.SSHKeyRequestDto;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.dto.JobStatusRecord;

public interface NscsCommandManager {

    /**
     * @param certType
     * @return
     */
    boolean validateCertTypeValue(String certType);

    /**
     * @param validNodesList
     * @param enrollmentValuesErrorMsg
     * @return
     */
    boolean isEnrollmentModeSupportedForNodeList(List<Node> validNodesList, List<String> enrollmentValuesErrorMsg);

    /**
     * @param xmlNodeList
     * @param certType
     * @param validNodesList
     * @param invalidNodesErrorMap
     * @param invalidDynamicNodesMap
     * @return
     */
    boolean validateNodesForCertificateIssue(List<Node> xmlNodeList, String certType, List<Node> validNodesList,
            Map<NodeReference, NscsServiceException> invalidNodesErrorMap,
            Map<String, String[]> invalidDynamicNodesMap);

    /**
     * This method executes the certificate issue workflow for each Node using the NodeEnrollmentDetails
     *
     * @param nodeEnrollmentDetailsList
     *            list of node Enrollment Details
     * @param certType
     *            certificate type
     * @param jobStatusRecord
     *            job status of the work flow initiated for a node
     */
    void executeCertificateIssueWfs(final List<NodeEnrollmentDetails> nodeEnrollmentDetailsList, final String certType,
            final JobStatusRecord jobStatusRecord);

    /**
     * @return
     */
    List<String> getValidCertificateTypes();

    /**
     * @param inputNodes
     * @param certType
     * @param validNodesList
     * @param invalidNodesErrorMap
     * @param invalidDynamicNodesMap
     * @return
     */
    boolean validateNodesForCrlCheck(List<NodeReference> inputNodes, String certType,
            List<NodeReference> validNodesList, Map<NodeReference, NscsServiceException> invalidNodesErrorMap,
            Map<String, String[]> invalidDynamicNodesMap, Boolean isReadCmd);

    /**
     * This method is used to start the workflow to activate or deactivate CRL check on a node.
     *
     * @param validNodesList
     *            contains list of valid nodes.
     * @param certType
     *            the certificate type value.
     * @param crlCheckStatus
     *            CRL check status ACTIVATED/DEACTIVATED
     * @param jobStatusRecord
     *            the CRL check enable/disable jobStatusRecord.
     */
    void executeCrlCheckWfs(List<NodeReference> validNodesList, String certType, String crlCheckStatus,
            JobStatusRecord jobStatusRecord);

    /**
     * @param getCertEnrollmentStateCommand
     * @param certType
     * @param inputNodes
     * @param validNodesList
     * @param invalidNodesErrorMap
     * @param invalidDynamicNodesMap
     * @return
     */
    boolean validateNodesGetCertEnrollTrustInstallState(String getCertEnrollmentStateCommand, String certType,
            List<NodeReference> inputNodes, List<NodeReference> validNodesList,
            Map<NodeReference, NscsServiceException> invalidNodesErrorMap,
            Map<String, String[]> invalidDynamicNodesMap);

    /**
     * @param validNodesList
     * @param issuerDn
     * @param serialNumber
     * @param certType
     * @param jobStatusRecord
     */
    void executeTrustRemoveWfs(final List<NodeReference> validNodesList, final String issuerDn,
            final String serialNumber, final String certType, final JobStatusRecord jobStatusRecord);

    /**
     * @param entityNodeName
     * @return
     */
    boolean isNodePresent(String entityNodeName);

    /**
     * @param associatedNodesEntity
     * @param dummyList
     * @param validNodes
     * @param blockingErrors
     * @param nonBlockingErrors
     * @return
     */
    boolean validateNodesForCertificateReissue(Map<Entity, NodeReference> associatedNodesEntity, List<Entity> dummyList,
            Map<Entity, NodeReference> validNodes, Map<Entity, NscsServiceException> blockingErrors,
            Map<String, String[]> nonBlockingErrors);

    /**
     * @param validNodesForOam
     * @param inputReason
     * @param name
     * @param jobStatusRecord
     */
    void executeCertificateReissueWfs(final Map<Entity, NodeReference> validNodesForOam, final String inputReason,
            final String name, final JobStatusRecord jobStatusRecord);

    /**
     * @param validNodes
     * @param inputReason
     * @param name
     * @param jobStatusRecord
     */
    void executeCertificateReissueWfs(final List<NodeReference> validNodes, final String inputReason, final String name,
            final JobStatusRecord jobStatusRecord);

    /**
     * @param inputNodes
     * @return
     */
    Set<String> validateDuplicatedNodes(List<NodeReference> inputNodes);

    /**
     * @param validCategoryEntities
     * @param inputCertType
     * @param inputNodes
     * @param validEntityNodesMap
     * @param blockingErrors
     * @param nonBlockingErrors
     * @return
     */
    boolean validateNodesWithEntitiesForCertificateReissue(List<Entity> validCategoryEntities, String inputCertType,
            List<NodeReference> inputNodes, Map<Entity, NodeReference> validEntityNodesMap,
            Map<NodeReference, NscsServiceException> blockingErrors, Map<String, String[]> nonBlockingErrors);

    /**
     * @param inputCertType
     * @param inputNodes
     * @param validNodesList
     * @param blockingErrors
     * @param nonBlockingErrors
     * @return
     */
    boolean validateNodesWithEntitiesForCertificateReissue(String inputCertType, List<NodeReference> inputNodes,
            List<NodeReference> validNodesList, Map<NodeReference, NscsServiceException> blockingErrors,
            Map<String, String[]> nonBlockingErrors);

    /**
     * @param reason
     * @return
     */
    boolean validateReasonValue(String reason);

    /**
     * @param trustCategory
     * @param emptyCaName
     * @param jobStatusRecord
     * @param trustedCAInformationlist
     *            contains the list of TrustedCAInformation values which contains valid nodes and TrustedCACertificates details and this
     *            TrustedCAInformation is mutually exclusive with the validNodesList parameter .
     */
    void executeTrustDistributeWfs(final String trustCategory, final String emptyCaName, final JobStatusRecord jobStatusRecord,
            final List<TrustedCAInformation> trustedCAInformationlist);

    //TODO This method name will be changed to validateNodesForOnDemandCrlDownload as part of TORF-180866
    /**
     * This method is used to validate the given nodes for on demand CRL download command.If all of the given input
     * nodes are valid then this method will return true.If any one of the given node is invalid then this method will
     * return false.
     *
     * @param inputNodes
     *            is the list of NodeReference values
     * @param validNodesList
     *            Only valid nodes are added to this list.
     * @param invalidNodesErrorMap
     *            All invalid nodes are added to this map.
     * @param invalidDynamicNodesMap
     *            Only unsynchronized nodes are added to this map.
     *
     * @return {@link Boolean}
     *         <p>
     *         true: if all nodes are valid.
     *         </p>
     *         false: if any one of the given node is invalid.
     *
     */
    boolean validateNodesForOnDemandCrlDownload(List<NodeReference> inputNodes, List<NodeReference> validNodesList,
            Map<NodeReference, NscsServiceException> invalidNodesErrorMap,
            Map<String, String[]> invalidDynamicNodesMap);

    /**
     * This method is used to start WorkflowInstance for on demand CRL download operation for nodes.
     *
     * @param validNodesList
     *            contains list of valid nodes.
     * @param jobStatusRecord
     *            the CRL check enable/disable jobStatusRecord.
     * @throws NscsServiceException
     *             this exception will be thrown in case of workflow failure.
     */

    void executeOnDemandCrlDownloadWfs(List<NodeReference> validNodesList, JobStatusRecord jobStatusRecord);

    /**
     * This method is used to start WorkflowInstance for set ciphers operation for nodes.
     *
     * @param cipherJobList
     *            is the list of CipherJobInfo values
     * @param jobStatusRecord
     *            job id value which associated with set ciphers work flow progress info.
     */
    void executeSetCiphersWfs(final List<CipherJobInfo> cipherJobList, JobStatusRecord jobStatusRecord);

    /**
     * This method is used to start WorkflowInstance for activate HTTPS operation for nodes.
     * 
     * @param nodes
     *            contains list of nodes.
     * @param jobStatusRecord
     *            job id value which associated with activate HTTPS workflow progress info.
     */
    void executeActivateHttpsWfs(final List<NodeReference> nodes, final JobStatusRecord jobStatusRecord);

    /**
     * This method is used to start WorkflowInstance for deactivate HTTPS operation for nodes.
     *
     * @param nodes
     *            contains list of nodes.
     * @param jobStatusRecord
     *            job id value which associated with deactivate HTTPS workflow progress info.
     */
    void executeDeactivateHttpsWfs(final List<NodeReference> nodes, final JobStatusRecord jobStatusRecord);

    /**
     * This method is used to start WorkflowInstance for get HTTPS status for unsynchronized nodes.
     *
     * @param nodes
     *            contains list of nodes.
     * @param jobStatusRecord
     *            job id value which associated with get HTTPS status workflow progress info.
     */
    void executeGetHttpsStatusWfs(final List<NodeReference> nodes, final JobStatusRecord jobStatusRecord);

    /**
     * This method is used to start WorkflowInstance for activate FTPES operation for nodes.
     *
     * @param nodes
     *            contains list of nodes.
     * @param jobStatusRecord
     *            job id value which associated with activate FTPES workflow progress info.
     */
    void executeActivateFtpesWfs(final List<NodeReference> nodes, final JobStatusRecord jobStatusRecord);

    /**
     * This method is used to start WorkflowInstance for deactivate FTPES operation for nodes.
     *
     * @param nodes
     *            contains list of nodes.
     * @param jobStatusRecord
     *            job id value which associated with deactivate FTPES workflow progress info.
     */
    void executeDeactivateFtpesWfs(final List<NodeReference> nodes, final JobStatusRecord jobStatusRecord);

    /**
     * This method is used to start WorkflowInstance for configure LDAP operation for nodes.
     *
     * @param nodes
     *            contains list of nodes.
     * @param jobStatusRecord
     *            job id value which associated with configure LDAP workflow progress info.
     */
    void executeConfigureLdapWfs(final List<NodeSpecificLdapConfiguration> nodes, final JobStatusRecord jobStatusRecord);

    /**
     * This method is used to start WorkflowInstance for reconfigure LDAP operation for nodes.
     *
     * @param nodes
     *            contains list of nodes.
     * @param jobStatusRecord
     *            job id value which associated with reconfigure LDAP workflow progress info.
     */
    void executeReconfigureLdapWfs(final List<NodeSpecificLdapConfiguration> nodes, final JobStatusRecord jobStatusRecord);

    /**
     * Start WorkflowInstance for renew LDAP operation for nodes.
     *
     * @param nodes
     *            contains list of nodes.
     * @param jobStatusRecord
     *            job id value which associated with renew LDAP workflow progress info.
     */
    void executeRenewLdapWfs(final List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfiguration, final JobStatusRecord jobStatusRecord);

    /**
     * This method is used to start WorkflowInstance for testing the workflows.
     *
     * @param numWorkflows
     *            the number of workflows to be started
     * @param jobStatusRecord
     *            the job associated with Test workflows progress
     */
    void executeTestWfs(final int numWorkflows, final JobStatusRecord jobStatusRecord);


    /**
     * This method is used to start WorkflowInstance for ipsec workflows.
     * @param requests
     *         the configuration for the workflows
     * @param jobStatusRecord
     *         the job associated with ipsec workflows progress
     */
    void executeIpSecWorkflows(final List<IpSecRequest> requests, final JobStatusRecord jobStatusRecord);


    /**
     * This method is used to start WorkflowInstance for ssh key  workflows.
     * @param requests
     *         the configuration for the workflows
     * @param jobStatusRecord
     *         the job associated with ipsec workflows progress
     */
    void executeSshKeyWorkflows(final List<SSHKeyRequestDto> requests, final JobStatusRecord jobStatusRecord);

}
