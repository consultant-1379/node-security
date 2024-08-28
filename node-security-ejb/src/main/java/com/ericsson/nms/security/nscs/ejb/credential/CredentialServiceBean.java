package com.ericsson.nms.security.nscs.ejb.credential;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.CredentialService;
import com.ericsson.nms.security.nscs.api.NscsService;
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse.Entry;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand.NscsPropertyCommandInvoker;
import com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand;
import com.ericsson.nms.security.nscs.api.command.types.GetSnmpCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.command.types.SetEnrollmentCommand;
import com.ericsson.nms.security.nscs.api.command.types.SnmpAuthnopriv;
import com.ericsson.nms.security.nscs.api.command.types.SnmpAuthpriv;
import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes;
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SnmpAuthProtocol;
import com.ericsson.nms.security.nscs.api.enums.SnmpPrivProtocol;
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.CredentialServiceException;
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.interceptor.EjbLoggerInterceptor;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@Stateless
public class CredentialServiceBean implements CredentialService {

    public static final String ROOT_UN_KEY = "rootusername";
    public static final String ROOT_UP_KEY = "rootuserpassword";
    public static final String SECURE_UN_KEY = "secureusername";
    public static final String SECURE_UP_KEY = "secureuserpassword";
    public static final String NORMAL_UN_KEY = "normalusername";
    public static final String NORMAL_UP_KEY = "normaluserpassword";
    public static final String NWIEA_SECURE_UP_KEY = "nwieasecureuserpassword";
    public static final String NWIEA_SECURE_UN_KEY = "nwieasecureusername";
    public static final String NWIEB_SECURE_UP_KEY = "nwiebsecureuserpassword";
    public static final String NWIEB_SECURE_UN_KEY = "nwiebsecureusername";
    public static final String NODECLI_UN_KEY = "nodecliusername";
    public static final String NODECLI_UP_KEY = "nodecliuserpassword";

    public static final String CRYPTED_UP = "******";

    public static final String HEADER_NODE = "Node";

    @EServiceRef
    private NscsService nscsService;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private Logger logger;

    @Inject
    private NscsContextService nscsContextService;

    @Override
    @Interceptors({ EjbLoggerInterceptor.class })
    public void createNodeCredentials(final CredentialAttributes credentialAttributes, final String inputNodeName) throws CredentialServiceException {
        createNodeCredentials(credentialAttributes, inputNodeName, null);
    }

