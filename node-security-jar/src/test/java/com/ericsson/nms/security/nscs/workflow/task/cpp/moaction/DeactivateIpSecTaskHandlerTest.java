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
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.cpp.level.CppIpSecService;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.SmrsUtils;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlOperatorUtils;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.DeactivateIpSecTask;

@RunWith(MockitoJUnitRunner.class)
public class DeactivateIpSecTaskHandlerTest {

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private NodeReference mockNodeRef;

    @Mock
    private DeactivateIpSecTask mockDeactivateIpSecTask;

    @Mock
    private MOActionService moAction;

    @Mock
    private CppSecurityService mockSecurityService;

    @InjectMocks
    private DeactivateIpSecTaskHandler taskHandler;

    @Mock
    private XmlOperatorUtils mockXmlOperatorUtils;

    @Mock
    private SmrsUtils smrsUtils;

    @Mock
    private SystemRecorder systemRecorder;

    @Mock
    NscsCMReaderService readerService;

    @Mock
    NormalizableNodeReference normNode;

    @Mock
    CppIpSecService cppIpSecService;

    private static final String NODE_FDN = "NODE_FDN";
    private static final String NODE_TYPE = "ERBS";
    private SmrsAccountInfo smrsAccountInfo;
    private String disableInputXml;
    private XmlOperatorUtils.SummaryXmlInfo summaryXmlInfo;

    @Before
    public void setup() throws IOException {
        smrsAccountInfo = new SmrsAccountInfo("username", "abcdef".toCharArray(), "abc.ericsson.com", getSmrsResourceAbsolutePath(), null);
        disableInputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<Node>" + "<NodeFdn>NODE004</NodeFdn>"
                + "<SubAltName>DEMO_SUB_ALT_NAME_004</SubAltName>" + "<DisableOMConfiguration>" + "<removeCert>false</removeCert>" + "<removeTrust>"
                + "<serialNumber>10001</serialNumber>" + "<issuer>ERICSSON</issuer>" + "</removeTrust>" + "<dnsServer1>192.168.0.5</dnsServer1>"
                + "<dnsServer2>192.168.1.5</dnsServer2>" + "<ipAddressOaMOuter>192.168.32.7</ipAddressOaMOuter>"
                + "<defaultRouter0>192.168.20.3</defaultRouter0>" + "<networkPrefixLength>23</networkPrefixLength>"
                + "<remoteIpAddress>192.168.20.3</remoteIpAddress>" + "</DisableOMConfiguration>" + "</Node>";
        summaryXmlInfo = mockXmlOperatorUtils.new SummaryXmlInfo("TEST", "testhash");

        Mockito.when(mockDeactivateIpSecTask.getNode()).thenReturn(mockNodeRef);
        Mockito.when(mockNodeRef.getName()).thenReturn(NODE_FDN);
        Mockito.when(mockNodeRef.getFdn()).thenReturn(NODE_FDN);
        Mockito.when(readerService.getTargetType(mockNodeRef.getFdn())).thenReturn(NODE_TYPE);
        Mockito.when(readerService.getNormalizableNodeReference(mockNodeRef)).thenReturn(normNode);
        Mockito.when(mockSecurityService.getSmrsAccountInfoForNode(mockNodeRef.getName(), NODE_TYPE)).thenReturn(smrsAccountInfo);
        Mockito.when(mockDeactivateIpSecTask.getNodesXml()).thenReturn(disableInputXml);
        Mockito.when(mockXmlOperatorUtils.getSummaryFileContent(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(summaryXmlInfo);
        Mockito.when(mockXmlOperatorUtils.generateIpForOamSettingFile(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(smrsUtils.uploadFileToSmrs(Mockito.any(SmrsAccountInfo.class), Mockito.anyString(), Mockito.any(byte[].class)))
                .thenReturn("sftp://username:[C@5381aa48@abc.ericsson.com:/tmp/smrs/IpForOamSettingFile.xml");
    }

    @Test
    public void testProcessTask() {
        nscsLogger.info("Dummy Smrs absolute path: " + getSmrsResourceAbsolutePath());
        taskHandler.processTask(mockDeactivateIpSecTask);
        Mockito.verify(moAction).performMOAction(Mockito.any(String.class), Mockito.any(MoActionWithParameter.class), Mockito.any(MoParams.class));
    }

    @Test(expected = WorkflowTaskException.class)
    public void testProcessTask_NodeXmlNull() {
        Mockito.when(mockDeactivateIpSecTask.getNodesXml()).thenReturn(null);
        taskHandler.processTask(mockDeactivateIpSecTask);
    }

    @Test(expected = RuntimeException.class)
    public void testProcessTask_PerformMOActionFailure() {
        Mockito.doThrow(new RuntimeException()).when(moAction).performMOAction(Mockito.any(String.class), Mockito.any(MoActionWithParameter.class),
                Mockito.any(MoParams.class));
        taskHandler.processTask(mockDeactivateIpSecTask);
    }

    private String getSmrsResourceAbsolutePath() {
        final URL resource = Thread.currentThread().getClass().getResource("/smrs");
        try {
            return Paths.get(resource.toURI()).toFile().getAbsolutePath();
        } catch (final Exception e) {
            nscsLogger.error("Error in getting absolute path of Smrs directory for JUnit tests, will use /tmp path " + e);
        }
        return "/tmp";
    }

}
