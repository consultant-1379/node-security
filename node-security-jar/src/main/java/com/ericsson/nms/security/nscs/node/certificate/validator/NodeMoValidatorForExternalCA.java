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
package com.ericsson.nms.security.nscs.node.certificate.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentAuthority;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentServer;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NodeCredential;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoAttributeException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;

/**
 * This Class is used to validate the MOs on the Node which required for the Certificate Reissue using External CA
 *
 * @author xsrirko
 *
 */
public class NodeMoValidatorForExternalCA {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private NscsCMReaderService readerService;

    @Inject
    private NscsCapabilityModelService capabilityService;

    /**
     * Check whether NodeCredential MO attributes are present on node. If at least one attribute is null or empty, throw exception.
     *
     * @param nodeCredentialMoObj
     *            NodeCredential MO Object
     * @throws MissingMoAttributeException
     *             when MO attribute is not present or null
     */
    public void validateNodeCredentialMo(final MoObject nodeCredentialMoObj) {

        final String enrollmentAuthority = nodeCredentialMoObj.getAttribute(NodeCredential.ENROLLMENT_AUTHORITY);

        if (enrollmentAuthority == null || enrollmentAuthority.isEmpty()) {
            final String errorMessage = "Error while reading enrollmentAuthority " + enrollmentAuthority;
            throw new MissingMoAttributeException(errorMessage);
        }

        final String enrollmentServerGroup = nodeCredentialMoObj.getAttribute(NodeCredential.ENROLLMENT_SERVER_GROUP);

        if (enrollmentServerGroup == null || enrollmentServerGroup.isEmpty()) {
            final String errorMessage = "Error while reading enrollmentServerGroup " + enrollmentServerGroup;
            throw new MissingMoAttributeException(errorMessage);
        }

        final String nodeCredentialKeyInfo = nodeCredentialMoObj.getAttribute(NodeCredential.KEY_INFO);

        if (nodeCredentialKeyInfo == null || nodeCredentialKeyInfo.isEmpty()) {
            final String errorMessage = "Error while reading nodeCredentialKeyInfo " + nodeCredentialKeyInfo;
            throw new MissingMoAttributeException(errorMessage);
        }

        final String nodeCredentialId = nodeCredentialMoObj.getAttribute(NodeCredential.NODE_CREDENTIAL_ID);
        if (nodeCredentialId == null) {
            final String errorMessage = "Error while reading nodeCredentialId " + nodeCredentialId;
            throw new MissingMoAttributeException(errorMessage);
        }

        final String nodeCredentialSubjectName = nodeCredentialMoObj.getAttribute(NodeCredential.SUBJECT_NAME);
        if (nodeCredentialSubjectName == null || nodeCredentialSubjectName.isEmpty()) {
            final String errorMessage = "Error while reading nodeCredentialSubjectName :" + nodeCredentialSubjectName;
            throw new MissingMoAttributeException(errorMessage);
        }

        final Map nodeCredentialCertificateContent = nodeCredentialMoObj.getAttribute(NodeCredential.CERTIFICATE_CONTENT);
        nscsLogger.info("certificate content is: " + nodeCredentialCertificateContent.get("extensionContent"));
    }

    /**
     * Check whether EnrollmentServerMo attributes are present on node. If at least one attribute is null or empty, throw exception.
     *
     * @param enrollmentServerGroupFdn
     *            fdn of EnrollmentServerGroup MO
     * @param rootMo
     *            rootMo of the node
     * @throws MissingMoException
     *             when MO is not present on the Node
     * @throws MissingMoAttributeException
     *             when MO attribute is not present or null
     */
    public void validateEnrollmentServerGroupMo(final String enrollmentServerGroupFdn, final Mo rootMo) {

        if (enrollmentServerGroupFdn == null || enrollmentServerGroupFdn.isEmpty()) {
            final String errorMessage = "EnrollmentServerGroup [" + enrollmentServerGroupFdn + "] not found";
            throw new MissingMoException(errorMessage);

        } else {
            final MoObject enrollmentServerGroupMoObj = readerService.getMoObjectByFdn(enrollmentServerGroupFdn);
            if (enrollmentServerGroupMoObj == null) {
                final String errorMessage = "EnrollmentServerGroup [" + enrollmentServerGroupMoObj + "] not found";
                throw new MissingMoException(errorMessage);
            }
            validateEnrollmentServerMo(enrollmentServerGroupFdn, rootMo);
        }
    }

