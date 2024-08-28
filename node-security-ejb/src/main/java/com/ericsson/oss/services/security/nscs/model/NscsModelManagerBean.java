/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.model;

import com.ericsson.nms.security.nscs.api.model.service.NscsModelService;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.services.security.nscs.interceptor.NscsRecordedCommand;
import com.ericsson.oss.services.security.nscs.interceptor.NscsSecurityViolationHandled;

public class NscsModelManagerBean implements NscsModelManager {

    private static final String RESOURCE = "nodesec_model";
    private static final String READ = "read";

    @EServiceRef
    private NscsModelService nscsModelService;

    @Override
    @Authorize(resource = RESOURCE, action = READ)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public Object getTargetInfo(final String targetCategory, final String targetType, final String targetModelIdentity) {
        if (targetCategory == null && targetType == null) {
            return nscsModelService.getTargetTypeDetails();
        } else if (targetType == null) {
            return nscsModelService.getTargetTypeDetails(targetCategory);
        } else if (targetCategory == null) {
            return nscsModelService.getTargetTypeDetails(targetCategory, targetType);
        } else {
            if (targetModelIdentity == null) {
                return nscsModelService.getTargetTypeVersionDetails(targetCategory, targetType);
            } else {
                return nscsModelService.getTargetTypeVersionDetails(targetCategory, targetType, targetModelIdentity);
            }
        }
    }

    @Override
    @Authorize(resource = RESOURCE, action = READ)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public Object getModelInfo(final String targetCategory, final String targetType, final String targetModelIdentity, final String namespace,
            final String type) {
        return nscsModelService.getMimPrimaryTypeModelInfo(targetCategory, targetType, targetModelIdentity, namespace, type);
    }

    @Override
    @Authorize(resource = RESOURCE, action = READ)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public Object getTargetPO(final String fdn) {
        return nscsModelService.getTargetPO(fdn);
    }
}
