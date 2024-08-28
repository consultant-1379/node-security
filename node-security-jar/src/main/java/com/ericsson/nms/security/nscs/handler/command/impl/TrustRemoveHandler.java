package com.ericsson.nms.security.nscs.handler.command.impl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.TrustRemoveCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NoNodesFoundException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.TrustDistributeWfException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.TrustValidator;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.CAEntity;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>
 * Removes trusts for the provided list of nodes.
 * </p>
 * 
 * Created by enmadmin
 */
@UseValidator({ NoDuplNodeNamesAllowedValidator.class })
@CommandType(NscsCommandType.TRUST_REMOVE)
@Local(CommandHandlerInterface.class)
public class TrustRemoveHandler implements CommandHandler<TrustRemoveCommand>, CommandHandlerInterface {

    public static final String TRUST_REMOVAL_EXECUTED = "Successfully started a job for trust removal from nodes";
    public static final String TRUST_REMOVAL_EXECUTED_DYN_ISSUE = "Successfully started a job for trust removal from valid nodes only";
    public static final String TRUST_REMOVAL_NOT_EXECUTED = "Trust remove command not executed.";
    public static final String TRUST_REMOVAL_NOT_EXECUTED_SOME_INVALID_NODES = "Trust remove command not executed as some provided nodes are invalid.";
    public static final String TRUST_REMOVAL_NOT_EXECUTED_NO_VALID_NODES = "Trust remove command not started as no input node is valid.";
    public static final String TRUST_REMOVAL_FAILED_INVALID_SERIAL_NUM = "Input serial number is invalid. It must be a numeric value.";
    public static final String TRUST_REMOVAL_FAILED_INVALID_CA = "Input CA name is invalid.";
    public static final String TRUST_REMOVAL_FAILED_INVALID_ISDN = "Input issuer distinguish name is invalid.";
    public static final String TRUST_REMOVAL_FAILED_EXC_RETRIEVING_ISDN = "Exception received while retrieving issuer distinguish name.";
    public static final String DEPRECATED_WARNING_MESSAGE = "[Warning: The command with --certType option will be deprecated in the future. Use --trustcategory instead of --certType]";
    public static final String GET_PROGRESS_INFO = "' to get progress info.";
    public static final String PERFORM_JOB_GET = ". Perform 'secadm job get -j ";
    public static final String[] ERROR_HEADER = new String[] { "Node Name", "Error Code", "Error Detail", "Suggested Solution" };
    private static final int NO_OF_COLUMNS = 3;

    @Inject
    private Logger logger;

    @EJB
    private NscsCommandManager commandManager;

    @EJB
    private NscsPkiEntitiesManagerIF nscsPkiManager;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsJobCacheHandler cacheHandler;

    @Inject
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Inject
    private TrustValidator trustValidator;

