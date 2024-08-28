package com.ericsson.nms.security.nscs.api;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes;
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.CredentialServiceException;
import com.ericsson.oss.itpf.sdk.core.annotation.EService;

@EService
@Remote
public interface CredentialService {

    /**
     * The method is called to create credentials for a node.
     *
     * @param credentialAttributes
     *            - object containing the credentials attributes to set.
     *
     * @param nodeName
     *            - the name of the node for which credentials will be created.
     *
     * @throws CredentialServiceException
     *            - the exception thrown by credential service
     */
        void createNodeCredentials (final CredentialAttributes credentialAttributes, final String nodeName)
            throws CredentialServiceException;

/**
     * The method is called to create credentials for a node.
     *
     * @param credentialAttributes
     *            - object containing the credentials attributes to set.
     *
     * @param nodeName
     *            - the name of the node for which credentials will be created.
     *
     * @param enablingPredefiniedENMLDAPUser
     *            - the LDAP user enabling/disabling
     *
     * @throws CredentialServiceException
     *            - the exception thrown by credential service
     */
        void createNodeCredentials(CredentialAttributes credentialAttributes, String inputNodeName, String enablingPredefiniedENMLDAPUser)
                throws CredentialServiceException;

    /**
     * @param nodeName
     *            - the name of the node to validate credentials for.
     *
     * @param credentialAttributes
     *            - object containing the credentials attributes to set.
     *
     * @return  
     *            - true if credentials attributes are valid for the given node, false otherwise.
     *
     */
        boolean validateAttributes(String nodeName, CredentialAttributes credentialAttributes);

    /**
     * The method is called to configure enrollment mode for a node.
     *
     * @param enrollmentMode
     *            - Enum defining the enrollment mode to set.
     *
     * @param nodeName
     *            - the name of the node for which the specified enrollment mode will be set.
     *
     * @throws CredentialServiceException
     *            - the exception thrown by credential service
     */
        void configureEnrollmentMode (final EnrollmentMode enrollmentMode, final String nodeName)
             throws CredentialServiceException;

    /**
     * The method is called to configure SNMPv3 attributes for a node/nodes according to security level.
     *
     * @param securityLevel
     *            - Parameter defining which SNMPv3 security level is applied
     *
     * @param snmpV3Attributes
     *            - Parameters for specific security level to set.
     *
     * @param nodes
     *            - List of node names for which the SNMPv3 configuration will be set.
     *
     * @throws CredentialServiceException
     *            - the exception thrown by credential service
     */
        void configureSnmpV3(final SnmpSecurityLevel securityLevel, final SnmpV3Attributes snmpV3Attributes, final List<String> nodes);

    /**
     * The method is called to read SNMPv3 configuration for a node/nodes.
     *
     * @param isPlainText
     *            - Parameter determines whether to show password.
     *
     * @param nodes
     *            - List of node names for which the SNMPv3 configuration is read.
     *
     * @return a Map containing SnmpV3Attributes mapped to different node names
     *
     * @throws CredentialServiceException
     *            - the exception thrown by credential service
     */
        Map<String, SnmpV3Attributes> getSnmpV3Configuration(final boolean isPlainText, final List<String> nodes);

}
