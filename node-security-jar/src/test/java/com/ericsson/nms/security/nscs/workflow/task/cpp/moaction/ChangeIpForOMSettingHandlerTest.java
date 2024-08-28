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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.cpp.level.CppIpSecService;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.SmrsUtils;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlOperatorUtils;
import com.ericsson.nms.security.nscs.cpp.model.RbsConfigInfo;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.sdk.resources.Resources;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.ChangeIpForOMSettingTask;

@RunWith(MockitoJUnitRunner.class)
public class ChangeIpForOMSettingHandlerTest {

    private static final NodeReference NODE = new NodeRef("MeContext=LTE05ERBS00015");
    private static final String NODE_TYPE = "ERBS";

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private ChangeIpForOMSettingTask mockChangeIpForOMSettingTask;

    @Mock
    private CppSecurityService mockSecurityService;

    @Mock
    private XmlOperatorUtils mockXmlOperatorUtils;
    @Mock
    XmlOperatorUtils.SummaryXmlInfo summaryXmlInfo;

    @Mock
    SmrsAccountInfo mockSmrsAccount;

    @Mock
    SmrsUtils mockSmrsUtils;

    @Mock
    RbsConfigInfo mockRbsConfigInfo;

    @Mock
    MoParams mockMoParams;

    @InjectMocks
    private ChangeIpForOMSettingHandler testObj;

    @Mock
    MOActionService mockMoAction;

    @Mock
    NscsCMReaderService readerService;

    @Mock
    CppIpSecService cppIpSecService;

    @Mock
    private Resources mockResources;
    private static final String PREFIX = "/dummy/path/";
    private static final String SETTING_FILE = PREFIX + "IpForOamSettingFile.xml";
    private static final String SUMMARY_FILE = PREFIX + "summary.xml";

    @Mock
    private NormalizableNodeReference normNode;

    @Before
    public void setUp() throws IOException {
        final String text = new String(Files.readAllBytes(Paths.get("src/test/resources/IpForOamSettingFile.xml")), StandardCharsets.UTF_8);

        when(mockChangeIpForOMSettingTask.getNode()).thenReturn(NODE);
        when(mockSecurityService.getSmrsAccountInfoForNode(anyString(), anyString())).thenReturn(mockSmrsAccount);
        Mockito.when(readerService.getTargetType(NODE.getFdn())).thenReturn(NODE_TYPE);
        Mockito.when(readerService.getNormalizableNodeReference(NODE)).thenReturn(normNode);
        Mockito.when(mockSecurityService.getSmrsAccountInfoForNode(NODE.getName(), NODE_TYPE)).thenReturn(mockSmrsAccount);
        final String inputXml = "<Nodes>\n" + "    <Node>\n" + "    <NodeFdn>ERBS001</NodeFdn>\n" + "    <SubAltName></SubAltName>\n"
                + "    <removeTrustOnFailure> </removeTrustOnFailure>\n" + "    <trustedCertificateFilePath></trustedCertificateFilePath>\n"
                + "    <EnableOMConfiguration1>\n" + "        <dnsServer1>2.2.2.2</dnsServer1>\n" + "        <dnsServer2>3.3.3.3</dnsServer2>\n"
                + "        <ipAddressOaMInner>3.3.3.36</ipAddressOaMInner>\n" + "        <networkPrefixLenght>32</networkPrefixLenght>\n"
                + "     <ipAccessHostEtId>3</ipAccessHostEtId>\n" + "        <defaultrouter0>1</defaultrouter0>\n"
                + "        <ipAddressOaMOuter>12.12.12.12</ipAddressOaMOuter>\n" + "        <peerIpAddress>3.2.3.2</peerIpAddress>\n"
                + "        <peerOaMIpAddress>4.3.4.3</peerOaMIpAddress>\n" + "        <peerIdentityIdFqdn>FQDN</peerIdentityIdFqdn>\n"
                + "        <peerIdentityIdType>ER</peerIdentityIdType>        \n" + "        <tsLocalIpAddressMask>32</tsLocalIpAddressMask>\n"
                + "        <tsLocalIpAddressMask-ipAddress>34.34.34.34</tsLocalIpAddressMask-ipAddress>\n"
                + "        <tsRemoteIpAddressRanges-mask>32</tsRemoteIpAddressRanges-mask>\n" + "    </EnableOMConfiguration1>\n" + "</Node>\n"
                + "    </Nodes>        \n";
        when(mockChangeIpForOMSettingTask.getUserInputXml()).thenReturn(inputXml);
        when(mockXmlOperatorUtils.generateIpForOamSettingFile(anyString(), anyString())).thenReturn(text);
        when(mockSmrsUtils.uploadFileToSmrs(Matchers.any(SmrsAccountInfo.class), Matchers.eq("IpForOamSettingFile.xml"), Matchers.any(byte[].class)))
                .thenReturn(SETTING_FILE);
        summaryXmlInfo = mockXmlOperatorUtils.new SummaryXmlInfo("abcd", "&BCD34");
        when(mockXmlOperatorUtils.getSummaryFileContent(anyString(), anyString(), Matchers.eq("SHA"))).thenReturn(summaryXmlInfo);
        when(mockSmrsAccount.getUserName()).thenReturn("user");
        when(mockSmrsAccount.getPassword()).thenReturn(new char[] { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' });
        when(mockSmrsAccount.getHost()).thenReturn("host");
        when(mockSmrsAccount.getSmrsDir()).thenReturn("/dummy/path");
        when(mockSmrsUtils.uploadFileToSmrs(Matchers.any(SmrsAccountInfo.class), Matchers.eq("summary.xml"), Matchers.any(byte[].class)))
                .thenReturn(SUMMARY_FILE);
        Mockito.doThrow(new RuntimeException()).when(mockMoAction).performMOAction(Mockito.any(String.class),
                Mockito.any(MoActionWithoutParameter.class));

    }

    @Test
    public void testProcessTask() {
        testObj.processTask(mockChangeIpForOMSettingTask);
    }

}