    @Override
    @Interceptors({ EjbLoggerInterceptor.class })
    public void createNodeCredentials(final CredentialAttributes credentialAttributes, final String inputNodeName,
                                      final String enablingPredefiniedENMLDAPUser)
            throws CredentialServiceException {

        logger.info("[CredentialService] Request creating credentialAttributes: [{}] for node [{}]", credentialAttributes.toString(), inputNodeName);

        nscsContextService.setInputNodeNameContextValue(inputNodeName);
        final NscsPropertyCommand nscsCommand = new NscsPropertyCommand();
        nscsCommand.setCommandInvokerValue(NscsPropertyCommandInvoker.API);

        final NodeReference nodeRef = new NodeRef(inputNodeName);
        final NormalizableNodeReference normNode = reader.getNormalizedNodeReference(nodeRef);

        if (normNode == null) {
            logger.error("Existing invalid nodes found ");
            final NodeDoesNotExistException nodeDoesNotExistException = new NodeDoesNotExistException(inputNodeName);
            nodeDoesNotExistException.setSuggestedSolution("Please specify a valid NetworkElement that exists in the system.");
            throw nodeDoesNotExistException;
        }
        final List<String> expectedAttributeKeys = nscsCapabilityModelService.getExpectedCredentialsParams(normNode);
        try {
            if (reader.exists(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(inputNodeName).fdn())) {
                nscsCommand.setCommandType(NscsCommandType.UPDATE_CREDENTIALS);
            } else {
                nscsCommand.setCommandType(NscsCommandType.CREATE_CREDENTIALS);
            }
        } catch (final NscsServiceException e) {
            logger.debug("Can't read NetworkElementSecurity MO for node [{}]. Performing a CREATE_CREDENTIALS command.", inputNodeName);
            nscsCommand.setCommandType(NscsCommandType.CREATE_CREDENTIALS);
        }

        logger.debug("Command Type: [{}]", nscsCommand.getCommandType());
        logger.debug("Expected attributes are: [{}]", expectedAttributeKeys);

        addSecurePropertyToNscsCommand(credentialAttributes, nscsCommand, expectedAttributeKeys);
        addNormalPropertyToNscsCommand(credentialAttributes, nscsCommand, expectedAttributeKeys);
        addRootPropertyToNscsCommand(credentialAttributes, nscsCommand, expectedAttributeKeys);
        addNwieaPropertyToNscsCommand(credentialAttributes, nscsCommand, expectedAttributeKeys);
        addNwiebPropertyToNscsCommand(credentialAttributes, nscsCommand, expectedAttributeKeys);
        addNodeCliPropertyToNscsCommand(credentialAttributes, nscsCommand);

        final List<String> inputNodesList = new ArrayList<>();
        inputNodesList.add(inputNodeName);
        nscsCommand.getProperties().put(NscsNodeCommand.NODE_LIST_PROPERTY, inputNodesList);
        addPredefinedLdapPropertyToNscsCommand(enablingPredefiniedENMLDAPUser, nscsCommand);

        try {
            logger.debug("[CredentialService] Processing command: [{}]", nscsCommand.getCommandType());
            nscsService.processCommand(nscsCommand);
        } catch (final NscsServiceException e) {
            e.printStackTrace();
            String message = e.getMessage();
            Throwable cause = e.getCause();
            if (message.contains(NscsErrorCodes.SYNTAX_ERROR)) {
                message = NscsErrorCodes.UNSUCCESSFUL_NODE_CREDENTIALS_CREATE;
                if (cause == null) {
                    cause = e;
                }
            }
            final CredentialServiceException credException = new CredentialServiceException(message, cause);
            credException.setErrorType(e.getErrorType());
            credException.setErrorCode(e.getErrorCode());
            credException.setSuggestedSolution(e.getSuggestedSolution());
            throw credException;
        }
    }

    @Override
    public void configureEnrollmentMode(final EnrollmentMode enrollmentMode, final String nodeName) throws CredentialServiceException {
        final NscsPropertyCommand nscsCommand = new NscsPropertyCommand();
        nscsCommand.setCommandInvokerValue(NscsPropertyCommandInvoker.API);
        nscsCommand.setCommandType(NscsCommandType.SET_ENROLLMENT);
        nscsCommand.getProperties().put(SetEnrollmentCommand.ENROLLMENT_MODE_PROPERTY, enrollmentMode);

        final List<String> inputNodesList = new ArrayList<>();
        inputNodesList.add(nodeName);
        nscsCommand.getProperties().put(NscsNodeCommand.NODE_LIST_PROPERTY, inputNodesList);

        try {
            final NscsCommandResponse response = nscsService.processCommand(nscsCommand);
        } catch (final NscsServiceException e) {
            e.printStackTrace();
            final CredentialServiceException credException = new CredentialServiceException(e.getMessage(), e.getCause());
            credException.setErrorType(e.getErrorType());
            credException.setErrorCode(e.getErrorCode());
            credException.setSuggestedSolution(e.getSuggestedSolution());
            throw credException;
        }
    }

