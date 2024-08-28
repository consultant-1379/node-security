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
package com.ericsson.nms.security.nscs.cpp.ipsec.util;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.*;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.EnableOMConfiguration1;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.EnableOMConfiguration2;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.SiteBasic;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.SiteBasic.*;
import com.ericsson.nms.security.nscs.cpp.ipsec.wf.IpSecRequestType;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpAccessHostEt;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpHostLink;
import com.ericsson.nms.security.nscs.data.ModelDefinition.IpSec;
import com.ericsson.nms.security.nscs.data.ModelDefinition.VpnInterface;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

/**
 * Utility class having method to get IpSec status for OM/traffic.
 */
public class CppIpSecStatusUtility {
    public static final String VPN_INTERFACE = "VpnInterface";
    public static final String ACTIVATED = "ACTIVATED";
    public static final String DEACTIVATED = "DEACTIVATED";
    public static final String UNKNOWN = "UNKNOWN";
    private boolean isConfigurationRequired = false;
    public static final String CONFIGURATION_1 = "(Configuration 1)";
    public static final String CONFIGURATION_2 = "(Configuration 2)";
    private ConfigurationInfo configurationInfo = null;
    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    /**
     * Get IpSec FeatureState from Node provided.
     * If Node is not accessible or FEATURE_STATE attribute is not found than return state as UNKNOWN
     *
     * @param nodeRef: {@link NodeReference}
     * @return ACTIVATED, DEACTIVATED, UNKNOWN
     */
    public String getIpSecFeatureState(final NodeReference nodeRef) {
        String featureState = UNKNOWN;
        CmResponse cmRespFstate = null;
        try {
            cmRespFstate = reader.getMOAttribute(nodeRef, Model.ME_CONTEXT.managedElement.ipSystem.ipSec.type(),
                    Model.ME_CONTEXT.managedElement.ipSystem.ipSec.namespace(), IpSec.FEATURE_STATE);
        } catch (DataAccessException | DataAccessSystemException daEx) {
            logger.debug("Unable to fetch attribute [{}]", IpSec.FEATURE_STATE);
            logger.error(daEx.getMessage());
        }
        if (cmRespFstate != null) {
            for (final CmObject cmObject : cmRespFstate.getCmObjects()) {
                String nodeName = "";
                if (cmObject.getFdn() != null) {
                    nodeName = extractNodeName(cmObject.getFdn());
                    logger.debug("Node name [{}]", nodeName);
                }

                final Object obj = cmObject.getAttributes().get(IpSec.FEATURE_STATE);
                if (obj != null) {
                    featureState = (String) obj;
                }
                logger.debug("featureState [{}] for node [{}]", featureState, nodeName);
            }
        }
        return featureState;

    }

