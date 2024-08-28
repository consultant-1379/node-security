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
package com.ericsson.nms.security.nscs.handler.rtselconfig.utility;

import static org.junit.Assert.assertNotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.RtselCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.rtsel.utility.ActivateRtselResponseBuilder;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;

/**
 * Test class for ActivateRtselResponseBuilder.
 * 
 * @author zkakven
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivateRtselResponseBuilderTest {

    @InjectMocks
    ActivateRtselResponseBuilder activateRtselResponseBuilder;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private NscsCommandManager commandManager;

    @Mock
    RtselCommand rtselCommand;

    @Mock
    NodeReference noderef;

    final Map<NodeReference, NscsServiceException> invalidNodesErrorMap = new HashMap<>();
    NscsCommandResponse commandResponse = null;
    private static final int NO_OF_COLUMNS = 3;
    DateFormat df;
    final NscsNameMultipleValueCommandResponse response = NscsCommandResponse.nameMultipleValue(NO_OF_COLUMNS - 1);
    private JobStatusRecord jobStatusRecord;

    @Before
    public void before() {

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jobStatusRecord = new JobStatusRecord();
        final UUID jobId = UUID.randomUUID();
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobStatusRecord.setUserId("Administrator");
        jobStatusRecord.setJobId(jobId);
        ;

    }

    @Test
    public void buildResponseForAllInvalidInputNodes() {
        invalidNodesErrorMap.put(noderef, new DuplicateNodeNamesException(NscsErrorCodes.RTSEL_CONFIG_DUPLICATE_NODE_NAMES, NscsErrorCodes.RTSEL_CONFIG_DUPLICATE_NODE_FDN)
                .setSuggestedSolution(NscsErrorCodes.RTSEL_CONFIG_DUPLICATE_NOT_ALLOWED));
        commandResponse = activateRtselResponseBuilder.buildResponseForPartialValidInputNodes(jobStatusRecord, invalidNodesErrorMap);
        commandResponse = activateRtselResponseBuilder.buildResponseForAllInvalidInputNodes(rtselCommand, invalidNodesErrorMap);
        assertNotNull(commandResponse);

    }

    @Test
    public void Test_buildResponseForAllValidInputNodes() {

        commandResponse = activateRtselResponseBuilder.buildResponseForAllValidInputNodes(jobStatusRecord);
        assertNotNull(commandResponse);
    }

    @Test
    public void Test_buildResponseForPartialValidInputNodes() {
        invalidNodesErrorMap.put(noderef, new DuplicateNodeNamesException(NscsErrorCodes.RTSEL_CONFIG_DUPLICATE_NODE_NAMES, NscsErrorCodes.RTSEL_CONFIG_DUPLICATE_NODE_FDN)
                .setSuggestedSolution(NscsErrorCodes.RTSEL_CONFIG_DUPLICATE_NOT_ALLOWED));
        commandResponse = activateRtselResponseBuilder.buildResponseForPartialValidInputNodes(jobStatusRecord, invalidNodesErrorMap);
        assertNotNull(commandResponse);
    }
}
