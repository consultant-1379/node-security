package com.ericsson.nms.security.nscs.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ericsson.nms.security.nscs.data.ModelDefinition.CertM;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;

/**
 * Created by emaynes on 07/05/2014.
 */
public class ModelTest {

    @Test
    public void urnTest() {
        final String version = "1.1.1";
        final String namespace = Model.ME_CONTEXT.namespace();
        final String type = Model.ME_CONTEXT.type();

        Assert.assertNotNull("model namespace should not be null", namespace);
        Assert.assertNotNull("model type should not be null", type);

        Assert.assertEquals(String.format("//%s/%s/%s", namespace, type, version), Model.ME_CONTEXT.urn(version));
    }

    @Test
    public void typeAndNamespaceTest() {

        Assert.assertEquals("NetworkElement", Model.NETWORK_ELEMENT.type());
        Assert.assertEquals("OSS_NE_DEF", Model.NETWORK_ELEMENT.namespace());
        Assert.assertEquals(ModelDefinition.NE_NS, Model.NETWORK_ELEMENT.namespace());

        Assert.assertEquals("SecurityFunction", Model.NETWORK_ELEMENT.securityFunction.type());
        Assert.assertEquals("OSS_NE_SEC_DEF", Model.NETWORK_ELEMENT.securityFunction.namespace());
        Assert.assertEquals(ModelDefinition.NE_SEC_NS, Model.NETWORK_ELEMENT.securityFunction.namespace());

        Assert.assertEquals("CmFunction", Model.NETWORK_ELEMENT.cmFunction.type());
        Assert.assertEquals("OSS_NE_CM_DEF", Model.NETWORK_ELEMENT.cmFunction.namespace());
        Assert.assertEquals(ModelDefinition.NE_CM_NS, Model.NETWORK_ELEMENT.cmFunction.namespace());

        Assert.assertEquals("NetworkElementSecurity", Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type());
        Assert.assertEquals("OSS_NE_SEC_DEF", Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace());
        Assert.assertEquals(ModelDefinition.NE_SEC_NS, Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace());

        Assert.assertEquals("MeContext", Model.ME_CONTEXT.type());
        Assert.assertEquals("OSS_TOP", Model.ME_CONTEXT.namespace());
        Assert.assertEquals(ModelDefinition.OSS_TOP_NS, Model.ME_CONTEXT.namespace());
    }

    private void cppTypeAndNamespaceTest(final CppManagedElement cppManagedElement, final String namespace) {

        Assert.assertEquals("ManagedElement", cppManagedElement.type());
        Assert.assertEquals(namespace, cppManagedElement.namespace());

        Assert.assertEquals("SystemFunctions", cppManagedElement.systemFunctions.type());
        Assert.assertEquals(namespace, cppManagedElement.systemFunctions.namespace());

        Assert.assertEquals("Security", cppManagedElement.systemFunctions.security.type());
        Assert.assertEquals(namespace, cppManagedElement.systemFunctions.security.namespace());

        Assert.assertEquals("IpSystem", cppManagedElement.ipSystem.type());
        Assert.assertEquals(namespace, cppManagedElement.ipSystem.namespace());

        Assert.assertEquals("IpSec", cppManagedElement.ipSystem.ipSec.type());
        Assert.assertEquals(namespace, cppManagedElement.ipSystem.ipSec.namespace());

        Assert.assertEquals("IpAccessSctp", cppManagedElement.ipSystem.ipAccessSctp.type());
        Assert.assertEquals(namespace, cppManagedElement.ipSystem.ipAccessSctp.namespace());

        Assert.assertEquals("IpAccessHostEt", cppManagedElement.ipSystem.ipAccessHostEt.type());
        Assert.assertEquals(namespace, cppManagedElement.ipSystem.ipAccessHostEt.namespace());

        Assert.assertEquals("IpSyncRef", cppManagedElement.ipSystem.ipAccessHostEt.ipSyncRef.type());
        Assert.assertEquals(namespace, cppManagedElement.ipSystem.ipAccessHostEt.ipSyncRef.namespace());

        Assert.assertEquals("VpnInterface", cppManagedElement.ipSystem.vpnInterface.type());
        Assert.assertEquals(namespace, cppManagedElement.ipSystem.vpnInterface.namespace());

        Assert.assertEquals("IpOam", cppManagedElement.ipOam.type());
        Assert.assertEquals(namespace, cppManagedElement.ipOam.namespace());

        Assert.assertEquals("Ip", cppManagedElement.ipOam.ip.type());
        Assert.assertEquals(namespace, cppManagedElement.ipOam.ip.namespace());

        Assert.assertEquals("IpHostLink", cppManagedElement.ipOam.ip.ipHostLink.type());
        Assert.assertEquals(namespace, cppManagedElement.ipOam.ip.ipHostLink.namespace());

        Assert.assertEquals("ENodeBFunction", cppManagedElement.eNodeBFunction.type());
        Assert.assertEquals(namespace, cppManagedElement.eNodeBFunction.namespace());

        Assert.assertEquals("RbsConfiguration", cppManagedElement.eNodeBFunction.rbsConfiguration.type());
        Assert.assertEquals(namespace, cppManagedElement.eNodeBFunction.rbsConfiguration.namespace());

        Assert.assertEquals("TransportNetwork", cppManagedElement.transportNetwork.type());
        Assert.assertEquals(namespace, cppManagedElement.transportNetwork.namespace());

        Assert.assertEquals("Sctp", cppManagedElement.transportNetwork.sctp.type());
        Assert.assertEquals(namespace, cppManagedElement.transportNetwork.sctp.namespace());

        Assert.assertEquals("NodeManagementFunction", cppManagedElement.nodeManagementFunction.type());
        Assert.assertEquals(namespace, cppManagedElement.nodeManagementFunction.namespace());

        Assert.assertEquals("RbsConfiguration", cppManagedElement.nodeManagementFunction.rbsConfiguration.type());
        Assert.assertEquals(namespace, cppManagedElement.nodeManagementFunction.rbsConfiguration.namespace());
    }

