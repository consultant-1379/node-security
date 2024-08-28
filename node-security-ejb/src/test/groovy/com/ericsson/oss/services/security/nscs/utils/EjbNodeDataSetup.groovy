/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.utils

import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps

class EjbNodeDataSetup extends EjbNscsCdiSpecification {

    protected RuntimeConfigurableDps runtimeConfigurableDps
    protected DataPersistenceService dataPersistenceService
    protected ManagedObject networkElement
    protected ManagedObject cmFunction
    protected ManagedObject securityFunction
    protected ManagedObject networkElementSecurity
    protected ManagedObject meContext
    protected ManagedObject managedElement
    protected ManagedObject virtualNetworkFunctionManager
    protected ManagedObject networkFunctionVirtualizationOrchestrator
    protected ManagedObject virtualInfrastructureManager
    protected ManagedObject cloudInfrastructureManager
    protected ManagedObject managementSystem
    protected ManagedObject connectivityInformation
    protected PersistenceObject targetPo

    def createNetworkElement(String neType, String ossModelIdentity, String nodeName, String platform) {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        targetPo = runtimeConfigurableDps.addPersistenceObject()
                .namespace("DPS")
                .type("Target")
                .addAttributes(getTargetAttributesMap("NODE", neType, nodeName, ossModelIdentity))
                .create()
        networkElement = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("NetworkElement")
                .name(nodeName)
                .version("2.0.0")
                .addAttribute("neType", neType)
                .addAttribute("ossModelIdentity", ossModelIdentity)
                .addAttribute("platformType", platform)
                .target(targetPo)
                .build()
    }

    def createNetworkElementWithoutTarget(String neType, String ossModelIdentity, String nodeName, String platform) {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        networkElement = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("NetworkElement")
                .name(nodeName)
                .version("2.0.0")
                .addAttribute("neType", neType)
                .addAttribute("ossModelIdentity", ossModelIdentity)
                .addAttribute("platformType", platform)
                .build()
    }

    def createCmFunctionUnderNetworkElement(String syncStatus) {
        cmFunction = runtimeConfigurableDps.addManagedObject()
                .parent(networkElement)
                .namespace("OSS_NE_CM_DEF")
                .version("1.0.1")
                .type("CmFunction")
                .name("1")
                .addAttribute("syncStatus", syncStatus)
                .target(targetPo)
                .build()
    }

    def createSecurityFunctionUnderNetworkElement() {
        securityFunction = runtimeConfigurableDps.addManagedObject()
                .parent(networkElement)
                .namespace("OSS_NE_SEC_DEF")
                .version("1.0.0")
                .type("SecurityFunction")
                .name("1")
                .target(targetPo)
                .build()
    }

    def createNetworkElementSecurityUnderSecurityFunction(String secureUserName, String algorithmAndKeySize, String enmSshPrivateKey, String enmSshPublicKey) {
        networkElementSecurity = runtimeConfigurableDps.addManagedObject()
                .parent(securityFunction)
                .namespace("OSS_NE_SEC_DEF")
                .version("4.1.1")
                .type("NetworkElementSecurity")
                .name("1")
                .addAttribute("secureUserName", secureUserName)
                .addAttribute("algorithmAndKeySize", algorithmAndKeySize)
                .addAttribute("enmSshPrivateKey", enmSshPrivateKey)
                .addAttribute("enmSshPublicKey", enmSshPublicKey)
                .target(targetPo)
                .build()
    }

    def createNetworkElementSecurityUnderSecurityFunctionWithProxyAccountDnAttribute(String proxyAccountDn) {
        networkElementSecurity = runtimeConfigurableDps.addManagedObject()
                .parent(securityFunction)
                .namespace("OSS_NE_SEC_DEF")
                .version("6.0.0")
                .type("NetworkElementSecurity")
                .name("1")
                .addAttribute("proxyAccountDn", proxyAccountDn)
                .target(targetPo)
                .build()
    }

    def createMeContext(String neType, String nodeName) {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        targetPo = runtimeConfigurableDps.addPersistenceObject()
                .namespace("DPS")
                .type("Target")
                .addAttributes(getTargetAttributesMap("NODE", neType, nodeName, null))
                .create()
        meContext = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_TOP")
                .type("MeContext")
                .name(nodeName)
                .addAttribute("neType", neType)
                .target(targetPo)
                .build()
    }

    def createNodeWithMeContext(String neType, String ossModelIdentity, String nodeName, String platform) {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        targetPo = runtimeConfigurableDps.addPersistenceObject()
                .namespace("DPS")
                .type("Target")
                .addAttributes(getTargetAttributesMap("NODE", neType, nodeName, ossModelIdentity))
                .create()
        meContext = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_TOP")
                .type("MeContext")
                .name(nodeName)
                .addAttribute("neType", neType)
                .target(targetPo)
                .build()
        networkElement = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("NetworkElement")
                .name(nodeName)
                .version("2.0.0")
                .addAttribute("neType", neType)
                .addAttribute("ossModelIdentity", ossModelIdentity)
                .addAttribute("platformType", platform)
                .target(targetPo)
                .build()
        networkElement.addAssociation("nodeRootRef", meContext)
        meContext.addAssociation("networkElementRef", networkElement)
    }

    def createNodeWithManagedElement(String neType, String ossModelIdentity, String nodeName, String platform, String managedElementNamespace) {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        targetPo = runtimeConfigurableDps.addPersistenceObject()
                .namespace("DPS")
                .type("Target")
                .addAttributes(getTargetAttributesMap("NODE", neType, nodeName, ossModelIdentity))
                .create()
        managedElement = runtimeConfigurableDps.addManagedObject()
                .namespace(managedElementNamespace)
                .type("ManagedElement")
                .name(nodeName)
                .target(targetPo)
                .build()
        networkElement = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("NetworkElement")
                .name(nodeName)
                .version("2.0.0")
                .addAttribute("neType", neType)
                .addAttribute("ossModelIdentity", ossModelIdentity)
                .addAttribute("platformType", platform)
                .target(targetPo)
                .build()
        networkElement.addAssociation("nodeRootRef", managedElement)
        managedElement.addAssociation("networkElementRef", networkElement)
    }

