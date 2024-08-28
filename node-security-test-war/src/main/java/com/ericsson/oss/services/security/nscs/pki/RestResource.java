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
package com.ericsson.oss.services.security.nscs.pki;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.security.nscs.util.EServiceHolder;

/**
 * Rest interface of the node-security EJBs. This is ONLY for testing purposes. This class should be removed as soon as the EJBs can be tested through real user stories.
 *
 * This interface supports instantiating WorkFlows.
 *
 * @author eabdsin
 *
 */
@Path("/")
public class RestResource {

    @Inject
    private Logger logger;

    @Inject
    EServiceHolder holder;

    @EJB
    NscsPkiEntitiesManagerIF nscsPkiManager;

    @Inject
    UserTransaction userTransaction;

    @GET
    @Path("pkimanager/mockStatus")
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkPkiManagerStatus() {
        logger.info("PKI Manager Status is invoked.");
        final StringBuilder sb = new StringBuilder();
        sb.append("mock.entity=");
        sb.append(nscsPkiManager.useMockEntityManager());
        sb.append("\nmock.profile=");
        sb.append(nscsPkiManager.useMockProfileManager());
        sb.append("\nmock.certificate=");
        sb.append(nscsPkiManager.useMockCertificateManager());
        return Response.ok().entity(sb.toString()).build();
    }