    @Test
    public void erbsTypeAndNamespaceTest() {

        Assert.assertEquals("ERBS_NODE_MODEL", ModelDefinition.ERBS_MODEL_NS);
        cppTypeAndNamespaceTest(Model.ME_CONTEXT.managedElement, "ERBS_NODE_MODEL");
    }

    @Test
    public void rncTypeAndNamespaceTest() {
        Assert.assertEquals("RNC_NODE_MODEL", ModelDefinition.RNC_MODEL_NS);
        cppTypeAndNamespaceTest(Model.ME_CONTEXT.rncManagedElement, "RNC_NODE_MODEL");
    }

    @Test
    public void rbsTypeAndNamespaceTest() {
        Assert.assertEquals("RBS_NODE_MODEL", ModelDefinition.RBS_MODEL_NS);
        cppTypeAndNamespaceTest(Model.ME_CONTEXT.rbsManagedElement, "RBS_NODE_MODEL");
    }

    @Test
    public void mgwTypeAndNamespaceTest() {

        Assert.assertEquals("MGW_NODE_MODEL", ModelDefinition.MGW_MODEL_NS);
        cppTypeAndNamespaceTest(Model.ME_CONTEXT.mgwManagedElement, "MGW_NODE_MODEL");
    }

    @Test
    public void mrsTypeAndNamespaceTest() {

        Assert.assertEquals("MRS_NODE_MODEL", ModelDefinition.MRS_MODEL_NS);
        cppTypeAndNamespaceTest(Model.ME_CONTEXT.mrsManagedElement, "MRS_NODE_MODEL");
    }

    @Test
    public void comEcimTypeNamespaceTest() {
        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
    }

    private void comEcimTypeAndNamespaceTest(final ComEcimManagedElement comEcimManagedElement, final String topNamespace, final String secMNamespace,
            final String certMNamespace, final String sysMNamespace, final String oamAccessPointNamespace, final String ikev2PolicyProfileNamespace) {

        Assert.assertEquals("ManagedElement", comEcimManagedElement.type());
        Assert.assertEquals(topNamespace, comEcimManagedElement.namespace());

        Assert.assertEquals("SystemFunctions", comEcimManagedElement.systemFunctions.type());
        Assert.assertEquals(topNamespace, comEcimManagedElement.systemFunctions.namespace());

        Assert.assertEquals("SecM", comEcimManagedElement.systemFunctions.secM.type());
        Assert.assertEquals(secMNamespace, comEcimManagedElement.systemFunctions.secM.namespace());

        Assert.assertEquals("CertM", comEcimManagedElement.systemFunctions.secM.certM.type());
        Assert.assertEquals(certMNamespace, comEcimManagedElement.systemFunctions.secM.certM.namespace());

        Assert.assertEquals("VendorCredential", comEcimManagedElement.systemFunctions.secM.certM.vendorCredential.type());
        Assert.assertEquals(certMNamespace, comEcimManagedElement.systemFunctions.secM.certM.vendorCredential.namespace());

        Assert.assertEquals("EnrollmentAuthority", comEcimManagedElement.systemFunctions.secM.certM.enrollmentAuthority.type());
        Assert.assertEquals(certMNamespace, comEcimManagedElement.systemFunctions.secM.certM.enrollmentAuthority.namespace());

        Assert.assertEquals("NodeCredential", comEcimManagedElement.systemFunctions.secM.certM.nodeCredential.type());
        Assert.assertEquals(certMNamespace, comEcimManagedElement.systemFunctions.secM.certM.nodeCredential.namespace());

        Assert.assertEquals("TrustCategory", comEcimManagedElement.systemFunctions.secM.certM.trustCategory.type());
        Assert.assertEquals(certMNamespace, comEcimManagedElement.systemFunctions.secM.certM.trustCategory.namespace());

        Assert.assertEquals("TrustedCertificate", comEcimManagedElement.systemFunctions.secM.certM.trustedCertificate.type());
        Assert.assertEquals(certMNamespace, comEcimManagedElement.systemFunctions.secM.certM.trustedCertificate.namespace());

        Assert.assertEquals("CertMCapabilities", comEcimManagedElement.systemFunctions.secM.certM.certMCapabilities.type());
        Assert.assertEquals(certMNamespace, comEcimManagedElement.systemFunctions.secM.certM.certMCapabilities.namespace());

        Assert.assertEquals("EnrollmentServerGroup", comEcimManagedElement.systemFunctions.secM.certM.enrollmentServerGroup.type());
        Assert.assertEquals(certMNamespace, comEcimManagedElement.systemFunctions.secM.certM.enrollmentServerGroup.namespace());

        Assert.assertEquals("EnrollmentServer", comEcimManagedElement.systemFunctions.secM.certM.enrollmentServerGroup.enrollmentServer.type());
        Assert.assertEquals(certMNamespace, comEcimManagedElement.systemFunctions.secM.certM.enrollmentServerGroup.enrollmentServer.namespace());

        Assert.assertEquals("SysM", comEcimManagedElement.systemFunctions.sysM.type());
        Assert.assertEquals(sysMNamespace, comEcimManagedElement.systemFunctions.sysM.namespace());

        if (comEcimManagedElement.systemFunctions.sysM.netconfTls != null) {
            Assert.assertEquals("NetconfTls", comEcimManagedElement.systemFunctions.sysM.netconfTls.type());
            Assert.assertEquals(sysMNamespace, comEcimManagedElement.systemFunctions.sysM.netconfTls.namespace());
        }

        if (comEcimManagedElement.systemFunctions.sysM.oamAccessPoint != null) {
            Assert.assertEquals("OamAccessPoint", comEcimManagedElement.systemFunctions.sysM.oamAccessPoint.type());
            Assert.assertEquals(oamAccessPointNamespace, comEcimManagedElement.systemFunctions.sysM.oamAccessPoint.namespace());
        }

        Assert.assertEquals("Transport", comEcimManagedElement.transport.type());
        Assert.assertEquals(topNamespace, comEcimManagedElement.transport.namespace());

        if (comEcimManagedElement.transport.ikev2PolicyProfile != null) {
            Assert.assertEquals("Ikev2PolicyProfile", comEcimManagedElement.transport.ikev2PolicyProfile.type());
            Assert.assertEquals(ikev2PolicyProfileNamespace, comEcimManagedElement.transport.ikev2PolicyProfile.namespace());
        }
    }