    /**
     * Check whether EnrollmentAuthority MO attributes are present on node. If at least one attribute is null or empty, throw exception.
     *
     * @param enrollmentAuthorityFdn
     *            fdn of EnrollmentAuthority MO
     * @param certificateType
     *            certificate Type with which the certificate reissue is requested
     * @param enrollmentCAAuthorizationModes
     *            authorization modes of enrollment CA
     * @throws MissingMoException
     *             thrown when MO is not found
     * @throws MissingMoAttributeException
     *             when MO attribute is not present or null
     */
    public void validateEnrollmentAuthority(final String enrollmentAuthorityFdn, final String certificateType,
            final Map<String, String> enrollmentCAAuthorizationModes) {

        if (enrollmentAuthorityFdn == null || enrollmentAuthorityFdn.isEmpty()) {
            final String errorMessage = "EnrollmentAuthority [" + enrollmentAuthorityFdn + "] not found";
            throw new MissingMoException(errorMessage);
        } else {
            nscsLogger.debug("Getting already created EnrollmentAuthority for [" + enrollmentAuthorityFdn + "]");
            final MoObject enrollmentAuthorityMoObj = readerService.getMoObjectByFdn(enrollmentAuthorityFdn);
            if (enrollmentAuthorityMoObj == null) {
                final String errorMessage = "EnrollmentAuthority [" + enrollmentAuthorityMoObj + "] not found";
                throw new MissingMoException(errorMessage);
            } else {
                validateEnrollmentAuthorityMo(enrollmentAuthorityMoObj, certificateType, enrollmentCAAuthorizationModes);
            }
        }
    }

    private void validateEnrollmentAuthorityMo(final MoObject enrollmentAuthorityMoObj, final String certificateType,
            final Map<String, String> enrollmentCAAuthorizationModes) {

        String enrollmentAuthorityDn = enrollmentAuthorityMoObj.getAttribute(EnrollmentAuthority.ENROLLMENT_AUTHORITY_NAME);
        if (enrollmentAuthorityDn == null || enrollmentAuthorityDn.isEmpty()) {
            final String errorMessage = "Error while reading " + enrollmentAuthorityDn;
            throw new MissingMoAttributeException(errorMessage);
        }

        if (capabilityService.isEnrollmentRootCAFingerPrintSupported(enrollmentCAAuthorizationModes, certificateType)) {
            final String enrollmentAuthorityFingerprint = enrollmentAuthorityMoObj.getAttribute(EnrollmentAuthority.ENROLLMENT_CA_FINGERPRINT);
            if (enrollmentAuthorityFingerprint == null || enrollmentAuthorityFingerprint.isEmpty()) {
                final String errorMessage = "Error while reading " + enrollmentAuthorityFingerprint;
                throw new MissingMoAttributeException(errorMessage);
            }
        }

    }

    private void validateEnrollmentServerMo(final String enrollmentServerGroupFdn, final Mo rootMo) {

        final Mo enrollmentServerMo = ((ComEcimManagedElement) rootMo).systemFunctions.secM.certM.enrollmentServerGroup.enrollmentServer;

        final List<String> attrs = new ArrayList<>();
        attrs.add(EnrollmentServer.PROTOCOL);
        attrs.add(EnrollmentServer.URI);
        final String[] requestedAttrs = attrs.toArray(new String[0]);
        final String readMessage = NscsLogger.stringifyReadParams(enrollmentServerGroupFdn, enrollmentServerMo.type(), requestedAttrs);
        try {
            final CmResponse enrollmentServerResponse = readerService.getMos(enrollmentServerGroupFdn, enrollmentServerMo.type(),
                    enrollmentServerMo.namespace(), requestedAttrs);
            if (enrollmentServerResponse != null && enrollmentServerResponse.getCmObjects() != null
                    && !enrollmentServerResponse.getCmObjects().isEmpty()) {
                for (final CmObject enrollmentServerCmObj : enrollmentServerResponse.getCmObjects()) {
                    final String protocol = (String) enrollmentServerCmObj.getAttributes().get(EnrollmentServer.PROTOCOL);
                    final String uri = (String) enrollmentServerCmObj.getAttributes().get(EnrollmentServer.URI);
                    if (uri == null || uri.isEmpty()) {
                        final String errorMessage = "Error while reading uri " + uri;
                        throw new MissingMoAttributeException(errorMessage);
                    }

                    if (protocol == null || protocol.isEmpty()) {
                        final String errorMessage = "Error while reading protocol " + protocol;
                        throw new MissingMoAttributeException(errorMessage);
                    }
                }
            } else {
                nscsLogger.info("No EnrollmentServer MOs under enrollmentServerGroup [" + enrollmentServerGroupFdn + "]");
            }
        } catch (DataAccessSystemException | DataAccessException e) {
            final String errorMessage = NscsLogger.stringifyException(e) + " while reading " + readMessage;
            throw new UnexpectedErrorException(errorMessage);
        }
    }

}
