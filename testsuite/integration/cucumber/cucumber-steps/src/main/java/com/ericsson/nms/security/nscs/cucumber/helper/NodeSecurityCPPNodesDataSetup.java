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
package com.ericsson.nms.security.nscs.cucumber.helper;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.RollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.cucumber.helper.laad.PasswordHelper;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.oss.itpf.datalayer.dps.BucketProperties;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;

/**
 * This class is used to create a test node of type ERBS with all mandatory MOs.
 *
 * @author xnagsow
 *
 */
public class NodeSecurityCPPNodesDataSetup {

    protected String neName;
    protected String neNeType;
    protected String nePlatformType;

    @Inject
    protected EServiceProducer eserviceProducer;

    @Inject
    protected UserTransaction dataUserTransaction;

    @Inject
    protected PasswordHelper passwordHelper;

    @Inject
    protected Logger log;

    /**
     * Method to create a test node of type ERBS with all mandatory MOs.
     *
     * @param nodeName
     *            Node name
     * @throws SystemException
     * @throws NotSupportedException
     * @throws HeuristicRollbackException
     * @throws HeuristicMixedException
     * @throws RollbackException
     * @throws Exception
     *             is thrown when any error occurs while creation of network element
     */
    public void createNode(final String nodeName, final String syncStatus) throws NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        log.info("Creatin a test node with name {}", nodeName);

        dataUserTransaction.begin();
        final DataBucket liveBucket = getLiveBucket();

        final ManagedObject parentMo = createMeContext(liveBucket, nodeName);
        log.info("Created MeContext.....  name {}, managedObject {}, fdn {}", nodeName, parentMo, parentMo.getFdn());

        final List<String> targetModelIdentities = eserviceProducer.getNscsModelService()
                .getTargetModelIdentities(TargetTypeInformation.CATEGORY_NODE, NodeSecurityDataConstants.ERBS_TARGET_TYPE);
        final String tmi = targetModelIdentities.isEmpty() ? NodeSecurityDataConstants.NETWORK_ELEMENT_OSS_MODEL_IDENTITY_VERSION
                : targetModelIdentities.get(0);
        final ManagedObject networkElement = createNetworkElementTree(liveBucket, parentMo, syncStatus, tmi);

        final ManagedObject managedElementMO = createManagedElement(liveBucket, parentMo);
        log.info("Test Setup ManagedElement, created: {}", managedElementMO.getFdn());

        managedElementMO.addAssociation("networkElementRef", networkElement);
        log.info("Test Setup ManagedElement Association , created: {} ", managedElementMO.getFdn());

        final ManagedObject systemFunctionsMO = createSystemFunctions(liveBucket, managedElementMO);
        log.info("Test Setup SystemFunctions, created: {} ", systemFunctionsMO.getFdn());

        final ManagedObject securityFunctionsMO = createSecurityFunctions(liveBucket, systemFunctionsMO);
        log.info("Test Setup SecurityFunctions, created: {} ", securityFunctionsMO.getFdn());

        final ManagedObject fmSuperVisionMO = createFmSupervisionChild(liveBucket, networkElement);
        log.info("Test Setup FMSuperVision, created: {}", fmSuperVisionMO.getFdn());

        final ManagedObject fmFunctionMO = createFmFunctionMoChild(liveBucket, networkElement);
        log.info("Test Setup FMFunction, created:  {}", fmFunctionMO.getFdn());

        dataUserTransaction.commit();

