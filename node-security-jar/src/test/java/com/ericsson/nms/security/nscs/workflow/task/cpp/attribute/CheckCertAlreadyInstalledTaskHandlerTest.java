package com.ericsson.nms.security.nscs.workflow.task.cpp.attribute;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.attribute.CheckCertAlreadyInstalledTask;

/**
 * Created with IntelliJ IDEA. User: ediniku Date: 06/11/14 Time: 16:58 To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class CheckCertAlreadyInstalledTaskHandlerTest {

	@Mock
    private NscsLogger nscsLogger;

    private static final NodeReference NODE = new NodeRef("MeContext=LTE05ERBS00015");

    @InjectMocks
    private CheckCertAlreadyInstalledTaskHandler ckCertAlInsTskHndlr;

    @Mock
    private CheckCertAlreadyInstalledTask ckCertAlInsTskHndlrTsk;

    @Mock
    private NscsCMReaderService mockReaderService;
    
    @Mock
    private CppSecurityService mockSecurityService;

    private Map<String, Object> map = new HashMap<String, Object>();

    private static final String SUBJECT_ALT_NAME = "subjectAltName";
    private static final String SUBJECT_ALT_NAME_VALUE = "subjectAltNameValue";
    private static final String NOT_VALID_AFTER = "notValidAfter";

    private void setUp_positive() throws Exception {
        when(ckCertAlInsTskHndlrTsk.getNode()).thenReturn(NODE);
        CmResponse cmResponse = buildCmResponse(NODE.getName(), ModelDefinition.IpSec.CERTIFICATE, createResponseMap());
        when(
                mockReaderService.getMOAttribute(any(NodeReference.class), eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type()),
                        eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace()), eq(ModelDefinition.IpSec.CERTIFICATE)))
                .thenReturn(cmResponse);
        when(ckCertAlInsTskHndlrTsk.getSubjectAltName()).thenReturn(SUBJECT_ALT_NAME_VALUE);
    }

    private void setUp_negative1() throws Exception {
        when(ckCertAlInsTskHndlrTsk.getNode()).thenReturn(NODE);
        CmResponse cmResponse = buildCmResponse_empty(NODE.getName());
        when(
                mockReaderService.getMOAttribute(any(NodeReference.class), eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type()),
                        eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace()), eq(ModelDefinition.IpSec.CERTIFICATE)))
                .thenReturn(cmResponse);
        when(ckCertAlInsTskHndlrTsk.getSubjectAltName()).thenReturn(SUBJECT_ALT_NAME_VALUE);
    }

    private void setUp_negative2() throws Exception {
        when(ckCertAlInsTskHndlrTsk.getNode()).thenReturn(NODE);
        CmResponse cmResponse = buildCmResponse_negative(NODE.getName(), createResponseMap());
        when(
                mockReaderService.getMOAttribute(any(NodeReference.class), eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type()),
                        eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace()), eq(ModelDefinition.IpSec.CERTIFICATE)))
                .thenReturn(cmResponse);
        when(ckCertAlInsTskHndlrTsk.getSubjectAltName()).thenReturn(SUBJECT_ALT_NAME_VALUE);
    }

    private void setUp_negative() throws Exception {
        when(ckCertAlInsTskHndlrTsk.getNode()).thenReturn(NODE);
        map.put(SUBJECT_ALT_NAME, SUBJECT_ALT_NAME_VALUE);
        when(ckCertAlInsTskHndlrTsk.getNode()).thenReturn(NODE);
        CmResponse cmResponse = buildCmResponse(NODE.getName(), ModelDefinition.IpSec.CERTIFICATE, map);
        when(
                mockReaderService.getMOAttribute(any(NodeReference.class), eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type()),
                        eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace()), eq(ModelDefinition.IpSec.CERTIFICATE)))
                .thenReturn(cmResponse);
        when(ckCertAlInsTskHndlrTsk.getSubjectAltName()).thenReturn(SUBJECT_ALT_NAME_VALUE);
    }

    @Test
    public void testProcessTask_positive() throws Exception {
        setUp_positive();
        Assert.assertEquals("Expected Installed response", "INSTALLED", ckCertAlInsTskHndlr.processTask(ckCertAlInsTskHndlrTsk));
    }

    @Test
    public void testProcessTask_negative_expired() throws Exception {
        setUp_negative();
        map.put(NOT_VALID_AFTER, "130721153020Z");
        Assert.assertEquals("Expected Installed response", "NOT_INSTALLED", ckCertAlInsTskHndlr.processTask(ckCertAlInsTskHndlrTsk));
    }

    @Test
    public void testProcessTask_negative_null_subjaltname() throws Exception {
        setUp_negative();
        map.put(SUBJECT_ALT_NAME, null);
        Assert.assertEquals("Expected Installed response", "NOT_INSTALLED", ckCertAlInsTskHndlr.processTask(ckCertAlInsTskHndlrTsk));
    }
    
    @Test
    public void testProcessTask_negative_empty_subjaltname() throws Exception {
        setUp_negative();
        when(ckCertAlInsTskHndlrTsk.getSubjectAltName()).thenReturn("");
        Assert.assertEquals("Expected Installed response", "NOT_INSTALLED", ckCertAlInsTskHndlr.processTask(ckCertAlInsTskHndlrTsk));
    }

    @Test
    public void testProcessTask_negative_expired_exception() throws Exception {
        setUp_negative();
        map.put(NOT_VALID_AFTER, "130721153XXZ");
        Assert.assertEquals("Expected Installed response", "NOT_INSTALLED", ckCertAlInsTskHndlr.processTask(ckCertAlInsTskHndlrTsk));
    }

    @Test(expected = MissingMoAttributeException.class)
    public void testProcessTask_negative_emptyResponse() throws Exception {
        setUp_negative1();
        ckCertAlInsTskHndlr.processTask(ckCertAlInsTskHndlrTsk);
    }

    @Test(expected = UnexpectedErrorException.class)
    public void testProcessTask_negative_more_than_one_response() throws Exception {
        setUp_negative2();
        ckCertAlInsTskHndlr.processTask(ckCertAlInsTskHndlrTsk);
    }

    private CmResponse buildCmResponse(final String nodeName, final String attribute, final Object expectedValue) {
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();

        attributesMap.put(attribute, expectedValue);

        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        cmObject.setFdn("MeContext=" + nodeName);
        cmObjects.add(cmObject);

        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        return cmResponse;

    }

    private CmResponse buildCmResponse_empty(final String nodeName) {
        final CmResponse cmResponse = new CmResponse();
        cmResponse.setStatusCode(0);
        return cmResponse;

    }

    private CmResponse buildCmResponse_negative(final String nodeName, final Map<String, Object> attributesMap) {
        final CmResponse cmResponse = new CmResponse();
        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        cmObject.setFdn("MeContext=" + nodeName);
        cmObjects.add(cmObject);
        final CmObject cmObject1 = new CmObject();
        cmObjects.add(cmObject1);

        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        return cmResponse;

    }

    private Map<String, Object> createResponseMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(SUBJECT_ALT_NAME, SUBJECT_ALT_NAME_VALUE);
        map.put(NOT_VALID_AFTER, "390721153020Z");
        return map;
    }
}
