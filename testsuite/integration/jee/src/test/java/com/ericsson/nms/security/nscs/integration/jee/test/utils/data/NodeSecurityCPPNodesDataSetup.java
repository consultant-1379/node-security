/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.utils.data;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;
import com.ericsson.nms.security.nscs.data.ModelDefinition.SecurityFunction;
import com.ericsson.oss.itpf.datalayer.dps.BucketProperties;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;

public class NodeSecurityCPPNodesDataSetup extends NodeSecurityNodesDataSetup {
    protected String nePlatformType;

    public static final String NETWORK_ELEMENT_NAME_SULFIX = "-NE";

    private static final String[] requestedModelsForNode = { NodeSecurityDataConstants.MANAGED_ELEMENT, NodeSecurityDataConstants.SYSTEM_FUNCTIONS,
            NodeSecurityDataConstants.SECURITY };

    private static final String IPV4_ADDRESS = "192.168.33.27";

    public void insertData(String neName, String neNeType, String nePlatformType) throws Exception {
        deleteAllNodes();
    }

    public void createCPPNode(final String nodeName, final String syncStatus, final SecurityLevel level) throws Exception {
        log.info("[NSCS_ARQ_CPP_NODES_DATA_SETUP] starting createCPPNode {}", nodeName);

        beginTransaction();

        final DataBucket liveBucket = getLiveBucket();

        final String targetModelIdentity = getTargetModelIdentityForTestNode(NodeSecurityDataConstants.ERBS_TARGET_TYPE);
        Map<String, NscsModelInfo> modelInfoMap = null;
        try {
            modelInfoMap = eserviceHolder.getNscsModelService().getModelInfoList(TargetTypeInformation.CATEGORY_NODE,
                    NodeSecurityDataConstants.ERBS_TARGET_TYPE, targetModelIdentity, requestedModelsForNode);
        } catch (IllegalArgumentException | NscsModelServiceException ex) {
            log.error("getModelInfoList() caught exception: ", ex);
        }
        if ((modelInfoMap != null) && !modelInfoMap.isEmpty()) {
            // level 1 MeContext Mib in OSS_TOP model
            final ManagedObject parentMo = createMeContext(liveBucket, nodeName);

            final ManagedObject networkElement = createNetworkElementTree(liveBucket, parentMo, syncStatus, targetModelIdentity);
            // level 2 create ManagedElement Mib under CPP model

            final ManagedObject managedElementMO = createManagedElement(liveBucket, parentMo,
                    modelInfoMap.get(NodeSecurityDataConstants.MANAGED_ELEMENT));
            log.info("Test Setup ManagedElement, created: " + managedElementMO.getFdn());

            managedElementMO.addAssociation("networkElementRef", networkElement);
            log.info("Test Setup ManagedElement Association , created: " + managedElementMO.getFdn());

            // level 3 create SYSTEM FUNCTIONS

            final ManagedObject systemFunctionsMO = createSystemFunctions(liveBucket, managedElementMO,
                    modelInfoMap.get(NodeSecurityDataConstants.SYSTEM_FUNCTIONS));
            log.info("Test Setup SystemFunctions, created: " + systemFunctionsMO.getFdn());

        }
        commitTransaction();

        log.info("[NSCS_ARQ_CPP_NODES_DATA_SETUP] END createCPPNode {}", nodeName);
    }

