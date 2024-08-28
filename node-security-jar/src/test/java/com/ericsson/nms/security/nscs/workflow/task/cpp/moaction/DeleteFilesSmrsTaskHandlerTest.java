/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.SmrsUtils;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.DeleteFilesSmrsTask;

@RunWith(MockitoJUnitRunner.class)
public class DeleteFilesSmrsTaskHandlerTest {

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private DeleteFilesSmrsTask mockDeleteFilesSmrsTask;

    @Mock
    private CppSecurityService mockSecurityService;

    @Mock
    SmrsAccountInfo mockSmrsAccount;

    @Mock
    SmrsUtils mockSmrsUtils;

    @Mock
    NscsCMReaderService readerService;

    @InjectMocks
    private DeleteFilesSmrsTaskHandler testObj;

    private static final NodeReference NODE = new NodeRef("MeContext=LTE05ERBS00015");
    private static final String NODE_TYPE = "ERBS";

    @Before
    public void setUp() {
        when(mockDeleteFilesSmrsTask.getNode()).thenReturn(NODE);
        when(mockSecurityService.getSmrsAccountInfoForNode(anyString(), anyString())).thenReturn(mockSmrsAccount);
        Mockito.when(readerService.getTargetType(NODE.getFdn())).thenReturn(NODE_TYPE);
        Mockito.when(mockSecurityService.getSmrsAccountInfoForNode(NODE.getName(), NODE_TYPE)).thenReturn(mockSmrsAccount);
        when(mockSmrsUtils.deleteFilesFromSmrs(Matchers.any(SmrsAccountInfo.class), anyString())).thenReturn(true);

    }

    @Test
    public void testProcessTask() {
        testObj.processTask(mockDeleteFilesSmrsTask);
    }
}
