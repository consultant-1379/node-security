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

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.data.ModelDefinition
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.HierarchicalPrimaryTypeSpecification
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeAttributeSpecification
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeSpecification
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo
import com.ericsson.oss.itpf.modeling.modelservice.ModelService
import com.ericsson.oss.itpf.modeling.modelservice.exception.InvalidModelForTargetTypeException
import com.ericsson.oss.itpf.modeling.modelservice.exception.UnknownModelException
import com.ericsson.oss.itpf.modeling.modelservice.exception.UnknownSchemaException
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelMetaInformation
import com.ericsson.oss.itpf.modeling.modelservice.typed.TypedModelAccess
import com.ericsson.oss.itpf.modeling.modelservice.typed.capabilities.CapabilityInformation
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.DataTypeSpecification
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.EModelSpecification
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.edt.EnumDataTypeSpecification
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.exception.MatchingClassNotFoundException
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.MimMappedTo
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.Target
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeVersionInformation
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants

class NscsCdiSpecification extends CdiSpecification {

    public static final String RADIONODE_TARGET_TYPE = "RadioNode"
    public static final String RADIONODE_TARGET_MODEL_IDENTITY = "20.Q2-R4A24"
    public static final String RADIONODE_TOP_NS = "ComTop"
    public static final String VDU_TARGET_TYPE = "vDU"
    public static final String VDU_TARGET_MODEL_IDENTITY = "0.5.1"
    public static final String VDU_TOP_NS = "VduTop"
    public static final String SHARED_CNF_TARGET_TYPE = "Shared-CNF"
    public static final String SHARED_CNF_TARGET_MODEL_IDENTITY = "1.1"
    public static final String SYSTEM_LDAP_SCOPED_TYPE = "ldap"
    public static final String TCP_LDAP_SCOPED_TYPE = "ldap"
    public static final String KEYSTORE_CMP_SCOPED_TYPE = "keystore\$\$cmp"
    public static final String ASYMMETRIC_KEYS_CMP_SCOPED_TYPE = "asymmetric-keys\$\$cmp"
    public static final String ASYMMETRIC_KEY_CMP_SCOPED_TYPE = "asymmetric-key\$\$cmp"

    @ImplementationInstance
    EnumDataTypeSpecification algorithmAndKeySizeEnumDataTypeSpecification = [
        getMemberNames : {
            return [
                "RSA_1024",
                "RSA_2048",
                "RSA_4096"
            ]
        },
        getMemberValues : {
            return [
                0,
                1,
                2
            ]
        }
    ] as EnumDataTypeSpecification

    @ImplementationInstance
    DataTypeSpecification algorithmAndKeySizeDataTypeSpecification = [
        getReferencedDataType : {
            return new ModelInfo(SchemaConstants.OSS_EDT, 'any', 'AlgorithmAndKeySize', '2.0.0')
        }
    ] as DataTypeSpecification

    @ImplementationInstance
    PrimaryTypeAttributeSpecification algorithmAndKeySizePrimaryTypeAttributeSpecification = [
        getDataTypeSpecification : {
            return algorithmAndKeySizeDataTypeSpecification
        }
    ] as PrimaryTypeAttributeSpecification

    @ImplementationInstance
    PrimaryTypeSpecification networkElementSecurityPrimaryTypeSpecification = [
        getAttributeSpecification : { String attributeName ->
            if (attributeName == 'algorithmAndKeySize') {
                return algorithmAndKeySizePrimaryTypeAttributeSpecification
            }
            return null
        },
        getActionSpecifications : {
            return []
        },
        getMemberNames : {
            return []
        }
    ] as PrimaryTypeSpecification

    @ImplementationInstance
    PrimaryTypeSpecification networkElementSecurityPrimaryTypeSpecification501 = [
        getAttributeSpecification : { String attributeName ->
            if (attributeName == 'algorithmAndKeySize') {
                return algorithmAndKeySizePrimaryTypeAttributeSpecification
            }
            return null
        },
        getActionSpecifications : {
            return []
        },
        getMemberNames : {
            return ['proxyAccountDn']
        }
    ] as PrimaryTypeSpecification

    @ImplementationInstance
    PrimaryTypeSpecification primaryTypeSpecification = [
        getActionSpecifications : {
            return []
        },
        getMemberNames : {
            return []
        }
    ] as PrimaryTypeSpecification

