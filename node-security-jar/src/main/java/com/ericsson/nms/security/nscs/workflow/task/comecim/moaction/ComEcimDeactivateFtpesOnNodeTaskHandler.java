/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.workflow.task.comecim.moaction;

import java.util.Collection;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.MoAttributeHandler;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.moaction.ComEcimDeactivateFtpesOnNodeTask;

@WFTaskType(WorkflowTaskType.COM_DEACTIVATE_FTPES)
@Local(WFTaskHandlerInterface.class)
public class ComEcimDeactivateFtpesOnNodeTaskHandler implements WFActionTaskHandler<ComEcimDeactivateFtpesOnNodeTask>, WFTaskHandlerInterface {

    private static final String SFTP = "SFTP";
    private static final String FTP_TLS_SERVER = "FtpTlsServer";
    private static final String FTP_SERVER = "FtpServer";
    private static final String FTP_TLS = "FtpTls";

    @Inject
    private NscsLogger nscsLogger;
    @Inject
    private NscsCMWriterService writerService;
    @Inject
    private NscsCMReaderService readerService;
    @Inject
    private MoAttributeHandler moAttributeHandler;
    @Inject
    private NscsCapabilityModelService capabilityService;
    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Override
    public void processTask(ComEcimDeactivateFtpesOnNodeTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);

        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(task.getNode());
        final ModelDefinition.Mo rootMo = capabilityService.getMirrorRootMo(normalizable);
        final String mirrorRootFdn = normalizable.getFdn();

        final ModelDefinition.Mo ftpTlsMo = ((ModelDefinition.ComEcimManagedElement) rootMo).systemFunctions.sysM.fileTPM.ftpTls;
        final ModelDefinition.Mo ftpServerMo = ((ModelDefinition.ComEcimManagedElement) rootMo).systemFunctions.sysM.fileTPM.ftpServer;

        final String ftpTlsFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, FTP_TLS, ftpTlsMo.namespace());
        final String ftpServerFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, FTP_SERVER, ftpServerMo.namespace());

        // common settings for both version of model
        final NscsCMWriterService.WriterSpecificationBuilder ftpTlsSpecification = writerService.withSpecification();
        ftpTlsSpecification.setAttribute(ModelDefinition.FtpTls.NODE_CREDENTIAL, null);
        ftpTlsSpecification.setAttribute(ModelDefinition.FtpTls.TRUST_CATEGORY, null);
        ftpTlsSpecification.setFdn(ftpTlsFdn);
        catchException(ftpTlsSpecification, "Updating MOAttribute nodeCredential and trustCategory in FtpTls model failed.", task);

        final NscsCMWriterService.WriterSpecificationBuilder comConnectivityInformationSpecification = writerService.withSpecification();
        final NscsCMWriterService.WriterSpecificationBuilder ftpTlsServerSpecification = writerService.withSpecification();
        final String comConnectivityInformationFdn = Model.NETWORK_ELEMENT.comConnectivityInformation.withNames(
                normalizable.getNormalizedRef().getName()).fdn();

        ModelDefinition.Mo ftpTlsServerMo = null;
        String ftpTlsServerFdn = null;

        if (ftpServerFdn == null) {

            // FileTPM 1.1
            nscsLogger.debug("Node supports FileTPM model version 1.1");

            ftpTlsServerMo = ((ModelDefinition.ComEcimManagedElement) rootMo).systemFunctions.sysM.fileTPM.ftpTls.ftpTlsServer;
            ftpTlsServerFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, FTP_TLS_SERVER, ftpTlsServerMo.namespace());
            nscsLogger.debug("ftpTlsServerFdn: {}", ftpTlsServerFdn);

            ftpTlsServerSpecification.setNotNullAttribute(ModelDefinition.FtpTlsServer.ADMINISTRATIVE_STATE,
                    ModelDefinition.FtpTlsServer.BasicAdmState.LOCKED.toString());
            ftpTlsServerSpecification.setFdn(ftpTlsServerFdn);
            catchException(ftpTlsServerSpecification, "Updating MOAttributes for FtpTlsServer (model 1.1) failed.", task);

        } else {

            // FileTPM 2.1
            nscsLogger.debug("Node supports FileTPM model version 2.1");

            ftpTlsServerMo = ((ModelDefinition.ComEcimManagedElement) rootMo).systemFunctions.sysM.fileTPM.ftpServer.ftpTlsServer;
            ftpTlsServerFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, FTP_TLS_SERVER, ftpTlsServerMo.namespace());
            nscsLogger.debug("ftpTlsServerFdn: {}", ftpTlsServerFdn);

            ftpTlsServerSpecification.setNotNullAttribute(ModelDefinition.FtpTlsServer.ADMINISTRATIVE_STATE,
                    ModelDefinition.FtpTlsServer.BasicAdmState.LOCKED.toString());
            ftpTlsServerSpecification.setAttribute(ModelDefinition.FtpTlsServer.NODE_CREDENTIAL, null);
            ftpTlsServerSpecification.setAttribute(ModelDefinition.FtpTlsServer.TRUST_CATEGORY, null);
            ftpTlsServerSpecification.setFdn(ftpTlsServerFdn);

            catchException(ftpTlsServerSpecification, "Updating MOAttributes in FtpTlsServer (model 2.1) failed.", task);
        }

        comConnectivityInformationSpecification.setNotNullAttribute(ModelDefinition.ComConnectivityInformation.FILE_TRANSFER_PROTOCOL, SFTP);
        comConnectivityInformationSpecification.setFdn(comConnectivityInformationFdn);
        catchException(comConnectivityInformationSpecification,
                "Updating MOAttribute fileTransferProtocol in ComConnectivityInformation model failed.", task);

        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Updating MOAttributes finished successfully for node: " + task.getNode().getName());
    }

    private void catchException(final NscsCMWriterService.WriterSpecificationBuilder specification, final String message,
                                ComEcimDeactivateFtpesOnNodeTask task) {
        try {
            specification.updateMO();
        } catch (final Exception e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, message);
            throw e;
        }
    }
}