    @Override
    public void configureSnmpV3(final SnmpSecurityLevel securityLevel, final SnmpV3Attributes snmpV3Attributes, final List<String> nodes) {
        logger.info("[CredentialService] Request configuring SNMPv3 security level [{}} for node [{}}", securityLevel, nodes);

        final NscsPropertyCommand nscsCommand = new NscsPropertyCommand();
        nscsCommand.setCommandInvokerValue(NscsPropertyCommandInvoker.API);

        switch (securityLevel){
        case AUTH_NO_PRIV:
            nscsCommand.setCommandType(NscsCommandType.SNMP_AUTHNOPRIV);
            nscsCommand.getProperties().put(SnmpAuthnopriv.AUTH_ALGO_PARAM, snmpV3Attributes.getAuthProtocolAttr().toString());
            if(validatePasswordNonEmpty(snmpV3Attributes.getAuthKey())){
                nscsCommand.getProperties().put(SnmpAuthnopriv.AUTH_PWD_PARAM, snmpV3Attributes.getAuthKey());
            } else {
                final String msg = "Password validation fails for auth_password: Null or empty value";
                logger.error(msg);
                throw new CredentialServiceException(msg);
            }
            break;

        case AUTH_PRIV:
            nscsCommand.setCommandType(NscsCommandType.SNMP_AUTHPRIV);
            nscsCommand.getProperties().put(SnmpAuthpriv.AUTH_ALGO_PARAM, snmpV3Attributes.getAuthProtocolAttr().toString());
            if(validatePasswordNonEmpty(snmpV3Attributes.getAuthKey())){
                nscsCommand.getProperties().put(SnmpAuthpriv.AUTH_PWD_PARAM, snmpV3Attributes.getAuthKey());
            } else {
                final String msg = "Password validation fails for auth_password: Null or empty value";
                logger.error(msg);
                throw new CredentialServiceException(msg);
            }
            nscsCommand.getProperties().put(SnmpAuthpriv.PRIV_ALGO_PARAM, snmpV3Attributes.getPrivProtocolAttr().toString());
            if(validatePasswordNonEmpty(snmpV3Attributes.getPrivKey())){
                nscsCommand.getProperties().put(SnmpAuthpriv.PRIV_PWD_PARAM, snmpV3Attributes.getPrivKey());
            } else {
                final String msg = "Password validation fails for priv_password: Null or empty value";
                logger.error(msg);
                throw new CredentialServiceException(msg);
            }
            break;

        default:
            final String msg = "No configuration is needed for security level NO_AUTH_NO_PRIV";
            logger.error(msg);
            throw new CredentialServiceException(msg);
        }

        nscsCommand.getProperties().put(NscsNodeCommand.NODE_LIST_PROPERTY, nodes);

        try {
            logger.debug("[CredentialService] Processing command: [{}]", nscsCommand.getCommandType());
            nscsService.processCommand(nscsCommand);
        } catch (final NscsServiceException e) {
            logger.error("Error during command execution. Re-throwing", e);
            final CredentialServiceException credException = new CredentialServiceException(e.getMessage(), e.getCause());
            credException.setErrorType(e.getErrorType());
            credException.setErrorCode(e.getErrorCode());
            credException.setSuggestedSolution(e.getSuggestedSolution());
            throw credException;
        }
    }

    @Override
    public Map<String, SnmpV3Attributes> getSnmpV3Configuration(final boolean isPlainText, final List<String> nodes) {
        logger.info("[CredentialService] Request reading SNMPv3 configuration for node [{}}", nodes);

        final NscsPropertyCommand nscsCommand = new NscsPropertyCommand();
        nscsCommand.setCommandInvokerValue(NscsPropertyCommandInvoker.API);
        nscsCommand.setCommandType(NscsCommandType.GET_SNMP);
        logger.debug("Command Type: [{}]", nscsCommand.getCommandType());

        if (isPlainText) {
            nscsCommand.getProperties().put(GetSnmpCommand.PLAIN_TEXT_PROPERTY, GetSnmpCommand.PLAIN_TEXT_SHOW);
        } else {
            nscsCommand.getProperties().put(GetSnmpCommand.PLAIN_TEXT_PROPERTY, GetSnmpCommand.PLAIN_TEXT_HIDE);
        }

        nscsCommand.getProperties().put(NscsNodeCommand.NODE_LIST_PROPERTY, nodes);

        try {
            logger.debug("[CredentialService] Processing command: [{}]", nscsCommand.getCommandType());
            final NscsCommandResponse response = nscsService.processCommand(nscsCommand);

            if (response == null) {
                logger.info("Response for command [{}} is null.", nscsCommand.getCommandType());
                return new HashMap<>();
            } else {
                return snmpResponseParser(response);
            }

        } catch (final NscsServiceException e) {
            logger.error("Error during command execution. Re-throwing", e);
            final CredentialServiceException credException = new CredentialServiceException(e.getMessage(), e.getCause());
            credException.setErrorType(e.getErrorType());
            credException.setErrorCode(e.getErrorCode());
            credException.setSuggestedSolution(e.getSuggestedSolution());
            throw credException;
        }
    }

