/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cpp.ipsec.util;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpAccessHostEt;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpHostLink;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.VpnInterface;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CppIpSecStatusUtilityTest {
	
	@Mock
	private NscsCMReaderService cMReaderService;
	
	private static final String NODE1 = "node1";

    private static final String ACTIVATED = "ACTIVATED";
    
    private static final String ME_CONTEXT = "MeContext="+ NODE1 + ",";
    @Spy
    private final Logger logger = LoggerFactory.getLogger(CppIpSecStatusUtility.class);
	
	@InjectMocks
	private CppIpSecStatusUtility beanUnderTest ;
	
	@Test
    public void testGetIpSecFeatureState_withOneNode_Positive() throws Exception {
        NodeReference nodeRef = new NodeRef(NODE1);
        setUpData_IpSec_featureState(nodeRef.getName());
        assertEquals("Should be Activated",ACTIVATED,beanUnderTest.getIpSecFeatureState(nodeRef));
    

}
@Test
public void testIsOMActivated_withOneNode_Positive() throws Exception {
        NodeReference nodeRef = new NodeRef(NODE1);
        setUpData_IpSec_featureState(nodeRef.getName());
        mockCmReaderService_om(nodeRef.getName());
        String featureState = beanUnderTest.getIpSecFeatureState(nodeRef);
        assertEquals("O&M should be Activated",true,beanUnderTest.isOMActivated(nodeRef,featureState));
    
}
@Test
public void testIsTrafficActivated_withOneNode_Positive() throws Exception {

	 NodeReference nodeRef = new NodeRef(NODE1);
        setUpData_IpSec_featureState(nodeRef.getName());
        String featureState = beanUnderTest.getIpSecFeatureState(nodeRef);
        mockCmReaderService_traffic(nodeRef.getName());
        assertEquals("Traffic should be Activated",true,beanUnderTest.isTrafficActivated(nodeRef,featureState));
    
}


 private CmResponse buildCmResponse(final String nodeName,final String attribute, final Object expectedValue) {
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


 private CmResponse setUpData_IpSec_featureState(final String nodeName){
        CmResponse cmResponse_fstate = null;
        if(cmResponse_fstate == null){
            cmResponse_fstate = buildCmResponse(NODE1, IpSec.FEATURE_STATE, ACTIVATED);
            mockCmReaderService_featureState(cmResponse_fstate);
        }
        return cmResponse_fstate;
    }
 

    private void mockCmReaderService_featureState(final CmResponse cmResponse_fstate) {

    	Mockito.when(
                cMReaderService.getMOAttribute(Mockito.any(NodeReference.class),Mockito.eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type()),
                		Mockito.eq(Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace()), Mockito.eq(IpSec.FEATURE_STATE))).thenReturn(
                cmResponse_fstate);

    }

    private CmResponse buildCmResponse_om(final String nodeName,final Object expectedValue) {

        return buildCmResponse(nodeName, IpHostLink.IP_INTERFACE_MO_REF, expectedValue);
    }
    
    private void mockCmReaderService_om(final String nodeName) {
    	/*egicass, elucbot 20160503 TORF-112729: changed ipIntfMoRef to match CppIpSecStatusUtility.prepareFdn behavior*/
    	
        String ipIntfMoRef = ME_CONTEXT+"ManagedElement=1,IpSystem=1,VpnInterface=2";
        final CmResponse cmResponse_ipIntfMoRef = buildCmResponse_om(nodeName, ipIntfMoRef);
        Mockito.when(
                cMReaderService.getMOAttribute(Mockito.any(NodeReference.class),Mockito.eq(Model.ME_CONTEXT.managedElement.ipOam.ip.ipHostLink.type()),
                		Mockito.eq(Model.ME_CONTEXT.managedElement.ipOam.ip.ipHostLink.namespace()),Mockito.eq(IpHostLink.IP_INTERFACE_MO_REF))).thenReturn(
                cmResponse_ipIntfMoRef);

        String vpnIntfId = "2";
        CmResponse cmRespVpnIntf = buildCmResponse(nodeName,VpnInterface.VPN_INTERFACE_ID, vpnIntfId );
        Mockito.when(
                cMReaderService.getMoByFdn(Mockito.eq(/*ME_CONTEXT + */ipIntfMoRef))).thenReturn(cmRespVpnIntf);
    }

    private void mockCmReaderService_traffic(final String nodeName) {        
    	/*egicass, elucbot 20160503 TORF-112729: changed ipIntfMoRef to match CppIpSecStatusUtility.prepareFdn behavior*/
        String ipIntfMoRef = ME_CONTEXT +"ManagedElement=1,IpSystem=1,VpnInterface=1";
        CmResponse cmResVpnIntfRef = buildCmResponse(nodeName, IpAccessHostEt.IP_INTERFACE_MO_REF, ipIntfMoRef );
        Mockito.when(
                cMReaderService.getMOAttribute(Mockito.any(NodeReference.class), Mockito.eq(Model.ME_CONTEXT.managedElement.ipSystem.ipAccessHostEt.type()),
                		Mockito.eq(Model.ME_CONTEXT.managedElement.ipSystem.ipAccessHostEt.namespace()), Mockito.eq(IpAccessHostEt.IP_INTERFACE_MO_REF))).thenReturn(
                cmResVpnIntfRef);

        String vpnIntfId = "1";
        CmResponse cmRespVpnIntf = buildCmResponse(nodeName, VpnInterface.VPN_INTERFACE_ID, vpnIntfId );
        Mockito.when(
                cMReaderService.getMoByFdn(Mockito.eq(/*ME_CONTEXT + */ipIntfMoRef))).thenReturn(cmRespVpnIntf);

        
    }
    
   }
