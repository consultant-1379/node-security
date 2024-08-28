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
package com.ericsson.nms.security.nscs.cucumber.helper;

import java.util.*;

import javax.inject.Inject;
import javax.transaction.*;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.oss.itpf.datalayer.dps.BucketProperties;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;

public class NodeSecurityDataSetup {

    public static final String NETWORK_ELEMENT_NAME_SULFIX = "-NE";
    public static final String COLLECTION_PO_TYPE = "Collection";
    public static final String COLLECTION_NAMESPACE = "OSS_TOP";
    public static final String COLLECTION_VERSION = "1.0.1";
    public static final String COLLECTION_TYPE = "Collection";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_USERID = "userId";
    public static final String ATTRIBUTE_CATEGORY = "category";
    public static final String ATTRIBUTE_TIME_CREATED = "timeCreated";
    public static final String NETWORKELEMENTPREFIX = "NetworkElement";
    private static final String ERBS_TARGET_TYPE = "ERBS";
    private static final String RADIO_TARGET_TYPE = "RadioNode";
    private static final String ipv6Address = "FEDC:BA98:7654:3210:FEDC:BA98:7654:3210";
    private static final String ipv4Address = "192.168.33.27";

    @Inject
    EServiceProducer eserviceHolder;

    @Inject
    public UserTransaction userTransaction;

    @Inject
    Logger log;

    protected void rollbackTransactionIfActive() throws Exception {
        if (isActive(userTransaction) || isMarkedForRollback(userTransaction)) {
            userTransaction.rollback();
        }
    }

    private boolean isMarkedForRollback(final UserTransaction utx) {
        try {
            final int status = utx.getStatus();
            return status == Status.STATUS_MARKED_ROLLBACK;
        } catch (final SystemException e) {
            return false;
        }
    }

    private boolean isActive(final UserTransaction utx) {
        try {
            final int status = utx.getStatus();
            return status == Status.STATUS_ACTIVE;
        } catch (final SystemException e) {
            return false;
        }
    }

    public static enum IpAddressVersion {
        IPv4, IPv6;
    }

    public void insertData() throws Exception {
        deleteAllNodes();
        Map<String, String> map1 = createNode(NodeSecurityDataConstants.NODE_NAME2);
        dumpMap(map1);
        Map<String, String> map2 = createNode(NodeSecurityDataConstants.NODE_NAME1);
        dumpMap(map2);
    }

    public void insertDataWithDG2() throws Exception {
        deleteAllNodes();
        Map<String, String> map1 = createNode(NodeSecurityDataConstants.NODE_NAME2);
        dumpMap(map1);
        Map<String, String> map2 = createNode(NodeSecurityDataConstants.NODE_NAME1);
        dumpMap(map2);
        Map<String, String> map3 = createNode(NodeSecurityDataConstants.NODE_NAME3);
        dumpMap(map3);
    }