        log.info("Test Setup, Transaction Commited");

    }

    private ManagedObject createMeContext(final DataBucket liveBucket, final String nodeName) {

        final ManagedObject parentMo = liveBucket.getMibRootBuilder().namespace(NodeSecurityDataConstants.TOP_NAMESPACE).type("MeContext")
                .addAttribute("neType", NodeSecurityDataConstants.ERBS_TARGET_TYPE).addAttribute("MeContextId", "1")
                .version(NodeSecurityDataConstants.TOP_NAMESPACE_VERSION).name(nodeName).create();

        log.info("Test Setup, created:  {}", parentMo.getFdn());
        log.info("Created MeContext.....  name {}, managedObject {}, fdn {}", nodeName, parentMo, parentMo.getFdn());
        return parentMo;
    }

    private ManagedObject createSecurityFunctions(final DataBucket liveBucket, final ManagedObject parentMo) {

        log.info("Ready to build a new {} with attribute {} - {}", NodeSecurityDataConstants.SECURITY, NodeSecurityDataConstants.CERT_ENROLL_STATE,
                NodeModelDefs.CertEnrollStateValue.ERROR);

        final Map<String, Object> userDefProfilesInfoAttributesMap = createMapAndInsertValues(NodeSecurityDataConstants.LATEST_STATE_CHANGE_ATTRIBUTE,
                NodeSecurityDataConstants.LATEST_STATE_CHANGE_ATTRIBUTE_VALUE, NodeSecurityDataConstants.DESCRIPTION_ATTRIBUTE,
                NodeSecurityDataConstants.DESCRIPTION_ATTRIBUTE_VALUE, NodeSecurityDataConstants.STATE_ATTRIBUTE,
                NodeSecurityDataConstants.STATE_ATTRIBUTE_VALUE);

        final Map<String, Object> securityAttributes = createMapAndInsertValues(NodeSecurityDataConstants.MANDATORY_SECURITY_ATTRIBUTE,
                NodeSecurityDataConstants.MANDATORY_SECURITY_ATTRIBUTE_VALUE, NodeSecurityDataConstants.OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE,
                SecurityLevel.LEVEL_1.getLevel(), NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE,
                NodeSecurityDataConstants.USER_LABEL_SECURITY_ATTRIBUTE_VALUE, NodeSecurityDataConstants.FILE_TRANSFER_CLIENT_MODE,
                NodeSecurityDataConstants.SECURE_FILE_TRANSFER_CLIENT_MODE_VALUE, NodeSecurityDataConstants.CERT_ENROLL_STATE, "ERROR");

        securityAttributes.put(NodeModelDefs.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE, Boolean.FALSE);
        securityAttributes.put(NodeSecurityDataConstants.ACTIVE_USER_PROFILE_COMPLEX_ATTRIBUTE, userDefProfilesInfoAttributesMap);
        securityAttributes.put(NodeSecurityDataConstants.LOCAL_AA_DATABASE_INSTALLATION_FAILURE_ATTRIBUTE, false);
        securityAttributes.put(NodeSecurityDataConstants.LOCAL_AUTHENTICATION_FILE_VERSION_ATTRIBUTE, "1");
        securityAttributes.put(NodeSecurityDataConstants.LOCAL_AUTHERIZATION_FILE_VERSION_ATTRIBUTE, "1");
        securityAttributes.put(NodeSecurityDataConstants.INSTALLED_TRUSTED_CERTIFICATES, prepareCertSpecs());
        return createManagedObject(liveBucket, parentMo, securityAttributes, NodeSecurityDataConstants.SECURITY);
    }

    private ManagedObject createManagedObject(final DataBucket liveBucket, final ManagedObject parentMo,
                                              final Map<String, Object> managedObjectAttributes, final String type) {

        return liveBucket.getMibRootBuilder().namespace(NodeSecurityDataConstants.ERBS_NAMESPACE).type(type)
                .version(NodeSecurityDataConstants.ERBS_NAMESPACE_VERSION).name(NodeSecurityDataConstants.MO_NAME)
                .addAttributes(managedObjectAttributes).parent(parentMo).create();
    }

    private ManagedObject createManagedElement(final DataBucket liveBucket, final ManagedObject parentMo) {

        final Map<String, Object> mandatoryManagedElementAttributes = createMapAndInsertValues(
                NodeSecurityDataConstants.MANDATORY_MANAGED_ELEMENT_ATTRIBUTE, NodeSecurityDataConstants.MANDATORY_MANAGED_ELEMENT_ATTRIBUTE_VALUE);

        mandatoryManagedElementAttributes.putAll(
                createMapAndInsertValues(NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE_VALUE));

        log.info("mandatoryManagedElementAttributes {}", mandatoryManagedElementAttributes);
        return createManagedObject(liveBucket, parentMo, mandatoryManagedElementAttributes, NodeSecurityDataConstants.MANAGED_ELEMENT);
    }

    private ManagedObject createNetworkElementTree(final DataBucket liveBucket, final ManagedObject siblingMo, final String syncStatus,
                                                   final String ossModelId) {
        final ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_NS).type("NetworkElement")
                .addAttribute(NodeModelDefs.NETWORK_ELEMENT_ID, "1").addAttribute(NodeModelDefs.NE_TYPE, "ERBS")
                .addAttribute(NodeModelDefs.PLATFORM_TYPE, "CPP").addAttribute(NodeModelDefs.OSS_MODEL_IDENTITY, ossModelId).name(siblingMo.getName())
                .version(NodeSecurityDataConstants.NETWORK_ELEMENT_VERSION).create();
        networkElement.addAssociation("nodeRootRef", siblingMo);

        final ManagedObject securityFunctionMo = liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_SEC_NS).type("SecurityFunction")
                .addAttribute(NodeModelDefs.SECURITY_FUNCTION_ID, "1").name("1").version("1.0.0").parent(networkElement).create();
        log.info("Created SecurityFunctions.....   managedObject {}", securityFunctionMo);
        final ManagedObject nwsMO = createNetworkElementSecurity(liveBucket, securityFunctionMo);
        log.info("Created NetworkElementSecutiy MO {}", nwsMO);
        liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_CM_NS).type(Model.NETWORK_ELEMENT.cmFunction.type())
                .addAttribute(NodeModelDefs.SYNC_STATUS, syncStatus).name("1").version("1.0.0").parent(networkElement).create();
        log.info("Created CmFunction.....   managedObject {}", networkElement);

        liveBucket.getMibRootBuilder().namespace(NodeModelDefs.CPP_MED_NS).type(Model.NETWORK_ELEMENT.cppConnectivityInformation.type())
                .addAttribute(NodeModelDefs.IPADDRESS, "192.168.33.27").name("1").version("1.0.0").parent(networkElement).create();
        log.info("Created cppConnectivityInformation.....   managedObject {}", networkElement);

        return networkElement;
    }

    private ManagedObject createNetworkElementSecurity(final DataBucket liveBucket, final ManagedObject parenMO) {
        final String encryptedPassword = passwordHelper.encryptEncode("netsim");
        final Map<String, Object> mandatoryAttr = createMapAndInsertValues(NodeSecurityDataConstants.NETWORKELEMENT_SECURITY_SECURE_USER_NAME,
                "netsim", NodeSecurityDataConstants.NETWORKELEMENT_SECURITY_SECURE_PASSPHRASE, encryptedPassword);

        return liveBucket.getMibRootBuilder().namespace("OSS_NE_SEC_DEF").type(NodeSecurityDataConstants.NETWORKELEMENT_SECURITY).version("4.1.1")
                .name(NodeSecurityDataConstants.MO_NAME).addAttributes(mandatoryAttr).parent(parenMO).create();
    }

    private ManagedObject createFmSupervisionChild(final DataBucket liveBucket, final ManagedObject parentMO) {
        log.info("Creating FM supervision MO");
        final Map<String, Object> mandatoryAttr = new HashMap<>();
        mandatoryAttr.put(NodeSecurityDataConstants.FM_SUPERVISION_MO_ATTR_ACTIVE, true);
        mandatoryAttr.put(NodeSecurityDataConstants.FM_SUPERVISION_MO_ATTR_AUTOMATIC_SYNC, true);
        mandatoryAttr.put(NodeSecurityDataConstants.FM_SUPERVISION_MO_ATTR_ID, "1");
        mandatoryAttr.put(NodeSecurityDataConstants.FM_SUPERVISION_MO_ATTR_HEARTBEAT_INTERVAL, 300);
        mandatoryAttr.put(NodeSecurityDataConstants.FM_SUPERVISION_MO_ATTR_HEARTBEAT_TIMEOUT, 101);
        return liveBucket.getMibRootBuilder().parent(parentMO).type(NodeSecurityDataConstants.FM_ALARM_SUPERVISION_MO).namespace("OSS_NE_FM_DEF")
                .name("1").addAttributes(mandatoryAttr).version("1.1.0").create();
    }

    private ManagedObject createSystemFunctions(final DataBucket liveBucket, final ManagedObject parentMo) {

        final Map<String, Object> mandatorySystemFunctionsAttributes = createMapAndInsertValues(
                NodeSecurityDataConstants.MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE, NodeSecurityDataConstants.MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE_VALUE);

        return createManagedObject(liveBucket, parentMo, mandatorySystemFunctionsAttributes, NodeSecurityDataConstants.SYSTEM_FUNCTIONS);
    }

    private ManagedObject createFmFunctionMoChild(final DataBucket liveBucket, final ManagedObject networkElement) {
        final Map<String, Object> mandatoryAttr = createMapAndInsertValues("currentServiceState", "IN_SERVICE");
        return liveBucket.getMibRootBuilder().parent(networkElement).type(Model.NETWORK_ELEMENT.fmFunction.type()).namespace("OSS_NE_FM_DEF")
                .name(NodeSecurityDataConstants.MO_NAME).addAttributes(mandatoryAttr).version("1.0.0").create();
    }

    private List<Map<String, Object>> prepareCertSpecs() {
        final List<Map<String, Object>> attributesListMap = new ArrayList<>();
        final Map<String, Object> singleAttMap = new HashMap<>();
        singleAttMap.put(NodeSecurityDataConstants.NODE_CERT_INFO_SERIAL_NUMBER_ATTRIBUTE,
                NodeSecurityDataConstants.NODE_CERT_INFO_SERIAL_NUMBER_VALUE);
        singleAttMap.put(NodeSecurityDataConstants.NODE_CERT_INFO_SUBJECT_ATTRIBUTE, NodeSecurityDataConstants.NODE_CERT_INFO_SUBJECT_VALUE);
        singleAttMap.put(NodeSecurityDataConstants.NODE_CERT_INFO_ISSUER_ATTRIBUTE, NodeSecurityDataConstants.NODE_CERT_INFO_ISSUER_VALUE);
        singleAttMap.put(NodeSecurityDataConstants.NODE_CERT_INFO_CATEGORY_ATTRIBUTE, NodeSecurityDataConstants.NODE_CERT_INFO_CATEGORY_VALUE);
        attributesListMap.add(singleAttMap);
        return attributesListMap;
    }

    private DataBucket getLiveBucket() {
        return eserviceProducer.getDataPersistenceService().getDataBucket("Live", BucketProperties.SUPPRESS_MEDIATION,
                BucketProperties.SUPPRESS_CONSTRAINTS);
    }

    private Map<String, Object> createMapAndInsertValues(final String... keyValues) {
        final Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put(keyValues[i], keyValues[i + 1]);
        }

        return map;
    }

    public void deleteAllNodes() throws NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        deleteAllNodes(NodeSecurityDataConstants.TOP_NAMESPACE, "MeContext");
        deleteAllNodes(NodeModelDefs.NE_NS, Model.NETWORK_ELEMENT.type());
        deleteAllNodes(NodeModelDefs.CPP_MED_NS, Model.NETWORK_ELEMENT.cppConnectivityInformation.type());
    }

    public void deleteAllNodes(final String namespace, final String type) throws NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        dataUserTransaction.begin();
        log.info("deletingAllNodes transaction begun");
        final QueryBuilder queryBuilder = eserviceProducer.getDataPersistenceService().getQueryBuilder();
        final Query query = queryBuilder.createTypeQuery(namespace, type);

        final Iterator<PersistenceObject> iterator = getLiveBucket().getQueryExecutor().execute(query);
        while (iterator.hasNext()) {
            final PersistenceObject po = iterator.next();
            eserviceProducer.getDataPersistenceService().getLiveBucket().deletePo(po);
        }
        log.info("deletingAllNodes transaction committing");
        dataUserTransaction.commit();
    }

    /**
     * Method to create a test node of type ERBS with all mandatory MOs for specific ossModelIdentityVersion
     *
     * @param nodeName
     *            Node name
     * @param syncStatus
     *            Sync Status
     * @param ossModelIdentityVersion
     *            OSS Model Identity Version
     * @throws SystemException
     * @throws NotSupportedException
     * @throws HeuristicRollbackException
     * @throws HeuristicMixedException
     * @throws RollbackException
     * @throws Exception
     *             is thrown when any error occurs while creation of network element
     */
    public void createNode(final String nodeName, final String syncStatus, final String ossModelIdentityVersion) throws NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        log.info("Creating a test node with name {} for specified ossModelIdentity {}", nodeName, ossModelIdentityVersion);

        dataUserTransaction.begin();
        final DataBucket liveBucket = getLiveBucket();
        log.info("liveBucket {}", liveBucket);

        final ManagedObject parentMo = createMeContext(liveBucket, nodeName);
        log.info("Created MeContext.....  name {}, managedObject {}, fdn {}", nodeName, parentMo, parentMo.getFdn());
        log.info("The provided OSS Model Identity Version: {} for Node: {} ", ossModelIdentityVersion, nodeName);

        final List<String> targetModelIdentities = eserviceProducer.getNscsModelService()
                .getTargetModelIdentities(TargetTypeInformation.CATEGORY_NODE, NodeSecurityDataConstants.ERBS_TARGET_TYPE);

        log.info("All available OSS Model Identity Versions provided by Capability model: {} for Node: {} ", targetModelIdentities, nodeName);

        final ManagedObject networkElement = createNetworkElementTree(liveBucket, parentMo, syncStatus, ossModelIdentityVersion);

        final ManagedObject managedElementMO = createManagedElement(liveBucket, parentMo);
        log.info("Test Setup ManagedElement, created: {}", managedElementMO.getFdn());

        managedElementMO.addAssociation("networkElementRef", networkElement);
        log.info("Test Setup ManagedElement Association , created: {} ", managedElementMO.getFdn());

        final ManagedObject systemFunctionsMO = createSystemFunctions(liveBucket, managedElementMO);
        log.info("Test Setup SystemFunctions, created: {} ", systemFunctionsMO.getFdn());

        final ManagedObject timeSettingMO = createTimeSettingMO(liveBucket, systemFunctionsMO);
        log.info("Test Setup TimeSetting, created: {} ", timeSettingMO.getFdn());

        final ManagedObject fmSuperVisionMO = createFmSupervisionChild(liveBucket, networkElement);
        log.info("Test Setup FMSuperVision, created: {}", fmSuperVisionMO.getFdn());

        final ManagedObject fmFunctionMO = createFmFunctionMoChild(liveBucket, networkElement);
        log.info("Test Setup FMFunction, created:  {}", fmFunctionMO.getFdn());

        dataUserTransaction.commit();

        log.info("Test Setup, Transaction Commited");

    }

    private ManagedObject createTimeSettingMO(final DataBucket liveBucket, final ManagedObject parentMo) {

        log.info("Ready to build a new {} with attributes: {} - {}", NodeSecurityDataConstants.TIME_SETTING_MO,
                NodeSecurityDataConstants.TIME_SETTING_ID_ATTRIBUTE, NodeSecurityDataConstants.INSTALLED_NTP_KEY_IDS_ATTRIBUTE);

        final Map<String, Object> timeSettingAttributes = createMapAndInsertValues(NodeSecurityDataConstants.TIME_SETTING_ID_ATTRIBUTE,
                NodeSecurityDataConstants.TIME_SETTING_ID_ATTRIBUTE_VALUE);
        timeSettingAttributes.put(NodeSecurityDataConstants.INSTALLED_NTP_KEY_IDS_ATTRIBUTE,
                NodeSecurityDataConstants.INSTALLED_NTP_KEY_IDS_ATTRIBUTE_VALUE);
        log.info("installed: {} ", NodeSecurityDataConstants.INSTALLED_NTP_KEY_IDS_ATTRIBUTE);

        final ManagedObject timeSettingMO = createManagedObject(liveBucket, parentMo, timeSettingAttributes,
                NodeSecurityDataConstants.TIME_SETTING_MO);

        final ManagedObject createNtpServerMO = createNtpServer(liveBucket, timeSettingMO);
        log.info("Test Setup NtpServer, created: {}", createNtpServerMO.getFdn());

        return timeSettingMO;
    }

    private ManagedObject createNtpServer(final DataBucket liveBucket, final ManagedObject parentMo) {
        log.info("Ready to build new {} with attributes {}", NodeSecurityDataConstants.NTP_SERVER_MO, NodeSecurityDataConstants.NTP_KEY_ID);
        final Map<String, Object> mandatoryNtpsServerAttributes = createMapAndInsertValues(NodeSecurityDataConstants.NTP_SERVER_ID,
                NodeSecurityDataConstants.NTP_SERVER_ID_VALUE, NodeSecurityDataConstants.SERVER_ADDRESS,
                NodeSecurityDataConstants.SERVER_ADDRESS_VALUE, NodeSecurityDataConstants.SERVICE_STATUS,
                NodeSecurityDataConstants.SERVICE_STATUS_VALUE, NodeSecurityDataConstants.USER_LABEL, NodeSecurityDataConstants.USER_LABEL_VALUE);
        mandatoryNtpsServerAttributes.put(NodeSecurityDataConstants.NTP_KEY_ID, NodeSecurityDataConstants.NTP_KEY_ID_VALUE);
        mandatoryNtpsServerAttributes.put(NodeSecurityDataConstants.SERVICE_ACTIVE, true);

        return createManagedObject(liveBucket, parentMo, mandatoryNtpsServerAttributes, NodeSecurityDataConstants.NTP_SERVER_MO);

    }

}