    @Override
    public boolean validateAttributes(final String nodeName, final CredentialAttributes credentialAttributes) {
        final NodeReference nodeRef = new NodeRef(nodeName);
        final NormalizableNodeReference normNode = reader.getNormalizedNodeReference(nodeRef);

        if (normNode == null) {
            final NodeDoesNotExistException nodeDoesNotExistException = new NodeDoesNotExistException(nodeName);
            nodeDoesNotExistException.setSuggestedSolution("Please specify a valid NetworkElement that exists in the system.");
            throw nodeDoesNotExistException;
        }
        final List<String> unexpectedAttributeKeys = nscsCapabilityModelService.getUnexpectedCredentialsParams(normNode);
        final Set<String> actualAttributeKeys = credentialAttributes.getActualCredentialAttributeKeys();

        actualAttributeKeys.retainAll(unexpectedAttributeKeys);
        return actualAttributeKeys.isEmpty();
    }

    private boolean validatePasswordNonEmpty(final String string) {
        return string != null && string.matches("\\S*");
    }

    private Map<String, SnmpV3Attributes> snmpResponseParser(final NscsCommandResponse response) {
        final Map<String, SnmpV3Attributes> nodeSnmpConfigurations = new HashMap<>();

        if (response.isNameMultipleValueResponseType()) {
            final NscsNameMultipleValueCommandResponse tempResponse = (NscsNameMultipleValueCommandResponse) response;
            for (final Iterator<Entry> entryList = tempResponse.iterator(); entryList.hasNext();) {
                final Entry entry = entryList.next();
                if (!entry.getName().isEmpty() && !HEADER_NODE.equals(entry.getName())) {
                    final String[] configuration = entry.getValues();
                    nodeSnmpConfigurations.put(entry.getName(), setSnmpAttributes(configuration));
                }
             }
        }
        return nodeSnmpConfigurations;
    }

    private SnmpV3Attributes setSnmpAttributes(final String[] configuration) {
        if (configuration.length >= 4) {
               return new SnmpV3Attributes(SnmpAuthProtocol.valueOf(configuration[0]), configuration[1],
                                           SnmpPrivProtocol.valueOf(configuration[2]), configuration[3]);
           } else {
               final String responseDisplay = Arrays.toString(configuration);
               final String msg = "Missing SNMPv3 attribute(s) in response" + responseDisplay;
               logger.error(msg);
               throw new CredentialServiceException(msg);
           }
    }

    private void addPredefinedLdapPropertyToNscsCommand(final String enablingPredefiniedENMLDAPUser, final NscsPropertyCommand nscsCommand) {
        if (enablingPredefiniedENMLDAPUser != null) {
            nscsCommand.getProperties().put(CredentialsCommand.LDAP_USER_ENABLE_PROPERTY, enablingPredefiniedENMLDAPUser);
        }
    }

    private void addNodeCliPropertyToNscsCommand(final CredentialAttributes credentialAttributes, final NscsPropertyCommand nscsCommand) {
        if (credentialAttributes.getNodeCliUser() != null) {
            nscsCommand.getProperties().put(NODECLI_UN_KEY, credentialAttributes.getNodeCliUser().getUsername());
            nscsCommand.getProperties().put(NODECLI_UP_KEY, credentialAttributes.getNodeCliUser().getPassword());
            logger.debug("Added to command nodeCliUN: [{}] and nodeCliUP: [{}]", nscsCommand.getProperties().get(NODECLI_UN_KEY), CRYPTED_UP);
        }
    }

    private void addNwiebPropertyToNscsCommand(final CredentialAttributes credentialAttributes, final NscsPropertyCommand nscsCommand,
                                               final List<String> expectedAttributeKeys) {
        if (expectedAttributeKeys.contains(CredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY) && credentialAttributes.getNwiebSecureUser() != null) {
            nscsCommand.getProperties().put(NWIEB_SECURE_UN_KEY, credentialAttributes.getNwiebSecureUser().getUsername());
            nscsCommand.getProperties().put(NWIEB_SECURE_UP_KEY, credentialAttributes.getNwiebSecureUser().getPassword());
            logger.debug("Added to command nwiebSecureUN: [{}] and nwiebSecureUP: [{}]", nscsCommand.getProperties().get(NWIEB_SECURE_UN_KEY),
                    CRYPTED_UP);
        }
    }

