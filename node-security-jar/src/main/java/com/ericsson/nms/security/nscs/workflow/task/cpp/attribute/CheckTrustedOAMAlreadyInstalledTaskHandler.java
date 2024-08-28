/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.TrustCategoryType;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CheckTrustedOAMAlreadyInstalledTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.OamTrustCategory;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_CHECK_TRUSTED_OAM_ALREADY_INSTALLED
 * </p>
 *
 * @author elucbot
 */
@WFTaskType(WorkflowTaskType.CPP_CHECK_TRUSTED_OAM_ALREADY_INSTALLED)
@Local(WFTaskHandlerInterface.class)
public class CheckTrustedOAMAlreadyInstalledTaskHandler implements WFQueryTaskHandler<CheckTrustedOAMAlreadyInstalledTask>, WFTaskHandlerInterface {

    private static final String NOT_INSTALLED = "NOT_INSTALLED";
    private static final String INSTALLED = "INSTALLED";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private CppSecurityService securityService;

    @Inject
    private MOGetServiceFactory moGetServiceFactory;

    @Override
    public String processTask(final CheckTrustedOAMAlreadyInstalledTask task) {

        nscsLogger.workFlowTaskHandlerStarted(task);

        String trustCategory = null;

        if (task.getTrustCerts().equals(TrustedCertCategory.LOCAL_AA_DB_FILE_SIGNERS.toString())) {
            trustCategory = TrustCategoryType.LAAD.toString();
        } else {
            trustCategory = TrustCategoryType.OAM.toString();
        }

        final NodeReference node = task.getNode();
        final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(node);

        final String readMessage = "installed [" + trustCategory + "] trusted certs on node [" + node + "]";
        nscsLogger.debug(task, "Reading " + readMessage);
        final CertStateInfo trustCertificateInfo = moGetServiceFactory.getTrustCertificateStateInfo(normNode, trustCategory);

        if (trustCertificateInfo.isNotAvailable()) {
            final String errorMessage = "Error reading " + readMessage;
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, errorMessage);
            throw new MissingMoAttributeException();
        } else {
            final List<CertDetails> installedTrustedCerts = trustCertificateInfo.getCertificates();
            nscsLogger.info(task, "Found [" + installedTrustedCerts.size() + "] " + readMessage);

            dumpCertificates(installedTrustedCerts);

            final String trustedCA = task.getTrustedCertificateAuthority();
            final List<CertDetails> installedTrustedCertsByCategory = new ArrayList<CertDetails>();
            final OamTrustCategory trustedCertCategory = OamTrustCategory.valueOf(task.getTrustCerts());

            nscsLogger.info("CheckTrustedOAMAlreadyInstalledTaskHandler trust category from input:" + trustedCertCategory.toString());

            for (final CertDetails certDetail : installedTrustedCerts) {
                if (trustedCertCategory.equals(certDetail.getCategory())) {
                    installedTrustedCertsByCategory.add(certDetail);
                }
            }

            List<CertDetails> expectedTrustedCerts = new ArrayList<>();
            if (trustedCA != null && !trustedCA.isEmpty()) {
                nscsLogger.info(task, "Fetching trusted certs for CA [" + trustedCA + "], category [" + task.getTrustCerts() + "]");
                expectedTrustedCerts = fetchTrustCertsFromInputCA(task.getTrustCerts(), trustedCA, node);
            } else {
                nscsLogger.info(task, "Fetching trusted certs for trust param [" + task.getTrustCerts() + "]");
                expectedTrustedCerts = fetchTrustCertsFromInput(task.getTrustCerts(), node);
            }

            // All the expected trusted certificates MUST exist on node
            for (final CertDetails expectedTrustedCert : expectedTrustedCerts) {
                nscsLogger.info(task, "Expected certificate: issuer [" + expectedTrustedCert.getIssuer() + "]: serialnumber ["
                        + expectedTrustedCert.getSerial().toString() + "]: subject [" + expectedTrustedCert.getSubject() + "]");
                boolean isInstalled = false;
                for (final CertDetails installedTrustedCert : installedTrustedCertsByCategory) {
                    if (installedTrustedCert.equals(expectedTrustedCert)) {
                        nscsLogger.info(task, "Already installed");
                        isInstalled = true;
                        break;
                    }
                }
                if (!isInstalled) {
                    nscsLogger.info(task, "Not yet installed");
                    // Node does not contain all expected certificates
                    return trustNotInstalledOnNode(task);
                }
            }

            // Node contains all expected certificates
            return trustAlreadyInstalledOnNode(task);
        }
    }

    /**
     *
     * @param certs
     */
    private void dumpCertificates(final List<CertDetails> certs) {
        nscsLogger.info("Dumping certificate list:");
        for (final CertDetails cert : certs) {
            nscsLogger.info(
                    "Certificate: issuer [" + cert.getIssuer() + "]: serialnumber [" + cert.getSerial() + "]: subject [" + cert.getSubject() + "]");
        }
        nscsLogger.info("End dump.");
    }

    /**
     *
     * @param task
     * @return
     */
    private String trustAlreadyInstalledOnNode(final CheckTrustedOAMAlreadyInstalledTask task) {
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task,
                "OAM trusted certificates already installed on node. Skipping trust installation step.", NscsLogger.ALREADY_INSTALLED);
        return INSTALLED;
    }

    /**
     *
     * @param task
     * @return
     */
    private String trustNotInstalledOnNode(final CheckTrustedOAMAlreadyInstalledTask task) {
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task,
                "OAM trusted certificates not yet installed on node. Proceeding to trust installation step.");
        return NOT_INSTALLED;
    }

    /**
     * Process the input information and extract the certs from it.
     *
     * @param trustParam
     *            {@link String} that can be a file path of certs (ie .pem file) or name of the trust category over pki-manager.
     * @param nodeRef
     * @return list of certificates details.
     */
    private List<CertDetails> fetchTrustCertsFromInput(final String trustParam, final NodeReference nodeRef) {
        final String readMessage = "trusted certs by category [" + trustParam + "]: node [" + nodeRef + "]";
        nscsLogger.debug("Fetching " + readMessage);
        final List<CertDetails> certs = new ArrayList<>();
        try {
            certs.addAll(fetchTrustCertsByCategory(trustParam, nodeRef));
        } catch (IllegalArgumentException | SmrsDirectoryException | UnknownHostException | CppSecurityServiceException | CertificateException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + "while fetching " + readMessage;
            nscsLogger.error(errorMessage);
            nscsLogger.debug("Fetching trusted certs by file: path [" + trustParam + "]");
            certs.addAll(fetchTrustCertsByFile(trustParam));
        }
        return certs;
    }

    /**
     * Process the input information and extract the certs from it.
     *
     * @param category
     * @param trustedCA
     * @param nodeRef
     * @return
     */
    private List<CertDetails> fetchTrustCertsFromInputCA(final String category, final String trustedCA, final NodeReference nodeRef) {
        final String readMessage = "trusted certs by trusted CA [" + trustedCA + "]: category [" + category + "]: node [" + nodeRef + "]";
        nscsLogger.debug("Fetching " + readMessage);
        final List<CertDetails> certs = new ArrayList<>();
        try {
            certs.addAll(fetchTrustCertsByTrustedCA(category, trustedCA, nodeRef));
        } catch (IllegalArgumentException | SmrsDirectoryException | UnknownHostException | CppSecurityServiceException | CertificateException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + "while fetching " + readMessage;
            nscsLogger.error(errorMessage);
        }
        return certs;
    }

    /**
     *
     * @param trustCategory
     * @param nodeRef
     * @return
     * @throws IllegalArgumentException
     * @throws SmrsDirectoryException
     * @throws CertificateException
     * @throws UnknownHostException
     * @throws CppSecurityServiceException
     */
    private List<CertDetails> fetchTrustCertsByCategory(final String trustCategory, final NodeReference nodeRef)
            throws IllegalArgumentException, SmrsDirectoryException, CertificateException, UnknownHostException, CppSecurityServiceException {
        final String readMessage = "trusted certs by category [" + trustCategory + "]: node [" + nodeRef + "]";
        nscsLogger.debug("Fetching " + readMessage);
        final List<CertDetails> certs = new ArrayList<>();
        final TrustedCertCategory category = getTrustCategory(trustCategory);
        final TrustStoreInfo trustStoreInfo = securityService.getTrustStoreForNode(category, nodeRef, false);
        for (final CertSpec certSpec : trustStoreInfo.getCertSpecs()) {
            certs.add(new CertDetails(certSpec.getCertHolder()));
        }
        return certs;
    }

    /**
     *
     * @param trustCategory
     * @param trustedCA
     * @param nodeRef
     * @return
     * @throws IllegalArgumentException
     * @throws SmrsDirectoryException
     * @throws CertificateException
     * @throws UnknownHostException
     * @throws CppSecurityServiceException
     */
    private List<CertDetails> fetchTrustCertsByTrustedCA(final String trustCategory, final String trustedCA, final NodeReference nodeRef)
            throws IllegalArgumentException, SmrsDirectoryException, CertificateException, UnknownHostException, CppSecurityServiceException {
        final String readMessage = "trusted certs by trusted CA [" + trustedCA + "]: category [" + trustCategory + "]: node [" + nodeRef + "]";
        nscsLogger.debug("Fetching " + readMessage);
        final List<CertDetails> certs = new ArrayList<>();
        final TrustedCertCategory category = getTrustCategory(trustCategory);
        final TrustStoreInfo trustStoreInfo = securityService.getTrustStoreForNodeWithCA(category, trustedCA, nodeRef, false);
        for (final CertSpec certSpec : trustStoreInfo.getCertSpecs()) {
            certs.add(new CertDetails(certSpec.getCertHolder()));
        }
        return certs;
    }

    /**
     *
     * @param file
     * @return
     */
    private List<CertDetails> fetchTrustCertsByFile(final String file) {
        //        if (file == null || file.isEmpty()) {
        //            throw new WorkflowTaskException(String
        //                    .format("processTask InstallTrustedCertificatesIpSecTask failed - File [%s] not found", file));
        //        }
        //        final List<CertDetails> certs = new ArrayList<>();
        //        final InputStream fis = resourcesBean.getFileSystemResource(file).getInputStream();
        //        try {
        //            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        //            final Collection<? extends Certificate> c = cf.generateCertificates(fis);
        //            final Iterator<? extends Certificate> i = c.iterator();
        //            while (i.hasNext()) {
        //                certs.add(new CertDetails((java.security.cert.X509Certificate) i.next()));
        //            }
        //        } catch (CertificateException e) {
        //            throw new WorkflowTaskException(String.format(
        //                    "processTask InstallTrustedCertificatesIpSecTask failed - CertificateException message [{}]",
        //                    e.getMessage()));
        //        }
        //        return certs;
        nscsLogger.error("Not yet supported: fetch trusted certs by file [" + file + "]");
        return null;
    }

    /**
     *
     * @param category
     * @return
     * @throws IllegalArgumentException
     */
    private TrustedCertCategory getTrustCategory(final String category) throws IllegalArgumentException {
        if (null == category) {
            //Default category
            return TrustedCertCategory.CORBA_PEERS;
        } else {
            return TrustedCertCategory.valueOf(category);
        }
    }
}
