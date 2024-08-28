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
package com.ericsson.nms.security.nscs.handler.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.UnassociatedNetworkElementException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NodeValidatorUtility;

@RunWith(MockitoJUnitRunner.class)
public class RtselCommonValidatorTest {

    @Mock
    private NormalizableNodeReference normNode;

    @Mock
    protected NscsCapabilityModelService nscsCapabilityModelService;

    @InjectMocks
    RtselCommonValidator rtselCommonValidator;

    @Mock
    NodeValidatorUtility nodeValidatorUtility;

    @Mock
    protected NscsCMReaderService reader;

    @Mock
    NodeModelInformation nodemodelInfo;

    @Mock
    protected NscsLogger nscsLogger;

    NodeReference nodeRef = new NodeRef("LTE02ERBS00004");

    @Test
    public void testValidateNode() {
        final List<String> unSupported = new ArrayList<String>();
        unSupported.add("RadioNode-16A-R13NV");
        final NodeModelInformation nodemodelInfo = new NodeModelInformation("16B-R28GY", ModelIdentifierType.MIM_VERSION, "MGW");
        Mockito.when(nodeValidatorUtility.validateNode(nodeRef)).thenReturn(true);
        Mockito.when(normNode.getNeType()).thenReturn("MGW");
        Mockito.when(nscsCapabilityModelService.isCliCommandSupported(normNode, NscsCapabilityModelService.RTSEL_COMMAND)).thenReturn(true);
        Mockito.when(normNode.getFdn()).thenReturn("LTE02ERBS00004");
        Mockito.when(reader.getNodeModelInformation(normNode.getFdn())).thenReturn(nodemodelInfo);
        rtselCommonValidator.validateNode(normNode, nodeRef, NscsCommandType.RTSEL_ACTIVATE);
        Mockito.verify(nodeValidatorUtility).validateNode(nodeRef);
    }

    @Test(expected = InvalidNodeNameException.class)
    public void testInvalidNodeNameException() {
        rtselCommonValidator.validateNode(null, nodeRef, NscsCommandType.RTSEL_ACTIVATE);
        Mockito.verify(nscsLogger).error("Invalid Node Name [{}]", nodeRef.getFdn());
    }

    @Test(expected = UnassociatedNetworkElementException.class)
    public void testUnassociatedNetworkElementException() {
        Mockito.when(reader.exists(nodeRef.getFdn())).thenReturn(true);
        rtselCommonValidator.validateNode(null, nodeRef, NscsCommandType.RTSEL_ACTIVATE);
        Mockito.verify(nscsLogger).error("NetworkElement [{}] is not associated to any MO.", nodeRef.getFdn());
    }

    @Test(expected = UnsupportedNodeTypeException.class)
    public void testUnsupportedNodeTypeException() {
        Mockito.when(nodeValidatorUtility.validateNode(nodeRef)).thenReturn(true);
        Mockito.when(normNode.getNeType()).thenReturn("MGW");
        final String neType = "MGW";
        final String errorMsg = String.format("Unsupported node type %s for %s.", neType, NscsCapabilityModelService.RTSEL_COMMAND);
        Mockito.when(nscsCapabilityModelService.isCliCommandSupported(normNode, NscsCapabilityModelService.RTSEL_COMMAND)).thenReturn(false);
        rtselCommonValidator.validateNode(normNode, nodeRef, NscsCommandType.RTSEL_ACTIVATE);
        Mockito.verify(nscsLogger).error("NE Type validation failed: {}", errorMsg);
    }

}
