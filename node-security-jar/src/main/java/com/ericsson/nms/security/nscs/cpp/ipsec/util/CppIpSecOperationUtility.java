/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cpp.ipsec.util;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes;
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.IpSecRequestType;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import org.slf4j.Logger;

import javax.inject.Inject;

public class CppIpSecOperationUtility {
    @Inject
    private Logger logger;

    @Inject
    CppIpSecStatusUtility cppIpSecStatusUtility;

    @Inject
    private NscsCMReaderService reader;

    /**
     * Verify if the sequence of ipsec activation/deactivation commands is correct
     * @param node: the input node
     * @return: if the command is in the proper sequence (the workflow for the node is eligible to start)
     */
    public CppIpSecConfigurationTypeResponse checkIpSecOperation(final Nodes.Node node) {
        final String nodeFdn = node.getNodeFdn();
        String message = "";
        String suggestedSolution = "";
        String currentConfiguration;
        boolean isValid = true;
        final IpSecRequestType requestType = CppIpSecStatusUtility.getIpSecRequestTypeFromInput(node);
        switch (requestType) {
            case IP_SEC_ENABLE_CONF1:
                currentConfiguration = getCurrentConfiguration(nodeFdn);
                if (currentConfiguration.equalsIgnoreCase(CppIpSecStatusUtility.CONFIGURATION_1)) {
                    message = String.format("No change required, IpSec is already enabled on: %s with requested configuration1.", nodeFdn);
                    suggestedSolution = "Deactivate configuration one and then applying again configuration one.";
                    logger.info("{} {}", message,suggestedSolution);
                    isValid = false;
                } else if (currentConfiguration.equalsIgnoreCase(CppIpSecStatusUtility.CONFIGURATION_2)) {
                    message = String.format(
                            "IpSec is enabled on: %s with configuration2.", nodeFdn);
                    suggestedSolution = "Deactivate configuration two before applying configuration one.";
                    logger.info("{} {}", message,suggestedSolution);
                    isValid = false;
                }
                break;
            //To be updated and verified as part of MR 55921
            case IP_SEC_ENABLE_CONF2:
                currentConfiguration = getCurrentConfiguration(nodeFdn);
                if (currentConfiguration.equalsIgnoreCase(CppIpSecStatusUtility.CONFIGURATION_2)) {
                    message = String.format("No change required, IpSec is already enabled on: %s with requested configuration2.", nodeFdn);
                    suggestedSolution = "Deactivate configuration two and then apply again configuration two.";
                    logger.info("{} {}", message,suggestedSolution);
                    isValid = false;
                } else if (currentConfiguration.equalsIgnoreCase(CppIpSecStatusUtility.CONFIGURATION_1)) {
                    message = String.format(
                            "IpSec is enabled on: %s with configuration1.",
                            nodeFdn);
                    suggestedSolution = "Deactivate configuration one before applying configuration two.";
                    logger.info("{} {}", message,suggestedSolution);
                    isValid = false;
                }
                break;
            case IP_SEC_DISABLE:
                logger.info("IpSec disable flow for node [{}] ", node.getNodeFdn());
                if (!isIpSecOmEnabled(nodeFdn)) {
                    message = String.format("No change required, IpSec was already disabled on: %s", nodeFdn);
                    suggestedSolution = "Activate configuration.";
                    logger.info("{} {}", message,suggestedSolution);
                    isValid = false;
                }
                break;
            case IP_SEC_INSTALL_CERTS:
                break;
        }

        CppIpSecConfigurationTypeResponse cppIpSecConfRspType = new CppIpSecConfigurationTypeResponse();
        cppIpSecConfRspType.setMessage(message);
        cppIpSecConfRspType.setSuggestedSolution(suggestedSolution);
        cppIpSecConfRspType.setValid(isValid);

        return cppIpSecConfRspType;
    }

    private String getCurrentConfiguration(final String nodeFdn) {
        String currentConfiguration = "";
        final NodeReference nodeRef = new NodeRef(nodeFdn);
        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
        cppIpSecStatusUtility.setConfigurationRequirement(true);
        final String featureState = cppIpSecStatusUtility.getIpSecFeatureState(normNode);
        final boolean isIpSecOMActivated = cppIpSecStatusUtility.isOMActivated(normNode, featureState);

        if (isIpSecOMActivated) {
            final boolean isIpSecTrafficActivated = cppIpSecStatusUtility.isTrafficActivated(normNode, featureState);
            logger.debug("Traffic is activated for {} : {}", nodeFdn, isIpSecTrafficActivated);
            currentConfiguration = cppIpSecStatusUtility.getOmConfigurationType();
        }
        return currentConfiguration;
    }

    private boolean isIpSecOmEnabled(final String nodeFdn) {
        final NodeReference nodeRef = new NodeRef(nodeFdn);
        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
        cppIpSecStatusUtility.setConfigurationRequirement(true);
        final String featureState = cppIpSecStatusUtility.getIpSecFeatureState(normNode);
        return cppIpSecStatusUtility.isOMActivated(normNode, featureState);
    }
}
