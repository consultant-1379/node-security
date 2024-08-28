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

import com.ericsson.oss.itpf.modeling.modelservice.ModelService;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.MimMappedTo;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeVersionInformation;
import com.ericsson.oss.services.security.nscs.util.NscsRestResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("model/")
public class NscsModelServiceRest {

    @Inject
    private Logger logger;

    @Inject
    private NscsModelManager nscsModelManager;

    @Inject
    private ModelService modelService;

    /**
     * Get target info for the given target category, type and model identity.
     * 
     * @param targetCategory
     *            the target category.
     * @param targetType
     *            the target type.
     * @param targetModelIdentity
     *            the target model identity.
     * @return the target info.
     * @throws JsonProcessingException
     */
    @GET
    @Path("getTargetInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTargetInfo(@QueryParam("targetCategory") final String targetCategory, @QueryParam("targetType") final String targetType,
            @QueryParam("targetModelIdentity") final String targetModelIdentity)
            throws JsonProcessingException {

        final String inputParams = String.format("GET target info: targetCategory %s targetType %s targetModelIdentity %s", targetCategory,
                targetType, targetModelIdentity);

        logger.debug(inputParams);

        final ObjectMapper objectMapper = new ObjectMapper();
        final NscsRestResult restResult = new NscsRestResult(inputParams, null);
        String result;
        try {
            final Object obj = getTargetInformation(targetCategory, targetType, targetModelIdentity);
            restResult.setResponse(obj);
            result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(restResult);
            logger.debug(result);
        } catch (final Exception e) {
            final String errorMsg = String.format("Error: [%s] %s", e.getClass().getCanonicalName(), e.getMessage());
            restResult.setResponse(errorMsg);
            try {
                result = objectMapper.writeValueAsString(restResult);
            } catch (final IOException e1) {
                final String errorMsg1 = String.format("Error: [%s] %s", e1.getClass().getCanonicalName(), e1.getMessage());
                logger.error(errorMsg1, e1);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMsg1).build();
            }
            logger.error(errorMsg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Get model info of a given MO type and namespace for the given target category, type and model identity.
     * 
     * @param targetCategory
     *            the target category.
     * @param targetType
     *            the target type.
     * @param targetModelIdentity
     *            the target model identity.
     * @param namespace
     *            the namespace.
     * @param type
     *            the MO type.
     * @return the model info.
     * @throws JsonProcessingException
     */
    @GET
    @Path("getModelInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getModelInfo(@QueryParam("targetCategory") final String targetCategory, @QueryParam("targetType") final String targetType,
            @QueryParam("targetModelIdentity") final String targetModelIdentity, @QueryParam("namespace") final String namespace,
            @QueryParam("type") final String type) throws JsonProcessingException {

        final String inputParams = String.format("GET model info: targetCategory %s targetType %s targetModelIdentity %s namespace %s type %s",
                targetCategory, targetType, targetModelIdentity, namespace, type);

        logger.debug(inputParams);

        final NscsRestResult restResult = new NscsRestResult(inputParams, null);
        final Object obj = nscsModelManager.getModelInfo(targetCategory, targetType, targetModelIdentity, namespace, type);
        restResult.setResponse(obj);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(restResult);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Get target PO for an MO of given FDN.
     * 
     * @param fdn
     *            the MO FDN.
     * @return the target PO.
     * @throws JsonProcessingException
     */
    @GET
    @Path("getTargetPO")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTargetPO(@QueryParam("fdn") final String fdn) throws JsonProcessingException {

        final String inputParams = String.format("GET target PO: fdn %s", fdn);

        logger.debug(inputParams);

        final NscsRestResult restResult = new NscsRestResult(inputParams, null);
        final Object obj = nscsModelManager.getTargetPO(fdn);
        restResult.setResponse(obj);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(restResult);
        logger.debug(result);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    private Object getTargetInformation(final String targetCategory, final String targetType, final String targetModelIdentity) {
        if (targetCategory == null && targetType == null) {
            return getTargetCategories();
        } else if (targetType == null) {
            return getTargetTypes(targetCategory);
        } else if (targetCategory == null) {
            return getTargetCategories(targetType);
        } else {
            if (targetModelIdentity == null) {
                return getTargetTypeVersionDetails(targetCategory, targetType);
            } else {
                return getTargetTypeVersionDetails(targetCategory, targetType, targetModelIdentity);
            }
        }
    }

    private Map<String, Object> getTargetCategories() {
        final Map<String, Object> responses = new HashMap<>();
        final TargetTypeInformation targetTypeInfo = modelService.getTypedAccess().getModelInformation(TargetTypeInformation.class);
        final Collection<String> targetCategories = targetTypeInfo.getTargetCategories();
        responses.put("Target categories", targetCategories);
        return responses;
    }

    private Map<String, Object> getTargetTypes(final String targetCategory) {
        final Map<String, Object> responses = new HashMap<>();
        final TargetTypeInformation targetTypeInfo = modelService.getTypedAccess().getModelInformation(TargetTypeInformation.class);
        final Collection<String> targetTypes = targetTypeInfo.getTargetTypes(targetCategory);
        responses.put("Target types", targetTypes);
        return responses;
    }

    private Map<String, Object> getTargetCategories(final String targetType) {
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
        final Collection<MimMappedTo> mimsMappedTo = targetTypeVersionInfo.getMimsMappedTo(targetModelIdentity);
        responses.put("MIMs mapped to", mimsMappedTo);
        return responses;
    }
}