    @Test
    public void sgsnMmeTypeAndNamespaceTest() {

        Assert.assertEquals("SgsnMmeTop", ModelDefinition.SGSN_MME_TOP_NS);
        Assert.assertEquals("SgsnMmeSecurityManagement", ModelDefinition.SGSN_MME_SEC_M_NS);
        Assert.assertEquals("SgsnMmeCertM", ModelDefinition.SGSN_MME_CERT_M_NS);
        Assert.assertEquals("SgsnMmeSysM", ModelDefinition.SGSN_MME_SYS_M_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.sgsnMmeManagedElement, "SgsnMmeTop", "SgsnMmeSecurityManagement", "SgsnMmeCertM", "SgsnMmeSysM",
                null, null);
        comEcimTypeAndNamespaceTest(Model.SGSN_MME_MANAGED_ELEMENT, "SgsnMmeTop", "SgsnMmeSecurityManagement", "SgsnMmeCertM", "SgsnMmeSysM", null,
                null);
    }

    @Test
    public void msrbsv1TypeAndNamespaceTest() {
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void radioNodeTypeAndNamespaceTest() {
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void radioTNodeTypeAndNamespaceTest() {
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void sapcTypeAndNamespaceTest() {
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vppTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vrmTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vrsmTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vEMETypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vWCGTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void hSSFETypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vIPWorksTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vUPGTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void bSPTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vBGFTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vMRFTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vrcTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void rnNodeTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void epgTypeAndNamespaceTest() {
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vepgTypeAndNamespaceTest() {
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void fiveGRadioNodeTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vtfRadioNodeTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void vsdNodeTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void rVNFMNodeTypeAndNamespaceTest() {

        Assert.assertEquals("ComTop", ModelDefinition.COM_TOP_NS);
        Assert.assertEquals("ComSecM", ModelDefinition.COM_SEC_M_NS);
        Assert.assertEquals("RcsCertM", ModelDefinition.COM_CERT_M_NS);
        Assert.assertEquals("ComSysM", ModelDefinition.COM_SYS_M_NS);
        Assert.assertEquals("RcsOamAccessPoint", ModelDefinition.COM_OAM_ACCESS_POINT_NS);
        Assert.assertEquals("RtnIkev2PolicyProfile", ModelDefinition.COM_IKEV2_POLICY_PROFILE_NS);
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void fdnTest() {

        Assert.assertEquals("MeContext=1", Model.ME_CONTEXT.withNames(null).fdn());

        Assert.assertEquals("MeContext=", Model.ME_CONTEXT.withNames("").fdn());

        Assert.assertEquals("MeContext=ContextName", Model.ME_CONTEXT.withNames("ContextName").fdn());

        Assert.assertEquals("MeContext=ContextName", Model.ME_CONTEXT.withNames("MeContext=ContextName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,MeContext=ContextName",
                Model.ME_CONTEXT.withNames("SubNetwork=SubNetwork1,MeContext=ContextName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=ContextName",
                Model.ME_CONTEXT.withNames("SubNetwork=SubNetwork1,,SubNetwork=SubNetwork2,MeContext=ContextName").fdn());

        Assert.assertEquals("MeContext=ContextName,ManagedElement=1,SystemFunctions=1",
                Model.ME_CONTEXT.managedElement.systemFunctions.withNames("ContextName").fdn());

        Assert.assertEquals("MeContext=ContextName,ManagedElement=1,SystemFunctions=1",
                Model.ME_CONTEXT.managedElement.systemFunctions.withNames("MeContext=ContextName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,MeContext=ContextName,ManagedElement=1,SystemFunctions=1",
                Model.ME_CONTEXT.managedElement.systemFunctions.withNames("SubNetwork=SubNetwork1,MeContext=ContextName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=ContextName,ManagedElement=1,SystemFunctions=1",
                Model.ME_CONTEXT.managedElement.systemFunctions.withNames("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=ContextName")
                        .fdn());

        Assert.assertEquals("MeContext=ContextName,ManagedElement=ManagedName,SystemFunctions=SFContextName",
                Model.ME_CONTEXT.managedElement.systemFunctions.withNames("ContextName", "ManagedName", "SFContextName").fdn());

        Assert.assertEquals("MeContext=ContextName,ManagedElement=ManagedName,SystemFunctions=SFContextName",
                Model.ME_CONTEXT.managedElement.systemFunctions
                        .withNames("MeContext=ContextName,ManagedElement=ManagedName,SystemFunctions=SFContextName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,MeContext=ContextName,ManagedElement=ManagedName,SystemFunctions=SFContextName",
                Model.ME_CONTEXT.managedElement.systemFunctions
                        .withNames("SubNetwork=SubNetwork1,MeContext=ContextName,ManagedElement=ManagedName,SystemFunctions=SFContextName").fdn());

        Assert.assertEquals(
                "SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=ContextName,ManagedElement=ManagedName,SystemFunctions=SFContextName",
                Model.ME_CONTEXT.managedElement.systemFunctions
                        .withNames(
                                "SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=ContextName,ManagedElement=ManagedName,SystemFunctions=SFContextName")
                        .fdn());

        Assert.assertEquals("MeContext=ContextName,ManagedElement=1", Model.ME_CONTEXT.sgsnMmeManagedElement.withNames("ContextName").fdn());

        Assert.assertEquals("MeContext=ContextName,ManagedElement=1",
                Model.ME_CONTEXT.sgsnMmeManagedElement.withNames("MeContext=ContextName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,MeContext=ContextName,ManagedElement=1",
                Model.ME_CONTEXT.sgsnMmeManagedElement.withNames("SubNetwork=SubNetwork1,MeContext=ContextName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=ContextName,ManagedElement=1",
                Model.ME_CONTEXT.sgsnMmeManagedElement.withNames("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=ContextName").fdn());

        Assert.assertEquals("MeContext=ContextName,ManagedElement=1", Model.ME_CONTEXT.comManagedElement.withNames("ContextName").fdn());

        Assert.assertEquals("MeContext=ContextName,ManagedElement=1", Model.ME_CONTEXT.comManagedElement.withNames("MeContext=ContextName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,MeContext=ContextName,ManagedElement=1",
                Model.ME_CONTEXT.comManagedElement.withNames("SubNetwork=SubNetwork1,MeContext=ContextName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=ContextName,ManagedElement=1",
                Model.ME_CONTEXT.comManagedElement.withNames("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=ContextName").fdn());

        Assert.assertEquals("ManagedElement=ManagedName", Model.SGSN_MME_MANAGED_ELEMENT.withNames("ManagedName").fdn());

        Assert.assertEquals("ManagedElement=ManagedName", Model.SGSN_MME_MANAGED_ELEMENT.withNames("ManagedElement=ManagedName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,ManagedElement=ManagedName",
                Model.SGSN_MME_MANAGED_ELEMENT.withNames("SubNetwork=SubNetwork1,ManagedElement=ManagedName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName",
                Model.SGSN_MME_MANAGED_ELEMENT.withNames("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName").fdn());

        Assert.assertEquals("ManagedElement=ManagedName", Model.COM_MANAGED_ELEMENT.withNames("ManagedName").fdn());

        Assert.assertEquals("ManagedElement=ManagedName", Model.COM_MANAGED_ELEMENT.withNames("ManagedElement=ManagedName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,ManagedElement=ManagedName",
                Model.COM_MANAGED_ELEMENT.withNames("SubNetwork=SubNetwork1,ManagedElement=ManagedName").fdn());

        Assert.assertEquals("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName",
                Model.COM_MANAGED_ELEMENT.withNames("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName").fdn());
    }

    @Test
    public void testMirrorRoot() {
        assertEquals(Model.ME_CONTEXT.managedElement, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "ERBS_NODE_MODEL"));
        assertEquals(Model.ME_CONTEXT.managedElement, Model
                .getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=NodeName,ManagedElement=ManagedName", "ERBS_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "ERBS_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("ManagedElement=ManagedName", "ERBS_NODE_MODEL"));
        assertEquals(Model.ME_CONTEXT.rncManagedElement, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "RNC_NODE_MODEL"));
        assertEquals(Model.ME_CONTEXT.rncManagedElement,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=NodeName,ManagedElement=ManagedName", "RNC_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "RNC_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("ManagedElement=ManagedName", "RNC_NODE_MODEL"));
        assertEquals(Model.ME_CONTEXT.rbsManagedElement, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "RBS_NODE_MODEL"));
        assertEquals(Model.ME_CONTEXT.rbsManagedElement,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=NodeName,ManagedElement=ManagedName", "RBS_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "RBS_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("ManagedElement=ManagedName", "RBS_NODE_MODEL"));
        assertEquals(Model.ME_CONTEXT.mgwManagedElement, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "MGW_NODE_MODEL"));
        assertEquals(Model.ME_CONTEXT.mgwManagedElement,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=NodeName,ManagedElement=ManagedName", "MGW_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "MGW_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("ManagedElement=ManagedName", "MGW_NODE_MODEL"));
        assertEquals(Model.ME_CONTEXT.mrsManagedElement, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "MRS_NODE_MODEL"));
        assertEquals(Model.ME_CONTEXT.mrsManagedElement,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=NodeName,ManagedElement=ManagedName", "MRS_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "MRS_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("ManagedElement=ManagedName", "MRS_NODE_MODEL"));
        assertEquals(Model.ME_CONTEXT.sgsnMmeManagedElement, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "SgsnMmeTop"));
        assertEquals(Model.ME_CONTEXT.sgsnMmeManagedElement,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=NodeName,ManagedElement=ManagedName", "SgsnMmeTop"));
        assertEquals(Model.SGSN_MME_MANAGED_ELEMENT,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "SgsnMmeTop"));
        assertEquals(Model.SGSN_MME_MANAGED_ELEMENT, Model.getMirrorRoot("ManagedElement=ManagedName", "SgsnMmeTop"));
        assertEquals(Model.ME_CONTEXT.comManagedElement, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "ComTop"));
        assertEquals(Model.ME_CONTEXT.comManagedElement,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=NodeName,ManagedElement=ManagedName", "ComTop"));
        assertEquals(Model.COM_MANAGED_ELEMENT,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "ComTop"));
        assertEquals(Model.COM_MANAGED_ELEMENT, Model.getMirrorRoot("ManagedElement=ManagedName", "ComTop"));
        assertEquals(null, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "VduTop"));
        assertEquals(null,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=NodeName,ManagedElement=ManagedName", "VduTop"));
        assertEquals(null,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "VduTop"));
        assertEquals(null, Model.getMirrorRoot("ManagedElement=ManagedName", "VduTop"));
        assertEquals(null, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "dummy"));

        assertEquals(null,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "VcuCpTop"));
        assertEquals(null, Model.getMirrorRoot("ManagedElement=ManagedName", "VcuCpTop"));

        assertEquals(null,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "VcuUpTop"));
        assertEquals(null, Model.getMirrorRoot("ManagedElement=ManagedName", "VcuUpTop"));

        assertEquals(null, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "VcuCpTop"));
        assertEquals(null,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=NodeName,ManagedElement=ManagedName", "VcuCpTop"));
        assertEquals(null,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "VcuCpTop"));
        assertEquals(null, Model.getMirrorRoot("ManagedElement=ManagedName", "VcuCpTop"));
        assertEquals(null, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "dummy"));

        assertEquals(null, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "VcuUpTop"));
        assertEquals(null,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=NodeName,ManagedElement=ManagedName", "VcuUpTop"));
        assertEquals(null,
                Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "VcuUpTop"));
        assertEquals(null, Model.getMirrorRoot("ManagedElement=ManagedName", "VcuUpTop"));
        assertEquals(null, Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "dummy"));
    }

    @Test
    public void testNullMirrorRoot() {
        assertNull(Model.getMirrorRoot("MeContext=NodeName,ManagedElement=ManagedName", "UNKNOWN_NAMESPACE"));
        assertNull(Model.getMirrorRoot("SubNetwork=Sub1,MeContext=NodeName,ManagedElement=ManagedName", "UNKNOWN_NAMESPACE"));
        assertNull(Model.getMirrorRoot("ManagedElement=ManagedName", "RNC_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "RNC_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "RBS_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("ManagedElement=ManagedName", "RBS_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "MGW_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("ManagedElement=ManagedName", "MGW_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,ManagedElement=ManagedName", "MRS_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("ManagedElement=ManagedName", "MRS_NODE_MODEL"));
        assertNull(Model.getMirrorRoot("", "SgsnMmeTop"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2", "SgsnMmeTop"));
        assertNull(Model.getMirrorRoot("SubNetwork=Sub1", "ComTop"));
        assertNull(Model.getMirrorRoot("", "ComTop"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1", "VduTop"));
        assertNull(Model.getMirrorRoot("", "VduTop"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1", "VcuCpTop"));
        assertNull(Model.getMirrorRoot("", "VcuCpTop"));
        assertNull(Model.getMirrorRoot("SubNetwork=SubNetwork1", "VcuUpTop"));
        assertNull(Model.getMirrorRoot("", "VcuUpTop"));
    }

    @Test
    public void extractTest() {

        Assert.assertEquals("ContextName", Model.ME_CONTEXT.extractName("MeContext=ContextName"));

        Assert.assertEquals("ContextName", Model.ME_CONTEXT.extractName("SubNetwork=SubNetwork1,MeContext=ContextName"));

        Assert.assertEquals("ContextName", Model.ME_CONTEXT.extractName("SubNetwork=SubNetwork1,SubNetwork=SubNetwork2,MeContext=ContextName"));

        Assert.assertEquals("ContextName", Model.ME_CONTEXT.extractName("MeContext=ContextName,SystemFunctions=1"));

        Assert.assertEquals("ContextName", Model.ME_CONTEXT.extractName("MeContext=ContextName,ManagedElement=1"));

        Assert.assertEquals("ContextName", Model.ME_CONTEXT.extractName("SubNetwork=SNname,MeContext=ContextName,ManagedElement=1"));

        Assert.assertEquals("ContextName",
                Model.ME_CONTEXT.extractName("SubNetwork=SNname1,SubNetwork=SNname2,MeContext=ContextName,ManagedElement=1"));

        Assert.assertEquals("1", Model.ME_CONTEXT.managedElement.systemFunctions.extractName("MeContext=ContextName,SystemFunctions=1"));

        Assert.assertEquals("1",
                Model.ME_CONTEXT.managedElement.systemFunctions.extractName("SubNetwork=SNname,MeContext=ContextName,SystemFunctions=1"));

        Assert.assertEquals("1", Model.ME_CONTEXT.managedElement.systemFunctions
                .extractName("SubNetwork=SNname1,SubNetwork=SNname2,MeContext=ContextName,SystemFunctions=1"));

        Assert.assertEquals("1",
                Model.ME_CONTEXT.managedElement.systemFunctions.extractName("MeContext=ContextName,SystemFunctions=1,Security=Test"));

        Assert.assertEquals("1", Model.ME_CONTEXT.mgwManagedElement.systemFunctions.extractName("MeContext=ContextName,SystemFunctions=1"));

        Assert.assertEquals("1",
                Model.ME_CONTEXT.mgwManagedElement.systemFunctions.extractName("SubNetwork=SNname,MeContext=ContextName,SystemFunctions=1"));

        Assert.assertEquals("1", Model.ME_CONTEXT.mgwManagedElement.systemFunctions
                .extractName("SubNetwork=SNname1,SubNetwork=SNname2,MeContext=ContextName,SystemFunctions=1"));

        Assert.assertEquals("1",
                Model.ME_CONTEXT.mgwManagedElement.systemFunctions.extractName("MeContext=ContextName,SystemFunctions=1,Security=Test"));

        Assert.assertEquals("1", Model.ME_CONTEXT.mrsManagedElement.systemFunctions.extractName("MeContext=ContextName,SystemFunctions=1"));

        Assert.assertEquals("1",
                Model.ME_CONTEXT.mrsManagedElement.systemFunctions.extractName("SubNetwork=SNname,MeContext=ContextName,SystemFunctions=1"));

        Assert.assertEquals("1", Model.ME_CONTEXT.mrsManagedElement.systemFunctions
                .extractName("SubNetwork=SNname1,SubNetwork=SNname2,MeContext=ContextName,SystemFunctions=1"));

        Assert.assertEquals("1",
                Model.ME_CONTEXT.mrsManagedElement.systemFunctions.extractName("MeContext=ContextName,SystemFunctions=1,Security=Test"));

        Assert.assertEquals("1", Model.ME_CONTEXT.sgsnMmeManagedElement.extractName("MeContext=ContextName,ManagedElement=1"));

        Assert.assertEquals("1", Model.ME_CONTEXT.comManagedElement.extractName("MeContext=ContextName,ManagedElement=1"));

        Assert.assertEquals("ManagedName", Model.SGSN_MME_MANAGED_ELEMENT.extractName("ManagedElement=ManagedName"));

        Assert.assertEquals("ManagedName", Model.COM_MANAGED_ELEMENT.extractName("ManagedElement=ManagedName"));
    }

    @Test
    public void securityFdnWithErbsNodeName() {
        final String nodeName = "node123";

        final String securityFDN = Model.ME_CONTEXT.managedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDN);

        final List<String> prefixes = new ArrayList<String>();
        prefixes.add("SubNetwork=subnetwork123");
        final String securityFDNWithSubnetwork = Model.ME_CONTEXT.managedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDNWithSubnetwork);

        prefixes.add("SubNetwork=subnetwork456");
        final String securityFDNWithMultipleSubnetworks = Model.ME_CONTEXT.managedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDNWithMultipleSubnetworks);
    }

    @Test
    public void securityFdnWithRNCNodeName() {
        final String nodeName = "rncNode";

        final String securityFDN = Model.ME_CONTEXT.managedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDN);

        final List<String> prefixes = new ArrayList<String>();
        prefixes.add("SubNetwork=subnetwork123");
        final String securityFDNWithSubnetwork = Model.ME_CONTEXT.managedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDNWithSubnetwork);

        prefixes.add("SubNetwork=subnetwork456");
        final String securityFDNWithMultipleSubnetworks = Model.ME_CONTEXT.managedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDNWithMultipleSubnetworks);
    }

    @Test
    public void securityFdnWithRBSNodeName() {
        final String nodeName = "rbsNode";

        final String securityFDN = Model.ME_CONTEXT.managedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDN);

        final List<String> prefixes = new ArrayList<String>();
        prefixes.add("SubNetwork=subnetwork123");
        final String securityFDNWithSubnetwork = Model.ME_CONTEXT.managedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDNWithSubnetwork);

        prefixes.add("SubNetwork=subnetwork456");
        final String securityFDNWithMultipleSubnetworks = Model.ME_CONTEXT.managedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDNWithMultipleSubnetworks);
    }

    @Test
    public void securityFdnWithMgwNodeName() {
        final String nodeName = "node123";

        final String securityFDN = Model.ME_CONTEXT.mgwManagedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDN);

        final List<String> prefixes = new ArrayList<String>();
        prefixes.add("SubNetwork=subnetwork123");
        final String securityFDNWithSubnetwork = Model.ME_CONTEXT.mgwManagedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDNWithSubnetwork);

        prefixes.add("SubNetwork=subnetwork456");
        final String securityFDNWithMultipleSubnetworks = Model.ME_CONTEXT.mgwManagedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDNWithMultipleSubnetworks);
    }

    @Test
    public void securityFdnWithMrsNodeName() {
        final String nodeName = "node123";

        final String securityFDN = Model.ME_CONTEXT.mrsManagedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDN);

        final List<String> prefixes = new ArrayList<String>();
        prefixes.add("SubNetwork=subnetwork123");
        final String securityFDNWithSubnetwork = Model.ME_CONTEXT.mrsManagedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDNWithSubnetwork);

        prefixes.add("SubNetwork=subnetwork456");
        final String securityFDNWithMultipleSubnetworks = Model.ME_CONTEXT.mrsManagedElement.systemFunctions.security.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,SystemFunctions=1,Security=1", securityFDNWithMultipleSubnetworks);
    }

    @Test
    public void ipsecFdnWithErbsNodeName() {
        final String nodeName = "node123";

        final String ipsecFDN = Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDN);

        final List<String> prefixes = new ArrayList<String>();
        prefixes.add("SubNetwork=subnetwork123");
        final String ipsecFDNWithSubnetwork = Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDNWithSubnetwork);

        prefixes.add("SubNetwork=subnetwork456");
        final String ipsecFDNWithMultipleSubnetworks = Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDNWithMultipleSubnetworks);
    }

    @Test
    public void ipsecFdnWithRNCNodeName() {
        final String nodeName = "rncNode";

        final String ipsecFDN = Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDN);

        final List<String> prefixes = new ArrayList<String>();
        prefixes.add("SubNetwork=subnetwork123");
        final String ipsecFDNWithSubnetwork = Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDNWithSubnetwork);

        prefixes.add("SubNetwork=subnetwork456");
        final String ipsecFDNWithMultipleSubnetworks = Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDNWithMultipleSubnetworks);
    }

    @Test
    public void ipsecFdnWithRBSNodeName() {
        final String nodeName = "rbsNode";

        final String ipsecFDN = Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDN);

        final List<String> prefixes = new ArrayList<String>();
        prefixes.add("SubNetwork=subnetwork123");
        final String ipsecFDNWithSubnetwork = Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDNWithSubnetwork);

        prefixes.add("SubNetwork=subnetwork456");
        final String ipsecFDNWithMultipleSubnetworks = Model.ME_CONTEXT.managedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDNWithMultipleSubnetworks);
    }

    @Test
    public void ipsecFdnWithMgwNodeName() {
        final String nodeName = "node123";

        final String ipsecFDN = Model.ME_CONTEXT.mgwManagedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDN);

        final List<String> prefixes = new ArrayList<String>();
        prefixes.add("SubNetwork=subnetwork123");
        final String ipsecFDNWithSubnetwork = Model.ME_CONTEXT.mgwManagedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDNWithSubnetwork);

        prefixes.add("SubNetwork=subnetwork456");
        final String ipsecFDNWithMultipleSubnetworks = Model.ME_CONTEXT.mgwManagedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDNWithMultipleSubnetworks);
    }

    @Test
    public void ipsecFdnWithMrsNodeName() {
        final String nodeName = "node123";

        final String ipsecFDN = Model.ME_CONTEXT.mrsManagedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDN);

        final List<String> prefixes = new ArrayList<String>();
        prefixes.add("SubNetwork=subnetwork123");
        final String ipsecFDNWithSubnetwork = Model.ME_CONTEXT.mrsManagedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDNWithSubnetwork);

        prefixes.add("SubNetwork=subnetwork456");
        final String ipsecFDNWithMultipleSubnetworks = Model.ME_CONTEXT.mrsManagedElement.ipSystem.ipSec.withNames(nodeName).fdn();
        assertEquals("MeContext=" + nodeName + ",ManagedElement=1,IpSystem=1,IpSec=1", ipsecFDNWithMultipleSubnetworks);
    }

    @Test
    public void installTrustedCertFromUriTest() {
        final MoParams moParams = CertM.installTrustedCertFromUriToMoParams("myFingerprint", "myUri", "myUriPassword");
        final String fingerprint1 = moParams.getParamMap().get(CertM.INSTALL_TRUSTED_CERT_FROM_URI_FINGERPRINT).getParam().toString();
        final String fingerprint2 = moParams.getParamMap().get("fingerprint").getParam().toString();
        Assert.assertEquals("myFingerprint", fingerprint1);
        Assert.assertEquals("myFingerprint", fingerprint2);
        final String uri1 = moParams.getParamMap().get(CertM.INSTALL_TRUSTED_CERT_FROM_URI_URI).getParam().toString();
        final String uri2 = moParams.getParamMap().get("uri").getParam().toString();
        Assert.assertEquals("myUri", uri1);
        Assert.assertEquals("myUri", uri2);
        final String uriPassword1 = moParams.getParamMap().get(CertM.INSTALL_TRUSTED_CERT_FROM_URI_URI_PASSWORD).getParam().toString();
        final String uriPassword2 = moParams.getParamMap().get("uriPassword").getParam().toString();
        Assert.assertEquals("myUriPassword", uriPassword1);
        Assert.assertEquals("myUriPassword", uriPassword2);
    }

    @Test
    public void removeTrustedCertTest() {
        final MoParams moParams = CertM.removeTrustedCertToMoParams("myTrustedCert");
        final String trustedCert1 = moParams.getParamMap().get(CertM.REMOVE_TRUSTED_CERT_TRUSTED_CERT).getParam().toString();
        final String trustedCert2 = moParams.getParamMap().get("trustedCert").getParam().toString();
        Assert.assertEquals("myTrustedCert", trustedCert1);
        Assert.assertEquals("myTrustedCert", trustedCert2);
    }

    @Test
    public void removeVduTrustedCertTest() {
        final MoParams moParams = new MoParams();
        moParams.addParam("name", "ENM_PKI_Root_CA");
        moParams.addParam("pem", "dummycert");
        final String name = moParams.getParamMap().get("name").getParam().toString();
        final String pem = moParams.getParamMap().get("pem").getParam().toString();
        Assert.assertEquals("ENM_PKI_Root_CA", name);
        Assert.assertEquals("dummycert", pem);
    }

    @Test
    public void installCredentialFromUriTest() {
        final MoParams moParams = NodeCredential.installCredentialFromUriToMoParams("myCredentialPassword", "myFingerprint", "myUri",
                "myUriPassword");
        final String credentialPassword1 = moParams.getParamMap().get(NodeCredential.INSTALL_CREDENTIAL_FROM_URI_CREDENTIAL_PASSWORD).getParam()
                .toString();
        final String credentialPassword2 = moParams.getParamMap().get("credentialPassword").getParam().toString();
        Assert.assertEquals("myCredentialPassword", credentialPassword1);
        Assert.assertEquals("myCredentialPassword", credentialPassword2);
        final String fingerprint1 = moParams.getParamMap().get(NodeCredential.INSTALL_CREDENTIAL_FROM_URI_FINGERPRINT).getParam().toString();
        final String fingerprint2 = moParams.getParamMap().get("fingerprint").getParam().toString();
        Assert.assertEquals("myFingerprint", fingerprint1);
        Assert.assertEquals("myFingerprint", fingerprint2);
        final String uri1 = moParams.getParamMap().get(NodeCredential.INSTALL_CREDENTIAL_FROM_URI_URI).getParam().toString();
        final String uri2 = moParams.getParamMap().get("uri").getParam().toString();
        Assert.assertEquals("myUri", uri1);
        Assert.assertEquals("myUri", uri2);
        final String uriPassword1 = moParams.getParamMap().get(NodeCredential.INSTALL_CREDENTIAL_FROM_URI_URI_PASSWORD).getParam().toString();
        final String uriPassword2 = moParams.getParamMap().get("uriPassword").getParam().toString();
        Assert.assertEquals("myUriPassword", uriPassword1);
        Assert.assertEquals("myUriPassword", uriPassword2);
    }

    @Test
    public void startOfflineCsrEnrollmentTest() {
        final MoParams moParams = NodeCredential.startOfflineCsrEnrollmentToMoParams("myUri", "myUriPassword");
        final String uri1 = moParams.getParamMap().get(NodeCredential.START_OFFLINE_CSR_ENROLLMENT_URI).getParam().toString();
        final String uri2 = moParams.getParamMap().get("uri").getParam().toString();
        Assert.assertEquals("myUri", uri1);
        Assert.assertEquals("myUri", uri2);
        final String uriPassword1 = moParams.getParamMap().get(NodeCredential.START_OFFLINE_CSR_ENROLLMENT_URI_PASSWORD).getParam().toString();
        final String uriPassword2 = moParams.getParamMap().get("uriPassword").getParam().toString();
        Assert.assertEquals("myUriPassword", uriPassword1);
        Assert.assertEquals("myUriPassword", uriPassword2);
    }

    @Test
    public void startOnlineEnrollmentTest() {
        final MoParams moParams = NodeCredential.startOnlineEnrollmentToMoParams("myChallengePassword");
        final String challengePassword1 = moParams.getParamMap().get(NodeCredential.START_ONLINE_ENROLLMENT_CHALLENGE_PASSWORD).getParam().toString();
        final String challengePassword2 = moParams.getParamMap().get("challengePassword").getParam().toString();
        Assert.assertEquals("myChallengePassword", challengePassword1);
        Assert.assertEquals("myChallengePassword", challengePassword2);
    }

    @Test
    public void sbgTypeAndNamespaceTest() {
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void cscfTypeAndNamespaceTest() {
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void mtasTypeAndNamespaceTest() {
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void bscTypeAndNamespaceTest() {
        comEcimTypeAndNamespaceTest(Model.ME_CONTEXT.comManagedElement, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
        comEcimTypeAndNamespaceTest(Model.COM_MANAGED_ELEMENT, "ComTop", "ComSecM", "RcsCertM", "ComSysM", "RcsOamAccessPoint",
                "RtnIkev2PolicyProfile");
    }

    @Test
    public void getNomalizedRootMO_withFdnThatContainsNetworkElement_returnsNetworkElement() {
        assertEquals(Model.NETWORK_ELEMENT, Model.getNomalizedRootMO("SubNetwork=Ireland,NetworkElement=MyNE,SomeChildMo=1"));
    }

    @Test
    public void getNomalizedRootMO_withFdnThatContainsVirtualNetworkFunctionManager_returnsVNFM() {
        assertEquals(Model.VNFM, Model.getNomalizedRootMO("SubNetwork=Ireland,VirtualNetworkFunctionManager=MyVNFM,SomeChildMo=1"));
    }

    @Test
    public void getNomalizedRootMO_withFdnThatContainsNetworkFunctionVirtualizationOrchestrator_returnsNFVO() {
        assertEquals(Model.NFVO, Model.getNomalizedRootMO("SubNetwork=Ireland,NetworkFunctionVirtualizationOrchestrator=MyNFVO,SomeChildMo=1"));
    }

    @Test
    public void getNomalizedRootMO_withFdnThatContainsVirtualInfrastructureManager_returnsVIM() {
        assertEquals(Model.VIM, Model.getNomalizedRootMO("SubNetwork=Ireland,VirtualInfrastructureManager=MyVIM,SomeChildMo=1"));
    }

    @Test
    public void getNomalizedRootMO_withFdnThatContainsCloudInfrastructureManager_returnsCIM() {
        assertEquals(Model.CIM, Model.getNomalizedRootMO("SubNetwork=Ireland,CloudInfrastructureManager=MyCIM,SomeChildMo=1"));
    }

    @Test
    public void getNomalizedRootMO_withFdnThatContainsManagementSystem_returnsManagementSystem() {
        assertEquals(Model.MS, Model.getNomalizedRootMO("SubNetwork=Ireland1,ManagementSystem=Ms1,SomeChildMo=1"));
    }

    @Test
    public void getNomalizedRootMO_withFdnInTheNameOfSomeChildMo_AlsoReturnsNetworkElement() {
        /**
         * TODO EEITSIK Technical debt needs to be fixed! Just added this test to proof the technical debt and risk that exists in this method!
         */
        assertEquals(Model.NETWORK_ELEMENT,
                Model.getNomalizedRootMO("SubNetwork=Ireland,MeContext=SomeNode,SomeChildMo=HowUnluckySomeOneIncludedNetworkElementInMyName"));
    }

}
