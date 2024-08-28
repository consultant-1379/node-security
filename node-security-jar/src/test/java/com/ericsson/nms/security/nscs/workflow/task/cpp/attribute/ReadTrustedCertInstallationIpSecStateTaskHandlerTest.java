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
package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadTrustedCertificateInstallationIpSecStateTask;

@RunWith(MockitoJUnitRunner.class)
public class ReadTrustedCertInstallationIpSecStateTaskHandlerTest {

	@Mock
    private NscsLogger nscsLogger;

    @InjectMocks
    private ReadTrustedCertInstallationIpSecStateTaskHandler testObj;

    @Mock
    private ReadTrustedCertificateInstallationIpSecStateTask mockReadTrustedCertInstallationIpSecStateTask;

    @Mock
    private NscsCMReaderService mockReaderService;

    @Mock
    private CmResponse mockCmResponse;

    @Mock
    private CmObject mockCmObj, mockCmObj2;

    @Mock
    private NormalizableNodeReference normRef;
    
    @Mock
    private Map<String, Object> attributeMap;

    private static final String NODE_NAME = "MeContext=Node123";

    private final NodeReference nodeRef = new NodeRef(NODE_NAME);;

    @Before
    public void setUp() {
        Mockito.when(mockReadTrustedCertInstallationIpSecStateTask.getNode()).thenReturn(nodeRef);
        when(mockReaderService.getNormalizableNodeReference(nodeRef)).thenReturn(normRef);
        Mockito.when(
                mockReaderService.getMOAttribute(normRef, Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type(),
                        Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace(), IpSec.TRUSTED_CERT_INST_STATE)).thenReturn(mockCmResponse);
        Mockito.when(mockCmResponse.getCmObjects()).thenReturn(Arrays.asList(mockCmObj));
        Mockito.when(mockCmObj.getAttributes()).thenReturn(attributeMap);
        Mockito.when(attributeMap.get(IpSec.TRUSTED_CERT_INST_STATE)).thenReturn("true");
    }

    @Test
    public void testProcessTask() {
        final String result = testObj.processTask(mockReadTrustedCertInstallationIpSecStateTask);
        Assert.assertEquals("ReadTrustedCertificateInstallationIpSecStateTask completed successfully.", "true", result);
    }

    @Test(expected = MissingMoAttributeException.class)
    public void testProcessTask_MissingMoAttributeException() {
        Mockito.when(mockCmResponse.getCmObjects()).thenReturn(new ArrayList<CmObject>());
        testObj.processTask(mockReadTrustedCertInstallationIpSecStateTask);
    }

    @Test(expected = UnexpectedErrorException.class)
    public void testProcessTask_UnexpectedErrorException() {
        Mockito.when(mockCmResponse.getCmObjects()).thenReturn(Arrays.asList(mockCmObj, mockCmObj2));
        testObj.processTask(mockReadTrustedCertInstallationIpSecStateTask);
    }

}