    public void createCPPNodeForCrlCheck(final String nodeName, final String syncStatus, final SecurityLevel level) throws Exception {
        log.info("[NSCS_ARQ_CPP_NODES_DATA_SETUP] starting createCPPNodeForCrlCheck {}", nodeName);

        beginTransaction();

        final DataBucket liveBucket = getLiveBucket();

        final String targetModelIdentity = getTargetModelIdentityForTestNode(NodeSecurityDataConstants.ERBS_TARGET_TYPE);
        Map<String, NscsModelInfo> modelInfoMap = null;
        try {
            modelInfoMap = eserviceHolder.getNscsModelService().getModelInfoList(TargetTypeInformation.CATEGORY_NODE,
                    NodeSecurityDataConstants.ERBS_TARGET_TYPE, targetModelIdentity, requestedModelsForNode);
        } catch (IllegalArgumentException | NscsModelServiceException ex) {
            log.error("getModelInfoList() caught exception: ", ex);
        }
        if ((modelInfoMap != null) && !modelInfoMap.isEmpty()) {
            // level 1 MeContext Mib in OSS_TOP model
            final ManagedObject parentMo = createMeContext(liveBucket, nodeName);

            final ManagedObject networkElement = createNetworkElementTree(liveBucket, parentMo, syncStatus,
                    getTargetModelIdentityForTestNode(NodeSecurityDataConstants.ERBS_TARGET_TYPE));

            // level 2 create ManagedElement Mib under CPP model

            final ManagedObject managedElementMO = createManagedElement(liveBucket, parentMo,
                    modelInfoMap.get(NodeSecurityDataConstants.MANAGED_ELEMENT));
            log.info("Test Setup ManagedElement, created: " + managedElementMO.getFdn());

            managedElementMO.addAssociation("networkElementRef", networkElement);
            log.info("Test Setup ManagedElement Association , created: " + managedElementMO.getFdn());

            // level 3 create SYSTEM FUNCTIONS

            final ManagedObject systemFunctionsMO = createSystemFunctions(liveBucket, managedElementMO,
                    modelInfoMap.get(NodeSecurityDataConstants.SYSTEM_FUNCTIONS));
            log.info("Test Setup SystemFunctions, created: " + systemFunctionsMO.getFdn());

            // level 4 create Security FUNCTIONS
            final Map<String, Object> userDefProfilesInfoAttributesMap = createMapAndInsertValues(
                    NodeSecurityDataConstants.LATEST_STATE_CHANGE_ATTRIBUTE, NodeSecurityDataConstants.LATEST_STATE_CHANGE_ATTRIBUTE_VALUE,
                    NodeSecurityDataConstants.DESCRIPTION_ATTRIBUTE, NodeSecurityDataConstants.DESCRIPTION_ATTRIBUTE_VALUE,
                    NodeSecurityDataConstants.STATE_ATTRIBUTE, NodeSecurityDataConstants.STATE_ATTRIBUTE_VALUE);

            final ManagedObject securityFunctionsMO = createSecurityFunctions(liveBucket, systemFunctionsMO, userDefProfilesInfoAttributesMap, level,
                    modelInfoMap.get(NodeSecurityDataConstants.SECURITY));
            log.info("Test Setup SecurityFunctions, created: " + securityFunctionsMO.getFdn());
        }

        commitTransaction();

        log.info("[NSCS_ARQ_CPP_NODES_DATA_SETUP] END createCPPNodeForCrlCheck {}", nodeName);
    }

    public ManagedObject createSecurityFunctions(final DataBucket liveBucket, final ManagedObject parentMo,
            final Map<String, Object> userDefProfilesInfoAttributesMap, final SecurityLevel level, final NscsModelInfo nscsModelInfo) {

        log.info("Ready to build a new [{}] with attribute [{}] = [{}]", NodeSecurityDataConstants.SECURITY,
                NodeSecurityDataConstants.CERT_ENROLL_STATE, ModelDefinition.Security.CertEnrollStateValue.ERROR.toString());

        final Map<String, Object> securityAttributes = createMapAndInsertValues(NodeSecurityDataConstants.MANDATORY_SECURITY_ATTRIBUTE,
                NodeSecurityDataConstants.MANDATORY_SECURITY_ATTRIBUTE_VALUE, NodeSecurityDataConstants.OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE,
                level.getLevel(), NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_SECURITY_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.FILE_TRANSFER_CLIENT_MODE, NodeSecurityDataConstants.FILE_TRANSFER_CLIENT_MODE_VALUE,
                NodeSecurityDataConstants.CERT_ENROLL_STATE, "ERROR" /* ModelDefinition.Security.CertEnrollStateValue.ERROR.toString() */);

        securityAttributes.put(Security.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE, Boolean.TRUE);
        securityAttributes.put(NodeSecurityDataConstants.ACTIVE_USER_PROFILE_COMPLEX_ATTRIBUTE, userDefProfilesInfoAttributesMap);
        //securityAttributes.put(Model.ME_CONTEXT.managedElement.systemFunctions.security.CERT_REV_STATUS_CHECK, "");

        final ManagedObject securityMo = createManagedObject(liveBucket, parentMo, securityAttributes, NodeSecurityDataConstants.SECURITY,
                nscsModelInfo);

        return securityMo;
    }

    public ManagedObject createSystemFunctions(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo nscsModelInfo) {

        final Map<String, Object> mandatorySystemFunctionsAttributes = createMapAndInsertValues(
                NodeSecurityDataConstants.MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE, NodeSecurityDataConstants.MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE_VALUE);

        final ManagedObject systemFunctionsMo = createManagedObject(liveBucket, parentMo, mandatorySystemFunctionsAttributes,
                NodeSecurityDataConstants.SYSTEM_FUNCTIONS, nscsModelInfo);

        return systemFunctionsMo;
    }