    @ImplementationInstance
    ModelMetaInformation modelMetaInformation = [
        getLatestVersionOfModel : { String schema, String namespace, String model ->
            if (schema == SchemaConstants.DPS_PRIMARYTYPE && model == 'NetworkElementSecurity') {
                return new ModelInfo(schema, namespace, model, '5.0.0')
            }
            return null
        },
        getModelsFromUrn : { String schema, String namespace, String model, String version ->
            if (model == "Ikev2PolicyProfile" && namespace == "RtnIkev2PolicyProfile") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if (model == "NodeCredential" && namespace == "RcsCertM") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if (model == "Ldap" && namespace == "RcsLdapAuthentication") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if ((model == "keystore" || model == "asymmetric-keys" || model == "asymmetric-key"
                    || model == "certificates" || model == "certificate") && namespace == "urn:ietf:params:xml:ns:yang:ietf-keystore") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if ((model == KEYSTORE_CMP_SCOPED_TYPE || model == "certificate-authorities" || model == "certificate-authority"
                    || model == "cmp-server-groups" || model == "cmp-server-group" || model == "cmp-server"
                    || model == ASYMMETRIC_KEYS_CMP_SCOPED_TYPE || model == ASYMMETRIC_KEY_CMP_SCOPED_TYPE) && namespace == "urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if ((model == "truststore" || model == "certificates" || model == "certificate") && namespace == "urn:ietf:params:xml:ns:yang:ietf-truststore") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if ((model == "system" || model == "authentication" || model == "user" || model == "authorized-key") && namespace == "urn:ietf:params:xml:ns:yang:ietf-system") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if ((model == SYSTEM_LDAP_SCOPED_TYPE || model == "security" || model == "simple-authenticated"
                    || model == "tls" || model == "server" || model == "tcp" || model == "ldaps"
                    || model == TCP_LDAP_SCOPED_TYPE) && namespace == "urn:rdns:com:ericsson:oammodel:ericsson-system-ext") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if (model == "Security" && namespace == "ERBS_NODE_MODEL") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if (model == "NetworkElementSecurity" && namespace == "OSS_NE_SEC_DEF") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else {
                return []
            }
        }
    ] as ModelMetaInformation

    @ImplementationInstance
    MimMappedTo mimQ2Ikev2PolicyProfile = [
        getNamespace : {
            return "RtnIkev2PolicyProfile"
        },
        getVersion : {
            return "1.14.0"
        },
        getReferenceMimNamespace: {
            return null
        }
    ] as MimMappedTo

    @ImplementationInstance
    MimMappedTo mimQ3Ikev2PolicyProfile = [
        getNamespace : {
            return "RtnIkev2PolicyProfile"
        },
        getVersion : {
            return "1.15.1"
        },
        getReferenceMimNamespace: {
            return null
        }
    ] as MimMappedTo

    @ImplementationInstance
    MimMappedTo mimQ2NodeCredential = [
        getNamespace : {
            return "RcsCertM"
        },
        getVersion : {
            return "3.0.5"
        },
        getReferenceMimNamespace: {
            return "ECIM_CertM"
        }
    ] as MimMappedTo

    @ImplementationInstance
    MimMappedTo mimQ3NodeCredential = [
        getNamespace : {
            return "RcsCertM"
        },
        getVersion : {
            return "3.0.5"
        },
        getReferenceMimNamespace: {
            return "ECIM_CertM"
        }
    ] as MimMappedTo

    @ImplementationInstance
    MimMappedTo mimLdapAuthentication = [
        getNamespace : {
            return ModelDefinition.RCS_LDAP_AUTH_NS
        },
        getVersion : {
            return "1.1.0"
        },
        getReferenceMimNamespace: {
            return ModelDefinition.REF_MIM_NS_ECIM_LDAP_AUTHENTICATION
        }
    ] as MimMappedTo

    @ImplementationInstance
    MimMappedTo mimVduKeystore = [
        getNamespace : {
            return "urn:ietf:params:xml:ns:yang:ietf-keystore"
        },
        getVersion : {
            return "2019.11.20"
        },
        getReferenceMimNamespace: {
            return null
        }
    ] as MimMappedTo

    @ImplementationInstance
    MimMappedTo mimVduKeystoreExt = [
        getNamespace : {
            return "urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext"
        },
        getVersion : {
            return "1.1.0"
        },
        getReferenceMimNamespace: {
            return null
        }
    ] as MimMappedTo

    @ImplementationInstance
    MimMappedTo mimVduTruststore = [
        getNamespace : {
            return "urn:ietf:params:xml:ns:yang:ietf-truststore"
        },
        getVersion : {
            return "2019.11.20"
        },
        getReferenceMimNamespace: {
            return null
        }
    ] as MimMappedTo

    @ImplementationInstance
    MimMappedTo mimVduSystem = [
        getNamespace : {
            return "urn:ietf:params:xml:ns:yang:ietf-system"
        },
        getVersion : {
            return "2019.11.20"
        },
        getReferenceMimNamespace: {
            return null
        }
    ] as MimMappedTo

    @ImplementationInstance
    MimMappedTo mimVduSystemExt = [
        getNamespace : {
            return "urn:rdns:com:ericsson:oammodel:ericsson-system-ext"
        },
        getVersion : {
            return "1.1.0"
        },
        getReferenceMimNamespace: {
            return null
        }
    ] as MimMappedTo

    @ImplementationInstance
    MimMappedTo mimErbs = [
        getNamespace : {
            return "ERBS_NODE_MODEL"
        },
        getVersion : {
            return "10.4.555"
        },
        getReferenceMimNamespace: {
            return null
        }
    ] as MimMappedTo

    @ImplementationInstance
    TargetTypeVersionInformation radioNodeVersionInformation = [
        getMimsMappedTo : { String tMI ->
            if (tMI == "20.Q2-R4A24") {
                return Arrays.asList(mimQ2Ikev2PolicyProfile, mimQ2NodeCredential, mimLdapAuthentication)
            } else if (tMI == "20.Q3-R13A40") {
                return Arrays.asList(mimQ3Ikev2PolicyProfile, mimQ3NodeCredential, mimLdapAuthentication)
            } else {
                throw new IllegalArgumentException()
            }
        }
    ] as TargetTypeVersionInformation

    @ImplementationInstance
    TargetTypeVersionInformation vduVersionInformation = [
        getMimsMappedTo : { String tMI ->
            if (tMI == VDU_TARGET_MODEL_IDENTITY || tMI == SHARED_CNF_TARGET_MODEL_IDENTITY) {
                return Arrays.asList(mimVduKeystore, mimVduKeystoreExt, mimVduTruststore, mimVduSystem, mimVduSystemExt)
            } else {
                throw new IllegalArgumentException()
            }
        }
    ] as TargetTypeVersionInformation

    @ImplementationInstance
    TargetTypeVersionInformation erbsVersionInformation = [
        getMimsMappedTo : { String tMI ->
            if (tMI == "20.Q1-J.4.555") {
                return Arrays.asList(mimErbs)
            } else {
                throw new IllegalArgumentException()
            }
        }
    ] as TargetTypeVersionInformation

    @ImplementationInstance
    TargetTypeInformation targetTypeInformation = [
        getTargetTypeVersionInformation : { String targetCategory, String targetType ->
            if (targetType == RADIONODE_TARGET_TYPE) {
                return radioNodeVersionInformation
            } else if (targetType == VDU_TARGET_TYPE || targetType == SHARED_CNF_TARGET_TYPE) {
                return vduVersionInformation
            } else if (targetType == "ERBS") {
                return erbsVersionInformation
            } else {
                throw new IllegalArgumentException()
            }
        },
        getRootMoType : { String targetCategory, String targetType ->
            if (targetType == RADIONODE_TARGET_TYPE) {
                return "//ComTop/ManagedElement/*"
            } else if (targetType == VDU_TARGET_TYPE) {
                return "//" + VDU_TOP_NS + "/ManagedElement/*"
            } else if (targetType == SHARED_CNF_TARGET_TYPE) {
                return "//OSS_TOP/MeContext/*"
            } else if (targetType == "ERBS") {
                return "//ERBS_NODE_MODEL/ManagedElement/*"
            } else if (targetType == "3GPP") {
                return null
            } else {
                throw new IllegalArgumentException()
            }
        },
        getPlatform : { String targetCategory, String targetType ->
            if (targetType == RADIONODE_TARGET_TYPE) {
                return "ECIM"
            } else if (targetType == VDU_TARGET_TYPE) {
                return "EOI"
            } else if (targetType == SHARED_CNF_TARGET_TYPE) {
                return "EOI"
            } else if (targetType == "ERBS") {
                return "CPP"
            } else {
                throw new IllegalArgumentException()
            }
        }
    ] as TargetTypeInformation

    @ImplementationInstance
    CapabilityInformation capabilityInformation = [
        getCapabilityValue : { String targetCategory, String targetType, String function, String capabilityName, String capabilitySupportModelVersion ->
            if (targetType == VDU_TARGET_TYPE || targetType == SHARED_CNF_TARGET_TYPE) {
                if (capabilityName == "momType") {
                    return "EOI"
                }
            } else if (targetType == RADIONODE_TARGET_TYPE) {
                if (capabilityName == "momType") {
                    return "ECIM"
                }
            } else if (targetType == "ERBS") {
                if (capabilityName == "momType") {
                    return "CPP"
                }
            } else
                return null
        }
    ] as CapabilityInformation

    @ImplementationInstance
    ModelService modelService = [
        getTypedAccess : {
            return typedModelAccess
        },
        getModelMetaInformation : {
            return modelMetaInformation
        }
    ] as ModelService

    @ImplementationInstance
    TypedModelAccess typedModelAccess = new TypedModelAccessStub()

    class TypedModelAccessStub implements TypedModelAccess {

        @Override
        def getModelInformation(Class clazz) {
            if (clazz == CapabilityInformation.class) {
                return capabilityInformation
            }
            return targetTypeInformation
        }

        @Override
        <T extends EModelSpecification> T getEModelSpecification(ModelInfo modelInfo, Class clazz) throws
        UnknownModelException, MatchingClassNotFoundException, UnknownSchemaException {
            if (clazz == PrimaryTypeSpecification.class && modelInfo.getName() == 'NetworkElementSecurity') {
                if ("5.1.0".equals(modelInfo.getVersion().toString())) {
                    return networkElementSecurityPrimaryTypeSpecification501
                } else {
                    return networkElementSecurityPrimaryTypeSpecification
                }
            } else if (clazz == EnumDataTypeSpecification.class && modelInfo.getName() == 'AlgorithmAndKeySize') {
                return algorithmAndKeySizeEnumDataTypeSpecification
            }
            return primaryTypeSpecification
        }

        //        @Override
        //        PrimaryTypeSpecification getEModelSpecification(ModelInfo modelInfo, Class clazz) throws
        //        UnknownModelException, MatchingClassNotFoundException, UnknownSchemaException {
        //            if (modelInfo.getName() == 'NetworkElementSecurity') {
        //                return networkElementSecurityPrimaryTypeSpecification
        //            }
        //            return primaryTypeSpecification
        //        }

        @Override
        HierarchicalPrimaryTypeSpecification getEModelSpecification(ModelInfo modelInfo, Class clazz, Target target) throws
        UnknownModelException, MatchingClassNotFoundException, UnknownSchemaException {

            def hierarchicalPrimaryTypeSpecificationMap = [:]
            def childHierarchicalPrimaryTypeSpecificationMap = [:]
            def childHierarchicalPrimaryTypeSpecificationMap2 = [:]
            def childHierarchicalPrimaryTypeSpecificationMap3 = [:]
            def childHierarchicalPrimaryTypeSpecificationMap4 = [:]

            switch (modelInfo.getName()) {
                case ModelDefinition.MANAGED_ELEMENT_TYPE:
                case ModelDefinition.ME_CONTEXT_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.KEYSTORE_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_NS, ModelDefinition.KEYSTORE_TYPE, "2019.11.20")
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getUnscopedType = {
                        return ModelDefinition.TRUSTSTORE_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_TRUSTSTORE_NS, ModelDefinition.TRUSTSTORE_TYPE, "2019.11.20")
                    }
                    childHierarchicalPrimaryTypeSpecificationMap3.getUnscopedType = {
                        return ModelDefinition.SYSTEM_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap3.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_SYSTEM_NS, ModelDefinition.SYSTEM_TYPE, "2019.11.20")
                    }
                    childHierarchicalPrimaryTypeSpecificationMap4.getUnscopedType = {
                        return ModelDefinition.SYSTEM_FUNCTIONS_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap4.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.COM_TOP_NS, ModelDefinition.SYSTEM_FUNCTIONS_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification,
                            childHierarchicalPrimaryTypeSpecificationMap2 as HierarchicalPrimaryTypeSpecification,
                            childHierarchicalPrimaryTypeSpecificationMap3 as HierarchicalPrimaryTypeSpecification,
                            childHierarchicalPrimaryTypeSpecificationMap4 as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                // SYSTEM_FUNCTIONS_TYPE -> SEC_M_TYPE
                case ModelDefinition.SYSTEM_FUNCTIONS_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.SEC_M_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.COM_SEC_M_NS, ModelDefinition.SEC_M_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                // SEC_M_TYPE -> USER_MANAGEMENT_TYPE
                case ModelDefinition.SYSTEM_FUNCTIONS_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.USER_MANAGEMENT_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.COM_SEC_M_NS, ModelDefinition.USER_MANAGEMENT_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                // USER_MANAGEMENT_TYPE -> LDAP_AUTHENTICATION_METHOD_TYPE
                case ModelDefinition.USER_MANAGEMENT_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.LDAP_AUTHENTICATION_METHOD_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.RCS_LDAP_AUTH_NS, ModelDefinition.LDAP_AUTHENTICATION_METHOD_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                // LDAP_AUTHENTICATION_METHOD_TYPE -> LDAP_TYPE
                case ModelDefinition.LDAP_AUTHENTICATION_METHOD_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.ECIM_LDAP_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.RCS_LDAP_AUTH_NS, ModelDefinition.ECIM_LDAP_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                // SYSTEM_TYPE -> SYSTEM_LDAP_TYPE
                // SYSTEM_TYPE -> AUTHENTICATION_TYPE
                case ModelDefinition.SYSTEM_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.LDAP_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_SYSTEM_EXT_NS, SYSTEM_LDAP_SCOPED_TYPE, "1.1.0")
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getUnscopedType = { return ModelDefinition.AUTHENTICATION_TYPE }
                    childHierarchicalPrimaryTypeSpecificationMap2.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_SYSTEM_NS, ModelDefinition.AUTHENTICATION_TYPE, "2019.11.20")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification,
                            childHierarchicalPrimaryTypeSpecificationMap2 as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                // SYSTEM_LDAP_TYPE -> CBP_OI_SECURITY_TYPE
                // SYSTEM_LDAP_TYPE -> SERVER_TYPE
                case SYSTEM_LDAP_SCOPED_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.CBP_OI_SECURITY_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_SYSTEM_EXT_NS, ModelDefinition.CBP_OI_SECURITY_TYPE, "1.1.0")
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getUnscopedType = {
                        return ModelDefinition.SERVER_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_SYSTEM_EXT_NS, ModelDefinition.SERVER_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification,
                            childHierarchicalPrimaryTypeSpecificationMap2 as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                // CBP_OI_SECURITY_TYPE -> CBP_OI_TLS_TYPE
                // CBP_OI_SECURITY_TYPE -> SIMPLE_AUTHENTICATED_TYPE
                case ModelDefinition.CBP_OI_SECURITY_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.CBP_OI_TLS_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_SYSTEM_EXT_NS, ModelDefinition.CBP_OI_TLS_TYPE, "1.1.0")
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getUnscopedType = {
                        return ModelDefinition.SIMPLE_AUTHENTICATED_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_SYSTEM_EXT_NS, ModelDefinition.SIMPLE_AUTHENTICATED_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification,
                            childHierarchicalPrimaryTypeSpecificationMap2 as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                // TCP_TYPE -> LDAPS_TYPE
                // TCP_TYPE -> TCP_LDAP_TYPE
                case ModelDefinition.TCP_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.LDAPS_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_SYSTEM_EXT_NS, ModelDefinition.LDAPS_TYPE, "1.1.0")
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getUnscopedType = {
                        return ModelDefinition.LDAP_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_SYSTEM_EXT_NS, TCP_LDAP_SCOPED_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification,
                            childHierarchicalPrimaryTypeSpecificationMap2 as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                // SERVER_TYPE -> TCP_TYPE
                case ModelDefinition.SERVER_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.TCP_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_SYSTEM_EXT_NS, ModelDefinition.TCP_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                // AUTHENTICATION_TYPE  -> USER_TYPE
                case ModelDefinition.AUTHENTICATION_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = { return ModelDefinition.USER_TYPE }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_SYSTEM_NS, ModelDefinition.USER_TYPE, "2019.11.20")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                // USER_TYPE -> AUTHORIZED_KEY_TYPE
                case ModelDefinition.USER_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = { return ModelDefinition.AUTHORIZED_KEY_TYPE }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_SYSTEM_NS, ModelDefinition.AUTHORIZED_KEY_TYPE, "2019.11.20")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                case ModelDefinition.KEYSTORE_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.ASYMMETRIC_KEYS_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_NS, ModelDefinition.ASYMMETRIC_KEYS_TYPE, "2019.11.20")
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getUnscopedType = {
                        return ModelDefinition.CMP_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_EXT_NS, KEYSTORE_CMP_SCOPED_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification,
                            childHierarchicalPrimaryTypeSpecificationMap2 as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                case KEYSTORE_CMP_SCOPED_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.CERTIFICATE_AUTHORITIES_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_EXT_NS, ModelDefinition.CERTIFICATE_AUTHORITIES_TYPE, "1.1.0")
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getUnscopedType = {
                        return ModelDefinition.CMP_SERVER_GROUPS_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_EXT_NS, ModelDefinition.CMP_SERVER_GROUPS_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification,
                            childHierarchicalPrimaryTypeSpecificationMap2 as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                case ModelDefinition.CERTIFICATE_AUTHORITIES_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.CERTIFICATE_AUTHORITY_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_EXT_NS, ModelDefinition.CERTIFICATE_AUTHORITY_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                case ModelDefinition.CMP_SERVER_GROUPS_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.CMP_SERVER_GROUP_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_EXT_NS, ModelDefinition.CMP_SERVER_GROUP_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                case ModelDefinition.CMP_SERVER_GROUP_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.CMP_SERVER_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_EXT_NS, ModelDefinition.CMP_SERVER_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                case ModelDefinition.ASYMMETRIC_KEYS_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.ASYMMETRIC_KEY_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_NS, ModelDefinition.ASYMMETRIC_KEY_TYPE, "2019.11.20")
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getUnscopedType = {
                        return ModelDefinition.CMP_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_EXT_NS, ASYMMETRIC_KEYS_CMP_SCOPED_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification,
                            childHierarchicalPrimaryTypeSpecificationMap2 as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                case ModelDefinition.ASYMMETRIC_KEY_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.KEYSTORE_CERTIFICATES_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_NS, ModelDefinition.KEYSTORE_CERTIFICATES_TYPE, "2019.11.20")
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getUnscopedType = {
                        return ModelDefinition.CMP_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap2.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_EXT_NS, ASYMMETRIC_KEY_CMP_SCOPED_TYPE, "1.1.0")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification,
                            childHierarchicalPrimaryTypeSpecificationMap2 as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                case ModelDefinition.KEYSTORE_CERTIFICATES_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.KEYSTORE_CERTIFICATE_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_KEYSTORE_NS, ModelDefinition.KEYSTORE_CERTIFICATE_TYPE, "2019.11.20")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                case ModelDefinition.TRUSTSTORE_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.TRUSTSTORE_CERTIFICATES_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_TRUSTSTORE_NS, ModelDefinition.TRUSTSTORE_CERTIFICATES_TYPE, "2019.11.20")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                case ModelDefinition.TRUSTSTORE_CERTIFICATES_TYPE:
                    childHierarchicalPrimaryTypeSpecificationMap.getUnscopedType = {
                        return ModelDefinition.TRUSTSTORE_CERTIFICATE_TYPE
                    }
                    childHierarchicalPrimaryTypeSpecificationMap.getModelInfo = {
                        return new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, ModelDefinition.CBP_OI_TRUSTSTORE_NS, ModelDefinition.TRUSTSTORE_CERTIFICATE_TYPE, "2019.11.20")
                    }
                    hierarchicalPrimaryTypeSpecificationMap.getAllChildTypes = {
                        return [
                            childHierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
                        ]
                    }
                    break
                default:
                    break
            }

            return hierarchicalPrimaryTypeSpecificationMap as HierarchicalPrimaryTypeSpecification
        }

        @Override
        <T extends EModelSpecification> Collection<T> getEModelSpecifications(String modelUrn, Class<T> modelSpecificationClass) {
            return null
        }

        @Override
        <T extends EModelSpecification> Collection<T> getEModelSpecifications(String schemaName, String modelNamespace, String modelName, String modelVersion, Class<T> modelSpecificationClass) {
            return null
        }

        @Override
        <T extends EModelSpecification> T getEModelSpecification(String modelUrn, Class<T> modelSpecificationClass) throws UnknownModelException, MatchingClassNotFoundException, UnknownSchemaException {
            return null
        }

        @Override
        <T extends EModelSpecification> Collection<T> getEModelSpecifications(String modelUrn, Class<T> modelSpecificationClass, Target target) {
            return null
        }

        @Override
        <T extends EModelSpecification> Collection<T> getEModelSpecifications(String schemaName, String modelNamespace, String modelName, String modelVersion, Class<T> modelSpecificationClass, Target target) {
            return null
        }

        @Override
        <T extends EModelSpecification> T getEModelSpecification(String modelUrn, Class<T> modelSpecificationClass, Target target) throws UnknownModelException, MatchingClassNotFoundException, UnknownSchemaException, InvalidModelForTargetTypeException {
            return null
        }
    }
}
