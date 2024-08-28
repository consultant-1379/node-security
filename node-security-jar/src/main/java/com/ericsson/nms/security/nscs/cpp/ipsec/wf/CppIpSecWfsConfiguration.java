/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cpp.ipsec.wf;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.DisableOMConfiguration;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.DisableOMConfiguration.RemoveTrust;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.InstallTrustCertificates;
import com.ericsson.nms.security.nscs.logger.NscsLogger;

/**
 * Helper for IpSec Workflow Configuration.
 *
 * @author emehsau
 */
public class CppIpSecWfsConfiguration {

    private static final String ENCODING = "UTF-8";

    private static final String TRUST_SERIAL_NUMBER = "TRUST_SERIAL_NUMBER";

    private static final String TRUST_ISSUER = "TRUST_ISSUER";

    private static final String REMOVE_CERT = "REMOVE_CERT";

    private static final String NODES_XML = "NODES_XML";

    private static final String REMOVE_TRUST = "REMOVE_TRUST";

    private static final String TRUST_CERTS = "TRUST_CERTS";

    private static final String SUB_ALT_NAME = "SUB_ALT_NAME";

    private static final String SUB_ALT_NAME_TYPE = "SUB_ALT_NAME_TYPE";

    private static final String IPSEC_ENABLE_WORKFLOW_NAME = "CPPActivateIpSec";

    private static final String IPSEC_DISABLE_WORKFLOW_NAME = "CPPDeactivateIpSec";

    private static final String IPSEC_INSTALL_TRUST_CERTIFICATES_WORKFLOW_NAME = "CPPInstallCertificatesIpSec";

    @Inject
    private NscsLogger nscsLogger;

    /**
     * This method perform the configuration needed by each IpSec workflow
     * @param request: the input data gathered from xml file
     * @return: the workflow configuration
     */
    public IpSecRequestWfsConfiguration configureIpSecWorkflow(final IpSecRequest request) {
        final String nodeFdn = request.getNodeFdn();
        Map<String, Object> workflowParams;
        IpSecRequestWfsConfiguration ipSecRequestWfsConfiguration = new IpSecRequestWfsConfiguration();
        switch (request.getIpSecRequestType()) {
            case IP_SEC_ENABLE_CONF1:
                nscsLogger.info("IpSec enable CONFIGURATION_1 flow for node [{}] ", request.getNodeFdn());
                workflowParams = createMapforEnableIpSec(request);
                ipSecRequestWfsConfiguration.setNodeFdn(nodeFdn);
                ipSecRequestWfsConfiguration.setWorkflowName(IPSEC_ENABLE_WORKFLOW_NAME);
                ipSecRequestWfsConfiguration.setWorkflowParams(workflowParams);
                break;
            //To be updated and verified as part of MR 55921
            case IP_SEC_ENABLE_CONF2:
                nscsLogger.info("IpSec enable CONFIGURATION_2 flow for node {} ", request.getNodeFdn());
                workflowParams = createMapforEnableIpSec(request);
                ipSecRequestWfsConfiguration.setNodeFdn(nodeFdn);
                ipSecRequestWfsConfiguration.setWorkflowName(IPSEC_ENABLE_WORKFLOW_NAME);
                ipSecRequestWfsConfiguration.setWorkflowParams(workflowParams);
                break;
            case IP_SEC_DISABLE:
                nscsLogger.info("IpSec disable flow for node [{}] ", request.getNodeFdn());
                workflowParams = createMapforDisableIpSec(request);
                ipSecRequestWfsConfiguration.setNodeFdn(nodeFdn);
                ipSecRequestWfsConfiguration.setWorkflowName(IPSEC_DISABLE_WORKFLOW_NAME);
                ipSecRequestWfsConfiguration.setWorkflowParams(workflowParams);
                break;
            case IP_SEC_INSTALL_CERTS:
                String message = String.format("Processing install certs only flow for node %s", request.getNodeFdn());
                nscsLogger.info(message);
                workflowParams = createMapForInstallCertificatesIpSec(request);
                ipSecRequestWfsConfiguration.setNodeFdn(nodeFdn);
                ipSecRequestWfsConfiguration.setWorkflowName(IPSEC_INSTALL_TRUST_CERTIFICATES_WORKFLOW_NAME);
                ipSecRequestWfsConfiguration.setWorkflowParams(workflowParams);
                break;
        }

        return ipSecRequestWfsConfiguration;
    }