    private ManagedObject createNetworkElementTree(DataBucket liveBucket, ManagedObject siblingMo, final String syncStatus, final String ossModelId) {
        String neName = networkElementNameFromMeContextName(siblingMo.getName());
        ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace(Model.NETWORK_ELEMENT.namespace()).type(Model.NETWORK_ELEMENT.type())
                .addAttribute(NetworkElement.NETWORK_ELEMENT_ID, "1").addAttribute(NetworkElement.NE_TYPE, "ERBS")
                .addAttribute(NetworkElement.PLATFORM_TYPE, "CPP").addAttribute(NetworkElement.OSS_MODEL_IDENTITY, ossModelId).name(neName)
                .version(NodeSecurityDataConstants.NETWORK_ELEMENT_VERSION).create();
        log.info("Created NetworkElement.....  name {}, managedObject {}", neName, networkElement);

        networkElement.addAssociation("nodeRootRef", siblingMo);
        log.info("Created association {} <--> {}", siblingMo.getName(), neName);

        liveBucket.getMibRootBuilder().namespace(Model.NETWORK_ELEMENT.securityFunction.namespace())
                .type(Model.NETWORK_ELEMENT.securityFunction.type()).addAttribute(SecurityFunction.SECURITY_FUNCTION_ID, "1").name("1")
                .version("1.0.0").parent(networkElement).create();
        log.info("Created SecurityFunctions.....   managedObject {}", networkElement);

        liveBucket.getMibRootBuilder().namespace(Model.NETWORK_ELEMENT.cmFunction.namespace()).type(Model.NETWORK_ELEMENT.cmFunction.type())
                .addAttribute(CmFunction.SYNC_STATUS, syncStatus).name("1").version("1.0.0").parent(networkElement).create();
        log.info("Created CmFunction.....   managedObject {}", networkElement);

        final String ipAddress = IPV4_ADDRESS;
        liveBucket.getMibRootBuilder().namespace(Model.NETWORK_ELEMENT.cppConnectivityInformation.namespace())
                .type(Model.NETWORK_ELEMENT.cppConnectivityInformation.type())
                .addAttribute(ModelDefinition.CppConnectivityInformation.IPADDRESS, ipAddress).name("1").version("1.0.0").parent(networkElement)
                .create();
        log.info("Created cppConnectivityInformation.....   managedObject {}", networkElement);

        return networkElement;
    }

    public static String networkElementNameFromMeContextName(String meContextName) {
        return meContextName + NETWORK_ELEMENT_NAME_SULFIX;
    }

    public ManagedObject createMeContext(final DataBucket liveBucket, final String nodeName) {

        final ManagedObject parentMo = liveBucket.getMibRootBuilder().namespace(NodeSecurityDataConstants.TOP_NAMESPACE).type("MeContext")
                .addAttribute("neType", "ERBS").addAttribute("MeContextId", "1").version(NodeSecurityDataConstants.TOP_NAMESPACE_VERSION)
                .name(nodeName).create();

        log.info("Created MeContext.....  name {}, managedObject {}, fdn {}", nodeName, parentMo, parentMo.getFdn());
        return parentMo;
    }

    public ManagedObject createManagedElement(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo nscsModelInfo) {

        final Map<String, Object> mandatoryManagedElementAttributes = createMapAndInsertValues(
                NodeSecurityDataConstants.MANDATORY_MANAGED_ELEMENT_ATTRIBUTE, NodeSecurityDataConstants.MANDATORY_MANAGED_ELEMENT_ATTRIBUTE_VALUE);

        mandatoryManagedElementAttributes.putAll(
                createMapAndInsertValues(NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE_VALUE));

        log.info("mandatoryManagedElementAttributes {}", mandatoryManagedElementAttributes);
        final ManagedObject managedElementMo = createManagedObject(liveBucket, parentMo, mandatoryManagedElementAttributes,
                NodeSecurityDataConstants.MANAGED_ELEMENT, nscsModelInfo);
        return managedElementMo;
    }

    private ManagedObject createManagedObject(final DataBucket liveBucket, final ManagedObject parentMo,
            final Map<String, Object> managedObjectAttributes, final String type, final NscsModelInfo nscsModelInfo) {

        final ManagedObject securityMo = liveBucket.getMibRootBuilder().namespace(nscsModelInfo.getNamespace()).type(type)
                .version(nscsModelInfo.getVersion()).name(NodeSecurityDataConstants.MO_NAME).addAttributes(managedObjectAttributes).parent(parentMo)
                .create();
        return securityMo;
    }

