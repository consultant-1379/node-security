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
package com.ericsson.nms.security.nscs.handler.command.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.HttpsCommand;
import com.ericsson.nms.security.nscs.api.exception.HttpsActivateOrDeactivateWfException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.util.CommandType;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@RunWith(MockitoJUnitRunner.class)
public class HttpsCommandHandlerHelperTest {

    private static final NscsCommandType ACTIVATE_COMMAND = CommandType.ACTIVATE.getNscsCommandType();
    private static final NscsCommandType DEACTIVATE_COMMAND = CommandType.DEACTIVATE.getNscsCommandType();

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private NscsCommandManager nscsCommandManager;

    @Mock
    private NscsJobCacheHandler nscsJobCacheHandler;

    @Mock
    private CommandContext commandContext;

    @InjectMocks
    private HttpsCommandHandlerHelper commandHandlerHelper;

    @Mock
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @Mock
    private NscsContextService nscsContextService;

    private HttpsCommand httpsCommand;

    @Before
    public void setUp() {

        httpsCommand = new HttpsCommand();
    }

    @Test
    public void processActivateShouldInvokeHttpsActivateMethod() {

        JobStatusRecord statusRecord = getJobStatusRecordMock(ACTIVATE_COMMAND);

        commandHandlerHelper.processActivate(httpsCommand, commandContext);

        verify(nscsCommandManager, times(1)).executeActivateHttpsWfs(Collections.<NodeReference>emptyList(), statusRecord);
    }

    @Test
    public void processDeactivateShouldInvokeHttpsDeactivateMethod() {

        JobStatusRecord statusRecord = getJobStatusRecordMock(DEACTIVATE_COMMAND);

        commandHandlerHelper.processDeactivate(httpsCommand, commandContext);

        verify(nscsCommandManager, times(1)).executeDeactivateHttpsWfs(Collections.<NodeReference>emptyList(), statusRecord);
    }

    @Test(expected = HttpsActivateOrDeactivateWfException.class)
    public void processShouldReThrowException() {

        when(nscsJobCacheHandler.insertJob(any(NscsCommandType.class))).thenThrow(HttpsActivateOrDeactivateWfException.class);
        commandHandlerHelper.processActivate(httpsCommand, commandContext);
    }

    private JobStatusRecord getJobStatusRecordMock(NscsCommandType commandType) {
        JobStatusRecord statusRecord = mock(JobStatusRecord.class);

        if (commandType.equals(ACTIVATE_COMMAND)) {
            when(nscsJobCacheHandler.insertJob(ACTIVATE_COMMAND)).thenReturn(statusRecord);
        } else {
            when(nscsJobCacheHandler.insertJob(DEACTIVATE_COMMAND)).thenReturn(statusRecord);
        }
        when(statusRecord.getJobId()).thenReturn(UUID.randomUUID());
        return statusRecord;
    }
}
