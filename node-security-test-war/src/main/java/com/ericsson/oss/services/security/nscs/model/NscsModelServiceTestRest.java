package com.ericsson.oss.services.security.nscs.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.oss.itpf.datalayer.dps.modeling.modelservice.typed.persistence.primarytype.PrimaryTypeSpecification;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.ModelService;
import com.ericsson.oss.itpf.modeling.modelservice.exception.UnknownModelException;
import com.ericsson.oss.itpf.modeling.modelservice.exception.UnknownSchemaException;
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelMetaInformation;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.exception.MatchingClassNotFoundException;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.MimMappedTo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.Target;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeVersionInformation;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.services.security.nscs.util.EServiceHolder;
import com.ericsson.oss.services.security.nscs.util.NscsRestResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("modelservice/")
@SuppressWarnings("all")
public class NscsModelServiceTestRest {

    @Inject
    Logger logger;

    @Inject
    private ModelService modelService;

    @Inject
    private EServiceHolder serviceHolder;

    @GET
    @Path("getTargetTypeVersionInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNscsTargetInfo(@QueryParam("nscs") final String isNscsModelService, @QueryParam("targetCategory") final String targetCategory,
            @QueryParam("targetType") final String targetType, @QueryParam("targetModelIdentity") final String targetModelIdentity) {
        final String source = isNscsModelService != null ? "NSCS Model Service" : "Model Service";
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get TargetTypeVersionInfo for targetCategory '" + targetCategory + "', targetType '" + targetType
                + "' and targetModelIdentity '" + targetModelIdentity + "' from nscs " + source);
        try {
            if (targetCategory == null && targetType == null) {
                restResult.setResponse(
                        isNscsModelService != null ? serviceHolder.getNscsModelService().getTargetTypeDetails() : getTargetTypeDetails());
            } else if (targetType == null) {
                restResult.setResponse(isNscsModelService != null ? serviceHolder.getNscsModelService().getTargetTypeDetails(targetCategory)
                        : getTargetTypeDetails(targetCategory));
            } else if (targetCategory == null) {
                restResult
                        .setResponse(isNscsModelService != null ? serviceHolder.getNscsModelService().getTargetTypeDetails(targetCategory, targetType)
                                : getTargetTypeDetails(targetCategory, targetType));
            } else {
                if (targetModelIdentity == null) {
                    restResult.setResponse(
                            isNscsModelService != null ? serviceHolder.getNscsModelService().getTargetTypeVersionDetails(targetCategory, targetType)
                                    : getTargetTypeVersionDetails(targetCategory, targetType));
                } else {
                    restResult.setResponse(isNscsModelService != null
                            ? serviceHolder.getNscsModelService().getTargetTypeVersionDetails(targetCategory, targetType, targetModelIdentity)
                            : getTargetTypeVersionDetails(targetCategory, targetType, targetModelIdentity));
                }
            }
            result = mapper.writeValueAsString(restResult);

        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getTmiFromMim")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTargetModelIdentityFromMimVersion(@QueryParam("targetCategory") final String targetCategory,
            @QueryParam("targetType") final String targetType, @QueryParam("mimVersion") final String mimVersion) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get target model identity for targetCategory '" + targetCategory + "', targetType '" + targetType
                + "', mimVersion '" + mimVersion + "' from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            final Map<String, Object> responses = new HashMap<>();
            responses.put("Target model identity",
                    serviceHolder.getNscsModelService().getTargetModelIdentityFromMimVersion(targetCategory, targetType, mimVersion));
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getNesModelInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNesModelInfo(@QueryParam("targetCategory") final String targetCategory, @QueryParam("targetType") final String targetType,
            @QueryParam("targetModelIdentity") final String targetModelIdentity) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get NetworkElementSecurity MO ModelInfo for targetCategory '" + targetCategory + "', targetType '" + targetType
                + "' and targetModelIdentity '" + targetModelIdentity + "' from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            final Map<String, Object> responses = new HashMap<>();
            responses.put("NES ModelInfo",
                    getPrimaryTypeModelInfo(targetCategory, targetType, targetModelIdentity, ModelDefinition.NE_SEC_NS, "NetworkElementSecurity"));
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getMimModelInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMimModelInfo(@QueryParam("targetCategory") final String targetCategory, @QueryParam("targetType") final String targetType,
            @QueryParam("targetModelIdentity") final String targetModelIdentity, @QueryParam("namespace") final String namespace,
            @QueryParam("type") final String type) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription(
                "Get MIM ModelInfo for targetCategory '" + targetCategory + "', targetType '" + targetType + "', targetModelIdentity '"
                        + targetModelIdentity + "', reference namespace '" + namespace + "' and type '" + type + "' from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            final Map<String, Object> responses = new HashMap<>();
            responses.put("MIM modelInfo",
                    serviceHolder.getNscsModelService().getMimPrimaryTypeModelInfo(targetCategory, targetType, targetModelIdentity, namespace, type));
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getMostAppropriateTMI")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMostAppropriateTMI(@QueryParam("targetCategory") final String targetCategory,
            @QueryParam("targetType") final String targetType, @QueryParam("targetModelIdentity") final String targetModelIdentity,
            @QueryParam("urn") final String modelUrn) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get Most Appropriate TMI for targetCategory '" + targetCategory + "', targetType '" + targetType
                + "', targetModelIdentity '" + targetModelIdentity + "' and urn '" + modelUrn + "' from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            final Map<String, Object> responses = new HashMap<>();
            if (targetModelIdentity != null) {
                final String version = serviceHolder.getNscsModelService().getCapabilitySupportModelVersion(targetCategory, targetType,
                        targetModelIdentity, "NSCS");
                responses.put("Most Appropriate TMI [oss_capabilitysupport version " + version + "]", serviceHolder.getNscsModelService()
                        .getMostAppropriateTmiForTarget(targetCategory, targetType, targetModelIdentity, modelUrn));
            } else {
                for (final String tMI : serviceHolder.getNscsModelService().getTargetModelIdentities(targetCategory, targetType)) {
                    final String version = serviceHolder.getNscsModelService().getCapabilitySupportModelVersion(targetCategory, targetType, tMI,
                            "NSCS");
                    responses.put("Most Appropriate TMI for targetModelIdentity '" + tMI + "' [oss_capabilitysupport version " + version + "]",
                            serviceHolder.getNscsModelService().getMostAppropriateTmiForTarget(targetCategory, targetType, tMI, modelUrn));
                }
            }
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("isKSandEMSupported")
    @Produces(MediaType.APPLICATION_JSON)
    public Response areKLandEMSupported(@QueryParam("targetCategory") final String targetCategory, @QueryParam("targetType") final String targetType,
            @QueryParam("targetModelIdentity") final String targetModelIdentity) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get isKSandEMSupported for targetCategory '" + targetCategory + "', targetType '" + targetType
                + "' and targetModelIdentity '" + targetModelIdentity + "' from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            final Map<String, Object> responses = new HashMap<>();
            responses.put("isKSandEMSupported",
                    serviceHolder.getNscsModelService().isKSandEMSupported(targetCategory, targetType, targetModelIdentity));
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("isCertificateAuthorityDnSupported")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isCertificateAuthorityDnSupported(@QueryParam("targetCategory") final String targetCategory,
            @QueryParam("targetType") final String targetType, @QueryParam("targetModelIdentity") final String targetModelIdentity) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get isCertificateAuthorityDnSupported for targetCategory '" + targetCategory + "', targetType '" + targetType
                + "' and targetModelIdentity '" + targetModelIdentity + "' from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            final Map<String, Object> responses = new HashMap<>();
            responses.put("isCertificateAuthorityDnSupported",
                    serviceHolder.getNscsModelService().isCertificateAuthorityDnSupported(targetCategory, targetType, targetModelIdentity));
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            logger.error("Exception: {}", e);
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                logger.error("IOException: {}", e1);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getSupportedModel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSupportedModel(@QueryParam("targetCategory") final String targetCategory, @QueryParam("targetType") final String targetType,
            @QueryParam("targetModelIdentity") final String targetModelIdentity, @QueryParam("schema") final String schema,
            @QueryParam("namespace") final String namespace, @QueryParam("model") final String modelName) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get URN of supported model with schema '" + schema + "', namespace '" + namespace + "' and model '" + modelName
                + "' for targetCategory '" + targetCategory + "', targetType '" + targetType + "', targetModelIdentity '" + targetModelIdentity
                + "', from 'Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            final Map<String, Object> responses = new HashMap<>();
            final TargetTypeInformation targetTypeInfo = modelService.getTypedAccess().getModelInformation(TargetTypeInformation.class);
            String modelUrn = null;
            // Target Type Version Information
            final TargetTypeVersionInformation targetTypeVersionInfo = targetTypeInfo.getTargetTypeVersionInformation(targetCategory, targetType);
            modelUrn = targetTypeVersionInfo.getSupportedModel(targetModelIdentity, schema, namespace, modelName);
            responses.put("URN", modelUrn);
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getModelsFromUrn")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModelsFromUrn(@QueryParam("urn") final String urn) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get model info from urn '" + urn + "', from 'Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            final Map<String, Object> responses = new HashMap<>();
            final ModelMetaInformation modelMetaInfo = modelService.getModelMetaInformation();
            // Latest Models From URN
            final Collection<ModelInfo> latestModelsFromUrn = modelMetaInfo.getLatestModelsFromUrn(urn);
            responses.put("Latest Models from URN", latestModelsFromUrn);

