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
package com.ericsson.oss.services.security.nscs.util

import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeSpecification
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo
import com.ericsson.oss.itpf.modeling.modelservice.ModelService
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelMetaInformation
import com.ericsson.oss.itpf.modeling.modelservice.typed.TypedModelAccess
import com.ericsson.oss.itpf.modeling.modelservice.typed.capabilities.CapabilityInformation
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.MimMappedTo
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeVersionInformation
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants

class NscsRestCdiSpecification extends CdiSpecification {

    public static final String VDU_TARGET_TYPE = "vDU"
    public static final String VDU_TARGET_MODEL_IDENTITY = "0.5.1"
    public static final String VDU_TOP_NS = "VduTop"
    public static final String SHARED_CNF_TARGET_TYPE = "Shared-CNF"
    public static final String SHARED_CNF_TARGET_MODEL_IDENTITY = "1.1"

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
        getModelsFromUrn : { String schema, String namespace, String model, String version ->
            if (model == "Ikev2PolicyProfile" && namespace == "RtnIkev2PolicyProfile") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if (model == "NodeCredential" && namespace == "RcsCertM") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if ((model == "keystore" || model == "asymmetric-keys" || model == "asymmetric-key"
            || model == "certificates" || model == "certificate") && namespace == "urn:ietf:params:xml:ns:yang:ietf-keystore") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if ((model == "keystore\$\$cmp" || model == "certificate-authorities" || model == "certificate-authority"
            || model == "cmp-server-groups" || model == "cmp-server-group" || model == "cmp-server"
            || model == "asymmetric-keys\$\$cmp" || model == "asymmetric-key\$\$cmp") && namespace == "urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if ((model == "truststore" || model == "certificates" || model == "certificate") && namespace == "urn:ietf:params:xml:ns:yang:ietf-truststore") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if (model == "system" && namespace == "urn:ietf:params:xml:ns:yang:ietf-system") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if ((model == "system\$\$ldap" || model == "security" || model == "simple-authenticated"
            || model == "tls" || model == "server" || model == "tcp" || model == "ldaps"
            || model == "tcp\$\$ldap") && namespace == "urn:rdns:com:ericsson:oammodel:ericsson-system-ext") {
                return Arrays.asList(new ModelInfo(SchemaConstants.DPS_PRIMARYTYPE, namespace, model, version))
            } else if (model == "Security" && namespace == "ERBS_NODE_MODEL") {
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
                return Arrays.asList(mimQ2Ikev2PolicyProfile, mimQ2NodeCredential)
            } else if (tMI == "20.Q3-R13A40") {
                return Arrays.asList(mimQ3Ikev2PolicyProfile, mimQ3NodeCredential)
            } else {
                throw new IllegalArgumentException()
            }
        },
        getReleases : { String tMI ->
            if (tMI == "20.Q2-R4A24") {
                Set<String> releases = new HashSet<>()
                releases.add("20.Q2")
                return releases
            } else if (tMI == "20.Q3-R13A40") {
                Set<String> releases = new HashSet<>()
                releases.add("20.Q3")
                return releases
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
        },
        getReleases : { String tMI ->
            if (tMI == VDU_TARGET_MODEL_IDENTITY || tMI == SHARED_CNF_TARGET_MODEL_IDENTITY) {
                Set<String> releases = new HashSet<>()
                releases.add(VDU_TARGET_MODEL_IDENTITY)
                return releases
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
        },
        getReleases : { String tMI ->
            if (tMI == "20.Q1-J.4.555") {
                Set<String> releases = new HashSet<>()
                releases.add("20.Q1")
                return releases
            } else {
                throw new IllegalArgumentException()
            }
        }
    ] as TargetTypeVersionInformation

    @ImplementationInstance
    TargetTypeInformation targetTypeInformation = [
        getTargetTypeVersionInformation : { String targetCategory, String targetType ->
            if (targetType == "RadioNode") {
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
            if (targetType == "RadioNode") {
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
        }
    ] as TargetTypeInformation

    @ImplementationInstance
    CapabilityInformation capabilityInformation = [
        getCapabilityValue : { String targetCategory, String targetType, String function, String capabilityName, String capabilitySupportModelVersion ->
            if (targetType == VDU_TARGET_TYPE || targetType == SHARED_CNF_TARGET_TYPE) {
                if (capabilityName == "momType") {
                    return "EOI"
                }
            } else if (targetType == "RadioNode") {
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
    TypedModelAccess typedModelAccess = [
        getModelInformation : { Class clazz ->
            if (clazz == CapabilityInformation.class) {
                return capabilityInformation
            }
            return targetTypeInformation
        },
        getEModelSpecification : { ModelInfo modelInfo, _ ->
            return primaryTypeSpecification
        }
    ] as TypedModelAccess

    @ImplementationInstance
    ModelService modelService = [
        getTypedAccess : {
            return typedModelAccess
        },
        getModelMetaInformation : {
            return modelMetaInformation
        }
    ] as ModelService
}
