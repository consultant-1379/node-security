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

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.cert.issue.CertIssueWfParams;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.ExternalCAEnrollmentInfo;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.IpSecRequestWfsConfiguration;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.entities.NodeCiphers;
import com.ericsson.nms.security.nscs.ldap.entities.NodeSpecificLdapConfiguration;
import com.ericsson.nms.security.nscs.ssh.SSHKeyWfsConfigurationDto;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.Nodes.NodeTrustInfo;
import com.ericsson.nms.security.nscs.trust.distribution.input.xml.TrustedCACertificates;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.dto.WfResult;

@Local
public interface NscsCommandManagerProcessor {

    /**
     * @param extCAEnrollmentInfo
     *            enrollment information
     * @param inputNode
     *            Node object for which enrollment has to be performed
     * @param wfParams
     *            the parameters which need to be sent to certificate issue workflow
     * @param isReissue
     *            check if the request is to do reissue
     * @param revocationReason
     *            in case of reissue, reason for revoking the existing certificate
     * @param jobStatusRecord
     *            workflow job status
     * @param workflowId
     *            id for the workflow called
     * @return WfResult providing the status of the workflow execution
     * @throws NscsServiceException
     *             is thrown when any error occurred while executing the workflow
     */
    public WfResult executeCertificateIssueSingleWf(final ExternalCAEnrollmentInfo extCAEnrollmentInfo, final Node inputNode,
            final CertIssueWfParams wfParams, final boolean isReissue, final String revocationReason, final JobStatusRecord jobStatusRecord,
            final int workflowId) throws NscsServiceException;

    /**
     * @param entry
     * @param revocationReason
     * @param certType
     * @param enrollMode
     * @return
     */
    public WfResult executeCertificateReIssueSingleWf(final Entry<Entity, NodeReference> entry,
            final String revocationReason, final String certType, final JobStatusRecord jobStatusRecord,
            final int workflowId) throws NscsServiceException;

    /**
     * @param entry
     * @param revocationReason
     * @param certType
     * @param enrollMode
     * @return
     */
    public WfResult executeCertificateReIssueSingleWf(final NodeReference entry, final String revocationReason, final String certType,
            final JobStatusRecord jobStatusRecord, final int workflowId);

    /**
     * @param node
     * @param trustCategory
     * @param caName
     * @param jobStatusRecord
     * @param workflowId
     * @param trustedCACertificates
     *            contains the list of TrustedCACertificateInfo and InterfaceIpAddressFdn values.
     * @return
     */
    public WfResult executeTrustDistributeSingleWf(NodeTrustInfo node, String trustCategory, String caName, JobStatusRecord jobStatusRecord,
            int workflowId, TrustedCACertificates trustedCACertificates);

    /**
     * @param validNode
     * @param issuerDn
     * @param serialNumber
     * @param trustCategory
     * @param jobStatusRecord
     * @return
     */
    public WfResult executeTrustRemoveSingleWf(final NodeReference validNode, final String issuerDn, final String serialNumber,
            final String trustCategory, final JobStatusRecord jobStatusRecord, final int workflowId);

    /**
     * @param wfResultList
     */
    public void insertWorkflowBatch(Map<UUID, WfResult> wfResultMap);

    /**
     * This method is used to start WorkflowInstance for Crl check status update for a single node.
     *
     * @param validNodesList
     *            contains list of valid nodes.
     * @param certType
     *            the certificate type value.
     * @param crlCheckStatus
     *            status of CRL Check.
     * @param jobStatusRecord
     *            the CRL check enable/disable jobStatusRecord.
     */
    public WfResult executeCrlCheckWfs(final NodeReference nodeRef, final String certType, final String crlCheckStatus,
            final JobStatusRecord jobStatusRecord, final int workflowId);

    /**
     * This method is to execute CRL download work flow for a single node.
     *
     * @param NodeReference
     *            NodeReference object
     * @param workFlowName
     *            workflow name.
     * @param workFlowParams
     *            list of workFlowParams
     * @param jobStatusRecord
     *            the on demand CRL download jobStatusRecord.
     */
    public WfResult executeCrlDownload(final NodeReference nodeRef, final Map<String, Object> workFlowParams,
            final JobStatusRecord jobStatusRecord, final int workflowId);

    /**
     * This method is to execute set ciphers work flow for a single node.
     *
     * @param nodeRef
     *            NodeReference object
     * @param nodeCiphers
     *            Object of type NodeCiphers
     * @param jobStatusRecord
     *            job id value which associated with set ciphers work flow progress info.
     * @return Object of type WfResult
     */
    public WfResult executeSetCiphersSingleWf(final NodeReference nodeRef, final NodeCiphers nodeCiphers,
            final JobStatusRecord jobStatusRecord, final int workflowId);

