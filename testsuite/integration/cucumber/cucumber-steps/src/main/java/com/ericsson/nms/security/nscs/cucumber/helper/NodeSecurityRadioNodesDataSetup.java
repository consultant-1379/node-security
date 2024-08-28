/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cucumber.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.oss.itpf.datalayer.dps.BucketProperties;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;

/**
 *
 * @author ebarmos, emelant
 */
public class NodeSecurityRadioNodesDataSetup {

    @Inject
    EServiceProducer eserviceHolder;

    @Inject
    public UserTransaction userTransaction;

    @Inject
    Logger log;

    private NscsCMReaderService readerService;

    final private static String managedElementType = Model.COM_MANAGED_ELEMENT.type();
    final private static String systemFunctionsType = Model.COM_MANAGED_ELEMENT.systemFunctions.type();
    final private static String secMType = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.type();
    final private static String sysMType = Model.COM_MANAGED_ELEMENT.systemFunctions.sysM.type();
    final private static String netconfTlsType = Model.COM_MANAGED_ELEMENT.systemFunctions.sysM.netconfTls.type();
    final private static String certMType = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.type();
    final private static String certMCapabilitiesType = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.certMCapabilities.type();
    final private static String trustCategoryType = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.trustCategory.type();
    final private static String nodeCredentialType = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.nodeCredential.type();
    final private static String userManagementType = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.userManagement.type();
    final private static String ldapAuthMethodType = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.userManagement.ldapAuthenticationMethod.type();
    final private static String ldapType = Model.COM_MANAGED_ELEMENT.systemFunctions.secM.userManagement.ldapAuthenticationMethod.ldap.type();

    public void insertData() throws Exception {
        deleteAllNodes();
        createNode(NodeSecurityDataConstants.NODE_NAME3);
    }

    private void createNode(final String nodeName, final String syncStatus) throws Exception {
        createNode(nodeName, syncStatus, NodeSecurityDataConstants.OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE_VALUE);
    }

    public void createNode(final String nodeName, final String syncStatus, final SecurityLevel level) throws Exception {

        userTransaction.begin();
        final DataBucket liveBucket = getLiveBucket();

        final ManagedObject parentMo = createMeContext(liveBucket, nodeName);

        createNetworkElementTree(liveBucket, parentMo, syncStatus,
                NodeSecurityDataConstants.NETWORK_ELEMENT_OSS_MODEL_IDENTITY_RADIO_VERSION);

        userTransaction.commit();
        log.info("Test Setup, Transaction Commited");
    }

    public void createComEcimNode(final String nodeName, final String syncStatus, final SecurityLevel level, final String actionName)
            throws Exception {

        userTransaction.begin();

        final String requestedModels[] = { managedElementType, systemFunctionsType, secMType, certMType, certMCapabilitiesType, trustCategoryType,
                nodeCredentialType };

        final String ossModelIdentity = NodeSecurityDataConstants.NETWORK_ELEMENT_OSS_MODEL_IDENTITY_RADIO_VERSION;
        Map<String, NscsModelInfo> modelInfoMap = null;
        try {
            modelInfoMap = eserviceHolder.getNscsModelService().getModelInfoList("NODE", "RadioNode",
                    ossModelIdentity, requestedModels);
        } catch (IllegalArgumentException | NscsModelServiceException ex) {
            log.error("getModelInfoList() caught exception: ", ex);
        }

        if ((modelInfoMap != null) && !modelInfoMap.isEmpty()) {
            final DataBucket liveBucket = getLiveBucket();

            final ManagedObject parentMo = createMeContext(liveBucket, nodeName);

            createNetworkElement(liveBucket, parentMo, syncStatus, ossModelIdentity);

            final ManagedObject managedElementMO = createComEcimManagedElement(liveBucket, parentMo, modelInfoMap.get(managedElementType));
            log.info("Test Setup ManagedElement, created: " + managedElementMO.getFdn());

            final ManagedObject systemFunctionsMO = createComEcimSystemFunctions(liveBucket, managedElementMO, modelInfoMap.get(systemFunctionsType));
            log.info("Test Setup SystemFunctions, created: " + systemFunctionsMO.getFdn());

            final ManagedObject secMMO = createSecM(liveBucket, systemFunctionsMO, modelInfoMap.get(secMType));
            log.info("Test Setup SecM, created: " + secMMO.getFdn());

            final ManagedObject certMMO = createCertM(liveBucket, secMMO, modelInfoMap.get(certMType));
            log.info("Test Setup CertM, created: " + certMMO.getFdn());

            final ManagedObject certMCapabilitiesMO = createCertMCapabilities(liveBucket, certMMO, modelInfoMap.get(certMCapabilitiesType));
            log.info("Test Setup certMCapabilities, created: " + certMCapabilitiesMO.getFdn());

            final ManagedObject trustCategoryMO = createTrustCategory(liveBucket, certMMO, modelInfoMap.get(trustCategoryType));
            log.info("Test Setup trustCategory, created: " + trustCategoryMO.getFdn());

            final ManagedObject nodeCredentialMO = createNodeCredential(liveBucket, certMMO, actionName, modelInfoMap.get(nodeCredentialType));
            log.info("Test Setup nodeCredential, created: " + nodeCredentialMO.getFdn());
        }
        userTransaction.commit();
        log.info("Test Setup, Transaction Commited");

    }

