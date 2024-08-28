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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadCertEnrollStateIpSecTask;

@RunWith(MockitoJUnitRunner.class)
public class ReadCertEnrollStateIpSecTaskHandlerTest {

	@Mock
    private NscsLogger nscsLogger;

    @Mock
    private ReadCertEnrollStateIpSecTask mockReadCertEnrollStateIpSecTask;

    @InjectMocks
    private ReadCertEnrollStateIpSecTaskHandler testObj;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    private CmResponse readerResponse;

    @Mock
    private CmObject cmObject;

    @Mock
    private Map<String, Object> attributeMap;

    @Mock
    private NormalizableNodeReference normRef;

    private static final String FDN = "MeContext=ERBS_001";
    private static final NodeReference NODE = new NodeRef(FDN);

    @Before
    public void setup() {
        when(mockReadCertEnrollStateIpSecTask.getNodeFdn()).thenReturn(FDN);
        when(mockReadCertEnrollStateIpSecTask.getNode()).thenReturn(NODE);
        when(normRef.getNormalizedRef()).thenReturn(NODE);
        when(normRef.getFdn()).thenReturn(FDN);
        when(cmObject.getAttributes()).thenReturn(attributeMap);
        when(readerResponse.getCmObjects()).thenReturn(Arrays.asList(cmObject));
        when(
                readerService.getMOAttribute(normRef, Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type(),
                        Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace(), IpSec.CERT_ENROLL_STATE)).thenReturn(readerResponse);
        when(readerService.getNormalizableNodeReference(NODE)).thenReturn(normRef);
    }

    @Test
    public void testProcessTask() {
        final String attValue = ModelDefinition.Security.CertEnrollStateValue.IDLE.toString();
        when(attributeMap.get(IpSec.CERT_ENROLL_STATE)).thenReturn(attValue);
        final String taskResult = testObj.processTask(mockReadCertEnrollStateIpSecTask);
        assertEquals(attValue, taskResult);
    }

    @Test(expected = MissingMoAttributeException.class)
    public void test_handlerInvocationWithMissingAttribute() {
        when(readerResponse.getCmObjects()).thenReturn(new ArrayList<CmObject>());
        testObj.processTask(mockReadCertEnrollStateIpSecTask);
    }

    @Test(expected = UnexpectedErrorException.class)
    public void test_handlerInvocationWithMultipleAttribute() {
        when(readerResponse.getCmObjects()).thenReturn(Arrays.asList(cmObject, cmObject));
        testObj.processTask(mockReadCertEnrollStateIpSecTask);
    }

}
