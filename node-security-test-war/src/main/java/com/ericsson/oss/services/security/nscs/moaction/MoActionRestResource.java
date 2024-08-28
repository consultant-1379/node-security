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
package com.ericsson.oss.services.security.nscs.moaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.model.AccountInfo;
import com.ericsson.nms.security.nscs.cpp.model.CPPCertSpec;
import com.ericsson.nms.security.nscs.cpp.model.ScepEnrollmentInfoImpl;
import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.utilities.NSCSCppNodeUtility;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * REST used by Arquillian tests to verify MO action service.
 */
@Path("/")
public class MoActionRestResource {

    @Inject
    private Logger logger;

    @Inject
    MOActionService moAction;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    UserTransaction userTransaction;

    private static final String MO_ACTION_SUCCESS = "MO Action executed successfully. Please check the logs for details.";
    private static final String MO_ACTION_NOT_SUPP = "Not supported MO action";

    public static boolean isKeySizeSupported = true;
    public static boolean isCertificateAuthorityDnSupported = true;

    @GET
    @Path("moaction/{action}/{node}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeMoAction(@PathParam("action") final String action, @PathParam("node") final String node) {
        logger.info("RestResource invoked. MOAction:  \"{}\", node: \"{}\"", action, node);
        try {
            final MoActionWithoutParameter actionEnum = MoActionWithoutParameter.valueOf(action);
            NodeReference nodeRef = new NodeRef(node);
            userTransaction.begin();
            NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeRef);
            userTransaction.commit();
            moAction.performMOAction(normalizable.getFdn(), actionEnum);
        } catch (final IllegalArgumentException e) {
            logger.warn(MO_ACTION_NOT_SUPP + ": " + action, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(MO_ACTION_NOT_SUPP + ". Supported MO actions: " + Arrays.toString(MoActionWithoutParameter.values())).build();
        } catch (final Exception e) {
            logger.warn("Failed to perform MO Action", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to perform MO Action.\n" + e.getClass().getName() + ":"
                    + e.toString() + "\n\n\nException stacktrace:\n" + Arrays.toString(e.getStackTrace())).build();
        }
        return Response.status(Response.Status.OK).entity(MO_ACTION_SUCCESS).build();
    }

    @GET
    @Path("moactionparams/{action}/{node}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeMoActionParams(@PathParam("action") final String action, @PathParam("node") final String node) {
        logger.info("RestResource invoked. MOAction (with params):  \"{}\", node: \"{}\"", action, node);
        try {
            final MoActionWithParameter moaction = MoActionWithParameter.valueOf(action);
            MoParams params = null;

            switch (moaction) {
            case Security_initCertEnrollment:
                params = getInitCertEnrollmentParams();
                break;
            case Security_installTrustedCertificates:
                params = getInstallTrustedCertsParams();
                break;
            default:
                return Response.status(Response.Status.BAD_REQUEST).entity(MO_ACTION_NOT_SUPP + action).build();
            }

            NodeReference nodeRef = new NodeRef(node);
            userTransaction.begin();
            NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeRef);
            userTransaction.commit();
            moAction.performMOAction(normalizable.getFdn(), moaction, params);
        } catch (final IllegalArgumentException e) {
            logger.warn("Failed to perform MO Action", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(MO_ACTION_NOT_SUPP + ". Supported MO actions: " + Arrays.toString(MoActionWithParameter.values())).build();
        } catch (final Exception e) {
            logger.warn("Failed to perform MO Action", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to perform MO Action.\n" + e.getClass().getName() + ":"
                    + e.toString() + "\n\n\nException stacktrace:\n" + Arrays.toString(e.getStackTrace())).build();
        }
        return Response.status(Response.Status.OK).entity(MO_ACTION_SUCCESS).build();
    }

    @GET
    @Path("moactionsetting/changeIsKeySizeSupported/{isSupported}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeIsKeySizeSupported(@PathParam("isSupported") final String isSupported) {
        logger.info("changeIsKeySizeSupported [{}] ", isSupported);
        try {

            final boolean isSupp = Boolean.valueOf(isSupported);
            isKeySizeSupported = isSupp;
            logger.debug("changeIsKeySizeSupported is changed to {}", isSupp);

            return Response.ok().entity("changeIsKeySizeSupported set to: " + isSupp).build();
        } catch (final Exception e) {
            logger.warn("Failed to change");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to changeIsKeySizeSupported: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("moactionsetting/changeIsCertificateAuthorityDnSupported/{isSupported}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response changeIsCertificateAuthorityDnSupported(@PathParam("isSupported") final String isSupported) {
        logger.info("changeIsCertificateAuthorityDnSupported [{}] ", isSupported);
        try {

            final boolean isSupp = Boolean.valueOf(isSupported);
            isCertificateAuthorityDnSupported = isSupp;
            logger.debug("changeIsCertificateAuthorityDnSupported is changed to {}", isSupp);

            return Response.ok().entity("IsCertificateAuthorityDnSupported set to: " + isSupp).build();
        } catch (final Exception e) {
            logger.warn("Failed to change");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to changeIsCertificateAuthorityDnSupported: " + e.getMessage()).build();
        }
    }

    /**
     * Dummy parameters for initCertEnrollment
     * 
     * @return
     */
    public static MoParams getInitCertEnrollmentParams() {
        final MoParams params = ScepEnrollmentInfoImpl.toMoParams("caFingerPrint".getBytes(), "challengePassword", "distinguishedName",
                EnrollmentMode.SCEP.getEnrollmentModeValue(), "enrollmentServerURL", NSCSCppNodeUtility.CPP_KEY_LENGTH_1024, 0, isKeySizeSupported,
                isCertificateAuthorityDnSupported, "authority", DigestAlgorithm.SHA1);
        return params;
    }

    /**
     * Dummy parameters for installTrustedCertificates
     * 
     * @return
     */
    public static MoParams getInstallTrustedCertsParams() {
        final List<MoParams> certSpecList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            certSpecList.add(CPPCertSpec.toMoParams(TrustedCertCategory.CORBA_PEERS, "fileName" + i, "/root/smrs", ("fingerprint" + i).getBytes(),
                    "serialNumber" + i, DigestAlgorithm.SHA1));
        }
        final List<MoParams> accountInfoList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            accountInfoList.add(AccountInfo.toMoParams(("password" + i).toCharArray(), "remoteHost" + i, "userID" + i));
        }
        final MoParams params = TrustStoreInfo.toMoParams(certSpecList, "0", 0, accountInfoList);
        return params;
    }
}