    private void addNwieaPropertyToNscsCommand(final CredentialAttributes credentialAttributes, final NscsPropertyCommand nscsCommand,
                                               final List<String> expectedAttributeKeys) {
        if (expectedAttributeKeys.contains(CredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY) && credentialAttributes.getNwieaSecureUser() != null) {
            nscsCommand.getProperties().put(NWIEA_SECURE_UN_KEY, credentialAttributes.getNwieaSecureUser().getUsername());
            nscsCommand.getProperties().put(NWIEA_SECURE_UP_KEY, credentialAttributes.getNwieaSecureUser().getPassword());
            logger.debug("Added to command nwieaSecureUN: [{}] and nwieaSecureUP: [{}]", nscsCommand.getProperties().get(NWIEA_SECURE_UN_KEY),
                    CRYPTED_UP);
        }
    }

    private void addRootPropertyToNscsCommand(final CredentialAttributes credentialAttributes, final NscsPropertyCommand nscsCommand,
                                              final List<String> expectedAttributeKeys) {
        if (expectedAttributeKeys.contains(CredentialsCommand.ROOT_USER_NAME_PROPERTY)) {
            if (credentialAttributes.getRootUser() != null) {
                nscsCommand.getProperties().put(ROOT_UN_KEY, credentialAttributes.getRootUser().getUsername());
                nscsCommand.getProperties().put(ROOT_UP_KEY, credentialAttributes.getRootUser().getPassword());
            } else {
                //workaround in case of CREATE_CREDENTIALS and ERBS node
                if (nscsCommand.getCommandType().toString().equalsIgnoreCase(NscsCommandType.CREATE_CREDENTIALS.toString())
                        && credentialAttributes.getUnSecureUser() != null) {
                    nscsCommand.getProperties().put(ROOT_UN_KEY, "unsupportedUsername");
                    nscsCommand.getProperties().put(ROOT_UP_KEY, "unsupportedPassword");
                }
            }
            logger.debug("Added to command rootUN: [{}] and rootUP: [{}]", nscsCommand.getProperties().get(ROOT_UN_KEY), CRYPTED_UP);
        }
    }

    private void addNormalPropertyToNscsCommand(final CredentialAttributes credentialAttributes, final NscsPropertyCommand nscsCommand,
                                                final List<String> expectedAttributeKeys) {
        if (expectedAttributeKeys.contains(CredentialsCommand.NORMAL_USER_NAME_PROPERTY) && credentialAttributes.getUnSecureUser() != null) {
            nscsCommand.getProperties().put(NORMAL_UN_KEY, credentialAttributes.getUnSecureUser().getUsername());
            nscsCommand.getProperties().put(NORMAL_UP_KEY, credentialAttributes.getUnSecureUser().getPassword());
            logger.debug("Added to command normalUN: [{}] and normalUP: [{}]", nscsCommand.getProperties().get(NORMAL_UN_KEY), CRYPTED_UP);
        }
    }

    private void addSecurePropertyToNscsCommand(final CredentialAttributes credentialAttributes, final NscsPropertyCommand nscsCommand,
                                                final List<String> expectedAttributeKeys) {
        if (expectedAttributeKeys.contains(CredentialsCommand.SECURE_USER_NAME_PROPERTY) && credentialAttributes.getSecureUser() != null) {
            nscsCommand.getProperties().put(SECURE_UN_KEY, credentialAttributes.getSecureUser().getUsername());
            nscsCommand.getProperties().put(SECURE_UP_KEY, credentialAttributes.getSecureUser().getPassword());
            logger.debug("Added to command secureUN: [{}] and secureUP: [{}]", nscsCommand.getProperties().get(SECURE_UN_KEY), CRYPTED_UP);
        }
    }

}