    @Inject
    private NscsContextService nscsContextService;

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.nms.security.nscs.handler.command.CommandHandler#process (com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand,
     * com.ericsson.nms.security.nscs.handler.CommandContext)
     */
    @Override
    public NscsCommandResponse process(final TrustRemoveCommand command, final CommandContext context) {

        final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS);
        response.add(ERROR_HEADER[0], Arrays.copyOfRange(ERROR_HEADER, 1, ERROR_HEADER.length));
        final List<NodeReference> validNodesList = new ArrayList<NodeReference>();
        final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
        final String serialNumber = command.getSerialNumber();
        JobStatusRecord jobStatusRecord;
        String trustCategory = getTrustCategory(command);
        Boolean hasCertTypeProperty = command.getProperties().containsKey(TrustRemoveCommand.CERT_TYPE_PROPERTY);
        logger.info("Trust remove command [{}]", command);

        trustValidator.validateCommandForCertTypeAndTrustCategory(command.getCertType(), trustCategory);
        final List<String> errMsg = new ArrayList<>();
        final String issuerDn = getIssuerDistinguishName(command, errMsg);

        validateIssuerDnAndSerialNumber(serialNumber, issuerDn, errMsg);

        if (command.getNodeNames().size() == 1 && command.getNodeNames().get(0).equalsIgnoreCase("all")) {
            final List<NodeReference> allNetworkElementsList = getNetWorkElementsList();
            logger.info("trustCategory[{}]", trustCategory);
            trustValidator.validateInputNodes(allNetworkElementsList, trustCategory, validNodesList, invalidNodesErrorMap, false);
            logger.info("allNetworkElementList is {}, validNodesList is {}", allNetworkElementsList, validNodesList);

        } else {
            trustValidator.validateInputNodes(nscsInputNodeRetrievalUtility.getNodeReferenceList(command), trustCategory, validNodesList,
                    invalidNodesErrorMap, false);

        }

        nscsContextService.initItemsStatsForAsyncCommand(Integer.valueOf(validNodesList.size()), Integer.valueOf(invalidNodesErrorMap.size()));

        if (invalidNodesErrorMap.isEmpty()) {
            jobStatusRecord = cacheHandler.insertJob(NscsCommandType.TRUST_REMOVE);
            initiateWorkflow(validNodesList, trustCategory, jobStatusRecord, serialNumber, issuerDn);
            return NscsCommandResponse.message(prepareSuccessResponse(jobStatusRecord, hasCertTypeProperty));
        } else if (!validNodesList.isEmpty()) {
            jobStatusRecord = cacheHandler.insertJob(NscsCommandType.TRUST_REMOVE);
            initiateWorkflow(validNodesList, trustCategory, jobStatusRecord, serialNumber, issuerDn);
            return preparePartialSuccessResponse(invalidNodesErrorMap, hasCertTypeProperty, jobStatusRecord, response);
        } else {
            return prepareFailureResponse(invalidNodesErrorMap, hasCertTypeProperty, response);
        }
    }

    private String getIssuerDistinguishName(final TrustRemoveCommand command, final List<String> errorMsg) {
        logger.info("getIssuerDistinguishName");
        String issuerDn = new String();
        if (command.getProperties().containsKey(TrustRemoveCommand.CA_PROPERTY)) {
            final String inputCA = command.getCaValue();
            try {
                if (nscsPkiManager.isEntityNameAvailable(inputCA, EntityType.CA_ENTITY)) {
                    errorMsg.add(TRUST_REMOVAL_FAILED_INVALID_CA);
                    logger.info("getIssuerDistinguishName invalid CA error, issuerDn = {}", issuerDn);
                    return issuerDn;
                }

                // Retrieving issuerDn from input CA name
                logger.info("Retrieving issuerDn from input CA name....");
                // Should return the X509Certificate of the CA
                final CAEntity CAentity = nscsPkiManager.getCAEntity(inputCA);
                logger.info("Retrieved from pki CAEntity [{}]", inputCA);
                if ((CAentity.getCertificateAuthority().getActiveCertificate() != null)
                        && (CAentity.getCertificateAuthority().getActiveCertificate().getX509Certificate() != null)
                        && (CAentity.getCertificateAuthority().getActiveCertificate().getX509Certificate().getIssuerX500Principal() != null)) {

                    logger.debug("ActiveCertificate : [{}]", CAentity.getCertificateAuthority().getActiveCertificate());
                    logger.debug("X509Certificate : [{}]", CAentity.getCertificateAuthority().getActiveCertificate().getX509Certificate());
                    logger.debug("IssuerDN : [{}]", CAentity.getCertificateAuthority().getActiveCertificate().getX509Certificate()
                            .getIssuerX500Principal());

                    issuerDn = CAentity.getCertificateAuthority().getActiveCertificate().getX509Certificate().getIssuerX500Principal().toString();
                    logger.info("Returning issuerDn = [{}]", issuerDn);
                    errorMsg.add("");
                    return issuerDn;
                } else {
                    errorMsg.add(TRUST_REMOVAL_FAILED_INVALID_ISDN);
                    logger.info("getIssuerDistinguishName exc retrieving isdn exc, issuerDn = {}", issuerDn);
                    return issuerDn;
                }
            } catch (final NscsPkiEntitiesManagerException e) {
                e.printStackTrace();
                errorMsg.add(TRUST_REMOVAL_FAILED_EXC_RETRIEVING_ISDN);
                logger.info("getIssuerDistinguishName exc retrieving isdn exc, issuerDn = {}", issuerDn);
                return issuerDn;
            }
        } else if (command.getProperties().containsKey(TrustRemoveCommand.ISDN_PROPERTY)) {
            issuerDn = command.getIssuerDn();
            boolean issuerDnFound = false;
            // validity check for input isdn must be done with CA Entities.
            try {
                final Map<String, List<X509Certificate>> trustsMap = nscsPkiManager.getCAsTrusts();
                logger.info("size of trustsMap is: {}", trustsMap.size());
                for (final Entry<String, List<X509Certificate>> entry : trustsMap.entrySet()) {
                    final List<X509Certificate> x509certList = entry.getValue();
                    logger.info("CA name: [{}]", entry.getKey());
                    logger.info("size of x509certList is: {}", x509certList.size());
                    for (final X509Certificate x509cert : x509certList) {
                        if (CertDetails.matchesDN(x509cert.getIssuerX500Principal().getName(), issuerDn)) {
                            logger.info("issuerDnFound!! issuerDn: {}, inputIsDn: {}", x509cert.getIssuerX500Principal().getName(), issuerDn);
                            issuerDnFound = true;
                            break;
                        }
                    }
                }
            } catch (final NscsPkiEntitiesManagerException e) {
                e.printStackTrace();
                errorMsg.add(TRUST_REMOVAL_FAILED_EXC_RETRIEVING_ISDN);
                logger.info("getIssuerDistinguishName retrieving isdn exc, issuerDn = {}", issuerDn);
                return issuerDn;
            }
            // If not present, abort command.
            if (!issuerDnFound) {
                errorMsg.add(TRUST_REMOVAL_FAILED_INVALID_ISDN);
                logger.info("getIssuerDistinguishName invalid ISDN error, issuerDn = {}", issuerDn);
                return issuerDn;
            } else {
                logger.info(" returning issuerDn = {}", issuerDn);
                errorMsg.add("");
                return issuerDn;
            }
        } else {
            logger.error("getIssuerDistinguishName CommandSyntaxException");
            throw new CommandSyntaxException();
        }
    }

    private NscsCommandResponse preparePartialSuccessResponse(final Map<NodeReference, NscsServiceException> invalidNodesErrorMap,
            final Boolean hasCertProperty, JobStatusRecord jobStatusRecord, final NscsNameMultipleValueCommandResponse response) {

        String jobIdMessage = preparePartialSuccessResponse(jobStatusRecord, hasCertProperty);

        final Set<java.util.Map.Entry<NodeReference, NscsServiceException>> entrySet = invalidNodesErrorMap.entrySet();
        for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : entrySet) {
            response.add(entry.getKey().getFdn(), new String[] { "" + entry.getValue().getErrorCode(), entry.getValue().getMessage(),
                    entry.getValue().getSuggestedSolution() });
        }
        response.setAdditionalInformation(jobIdMessage + " Invalid node details are given below : ");
        return response;
    }

    private NscsCommandResponse prepareFailureResponse(final Map<NodeReference, NscsServiceException> invalidNodesErrorMap,
            final Boolean hasCertProperty, final NscsNameMultipleValueCommandResponse response) {

        final Set<java.util.Map.Entry<NodeReference, NscsServiceException>> entrySet = invalidNodesErrorMap.entrySet();
        for (java.util.Map.Entry<NodeReference, NscsServiceException> entry : entrySet) {
            response.add(entry.getKey().getFdn(), new String[] { "" + entry.getValue().getErrorCode(), entry.getValue().getMessage(),
                    entry.getValue().getSuggestedSolution() });
        }

        if (hasCertProperty) {
            response.setAdditionalInformation(TRUST_REMOVAL_NOT_EXECUTED_NO_VALID_NODES + DEPRECATED_WARNING_MESSAGE
                    + ". Invalid node details are given below :");
        } else {
            response.setAdditionalInformation(TRUST_REMOVAL_NOT_EXECUTED_NO_VALID_NODES + " Invalid node details are given below :");
        }
        return response;

    }

    private void initiateWorkflow(final List<NodeReference> validNodesList, final String trustCategory, JobStatusRecord jobStatusRecord,
            final String serialNumber, final String issuerDn) {
        try {
            commandManager.executeTrustRemoveWfs(validNodesList, issuerDn, serialNumber, trustCategory, jobStatusRecord);
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new TrustDistributeWfException();
        }
    }

    private String prepareSuccessResponse(JobStatusRecord jobStatusRecord, final Boolean hasCertProperty) {
        String successMessage = "";
        if (jobStatusRecord != null) {
            successMessage = TRUST_REMOVAL_EXECUTED + PERFORM_JOB_GET + jobStatusRecord.getJobId().toString() + GET_PROGRESS_INFO;
            if (hasCertProperty) {
                successMessage = successMessage + DEPRECATED_WARNING_MESSAGE;
            }
        }
        return successMessage;
    }

    private String preparePartialSuccessResponse(JobStatusRecord jobStatusRecord, final Boolean hasCertProperty) {
        String successMessage = "";
        if (jobStatusRecord != null) {
            successMessage = TRUST_REMOVAL_EXECUTED_DYN_ISSUE + PERFORM_JOB_GET + jobStatusRecord.getJobId().toString() + GET_PROGRESS_INFO;
            if (hasCertProperty) {
                successMessage = successMessage + DEPRECATED_WARNING_MESSAGE;
            }
        }
        return successMessage;
    }

    private List<NodeReference> getNetWorkElementsList() {
        final List<NodeReference> allNetworkElementsList = new ArrayList<NodeReference>();

        final CmResponse cmResp = reader.getAllMos(Model.NETWORK_ELEMENT.type(), Model.NETWORK_ELEMENT.namespace());
        if (cmResp.getStatusCode() <= 0) {
            throw new NoNodesFoundException(TRUST_REMOVAL_NOT_EXECUTED);
        }
        for (final CmObject cmObj : cmResp.getCmObjects()) {
            logger.info("Adding NetworkElement with name : {} and fdn : {}", cmObj.getName(), cmObj.getFdn());
            allNetworkElementsList.add(new NodeRef(cmObj.getName()));
        }

        if (allNetworkElementsList.isEmpty()) {
            throw new NoNodesFoundException(TRUST_REMOVAL_NOT_EXECUTED);
        }
        return allNetworkElementsList;
    }

    private String getTrustCategory(final TrustRemoveCommand command) {
        String trustCategory = "";
        if (command.getCertType() != null) {
            trustCategory = command.getCertType();
        } else {
            trustCategory = command.getTrustCategory();
        }
        return trustCategory;
    }

    private void validateIssuerDnAndSerialNumber(final String serialNumber, final String issuerDn, final List<String> errMsg) {
        if (!serialNumber.matches("\\b[0-9]+\\b")) {
            throw new InvalidArgumentValueException(TRUST_REMOVAL_FAILED_INVALID_SERIAL_NUM);
        }
        if (issuerDn.isEmpty()) {
            throw new InvalidArgumentValueException(errMsg.get(0));
        }
    }

}
