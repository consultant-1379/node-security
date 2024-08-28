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
package com.ericsson.oss.services.security.nscs.iscf;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.IscfResponse;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityNotFoundException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.EntityServiceException;
import com.ericsson.oss.itpf.security.pki.manager.exception.entity.endentity.otp.OTPExpiredException;
import com.ericsson.oss.services.security.nscs.util.EServiceHolder;
import com.ericsson.oss.services.security.nscs.util.NscsRestResult;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST used by Arquillian tests to verify ISCF service.
 */
@Path("iscf/")
public class IscfTestRest {

    @Inject
    private Logger logger;

    @Inject
    EServiceHolder holder;

    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getIscfTest() {
        logger.info("ISCF REST Test received.");
        return Response.status(Response.Status.OK).entity("ISCF REST TEST OK.").build();
    }

    @GET
    @Path("testotp")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getOtpValidStatus() {
        logger.info("ISCF REST testotp received.");
        boolean result;
        try {
            result = holder.getEntityManagementService().isOTPValid("DummyEntity", "TestOTP");
        } catch (OTPExpiredException | EntityNotFoundException | EntityServiceException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to get OTP: " + e.getMessage()).build();

        }
        return Response.status(Response.Status.OK).entity("ISCF REST TEST OTP returns " + result).build();
    }

