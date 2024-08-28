/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.OnDemandCrlDownloadCommand;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@RunWith(MockitoJUnitRunner.class)
public class OnDemandCrlDownloadHandlerTest {

    @Mock
    private NscsCommandManager commandManager;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private NormalizableNodeReference mockNormalizedNodeReference;

    @InjectMocks
    private OnDemandCrlDownloadHandler OnDemandCrlDownlaodHandler;

    @Mock
    private NscsCMReaderService mockReaderService;

    @Mock
    private NscsJobCacheHandler cacheHandler;

    @Mock
    NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Mock
    private NscsContextService nscsContextService;

    private OnDemandCrlDownloadCommand onDemandCrlDownloadCommand;

    private CommandContext context;

    private List<String> inputNodesList;
    List<NodeReference> validNodesList = null;

    private JobStatusRecord jobStatusRecord;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        inputNodesList = new LinkedList<String>();
        inputNodesList.add("node1");
        inputNodesList.add("node2");
        inputNodesList.add("node3");
        onDemandCrlDownloadCommand = new OnDemandCrlDownloadCommand();

        jobStatusRecord = new JobStatusRecord();
        final UUID jobId = UUID.randomUUID();
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobStatusRecord.setUserId("ENM User");
        jobStatusRecord.setJobId(jobId);
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.handler.command.impl.OnDemandCrlDownlaodHandler#process(com.ericsson.nms.security.nscs.api.command.types.OnDemandCrlDownloadCommand, com.ericsson.nms.security.nscs.handler.CommandContext)}
     * .
     */
    @Test
    public void testProcess() {

        final String jobIdMessage = String.format(OnDemandCrlDownloadHandler.ON_DEMAND_CRL_DOWNLOAD_EXECUTED, jobStatusRecord.getJobId().toString());
        Mockito.when(commandManager.validateNodesForOnDemandCrlDownload(Matchers.anyList(), Matchers.anyList(), Matchers.anyMap(), Matchers.anyMap()))
                .thenReturn(true);
        Mockito.when(cacheHandler.insertJob(NscsCommandType.ON_DEMAND_CRL_DOWNLOAD)).thenReturn(jobStatusRecord);

        final NscsCommandResponse response = OnDemandCrlDownlaodHandler.process(onDemandCrlDownloadCommand, context);
        Assert.assertEquals(((NscsMessageCommandResponse) response).getMessage(), jobIdMessage);
    }

    /**
     * Test method for
     * {@link com.ericsson.nms.security.nscs.handler.command.impl.OnDemandCrlDownlaodHandler#process(com.ericsson.nms.security.nscs.api.command.types.OnDemandCrlDownloadCommand, com.ericsson.nms.security.nscs.handler.CommandContext)}
     * .
     */
    @Test
    public void testProcess_OnDemandCrlDownloadNotExecuted() {

        final NodeReference nodeRef = new NodeRef("node1");
        Mockito.when(mockReaderService.getNormalizableNodeReference(Matchers.any(NodeReference.class))).thenReturn(mockNormalizedNodeReference);
        Mockito.when(mockNormalizedNodeReference.getNormalizedRef()).thenReturn(nodeRef);
        validNodesList = new ArrayList<NodeReference>();
        validNodesList.add(nodeRef);

        Mockito.when(commandManager.validateNodesForOnDemandCrlDownload(Matchers.anyList(), Matchers.anyList(), Matchers.anyMap(), Matchers.anyMap()))
                .thenReturn(false);

        final NscsCommandResponse response = OnDemandCrlDownlaodHandler.process(onDemandCrlDownloadCommand, context);
        Assert.assertEquals(((NscsNameMultipleValueCommandResponse) response).getAdditionalInformation(),
                OnDemandCrlDownloadHandler.ON_DEMAND_CRL_DOWNLOAD_NOT_EXECUTED);

    }

}
