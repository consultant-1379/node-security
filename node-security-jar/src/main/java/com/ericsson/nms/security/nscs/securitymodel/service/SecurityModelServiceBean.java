/**
 *
 */
package com.ericsson.nms.security.nscs.securitymodel.service;

import java.util.List;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.SecurityModelService;
import com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand;
import com.ericsson.nms.security.nscs.api.credentials.SecurityCredentialMetaData;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;

/**
 * @author enmadmin
 *
 */
public class SecurityModelServiceBean implements SecurityModelService {

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private NscsCMReaderService readerService;

    @Override
    public SecurityCredentialMetaData getCredentialMetaData(final String nodeName) {

        final NodeReference nodeRef = new NodeRef(nodeName);
        final NormalizableNodeReference normNodeRef = readerService.getNormalizedNodeReference(nodeRef);
        final List<String> supportedAttributes = nscsCapabilityModelService.getExpectedCredentialsParams(normNodeRef);

        final SecurityCredentialMetaDataImpl credentialMetaDataImpl = new SecurityCredentialMetaDataImpl();
        credentialMetaDataImpl.setRootRequired(supportedAttributes.contains(CredentialsCommand.ROOT_USER_NAME_PROPERTY));
        credentialMetaDataImpl.setSecureRequired(supportedAttributes.contains(CredentialsCommand.SECURE_USER_NAME_PROPERTY));
        credentialMetaDataImpl.setUnsecureRequired(supportedAttributes.contains(CredentialsCommand.NORMAL_USER_NAME_PROPERTY));
        credentialMetaDataImpl.setSecureRequired(supportedAttributes.contains(CredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY));
        credentialMetaDataImpl.setSecureRequired(supportedAttributes.contains(CredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY));
        credentialMetaDataImpl.setUnsecureRequired(supportedAttributes.contains(CredentialsCommand.NODECLI_USER_NAME_PROPERTY));

        return credentialMetaDataImpl;
    }

}