    /**
     * Checks if O&M is activated on the Node.
     *
     * @param nodeRef: {@link NodeReference}
     * @param featureState: ACTIVATED, DEACTIVATED, UNKNOWN
     * @return True if ACTIVE, False if NON-ACTIVE
     */
    public boolean isOMActivated(final NodeReference nodeRef, final String featureState) {
        boolean isOMActivated = false;
        configurationInfo = null;
        if (featureState.equals(ACTIVATED)) {
            logger.debug("O&M - IpSec feature state is activated for the node [{}]", extractNodeName(nodeRef.getFdn()));
            CmResponse cmResponseHlink = null;
            try {
                cmResponseHlink = reader.getMOAttribute(nodeRef, Model.ME_CONTEXT.managedElement.ipOam.ip.ipHostLink.type(),
                        Model.ME_CONTEXT.managedElement.ipOam.ip.ipHostLink.namespace(), IpHostLink.IP_INTERFACE_MO_REF);
            } catch (DataAccessException | DataAccessSystemException daEx) {
                logger.debug("O&M - Unable to fetch attribute : [{}]", IpAccessHostEt.IP_INTERFACE_MO_REF);
                logger.error(daEx.getMessage());
            }
            if (cmResponseHlink != null) {
                for (final CmObject cmObjIntfs : cmResponseHlink.getCmObjects()) {
                    String intfMoRefValue = (String) cmObjIntfs.getAttributes().get(IpHostLink.IP_INTERFACE_MO_REF);
                    if (intfMoRefValue != null && intfMoRefValue.contains(VPN_INTERFACE)) {
                        intfMoRefValue = prepareFdn(extractNodeName(cmObjIntfs.getFdn()), intfMoRefValue);
                        CmResponse cmRespVpnIntfs = null;
                        try {
                            cmRespVpnIntfs = reader.getMoByFdn(intfMoRefValue);
                        } catch (DataAccessException | DataAccessSystemException daEx) {
                            logger.debug("O&M - Unable to fetch MO [{}]", intfMoRefValue);
                            logger.error(daEx.getMessage());
                        }
                        if (cmRespVpnIntfs != null) {
                            for (final CmObject cmObjVpn : cmRespVpnIntfs.getCmObjects()) {
                                final String vpnInterfaceId = (String) cmObjVpn.getAttributes().get(VpnInterface.VPN_INTERFACE_ID);
                                logger.debug("O&M - vpnInterfaceId [{}]", vpnInterfaceId);
                                isOMActivated = true;
                                logger.debug("O&M - Activated on Node [{}] VpnInterfaceId [{}]", extractNodeName(nodeRef.getFdn()), vpnInterfaceId);
                                if (isConfigurationRequired) {
                                    String ipAccHostEtRef = (String) cmObjVpn.getAttributes().get(VpnInterface.IP_ACCESS_HOST_ET_REF);
                                    logger.debug("O&M - ipAccHostEtRef : [{}]", ipAccHostEtRef);
                                    if (ipAccHostEtRef != null && !ipAccHostEtRef.isEmpty()) {
                                        try {
                                            ipAccHostEtRef = prepareFdn(extractNodeName(cmObjIntfs.getFdn()), ipAccHostEtRef);
                                            final CmResponse cmRespIpAccHstEtRef = reader.getMoByFdn(ipAccHostEtRef);
                                            if (cmRespIpAccHstEtRef != null) {
                                                for (final CmObject cmObjHostEt : cmRespIpAccHstEtRef.getCmObjects()) {
                                                    final String ipAccHostEtId = (String) cmObjHostEt.getAttributes().get(
                                                            IpAccessHostEt.IP_ACCESS_HOST_ET_ID);
                                                    if (ipAccHostEtId != null && !ipAccHostEtId.isEmpty()) {
                                                        logger.debug("O&M - ipAccHostEtId : [{}]", ipAccHostEtId);
                                                        configurationInfo = new ConfigurationInfo();
                                                        configurationInfo.setOmIpAccessHostEtId(ipAccHostEtId);
                                                    }
                                                }
                                            }
                                        } catch (DataAccessException | DataAccessSystemException daEx) {
                                            logger.debug("O&M - Unable to fetch attribute [{}]", ipAccHostEtRef);
                                            logger.error(daEx.getMessage());
                                        }

                                    }
                                }
                                break;
                            }
                        }
                    } else {
                        isOMActivated = false;
                        logger.debug("O&M - Deactivated on Node : [{}]", extractNodeName(nodeRef.getFdn()));
                    }
                    if (isOMActivated) {
                        break;
                    }
                }
            }

        }
        return isOMActivated;
    }