    public void createComEcimNode(final String nodeName, final String syncStatus, final SecurityLevel level) throws Exception {

        userTransaction.begin();

        final String requestedModels[] = { managedElementType, systemFunctionsType, secMType, certMType, certMCapabilitiesType, userManagementType,
                ldapAuthMethodType, ldapType };

        final String ossModelIdentity = NodeSecurityDataConstants.NETWORK_ELEMENT_OSS_MODEL_IDENTITY_RADIO_VERSION;
        Map<String, NscsModelInfo> modelInfoMap = null;
        try {
            modelInfoMap = eserviceHolder.getNscsModelService().getModelInfoList("NODE", "RadioNode",
                    ossModelIdentity, requestedModels);
        } catch (IllegalArgumentException | NscsModelServiceException ex) {
            log.error("getModelInfoList() caught exception: ", ex);
        }

        if ((modelInfoMap != null) && !modelInfoMap.isEmpty()) {
            final DataBucket liveBucket = getLiveBucket();

            final ManagedObject meContextMO = createMeContext(liveBucket, nodeName);

            createNetworkElementTree(liveBucket, meContextMO, syncStatus, ossModelIdentity);

            final ManagedObject managedElementMO = createComEcimManagedElement(liveBucket, meContextMO, modelInfoMap.get(managedElementType));
            log.info("Test Setup ManagedElement, created: " + managedElementMO.getFdn());

            final ManagedObject systemFunctionsMO = createComEcimSystemFunctions(liveBucket, managedElementMO, modelInfoMap.get(systemFunctionsType));
            log.info("Test Setup SystemFunctions, created: " + systemFunctionsMO.getFdn());

            final ManagedObject secMMO = createSecM(liveBucket, systemFunctionsMO, modelInfoMap.get(secMType));
            log.info("Test Setup SecM, created: " + secMMO.getFdn());

            final ManagedObject userManagementMO = createUserManagement(liveBucket, secMMO, modelInfoMap.get(userManagementType));
            log.info("Test Setup UserManagement, created: " + userManagementMO.getFdn());

            final ManagedObject LdapAuthenticationMethodMO = createLdapAuthenticationMethod(liveBucket, userManagementMO,
                    modelInfoMap.get(ldapAuthMethodType));
            log.info("Test Setup LdapAuthenticationMethod, created: " + LdapAuthenticationMethodMO.getFdn());

            final ManagedObject LdapMO = createLdap(liveBucket, LdapAuthenticationMethodMO, modelInfoMap.get(ldapType));
            log.info("Test Setup Ldap, created: " + LdapMO.getFdn());

            final ManagedObject certMMO = createCertM(liveBucket, secMMO, modelInfoMap.get(certMType));
            log.info("Test Setup CertM, created: " + certMMO.getFdn());

            final ManagedObject certMCapabilitiesMO = createCertMCapabilities(liveBucket, certMMO, modelInfoMap.get(certMCapabilitiesType));
            log.info("Test Setup certMCapabilities, created: " + certMCapabilitiesMO.getFdn());
        }
        userTransaction.commit();
        log.info("Test Setup, Transaction Commited");

    }

    private ManagedObject createManagedObject(final DataBucket liveBucket, final ManagedObject parentMo,
            final Map<String, Object> managedObjectAttributes, final String type, final String namespace, final String version, final String name) {

        final ManagedObject mo = liveBucket.getMibRootBuilder().namespace(namespace).type(type).version(version).name(name)
                .addAttributes(managedObjectAttributes).parent(parentMo).create();
        return mo;
    }