            final Collection<ModelInfo> modelsFromUrn = modelMetaInfo.getModelsFromUrn(urn);
            responses.put("Models from URN", modelsFromUrn);
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getLatestVersionOfNormalizedModel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatestVersionOfNormalizedModel(@QueryParam("model") final String model) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get latest version of normalized model '" + model + "', from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            final Map<String, Object> responses = new HashMap<>();
            responses.put("Model info", serviceHolder.getNscsModelService().getLatestVersionOfNormalizedModel(model));
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getLatestVersionOfModel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatestVersionOfModel(@QueryParam("schema") final String schema, @QueryParam("namespace") final String namespace,
            @QueryParam("model") final String modelName) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Latest version of model with schema '" + schema + "', namespace '" + namespace + "' and model '" + modelName
                + "' from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            final Map<String, Object> responses = new HashMap<>();
            responses.put("Model info", serviceHolder.getNscsModelService().getLatestVersionOfModel(schema, namespace, modelName));
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("mockStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCapabilityModelServiceMockStatus() {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get NSCS Capability Model mock status");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            final Map<String, Object> responses = new HashMap<>();
            responses.put("NSCS Capability Model mock status", serviceHolder.getNscsModelService().isMockCapabilityModelUsed());
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getCapabilities")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCapabilities(@QueryParam("function") final String function, @QueryParam("name") final String capabilityName) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription(
                "Get all capabilitysupport for capability name '" + capabilityName + "' of function '" + function + "' from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            restResult.setResponse(serviceHolder.getNscsModelService().getCapabilities(function, capabilityName));
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error : [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getCapabilitySupport")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCapabilitySupport(@QueryParam("targetCategory") final String targetCategory, @QueryParam("targetType") final String targetType,
            @QueryParam("targetModelIdentity") final String targetModelIdentity, @QueryParam("function") final String function) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get all capabilitysupport of function '" + function + "' for targetCategory '" + targetCategory + "', targetType '"
                + targetType + "' and targetModelIdentity '" + targetModelIdentity + "' from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        final Map<String, Object> responses = new HashMap<>();
        try {
            final String version = serviceHolder.getNscsModelService().getCapabilitySupportModelVersion(targetCategory, targetType,
                    targetModelIdentity, function);
            responses.put("capabilitysupport version", version);
            responses.put("capabilitysupport values",
                    serviceHolder.getNscsModelService().getCapabilities(targetCategory, targetType, function, version));
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error : [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getCapabilitySupportVersion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCapabilitySupportVersion(@QueryParam("targetCategory") final String targetCategory,
            @QueryParam("targetType") final String targetType, @QueryParam("function") final String function,
            @QueryParam("version") final String version) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get all capabilitysupport of function '" + function + "' version '" + version + "' for targetCategory '"
                + targetCategory + "' and targetType '" + targetType + "' from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            restResult.setResponse(serviceHolder.getNscsModelService().getCapabilities(targetCategory, targetType, function, version));
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error : [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getCapabilityValue")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCapabilityValue(@QueryParam("targetCategory") final String targetCategory, @QueryParam("targetType") final String targetType,
            @QueryParam("targetModelIdentity") final String targetModelIdentity, @QueryParam("function") final String function,
            @QueryParam("name") final String capabilityName) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription(
                "Get value for capability name '" + capabilityName + "' of function '" + function + "' for targetCategory '" + targetCategory
                        + "', targetType '" + targetType + "' and targetModelIdentity '" + targetModelIdentity + "' from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        final Map<String, Object> responses = new HashMap<>();
        try {
            responses.put("capabilitysupport version",
                    serviceHolder.getNscsModelService().getCapabilitySupportModelVersion(targetCategory, targetType, targetModelIdentity, function));
            responses.put("capability value", serviceHolder.getNscsModelService().getCapabilityValue(targetCategory, targetType, targetModelIdentity,
                    function, capabilityName));
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getCapabilityDefault")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCapabilityDefault(@QueryParam("function") final String function, @QueryParam("name") final String capabilityName) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription(
                "Get default value for capability name '" + capabilityName + "' of function '" + function + "' from 'NSCS Model Service'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            restResult.setResponse(serviceHolder.getNscsModelService().getDefaultValue(function, capabilityName));
            result = mapper.writeValueAsString(restResult);
        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getTargetPO")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTargetPO(@QueryParam("fdn") final String fdn) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription("Get Target PO for MO of FDN '" + fdn + "'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            final Map<String, Object> responses = new HashMap<>();
            responses.put("targetPO", serviceHolder.getNscsModelService().getTargetPO(fdn));
            restResult.setResponse(responses);
            result = mapper.writeValueAsString(restResult);

        } catch (final Exception e) {
            restResult.setResponse("Error: [" + e.getClass().getCanonicalName() + "] " + e.getMessage());
            try {
                result = mapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e1.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    private Map<String, Object> getTargetTypeDetails() {
        final Map<String, Object> responses = new HashMap<>();
        final TargetTypeInformation targetTypeInfo = modelService.getTypedAccess().getModelInformation(TargetTypeInformation.class);
        final Collection<String> targetCategories = targetTypeInfo.getTargetCategories();
        responses.put("Target categories", targetCategories);
        return responses;
    }

    private Map<String, Object> getTargetTypeDetails(final String targetCategory) {
        final Map<String, Object> responses = new HashMap<>();
        final TargetTypeInformation targetTypeInfo = modelService.getTypedAccess().getModelInformation(TargetTypeInformation.class);
        final Collection<String> targetTypes = targetTypeInfo.getTargetTypes(targetCategory);
        responses.put("Target types", targetTypes);
        return responses;
    }

    private Map<String, Object> getTargetTypeDetails(final String targetCategory, final String targetType) {
        final Map<String, Object> responses = new HashMap<>();
        final TargetTypeInformation targetTypeInfo = modelService.getTypedAccess().getModelInformation(TargetTypeInformation.class);
        final Collection<String> targetCategories = new ArrayList<>();
        final Collection<String> supportedTargetCategories = targetTypeInfo.getTargetCategories();
        for (final String supportedTargetCategory : supportedTargetCategories) {
            final Collection<String> targetTypes = targetTypeInfo.getTargetTypes(supportedTargetCategory);
            if (targetTypes != null && targetTypes.contains(targetType)) {
                targetCategories.add(supportedTargetCategory);
            }
        }
        responses.put("Target categories", targetCategories);
        return responses;
    }

    private Map<String, Object> getTargetTypeVersionDetails(final String targetCategory, final String targetType) {
        final Map<String, Object> responses = new HashMap<>();
        final TargetTypeInformation targetTypeInfo = modelService.getTypedAccess().getModelInformation(TargetTypeInformation.class);
        final TargetTypeVersionInformation targetTypeVersionInfo = targetTypeInfo.getTargetTypeVersionInformation(targetCategory, targetType);
        final String connectivityInfoMoType = targetTypeInfo.getConnectivityInfoMoType(targetCategory, targetType);
        responses.put("ConnectivityInfo MO type", connectivityInfoMoType);
        final String platform = targetTypeInfo.getPlatform(targetCategory, targetType);
        responses.put("Platform", platform);
        final String rootMoType = targetTypeInfo.getRootMoType(targetCategory, targetType);
        responses.put("Root MO type", rootMoType);
        final String targetTypeName = targetTypeVersionInfo.getTargetType();
        responses.put("Name of targetType", targetTypeName);
        final Collection<String> targetModelIdentities = targetTypeVersionInfo.getTargetModelIdentities();
        responses.put("Target model identities", targetModelIdentities);
        return responses;
    }

    private Map<String, Object> getTargetTypeVersionDetails(final String targetCategory, final String targetType, final String targetModelIdentity) {
        final Map<String, Object> responses = new HashMap<>();
        final TargetTypeInformation targetTypeInfo = modelService.getTypedAccess().getModelInformation(TargetTypeInformation.class);
        final TargetTypeVersionInformation targetTypeVersionInfo = targetTypeInfo.getTargetTypeVersionInformation(targetCategory, targetType);
        final Collection<String> releases = targetTypeVersionInfo.getReleases(targetModelIdentity);
        responses.put("Releases", releases);
        final Collection<String> supportedModels = targetTypeVersionInfo.getSupportedModels(targetModelIdentity);
        responses.put("Supported models", supportedModels);
        final Collection<MimMappedTo> mimsMappedTo = targetTypeVersionInfo.getMimsMappedTo(targetModelIdentity);
        responses.put("MIMs mapped to", mimsMappedTo);
        return responses;
    }

    private Map<String, Object> getPrimaryTypeModelInfo(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String namespace, final String type) {
        final Map<String, Object> responses = new HashMap<>();
        final ModelInfo modelInfo = modelService.getModelMetaInformation().getLatestVersionOfModel(SchemaConstants.DPS_PRIMARYTYPE, namespace, type);
        final Target target = new Target(targetCategory, targetType, null, targetModelIdentity);
        PrimaryTypeSpecification primaryTypeSpec = null;
        try {
            primaryTypeSpec = modelService.getTypedAccess().getEModelSpecification(modelInfo, PrimaryTypeSpecification.class, target);
        } catch (UnknownModelException | MatchingClassNotFoundException | UnknownSchemaException e) {
            primaryTypeSpec = modelService.getTypedAccess().getEModelSpecification(modelInfo, PrimaryTypeSpecification.class);
        }
        final Collection<String> mandatoryAttrs = primaryTypeSpec.getAllMandatoryMemberNames();
        responses.put("ModelInfo", modelInfo);
        responses.put("Mandatory Attributes", mandatoryAttrs);
        return responses;
    }

}
