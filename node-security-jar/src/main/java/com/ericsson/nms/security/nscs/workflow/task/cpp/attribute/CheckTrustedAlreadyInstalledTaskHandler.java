/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.laad.service.ResourcesBean;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.utilities.NSCSCertificateUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CheckTrustedAlreadyInstalledTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_CHECK_TRUSTED_ALREADY_INSTALLED
 * </p>
 *
 * @author eanbuzz
 */
@WFTaskType(WorkflowTaskType.CPP_CHECK_TRUSTED_ALREADY_INSTALLED)
@Local(WFTaskHandlerInterface.class)
public class CheckTrustedAlreadyInstalledTaskHandler implements WFQueryTaskHandler<CheckTrustedAlreadyInstalledTask>, WFTaskHandlerInterface {

    private static final String NOT_INSTALLED = "NOT_INSTALLED";
    private static final String INSTALLED = "INSTALLED";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private ResourcesBean resourcesBean;

    @Inject
    private CppSecurityService securityService;

    @Inject
    private NSCSCertificateUtility certificateUtility;

    @Override
    public String processTask(final CheckTrustedAlreadyInstalledTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);
        final CmResponse trustCert = readerService.getMOAttribute(normNode, Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type(),
                Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace(), IpSec.INSTALLED_TRUSTED_CERTIFICATES);
        if (trustCert.getCmObjects().isEmpty()) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Empty results in the CMReader response : " + trustCert.getCmObjects());
            throw new MissingMoAttributeException(node.getFdn(), Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type(),
                    IpSec.INSTALLED_TRUSTED_CERTIFICATES);
        } else if (trustCert.getCmObjects().size() > 1) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Too many results in the CMReader response : " + trustCert.getCmObjects());
            throw new UnexpectedErrorException(String.format("Got too many results (%s) was expecting 1", trustCert.getCmObjects().size()));
        } else {
            // process certs from MO
            final List<Map<String, Object>> maps = (List<Map<String, Object>>) trustCert.getCmObjects().iterator().next().getAttributes()
                    .get(IpSec.INSTALLED_TRUSTED_CERTIFICATES);
            final List<CertDetails> certsMo = certificateUtility.extractDetailsFromMap(maps);
            final String trustedCA = task.getTrustedCertificateAuthority();
            List<CertDetails> certsInput = new ArrayList<>();

            if (trustedCA != null && !trustedCA.isEmpty()) {
                certsInput = fetchTrustCertsFromInputCA(task.getTrustCerts(), trustedCA, node, task);
            } else {
                // process certs from input and extract details
                certsInput = fetchTrustCertsByCategory(TrustedCertCategory.IPSEC.toString(), node, task);
            }
            // all the certs from input MUST exist on MO
            for (final CertDetails detail : certsInput) {
                if (!certsMo.contains(detail)) {
                    return trustNotInstalledOnNode(task.getNodeFdn(), task);
                }
            }
            nscsLogger.workFlowTaskHandlerOngoing(task, "Action Ongoing for node: " + node.getFdn());
            // MO contains all certs from input
            return trustAlreadyInstalledOnNode(task.getNodeFdn(), task);
        }
    }

    private String trustAlreadyInstalledOnNode(final String nodeFdn, final CheckTrustedAlreadyInstalledTask task) {
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task,
                "IPsec [" + nodeFdn + "] : Trusted certificate already installed on node with Success", NscsLogger.ALREADY_INSTALLED);
        return INSTALLED;
    }

    private String trustNotInstalledOnNode(final String nodeFdn, final CheckTrustedAlreadyInstalledTask task) {
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "IPsec [" + nodeFdn + "] : Trusted certificate is not installed on node");
        return NOT_INSTALLED;
    }

    /**
     * Process the input information and extract the certs from it.
     *
     * @param trustParam
     *            {@link String} that can be a file path of certs (ie .pem file) or name of the trust category over pki-manager.
     * @return list of certificates details.
     */
    private List<CertDetails> fetchTrustCertsFromInput(final String trustParam, final NodeReference nodeRef,
            final CheckTrustedAlreadyInstalledTask task) {
        final List<CertDetails> certs = new ArrayList<>();
        try {
            certs.addAll(fetchTrustCertsByFile(trustParam));
        } catch (FileNotFoundException | CertificateException e) {
            nscsLogger.info("Could not read certificates from source file " + trustParam);
            certs.addAll(fetchTrustCertsByCategory(trustParam, nodeRef, task));
        }
        return certs;
    }

    /**
     * Process the input information and extract the certs from it.
     *
     * @param trustedCA
     *            {@link String} that is the name of the trusted Certificate Authority.
     * @return list of certificates details.
     */
    private List<CertDetails> fetchTrustCertsFromInputCA(final String category, final String trustedCA, final NodeReference nodeRef,
            final CheckTrustedAlreadyInstalledTask task) {
        nscsLogger.info("fetching TrustCerts from data: trustedCA [" + trustedCA + "], FDN [" + nodeRef.getFdn() + "]");
        final List<CertDetails> certs = new ArrayList<>();
        try {
            certs.addAll(fetchTrustCertsByTrustedCA(category, trustedCA, nodeRef, task));
        } catch (IllegalArgumentException | SmrsDirectoryException | UnknownHostException | CppSecurityServiceException | CertificateException e) {
            nscsLogger.info("Could not read Trust Certificate for Trust Category [" + category + "], CA [" + trustedCA + "], FDN [" + nodeRef.getFdn()
                    + "], exception [" + e.getCause() + "]");
        }
        return certs;
    }

    private List<CertDetails> fetchTrustCertsByCategory(final String trustCategory, final NodeReference nodeRef,
            final CheckTrustedAlreadyInstalledTask task) {
        final List<CertDetails> certs = new ArrayList<>();
        try {
            final TrustedCertCategory category = getTrustCategory(trustCategory);
            final TrustStoreInfo trustStoreInfo = securityService.getTrustStoreForNode(category, nodeRef, false);
            for (final CertSpec certSpec : trustStoreInfo.getCertSpecs()) {
                certs.add(new CertDetails(certSpec.getCertHolder()));
            }
            return certs;
        } catch (SmrsDirectoryException | UnknownHostException | CppSecurityServiceException | CertificateException e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "ProcessTask CheckTrustedAlreadyInstalledTask failed" + e);
            throw new WorkflowTaskException("processTask InstallTrustedCertificatesIpSecTask failed", e);
        }
    }

    private List<CertDetails> fetchTrustCertsByTrustedCA(final String trustCategory, final String trustedCA, final NodeReference nodeRef,
            final CheckTrustedAlreadyInstalledTask task)
            throws IllegalArgumentException, SmrsDirectoryException, CertificateException, UnknownHostException, CppSecurityServiceException {
        nscsLogger.info("fetching TrustCerts by TrustedCA : trustedCA [" + trustedCA + "], FDN [" + nodeRef.getFdn() + "]");
        final List<CertDetails> certs = new ArrayList<>();
        final TrustedCertCategory category = getTrustCategory(trustCategory);
        final TrustStoreInfo trustStoreInfo = securityService.getTrustStoreForNodeWithCA(category, trustedCA, nodeRef, false);
        for (final CertSpec certSpec : trustStoreInfo.getCertSpecs()) {
            certs.add(new CertDetails(certSpec.getCertHolder()));
        }
        return certs;
    }

    private List<CertDetails> fetchTrustCertsByFile(final String file) throws FileNotFoundException, CertificateException {
        if (file == null || "".equals(file)) {
            throw new FileNotFoundException();
        }
        final List<CertDetails> certs = new ArrayList<>();
        final InputStream fis = resourcesBean.getFileSystemResource(file).getInputStream();
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final Collection<? extends Certificate> c = cf.generateCertificates(fis);
        final Iterator<? extends Certificate> i = c.iterator();
        while (i.hasNext()) {
            certs.add(new CertDetails((java.security.cert.X509Certificate) i.next()));
        }
        return certs;
    }

    private TrustedCertCategory getTrustCategory(final String category) {
        if (null == category) {
            //Default category
            return TrustedCertCategory.IPSEC;
        } else {
            return TrustedCertCategory.valueOf(category);
        }
    }

}