    private static String networkElementNameFromMeContextName(final String meContextName) {
        return meContextName;
    }

    private ManagedObject createNetworkElementTree(final DataBucket liveBucket, final ManagedObject siblingMo, final String syncStatus,
            final String ossModelId) {
        final String neName = networkElementNameFromMeContextName(siblingMo.getName());
        final ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_NS).type(Model.NETWORK_ELEMENT.type())
                .addAttribute(NodeModelDefs.NETWORK_ELEMENT_ID, "1").addAttribute(Model.NETWORK_ELEMENT.NE_TYPE, "RadioNode")
                .addAttribute(NodeModelDefs.OSS_MODEL_IDENTITY, ossModelId).name(neName).version(NodeSecurityDataConstants.NETWORK_ELEMENT_VERSION)
                .create();
        log.info("Created NetworkElement.....  name {}, managedObject {}", neName, networkElement);

        networkElement.addAssociation("nodeRootRef", siblingMo);
        log.info("Created association {} <--> {}", siblingMo.getName(), neName);

        liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_SEC_NS).type(Model.NETWORK_ELEMENT.securityFunction.type())
                .addAttribute(NodeModelDefs.SECURITY_FUNCTION_ID, "1").name("1").version("1.0.0").parent(networkElement).create();
        log.info("Created SecurityFunctions.....   managedObject {}", networkElement);

        liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_CM_NS).type(Model.NETWORK_ELEMENT.cmFunction.type())
                .addAttribute(NodeModelDefs.SYNC_STATUS, syncStatus).name("1").version("1.0.0").parent(networkElement).create();
        log.info("Created CmFunction.....   managedObject {}", networkElement);

        return networkElement;
    }

    private ManagedObject createNetworkElement(final DataBucket liveBucket, final ManagedObject siblingMo, final String syncStatus,
            final String ossModelId) {
        final String neName = networkElementNameFromMeContextName(siblingMo.getName());
        final ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_NS).type(Model.NETWORK_ELEMENT.type())
                .addAttribute(NodeModelDefs.NETWORK_ELEMENT_ID, "1").addAttribute(Model.NETWORK_ELEMENT.NE_TYPE, "RadioNode")
                .addAttribute(NodeModelDefs.OSS_MODEL_IDENTITY, ossModelId).name(neName).version(NodeSecurityDataConstants.NETWORK_ELEMENT_VERSION)
                .create();
        log.info("Created NetworkElement.....  name {}, managedObject {}", neName, networkElement);

        networkElement.addAssociation("nodeRootRef", siblingMo);
        log.info("Created association {} <--> {}", siblingMo.getName(), neName);

        return networkElement;
    }

    private ManagedObject createMeContext(final DataBucket liveBucket, final String nodeName) {

        final ManagedObject parentMo = liveBucket.getMibRootBuilder().namespace(NodeSecurityDataConstants.TOP_NAMESPACE).type("MeContext")
                .addAttribute("neType", "RadioNode").addAttribute("MeContextId", "1").version(NodeSecurityDataConstants.TOP_NAMESPACE_VERSION)
                .name(nodeName).create();

        System.out.println("Test Setup, created: " + parentMo.getFdn());
        return parentMo;
    }

    private ManagedObject createComEcimManagedElement(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo modelInfo) {

        final Map<String, Object> mandatoryManagedElementAttributes = createMapAndInsertValues(NodeModelDefs.MANAGED_ELEMENT_ID,
                NodeSecurityDataConstants.MANDATORY_MANAGED_ELEMENT_ATTRIBUTE_VALUE);
        log.info("mandatoryManagedElementAttributes {}", mandatoryManagedElementAttributes);

        final ManagedObject managedElementMo = createManagedObject(liveBucket, parentMo, mandatoryManagedElementAttributes,
                Model.COM_MANAGED_ELEMENT.type(), modelInfo.getNamespace(),
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.type(), modelInfo.getNamespace()), "1");
        //        final ManagedObject managedElementMo = createManagedObject(liveBucket, parentMo, mandatoryManagedElementAttributes, Model.COM_MANAGED_ELEMENT.type(), Model.COM_MANAGED_ELEMENT.namespace(),
        //                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.type(), Model.COM_MANAGED_ELEMENT.namespace()), "1");

        return managedElementMo;
    }

    private ManagedObject createComEcimManagedElement(final DataBucket liveBucket, final String nodeName) {

        final Map<String, Object> mandatoryManagedElementAttributes = createMapAndInsertValues(NodeModelDefs.MANAGED_ELEMENT_ID,
                NodeSecurityDataConstants.MANDATORY_MANAGED_ELEMENT_ATTRIBUTE_VALUE);
        log.info("mandatoryManagedElementAttributes {}", mandatoryManagedElementAttributes);

        final String version = readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.type(), NodeModelDefs.COM_TOP_NS);

        log.info("Managed Element Version : {}", version);
        final ManagedObject managedElementMo = liveBucket.getMibRootBuilder().namespace(NodeModelDefs.COM_TOP_NS)
                .type(Model.COM_MANAGED_ELEMENT.type()).version(version).name(nodeName).addAttributes(mandatoryManagedElementAttributes).create();

        return managedElementMo;
    }

    private ManagedObject createComEcimSystemFunctions(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo modelInfo) {

        final Map<String, Object> mandatorySystemFunctionsAttributes = createMapAndInsertValues(NodeModelDefs.SYSTEM_FUNCTIONS_ID,
                NodeSecurityDataConstants.MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE_VALUE);

        log.info("mandatorySystemFunctionsAttributes {}", mandatorySystemFunctionsAttributes);

        final ManagedObject systemFunctionsMo = createManagedObject(liveBucket, parentMo, mandatorySystemFunctionsAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.type(), modelInfo.getNamespace(),
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.type(), modelInfo.getNamespace()), "1");

        return systemFunctionsMo;

    }

    private ManagedObject createUserManagement(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo modelInfo) {

        final Map<String, Object> mandatoryUserManagementAttributes = createMapAndInsertValues(NodeModelDefs.USER_MANAGEMENT_ID, "1");

        log.info("mandatoryUserManagementAttributes {}", mandatoryUserManagementAttributes);

        final ManagedObject userManagementMo = createManagedObject(liveBucket, parentMo, mandatoryUserManagementAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.secM.userManagement.type(), modelInfo.getNamespace(),
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.secM.userManagement.type(), modelInfo.getNamespace()), "1");

        return userManagementMo;

    }

    private ManagedObject createLdapAuthenticationMethod(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo modelInfo) {

        final Map<String, Object> mandatoryLdapAuthenticationMethodAttributes = createMapAndInsertValues(NodeModelDefs.LDAP_AUTHENTICATION_METHOD_ID,
                "1");

        log.info("mandatoryLdapAuthenticationMethodAttributes {}", mandatoryLdapAuthenticationMethodAttributes);

        final ManagedObject ldapAuthenticationMethodMo = createManagedObject(liveBucket, parentMo, mandatoryLdapAuthenticationMethodAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.secM.userManagement.ldapAuthenticationMethod.type(), modelInfo.getNamespace(),
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.secM.userManagement.ldapAuthenticationMethod.type(),
                        modelInfo.getNamespace()),
                "1");

        return ldapAuthenticationMethodMo;

    }

    private ManagedObject createLdap(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo modelInfo) {

        final Map<String, Object> mandatoryLdapAttributes = new HashMap<>();

        mandatoryLdapAttributes.put(NodeModelDefs.SERVER_PORT, 1636);
        mandatoryLdapAttributes.put(NodeModelDefs.BASE_DN, "uid=ssouser,ou=people,dc=apache,dc=com");
        mandatoryLdapAttributes.put(NodeModelDefs.BIND_DN, "cn=ProxyAccount_5,ou=Profiles,dc=apache,dc=com");
        mandatoryLdapAttributes.put(NodeModelDefs.TLS_MODE, "STARTTLS");
        mandatoryLdapAttributes.put(NodeModelDefs.USE_TLS, true);
        mandatoryLdapAttributes.put(NodeModelDefs.LDAP_ID, "1");

        log.info("mandatoryLdapAttributes {}", mandatoryLdapAttributes);

        final ManagedObject ldapMo = createManagedObject(liveBucket, parentMo, mandatoryLdapAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.secM.userManagement.ldapAuthenticationMethod.ldap.type(), modelInfo.getNamespace(),
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.secM.userManagement.ldapAuthenticationMethod.ldap.type(),
                        modelInfo.getNamespace()),
                "1");

        return ldapMo;
    }

    private ManagedObject createSecM(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo modelInfo) {

        final Map<String, Object> mandatorySecMAttributes = createMapAndInsertValues(NodeModelDefs.SEC_M_ID, "1");

        log.info("mandatorySecMAttributes {}", mandatorySecMAttributes);

        final ManagedObject secMMo = createManagedObject(liveBucket, parentMo, mandatorySecMAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.secM.type(), modelInfo.getNamespace(),
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.secM.type(), modelInfo.getNamespace()), "1");

        return secMMo;

    }

    private ManagedObject createCertM(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo modelInfo) {

        final Map<String, Object> mandatoryCertMAttributes = createMapAndInsertValues(NodeModelDefs.CERT_M_ID, "1", NodeModelDefs.REPORT_PROGRESS,
                null);

        log.info("mandatoryCertMAttributes {}", mandatoryCertMAttributes);

        final ManagedObject certMMo = createManagedObject(liveBucket, parentMo, mandatoryCertMAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.type(), modelInfo.getNamespace(),
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.type(), modelInfo.getNamespace()), "1");

        return certMMo;

    }

    private ManagedObject createCertMCapabilities(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo modelInfo) {

        final List<String> enrollmentSupportList = new ArrayList<>();
        enrollmentSupportList.add(NodeModelDefs.EnrollmentSupport.ONLINE_SCEP.name());

        final Map<String, Object> mandatoryCertMCapabilitiesAttributes = new HashMap<String, Object>();
        mandatoryCertMCapabilitiesAttributes.put(NodeModelDefs.CERT_M_CAPABILITIES_ID, "1");
        mandatoryCertMCapabilitiesAttributes.put(NodeModelDefs.ENROLLMENT_SUPPORT, enrollmentSupportList);
        log.info("mandatoryCertMCapabilitiesAttributes {}", mandatoryCertMCapabilitiesAttributes);

        final ManagedObject certMCapabilitiesMo = createManagedObject(liveBucket, parentMo, mandatoryCertMCapabilitiesAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.certMCapabilities.type(), modelInfo.getNamespace(), readerService
                        .getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.certMCapabilities.type(), modelInfo.getNamespace()),
                "1");

        return certMCapabilitiesMo;

    }

    private ManagedObject createTrustCategory(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo modelInfo) {

        final Map<String, Object> mandatoryTrustCategoryAttributes = new HashMap<String, Object>();
        mandatoryTrustCategoryAttributes.put(NodeModelDefs.TRUST_CATEGORY_ID, "1");
        log.info("mandatoryTrustCategoryAttributes {}", mandatoryTrustCategoryAttributes);

        final ManagedObject trustCategoryMo = createManagedObject(liveBucket, parentMo, mandatoryTrustCategoryAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.trustCategory.type(), modelInfo.getNamespace(),
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.trustCategory.type(), modelInfo.getNamespace()),
                "1");

        return trustCategoryMo;

    }

    public void deleteAllNodes() throws Exception {
        deleteAllNodes(NodeSecurityDataConstants.TOP_NAMESPACE, "MeContext");
        deleteAllNodes(NodeModelDefs.COM_TOP_NS, Model.COM_MANAGED_ELEMENT.type());
        deleteAllNodes(NodeModelDefs.NE_NS, Model.NETWORK_ELEMENT.type());
    }

    private void deleteAllNodes(final String namespace, final String type) throws Exception {
        userTransaction.begin();
        final QueryBuilder queryBuilder = eserviceHolder.getDataPersistenceService().getQueryBuilder();
        final Query query = queryBuilder.createTypeQuery(namespace, type);

        final Iterator<PersistenceObject> iterator = getLiveBucket().getQueryExecutor().execute(query);
        while (iterator.hasNext()) {
            final PersistenceObject po = iterator.next();
            eserviceHolder.getDataPersistenceService().getLiveBucket().deletePo(po);
        }
        userTransaction.commit();
    }

    private DataBucket getLiveBucket() {
        return getDataPersistenceService().getDataBucket("Live", BucketProperties.SUPPRESS_MEDIATION, BucketProperties.SUPPRESS_CONSTRAINTS);
    }

    private com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService getDataPersistenceService() {
        return eserviceHolder.getDataPersistenceService();
    }

    private void createNode(final String nodeName) throws Exception {
        createNode(nodeName, NodeModelDefs.SyncStatusValue.SYNCHRONIZED.name());
    }

    public ManagedObject createManagedElement(final DataBucket liveBucket, final ManagedObject parentMo) {

        final Map<String, Object> mandatoryManagedElementAttributes = createMapAndInsertValues(
                NodeSecurityDataConstants.MANDATORY_MANAGED_ELEMENT_ATTRIBUTE, NodeSecurityDataConstants.MANDATORY_MANAGED_ELEMENT_ATTRIBUTE_VALUE);

        mandatoryManagedElementAttributes.putAll(
                createMapAndInsertValues(NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE_VALUE));

        log.info("mandatoryManagedElementAttributes {}", mandatoryManagedElementAttributes);

        final ManagedObject managedElementMo = liveBucket.getMibRootBuilder().namespace(NodeModelDefs.COM_TOP_NS)
                .type(Model.ME_CONTEXT.comManagedElement.type()).version(NodeSecurityDataConstants.MANAGED_ELEMENT_VERSION_VALUE_COM_ECIM)
                .name(NodeSecurityDataConstants.MO_NAME).addAttributes(mandatoryManagedElementAttributes).parent(parentMo).create();
        log.info("Created ManagedElement.....   managedObject {}", parentMo);

        return managedElementMo;
    }

    public ManagedObject createSystemFunctions(final DataBucket liveBucket, final ManagedObject parentMo) {

        final Map<String, Object> mandatorySystemFunctionsAttributes = createMapAndInsertValues(
                NodeSecurityDataConstants.MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE, NodeSecurityDataConstants.MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE_VALUE);

        log.info("mandatorySystemFunctionsAttributes {}", mandatorySystemFunctionsAttributes);

        final ManagedObject systemFunctionsMo = liveBucket.getMibRootBuilder().namespace(NodeModelDefs.COM_TOP_NS)
                .type(Model.ME_CONTEXT.comManagedElement.systemFunctions.type()).version(NodeSecurityDataConstants.ERBS_NAMESPACE_VERSION)
                .name(NodeSecurityDataConstants.MO_NAME).addAttributes(mandatorySystemFunctionsAttributes).parent(parentMo).create();

        return systemFunctionsMo;
    }

    private Map<String, Object> createMapAndInsertValues(final String... keyValues) {
        final Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < keyValues.length; i += 2) {
            log.info("key [{}] value [{}]", keyValues[i], keyValues[i + 1]);
            map.put(keyValues[i], keyValues[i + 1]);
        }

        return map;
    }

    private ManagedObject createNodeCredential(final DataBucket liveBucket, final ManagedObject parentMo, final String name,
            final NscsModelInfo modelInfo) {

        final Map<String, Object> enrollmentProgressAttributes = new HashMap<>();
        enrollmentProgressAttributes.put(NodeModelDefs.ACTION_ID, "1");

        final Map<String, Object> mandatoryNodeCredentialAttributes = new HashMap<>();
        mandatoryNodeCredentialAttributes.put(NodeModelDefs.NODE_CREDENTIAL_ID, "1");
        //mandatoryNodeCredentialAttributes.put(NodeModelDefs.NodeCredential.ENROLLMENT_PROGRESS, enrollmentProgressAttributes);

        log.info("mandatoryNodeCredentialAttributes {}", mandatoryNodeCredentialAttributes);

        final ManagedObject nodeCredentialMO = createManagedObject(liveBucket, parentMo, mandatoryNodeCredentialAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.nodeCredential.type(), modelInfo.getNamespace(),
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.nodeCredential.type(), modelInfo.getNamespace()),
                name);
        log.info("Test Setup NodeCredential, created: " + nodeCredentialMO.getFdn());

        return nodeCredentialMO;
    }

    public void createComEcimNodeForCrlCheck(final String nodeName, final String syncStatus, final SecurityLevel level, final String actionName)
            throws Exception {

        userTransaction.begin();

        final String requestedModels[] = { managedElementType, systemFunctionsType, secMType, certMType, sysMType, netconfTlsType,
                certMCapabilitiesType, trustCategoryType, nodeCredentialType };

        final String ossModelIdentity = NodeSecurityDataConstants.NETWORK_ELEMENT_OSS_MODEL_IDENTITY_RADIO_VERSION;
        Map<String, NscsModelInfo> modelInfoMap = null;
        try {
            modelInfoMap = eserviceHolder.getNscsModelService().getModelInfoList("NODE", "RadioNode",
                    ossModelIdentity, requestedModels);
        } catch (IllegalArgumentException | NscsModelServiceException ex) {
            log.error("getModelInfoList() caught exception: ", ex);
        }

        if ((modelInfoMap != null) && !modelInfoMap.isEmpty()) {
            final DataBucket liveBucket = getLiveBucket();

            final ManagedObject parentMo = createMeContext(liveBucket, nodeName);

            final ManagedObject networkElment = createNetworkElementTree(liveBucket, parentMo, syncStatus, ossModelIdentity);
            log.info("Test Setup networkElment, created: " + networkElment.getFdn());

            final ManagedObject managedElementMO = createComEcimManagedElement(liveBucket, parentMo, modelInfoMap.get(managedElementType));
            log.info("Test Setup ManagedElement, created: " + managedElementMO.getFdn());

            final ManagedObject systemFunctionsMO = createComEcimSystemFunctions(liveBucket, managedElementMO, modelInfoMap.get(systemFunctionsType));
            log.info("Test Setup SystemFunctions, created: " + systemFunctionsMO.getFdn());

            final ManagedObject secMMO = createSecM(liveBucket, systemFunctionsMO, modelInfoMap.get(secMType));
            log.info("Test Setup SecM, created: " + secMMO.getFdn());

            final ManagedObject SysMMO = createComEcimSysM(liveBucket, systemFunctionsMO, modelInfoMap.get(sysMType));
            log.info("Test Setup SysM, created: " + SysMMO.getFdn());

            final ManagedObject NetconfTlsMO = createComEcimNetconfTls(liveBucket, SysMMO, modelInfoMap.get(netconfTlsType));
            log.info("Test Setup NetconfTlsMO, created: " + NetconfTlsMO.getFdn());

            final ManagedObject certMMO = createCertM(liveBucket, secMMO, modelInfoMap.get(certMType));
            log.info("Test Setup CertM, created: " + certMMO.getFdn());

            final ManagedObject certMCapabilitiesMO = createCertMCapabilitiesForCrlCheck(liveBucket, certMMO,
                    modelInfoMap.get(certMCapabilitiesType));
            log.info("Test Setup certMCapabilities, created: " + certMCapabilitiesMO.getFdn());

            final ManagedObject trustCategoryMO = createTrustCategory(liveBucket, certMMO, modelInfoMap.get(trustCategoryType));
            log.info("Test Setup trustCategory, created: " + trustCategoryMO.getFdn());

            final ManagedObject nodeCredentialMO = createNodeCredential(liveBucket, certMMO, actionName, modelInfoMap.get(nodeCredentialType));
            log.info("Test Setup nodeCredential, created: " + nodeCredentialMO.getFdn());
        }
        userTransaction.commit();
        log.info("Test Setup, Transaction Commited");

    }

    private ManagedObject createComEcimSysM(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo modelInfo) {

        final Map<String, Object> mandatorySysMAttributes = new HashMap<>();
        mandatorySysMAttributes.put(NodeModelDefs.SYS_M_ID, "1");
        log.info("mandatorySysMAttributes {}", mandatorySysMAttributes);

        final ManagedObject sysMMo = createManagedObject(liveBucket, parentMo, mandatorySysMAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.sysM.type(), modelInfo.getNamespace(),
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.sysM.type(), modelInfo.getNamespace()), "1");

        return sysMMo;

    }

    private ManagedObject createComEcimNetconfTls(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo modelInfo) {

        final Map<String, Object> mandatoryNetconfTlsAttributes = new HashMap<>();
        mandatoryNetconfTlsAttributes.put(NodeModelDefs.NETCONF_TLS_ID, "1");
        mandatoryNetconfTlsAttributes.put(NodeModelDefs.TRUST_CATEGORY, "1");

        log.info("mandatoryNetconfTlsAttributes {}", mandatoryNetconfTlsAttributes);

        final ManagedObject systemFunctionsMo = createManagedObject(liveBucket, parentMo, mandatoryNetconfTlsAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.sysM.netconfTls.type(), modelInfo.getNamespace(),
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.sysM.netconfTls.type(), modelInfo.getNamespace()), "1");

        return systemFunctionsMo;

    }

    private ManagedObject createCertMCapabilitiesForCrlCheck(final DataBucket liveBucket, final ManagedObject parentMo,
            final NscsModelInfo modelInfo) {

        final List<String> enrollmentSupportList = new ArrayList<>();
        enrollmentSupportList.add(NodeModelDefs.EnrollmentSupport.ONLINE_CMP.name());

        final Map<String, Object> mandatoryCertMCapabilitiesAttributes = new HashMap<String, Object>();
        mandatoryCertMCapabilitiesAttributes.put(NodeModelDefs.CERT_M_CAPABILITIES_ID, "1");
        mandatoryCertMCapabilitiesAttributes.put(NodeModelDefs.ENROLLMENT_SUPPORT, enrollmentSupportList);
        log.info("mandatoryCertMCapabilitiesAttributes {}", mandatoryCertMCapabilitiesAttributes);

        final ManagedObject certMCapabilitiesMo = createManagedObject(liveBucket, parentMo, mandatoryCertMCapabilitiesAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.certMCapabilities.type(), modelInfo.getNamespace(), readerService
                        .getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.secM.certM.certMCapabilities.type(), modelInfo.getNamespace()),
                "1");

        return certMCapabilitiesMo;

    }

    public void createComEcimNodeForCiphersConfig(final String nodeName, final String syncStatus, final String ossModelId) throws Exception {
        log.info("Creating ComEcim node for Ciphers configuration");
        userTransaction.begin();

        NscsModelInfo modelInfo = null;
        try {
            modelInfo = eserviceHolder.getNscsModelService().getModelInfo("NODE", "RadioNode", ossModelId,
                    systemFunctionsType);
        } catch (IllegalArgumentException | NscsModelServiceException ex) {
            log.error("getModelInfo(): caught exception: ", ex);
        }

        if (modelInfo != null) {
            final DataBucket liveBucket = getLiveBucket();

            final ManagedObject meContextMO = createMeContext(liveBucket, nodeName);

            final ManagedObject managedElementMO = createComEcimManagedElement(liveBucket, nodeName);

            final ManagedObject networkElement = createNetworkElementTree(liveBucket, meContextMO, syncStatus, ossModelId);

            managedElementMO.addAssociation("networkElementRef", networkElement);

            final ManagedObject systemFunctionsMO = createComEcimSystemFunctions(liveBucket, managedElementMO, modelInfo);

            final ManagedObject secMMO = createRcsSecMMo(liveBucket, systemFunctionsMO);

            createSshMo(liveBucket, secMMO);
            createTlsMo(liveBucket, secMMO);
            log.info("ComEcim node created for Ciphers configuration");
        }
        userTransaction.commit();
    }

    private ManagedObject createRcsSecMMo(final DataBucket liveBucket, final ManagedObject parentMo) {
        final Map<String, Object> mandatorySecMAttributes = createMapAndInsertValues(NodeModelDefs.SEC_M_ID, "1");
        log.info("mandatorySecMAttributes {}", mandatorySecMAttributes);
        final ManagedObject secMMo = createManagedObject(liveBucket, parentMo, mandatorySecMAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.secM.type(), NodeSecurityDataConstants.RCS_SEC_M_NS,
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.secM.type(), NodeSecurityDataConstants.RCS_SEC_M_NS), "1");
        log.info("RcsSecM MO created {}", secMMo.getFdn());
        return secMMo;
    }

    private ManagedObject createTlsMo(final DataBucket liveBucket, final ManagedObject parentMo) {

        final Map<String, Object> mandatoryTlsAttributes = createMapAndInsertValues(NodeModelDefs.TLS_ID, "1");
        log.info("mandatoryTlsAttributes {}", mandatoryTlsAttributes);
        final ManagedObject tlsMo = createManagedObject(liveBucket, parentMo, mandatoryTlsAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.secM.tls.type(), NodeSecurityDataConstants.RCS_SEC_M_NS,
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.secM.tls.type(), NodeSecurityDataConstants.RCS_SEC_M_NS),
                "1");
        log.info("Tls MO created {}", tlsMo.getFdn());
        return tlsMo;
    }

    private ManagedObject createSshMo(final DataBucket liveBucket, final ManagedObject parentMo) {
        final Map<String, Object> mandatorySshAttributes = createMapAndInsertValues(NodeModelDefs.SSH_ID, "1");
        log.info("mandatorySshAttributes {}", mandatorySshAttributes);

        final ManagedObject sshMo = createManagedObject(liveBucket, parentMo, mandatorySshAttributes,
                Model.COM_MANAGED_ELEMENT.systemFunctions.secM.ssh.type(), NodeSecurityDataConstants.RCS_SEC_M_NS,
                readerService.getModelVersion(Model.COM_MANAGED_ELEMENT.systemFunctions.secM.ssh.type(), NodeSecurityDataConstants.RCS_SEC_M_NS),
                "1");
        log.info("Ssh MO created {} {}", sshMo.getFdn(), sshMo);

        log.info("Ssh MO attributes");
        for (final String key : sshMo.getAllAttributes().keySet()) {
            log.info("{}-->{}", key, sshMo.getAllAttributes().get(key));
        }
        return sshMo;
    }
}