    @Override
    public void deleteAllNodes() throws Exception {
        log.info("Deleting CPP Nodes..................");
        deleteAllNodes(NodeSecurityDataConstants.TOP_NAMESPACE, "MeContext");
        deleteAllNodes(Model.NETWORK_ELEMENT.namespace(), Model.NETWORK_ELEMENT.type());
        deleteAllNodes(Model.NETWORK_ELEMENT.cppConnectivityInformation.namespace(), Model.NETWORK_ELEMENT.cppConnectivityInformation.type());
        log.info("Deleting CPP Nodes..................End");

    }

    private DataBucket getLiveBucket() {
        return eserviceHolder.getDataPersistenceService().getDataBucket("Live", BucketProperties.SUPPRESS_MEDIATION,
                BucketProperties.SUPPRESS_CONSTRAINTS);
    }

    public Map<String, Object> createMapAndInsertValues(final String... keyValues) {
        final Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < keyValues.length; i += 2) {
            log.info("key [{}] value [{}]", keyValues[i], keyValues[i + 1]);
            map.put(keyValues[i], keyValues[i + 1]);
        }

        return map;
    }

    public enum IpAddressVersion {
        IPv4,
        IPv6;
    }

    @Override
    public String getNeName() {
        return neName;
    }

    public void createCppNodeForCrlCheckWithoutSecurityMO(final String nodeName, final String syncStatus, final SecurityLevel level)
            throws Exception {
        log.info("[NSCS_ARQ_CPP_NODES_DATA_SETUP] starting createCppNodeForCrlCheckWithoutSecurityMO {}", nodeName);

        beginTransaction();

        final DataBucket liveBucket = getLiveBucket();

        final String targetModelIdentity = getTargetModelIdentityForTestNode(NodeSecurityDataConstants.ERBS_TARGET_TYPE);
        Map<String, NscsModelInfo> modelInfoMap = null;
        try {
            modelInfoMap = eserviceHolder.getNscsModelService().getModelInfoList(TargetTypeInformation.CATEGORY_NODE,
                    NodeSecurityDataConstants.ERBS_TARGET_TYPE, targetModelIdentity, requestedModelsForNode);
        } catch (IllegalArgumentException | NscsModelServiceException ex) {
            log.error("getModelInfoList() caught exception: ", ex);
        }
        if ((modelInfoMap != null) && !modelInfoMap.isEmpty()) {
            // level 1 MeContext Mib in OSS_TOP model
            final ManagedObject parentMo = createMeContext(liveBucket, nodeName);

            final ManagedObject networkElement = createNetworkElementTree(liveBucket, parentMo, syncStatus,
                    getTargetModelIdentityForTestNode(NodeSecurityDataConstants.ERBS_TARGET_TYPE));

            // level 2 create ManagedElement Mib under CPP model

            final ManagedObject managedElementMO = createManagedElement(liveBucket, parentMo,
                    modelInfoMap.get(NodeSecurityDataConstants.MANAGED_ELEMENT));
            log.info("Test Setup ManagedElement, created: " + managedElementMO.getFdn());

            managedElementMO.addAssociation("networkElementRef", networkElement);
            log.info("Test Setup ManagedElement Association , created: " + managedElementMO.getFdn());

            // level 3 create SYSTEM FUNCTIONS

            final ManagedObject systemFunctionsMO = createSystemFunctions(liveBucket, managedElementMO,
                    modelInfoMap.get(NodeSecurityDataConstants.SYSTEM_FUNCTIONS));
            log.info("Test Setup SystemFunctions, created: " + systemFunctionsMO.getFdn());
        }

        commitTransaction();

        log.info("[NSCS_ARQ_CPP_NODES_DATA_SETUP] END createCppNodeForCrlCheckWithoutSecurityMO {}", nodeName);

    }

    private void createCPPNodeForCiphers(final String nodeName, final String syncStatus, final Boolean ciphersConfigurationSupported)
            throws Exception {

        log.info("[NSCS_ARQ_CPP_NODES_DATA_SETUP] Creating Cpp node for Ciphers configuration");

        beginTransaction();

        final DataBucket liveBucket = getLiveBucket();

        final String[] requestedModelsForCiphersNode = { NodeSecurityDataConstants.MANAGED_ELEMENT, NodeSecurityDataConstants.SYSTEM_FUNCTIONS,
                NodeSecurityDataConstants.SECURITY, ModelDefinition.TLS_TYPE, ModelDefinition.SSH_TYPE };

        final String targetModelIdentity = getTargetModelIdentityForCiphersTestNode(NodeSecurityDataConstants.ERBS_TARGET_TYPE,
                ciphersConfigurationSupported);

        Map<String, NscsModelInfo> modelInfoMap = null;
        final String currentRequestedModels[] = ciphersConfigurationSupported ? requestedModelsForCiphersNode : requestedModelsForNode;
        try {
            modelInfoMap = eserviceHolder.getNscsModelService().getModelInfoList(TargetTypeInformation.CATEGORY_NODE,
                    NodeSecurityDataConstants.ERBS_TARGET_TYPE, targetModelIdentity, currentRequestedModels);
        } catch (IllegalArgumentException | NscsModelServiceException ex) {
            log.error("getModelInfoList() caught exception: ", ex);
        }
        final ManagedObject parentMo = createMeContext(liveBucket, nodeName);

        final ManagedObject networkElement = createNetworkElementTree(liveBucket, parentMo, syncStatus, targetModelIdentity);

        if ((modelInfoMap != null) && !modelInfoMap.isEmpty()) {
            final ManagedObject managedElementMO = createManagedElement(liveBucket, parentMo,
                    modelInfoMap.get(NodeSecurityDataConstants.MANAGED_ELEMENT));
            log.info("Test Setup ManagedElement, created: " + managedElementMO.getFdn());

            managedElementMO.addAssociation("networkElementRef", networkElement);
            log.info("Test Setup ManagedElement Association , created: " + managedElementMO.getFdn());

            final ManagedObject systemFunctionsMO = createSystemFunctions(liveBucket, managedElementMO,
                    modelInfoMap.get(NodeSecurityDataConstants.SYSTEM_FUNCTIONS));
            log.info("Test Setup SystemFunctions, created: " + systemFunctionsMO.getFdn());

            final Map<String, Object> securityAttributes = createMapAndInsertValues(NodeSecurityDataConstants.MANDATORY_SECURITY_ATTRIBUTE, "1");

            final ManagedObject securityMO = createManagedObject(liveBucket, systemFunctionsMO, securityAttributes,
                    NodeSecurityDataConstants.SECURITY, modelInfoMap.get(NodeSecurityDataConstants.SECURITY));
            log.info("Test Setup securityMO, created: " + securityMO.getFdn());
            if (ciphersConfigurationSupported) {
                createTlsMo(liveBucket, securityMO, modelInfoMap.get(ModelDefinition.TLS_TYPE));
                createSshMo(liveBucket, securityMO, modelInfoMap.get(ModelDefinition.SSH_TYPE));
            }
        }

        commitTransaction();

        log.info("[NSCS_ARQ_CPP_NODES_DATA_SETUP] Cpp node created for Ciphers configuration");
    }

    /**
     * Create a CPP node suitable for Ciphers tests.
     *
     * @param nodeName
     *            the node name
     * @param syncStatus
     * @param ciphersConfigurationSupported
     *            flag to specify if node TMI must support Cipher CLI commands
     */
    public void createCPPNodeForCiphersConfig(final String nodeName, final String syncStatus, final Boolean ciphersConfigurationSupported)
            throws Exception {
        log.info("Creating CPP node with Ciphers configuration support [{}]", ciphersConfigurationSupported);
        this.createCPPNodeForCiphers(nodeName, syncStatus, ciphersConfigurationSupported);
    }

    private ManagedObject createTlsMo(final DataBucket liveBucket, final ManagedObject parentMo, NscsModelInfo nscsModelInfo) {

        final Map<String, Object> mandatoryTlsAttributes = createMapAndInsertValues(ModelDefinition.SecurityTls.TLS_ID, "1");
        log.info("mandatoryTlsAttributes {}", mandatoryTlsAttributes);

        final ManagedObject tlsMo = createManagedObject(liveBucket, parentMo, mandatoryTlsAttributes, ModelDefinition.TLS_TYPE, nscsModelInfo);
        log.info("Tls MO created {}", tlsMo.getFdn());
        return tlsMo;
    }

    private ManagedObject createSshMo(final DataBucket liveBucket, final ManagedObject parentMo, NscsModelInfo nscsModelInfo) {
        final Map<String, Object> mandatorySshAttributes = createMapAndInsertValues(ModelDefinition.SecuritySsh.SSH_ID, "1");
        log.info("mandatorySshAttributes {}", mandatorySshAttributes);

        final ManagedObject sshMo = createManagedObject(liveBucket, parentMo, mandatorySshAttributes, ModelDefinition.SSH_TYPE, nscsModelInfo);
        log.info("Ssh MO created {}", sshMo.getFdn());
        return sshMo;
    }

}