    @GET
    @Path("seclevel")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getIscfXml(@QueryParam("wanted") final SecurityLevel wanted, @QueryParam("minimum") final SecurityLevel minimum,
            @QueryParam("node") final String node, @QueryParam("logical") final String logicalName,
            @QueryParam("mode") final EnrollmentMode wantedEnrollmentMode, @QueryParam("version") final String modelIdentifier,
            @QueryParam("nodeType") final String nodeType) {
        logger.info(
                "RestResource invoked. Wanted security level: {}, Minimum security level: {}, Node name: {},Logical name: {}, enrollmentMode: {}, modelIdentifier: {}, nodeType: {}",
                wanted, minimum, node, logicalName, wantedEnrollmentMode, modelIdentifier, nodeType);
        try {

            final ModelIdentifierType modelIdType = "ERBS".equals(nodeType) ? ModelIdentifierType.MIM_VERSION : ModelIdentifierType.OSS_IDENTIFIER;
            final NodeModelInformation modelInfo = new NodeModelInformation(modelIdentifier, modelIdType, nodeType);
            final IscfResponse iscfResponse = holder.getIscfService().generate(logicalName, node, wanted, minimum, wantedEnrollmentMode, modelInfo);
            final String xml = new String(iscfResponse.getIscfContent(), "UTF-8");
            return Response.ok().entity(xml).build();

        } catch (final Exception e) {
            logger.warn("Failed to generate ISCF XML via RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to generate ISCF XML: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("ipsec")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getIscfIpsecXml(@QueryParam("node") final String node, @QueryParam("logical") final String logicalName,
            @QueryParam("subjectAltName") final String subjectAltName,
            @QueryParam("subjectAltNameType") final SubjectAltNameFormat subjectAltNameFormat,
            @QueryParam("ipsecAreas") final List<String> ipsecAreas, @QueryParam("mode") final EnrollmentMode wantedEnrollmentMode,
            @QueryParam("version") final String modelIdentifier, @QueryParam("nodeType") final String nodeType) {
        logger.info(
                "RestResource for ipsec invoked.  Node name: {}, Logical name: {}, subjectAltName {},subjectAltNameType {}, ipsecArea {}, enrollmentMode {}, modelIdentifier {}, nodeType {}",
                node, logicalName, subjectAltName, subjectAltNameFormat, ipsecAreas.toString(), wantedEnrollmentMode, modelIdentifier, nodeType);
        try {

            final Set<IpsecArea> ipsecAreasForResult = new HashSet<>();

            for (final String ipsecArea : ipsecAreas) {
                if (ipsecArea.equalsIgnoreCase("traffic")) {
                    ipsecAreasForResult.add(IpsecArea.TRANSPORT);
                }
                if (ipsecArea.equalsIgnoreCase("om")) {
                    ipsecAreasForResult.add(IpsecArea.OM);
                }
            }

            final ModelIdentifierType modelIdType = "ERBS".equals(nodeType) ? ModelIdentifierType.MIM_VERSION : ModelIdentifierType.OSS_IDENTIFIER;
            final NodeModelInformation modelInfo = new NodeModelInformation(modelIdentifier, modelIdType, nodeType);
            final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat,
                    new SubjectAltNameStringType(subjectAltName));
            final IscfResponse iscfResponse = holder.getIscfService().generate(logicalName, node, "UserLabel", subjectAltNameParam,
                    ipsecAreasForResult, wantedEnrollmentMode, modelInfo);
            final String xml = new String(iscfResponse.getIscfContent(), "UTF-8");
            return Response.ok().entity(xml).build();

        } catch (final Exception e) {
            logger.warn("Failed to generate ISCF XML via RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to generate ISCF XML: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("combined")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getIscfCombinedXml(@QueryParam("node") final String node, @QueryParam("logical") final String logicalName,
            @QueryParam("wanted") final SecurityLevel wanted, @QueryParam("minimum") final SecurityLevel minimum,
            @QueryParam("subjectAltName") final String subjectAltName,
            @QueryParam("subjectAltNameType") final SubjectAltNameFormat subjectAltNameFormat,
            @QueryParam("ipsecAreas") final List<String> ipsecAreas, @QueryParam("mode") final EnrollmentMode wantedEnrollmentMode,
            @QueryParam("version") final String modelIdentifier, @QueryParam("nodeType") final String nodeType) {
        logger.info(
                "RestResource for combined ISCF invoked.  Node name: {}, Logical name: {}, Wanted level: {}, Minimum level: {} subjectAltName {}, subjectAltNameType {}, ipsecArea {}, enrollmentMode {}, modelIdentifier {}, nodeType {}",
                node, logicalName, wanted, minimum, subjectAltName, subjectAltNameFormat, ipsecAreas.toString(), wantedEnrollmentMode,
                modelIdentifier, nodeType);
        try {
            final Set<IpsecArea> ipsecAreasForResult = new HashSet<>();
            for (final String ipsecArea : ipsecAreas) {
                if (ipsecArea.equalsIgnoreCase("traffic")) {
                    ipsecAreasForResult.add(IpsecArea.TRANSPORT);
                }
                if (ipsecArea.equalsIgnoreCase("om")) {
                    ipsecAreasForResult.add(IpsecArea.OM);
                }
            }
            final ModelIdentifierType modelIdType = "ERBS".equals(nodeType) ? ModelIdentifierType.MIM_VERSION : ModelIdentifierType.OSS_IDENTIFIER;
            final NodeModelInformation modelInfo = new NodeModelInformation(modelIdentifier, modelIdType, nodeType);
            final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat,
                    new SubjectAltNameStringType(subjectAltName));
            final IscfResponse iscfResponse = holder.getIscfService().generate(logicalName, node, wanted, minimum, "UserLabel", subjectAltNameParam,
                    ipsecAreasForResult, wantedEnrollmentMode, modelInfo);
            final String xml = new String(iscfResponse.getIscfContent(), "UTF-8");
            return Response.ok().entity(xml).build();
        } catch (final Exception e) {
            logger.warn("Failed to generate ISCF XML via RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to generate ISCF XML: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("cancel/{node}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getIscfCancel(@PathParam("node") final String node) {
        logger.info("RestResource Delete invoked. Node name: {}", node);
        try {
            holder.getIscfService().cancel(node);
            return Response.ok().entity("ISCF info for node [" + node + "] removed").build();

        } catch (final Exception e) {
            logger.warn("Failed to delete ISCF info via RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete ISCF info: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("secdata/oam")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSecurityDataOam(@QueryParam("node") final String node, @QueryParam("serial") final String serialNumber,
            @QueryParam("subjectAltName") final String subjectAltName, 
            @QueryParam("subjectAltNameType") final SubjectAltNameFormat subjectAltNameFormat,
            @QueryParam("mode") final EnrollmentMode wantedEnrollmentMode,
            @QueryParam("product") @DefaultValue("16B-R28GY") final String modelIdentifier,
            @QueryParam("nodeType") @DefaultValue("RadioNode") final String nodeType,
            @QueryParam("ipVersion") final StandardProtocolFamily ipVersion) {
        logger.info("RestResource invoked. Node name: {}, serialNumber: {} enrollmentMode: {}, modelIdentifier: {}, nodeType: {}, ipVersion: {}" , 
                node, serialNumber, wantedEnrollmentMode, modelIdentifier, nodeType, ipVersion);
        try {
            final ModelIdentifierType modelIdType = "ERBS".equals(nodeType) ? ModelIdentifierType.MIM_VERSION : ModelIdentifierType.OSS_IDENTIFIER;
            final NodeModelInformation modelInfo = new NodeModelInformation(modelIdentifier, modelIdType, nodeType);
            final NodeIdentifier nodeId = new NodeIdentifier(node, serialNumber);
            SecurityDataResponse secResponse;
            if ((subjectAltNameFormat == null) || (subjectAltName == null)) {
                secResponse = holder.getIscfService().generateSecurityDataOam(nodeId, wantedEnrollmentMode, modelInfo, ipVersion);
            } else {
                final SubjectAltNameStringType subjectAltNameString = new SubjectAltNameStringType(subjectAltName);
                final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameString);
                secResponse = holder.getIscfService().generateSecurityDataOam(nodeId, subjectAltNameParam, wantedEnrollmentMode, modelInfo, ipVersion);
            }
            return Response.ok().entity(secResponse.toString()).build();

        } catch (final Exception e) {
            logger.warn("Failed to generate Security Data via RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to generate Security Data: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("secdata/ipsec")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSecurityDataIpsec(@QueryParam("node") final String node, @QueryParam("serial") final String serialNumber,
            @QueryParam("subjectAltName") final String subjectAltName, 
            @QueryParam("subjectAltNameType") final SubjectAltNameFormat subjectAltNameFormat,
            @QueryParam("mode") final EnrollmentMode wantedEnrollmentMode,
            @QueryParam("product") @DefaultValue("16B-R28GY") final String modelIdentifier,
            @QueryParam("nodeType") @DefaultValue("RadioNode") final String nodeType,
            @QueryParam("ipVersion") final StandardProtocolFamily ipVersion) {
        logger.info("RestResource invoked. Node name: {}, serialNumber: {} enrollmentMode: {}, modelIdentifier: {}, nodeType: {}, ipVersion: {}",
                node, serialNumber, wantedEnrollmentMode, modelIdentifier, nodeType, ipVersion);
        try {
            final ModelIdentifierType modelIdType = "ERBS".equals(nodeType) ? ModelIdentifierType.MIM_VERSION : ModelIdentifierType.OSS_IDENTIFIER;
            final NodeModelInformation modelInfo = new NodeModelInformation(modelIdentifier, modelIdType, nodeType);
            final NodeIdentifier nodeId = new NodeIdentifier(node, serialNumber);
            SubjectAltNameStringType subjectAltNameString = null;
            if (subjectAltName != null) {
                subjectAltNameString = new SubjectAltNameStringType(subjectAltName);
            }
            final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameString);
            final SecurityDataResponse secResponse = holder.getIscfService().generateSecurityDataIpsec(nodeId, subjectAltNameParam,
                    wantedEnrollmentMode, modelInfo, ipVersion);
            return Response.ok().entity(secResponse.toString()).build();

        } catch (final Exception e) {
            logger.warn("Failed to generate Security Data via RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to generate Security Data: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("secdata/combined")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSecurityDataCombo(@QueryParam("node") final String node, @QueryParam("serial") final String serialNumber,
            @QueryParam("subjectAltName") final String subjectAltName, 
            @QueryParam("subjectAltNameType") final SubjectAltNameFormat subjectAltNameFormat,
            @QueryParam("mode") final EnrollmentMode wantedEnrollmentMode,
            @QueryParam("product") @DefaultValue("16B-R28GY") final String modelIdentifier,
            @QueryParam("nodeType") @DefaultValue("RadioNode") final String nodeType,
            @QueryParam("ipVersion") final StandardProtocolFamily ipVersion) {
        logger.info("RestResource invoked. Node name: {}, serialNumber: {} enrollmentMode: {}, modelIdentifier: {}, nodeType: {}, ipVersion: {}",
                node, serialNumber, wantedEnrollmentMode, modelIdentifier, nodeType, ipVersion);
        try {
            final ModelIdentifierType modelIdType = "ERBS".equals(nodeType) ? ModelIdentifierType.MIM_VERSION : ModelIdentifierType.OSS_IDENTIFIER;
            final NodeModelInformation modelInfo = new NodeModelInformation(modelIdentifier, modelIdType, nodeType);
            final NodeIdentifier nodeId = new NodeIdentifier(node, serialNumber);
            SubjectAltNameStringType subjectAltNameString = null;
            if (subjectAltName != null) {
                subjectAltNameString = new SubjectAltNameStringType(subjectAltName);
            }
            final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameString);
            final SecurityDataResponse secResponse = holder.getIscfService().generateSecurityDataCombo(nodeId, subjectAltNameParam,
                    wantedEnrollmentMode, modelInfo, ipVersion);
            return Response.ok().entity(secResponse.toString()).build();

        } catch (final Exception e) {
            logger.warn("Failed to generate Security Data via RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to generate Security Data: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("secdata/oam/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateSecurityDataOam(@QueryParam("targetCategory") final String targetCategory,
            @QueryParam("targetType") final String targetType, @QueryParam("targetModelIdentity") final String targetModelIdentity,
            @QueryParam("node") final String node, @QueryParam("serialNumber") final String serialNumber,
            @QueryParam("subjectAltName") final String subjectAltName, @QueryParam("subjectAltNameFormat") final String subjectAltNameFormatStr,
            @QueryParam("enrollmentMode") final String enrollmentModeStr,
            @QueryParam("ipVersion") final StandardProtocolFamily ipVersion) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription(
                "Generate OAM security data for targetCategory '" + targetCategory + "', targetType '" + targetType + "', targetModelIdentity '"
                        + targetModelIdentity + "', node '" + node + "', serialNumber '" + serialNumber + "', subjectAltNameFormat '"
                        + subjectAltNameFormatStr + "', subjectAltName '" + subjectAltName + "', enrollmentMode '" + enrollmentModeStr
                        + "', ipVersion '" + ipVersion + "'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        final Map<String, Object> responses = new HashMap<>();
        try {
            final NodeModelInformation modelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER, targetType);
            final NodeIdentifier nodeId = new NodeIdentifier(node, serialNumber);
            SecurityDataResponse secResponse;
            final EnrollmentMode enrollmentMode = EnrollmentMode.valueOf(enrollmentModeStr);
            if ((subjectAltNameFormatStr == null) || (subjectAltName == null)) {
                secResponse = holder.getIscfService().generateSecurityDataOam(nodeId, enrollmentMode, modelInfo, ipVersion);
            } else {
                final SubjectAltNameStringType subjectAltNameString = new SubjectAltNameStringType(subjectAltName);
                final SubjectAltNameFormat subjectAltNameFormat = SubjectAltNameFormat.valueOf(subjectAltNameFormatStr);
                final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameString);
                secResponse = holder.getIscfService().generateSecurityDataOam(nodeId, subjectAltNameParam, enrollmentMode, modelInfo, ipVersion);
            }
            responses.put("security data response", secResponse);
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
    @Path("secdata/ipsec/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateSecurityDataIpsec(@QueryParam("targetCategory") final String targetCategory,
            @QueryParam("targetType") final String targetType, @QueryParam("targetModelIdentity") final String targetModelIdentity,
            @QueryParam("node") final String node, @QueryParam("serialNumber") final String serialNumber,
            @QueryParam("subjectAltName") final String subjectAltName, @QueryParam("subjectAltNameFormat") final String subjectAltNameFormatStr,
            @QueryParam("enrollmentMode") final String enrollmentModeStr,
            @QueryParam("ipVersion") final StandardProtocolFamily ipVersion) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription(
                "Generate IPSEC security data for targetCategory '" + targetCategory + "', targetType '" + targetType + "', targetModelIdentity '"
                        + targetModelIdentity + "', node '" + node + "', serialNumber '" + serialNumber + "', subjectAltNameFormat '"
                        + subjectAltNameFormatStr + "', subjectAltName '" + subjectAltName + "', enrollmentMode '" + enrollmentModeStr
                        + "', ipVersion '" + ipVersion + "'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        final Map<String, Object> responses = new HashMap<>();
        try {
            final NodeModelInformation modelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER, targetType);
            final NodeIdentifier nodeId = new NodeIdentifier(node, serialNumber);
            SecurityDataResponse secResponse;
            final EnrollmentMode enrollmentMode = EnrollmentMode.valueOf(enrollmentModeStr);
            SubjectAltNameStringType subjectAltNameString = null;
            if (subjectAltName != null) {
                subjectAltNameString = new SubjectAltNameStringType(subjectAltName);
            }
            final SubjectAltNameFormat subjectAltNameFormat = SubjectAltNameFormat.valueOf(subjectAltNameFormatStr);
            final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameString);
            secResponse = holder.getIscfService().generateSecurityDataIpsec(nodeId, subjectAltNameParam, enrollmentMode, modelInfo, ipVersion);
            responses.put("security data response", secResponse);
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
    @Path("secdata/combo/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateSecurityDataCombo(@QueryParam("targetCategory") final String targetCategory,
            @QueryParam("targetType") final String targetType, @QueryParam("targetModelIdentity") final String targetModelIdentity,
            @QueryParam("node") final String node, @QueryParam("serialNumber") final String serialNumber,
            @QueryParam("subjectAltName") final String subjectAltName, @QueryParam("subjectAltNameFormat") final String subjectAltNameFormatStr,
            @QueryParam("enrollmentMode") final String enrollmentModeStr,
            @QueryParam("ipVersion") final StandardProtocolFamily ipVersion) {
        final NscsRestResult restResult = new NscsRestResult("", null);
        restResult.setDescription(
                "Generate COMBO security data for targetCategory '" + targetCategory + "', targetType '" + targetType + "', targetModelIdentity '"
                        + targetModelIdentity + "', node '" + node + "', serialNumber '" + serialNumber + "', subjectAltNameFormat '"
                        + subjectAltNameFormatStr + "', subjectAltName '" + subjectAltName + "', enrollmentMode '" + enrollmentModeStr
                        + "', ipVersion '" + ipVersion + "'");
        final ObjectMapper mapper = new ObjectMapper();
        String result = "";
        final Map<String, Object> responses = new HashMap<>();
        try {
            final NodeModelInformation modelInfo = new NodeModelInformation(targetModelIdentity, ModelIdentifierType.OSS_IDENTIFIER, targetType);
            final NodeIdentifier nodeId = new NodeIdentifier(node, serialNumber);
            SecurityDataResponse secResponse;
            final EnrollmentMode enrollmentMode = EnrollmentMode.valueOf(enrollmentModeStr);
            SubjectAltNameStringType subjectAltNameString = null;
            if (subjectAltName != null) {
                subjectAltNameString = new SubjectAltNameStringType(subjectAltName);
            }
            final SubjectAltNameFormat subjectAltNameFormat = SubjectAltNameFormat.valueOf(subjectAltNameFormatStr);
            final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameString);
            secResponse = holder.getIscfService().generateSecurityDataCombo(nodeId, subjectAltNameParam, enrollmentMode, modelInfo, ipVersion);
            responses.put("security data response", secResponse);
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

}