    private void dumpMap(Map<String, String> map) {
        log.info("... dumping map ...");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            log.info(String.format("key: [%s], value: [%s]", entry.getKey(), entry.getValue()));
        }
    }

    // @After
    public void cleanUp() throws Exception {
        try {
            deleteAllNodes();
        } finally {
            // rollbackTransactionIfActive();
        }
    }

    public Map<String, String> createNode(final String nodeName, final String syncStatus,
            IpAddressVersion ipVersion) throws Exception {
        return createNode(nodeName, syncStatus, NodeSecurityDataConstants.OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE_VALUE, ipVersion);
    }

    public Map<String, String> createNode(final String nodeName, final String syncStatus,
            final SecurityLevel level, IpAddressVersion ipVersion) throws Exception {
        userTransaction.begin();
        final DataBucket liveBucket = getLiveBucket();

        // level 1 MeContext Mib in OSS_TOP model
        final ManagedObject parentMo = createMeContext(liveBucket, nodeName);

        final ManagedObject networkElment = createNetworkElementTree(liveBucket, parentMo,
                syncStatus, ipVersion);
        // level 2 create ManagedElement Mib under CPP model

        final ManagedObject managedElementMO = createManagedElement(liveBucket, parentMo);
        log.info("Test Setup ManagedElement, created: " + managedElementMO.getFdn());

		// level 3 create SYSTEM FUNCTIONS
        final ManagedObject systemFunctionsMO = createSystemFunctions(liveBucket, managedElementMO);
        log.info("Test Setup SystemFunctions, created: " + systemFunctionsMO.getFdn());

        // level 4 create Security FUNCTIONS
        final Map<String, Object> userDefProfilesInfoAttributesMap = createMapAndInsertValues(NodeSecurityDataConstants.LATEST_STATE_CHANGE_ATTRIBUTE,
                NodeSecurityDataConstants.LATEST_STATE_CHANGE_ATTRIBUTE_VALUE, NodeSecurityDataConstants.DESCRIPTION_ATTRIBUTE, NodeSecurityDataConstants.DESCRIPTION_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.STATE_ATTRIBUTE, NodeSecurityDataConstants.STATE_ATTRIBUTE_VALUE);

        final ManagedObject securityFunctionsMO = createSecurityFunctions(liveBucket, systemFunctionsMO, userDefProfilesInfoAttributesMap, level);
        log.info("Test Setup SecurityFunctions, created: " + securityFunctionsMO.getFdn());
        final Map<String, String> fdnMap = new HashMap<String, String>();
        fdnMap.put("ManagedElementMOFDN", managedElementMO.getFdn());
        fdnMap.put("SystemFunctionsMOFDN", systemFunctionsMO.getFdn());
        fdnMap.put("SecurityFunctionsMOFDN", securityFunctionsMO.getFdn());

        // create IpSystem
        final ManagedObject ipSystemMO = createIpSystem(liveBucket, managedElementMO);
        log.info("Test Setup IpSystem, created: " + ipSystemMO.getFdn());
        fdnMap.put("IpSystemMOFDN", ipSystemMO.getFdn());

        // create IpSec
        final ManagedObject ipSecMO = createIpSec(liveBucket, ipSystemMO);
        log.info("Test Setup IpSec, created: " + ipSecMO.getFdn());
        fdnMap.put("IpSecMOFDN", ipSystemMO.getFdn());

        userTransaction.commit();

        userTransaction.begin();

        final DataBucket otherLiveBucket = getLiveBucket();

        final ManagedObject securityMObject = otherLiveBucket.findMoByFdn(securityFunctionsMO.getFdn());

        final Map<String, Object> securityAttributes = new HashMap<String, Object>();

        securityAttributes.put(NodeSecurityDataConstants.MANDATORY_SECURITY_ATTRIBUTE, null);
        securityAttributes.put(NodeSecurityDataConstants.OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE, null);
        securityAttributes.put(NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, null);
        securityAttributes.put(NodeSecurityDataConstants.FILE_TRANSFER_CLIENT_MODE, null);
        securityAttributes.put(NodeSecurityDataConstants.CERT_ENROLL_STATE, null);
        securityAttributes.put(NodeModelDefs.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE, null);
        securityAttributes.put(NodeSecurityDataConstants.ACTIVE_USER_PROFILE_COMPLEX_ATTRIBUTE, null);

        for (Map.Entry<String, Object> entry : securityAttributes.entrySet()) {
            log.info("Reading securityMObject.....  securityMObject [{}], attribute [{}], value [{}]", securityMObject, entry.getKey(), securityMObject.getAttribute(entry.getKey()));
        }

        userTransaction.commit();

        log.info("Test Setup, Transaction Commited");
        return fdnMap;
    }

    public static String networkElementNameFromMeContextName(final String meContextName) {
        return meContextName + NETWORK_ELEMENT_NAME_SULFIX;
    }

    private ManagedObject createNetworkElementTree(final DataBucket liveBucket,final ManagedObject siblingMo,
            final String syncStatus, final IpAddressVersion ipVersion) {
        final String neName = networkElementNameFromMeContextName(siblingMo.getName());
        final List<String> targetModelIdentities = eserviceHolder.getNscsModelService()
                .getTargetModelIdentities(TargetTypeInformation.CATEGORY_NODE, ERBS_TARGET_TYPE);
        final String tmi = targetModelIdentities.isEmpty() ?
           NodeSecurityDataConstants.NETWORK_ELEMENT_OSS_MODEL_IDENTITY_VERSION : targetModelIdentities.get(0);
        final ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_NS)
                .type(Model.NETWORK_ELEMENT.type())
                .addAttribute(NodeModelDefs.NETWORK_ELEMENT_ID, "1")
                .addAttribute(NodeModelDefs.NE_TYPE, ERBS_TARGET_TYPE)
                .addAttribute(NodeModelDefs.PLATFORM_TYPE, "CPP")
                .addAttribute(NodeModelDefs.OSS_MODEL_IDENTITY, tmi)
                .name(neName)
                .version(NodeSecurityDataConstants.NETWORK_ELEMENT_VERSION)
                .create();
        log.info("Created NetworkElement.....  name {}, managedObject {}", neName, networkElement);

        networkElement.addAssociation("nodeRootRef", siblingMo);
        log.info("Created association {} <--> {}", siblingMo.getName(), neName);

        liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_SEC_NS)
                .type(Model.NETWORK_ELEMENT.securityFunction.type())
                .addAttribute(NodeModelDefs.SECURITY_FUNCTION_ID, "1")
                .name("1")
                .version("1.0.0")
                .parent(networkElement)
                .create();
        log.info("Created SecurityFunctions.....   managedObject {}", networkElement);

        liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_CM_NS)
                .type(Model.NETWORK_ELEMENT.cmFunction.type())
                .addAttribute(NodeModelDefs.SYNC_STATUS, syncStatus)
                .name("1")
                .version("1.0.0")
                .parent(networkElement)
                .create();
        log.info("Created CmFunction.....   managedObject {}", networkElement);

        final String ipAddress = (ipVersion == IpAddressVersion.IPv6) ? ipv6Address : ipv4Address;
        liveBucket.getMibRootBuilder().namespace(NodeModelDefs.CPP_MED_NS)
                .type(Model.NETWORK_ELEMENT.cppConnectivityInformation.type())
                .addAttribute(NodeModelDefs.IPADDRESS, ipAddress)
                .name("1")
                .version("1.0.0")
                .parent(networkElement)
                .create();
        log.info("Created cppConnectivityInformation.....   managedObject {}", networkElement);

        return networkElement;
    }

    public void createNetworkElementSecurity(String parentNetworkElementName) throws Exception {
        userTransaction.begin();

        final DataBucket liveBucket = getLiveBucket();

        final ManagedObject parent = liveBucket.findMoByFdn(Model.NETWORK_ELEMENT.securityFunction.withNames(parentNetworkElementName).fdn());

        ManagedObject networkElementSecurity = liveBucket.getMibRootBuilder()
                .namespace("OSS_NE_SEC_DEF")
                .type(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type())
                .addAttribute(NodeModelDefs.ROOT_USER_NAME, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_ROOT_USER_NAME)
                .addAttribute(NodeModelDefs.ROOT_USER_PASSWORD, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_ROOT_USER_PASSWORD)
                .addAttribute(NodeModelDefs.SECURE_USER_PASSWORD, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_SECURE_USER_PASSWORD)
                .addAttribute(NodeModelDefs.SECURE_USER_NAME, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_SECURE_USER_NAME)
                .addAttribute(NodeModelDefs.NORMAL_USER_PASSWORD, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_NORMAL_USER_PASSWORD)
                .addAttribute(NodeModelDefs.NORMAL_USER_NAME, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_NORMAL_USER_NAME)
                .addAttribute(NodeModelDefs.NETWORK_ELEMENT_SECURITY_ID, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_ID)
                .addAttribute(NodeModelDefs.TARGET_GROUPS, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_TARGET_GROUPS)
                .addAttribute(NodeModelDefs.ALGORITHM_AND_KEY_SIZE, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_ALGORITHM_KEY_SIZE)
                .addAttribute(NodeModelDefs.ENM_SSH_PUBLIC_KEY, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_PUBLIC_KEY)
                .addAttribute(NodeModelDefs.ENM_SSH_PRIVATE_KEY, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_PRIVATE_KEY)
                .addAttribute(NodeModelDefs.ENROLLMENT_MODE, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_ENROLLMENT_MODE)
//                added
                .addAttribute(NodeModelDefs.AUTH_KEY, NodeSecurityDataConstants.AUTH_PASSWD)
                .addAttribute(NodeModelDefs.PRIV_KEY, NodeSecurityDataConstants.PRIV_PASSWD)
                .addAttribute(NodeModelDefs.AUTH_PROTOCOL, NodeSecurityDataConstants.AUTH_PROTOCOL)
                .addAttribute(NodeModelDefs.PRIV_PROTOCOL, NodeSecurityDataConstants.PRIV_PROTOCOL)
                .name(NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_NAME)
                .version(NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_VERSION)
                .parent(parent)
                .create();

        userTransaction.commit();
        log.info("Created NetworkElementSecurity.....   managedObject {}", networkElementSecurity);
    }

    public void changeAttributesForSecurityMO(final String securityMOFdn, final Map<String, Object> userDefProfilesInfoAttributesMap) throws Exception {
        userTransaction.begin();
        final DataBucket liveBucket = getLiveBucket();

        final ManagedObject managedObject = liveBucket.findMoByFdn(securityMOFdn);
        log.info("ChangeAttributesForSecurityMO.....  securityMOFdn {}, managedObject {}", securityMOFdn, managedObject);

        managedObject.setAttributes(userDefProfilesInfoAttributesMap);
        userTransaction.commit();

    }

    public ManagedObject createMeContext(final DataBucket liveBucket, final String nodeName) {

        final ManagedObject parentMo = liveBucket.getMibRootBuilder().namespace(NodeSecurityDataConstants.TOP_NAMESPACE).type("MeContext").addAttribute("neType", ERBS_TARGET_TYPE)
                .addAttribute("MeContextId", "1")
                .version(NodeSecurityDataConstants.TOP_NAMESPACE_VERSION).name(nodeName).create();

        log.info("Test Setup, created: " + parentMo.getFdn());
        log.info("Created MeContext.....  name {}, managedObject {}, fdn {}", nodeName, parentMo, parentMo.getFdn());
        return parentMo;
    }
