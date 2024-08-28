/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.privacy.impl;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.privacy.PrivacyHandler;


public class PrivacyHandlerImpl implements PrivacyHandler {

    private final Logger logger = LoggerFactory.getLogger(PrivacyHandlerImpl.class);

    @Inject
    private SystemRecorder systemRecorder;

    private static final String PRIVACYANONYMIZED = "PRIVACY.ANONYMIZED";
    private static final String NSCS_RESOURCE = "Node Security Service";
    private static final String ANONYMIZED_EVENT_DATA = "AnonymizedEventData";

    @Override
    public void updateAnonymized(final String nodeFdn, final String nodeName, final String attribute, final Object value) {

        logger.debug("Received AVC notification for privacy toNode [{}], attr [{}], newValue [{}]", nodeName, attribute, value);

        final String rncFeatureId = Model.ME_CONTEXT.rncManagedElement.systemFunctions.licensing.rncFeature.extractName(nodeFdn);


        if (ANONYMIZED_EVENT_DATA.equals(rncFeatureId)) {

            if (ModelDefinition.RncFeature.ActivationVals.ACTIVATED.name().equalsIgnoreCase(value.toString())) {
                final String additionalInfo = String.format("received %s=%s",
                                                            ModelDefinition.RncFeature.FEATURE_STATE,
                                                            ModelDefinition.RncFeature.ActivationVals.ACTIVATED.name());
                final String info = String.format("GDPR-AVC: privacy anonymized for node [%s]: %s", nodeName, additionalInfo);
                logger.debug(info);
                systemRecorder.recordPrivacyEvent(PRIVACYANONYMIZED, EventLevel.COARSE, ANONYMIZED_EVENT_DATA, NSCS_RESOURCE, info);
            } else if (ModelDefinition.RncFeature.ActivationVals.DEACTIVATED.name().equalsIgnoreCase(value.toString())) {
                final String additionalInfo = String.format("received %s=%s",
                                                            ModelDefinition.RncFeature.FEATURE_STATE,
                                                            ModelDefinition.RncFeature.ActivationVals.DEACTIVATED.name());
                final String info = String.format("GDPR-AVC: privacy anonymized for node [%s]: %s", nodeName, additionalInfo);
                logger.debug(info);
                systemRecorder.recordPrivacyEvent(PRIVACYANONYMIZED, EventLevel.COARSE, ANONYMIZED_EVENT_DATA, NSCS_RESOURCE, info);
            } else {
                final String additionalInfo = String.format("UNEXPECTED VALUE because its value is [%s]", value.toString());
                logger.debug("GDPR-AVC: privacy anonymized for node [{}]: {}", nodeName, additionalInfo);
            }

        } else {
            final String additionalInfo = String.format("IGNORED because its RncFeatureId is [%s]", rncFeatureId);
            logger.debug("GDPR-AVC: privacy anonymized for node [{}]: {}", nodeName, additionalInfo);
        }


    }

}