    def createManagedElement(String nodeName, String managedElementNamespace) {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        targetPo = runtimeConfigurableDps.addPersistenceObject()
                .namespace("DPS")
                .type("Target")
                .addAttributes(getTargetAttributesMap("NODE", null, nodeName, null))
                .create()
        managedElement = runtimeConfigurableDps.addManagedObject()
                .namespace(managedElementNamespace)
                .type("ManagedElement")
                .name(nodeName)
                .target(targetPo)
                .build()
    }

    def createManagedElementUnderMeContext(String neType, String nodeName, String managedElementNamespace) {
        targetPo = runtimeConfigurableDps.addPersistenceObject()
                .namespace("DPS")
                .type("Target")
                .addAttributes(getTargetAttributesMap("NODE", neType, nodeName, null))
                .create()
        managedElement = runtimeConfigurableDps.addManagedObject()
                .parent(meContext)
                .namespace(managedElementNamespace)
                .type("ManagedElement")
                .name(nodeName)
                .target(targetPo)
                .build()
    }

    def createVirtualNetworkFunctionManager(String nodeName) {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        targetPo = runtimeConfigurableDps.addPersistenceObject()
                .namespace("DPS")
                .type("Target")
                .addAttributes(getTargetAttributesMap("VNFM", "ECM", nodeName, null))
                .create()
        virtualNetworkFunctionManager = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("VirtualNetworkFunctionManager")
                .name(nodeName)
                .version("1.0.0")
                .addAttribute("vmType", "ECM")
                .target(targetPo)
                .build()
    }

    def createNetworkFunctionVirtualizationOrchestrator(String nodeName) {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        targetPo = runtimeConfigurableDps.addPersistenceObject()
                .namespace("DPS")
                .type("Target")
                .addAttributes(getTargetAttributesMap("NFVO", "ECM", nodeName, null))
                .create()
        networkFunctionVirtualizationOrchestrator = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("NetworkFunctionVirtualizationOrchestrator")
                .name(nodeName)
                .version("1.0.0")
                .addAttribute("nfvoType", "ECM")
                .target(targetPo)
                .build()
    }

    def createVirtualInfrastructureManager(String nodeName) {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        targetPo = runtimeConfigurableDps.addPersistenceObject()
                .namespace("DPS")
                .type("Target")
                .addAttributes(getTargetAttributesMap("VIM", "ECEE", nodeName, null))
                .create()
        virtualInfrastructureManager = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("VirtualInfrastructureManager")
                .name(nodeName)
                .version("1.0.0")
                .addAttribute("vimType", "ECEE")
                .target(targetPo)
                .build()
    }

    def createCloudInfrastructureManager(String nodeName) {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        targetPo = runtimeConfigurableDps.addPersistenceObject()
                .namespace("DPS")
                .type("Target")
                .addAttributes(getTargetAttributesMap("CIM", "CCD", nodeName, null))
                .create()
        cloudInfrastructureManager = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("CloudInfrastructureManager")
                .name(nodeName)
                .version("1.0.0")
                .addAttribute("cimType", "CCD")
                .target(targetPo)
                .build()
    }

    def createManagementSystem(String nodeName) {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        targetPo = runtimeConfigurableDps.addPersistenceObject()
                .namespace("DPS")
                .type("Target")
                .addAttributes(getTargetAttributesMap("MS", "ENM", nodeName, null))
                .create()
        managementSystem = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("ManagementSystem")
                .name(nodeName)
                .version("1.0.0")
                .addAttribute("msType", "ENM")
                .target(targetPo)
                .build()
    }

    def createUnsupportedTypeMo(String nodeName) {
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        runtimeConfigurableDps.addManagedObject()
                .namespace("UNSUPPORTED_NS")
                .type("UnsupportedType")
                .name(nodeName)
                .build()
    }

    def createConnectivityInformationUnderNetworkElement(String targetType, String ipAddress) {
        String ns = null
        String type = null
        if (targetType == RADIONODE_TARGET_TYPE) {
            ns = "COM_MED"
            type = "ComConnectivityInformation"
        } else if (targetType == VDU_TARGET_TYPE) {
            ns = "CBPOI_MED"
            type = "CbpOiConnectivityInformation"
        } else if (targetType == SHARED_CNF_TARGET_TYPE) {
            ns = "CBPOI_MED"
            type = "CbpOiConnectivityInformation"
        } else if (targetType == "ERBS") {
            ns = "CPP_MED"
            type = "CppConnectivityInformation"
        }
        connectivityInformation = runtimeConfigurableDps.addManagedObject()
                .parent(networkElement)
                .namespace(ns)
                .version("1.0.0")
                .type(type)
                .name("1")
                .addAttribute("ipAddress", ipAddress)
                .target(targetPo)
                .build()
    }

    def findMoByFdn(String fdn) {
        return dataPersistenceService.getLiveBucket().findMoByFdn(fdn)
    }

    private Map<String, Object> getTargetAttributesMap(String targetCategory, String targetType, String targetName, String targetModelIdentity) {
        final Map<String, Object> targetMap = new HashMap<>();
        targetMap.put("category", targetCategory);
        targetMap.put("type", targetType);
        targetMap.put("name", targetName);
        targetMap.put("modelIdentity", targetModelIdentity);
        return targetMap;
    }
}