//
    public ManagedObject createManagedElement(final DataBucket liveBucket, final ManagedObject parentMo) {

        final Map<String, Object> mandatoryManagedElementAttributes = createMapAndInsertValues(NodeSecurityDataConstants.MANDATORY_MANAGED_ELEMENT_ATTRIBUTE,
                NodeSecurityDataConstants.MANDATORY_MANAGED_ELEMENT_ATTRIBUTE_VALUE);

        mandatoryManagedElementAttributes.putAll(createMapAndInsertValues(NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE_VALUE));

        log.info("mandatoryManagedElementAttributes {}", mandatoryManagedElementAttributes);
        final ManagedObject managedElementMo = createManagedObject(liveBucket, parentMo, mandatoryManagedElementAttributes, NodeSecurityDataConstants.MANAGED_ELEMENT);
        return managedElementMo;
    }

    public ManagedObject createIpSystem(final DataBucket liveBucket, final ManagedObject parentMo) {
        final Map<String, Object> ipSystemAttributes = createMapAndInsertValues(NodeSecurityDataConstants.MANDATORY_IP_SYSTEM_ATTRIBUTE,
                NodeSecurityDataConstants.MANDATORY_IP_SYSTEM_ATTRIBUTE_VALUE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE_VALUE);
        return createManagedObject(liveBucket, parentMo, ipSystemAttributes, NodeSecurityDataConstants.IP_SYSTEM);
    }

    public ManagedObject createIpSec(final DataBucket liveBucket, final ManagedObject parentMo) {
        final Map<String, Object> mandatoryIpSecAttributes = createMapAndInsertValues(
                NodeSecurityDataConstants.MANDATORY_IP_SEC_ATTRIBUTE, NodeSecurityDataConstants.MANDATORY_IP_SEC_ATTRIBUTE_VALUE,
                NodeModelDefs.FEATURE_STATE, NodeModelDefs.ActivationVals.ACTIVATED.toString(),
                NodeModelDefs.LICENSE_STATE, NodeModelDefs.StateVals.ENABLED.toString(),
                NodeModelDefs.TRUSTED_CERT_INST_STATE, NodeModelDefs.IpSecTrustedCertInstallStateValue.ERROR.toString(),
                NodeModelDefs.CERT_ENROLL_STATE, NodeModelDefs.IpSecCertEnrollStateValue.ERROR.toString());

        // complex attribute
        final Map<String, Object> ipSecCertInfo = createMapAndInsertValues(NodeSecurityDataConstants.IP_SEC_CERTIFICATE_SERIALNUMBER_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.IP_SEC_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE);
        mandatoryIpSecAttributes.put(NodeModelDefs.CERTIFICATE, ipSecCertInfo);

        final Map<String, Object> trustCert1 = createMapAndInsertValues(
                NodeSecurityDataConstants.IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_0_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE);

        final Map<String, Object> trustCert2 = createMapAndInsertValues(
                NodeSecurityDataConstants.IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_ISSUER_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECT_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_NOTVALIDAFTER_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_1_INSTALLED_TRUSTED_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE);

        final List<Map<String, Object>> trustCerts = new ArrayList<>();
        trustCerts.add(trustCert1);
        trustCerts.add(trustCert2);
        mandatoryIpSecAttributes.put(NodeModelDefs.INSTALLED_TRUSTED_CERTIFICATES, trustCerts);

        return createManagedObject(liveBucket, parentMo, mandatoryIpSecAttributes, NodeSecurityDataConstants.IP_SEC);
    }

    public ManagedObject createSystemFunctions(final DataBucket liveBucket, final ManagedObject parentMo) {

        final Map<String, Object> mandatorySystemFunctionsAttributes = createMapAndInsertValues(NodeSecurityDataConstants.MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE,
                NodeSecurityDataConstants.MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE_VALUE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE_VALUE);

        final ManagedObject systemFunctionsMo = createManagedObject(liveBucket, parentMo, mandatorySystemFunctionsAttributes, NodeSecurityDataConstants.SYSTEM_FUNCTIONS);

        return systemFunctionsMo;
    }

    public ManagedObject createSecurityFunctions(final DataBucket liveBucket, final ManagedObject parentMo, final Map<String, Object> userDefProfilesInfoAttributesMap, final SecurityLevel level) {

        log.info("Ready to build a new [{}] with attribute [{}] = [{}]", NodeSecurityDataConstants.SECURITY, NodeSecurityDataConstants.CERT_ENROLL_STATE, NodeModelDefs.CertEnrollStateValue.ERROR.toString());

        final Map<String, Object> securityAttributes = createMapAndInsertValues(
                NodeSecurityDataConstants.MANDATORY_SECURITY_ATTRIBUTE, NodeSecurityDataConstants.MANDATORY_SECURITY_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE, level.getLevel(),
                NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_SECURITY_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.FILE_TRANSFER_CLIENT_MODE, NodeSecurityDataConstants.FILE_TRANSFER_CLIENT_MODE_VALUE,
                NodeSecurityDataConstants.CERT_ENROLL_STATE, "ERROR" /*NodeModelDefs.Security.CertEnrollStateValue.ERROR.toString()*/);

        securityAttributes.put(NodeModelDefs.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE, Boolean.TRUE);
        securityAttributes.put(NodeSecurityDataConstants.ACTIVE_USER_PROFILE_COMPLEX_ATTRIBUTE, userDefProfilesInfoAttributesMap);

        final ManagedObject SecurityMo = createManagedObject(liveBucket, parentMo, securityAttributes, NodeSecurityDataConstants.SECURITY);

        return SecurityMo;
    }

    private ManagedObject createManagedObject(final DataBucket liveBucket, final ManagedObject parentMo, final Map<String, Object> managedObjectAttributes, final String type) {

        final ManagedObject SecurityMo = liveBucket.getMibRootBuilder().namespace(NodeSecurityDataConstants.ERBS_NAMESPACE).type(type).version(NodeSecurityDataConstants.ERBS_NAMESPACE_VERSION)
                .name(NodeSecurityDataConstants.MO_NAME).addAttributes(managedObjectAttributes).parent(parentMo).create();
        return SecurityMo;
    }

    public Map<String, Object> createMapAndInsertValues(final String... keyValues) {
        final Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < keyValues.length; i += 2) {
            log.info("key [{}] value [{}]", keyValues[i], keyValues[i + 1]);
            map.put(keyValues[i], keyValues[i + 1]);
        }

        return map;
    }

    public void deleteAllNodes() throws Exception {
        deleteAllNodes(NodeSecurityDataConstants.TOP_NAMESPACE, "MeContext");
        deleteAllNodes(NodeModelDefs.NE_NS, Model.NETWORK_ELEMENT.type());
        deleteAllNodes(NodeModelDefs.CPP_MED_NS,
                Model.NETWORK_ELEMENT.cppConnectivityInformation.type());
    }

    public void deleteAllNodes(final String namespace, final String type) throws Exception {
        userTransaction.begin();
        log.info("deletingAllNodes transaction begun");
        final QueryBuilder queryBuilder = eserviceHolder.getDataPersistenceService().getQueryBuilder();
        final Query query = queryBuilder.createTypeQuery(namespace, type);

        final Iterator<PersistenceObject> iterator = getLiveBucket().getQueryExecutor().execute(query);
        while (iterator.hasNext()) {
            final PersistenceObject po = iterator.next();
            eserviceHolder.getDataPersistenceService().getLiveBucket().deletePo(po);
        }
        log.info("deletingAllNodes transaction committing");
        userTransaction.commit();
    }

    private DataBucket getLiveBucket() {
        return getDataPersistenceService().getDataBucket("Live", BucketProperties.SUPPRESS_MEDIATION, BucketProperties.SUPPRESS_CONSTRAINTS);
    }

    private com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService getDataPersistenceService() {
        return eserviceHolder.getDataPersistenceService();
    }

    public Map<String, String> createNode(final String nodeName) throws Exception {
        return createNode(nodeName, NodeModelDefs.SyncStatusValue.SYNCHRONIZED.name(),
                IpAddressVersion.IPv4);
    }

	//@Inject
    //DigestAlgorithm digestAlgorithm;
    public Map<String, String> createComEcimNode(final String nodeName, final String syncStatus, final String enrollmentSupport, final String neType) throws Exception {
        return createNode(nodeName, syncStatus, enrollmentSupport, neType);
    }

    public Map<String, String> createNode(final String nodeName, final String syncStatus, final String enrollmentSupport, final String neType) throws Exception {
        userTransaction.begin();
        final DataBucket liveBucket = getLiveBucket();

        // level 1 MeContext Mib in OSS_TOP model
        final ManagedObject parentMo = createComEcimMeContext(liveBucket, nodeName, neType);

        // level 2 create ManagedElement Mib under CPP model
        createComEcimNetworkElementTree(liveBucket, parentMo, neType);

        final Map<String, String> fdnMap = new HashMap<String, String>();

        userTransaction.commit();

        log.info("Test Setup, Transaction Commited");

        return fdnMap;
    }

    public ManagedObject createComEcimMeContext(final DataBucket liveBucket, final String nodeName, final String neType) {

        final ManagedObject parentMo = liveBucket.getMibRootBuilder().namespace(NodeSecurityDataConstants.TOP_NAMESPACE).type("MeContext").addAttribute("neType", neType)
                .addAttribute("MeContextId", nodeName).version(NodeSecurityDataConstants.TOP_NAMESPACE_VERSION).name(nodeName).create();

        log.info("Test Setup, created: " + parentMo.getFdn());
        log.info("Created MeContext.....  name {}, managedObject {}, fdn {}", nodeName, parentMo, parentMo.getFdn());
        return parentMo;
    }

    private ManagedObject createComEcimNetworkElementTree(final DataBucket liveBucket, 
                                    final ManagedObject siblingMo, final String neType) {

        final String neName = networkElementNameFromMeContextName(siblingMo.getName());

        final List<String> targetModelIdentities = eserviceHolder.getNscsModelService()
                .getTargetModelIdentities(TargetTypeInformation.CATEGORY_NODE, RADIO_TARGET_TYPE);
        final String tmi = targetModelIdentities.isEmpty() ?
           NodeSecurityDataConstants.NETWORK_ELEMENT_OSS_MODEL_IDENTITY_VERSION : targetModelIdentities.get(0);
        final ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace(NodeModelDefs.NE_NS)
                .type(Model.NETWORK_ELEMENT.type())
                .addAttribute(NodeModelDefs.NETWORK_ELEMENT_ID, "1")
                .addAttribute(NodeModelDefs.NE_TYPE, neType)
                .addAttribute(NodeModelDefs.OSS_MODEL_IDENTITY, tmi)
                .name(neName)
                .version(NodeSecurityDataConstants.NETWORK_ELEMENT_VERSION)
                .create();
        log.info("Created NetworkElement.....  name {}, managedObject {}", neName, networkElement);

        networkElement.addAssociation("nodeRootRef", siblingMo);
        log.info("Created association {} <--> {}", siblingMo.getName(), neName);

        return networkElement;
    }

    public ManagedObject createSecM(final DataBucket liveBucket, final ManagedObject parentMo) {

        final Map<String, Object> mandatorySecMAttributes = createMapAndInsertValues(NodeSecurityDataConstants.MANDATORY_SEC_M_ATTRIBUTE,
                NodeSecurityDataConstants.MANDATORY_SEC_M_ATTRIBUTE_VALUE);

        final ManagedObject certMCapabilitiesMo = createManagedObject(liveBucket, parentMo, mandatorySecMAttributes, NodeSecurityDataConstants.SEC_M);

        return certMCapabilitiesMo;
    }

    public ManagedObject createCertM(final DataBucket liveBucket, final ManagedObject parentMo) {

        final Map<String, Object> mandatoryCertMAttributes = createMapAndInsertValues(NodeSecurityDataConstants.MANDATORY_CERT_M_ATTRIBUTE,
                NodeSecurityDataConstants.MANDATORY_CERT_M_ATTRIBUTE_VALUE);

        final ManagedObject certMCapabilitiesMo = createManagedObject(liveBucket, parentMo, mandatoryCertMAttributes, NodeSecurityDataConstants.CERT_M);

        return certMCapabilitiesMo;
    }

    public Long getNodePoid(String fdn) throws Exception {
        userTransaction.begin();
        final DataBucket liveBucket = getLiveBucket();

        ManagedObject mo = liveBucket.findMoByFdn(fdn);

        userTransaction.commit();

        return mo.getPoId();

    }

    private List<String> createMoList(String... poIds) {
        final List<String> moList = new ArrayList<>();
        if (poIds.length > 0) {
            for (String id : poIds) {
                moList.add(id);
            }
        }
        return moList;
    }

    public PersistenceObject createStaticCollection() throws Exception {
        log.info("------------------------Started executing createStaticCollection-------------------------- ");
        final PersistenceObject po = createCollection("CreateTopologyCollectionTestCase1-Administrator", "Administrator", "Public");
        log.info("------------------------Finished executing createStaticCollection-------------------------- ");
        return po;
    }

    /**
     * @param string
     * @param string2
     * @param string3
     * @return
     */
    public PersistenceObject createCollection(final String name, final String userId, final String category) throws Exception {
        Map<String, Object> validatedAttributes = createValidatedAttributes(name, userId, category);
        userTransaction.begin();
        final DataBucket liveBucket = getLiveBucket();

        final PersistenceObject po = liveBucket.getPersistenceObjectBuilder().namespace(COLLECTION_NAMESPACE)
                .type(COLLECTION_TYPE).addAttributes(validatedAttributes)
                .version(COLLECTION_VERSION).create();

        userTransaction.commit();

        return po;
    }

    public Map<String, Object> createValidatedAttributes(final String name, final String user, final String category) {
        final Map<String, Object> validatedAttributes = new HashMap();
        validatedAttributes.put(ATTRIBUTE_NAME, name);
        validatedAttributes.put(ATTRIBUTE_USERID, user);
        validatedAttributes.put(ATTRIBUTE_CATEGORY, category);
        validatedAttributes.put(ATTRIBUTE_TIME_CREATED, generateCurrentUtcTime());

        return validatedAttributes;
    }

    public String generateCurrentUtcTime() {
        final Long currentTime = System.currentTimeMillis();
        return currentTime.toString();
    }


}
