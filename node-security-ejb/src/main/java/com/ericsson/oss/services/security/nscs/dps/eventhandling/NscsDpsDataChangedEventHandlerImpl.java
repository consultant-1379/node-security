/*------------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2018
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.dps.eventhandling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.WorkflowHandler;
import com.ericsson.nms.security.nscs.api.exception.WorkflowHandlerException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.data.DpsNodeLoader;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CmFunction;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComConnectivityInformation;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppConnectivityInformation;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Security;
import com.ericsson.nms.security.nscs.dps.eventhandling.NscsDPSEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.AttributeChangeData;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsAttributeChangedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsDataChangedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsObjectCreatedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsObjectDeletedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.EventType;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nscs.nodes.cache.service.interfaces.NscsNodesCacheHandler;
import com.ericsson.oss.services.security.nscs.dps.availability.NscsDpsAvailabilityStatus;
import com.ericsson.nms.security.nscs.api.privacy.PrivacyHandler;
import com.ericsson.nms.security.nscs.data.ModelDefinition;

public class NscsDpsDataChangedEventHandlerImpl implements NscsDpsDataChangedEventHandler {

    private static final List<NscsDPSEvent> avcExpectedByNodeSecurity = new ArrayList<NscsDPSEvent>();
    static {
        /**
         * WORKFLOW ONLY EVENTS
         */
        // OLD USELESS EVENT
        // Ns: ERBS_NODE_MODEL, Mo: IpHostLink, attribute: ipAddress
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.ME_CONTEXT.managedElement.ipOam.ip.ipHostLink.type(),
                Model.ME_CONTEXT.managedElement.ipOam.ip.ipHostLink.namespace(), "ipAddress", false, false));
        // Ns: ERBS_NODE_MODEL, Mo: Security, attribute: certEnrollState
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), Security.CERT_ENROLL_STATE, true, false));
        // Ns: ERBS_NODE_MODEL, Mo: Security, attribute: trustedCertificateInstallationFailure
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), Security.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE, true,
                false));
        // Ns: RBS_NODE_MODEL, Mo: Security, attribute: trustedCertificateInstallationFailure
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.ME_CONTEXT.rbsManagedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.rbsManagedElement.systemFunctions.security.namespace(), Security.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE, true,
                false));
        // Ns: RNC_NODE_MODEL, Mo: Security, attribute: trustedCertificateInstallationFailure
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.ME_CONTEXT.rncManagedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.rncManagedElement.systemFunctions.security.namespace(), Security.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE, true,
                false));
        // Ns: MGW_NODE_MODEL, Mo: Security, attribute: trustedCertificateInstallationFailure
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.ME_CONTEXT.mgwManagedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.mgwManagedElement.systemFunctions.security.namespace(), Security.TRUSTED_CERTIFICATE_INSTALLATION_FAILURE, true,
                false));
        // Ns: ERBS_NODE_MODEL, Mo: Security, attribute: localAADatabaseInstallationFailure
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), Security.LOCAL_AA_DATABASE_INSTALLATION_FAILURE, true, false));
        // Ns: RBS_NODE_MODEL, Mo: Security, attribute: localAADatabaseInstallationFailure
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.ME_CONTEXT.rbsManagedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.rbsManagedElement.systemFunctions.security.namespace(), Security.LOCAL_AA_DATABASE_INSTALLATION_FAILURE, true,
                false));
        // Ns: RNC_NODE_MODEL, Mo: Security, attribute: localAADatabaseInstallationFailure
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.ME_CONTEXT.rncManagedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.rncManagedElement.systemFunctions.security.namespace(), Security.LOCAL_AA_DATABASE_INSTALLATION_FAILURE, true,
                false));
        // Ns: MGW_NODE_MODEL, Mo: Security, attribute: localAADatabaseInstallationFailure
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.ME_CONTEXT.mgwManagedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.mgwManagedElement.systemFunctions.security.namespace(), Security.LOCAL_AA_DATABASE_INSTALLATION_FAILURE, true,
                false));
        
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.NETWORK_ELEMENT.cppConnectivityInformation.type(),
                Model.NETWORK_ELEMENT.cppConnectivityInformation.namespace(), CppConnectivityInformation.HTTPS, true, false));

        /**
         * WORKFLOW AND CACHE EVENTS
         */
        // Ns: CPP_MED, Mo: CppConnectivityInformation, attribute: ipAddress
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.NETWORK_ELEMENT.cppConnectivityInformation.type(),
                Model.NETWORK_ELEMENT.cppConnectivityInformation.namespace(), CppConnectivityInformation.IPADDRESS, true, true));
        // Ns: CPP_MED, Mo: CppConnectivityInformation, attribute: ipAddress
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.NETWORK_ELEMENT.comConnectivityInformation.type(),
                Model.NETWORK_ELEMENT.comConnectivityInformation.namespace(), ComConnectivityInformation.IPADDRESS, true, true));

        /**
         * CACHE ONLY EVENTS
         */
        // Ns: ERBS_NODE_MODEL, Mo: Security, attribute: certEnrollState
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), Security.OPERATIONAL_SECURITY_LEVEL, false, true));
        // Ns: OSS_NE_CM_DEF, Mo: CmFunction, attribute: syncStatus
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.NETWORK_ELEMENT.cmFunction.type(), Model.NETWORK_ELEMENT.cmFunction.namespace(),
                CmFunction.SYNC_STATUS, false, true));
        /**
         * PRIVACY ONLY EVENTS
         */
        avcExpectedByNodeSecurity.add(new NscsDPSEvent(Model.ME_CONTEXT.rncManagedElement.systemFunctions.licensing.rncFeature.type(),
                Model.ME_CONTEXT.rncManagedElement.systemFunctions.licensing.rncFeature.namespace(),
                ModelDefinition.RncFeature.FEATURE_STATE,false, false,true));
    }

    private final Logger logger = LoggerFactory.getLogger(NscsDpsDataChangedEventHandlerImpl.class);

    @EServiceRef
    private WorkflowHandler workflowHandler;

    @Inject
    private NscsDpsAvailabilityStatus nscsDpsAvailabilityStatus;

    @Inject
    private NscsNodesCacheHandler nscsNodeStatusDataHandler;

    @Inject
    private DpsNodeLoader dpsNodeLoader;

    @Inject
    private PrivacyHandler privacyHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    public void processDpsDataChangedEvent(final DpsDataChangedEvent event) {
        if (event != null && event.getPoId() != null) {
            if (EventType.ATTRIBUTE_CHANGED.equals(event.getEventType())) {
                final DpsAttributeChangedEvent attributeChangedEvent = (DpsAttributeChangedEvent) event;
                final NodeRef nodeRef = new NodeRef(attributeChangedEvent.getFdn());
                logger.debug("Event received is of type [" + event.getEventType() + "] for nodeFdn [" + nodeRef.getFdn() + "], nodeName ["
                        + nodeRef.getName() + "].");
                for (final AttributeChangeData attributeChangeData : attributeChangedEvent.getChangedAttributes()) {
                    final String attributeName = attributeChangeData.getName();
                    final NscsDPSEvent nscsEvent = getNscsDPSEventFromDpsEventContent(event.getType(), event.getNamespace(), attributeName);
                    if (attributeChangeData.getOldValue() != null && nscsEvent != null) {
                        if (nscsEvent.isWorkflowNotificable()) {
                            final StringBuilder messageBuffer = new StringBuilder(50);
                            messageBuffer.delete(0, messageBuffer.length());
                            messageBuffer.append(nscsEvent.getWfEventName());
                            final String message = messageBuffer.toString();
                            logger.debug("Dispatching AVC notification to workflow. Message : '" + message + "', Target : " + nodeRef);
                            try {
                                workflowHandler.dispatchMessage(nodeRef, message);
                            } catch (final WorkflowHandlerException e) {
                                logger.error("Cannot dispatch message to workflow for [" + nodeRef
                                        + "], no execution is in waiting state for message[" + message + "]");
                            } catch (final Exception e) {
                                logger.error("Exception [" + e.getClass().getCanonicalName() + "] occurred dispatching AVC event to workflow for ["
                                        + nodeRef + "] message [" + message + "]");
                            }
                        }
                        if (nscsEvent.isCacheNotificable()) {
                            logger.debug("Dispatching AVC notification to cache for nodeName [" + nodeRef.getName() + "], attr [" + attributeName
                                    + "], newValue [" + attributeChangeData.getNewValue() + "]");
                            updateNodeSecurityCache(event.getEventType(), nodeRef.getName(), attributeName, attributeChangeData.getNewValue());
                        }
                        if (nscsEvent.isPrivacyNotificable()) {
                            logger.debug("Dispatching AVC notification for privacy of nodeName [{}], attr [{}], newValue [{}]",
                                                       nodeRef.getName(), attributeName, attributeChangeData.getNewValue());
                            privacyHandler.updateAnonymized(attributeChangedEvent.getFdn(), nodeRef.getName(), attributeName, attributeChangeData.getNewValue());
                        }
                    } else {
                        logger.debug("Received AVC [Type: " + event.getType() + ", Namespace: " + event.getNamespace() + ", Attribute: "
                                + attributeName + "] not expected by node security...");
                    }
                }
            } else if (EventType.OBJECT_CREATED.equals(event.getEventType())) {
                final DpsObjectCreatedEvent objectCreatedEvent = (DpsObjectCreatedEvent) event;
                final NodeRef nodeRef = new NodeRef(objectCreatedEvent.getFdn());
                logger.debug("Event received is of type [" + event.getEventType() + "] for nodeFdn [" + nodeRef.getFdn() + "], nodeName ["
                        + nodeRef.getName() + "].");
                for (final Map.Entry<String, Object> entry : objectCreatedEvent.getAttributeValues().entrySet()) {
                    logger.debug("Attribute value key [" + entry.getKey() + "] object value [" + entry.getValue() + "].");
                    final NscsDPSEvent nscsDPSEvent = getNscsDPSEventFromDpsEventContent(event.getType(), event.getNamespace(), entry.getKey());
                    if (nscsDPSEvent != null) {
                        if (nscsDPSEvent.isCacheNotificable()) {
                            logger.debug("Dispatching CREATE notification to cache for nodeName [" + nodeRef.getName() + "], attr [" + entry.getKey()
                                    + "], value [" + entry.getValue() + "]");
                            updateNodeSecurityCache(event.getEventType(), nodeRef.getName(), entry.getKey(), entry.getValue());
                        } else {
                            logger.debug(
                                    "DPS CREATE notification NOT REQUIRED to cache [" + nscsDPSEvent + "], nodeName [" + nodeRef.getName() + "]");
                        }
                    } else {
                        logger.debug("Received CREATE [Type: " + event.getType() + ", Namespace: " + event.getNamespace() + ", Attribute: "
                                + entry.getKey() + "] not expected by node security...");
                    }
                }
            } else if (EventType.OBJECT_DELETED.equals(event.getEventType())) {
                final DpsObjectDeletedEvent objectDeletedEvent = (DpsObjectDeletedEvent) event;
                final NodeRef nodeRef = new NodeRef(objectDeletedEvent.getFdn());
                logger.debug("Event received is of type [" + event.getEventType() + "] for nodeFdn [" + nodeRef.getFdn() + "], nodeName ["
                        + nodeRef.getName() + "].");
                for (final Map.Entry<String, Object> entry : objectDeletedEvent.getAttributeValues().entrySet()) {
                    logger.debug("Attribute value key [" + entry.getKey() + "] object value [" + entry.getValue() + "].");
                    final NscsDPSEvent nscsDPSEvent = getNscsDPSEventFromDpsEventContent(event.getType(), event.getNamespace(), entry.getKey());
                    if (nscsDPSEvent != null) {
                        if (nscsDPSEvent.isCacheNotificable()) {
                            logger.debug("Dispatching DPS DELETE notification to cache for nodeName [" + nodeRef.getName() + "], attr ["
                                    + entry.getKey() + "], value [" + entry.getValue() + "]");
                            updateNodeSecurityCache(event.getEventType(), nodeRef.getName(), entry.getKey(), entry.getValue());
                        } else {
                            logger.debug(
                                    "DPS DELETE notification NOT REQUIRED to cache [" + nscsDPSEvent + "], nodeName [" + nodeRef.getName() + "]");
                        }
                    } else {
                        logger.debug("Received DELETE [Type: " + event.getType() + ", Namespace: " + event.getNamespace() + ", Attribute: "
                                + entry.getKey() + "] not expected by node security...");
                    }
                }
            } else {
                logger.debug("Event received is of type " + event.getEventType() + " and will be ignored");
            }
        } else {
            logger.warn("Invalid message received from DPS.\n" + event);
        }
    }

    /**
     * Updates NSCS node cache. If DPS is unavailable the DPS event is discarded. If node is not present in cache, the event from DPS can be ignored.
     *
     * @param eventType
     *            the event type received from DPS.
     * @param nodeName
     *            the name of involved node.
     * @param attribute
     *            the attribute name
     * @param value
     *            the attribute new value
     */
    private void updateNodeSecurityCache(final EventType eventType, final String nodeName, final String attribute, final Object value) {
        final String inputParams = "eventType [" + eventType + "], nodeName [" + nodeName + "], attr [" + attribute + "],  value [" + value.toString()
                + "]";
        // Reads DTO from cache
        final NodesConfigurationStatusRecord cacheDto = nscsNodeStatusDataHandler.getNode(nodeName);
        if (cacheDto != null) {
            // Node is present in cache
            logger.debug("Updating cache entry [" + nodeName + "] for " + inputParams);
            if (EventType.OBJECT_DELETED.equals(eventType)) {
                logger.debug("DELETE: cache entry [" + nodeName + "]: CONSUMED");
                nscsNodeStatusDataHandler.removeEntryFromNodeStatusCache(nodeName);
            } else if (EventType.OBJECT_CREATED.equals(eventType)) {
                if (nscsDpsAvailabilityStatus.isDpsAvailable()) {
                    // Reads from DPS the whole DTO
                    final NodesConfigurationStatusRecord dpsDto = dpsNodeLoader.getNode(nodeName);
                    if (dpsDto != null) {
                        logger.debug("CREATE: cache entry [" + nodeName + "]: CONSUMED");
                        nscsNodeStatusDataHandler.insertOrUpdateNode(nodeName, dpsDto);
                    } else {
                        logger.warn("CREATE: null DPS DTO: cache entry [" + nodeName + "]: DISCARDED " + inputParams);
                    }
                } else {
                    logger.warn("CREATE: DPS is unavailable: cache entry [" + nodeName + "]: DISCARDED " + inputParams);
                }
            } else if (EventType.ATTRIBUTE_CHANGED.equals(eventType)) {
                boolean performUpdate = true;
                NodesConfigurationStatusRecord dpsDto = null;
                if (CmFunction.SYNC_STATUS.equalsIgnoreCase(attribute)) {
                    performUpdate = false;
                    if (CmFunction.SyncStatusValue.UNSYNCHRONIZED.name().equalsIgnoreCase(value.toString())
                            || CmFunction.SyncStatusValue.SYNCHRONIZED.name().equalsIgnoreCase(value.toString())
                            || CmFunction.SyncStatusValue.PENDING.name().equalsIgnoreCase(value.toString())) {
                        performUpdate = true;
                    }
                }
                if (performUpdate) {
                    if (nscsDpsAvailabilityStatus.isDpsAvailable()) {
                        // Reads from DPS the whole DTO
                        dpsDto = dpsNodeLoader.getNode(nodeName);
                        if (dpsDto != null) {
                            logger.debug("AVC: cache entry [" + nodeName + "]: CONSUMED");
                            nscsNodeStatusDataHandler.insertOrUpdateNode(nodeName, dpsDto);
                        } else {
                            logger.warn("AVC: null DPS DTO: cache entry [" + nodeName + "]: DISCARDED " + inputParams);
                        }
                    } else {
                        logger.warn("AVC: DPS is unavailable: cache entry [" + nodeName + "]: DISCARDED " + inputParams);
                    }
                } else {
                    logger.debug("AVC: no update required for entry [" + nodeName + "]: IGNORED");
                }
            }
        } else {
            logger.debug("Cache entry [" + nodeName + "] not present: IGNORED " + inputParams);
        }
    }

    /**
     * Gets if current AVC event is an expected AVC event for NSCS.
     *
     * @param moType
     *            the MO type
     * @param moNamespace
     *            the MO namespace
     * @param attributeName
     *            the MO attribute name
     * @return an NSCS DPS event if expected or null if unexpected
     */
    private NscsDPSEvent getNscsDPSEventFromDpsEventContent(final String moType, final String moNamespace, final String attributeName) {
        NscsDPSEvent nscsDPSEvent = null;
        for (final NscsDPSEvent expectedEvent : avcExpectedByNodeSecurity) {
            if (expectedEvent.getMoNamespace().equals(moNamespace) && expectedEvent.getMoType().equals(moType)
                    && expectedEvent.getAttributeName().equals(attributeName)) {
                nscsDPSEvent = expectedEvent;
                logger.debug("Found expected NscsDPSEvent [" + nscsDPSEvent + "]");
                break;
            }
        }
        return nscsDPSEvent;
    }

}
