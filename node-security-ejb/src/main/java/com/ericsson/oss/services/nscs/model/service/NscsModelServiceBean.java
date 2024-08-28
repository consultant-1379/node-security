package com.ericsson.oss.services.nscs.model.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.model.service.NscsCapability;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelService;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelServiceException;
import com.ericsson.nms.security.nscs.api.model.service.NscsTargetPO;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelConstants;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.oss.itpf.modeling.modelservice.typed.capabilities.Capability;

/**
 * Provides remote access to NSCS Model Service.
 */
@Stateless
public class NscsModelServiceBean implements NscsModelService {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Override
    public List<String> getTargetCategories() {
        final Set<String> targetCategories = nscsModelServiceImpl.getTargetCategories();
        return new ArrayList<>(targetCategories);
    }

    @Override
    public List<String> getTargetTypes(final String targetCategory) {
        return nscsCapabilityModelService.getTargetTypes(targetCategory);
    }

    @Override
    public String getPlatform(final String targetCategory, final String targetType) throws NscsCapabilityModelException {
        String platform = null;
        try {
            platform = nscsModelServiceImpl.getPlatform(targetCategory, targetType);
        } catch (final IllegalArgumentException e) {
            throw new NscsCapabilityModelException(e.getMessage());
        }
        return platform;
    }

    @Override
    public List<String> getTargetModelIdentities(final String targetCategory, final String targetType) {
        final Set<String> targetModelIdentities = nscsModelServiceImpl.getTargetModelIdentities(targetCategory, targetType);
        return new ArrayList<>(targetModelIdentities);
    }

    @Override
    public List<String> getReleases(final String targetCategory, final String targetType, final String targetModelIdentity)
            throws NscsCapabilityModelException {
        Set<String> releases = null;
        try {
            releases = nscsModelServiceImpl.getReleases(targetCategory, targetType, targetModelIdentity);
        } catch (final IllegalArgumentException e) {
            throw new NscsCapabilityModelException(e.getMessage());
        }
        return new ArrayList<>(releases);
    }

    @Override
    public List<String> getTargetCategories(final String targetType) throws NscsCapabilityModelException {
        final List<String> targetCategories = (List<String>) nscsModelServiceImpl.getTargetCategories(targetType);
        return targetCategories;
    }

    @Override
    public Map<String, String> getRootMoType(final String targetCategory, final String targetType) throws NscsCapabilityModelException {
        final Map<String, String> rootMoType = nscsCapabilityModelService.getRootMoType(targetCategory, targetType);
        return rootMoType;
    }

    @Override
    public boolean isKSandEMSupported(final String targetCategory, final String targetType, final String targetModelIdentity)
            throws NscsCapabilityModelException {
        return nscsCapabilityModelService.isKSandEMSupported(targetCategory, targetType, targetModelIdentity);
    }

    @Override
    public boolean isCertificateAuthorityDnSupported(final String targetCategory, final String targetType, final String targetModelIdentity)
            throws NscsCapabilityModelException {
        return nscsCapabilityModelService.isCertificateAuthorityDnSupported(targetCategory, targetType, targetModelIdentity);
    }

    @Override
    public boolean isMockCapabilityModelUsed() {
        return nscsCapabilityModelService.useMockCapabilityModel();
    }

    @Override
    public Map<String, List<String>> getNscsCapabilities() {
        final Map<String, List<String>> nscsCapabilities = new HashMap<>();
        final Map<String, Set<String>> capabilities = NscsCapabilityModelConstants.getCapabilityModels();
        for (final String function : capabilities.keySet()) {
            final List<String> names = new ArrayList<>();
            for (final String name : capabilities.get(function)) {
                names.add(name);
            }
            nscsCapabilities.put(function, names);
        }
        return nscsCapabilities;
    }

    @Override
    public Map<String, Object> getTargetTypeDetails() {
        return nscsModelServiceImpl.getTargetTypeDetails();
    }

    @Override
    public Map<String, Object> getTargetTypeDetails(final String targetCategory) {
        return nscsModelServiceImpl.getTargetTypeDetails(targetCategory);
    }

    @Override
    public Map<String, Object> getTargetTypeDetails(final String targetCategory, final String targetType) {
        return nscsModelServiceImpl.getTargetTypeDetails(targetCategory, targetType);
    }

