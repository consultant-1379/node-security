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
package com.ericsson.nms.security.nscs.integration.jee.test.utils.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;
import com.ericsson.nms.security.nscs.data.ModelDefinition.SecurityFunction;
import com.ericsson.nms.security.nscs.integration.jee.test.producer.EServiceProducer;
import com.ericsson.oss.itpf.datalayer.dps.BucketProperties;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.services.cm.cmshared.dto.AttributeSpecification;
import com.ericsson.oss.services.cm.cmshared.dto.AttributeSpecificationContainer;
import com.ericsson.oss.services.cm.cmshared.dto.search.CmMatchCondition;

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
    private static final String IPV6_ADDRESS = "FEDC:BA98:7654:3210:FEDC:BA98:7654:3210";
    private static final String IPV4_ADDRESS = "192.168.33.27";

    @Inject
    private EServiceProducer eserviceHolder;

    @Inject
    private UserTransaction userTransaction;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private Logger log;

    public enum IpAddressVersion {
        IPv4,
        IPv6;
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
        System.out.println("... dumping map ...");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(String.format("key: [%s], value: [%s]", entry.getKey(), entry.getValue()));
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

    public Map<String, String> createNode(final String nodeName, final String syncStatus, IpAddressVersion ipVersion) throws Exception {
        return createNode(nodeName, syncStatus, NodeSecurityDataConstants.OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE_VALUE, ipVersion);
    }

    public Map<String, String> createNode(final String nodeName, final String syncStatus, final SecurityLevel level, IpAddressVersion ipVersion)
            throws Exception {
        log.info("[NSCS_ARQ_DATA_SETUP] create CPP node {}", nodeName);

        beginTransaction();

        final DataBucket liveBucket = getLiveBucket();

        // Find suitable targetModelIdentity from model service
        final List<String> targetModelIdentities = eserviceHolder.getNscsModelService().getTargetModelIdentities(TargetTypeInformation.CATEGORY_NODE,
                ERBS_TARGET_TYPE);
        String targetModelIdentity = NodeSecurityDataConstants.NETWORK_ELEMENT_OSS_MODEL_IDENTITY_VERSION;
        for (String tmi : targetModelIdentities) {
            final NodeModelInformation nodeModelInfo = new NodeModelInformation(tmi, NodeModelInformation.ModelIdentifierType.OSS_IDENTIFIER,
                    NodeSecurityDataConstants.RADIO_TARGET_TYPE);
            if (nscsCapabilityModelService.isCertificateAuthorityDnSupported(nodeModelInfo)) {
                targetModelIdentity = tmi;
                break;
            }
        }

        Map<String, String> fdnMap = new HashMap<>();

        final String requestedModels[] = { NodeSecurityDataConstants.MANAGED_ELEMENT, NodeSecurityDataConstants.SYSTEM_FUNCTIONS,
                NodeSecurityDataConstants.IP_SYSTEM, NodeSecurityDataConstants.IP_SEC, NodeSecurityDataConstants.SECURITY };
        Map<String, NscsModelInfo> modelInfoMap = null;
        try {
            modelInfoMap = eserviceHolder.getNscsModelService().getModelInfoList(TargetTypeInformation.CATEGORY_NODE, ERBS_TARGET_TYPE,
                    targetModelIdentity, requestedModels);
        } catch (IllegalArgumentException | NscsModelServiceException ex) {
            log.error("getModelInfoList() caught exception: ", ex);
        }
        if ((modelInfoMap != null) && !modelInfoMap.isEmpty()) {

            // level 1 MeContext Mib in OSS_TOP model
            final ManagedObject parentMo = createMeContext(liveBucket, nodeName);

            final ManagedObject networkElement = createNetworkElementTree(liveBucket, parentMo, syncStatus, ipVersion, targetModelIdentity);
            // level 2 create ManagedElement Mib under CPP model

            final ManagedObject managedElementMO = createManagedElement(liveBucket, parentMo, targetModelIdentity,
                    modelInfoMap.get(NodeSecurityDataConstants.MANAGED_ELEMENT));
            log.info("Test Setup ManagedElement, created: " + managedElementMO.getFdn());

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
            fdnMap.put("ManagedElementMOFDN", managedElementMO.getFdn());
            fdnMap.put("SystemFunctionsMOFDN", systemFunctionsMO.getFdn());
            fdnMap.put("SecurityFunctionsMOFDN", securityFunctionsMO.getFdn());

            // create IpSystem
            final ManagedObject ipSystemMO = createIpSystem(liveBucket, managedElementMO, modelInfoMap.get(NodeSecurityDataConstants.IP_SYSTEM));
            log.info("Test Setup IpSystem, created: " + ipSystemMO.getFdn());
            fdnMap.put("IpSystemMOFDN", ipSystemMO.getFdn());

            // create IpSec
            final ManagedObject ipSecMO = createIpSec(liveBucket, ipSystemMO, modelInfoMap.get(NodeSecurityDataConstants.IP_SEC));
            log.info("Test Setup IpSec, created: " + ipSecMO.getFdn());
            fdnMap.put("IpSecMOFDN", ipSystemMO.getFdn());

            commitTransaction();

            log.info("[NSCS_ARQ_DATA_SETUP] read created CPP node {}", nodeName);
            beginTransaction();

            final DataBucket otherLiveBucket = getLiveBucket();

            final ManagedObject securityMObject = otherLiveBucket.findMoByFdn(securityFunctionsMO.getFdn());

            final Map<String, Object> securityAttributes = new HashMap<String, Object>();

            securityAttributes.put(NodeSecurityDataConstants.MANDATORY_SECURITY_ATTRIBUTE, null);
            securityAttributes.put(NodeSecurityDataConstants.OPERATIONAL_SECURITY_LEVEL_ATTRIBUTE, null);
            securityAttributes.put(NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, null);
            securityAttributes.put(NodeSecurityDataConstants.FILE_TRANSFER_CLIENT_MODE, null);
            securityAttributes.put(NodeSecurityDataConstants.CERT_ENROLL_STATE, null);
            securityAttributes.put(Security.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE, null);
            securityAttributes.put(NodeSecurityDataConstants.ACTIVE_USER_PROFILE_COMPLEX_ATTRIBUTE, null);

            for (Map.Entry<String, Object> entry : securityAttributes.entrySet()) {
                log.info("Reading securityMObject.....  securityMObject [{}], attribute [{}], value [{}]", securityMObject, entry.getKey(),
                        securityMObject.getAttribute(entry.getKey()));
            }
        }
        commitTransaction();

        log.info("[NSCS_ARQ_DATA_SETUP] create CPP node {} completed", nodeName);
        return fdnMap;
    }

    public static String networkElementNameFromMeContextName(String meContextName) {
        return meContextName + NETWORK_ELEMENT_NAME_SULFIX;
    }

    private ManagedObject createNetworkElementTree(final DataBucket liveBucket, final ManagedObject siblingMo, final String syncStatus,
            final IpAddressVersion ipVersion, final String targetModelIdentity) {
        final String neName = networkElementNameFromMeContextName(siblingMo.getName());
        ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace(Model.NETWORK_ELEMENT.namespace()).type(Model.NETWORK_ELEMENT.type())
                .addAttribute(NetworkElement.NETWORK_ELEMENT_ID, "1").addAttribute(NetworkElement.NE_TYPE, "ERBS")
                .addAttribute(NetworkElement.PLATFORM_TYPE, "CPP").addAttribute(NetworkElement.OSS_MODEL_IDENTITY, targetModelIdentity).name(neName)
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

        final String ipAddress = (ipVersion == IpAddressVersion.IPv6) ? IPV6_ADDRESS : IPV4_ADDRESS;
        liveBucket.getMibRootBuilder().namespace(Model.NETWORK_ELEMENT.cppConnectivityInformation.namespace())
                .type(Model.NETWORK_ELEMENT.cppConnectivityInformation.type())
                .addAttribute(ModelDefinition.CppConnectivityInformation.IPADDRESS, ipAddress).name("1").version("1.0.0").parent(networkElement)
                .create();
        log.info("Created cppConnectivityInformation.....   managedObject {}", networkElement);

        return networkElement;
    }

    public void createNetworkElementSecurity(final String parentNetworkElementName) throws Exception {
        log.info("[NSCS_ARQ_DATA_SETUP] create NetworkElementSecurity under {}", parentNetworkElementName);
        beginTransaction();

        final DataBucket liveBucket = getLiveBucket();

        final ManagedObject parent = liveBucket.findMoByFdn(Model.NETWORK_ELEMENT.securityFunction.withNames(parentNetworkElementName).fdn());

        ManagedObject networkElementSecurity = liveBucket.getMibRootBuilder()
                .namespace(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace())
                .type(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type())
                .addAttribute(NetworkElementSecurity.ROOT_USER_NAME, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_ROOT_USER_NAME)
                .addAttribute(NetworkElementSecurity.ROOT_USER_PASSWORD, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_ROOT_USER_PASSWORD)
                .addAttribute(NetworkElementSecurity.SECURE_USER_PASSWORD, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_SECURE_USER_PASSWORD)
                .addAttribute(NetworkElementSecurity.SECURE_USER_NAME, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_SECURE_USER_NAME)
                .addAttribute(NetworkElementSecurity.NORMAL_USER_PASSWORD, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_NORMAL_USER_PASSWORD)
                .addAttribute(NetworkElementSecurity.NORMAL_USER_NAME, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_NORMAL_USER_NAME)
                .addAttribute(NetworkElementSecurity.NETWORK_ELEMENT_SECURITY_ID, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_ID)
                .addAttribute(NetworkElementSecurity.TARGET_GROUPS, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_TARGET_GROUPS)
                .addAttribute(NetworkElementSecurity.ALGORITHM_AND_KEY_SIZE, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_ALGORITHM_KEY_SIZE)
                .addAttribute(NetworkElementSecurity.ENM_SSH_PUBLIC_KEY, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_PUBLIC_KEY)
                .addAttribute(NetworkElementSecurity.ENM_SSH_PRIVATE_KEY, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_PRIVATE_KEY)
                .addAttribute(NetworkElementSecurity.ENROLLMENT_MODE, NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_ENROLLMENT_MODE)
                .name(NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_NAME).version(NodeSecurityDataConstants.NETWORK_ELEMENT_SEC_VERSION)
                .parent(parent).create();

        commitTransaction();
        log.info("[NSCS_ARQ_DATA_SETUP] Created NetworkElementSecurity.....   managedObject {}", networkElementSecurity);
    }

    public void changeAttributesForSecurityMO(final String securityMOFdn, final Map<String, Object> userDefProfilesInfoAttributesMap)
            throws Exception {
        log.info("[NSCS_ARQ_DATA_SETUP] change attributes for Security MO {}", securityMOFdn);

        beginTransaction();

        final DataBucket liveBucket = getLiveBucket();

        final ManagedObject managedObject = liveBucket.findMoByFdn(securityMOFdn);
        log.info("ChangeAttributesForSecurityMO.....  securityMOFdn {}, managedObject {}", securityMOFdn, managedObject);

        managedObject.setAttributes(userDefProfilesInfoAttributesMap);
        commitTransaction();
        log.info("[NSCS_ARQ_DATA_SETUP] change attributes for Security MO {} completed", securityMOFdn);
    }

    public ManagedObject createMeContext(final DataBucket liveBucket, final String nodeName) {
        final ManagedObject parentMo = liveBucket.getMibRootBuilder().namespace(NodeSecurityDataConstants.TOP_NAMESPACE).type("MeContext")
                .addAttribute("neType", ERBS_TARGET_TYPE).addAttribute("MeContextId", "1").version(NodeSecurityDataConstants.TOP_NAMESPACE_VERSION)
                .name(nodeName).create();

        log.info("Created MeContext.....  name {}, managedObject {}, fdn {}", nodeName, parentMo, parentMo.getFdn());
        return parentMo;
    }

    public ManagedObject createManagedElement(final DataBucket liveBucket, final ManagedObject parentMo, final String tmi,
            final NscsModelInfo nscsModelInfo) {

        final Map<String, Object> mandatoryManagedElementAttributes = createMapAndInsertValues(
                NodeSecurityDataConstants.MANDATORY_MANAGED_ELEMENT_ATTRIBUTE, NodeSecurityDataConstants.MANDATORY_MANAGED_ELEMENT_ATTRIBUTE_VALUE);

        mandatoryManagedElementAttributes.putAll(
                createMapAndInsertValues(NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE_VALUE));

        log.info("Create ManagedElement : mandatoryManagedElementAttributes {}", mandatoryManagedElementAttributes);
        final ManagedObject managedElementMo = createManagedObject(liveBucket, parentMo, mandatoryManagedElementAttributes,
                NodeSecurityDataConstants.MANAGED_ELEMENT, nscsModelInfo);
        return managedElementMo;
    }

    public ManagedObject createIpSystem(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo nscsModelInfo) {
        final Map<String, Object> ipSystemAttributes = createMapAndInsertValues(NodeSecurityDataConstants.MANDATORY_IP_SYSTEM_ATTRIBUTE,
                NodeSecurityDataConstants.MANDATORY_IP_SYSTEM_ATTRIBUTE_VALUE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE,
                NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE_VALUE);
        return createManagedObject(liveBucket, parentMo, ipSystemAttributes, NodeSecurityDataConstants.IP_SYSTEM, nscsModelInfo);
    }

    public ManagedObject createIpSec(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo nscsModelInfo) {
        final Map<String, Object> mandatoryIpSecAttributes = createMapAndInsertValues(NodeSecurityDataConstants.MANDATORY_IP_SEC_ATTRIBUTE,
                NodeSecurityDataConstants.MANDATORY_IP_SEC_ATTRIBUTE_VALUE, IpSec.FEATURE_STATE,
                ModelDefinition.IpSec.ActivationVals.ACTIVATED.toString(), IpSec.LICENSE_STATE, ModelDefinition.IpSec.StateVals.ENABLED.toString(),
                IpSec.TRUSTED_CERT_INST_STATE, ModelDefinition.IpSec.IpSecTrustedCertInstallStateValue.ERROR.toString(), IpSec.CERT_ENROLL_STATE,
                ModelDefinition.IpSec.IpSecCertEnrollStateValue.ERROR.toString());

        // complex attribute
        final Map<String, Object> ipSecCertInfo = createMapAndInsertValues(NodeSecurityDataConstants.IP_SEC_CERTIFICATE_SERIALNUMBER_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_CERTIFICATE_SERIALNUMBER_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.IP_SEC_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE,
                NodeSecurityDataConstants.IP_SEC_CERTIFICATE_SUBJECTALTNAME_ATTRIBUTE_VALUE);
        mandatoryIpSecAttributes.put(IpSec.CERTIFICATE, ipSecCertInfo);

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
        mandatoryIpSecAttributes.put(IpSec.INSTALLED_TRUSTED_CERTIFICATES, trustCerts);

        return createManagedObject(liveBucket, parentMo, mandatoryIpSecAttributes, NodeSecurityDataConstants.IP_SEC, nscsModelInfo);
    }

    public ManagedObject createSystemFunctions(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo nscsModelInfo) {

        final Map<String, Object> mandatorySystemFunctionsAttributes = createMapAndInsertValues(
                NodeSecurityDataConstants.MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE, NodeSecurityDataConstants.MANDATORY_SYSTEM_FUNCTIONS_ATTRIBUTE_VALUE,
                NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE, NodeSecurityDataConstants.USER_LABEL_ATTRIBUTE_VALUE);

        final ManagedObject systemFunctionsMo = createManagedObject(liveBucket, parentMo, mandatorySystemFunctionsAttributes,
                NodeSecurityDataConstants.SYSTEM_FUNCTIONS, nscsModelInfo);

        return systemFunctionsMo;
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

        final ManagedObject SecurityMo = createManagedObject(liveBucket, parentMo, securityAttributes, NodeSecurityDataConstants.SECURITY,
                nscsModelInfo);

        return SecurityMo;
    }

    private ManagedObject createManagedObject(final DataBucket liveBucket, final ManagedObject parentMo,
            final Map<String, Object> managedObjectAttributes, final String type, final NscsModelInfo nscsModelInfo) {

        final ManagedObject SecurityMo = liveBucket.getMibRootBuilder().namespace(nscsModelInfo.getNamespace()).type(type)
                .version(nscsModelInfo.getVersion()).name(NodeSecurityDataConstants.MO_NAME).addAttributes(managedObjectAttributes).parent(parentMo)
                .create();
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
        deleteAllNodes(Model.NETWORK_ELEMENT.namespace(), Model.NETWORK_ELEMENT.type());
        deleteAllNodes(Model.NETWORK_ELEMENT.cppConnectivityInformation.namespace(), Model.NETWORK_ELEMENT.cppConnectivityInformation.type());
    }

    private void deleteAllNodes(final String namespace, final String type) throws Exception {

        log.info("[NSCS_ARQ_DATA_SETUP] deleteAllNodes for namespace [{}] , type [{}]", namespace, type);

        beginTransaction();

        final QueryBuilder queryBuilder = eserviceHolder.getDataPersistenceService().getQueryBuilder();
        final Query<TypeRestrictionBuilder> query = queryBuilder.createTypeQuery(namespace, type);

        commitTransaction();

        beginTransaction();

        try {
            DataBucket liveBucket = eserviceHolder.getDataPersistenceService().getLiveBucket();
            if (liveBucket != null) {
                log.info("[NSCS_ARQ_DATA_SETUP] reading all POs for namespace [{}] , type [{}]", namespace, type);
                final Iterator<PersistenceObject> iterator = liveBucket.getQueryExecutor().execute(query);
                final boolean hasPOs = (iterator != null) && iterator.hasNext();
                log.info("[NSCS_ARQ_DATA_SETUP] deleting all POs (hasPOs={}) with namespace [{}] , type [{}]", hasPOs, namespace, type);
                while ((iterator != null) && iterator.hasNext()) {
                    final PersistenceObject po = iterator.next();
                    log.info("[NSCS_ARQ_DATA_SETUP] deleting PO [{}]", po.toString());
                    liveBucket.deletePo(po);
                }
            }
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_DATA_SETUP] deleteAllNodes: caught exception", e);

            rollbackTransaction();

            return;
        }

        commitTransaction();
    }

    private DataBucket getLiveBucket() {
        return getDataPersistenceService().getDataBucket("Live", BucketProperties.SUPPRESS_MEDIATION, BucketProperties.SUPPRESS_CONSTRAINTS);
    }

    private com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService getDataPersistenceService() {
        return eserviceHolder.getDataPersistenceService();
    }

    public Map<String, String> createNode(final String nodeName) throws Exception {
        return createNode(nodeName, ModelDefinition.CmFunction.SyncStatusValue.SYNCHRONIZED.name(), IpAddressVersion.IPv4);
    }

    public Map<String, String> createComEcimNode(final String nodeName, final String syncStatus, final String enrollmentSupport, final String neType)
            throws Exception {
        return createNode(nodeName, syncStatus, enrollmentSupport, neType);
    }

    private Map<String, String> createNode(final String nodeName, final String syncStatus, final String enrollmentSupport, final String neType)
            throws Exception {
        log.info("[NSCS_ARQ_DATA_SETUP] create node {} of type {}", nodeName, neType);

        beginTransaction();

        final DataBucket liveBucket = getLiveBucket();

        // level 1 MeContext Mib in OSS_TOP model
        final ManagedObject parentMo = createComEcimMeContext(liveBucket, nodeName, neType);

        // level 2 create ManagedElement Mib under CPP model
        final ManagedObject networkElement = createComEcimNetworkElementTree(liveBucket, parentMo, syncStatus, neType);

        final Map<String, String> fdnMap = new HashMap<String, String>();

        commitTransaction();

        log.info("[NSCS_ARQ_DATA_SETUP] create node {} of type {} : completed", nodeName, neType);

        return fdnMap;
    }

    public ManagedObject createComEcimMeContext(final DataBucket liveBucket, final String nodeName, final String neType) {

        final ManagedObject parentMo = liveBucket.getMibRootBuilder().namespace(NodeSecurityDataConstants.TOP_NAMESPACE).type("MeContext")
                .addAttribute("neType", neType).addAttribute("MeContextId", nodeName).version(NodeSecurityDataConstants.TOP_NAMESPACE_VERSION)
                .name(nodeName).create();

        log.info("Created MeContext.....  name {}, managedObject {}, fdn {}", nodeName, parentMo, parentMo.getFdn());
        return parentMo;
    }

    private ManagedObject createComEcimNetworkElementTree(DataBucket liveBucket, ManagedObject siblingMo, final String syncStatus,
            final String neType) {

        final String neName = networkElementNameFromMeContextName(siblingMo.getName());

        final List<String> targetModelIdentities = eserviceHolder.getNscsModelService().getTargetModelIdentities(TargetTypeInformation.CATEGORY_NODE,
                RADIO_TARGET_TYPE);
        final String tmi = targetModelIdentities.isEmpty() ? NodeSecurityDataConstants.NETWORK_ELEMENT_OSS_MODEL_IDENTITY_VERSION
                : targetModelIdentities.get(0);
        final ManagedObject networkElement = liveBucket.getMibRootBuilder().namespace(Model.NETWORK_ELEMENT.namespace())
                .type(Model.NETWORK_ELEMENT.type()).addAttribute(NetworkElement.NETWORK_ELEMENT_ID, "1").addAttribute(NetworkElement.NE_TYPE, neType)
                .addAttribute(NetworkElement.OSS_MODEL_IDENTITY, tmi).name(neName).version(NodeSecurityDataConstants.NETWORK_ELEMENT_VERSION)
                .create();
        log.info("Created NetworkElement.....  name {}, managedObject {}", neName, networkElement);

        networkElement.addAssociation("nodeRootRef", siblingMo);
        log.info("Created association {} <--> {}", siblingMo.getName(), neName);

        return networkElement;

    }

    public ManagedObject createSecM(final DataBucket liveBucket, final ManagedObject parentMo, final NscsModelInfo nscsModelInfo) {

        final Map<String, Object> mandatorySecMAttributes = createMapAndInsertValues(NodeSecurityDataConstants.MANDATORY_SEC_M_ATTRIBUTE,
                NodeSecurityDataConstants.MANDATORY_SEC_M_ATTRIBUTE_VALUE);

        final ManagedObject secMMo = createManagedObject(liveBucket, parentMo, mandatorySecMAttributes, NodeSecurityDataConstants.SEC_M,
                nscsModelInfo);

        return secMMo;
    }

    public ManagedObject createCertM(final DataBucket liveBucket, final ManagedObject parentMo, NscsModelInfo nscsModelInfo) {

        final Map<String, Object> mandatoryCertMAttributes = createMapAndInsertValues(NodeSecurityDataConstants.MANDATORY_CERT_M_ATTRIBUTE,
                NodeSecurityDataConstants.MANDATORY_CERT_M_ATTRIBUTE_VALUE);

        final ManagedObject certMsMo = createManagedObject(liveBucket, parentMo, mandatoryCertMAttributes, NodeSecurityDataConstants.CERT_M,
                nscsModelInfo);

        return certMsMo;
    }

    public Long getNodePoid(final String fdn) throws Exception {
        log.info("[NSCS_ARQ_DATA_SETUP] get node PoId for {}", fdn);

        beginTransaction();

        final DataBucket liveBucket = getLiveBucket();

        ManagedObject mo = liveBucket.findMoByFdn(fdn);

        commitTransaction();

        return mo.getPoId();

    }

    public PersistenceObject createStaticCollection(String name) throws Exception {

        log.info("[NSCS_ARQ_DATA_SETUP] ------------------------Started executing createStaticCollection-------------------------- ");
        final PersistenceObject po = createCollection("CreateTopologyCollectionTestCase1-Administrator", "Administrator", "Public");
        log.info("[NSCS_ARQ_DATA_SETUP] ------------------------Finished executing createStaticCollection-------------------------- ");
        return po;
    }

    public PersistenceObject createCollection(final String name, final String userId, final String category) throws Exception {
        log.info("[NSCS_ARQ_DATA_SETUP] create collection {} {} {}", name, userId, category);
        Map<String, Object> validatedAttributes = createValidatedAttributes(name, userId, category);

        beginTransaction();

        final DataBucket liveBucket = getLiveBucket();

        final PersistenceObject po = liveBucket.getPersistenceObjectBuilder().namespace(COLLECTION_NAMESPACE).type(COLLECTION_TYPE)
                .addAttributes(validatedAttributes).version(COLLECTION_VERSION).create();

        commitTransaction();

        return po;
    }

    public Map<String, Object> createValidatedAttributes(final String name, final String user, final String category) {
        final Map<String, Object> validatedAttributes = new HashMap<>();
        validatedAttributes.put(ATTRIBUTE_NAME, name);
        validatedAttributes.put(ATTRIBUTE_USERID, user);
        validatedAttributes.put(ATTRIBUTE_CATEGORY, category);
        validatedAttributes.put(ATTRIBUTE_TIME_CREATED, generateCurrentUtcTime());

        return validatedAttributes;
    }

    public String generateCurrentUtcTime() {
        final Long currentTime = new Long(System.currentTimeMillis());
        return currentTime.toString();
    }

    public void deleteCollection(final List<Long> poids) {
        log.info("[NSCS_ARQ_DATA_SETUP] delete collection");
        try {

            beginTransaction();

            final DataBucket liveBucket = getLiveBucket();
            PersistenceObject po;
            if ((poids != null) && !poids.isEmpty()) {
                for (final Long poId : poids) {
                    po = liveBucket.findPoById(poId);
                    if (po != null) {
                        if (liveBucket.deletePo(po) < 0) {
                            log.error("Error: cannot delete Persistence Object PoId[{}]", poId);
                        }
                    } else {
                        log.error("Error: cannot find Persistence Object PoId[{}]", poId);
                    }
                }
            }

            commitTransaction();

        } catch (final Exception e) {
            log.error("[NSCS_ARQ_DATA_SETUP] Cannot delete collection : " + e.getClass() + " : " + e.getMessage());
        }
    }

    public void addAttributeCriteria(final String name, final Object value, final AttributeSpecificationContainer attributeSpecifications) {
        final AttributeSpecification attributeSpecification = new AttributeSpecification();
        attributeSpecification.setName(name);
        attributeSpecification.setValue(value);
        attributeSpecification.setCmMatchCondition(CmMatchCondition.EQUALS);
        attributeSpecifications.addAttributeSpecification(attributeSpecification);
    }

    private int getTransactionStatus() throws Exception {
        try {
            return userTransaction.getStatus();
        } catch (final SystemException e) {
            log.error("[NSCS_ARQ_DATA_SETUP] transaction : status : FAILED", e);
            throw e;
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_DATA_SETUP] transaction : status : ERROR", e);
            throw e;
        }
    }

    protected void beginTransaction() throws Exception {
        log.info("[NSCS_ARQ_DATA_SETUP] transaction : begin : STARTED : status [{}]", getTransactionStatus());
        try {
            //            userTransaction.setTransactionTimeout(120);
            userTransaction.begin();
            log.info("[NSCS_ARQ_DATA_SETUP] transaction : begin : SUCCESS : status [{}]", getTransactionStatus());
        } catch (final NotSupportedException | SystemException e) {
            log.error("[NSCS_ARQ_DATA_SETUP] transaction : begin : FAILED", e);
            throw e;
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_DATA_SETUP] transaction : begin : ERROR", e);
            throw e;
        }
    }

    protected void commitTransaction() throws Exception {
        log.info("[NSCS_ARQ_DATA_SETUP] transaction : commit : STARTED : status [{}]", getTransactionStatus());
        try {
            userTransaction.commit();
            log.info("[NSCS_ARQ_DATA_SETUP] transaction : commit : SUCCESS : status [{}]", getTransactionStatus());
        } catch (final SecurityException | IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException
                | SystemException e) {
            log.error("[NSCS_ARQ_DATA_SETUP] transaction : commit : FAILED", e);
            throw e;
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_DATA_SETUP] transaction : commit : ERROR", e);
            throw e;
        }
    }

    protected void rollbackTransaction() throws Exception {
        log.info("[NSCS_ARQ_DATA_SETUP] transaction : rollback : STARTED : status [{}]", getTransactionStatus());
        try {
            userTransaction.rollback();
            log.info("[NSCS_ARQ_DATA_SETUP] transaction : rollback : SUCCESS : status [{}]", getTransactionStatus());
        } catch (final IllegalStateException | SecurityException | SystemException e) {
            log.error("[NSCS_ARQ_DATA_SETUP] transaction : rollback : FAILED", e);
            throw e;
        } catch (final Exception e) {
            log.error("[NSCS_ARQ_DATA_SETUP] transaction : rollback : ERROR", e);
            throw e;
        }
    }

}
