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
package com.ericsson.oss.services.security.nscs.iscf;

import com.ericsson.nms.security.nscs.api.IscfService;
import com.ericsson.nms.security.nscs.api.iscf.IscfResponse;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfSecDataDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlComboDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlIpsecDto;
import com.ericsson.oss.services.nscs.api.iscf.dto.IscfXmlOamDto;
import com.ericsson.oss.services.security.nscs.interceptor.NscsRecordedCommand;
import com.ericsson.oss.services.security.nscs.interceptor.NscsSecurityViolationHandled;
import com.ericsson.oss.services.security.nscs.iscf.dto.IscfDtoHelper;

public class IscfManagerBean implements IscfManager {

    private static final String RESOURCE = "nodesec_iscf";
    private static final String EXECUTE = "execute";
    private static final String DELETE = "delete";

    @EServiceRef
    private IscfService iscfService;

    @Override
    @Authorize(resource = RESOURCE, action = EXECUTE)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public IscfResponse generateXmlOam(final IscfXmlOamDto dto) {
        IscfDtoHelper.validate(dto);
        final NodeModelInformation nodeModelInformation = IscfDtoHelper.fromDto(dto.getNodeModelInfo());
        return iscfService.generate(dto.getLogicalName(), dto.getNodeFdn(), dto.getParams().getWantedSecurityLevel(),
                dto.getParams().getMinimumSecurityLevel(), dto.getEnrollmentMode(), nodeModelInformation);
    }

    @Override
    @Authorize(resource = RESOURCE, action = EXECUTE)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public IscfResponse generateXmlIpsec(final IscfXmlIpsecDto dto) {
        IscfDtoHelper.validate(dto);
        final NodeModelInformation nodeModelInformation = IscfDtoHelper.fromDto(dto.getNodeModelInfo());
        final SubjectAltNameParam subjectAltNameParam = IscfDtoHelper.fromDto(dto.getParams().getSubjectAltNameParam());
        return iscfService.generate(dto.getLogicalName(), dto.getNodeFdn(), dto.getParams().getUserLabel(), subjectAltNameParam,
                dto.getParams().getIpsecAreas(), dto.getEnrollmentMode(), nodeModelInformation);
    }

    @Override
    @Authorize(resource = RESOURCE, action = EXECUTE)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public IscfResponse generateXmlCombo(final IscfXmlComboDto dto) {
        IscfDtoHelper.validate(dto);
        final NodeModelInformation nodeModelInformation = IscfDtoHelper.fromDto(dto.getNodeModelInfo());
        final SubjectAltNameParam subjectAltNameParam = IscfDtoHelper.fromDto(dto.getIpsecParams().getSubjectAltNameParam());
        return iscfService.generate(dto.getLogicalName(), dto.getNodeFdn(), dto.getOamParams().getWantedSecurityLevel(),
                dto.getOamParams().getMinimumSecurityLevel(), dto.getIpsecParams().getUserLabel(), subjectAltNameParam,
                dto.getIpsecParams().getIpsecAreas(), dto.getEnrollmentMode(), nodeModelInformation);
    }

    @Override
    @Authorize(resource = RESOURCE, action = DELETE)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public String cancel(final String node) {
        iscfService.cancel(node);
        return String.format("Successfully deleted PKI EE for %s", node);
    }

    @Override
    @Authorize(resource = RESOURCE, action = EXECUTE)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public SecurityDataResponse generateSecurityDataOam(final IscfSecDataDto dto) {
        IscfDtoHelper.validateSecDataOam(dto);
        final NodeIdentifier nodeId = IscfDtoHelper.fromDto(dto.getNodeId());
        final NodeModelInformation nodeModelInformation = IscfDtoHelper.fromDto(dto.getNodeModelInfo());
        if (dto.getSubjectAltNameParam() != null) {
            final SubjectAltNameParam subjectAltNameParam = IscfDtoHelper.fromDto(dto.getSubjectAltNameParam());
            if (dto.getIpVersion() != null) {
                return iscfService.generateSecurityDataOam(nodeId, subjectAltNameParam, dto.getEnrollmentMode(), nodeModelInformation,
                        dto.getIpVersion());
            } else {
                return iscfService.generateSecurityDataOam(nodeId, subjectAltNameParam, dto.getEnrollmentMode(), nodeModelInformation);
            }
        } else {
            if (dto.getIpVersion() != null) {
                return iscfService.generateSecurityDataOam(nodeId, dto.getEnrollmentMode(), nodeModelInformation, dto.getIpVersion());
            } else {
                return iscfService.generateSecurityDataOam(nodeId, dto.getEnrollmentMode(), nodeModelInformation);
            }
        }
    }

    @Override
    @Authorize(resource = RESOURCE, action = EXECUTE)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public SecurityDataResponse generateSecurityDataIpsec(final IscfSecDataDto dto) {
        IscfDtoHelper.validateSecDataIpsecAndCombo(dto);
        final NodeIdentifier nodeId = IscfDtoHelper.fromDto(dto.getNodeId());
        final NodeModelInformation nodeModelInformation = IscfDtoHelper.fromDto(dto.getNodeModelInfo());
        final SubjectAltNameParam subjectAltNameParam = IscfDtoHelper.fromDto(dto.getSubjectAltNameParam());
        if (dto.getIpVersion() != null) {
            return iscfService.generateSecurityDataIpsec(nodeId, subjectAltNameParam, dto.getEnrollmentMode(), nodeModelInformation,
                    dto.getIpVersion());
        } else {
            return iscfService.generateSecurityDataIpsec(nodeId, subjectAltNameParam, dto.getEnrollmentMode(), nodeModelInformation);
        }
    }

    @Override
    @Authorize(resource = RESOURCE, action = EXECUTE)
    @NscsSecurityViolationHandled
    @NscsRecordedCommand
    public SecurityDataResponse generateSecurityDataCombo(final IscfSecDataDto dto) {
        IscfDtoHelper.validateSecDataIpsecAndCombo(dto);
        final NodeIdentifier nodeId = IscfDtoHelper.fromDto(dto.getNodeId());
        final NodeModelInformation nodeModelInformation = IscfDtoHelper.fromDto(dto.getNodeModelInfo());
        final SubjectAltNameParam subjectAltNameParam = IscfDtoHelper.fromDto(dto.getSubjectAltNameParam());
        if (dto.getIpVersion() != null) {
            return iscfService.generateSecurityDataCombo(nodeId, subjectAltNameParam, dto.getEnrollmentMode(), nodeModelInformation,
                    dto.getIpVersion());
        } else {
            return iscfService.generateSecurityDataCombo(nodeId, subjectAltNameParam, dto.getEnrollmentMode(), nodeModelInformation);
        }
    }
}