    /**
     * Checks if Traffic is activated on the Node.
     *
     * @param nodeRef: {@link NodeReference}
     * @param featureState: ACTIVATED, DEACTIVATED, UNKNOWN
     * @return True if ACTIVE, False if NON-ACTIVE
     */
    public boolean isTrafficActivated(final NodeReference nodeRef, final String featureState) {
        boolean isActivated = false;
        if (featureState.equals(ACTIVATED)) {
            CmResponse cmRespIpIntfMoRef = null;
            try {
                cmRespIpIntfMoRef = reader.getMOAttribute(nodeRef, Model.ME_CONTEXT.managedElement.ipSystem.ipAccessHostEt.type(),
                        Model.ME_CONTEXT.managedElement.ipSystem.ipAccessHostEt.namespace(), IpAccessHostEt.IP_INTERFACE_MO_REF);
            } catch (DataAccessException | DataAccessSystemException daEx) {
                logger.debug("Traffic - Unable to fetch attribute [{}]", IpAccessHostEt.IP_INTERFACE_MO_REF);
                logger.error(daEx.getMessage());
            }
            if (cmRespIpIntfMoRef != null) {
                logger.debug("Traffic - No of cm_resp_ipInterfaceMoRef [{}]", cmRespIpIntfMoRef.getCmObjects().size());
                for (final CmObject cmObjIntf : cmRespIpIntfMoRef.getCmObjects()) {

                    String intfMoRefValue = (String) cmObjIntf.getAttributes().get(IpHostLink.IP_INTERFACE_MO_REF);
                    logger.debug("Traffic - intfMoRefValue [{}]", intfMoRefValue);
                    if (intfMoRefValue != null && intfMoRefValue.contains(VPN_INTERFACE)) {
                        intfMoRefValue = prepareFdn(extractNodeName(cmObjIntf.getFdn()), intfMoRefValue);
                        CmResponse cmRespVpnIntf = null;
                        try {
                            cmRespVpnIntf = reader.getMoByFdn(intfMoRefValue);
                        } catch (DataAccessException | DataAccessSystemException daEx) {
                            logger.debug("Traffic - Unable to fetch MO [{}]", intfMoRefValue);
                            logger.error(daEx.getMessage());
                        }
                        if (cmRespVpnIntf != null) {
                            for (final CmObject cmObjVpn : cmRespVpnIntf.getCmObjects()) {
                                final String vpnInterfaceId = (String) cmObjVpn.getAttributes().get(VpnInterface.VPN_INTERFACE_ID);
                                logger.debug("Traffic - vpnInterfaceId [{}]", vpnInterfaceId);
                                isActivated = true;
                                logger.debug("Traffic - Activated on Node [{}]", extractNodeName(nodeRef.getFdn()));
                                if (isConfigurationRequired) {
                                    String ipAccHostEtRef = (String) cmObjVpn.getAttributes().get(VpnInterface.IP_ACCESS_HOST_ET_REF);
                                    if (ipAccHostEtRef != null && !ipAccHostEtRef.isEmpty()) {
                                        try {
                                            ipAccHostEtRef = prepareFdn(extractNodeName(cmObjIntf.getFdn()), ipAccHostEtRef);
                                            final CmResponse cmRespIpAccHstEtRef = reader.getMoByFdn(ipAccHostEtRef);
                                            if (cmRespIpAccHstEtRef != null) {
                                                for (final CmObject cmObjHostEt : cmRespIpAccHstEtRef.getCmObjects()) {
                                                    final String ipAccHostEtId = (String) cmObjHostEt.getAttributes().get(
                                                            IpAccessHostEt.IP_ACCESS_HOST_ET_ID);
                                                    if (ipAccHostEtId != null && !ipAccHostEtId.isEmpty()) {
                                                        if (configurationInfo != null) {
                                                            logger.debug("Traffic - ipAccHostEtId [{}]", ipAccHostEtId);
                                                            configurationInfo.setTrafficIpAccessHostEtId(ipAccHostEtId);
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (DataAccessException | DataAccessSystemException daEx) {
                                            logger.debug("Traffic - Unable to fetch attribute [{}]", ipAccHostEtRef);
                                            logger.error(daEx.getMessage());
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    } else {
                        isActivated = false;
                        logger.debug("Traffic - Deactivated on Node [{}]", extractNodeName(nodeRef.getFdn()));
                    }
                    if (isActivated) {
                        break;
                    }
                }
            }
        }
        logger.debug("Traffic - isTrafficActivated isActivated [{}]", isActivated);
        return isActivated;

    }

    public String getOmConfigurationType () {
        if(configurationInfo == null) {
            throw new IllegalArgumentException("configuration info is null");
        }
        return configurationInfo.getOmConfigurationType();
    }

    /**
     * Adding MeContext to the provided FDN
     *
     * @param nodeName: the name of the node
     * @param partialFdn the partial fdn
     * @return {@link String} fdn of node
     */
    private String prepareFdn(final String nodeName, final String partialFdn) {
        /* 20160503 TORF-112729: changed to fix ipsec errors on getting actual configuration */
        return /* Model.ME_CONTEXT.withNames(nodeName).fdn() + "," + */partialFdn;
    }

    /**
     * Extracts NodeName from provided FDN
     *
     * @param fdn : the fdn of the node
     * @return {@link String} node name
     */
    private String extractNodeName(final String fdn) {
        return Model.ME_CONTEXT.extractName(fdn);
    }

    /**
     * Sets to check if generation of Configuration Info is required or not.
     *
     * @param isRequired: to set configuration required
     */
    public void setConfigurationRequirement(final boolean isRequired) {
        isConfigurationRequired = isRequired;
    }

    /**
     * Fetches configurationInfo filled with configuration related information
     *
     * @return: the ipsec configuration information
     */
    public ConfigurationInfo getConfigurationInfo() {
        return configurationInfo;
    }

    /**
     * Class to hold Configuration type of the OM
     */
    public class ConfigurationInfo {
        private String omIpAccessHostEtId;
        private String trafficIpAccessHostEtId;

        public void setOmIpAccessHostEtId(final String omIpAccessHostEtId) {
            this.omIpAccessHostEtId = omIpAccessHostEtId;
        }

        public void setTrafficIpAccessHostEtId(final String trafficIpAccessHostEtId) {
            this.trafficIpAccessHostEtId = trafficIpAccessHostEtId;
        }

        public String getOmConfigurationType() {
            if (omIpAccessHostEtId != null) {
                if (trafficIpAccessHostEtId != null) {
                    return omIpAccessHostEtId.equals(trafficIpAccessHostEtId) ? CONFIGURATION_2 : CONFIGURATION_1;
                } else {
                    return CONFIGURATION_1;
                }
            }
            return "";
        }
    }

    /*
     * XML utility functions
     */
    public static IpSecRequestType getIpSecRequestTypeFromInput(final Node xmlNode) {
        IpSecRequestType ipSecRequestType = null;
        if (isEnableIpSecConf1Operation(xmlNode) || isSiteBasicProvided(xmlNode, true)) {
            ipSecRequestType = IpSecRequestType.IP_SEC_ENABLE_CONF1;
        } else if (isEnableIpSecConf2Operation(xmlNode)) {
            ipSecRequestType = IpSecRequestType.IP_SEC_ENABLE_CONF2;
        } else if (isDisableIpSecOperation(xmlNode) || isSiteBasicProvided(xmlNode, false)) {
            ipSecRequestType = IpSecRequestType.IP_SEC_DISABLE;
        } else if (isInstallTrustCertificatesOperation(xmlNode)) {
            ipSecRequestType = IpSecRequestType.IP_SEC_INSTALL_CERTS;
        } else {
            throw new IllegalArgumentException("Invalid node xml, does not match any operation type (ENABLE, DISABLE, INSTALL_CERT)");
        }
        return ipSecRequestType;
    }

    /**
     * Method to check whether tag <SiteBasic> is present in input or not. Also trigger corresponding workflow based on featureState given in Input
     *
     * @param xmlNode
     *            : {@link Node} XML representation for a node data
     * @return: flag to identity whether tag <SiteBasic> is present
     */
    public static boolean isSiteBasicProvided(final Node xmlNode, boolean isForActivation) {

        final SiteBasic siteBasic = xmlNode.getSiteBasic();

        if (siteBasic != null) {

            final ActivatedOrDeactivated featureState = getIpSecFeatureStateFromInput(siteBasic);

            if (isForActivation) {
                if (featureState == ActivatedOrDeactivated.ACTIVATED) {
                    return true;
                }
            } else {
                if (featureState == ActivatedOrDeactivated.DEACTIVATED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method to get featureState provided in <Ipsec> tag
     *
     * @param: the siteBasic
     *
     * @return ActivatedOrDeactivated status
     */
    public static ActivatedOrDeactivated getIpSecFeatureStateFromInput(final SiteBasic siteBasic) {

        final List<Object> elements = siteBasic.getFormatOrIpOrENodeBFunction();

        int indexOfIpSystem = 0;

        for (final Object obj : elements) {

            if (obj instanceof IpSystem) {
                int indexOfIpSec = 0;

                final IpSystem ipSys = (IpSystem) elements.get(indexOfIpSystem);

                final List<Object> innerElements = ipSys.getIpAccessHostEtOrVpnInterfaceOrDnsResolver();

                for (final Object innerObj : innerElements) {

                    if (innerObj instanceof com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.SiteBasic.IpSystem.IpSec) {

                        com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.SiteBasic.IpSystem.IpSec ipSec = (com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node.SiteBasic.IpSystem.IpSec) innerElements
                                .get(indexOfIpSec);

                        return ipSec.getFeatureState();

                    }

                    indexOfIpSec++;
                }

            }
            indexOfIpSystem++;
        }

        return ActivatedOrDeactivated.DEACTIVATED;
    }

    /**
     * Method to check whether operation is to enable IpSec or not.
     *
     * @param xmlNode
     *            : {@link Node} XML representation for a node data
     * @return: the enable/disable flag status
     */
    public static boolean isDisableIpSecOperation(final Node xmlNode) {
        return null != xmlNode && null != xmlNode.getDisableOMConfiguration();
    }

    /**
     * Method to check whether operation is to install trust certificates only
     *
     * @param xmlNode
     *            : {@link Node} XML representation for a node data
     * @return: the trust certificate installation only configuration
     */
    public static boolean isInstallTrustCertificatesOperation(final Node xmlNode) {
        return null != xmlNode && null != xmlNode.getInstallTrustCertificates();
    }

    /**
     * Method to check whether operation is to enable IpSec with configuration1 or not.
     *
     * @param xmlNode
     *            : {@link Node} XML representation for a node data
     * @return: the enabling of the ipsec configuration
     */
    public static boolean isEnableIpSecConf1Operation(final Node xmlNode) {
        boolean enableIpSecOperation = false;
        final EnableOMConfiguration1 conf1 = xmlNode.getEnableOMConfiguration1();
        if (conf1 != null) {
            enableIpSecOperation = true;
        }
        return enableIpSecOperation;
    }

    /**
     * Method to check whether operation is to enable IpSec with configuration2 or not.
     *
     * @param xmlNode
     *            : {@link Node} XML representation for a node data
     * @return: the enabling of the ipsec configuration two
     */
    public static boolean isEnableIpSecConf2Operation(final Node xmlNode) {
        boolean enableIpSecOperation = false;
        final EnableOMConfiguration2 conf2 = xmlNode.getEnableOMConfiguration2();
        if (conf2 != null) {
            enableIpSecOperation = true;
        }
        return enableIpSecOperation;
    }

    /**
     * Gets OAM vlan id.
     *
     * @param nodeRef
     *            {@link NodeRef}
     * @return vlan id.
     */
    public Integer getOamVlanId(final NodeReference nodeRef) {
        Integer vlanId = 0;
        final NormalizableNodeReference normNode = reader.getNormalizableNodeReference(nodeRef);
        logger.info("Fetching VlanId of IPSEC for the node [{}]", extractNodeName(normNode.getFdn()));
        CmResponse cmResponseHlink = null;
        try {
            cmResponseHlink = reader.getMOAttribute(normNode, Model.ME_CONTEXT.managedElement.ipOam.ip.ipHostLink.type(),
                    Model.ME_CONTEXT.managedElement.ipOam.ip.ipHostLink.namespace(), IpHostLink.IP_INTERFACE_MO_REF);
        } catch (DataAccessException | DataAccessSystemException daEx) {
            logger.debug("O&M - Unable to fetch attribute : [{}]", IpAccessHostEt.IP_INTERFACE_MO_REF);
            logger.error(daEx.getMessage());
        }
        if (cmResponseHlink != null) {
            for (final CmObject cmObjIntfs : cmResponseHlink.getCmObjects()) {
                String intfMoRefValue = (String) cmObjIntfs.getAttributes().get(IpHostLink.IP_INTERFACE_MO_REF);
                if (intfMoRefValue != null && intfMoRefValue.contains(VPN_INTERFACE)) {
                    CmResponse cmRespVpnIntfs = null;
                    try {
                        cmRespVpnIntfs = reader.getMoByFdn(intfMoRefValue);
                    } catch (DataAccessException | DataAccessSystemException daEx) {
                        logger.debug("O&M - Unable to fetch MO [{}]", intfMoRefValue);
                        logger.error(daEx.getMessage());
                    }
                    if (cmRespVpnIntfs != null) {
                        for (final CmObject cmObjVpn : cmRespVpnIntfs.getCmObjects()) {
                            final String vpnInterfaceId = (String) cmObjVpn.getAttributes().get(VpnInterface.VPN_INTERFACE_ID);
                            logger.debug("O&M - vpnInterfaceId [{}]", vpnInterfaceId);
                            logger.debug("O&M - Activated on Node [{}] VpnInterfaceId [{}]", extractNodeName(normNode.getFdn()), vpnInterfaceId);
                            String ipAccHostEtRef = (String) cmObjVpn.getAttributes().get(VpnInterface.IP_ACCESS_HOST_ET_REF);
                            logger.debug("O&M - ipAccHostEtRef : [{}]", ipAccHostEtRef);
                            if (ipAccHostEtRef != null && !ipAccHostEtRef.isEmpty()) {
                                try {
                                    final CmResponse cmRespIpAccHstEtRef = reader.getMoByFdn(ipAccHostEtRef);
                                    if (cmRespIpAccHstEtRef != null) {
                                        for (final CmObject cmObjHostEt : cmRespIpAccHstEtRef.getCmObjects()) {
                                            logger.debug("O&M - cmObjHostEt : [{}]", cmObjHostEt);
                                            final String ipAccHostEtId = (String) cmObjHostEt.getAttributes()
                                                    .get(IpAccessHostEt.IP_ACCESS_HOST_ET_ID);
                                            if (ipAccHostEtId != null && !ipAccHostEtId.isEmpty()) {
                                                logger.debug("O&M - ipAccHostEtId : [{}]", ipAccHostEtId);
                                                String ipInterfaceRef = (String) cmObjHostEt.getAttributes().get(IpAccessHostEt.IP_INTERFACE_MO_REF);
                                                logger.debug("O&M - ipInterfaceRef [{}]", ipInterfaceRef);
                                                if (ipInterfaceRef != null && !ipInterfaceRef.isEmpty()) {
                                                    final CmResponse cmIpInterfaceRef = reader.getMoByFdn(ipInterfaceRef);
                                                    if (cmIpInterfaceRef != null) {
                                                        for (final CmObject cmObjIpInterface : cmIpInterfaceRef.getCmObjects()) {
                                                            vlanId = (Integer) cmObjIpInterface.getAttributes().get("vid");
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (DataAccessException | DataAccessSystemException daEx) {
                                    logger.debug("O&M - Unable to fetch attribute [{}]", ipAccHostEtRef);
                                    logger.error(daEx.getMessage());
                                }
                            }
                            break;
                        }
                    }
                } else {
                    if (intfMoRefValue != null && !intfMoRefValue.isEmpty()) {
                        logger.info("O&M - intfMoRefValue [{}]", intfMoRefValue);
                        try {
                            final CmResponse cmIpInterfaceRef = reader.getMoByFdn(intfMoRefValue);
                            if (cmIpInterfaceRef != null) {
                                for (final CmObject cmObjIpInterface : cmIpInterfaceRef.getCmObjects()) {
                                    vlanId = (Integer) cmObjIpInterface.getAttributes().get("vid");
                                }
                            }
                        } catch (DataAccessException | DataAccessSystemException daEx) {
                            logger.debug("O&M - Unable to fetch attribute [{}]", intfMoRefValue);
                            logger.error(daEx.getMessage());
                        }
                    }
                }
            }
        }
        return vlanId;
    }
}