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
package com.ericsson.nms.security.nscs.cpp.seclevel.util;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

public class SecurityLevelCommonUtils {

    @Inject
    private Logger log;

    @Inject
    private NscsCMWriterService writer;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCapabilityModelService capabilityService;


    /**
     * This method is used to Enrollment Mode in NetworkElementSecurity MO
     *
     * @param enrollmentMode
     *            parameters of this method accepts enrollmentMode and normalized node reference object
     * @param normNode  normNode
     *
     */

    public void setEnrollmentMode(String enrollmentMode, NormalizableNodeReference normNode) {
        try {
            String networkElementSecurityFdn = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(normNode.getName()).fdn();

            log.debug("Updating Enrollment Mode in NetworkElementSecurity MO {} NES fdn and normNode.getFdn() {} :  "
                    , networkElementSecurityFdn, normNode.getFdn());

            writer.withSpecification(networkElementSecurityFdn).setAttribute(NetworkElementSecurity.ENROLLMENT_MODE, enrollmentMode).updateMO();

            log.debug("EnrolmentMode succesfully set for node {}" + normNode.getFdn());
        } catch (final Exception e) {
            e.printStackTrace();
            log.debug("Update of Enrollment mode in NetworkElementSecurity MO failed!" + e);
        }

    }

    /**
     * This method is used to get Current Security Level on the node
     *
     * @param node
     *            parameter of this method accepts node
     *
     * @return The security level that is present on the node
     */

    public SecurityLevel getCurrentSecurityLevel(final NodeReference node) {
        SecurityLevel securityLevel = SecurityLevel.LEVEL_NOT_SUPPORTED;

        log.info("securityLevel in current security level : {}", securityLevel);

        final CmResponse cmResponse = readerService.getMOAttribute(node, Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL);

        log.debug("CM Response : {}", cmResponse);
        log.debug("CM Response objects size : {}", cmResponse.getCmObjects().size());

        if (cmResponse != null && cmResponse.getCmObjects().size() != 0) {
            final CmObject securityMo = cmResponse.getCmObjects().iterator().next();
            log.debug("security Mo in current ssecurity level : {}", securityMo);

            final Object osLevel = securityMo.getAttributes()
                    .get(Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL);
            if (osLevel != null) {
                log.debug("os Level in current ssecurity level : {}", osLevel);
                securityLevel = SecurityLevel.valueOf(osLevel.toString());
                log.debug("os Level in current ssecurity level : {}", osLevel);
            }
        }

        return securityLevel;
    }

    public String getManagedElementDataFdn(final NormalizableNodeReference normalizedReference) {

    	Mo rootMo = capabilityService.getMirrorRootMo(normalizedReference);
    	Mo managedElementDataMo = ((CppManagedElement)rootMo).managedElementData;
    	String managedElementDataFdn = managedElementDataMo.withNames(normalizedReference.getFdn()).fdn();

    	log.info("manage element data FDN {}", managedElementDataFdn);
    	return managedElementDataFdn;
    }

    public String getSecurityFdn(final NormalizableNodeReference normalizedReference) {

    	Mo rootMo = capabilityService.getMirrorRootMo(normalizedReference);
    	String securityFdn = null;
    	if( rootMo instanceof CppManagedElement){
    	Mo securityMo = ((CppManagedElement)rootMo).systemFunctions.security;
    	securityFdn = securityMo.withNames(normalizedReference.getFdn()).fdn();

    	log.info("Security FDN {}", securityFdn);
    	}
    	return securityFdn;
    }
}