    /**
     * This method will create the map required for WF process
     *
     * @param request : {@link IpSecRequest}
     * @return {@link Map} consists of params required for WF.
     */
    private Map<String, Object> createMapforEnableIpSec(final IpSecRequest request) {
        final Map<String, Object> mapForEnableIpSec = new HashMap<>();
        final Node xmlNode = request.getXmlRepresntationOfNode();
        final String subjectAltNode = xmlNode.getSubAltName();
        final String subAltNameType = xmlNode.getSubAltNameType();
        final String xmlFileContentsForNode = getXMLContentForNode(xmlNode);
        mapForEnableIpSec.put(SUB_ALT_NAME, subjectAltNode);
        mapForEnableIpSec.put(SUB_ALT_NAME_TYPE, subAltNameType);
        mapForEnableIpSec.put(NODES_XML, xmlFileContentsForNode);
        return mapForEnableIpSec;
    }

    /**
     * This method will create the map required for WF process
     *
     * @param request : {@link IpSecRequest}
     * @return {@link Map} consists of params required for WF.
     */
    private Map<String, Object> createMapforDisableIpSec(final IpSecRequest request) {
        final Map<String, Object> mapForDisableIpSec = new HashMap<>();
        final Node xmlNode = request.getXmlRepresntationOfNode();
        final DisableOMConfiguration disableConf = xmlNode.getDisableOMConfiguration();
        if (disableConf != null) {
            final boolean removeCert = disableConf.isRemoveCert();
            final RemoveTrust removeTrust = disableConf.getRemoveTrust();
            String trustIssuer = null;
            long trustSerialNumber = -1L;
            if (removeTrust != null) {
                mapForDisableIpSec.put(REMOVE_TRUST, "true");
                trustIssuer = removeTrust.getIssuer();
                trustSerialNumber = removeTrust.getSerialNumber();
            }

            mapForDisableIpSec.put(REMOVE_CERT, removeCert + "");
            mapForDisableIpSec.put(TRUST_ISSUER, trustIssuer);
            mapForDisableIpSec.put(TRUST_SERIAL_NUMBER, trustSerialNumber + "");

        }
        final String xmlFileContentsForNode = getXMLContentForNode(xmlNode);
        mapForDisableIpSec.put(NODES_XML, xmlFileContentsForNode);
        return mapForDisableIpSec;
    }

    /**
     * Method to get XML equivalent data per Node entry.
     *
     * @param xmlNode :
     * @return String representation of XML
     */
    private String getXMLContentForNode(final Node xmlNode) {
        final Nodes nodes = new Nodes();
        nodes.getNode().add(xmlNode);
        String result = null;
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(Nodes.class);
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
            final StringWriter stringWriter = new StringWriter();
            marshaller.marshal(nodes, stringWriter);
            result = stringWriter.toString();
        } catch (final JAXBException e) {
            nscsLogger.warn("Conversion of Node object to XML failed. Excp:{}",e);
        }
        return result;
    }

    /**
     * This method will create the map required for install certificate WF process
     *
     * @param request : {@link IpSecRequest}
     * @return {@link Map} consists of params required for WF.
     */
    private Map<String, Object> createMapForInstallCertificatesIpSec(final IpSecRequest request) {
        final Map<String, Object> mapForInstallTrustCertificatesIpSec = new HashMap<>();
        final Node xmlNode = request.getXmlRepresntationOfNode();
        final String subjectAltNode = xmlNode.getSubAltName();
        final InstallTrustCertificates installTrustCertificates = xmlNode.getInstallTrustCertificates();
        final boolean removeTrustOnFailure = installTrustCertificates.isRemoveTrustOnFailure();
        final String trustedCertPath = installTrustCertificates.getTrustedCertificateFilePath();
        mapForInstallTrustCertificatesIpSec.put(SUB_ALT_NAME, subjectAltNode);
        mapForInstallTrustCertificatesIpSec.put(TRUST_CERTS, trustedCertPath);
        mapForInstallTrustCertificatesIpSec.put(REMOVE_TRUST, removeTrustOnFailure + "");
        return mapForInstallTrustCertificatesIpSec;
    }
}