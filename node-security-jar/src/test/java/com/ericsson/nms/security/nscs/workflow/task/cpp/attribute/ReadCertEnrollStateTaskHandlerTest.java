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
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.ReadCertEnrollStateTask;

/**
 * Unit test for ReadFileTransferClientModeTaskHandler
 * @author emaynes
 */
@RunWith(MockitoJUnitRunner.class)
public class ReadCertEnrollStateTaskHandlerTest {

	@Mock
    private NscsLogger nscsLogger;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    private CmResponse readerResponse;

    @Mock
    private CmObject cmObject;

    @Mock
    private Map<String, Object> attributeMap;

    @Mock
    private ReadCertEnrollStateTask task;

    @Mock
    private NormalizableNodeReference normRef;

    @InjectMocks
    private ReadCertEnrollStateTaskHandler handlerUnderTest;

    private static final String FDN = "MeContext=ERBS_001";
    private static final NodeReference NODE = new NodeRef(FDN);

    @Before
    public void setup() {
        when(task.getNodeFdn()).thenReturn(FDN);
        when(task.getNode()).thenReturn(NODE);
        when(normRef.getNormalizedRef()).thenReturn(NODE);
        when(normRef.getFdn()).thenReturn(FDN);
        when(cmObject.getAttributes()).thenReturn(attributeMap);
        when(readerResponse.getCmObjects()).thenReturn(Arrays.asList(cmObject));
        when(readerService.getMOAttribute(
                        normRef,
                        Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                        Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(),
                        Model.ME_CONTEXT.managedElement.systemFunctions.security.CERT_ENROLL_STATE)
        ).thenReturn(readerResponse);
        when(readerService.getNormalizableNodeReference(NODE)).thenReturn(normRef);
    }

    @Test
    public void test_handlerInvocationWithValuedAttribute() {
        final String attValue = ModelDefinition.Security.CertEnrollStateValue.IDLE.toString();
        when(attributeMap.get(Model.ME_CONTEXT.managedElement.systemFunctions.security.CERT_ENROLL_STATE)).thenReturn(attValue);

        String taskResult = handlerUnderTest.processTask(task);

        assertEquals(attValue, taskResult);

    }

    @Test(expected = MissingMoAttributeException.class)
    public void test_handlerInvocationWithMissingAttribute() {
        when(readerResponse.getCmObjects()).thenReturn(new ArrayList<CmObject>());

        String taskResult = handlerUnderTest.processTask(task);
    }
}
