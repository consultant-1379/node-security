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
package com.ericsson.nms.security.nscs.ntp.utility;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Ntp;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.ldap.utility.PlatformConfigurationReader;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.ntp.model.NtpKeyData;
import com.ericsson.nms.security.nscs.util.NtpConstants;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for Ntp Operations
 *
 * @author xjangop
 */
public class NtpUtility {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    @Inject
    private PlatformConfigurationReader platformConfigurationReader;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    /**
     * Gets ntp key data from work flow output params
     *
     * @param task
     *            the task
     * @param outputParams
     *            the outputParams
     * @return the ntp key data
     */
    public NtpKeyData getNtpKeyDataFromOutPutParams(final WorkflowQueryTask task, final Map<String, Serializable> outputParams,
            final String usecase) {

        NtpKeyData ntpKeyData = null;
        final String MISSING_NTP_KEY_DATA_WORKFLOW_PARAM = "Missing Ntp configure out put parameters.";
        if (outputParams == null) {
            final String errorMessage = MISSING_NTP_KEY_DATA_WORKFLOW_PARAM + usecase;
            nscsLogger.error(task, errorMessage);
            throw new WorkflowTaskException(MISSING_NTP_KEY_DATA_WORKFLOW_PARAM);
        }

        final String ntpKeyDataSerialized = (String) outputParams.get(WorkflowOutputParameterKeys.NTP_KEY.toString());

        final ObjectMapper mapper = new ObjectMapper();
        try {
            ntpKeyData = mapper.readValue(ntpKeyDataSerialized, NtpKeyData.class);
            if (ntpKeyData == null) {
                final String errorMessage = "Null NtpKeyData for Node: " + task.getNodeFdn() + " " + usecase;
                nscsLogger.error(task, errorMessage);
                throw new WorkflowTaskException("Missing Ntp key data parameter.");
            }

        } catch (final IOException ioException) {
            final String errorMessage = NscsLogger.stringifyException(ioException) + " " + usecase;
            nscsLogger.error(task, errorMessage);
            throw new WorkflowTaskException("Error while mapping Ntp key data parameter.");
        }
        nscsLogger.info("Returning ntp key data with key id: [{}] for Node:[{}]. {}", ntpKeyData.getId(), task.getNodeFdn(), usecase);
        return ntpKeyData;
    }

    /**
     * Gets itservices IPv4 or IPv6 addresses
     *
     * @param nodeRef
     *            the nodeRef
     * @return the itservices IPv4 or IPv6 addresses
     */
    public List<String> getNtpServerIpAddresses(final NormalizableNodeReference nodeRef) {

        final boolean hasIPv6Address = nscsNodeUtility.hasNodeIPv6Address(nodeRef);

        if (hasIPv6Address) {
            return getGlobalValues(NtpConstants.ITSERVICES_IPV6_IP_ADDR_PROPERTY);
        }
        return getGlobalValues(NtpConstants.ITSERVICES_IPV4_IP_ADDR_PROPERTY);

    }

    /**
     * Gets ENM web host url
     *
     * @return the ENM web host url
     */
    public String getEnmHostId() {
        return getGlobalValue(NtpConstants.WEB_HOST_DEFAULT_PROPERTY);
    }

    /**
     * Gets the global property value
     *
     * @param globalProperty
     *            the globalProperty
     * @return the global property value
     */
    public String getGlobalValue(final String globalProperty) {
        final String globalValues = platformConfigurationReader.getProperty(globalProperty);
        if (globalValues == null || globalValues.isEmpty()) {
            final String errorMessage = "NtpUtility: No Value Found for Property: " + globalProperty + "during NTP Configuration";
            nscsLogger.error(errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info("getGlobalValue: property [{}] values [{}]", globalProperty, globalValues);
        final List<String> globalValue = Arrays.asList(globalValues.split("\\s*,\\s*"));
        return globalValue.iterator().next();

    }


    private List<String> getGlobalValues(final String globalProperty) {
        final String globalValues = platformConfigurationReader.getProperty(globalProperty);
        if (globalValues == null || globalValues.isEmpty()) {
            final String errorMessage = "NtpUtility: Ip address is not found : " + globalProperty + "during NTP Configuration";
            nscsLogger.error(errorMessage);
            throw new UnexpectedErrorException(errorMessage);
        }
        nscsLogger.info("getGlobalValue: Values read from globalProperties : property [{}] values [{}]", globalProperty, globalValues);
        final List<String> globalValue = Arrays.asList(globalValues.split("\\s*,\\s*"));
        nscsLogger.info("List of globalValues [{}] retreived from globalProperty", globalValue);
        return globalValue;

    }
    /**
     * Gets TimeSettingMO for CPP nodes
     *
     * @param mirrorRootFdn
     *            the mirrorRootFdn
     * @param rootMo
     *            the rootMo
     * @return the timeSettingFdn
     */
    public String getTimeSettingMOFdn(final String mirrorRootFdn, final Mo rootMo) {

        nscsLogger.debug("Get TimeSettingMOFdn for mirrorRootFdn[{}] rootMo[{}] ", mirrorRootFdn, rootMo);
        if (mirrorRootFdn == null || mirrorRootFdn.isEmpty() || rootMo == null) {
            nscsLogger.error("Get TimeSettingMOFdn : invalid value : mirrorRootFdn[{}] rootMo[{}] ", mirrorRootFdn, rootMo);
            return null;
        }
        final Mo timeSettingMo = ((CppManagedElement) rootMo).systemFunctions.timeSetting;
        final String timeSettingFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, timeSettingMo);
        nscsLogger.debug("Get TimeSettingMOFdn return[{}]", timeSettingFdn);
        return timeSettingFdn;
    }


    /**
     * Gets getNtpMoFdn for COMECIM nodes
     *
     * @param normalizable
     *            the normalizable node
     * @return the ntpMoFdn
     */
    public String getNtpMoFdn(final NormalizableNodeReference normalizable) {
        final Map<String, Object> attributes = new HashMap<>();
        final Mo rootMo = nscsCapabilityModelService.getMirrorRootMo(normalizable);
        final Mo ntpMo = ((ComEcimManagedElement) rootMo).systemFunctions.sysM.timeM.ntp;
        final String ntpMoFdn = nscsNodeUtility.getSingleInstanceMoFdn(normalizable.getFdn(), ntpMo, attributes, Ntp.SUPPORTED_KEY_ALGO);
        nscsLogger.info("ntpMo : getSingleInstanceMoFdn: [{}]", ntpMoFdn);
        return ntpMoFdn;
    }

    /**
     * Bulids NTP server ID based on ENM host ID and serverAddress
     *
     * @param serverAddress
     *            serverAddress configured on node
     * @return NTP server ID
     */
    public String buildNtpserverIdFromEnmHostId(final String serverAddress){
        final StringBuilder ntpServerId = new StringBuilder();
        ntpServerId.append(getEnmHostId().substring(0, getEnmHostId().indexOf('.')));
        ntpServerId.append("_"+serverAddress);
        return ntpServerId.toString();
    }
}