    @GET
    @Path("pkicore/{entityName}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkPKIService(@PathParam("entityName") final String entityName) {
        logger.info("PKI core is invoked.  entityName is [{}] ", entityName);
        try {
            logger.info("Calling:   checkPKIService()");

            final Entity endEntity = new Entity();
            endEntity.setType(EntityType.ENTITY);
            final EntityInfo entityInfo = new EntityInfo();
            entityInfo.setName(entityName);
            endEntity.setEntityInfo(entityInfo);

            nscsPkiManager.createEntity(endEntity);

            logger.info("Returning:   from checkPKIService()");
            return Response.ok().entity(endEntity).build();

        } catch (final Exception e) {
            logger.warn("Failed to call PKI Core through RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to pki-core: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("pkicore/getcas/{entityName}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCA(@PathParam("entityName") final String entityName) {
        logger.info("PKI core is invoked.  entityName is [{}] ", entityName);
        try {
            logger.info("Calling:   checkPKIService()");

            final Entity endEntity = new Entity();
            endEntity.setType(EntityType.ENTITY);
            final EntityInfo entityInfo = new EntityInfo();
            entityInfo.setName(entityName);
            endEntity.setEntityInfo(entityInfo);
            nscsPkiManager.createEntity(endEntity);

            logger.info("Returning:   from checkPKIService()");
            return Response.ok().entity(endEntity).build();

        } catch (final Exception e) {
            logger.warn("Failed to call PKI Core through RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to pki-core: " + e.getMessage()).build();
        }
    }

    public static final String CPP_NODE_CA_NAME = "atrcxb3160NECertCA";

    public static final String CPP_NODE_EE_PROFILE = "CPPNodeEndEntityProfile";

    @GET
    @Path("pkicore/createEE/{entityName}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response omsasPKIBeanCreateTest(@PathParam("entityName") final String entityName) {
        logger.info("PKI core is invoked.  entityName is [{}] ", entityName);
        try {
            logger.info("Calling:   omsasPKIBeanCreateTest()");

            final Entity endEntity = new Entity();
            endEntity.setType(EntityType.ENTITY);
//            endEntity.setEntityProfileName(CPP_NODE_EE_PROFILE);
            final EntityInfo entityInfo = new EntityInfo();
            entityInfo.setName(entityName);
            entityInfo.setOTP("enmenm123");
            endEntity.setEntityInfo(entityInfo);

//            endEntity.setEndEntityProfileName(CPP_NODE_EE_PROFILE);
//            endEntity.setSingerCA(CPP_NODE_CA_NAME);
//            endEntity.setOTP("enmenm123".toCharArray());

            nscsPkiManager.createEntity(endEntity);

            logger.info("entity created...................");
            logger.info("entity ...................Dn............................{}",
                    endEntity.getEntityInfo().getSubject());

            if(logger.isInfoEnabled()){
                logger.info("entity ...................SubjectAltName ...............{}",
                        endEntity.getEntityInfo().getSubjectAltName());
            }

            logger.info("Returning:   from omsasPKIBeanCreateTest()");
            return Response.ok().entity(endEntity).build();

        } catch (final Exception e) {
            logger.warn("Failed to call PKI Core through RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to pki-core: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("pkicore/deleteEE/{entityName}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response omsasPKIBeanDeleteTest(@PathParam("entityName") final String entityName) {
        logger.info("PKI core is invoked.  entityName is [{}] ", entityName);
        try {
            logger.info("Calling:   omsasPKIBeanDeleteTest()");

//            final Entity endEntity = new Entity();
//            endEntity.setType(EntityType.ENTITY);
//            endEntity.setEntityProfileName(CPP_NODE_EE_PROFILE);
//            final EntityInfo entityInfo = new EntityInfo();
//            entityInfo.setName(entityName);
//            entityInfo.setOTP("enmenm123");
//            endEntity.setEntityInfo(entityInfo);

//            final X500NameSerializable eeName = new X500NameSerializable(entityName);
//            final EndEntity endEntity = new EndEntity(eeName);
//            endEntity.setEndEntityProfileName(CPP_NODE_EE_PROFILE);
//            endEntity.setSingerCA(CPP_NODE_CA_NAME);
//            endEntity.setOTP("enmenm123".toCharArray());

            userTransaction.begin();
            nscsPkiManager.deleteEntity(entityName);
            userTransaction.commit();

            logger.info("entity deleted...................");
            return Response.ok().status(Status.OK).build();

        } catch (final Exception e) {
            logger.warn("Failed to call PKI Core through RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to pki-core: " + e.getMessage()).build();
        }
    }

//    @GET
//    @Path("pkicore/editEE/{entityName}/{changedName}")
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response omsasPKIBeanEditTest(@PathParam("entityName") final String entityName, @PathParam("changedName") final String changedName) {
//        logger.info("PKI core is invoked.  entityName is [{}] ", entityName);
//        logger.info("PKI core is invoked.  changed entityName is [{}] ", changedName);
//        try {
//            logger.info("Calling:   omsasPKIBeanEditTest()");
//
//
//            final Entity entityRetrieved = nscsPkiManager.getEndEntity(eeName);
//
//            entityRetrieved.setSubjectAltName(changedName);
//            holder.getPKIService().editEndEntity(entityRetrieved);
//
//            logger.info("entity changed...................");
//            logger.info("entity ...................Dn............................" + entityRetrieved.getDN());
//            logger.info("entity ...................SubjectAltName ..............." + entityRetrieved.getSubjectAltName());
//
//            logger.info("Returning:   from omsasPKIBeanEditTest()");
//            return Response.ok().entity(entityRetrieved).build();
//
//        } catch (final Exception e) {
//            logger.warn("Failed to call PKI Core through RestResource");
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to pki-core: " + e.getMessage()).build();
//        }
//    }

    @GET
    @Path("pkicore/getCAs")
    @Produces(MediaType.TEXT_PLAIN)
    public Response omsasPKIBeanGetCAsTest() {
        logger.info("PKI core is invoked.  GetCAs ");
        try {
            logger.info("Calling:   omsasPKIBeanGetCAsTest()");

//            final Map<String, CertificateAuthority> cAMap;
//
//            cAMap = nscsPkiManager.getCAs();
//
//            logger.info("All ENM CAs retrieved .................");
//            final StringBuilder strBuilder = new StringBuilder();
//            Collection<CertificateAuthority> cca = cAMap.values();
//            for (final CertificateAuthority certificateAuthorityIterator : cca) {
//
//                logger.info("CA ...................Name ..............." + certificateAuthorityIterator.getName().toString());
//
//                strBuilder.append("CA ...................Name ..............." + certificateAuthorityIterator.getName().toString());
//                for (final PKIX509Certificate certs : certificateAuthorityIterator.getCertChainSerializable()) {
//
//                    logger.info(" ");
//                    logger.info("        CA ...................Certificate Chain ..............." + certificateAuthorityIterator.getCertChainSerializable());
//
//                    strBuilder.append(certs.getCertificate().toString());
//                    strBuilder.append("----------------------------------------------------------------------------------------------");
//                    logger.info("        CA ...................Certificate Encoded String ..............." + certs.getCertificate().toString());
//
//                }
//            }



            final Map<String, List<X509Certificate>>  cas = nscsPkiManager.getCAsTrusts();
            logger.info("All ENM CAs retrieved .................");
            final StringBuilder strBuilder = new StringBuilder();
            for (final Map.Entry<String, List<X509Certificate>> entry : cas.entrySet()) {

                logger.info("CA ...................Name ..............." + entry.getKey());

                strBuilder.append("CA ...................Name ...............").append(entry.getKey());
                for (final X509Certificate certs : entry.getValue()) {

                    logger.info(" ");
                    logger.info("        CA ...................Certificate Chain ...............{}", entry.getValue());

                    strBuilder.append(certs.toString());
                    strBuilder.append("----------------------------------------------------------------------------------------------");
                    logger.info("        CA ...................Certificate Encoded String ...............{}", certs);

                }
            }

            logger.info("Returning:   from omsasPKIBeanGetCAsTest()");
            return Response.ok().entity(strBuilder).build();

        } catch (final Exception e) {
            logger.warn("Failed to call PKI Core through RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to pki-core: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("pkicore/getCA/{caName}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response omsasPKIBeanGetCATest(@PathParam("caName") final String caName) {

        logger.info("PKI core is invoked.  GetCA ");
        try {
            logger.info("Calling:   omsasPKIBeanGetCATest()");

//            final CertificateAuthority certificateAuthority;
//            certificateAuthority = nscsPkiManager.getCA(caName);
//
//            logger.info("All ENM CAs retrieved .................");
//            final StringBuilder strBuilder = new StringBuilder();
//
//            logger.info("CA retrieved .................");
//            logger.info("CA ...................CertificateAuthority.Name............................" + certificateAuthority.getName());
//
//            for (final PKIX509Certificate certs : certificateAuthority.getCertChainSerializable()) {
//
//                logger.info("CA ...................Certificate Chain ..............." + certificateAuthority.getCertChainSerializable());
//
//                logger.info("CA ...................Certificate Encoded String ..............." + certs.getCertificate().toString());
//                strBuilder.append(certs.getCertificate().toString());
//
//                strBuilder.append("----------------------------------------------------------------------------------------------");
//
//            }

            final List<X509Certificate>  ca = nscsPkiManager.getCATrusts(caName);
            logger.info("All ENM CAs retrieved .................");
            final StringBuilder strBuilder = new StringBuilder();

            logger.info("CA retrieved .................");
            logger.info("CA ...................CertificateAuthority.Name............................{}", caName);

                for (final X509Certificate cert : ca) {

                logger.info("CA ...................Certificate Chain ...............{}", ca);

                logger.info("CA ...................Certificate Encoded String ...............{}", cert);
                strBuilder.append(cert.toString());

                strBuilder.append("----------------------------------------------------------------------------------------------");

            }


            logger.info("Returning:   from omsasPKIBeanGetCATest()");
            return Response.ok().entity(strBuilder).build();

        } catch (final Exception e) {
            logger.warn("Failed to call PKI Core through RestResource");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to pki-core: " + e.getMessage()).build();
        }

    }

}
