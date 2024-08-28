package com.ericsson.nms.security.nscs.data.moget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.exception.InvalidNodeTypeException;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.moget.impl.ComEcimMOGetServiceImpl;
import com.ericsson.nms.security.nscs.data.moget.impl.CppMOGetServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class MOGetServiceFactoryTest {

    private static final String COM_ECIM_FAMILY = "ECIM";
    private static final String CPP_FAMILY = "CPP";

    private static final String UNKNOWN_NODE_NAME = "UNKNOWN";
    private static final String ERBS_NODE_NAME = "ERBS123";
    private static final String MGW_NODE_NAME = "MGW123";
    private static final String SGSN_NODE_NAME = "SGSN123";
    private static final String SBG_NODE_NAME = "SBG123";
    private static final String VSBG_NODE_NAME = "VSBG123";
    private static final String CSCF_NODE_NAME = "CSCF123";
    private static final String VCSCF_NODE_NAME = "VCSCF123";
    private static final String MTAS_NODE_NAME = "MTAS123";
    private static final String VMTAS_NODE_NAME = "VMTAS123";
    private static final String MSRBSV1_NODE_NAME = "MSRBSV1123";
    private static final String SAPC_NODE_NAME = "SAPC123";
    private static final String RNNODE_NODE_NAME = "RNNODE123";
    private static final String VPP_NODE_NAME = "VPP123";
    private static final String VRM_NODE_NAME = "VRM123";
    private static final String VRC_NODE_NAME = "VRC123";
    private static final String VEME_NODE_NAME = "VEME123";
    private static final String VWCG_NODE_NAME = "VWCG123";
    private static final String HSSFE_NODE_NAME = "HSSFE123";
    private static final String VHSSFE_NODE_NAME = "VHSSFE123";
    private static final String VIPWORKS_NODE_NAME = "VIPWORKS123";
    private static final String VUPG_NODE_NAME = "VUPG123";
    private static final String BSP_NODE_NAME = "BSP123";
    private static final String VBGF_NODE_NAME = "VBGF123";
    private static final String VMRF_NODE_NAME = "VMRF123";
    private static final String EPG_NODE_NAME = "EPG123";
    private static final String VEPG_NODE_NAME = "VEPG123";
    private static final String RADIO_NODE_NAME = "DUG2123";
    private static final String RADIOTNODE_NODE_NAME = "RADIOTNODE2123";
    private static final String ER6000_NODE_NAME = "ER6000-123";
    private static final String MINI_LINK_INDOOR_NODE_NAME = "MINI-LINK-Indoor123";
    private static final String BSC_NODE_NAME = "BSC123";
    private static final String FIVE_G_RADIO_NODE_NAME = "5GRADIONODE123";
    private static final String VTF_RADIONODE = "VTFRadioNode";
    private static final String VSD_NODE_NAME = "VSDNode";
    private static final String RVNFM_NODE_NAME = "RVNFMNode";
    private static final String HLR_FE_NODE_NAME = "HLRFE123";
    private static final String vHLR_FE_NODE_NAME = "vHLRFE123";
    private static final String HLR_FE_BSP_NODE_NAME = "HLRFEBSP123";
    private static final String HLR_FE_IS_NODE_NAME = "HLRFEIS123";
    private static final String VRSM_NODE_NAME = "VRSM123";

    private final NodeReference nullNodeRef = null;
    private final NodeReference unknownNodeRef = new NodeRef(UNKNOWN_NODE_NAME);
    private final NodeReference erbsNodeRef = new NodeRef(ERBS_NODE_NAME);
    private final NodeReference mgwNodeRef = new NodeRef(MGW_NODE_NAME);
    private final NodeReference sgsnNodeRef = new NodeRef(SGSN_NODE_NAME);
    private final NodeReference sbgNodeRef = new NodeRef(SBG_NODE_NAME);
    private final NodeReference vsbgNodeRef = new NodeRef(VSBG_NODE_NAME);
    private final NodeReference cscfNodeRef = new NodeRef(CSCF_NODE_NAME);
    private final NodeReference vcscfNodeRef = new NodeRef(VCSCF_NODE_NAME);
    private final NodeReference mtasNodeRef = new NodeRef(MTAS_NODE_NAME);
    private final NodeReference vmtasNodeRef = new NodeRef(VMTAS_NODE_NAME);
    private final NodeReference msrbsv1NodeRef = new NodeRef(MSRBSV1_NODE_NAME);
    private final NodeReference sapcNodeRef = new NodeRef(SAPC_NODE_NAME);
    private final NodeReference rnNodeNodeRef = new NodeRef(RNNODE_NODE_NAME);
    private final NodeReference vppNodeRef = new NodeRef(VPP_NODE_NAME);
    private final NodeReference vrmNodeRef = new NodeRef(VRM_NODE_NAME);
    private final NodeReference vrcNodeRef = new NodeRef(VRC_NODE_NAME);
    private final NodeReference vEMENodeRef = new NodeRef(VEME_NODE_NAME);
    private final NodeReference vWCGNodeRef = new NodeRef(VWCG_NODE_NAME);
    private final NodeReference hSSFENodeRef = new NodeRef(HSSFE_NODE_NAME);
    private final NodeReference vhSSFENodeRef = new NodeRef(VHSSFE_NODE_NAME);
    private final NodeReference vIPWorksNodeRef = new NodeRef(VIPWORKS_NODE_NAME);
    private final NodeReference vUPGNodeRef = new NodeRef(VUPG_NODE_NAME);
    private final NodeReference bSPNodeRef = new NodeRef(BSP_NODE_NAME);
    private final NodeReference vBGFNodeRef = new NodeRef(VBGF_NODE_NAME);
    private final NodeReference vMRFNodeRef = new NodeRef(VMRF_NODE_NAME);
    private final NodeReference epgNodeRef = new NodeRef(EPG_NODE_NAME);
    private final NodeReference vepgNodeRef = new NodeRef(VEPG_NODE_NAME);
    private final NodeReference radioNodeRef = new NodeRef(RADIO_NODE_NAME);
    private final NodeReference radioTNodeRef = new NodeRef(RADIOTNODE_NODE_NAME);
    private final NodeReference er6000NodeRef = new NodeRef(ER6000_NODE_NAME);
    private final NodeReference miniLinkIndoorNodeRef = new NodeRef(MINI_LINK_INDOOR_NODE_NAME);
    private final NodeReference bscNodeRef = new NodeRef(BSC_NODE_NAME);
    private final NodeReference fiveGRadioNodeRef = new NodeRef(FIVE_G_RADIO_NODE_NAME);
    private final NodeReference vtfRadioNodeRef = new NodeRef(VTF_RADIONODE);
    private final NodeReference vsdNodeRef = new NodeRef(VSD_NODE_NAME);
    private final NodeReference rVNFMNodeRef = new NodeRef(RVNFM_NODE_NAME);
    private final NodeReference hlrfeNodeRef = new NodeRef(HLR_FE_NODE_NAME);
    private final NodeReference vhlrfeNodeRef = new NodeRef(vHLR_FE_NODE_NAME);
    private final NodeReference hlrfebspNodeRef = new NodeRef(HLR_FE_BSP_NODE_NAME);
    private final NodeReference hlrfeisNodeRef = new NodeRef(HLR_FE_IS_NODE_NAME);
    private final NodeReference vrsmNodeRef = new NodeRef(VRSM_NODE_NAME);

    @Spy
    private final Logger logger = LoggerFactory.getLogger(MOGetServiceFactory.class);

    @InjectMocks
    private MOGetServiceFactory beanUnderTest;

    @Mock
    private BeanManager beanManager;

    @Mock
    private Bean<?> bean;

    @Mock
    private CreationalContext creationalContext;

    @InjectMocks
    ComEcimMOGetServiceImpl comEcimCertificateStateInfo;

    @InjectMocks
    CppMOGetServiceImpl cppCertificateStateInfo;

    @Mock
    NscsCapabilityModelService capabilityService;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        when(capabilityService.getMomType(nullNodeRef)).thenThrow(NscsCapabilityModelException.class);
        when(capabilityService.getMomType(unknownNodeRef)).thenThrow(NscsCapabilityModelException.class);
        when(capabilityService.getMomType(erbsNodeRef)).thenReturn(CPP_FAMILY);
        when(capabilityService.getMomType(mgwNodeRef)).thenReturn(CPP_FAMILY);
        when(capabilityService.getMomType(msrbsv1NodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(sapcNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(rnNodeNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vppNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vrmNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vrcNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vEMENodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vWCGNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(hSSFENodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vhSSFENodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vIPWorksNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vUPGNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(bSPNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vBGFNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vMRFNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(epgNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vepgNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(radioNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(radioTNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(sgsnNodeRef)).thenThrow(NscsCapabilityModelException.class);
        when(capabilityService.getMomType(sbgNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vsbgNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(cscfNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vcscfNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(mtasNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vmtasNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(bscNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(fiveGRadioNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vtfRadioNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vsdNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(rVNFMNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(er6000NodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(miniLinkIndoorNodeRef)).thenThrow(NscsCapabilityModelException.class);
        when(capabilityService.getMomType(hlrfeNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vhlrfeNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(hlrfebspNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(hlrfeisNodeRef)).thenReturn(COM_ECIM_FAMILY);
        when(capabilityService.getMomType(vrsmNodeRef)).thenReturn(COM_ECIM_FAMILY);

        final Set<Bean<?>> beans = new HashSet<Bean<?>>();
        beans.add(bean);
        when(beanManager.getBeans(any(Type.class), any(Annotation.class))).thenReturn(beans);
        when(beanManager.createCreationalContext(bean)).thenReturn(creationalContext);
        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(comEcimCertificateStateInfo);
    }

    @Test(expected = InvalidNodeTypeException.class)
    public void testGetMOGetServiceTypeStringForNullNodeReference() {
        beanUnderTest.getMOGetServiceTypeString(nullNodeRef);
    }

    @Test
    public void testGetMOGetServiceTypeStringForErbsNodeReference() {
        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(cppCertificateStateInfo);
        assertEquals(beanUnderTest.getMOGetServiceTypeString(erbsNodeRef), CPP_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForMgwNodeReference() {
        when(beanManager.getReference(Mockito.eq(bean), any(Type.class), Mockito.eq(creationalContext))).thenReturn(cppCertificateStateInfo);
        assertEquals(beanUnderTest.getMOGetServiceTypeString(mgwNodeRef), CPP_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForMSRBSV1NodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(msrbsv1NodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForSAPCNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(sapcNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForRNNODENodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(rnNodeNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVPPNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vppNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVRMNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vrmNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVRCNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vrcNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVEMENodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vEMENodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForHSSFENodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(hSSFENodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVHSSFENodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vhSSFENodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVWCGNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vWCGNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVIPWORKSNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vIPWorksNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVUPGNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vUPGNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForBSPNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(bSPNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVBGFNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vBGFNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVMRFNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vMRFNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForEPGNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(epgNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVEPGNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vepgNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForRadioNodeNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(radioNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForRadioTNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(radioTNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForBscReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(bscNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForHlrfeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(hlrfeNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForvHlrfeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vhlrfeNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForHlrfebspReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(hlrfebspNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForHlrfeisReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(hlrfeisNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringFor5GRadioNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(fiveGRadioNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVTFRadioNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vtfRadioNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVSDNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vsdNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForRVNFMNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(rVNFMNodeRef), COM_ECIM_FAMILY);
    }

    @Test(expected = InvalidNodeTypeException.class)
    public void testGetMOGetServiceTypeStringForSgsnNodeReference() {
        assertNotNull(beanUnderTest.getMOGetServiceTypeString(sgsnNodeRef));
    }

    @Test
    public void testGetMOGetServiceTypeStringForSbgNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(sbgNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVSBGNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vsbgNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForCscfNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(cscfNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVCSCFNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vcscfNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForMtasNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(mtasNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForVMTASNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vmtasNodeRef), COM_ECIM_FAMILY);
    }

    @Test
    public void testGetMOGetServiceTypeStringForEr6000NodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(er6000NodeRef), COM_ECIM_FAMILY);
    }

    @Test(expected = InvalidNodeTypeException.class)
    public void testGetMOGetServiceTypeStringForMiniLinkIndoorNodeReference() {
        assertNotNull(beanUnderTest.getMOGetServiceTypeString(miniLinkIndoorNodeRef));
    }

    @Test
    public void testGetMOGetServiceTypeStringForVRSMNodeReference() {
        assertEquals(beanUnderTest.getMOGetServiceTypeString(vrsmNodeRef), COM_ECIM_FAMILY);
    }
}