    @Override
    public Map<String, Object> getTargetTypeVersionDetails(final String targetCategory, final String targetType) {
        return nscsModelServiceImpl.getTargetTypeVersionDetails(targetCategory, targetType);
    }

    @Override
    public Map<String, Object> getTargetTypeVersionDetails(final String targetCategory, final String targetType, final String targetModelIdentity) {
        return nscsModelServiceImpl.getTargetTypeVersionDetails(targetCategory, targetType, targetModelIdentity);
    }

    @Override
    public String getTargetModelIdentityFromMimVersion(final String targetCategory, final String targetType, final String mimVersion)
            throws NscsModelServiceException, IllegalArgumentException {
        return nscsCapabilityModelService.getTargetModelIdentityFromMimVersion(targetCategory, targetType, mimVersion);
    }

    @Override
    public Map<String, NscsModelInfo> getModelInfoList(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String... models) {
        return nscsModelServiceImpl.getModelInfoList(targetCategory, targetType, targetModelIdentity, models);
    }

    @Override
    public NscsModelInfo getMimPrimaryTypeModelInfo(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String refMimNamespace, final String primaryType) {
        return nscsModelServiceImpl.getMimPrimaryTypeModelInfo(targetCategory, targetType, targetModelIdentity, refMimNamespace, primaryType);
    }

    @Override
    public String getMostAppropriateTmiForTarget(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String modelUrn) {
        return nscsModelServiceImpl.getMostAppropriateTmiForTarget(targetCategory, targetType, targetModelIdentity, modelUrn);
    }

    @Override
    public NscsModelInfo getLatestVersionOfNormalizedModel(final String model) throws IllegalArgumentException, NscsModelServiceException {
        return nscsModelServiceImpl.getLatestVersionOfNormalizedModel(model);
    }

    @Override
    public NscsModelInfo getLatestVersionOfModel(final String schema, final String namespace, final String model)
            throws IllegalArgumentException, NscsModelServiceException {
        return nscsModelServiceImpl.getLatestVersionOfModel(schema, namespace, model);
    }

    @Override
    public Collection<NscsCapability> getCapabilities(final String function, final String capabilityName) {
        final Collection<Capability> capabilities = nscsModelServiceImpl.getCapabilities(function, capabilityName);
        final Collection<NscsCapability> nscsCapabilities = new ArrayList<NscsCapability>();
        for (final Capability capability : capabilities) {
            final NscsCapability nscsCapability = new NscsCapability(capability.getFunction(), capability.getName(), capability.getTargetCategory(),
                    capability.getTargetType(), capability.getVersion(), capability.getValue());
            nscsCapabilities.add(nscsCapability);
        }
        return nscsCapabilities;
    }

    @Override
    public Map<String, Object> getCapabilities(final String targetCategory, final String targetType, final String function, final String version) {
        return nscsModelServiceImpl.getCapabilities(targetCategory, targetType, function, version);
    }

    @Override
    public Object getDefaultValue(final String function, final String capabilityName) {
        return nscsCapabilityModelService.getDefaultValue(function, capabilityName);
    }

    @Override
    public Object getCapabilityValue(final String targetCategory, final String targetType, final String targetModelIdentity, final String function,
            final String capabilityName) {
        return nscsCapabilityModelService.getCapabilityValue(targetCategory, targetType, targetModelIdentity, function, capabilityName);
    }

    @Override
    public String getCapabilitySupportModelVersion(final String targetCategory, final String targetType, final String targetModelIdentity,
            final String function) {
        return nscsModelServiceImpl.getCapabilitySupportModelVersion(targetCategory, targetType, targetModelIdentity, function);
    }

    @Override
    public NscsTargetPO getTargetPO(final String fdn) {
        final String inputParams = "fdn [" + fdn + "]";
        if (fdn == null || fdn.isEmpty()) {
            final String errorMsg = "MO FDN can't be null or empty.";
            nscsLogger.error("get TargetPO : " + errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        nscsLogger.debug("get TargetPO : starts for " + inputParams);
        final NscsTargetPO target = readerService.getTargetPO(fdn);
        nscsLogger.debug("get TargetPO : returns " + target);
        return target;
    }

    @Override
    public NscsModelInfo getModelInfo(final String targetCategory, final String targetType, final String targetModelIdentity, final String model) {
        return nscsModelServiceImpl.getModelInfo(targetCategory, targetType, targetModelIdentity, model);
    }
}