    /**
     * This method is to execute activate HTTPS workflow for a single node.
     *
     * @param nodeRef
     *            NodeReference object
     * @param jobStatusRecord
     *            job id value which associated with activate HTTPS workflow progress info.
     * @return Object of type WfResult
     */
    public WfResult executeActivateHttpsWfs(final NodeReference nodeRef,final CertIssueWfParams wfParams,final boolean isReissue,
                                            final String revocationReason, final JobStatusRecord jobStatusRecord,final int workflowId);
    /**
     * This method is to execute deactivate HTTPS workflow for a single node.
     *
     * @param nodeRef
     *            NodeReference object
     * @param jobStatusRecord
     *            job id value which associated with deactivate HTTPS workflow progress info.
     * @return Object of type WfResult
     */
    public WfResult executeDeactivateHttpsWfs(final NodeReference nodeRef, final JobStatusRecord jobStatusRecord,
            final int workflowId);

    /**
     * This method is to execute get HTTPS status workflow for a single node.
     *
     * @param nodeRef
     *            NodeReference object
     * @param jobStatusRecord
     *             job id value which associated with get HTTPS status workflow progress info.
     * @return Object of type WfResult
     */
    public WfResult executeGetHttpsStatusWfs(final NodeReference nodeRef, final JobStatusRecord jobStatusRecord,
                                             final int workflowId);

     /**
     * This method is to execute activate FTPES workflow for a single node.
     *
     * @param nodeReference
     *            NodeReference object
     * @param jobStatusRecord
     *             job id value which associated with activate FTPES workflow progress info.
     * @return Object of type WfResult
     */
     WfResult executeActivateFtpesWfs(final NodeReference nodeReference, final CertIssueWfParams wfParams, final boolean isReissue,
                                             final String revocationReason, final JobStatusRecord jobStatusRecord, final int workflowId);


    /**
     * This method is to execute deactivate FTPES workflow for a single node.
     *
     * @param nodeRef
     *            NodeReference object
     * @param jobStatusRecord
     *            job id value which associated with deactivate FTPES workflow progress info.
     * @return Object of type WfResult
     */
     WfResult executeDeactivateFtpesWfs(final NodeReference nodeRef, final JobStatusRecord jobStatusRecord, final int workflowId);

     /**
      * Execute old-style LDAP configure/reconfigure workflow for a single node.
      * 
      * @param node
      *            the node.
      * @param jobStatusRecord
      *            job status record associated with LDAP workflow progress info.
      * @param workflowId
      *            the workflow index in the job.
      * @return the workflow result.
      */
     WfResult executeConfigureLdapWfs(final NodeSpecificLdapConfiguration node, final JobStatusRecord jobStatusRecord, final int workflowId);

    /**
     * Execute LDAP renew workflow for a single node.
     * 
     * This workflow is designed to be open to manage also the LDAP configure/reconfigure command even if the old-style workflows are still used for
     * them.
     * 
     * @param node
     *            the node.
     * @param jobStatusRecord
     *            job status record associated with LDAP workflow progress info.
     * @param workflowId
     *            the workflow index in the job.
     * @param isRenew
     *            true if LDAP renew is requested, false otherwise.
     * @return
     */
    WfResult executeLdapWf(final NodeSpecificLdapConfiguration node, final JobStatusRecord jobStatusRecord, final int workflowId,
            final boolean isRenew);

    /**
     * @param nodeRef
     *            the node reference
     * @param jobStatusRecord
     *            workflow job status
     * @param workflowId
     *            id for the workflow called
     * @return WfResult providing the status of the workflow execution
     * @throws NscsServiceException
     *             is thrown when any error occurred while executing the workflow
     */
    WfResult executeTestSingleWf(final NodeReference nodeRef, final JobStatusRecord jobStatusRecord, final int workflowId);


    /**
     *
     * @param nodeRef
     *          the node reference
     * @param ipSecRequestWfsConfiguration
     *          ipsec workflow configuration
     * @param jobStatusRecord
     *          workflow job status
     * @param workflowId
     *          id for the workflow called
     * @return WfResult providing the status of the workflow execution
     * @throws NscsServiceException
     *             is thrown when any error occurred while executing the workflow
     */
    WfResult executeIpSecWorkflow(final NodeReference nodeRef, final IpSecRequestWfsConfiguration ipSecRequestWfsConfiguration,
                             final JobStatusRecord jobStatusRecord, final int workflowId);


    /**
     *
     * @param nodeRef
     *          the node reference
     * @param sshKeyRequestWfsConfiguration
     *          ssh key workflow configuration
     * @param jobStatusRecord
     *          workflow job status
     * @param workflowId
     *          id for the workflow called
     * @return WfResult providing the status of the workflow execution
     * @throws NscsServiceException
     *             is thrown when any error occurred while executing the workflow
     */
    WfResult executeSshKeyWorkflow(final NodeReference nodeRef, final SSHKeyWfsConfigurationDto sshKeyRequestWfsConfiguration,
                                  final JobStatusRecord jobStatusRecord, final int workflowId);

}
