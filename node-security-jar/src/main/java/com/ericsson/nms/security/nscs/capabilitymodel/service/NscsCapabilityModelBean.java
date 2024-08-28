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
package com.ericsson.nms.security.nscs.capabilitymodel.service;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.oss.itpf.modeling.modelservice.typed.capabilities.Capability;

@NscsCapabilityModelType(isCapabilityModelMock = false)
public class NscsCapabilityModelBean implements NscsCapabilityModel {

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Override
    public Collection<String> getTargetTypes(final String targetCategory) throws NscsCapabilityModelException {
        return nscsModelServiceImpl.getTargetTypes(targetCategory);
    }

    @Override
    public Collection<Capability> getCapabilities(final String function, final String capabilityName) {
        return nscsModelServiceImpl.getCapabilities(function, capabilityName);
    }

    @Override
    public Map<String, Object> getCapabilities(final String targetCategory, final String targetType, final String function, final String version) {
        return nscsModelServiceImpl.getCapabilities(targetCategory, targetType, function, version);
    }

    @Override
    public Object getDefaultValue(final String function, final String capabilityName) {
        return nscsModelServiceImpl.getDefaultValue(function, capabilityName);
    }

    @Override
    public Object getCapabilityValue(final String targetCategory, final String targetType, final String targetModelIdentity, final String function,
            final String capabilityName) {
        return nscsModelServiceImpl.getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
    }

    @Override
    public String getTargetModelIdentityFromMimVersion(final String targetCategory, final String targetType, final String mimVersion) {
        return nscsModelServiceImpl.getTargetModelIdentityFromMimVersion(targetCategory, targetType, mimVersion);
    }

    @Override
    public String getTargetModelIdentityFromProductNumber(final String targetCategory, final String targetType, final String productNumber) {
        return null;
    }
}
