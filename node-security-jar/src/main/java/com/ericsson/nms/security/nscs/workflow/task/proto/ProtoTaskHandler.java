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
package com.ericsson.nms.security.nscs.workflow.task.proto;

import java.security.cert.CertificateException;

import javax.ejb.Local;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.CACertSftpPublisher;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.proto.ProtoTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.PROTOTYPE_TASK
 * </p>
 * <p>
 * Prototype task that calls CACertSftpPublisher.publishCertificates
 * </p>
 * 
 * @author emaynes on 16/06/2014.
 */
@WFTaskType(WorkflowTaskType.PROTOTYPE_TASK)
@Local(WFTaskHandlerInterface.class)
public class ProtoTaskHandler implements WFActionTaskHandler<ProtoTask>, WFTaskHandlerInterface {

    @Inject
    private Logger logger;

    @Inject
    private CACertSftpPublisher caCertSftpPublisher;

    @Override
    public void processTask(final ProtoTask task) {

        logger.info("Processing prototype task (void)");
        final NodeReference node = task.getNode();
        final String neName = node.getName();
        final String networkType = task.getNetworkType();
        final String neType = task.getNodeType();
        logger.info("From prototype task : networkType {} neType {} neName {}", networkType, neType, neName);

        try {
            caCertSftpPublisher.publishCertificates(neType, neName);
        } catch (CertificateException | NscsPkiEntitiesManagerException e) {
            logger.error("Failed to publish certs", e);
        }
        logger.info("Processing prototype task finished");
    }
}
