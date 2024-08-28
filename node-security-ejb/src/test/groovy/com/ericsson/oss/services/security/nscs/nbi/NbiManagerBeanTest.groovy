/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2024
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi

import java.util.concurrent.locks.Lock

import javax.inject.Inject

import org.slf4j.Logger

import com.ericsson.cds.cdi.support.rule.ImplementationClasses
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.NscsService
import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes
import com.ericsson.nms.security.nscs.api.credentials.UserCredentials
import com.ericsson.nms.security.nscs.api.enums.SnmpAuthProtocol
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException
import com.ericsson.nms.security.nscs.api.exception.CredentialServiceException
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException
import com.ericsson.nms.security.nscs.api.exception.NetworkElementSecurityNotfoundException
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.ejb.credential.CredentialServiceBean
import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager
import com.ericsson.oss.services.security.nscs.context.NscsContextService

class NbiManagerBeanTest extends CdiSpecification {

    private Lock resourceLock = Mock()

    @ObjectUnderTest
    NbiManagerBean manager

    @Inject
    Logger logger;

    @MockedImplementation
    NscsCMReaderService readerMock;

    @MockedImplementation
    NormalizableNodeReference normalizedMock

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelServiceMock

    @MockedImplementation
    NscsContextService nscsContextServiceMock

    @MockedImplementation
    NscsService nscsServiceMock

    @ImplementationInstance
    LockManager lockManager = [
        getDistributedLock : { String lockName, String clusterName ->
            return resourceLock
        }
    ] as LockManager

    @ImplementationClasses
    def myclass = [
        NbiServiceBean.class,
        CredentialServiceBean.class
    ]


    def 'object under test'() {
        expect:
        manager != null
    }

    def 'create or update credentials for a node, no Exception'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList("pippo")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        nscsCapabilityModelServiceMock.getExpectedCredentialsParams(_) >> Arrays.asList("secureusername","rootusername","nodecliusername","normalusername","nwieasecureusername","nwiebsecureusername")
        nscsContextServiceMock.setInputNodeNameContextValue(_) >> null
        readerMock.exists(_) >> null
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def nwieasecureuser = new UserCredentials("nwieaSecureUserName", "nwieaSecurePass")
        def nwiebsecureuser = new UserCredentials("nwiebSecureUserName", "nwiebSecurePass")
        def nodecliuser = new UserCredentials("nodeCliUserName","nodeCliUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser, nwieasecureuser, nwiebsecureuser, nodecliuser)
        def enablingPredefinedENMLDAPUser = "enable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        true == true
    }

    def 'create or update credentials for a node, no rootuser no Exception'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList("pippo")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        nscsCapabilityModelServiceMock.getExpectedCredentialsParams(_) >> Arrays.asList("secureusername","rootusername","nodecliusername","normalusername","nwieasecureusername","nwiebsecureusername")
        nscsContextServiceMock.setInputNodeNameContextValue(_) >> null
        readerMock.exists(_) >> null
        def nodeNameorFdn = "Node1"
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def nwieasecureuser = new UserCredentials("nwieaSecureUserName", "nwieaSecurePass")
        def nwiebsecureuser = new UserCredentials("nwiebSecureUserName", "nwiebSecurePass")
        def nodecliuser = new UserCredentials("nodeCliUserName","nodeCliUserPass")
        def credentialAttributes = new CredentialAttributes(null, normalUser, secureUser, nwieasecureuser, nwiebsecureuser, nodecliuser)
        def enablingPredefinedENMLDAPUser = "enable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        true == true
    }

    def 'create or update credentials for a node, get NodeDoesNotExistException while validate attributes'() {
        given:
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser)
        def enablingPredefinedENMLDAPUser = "enable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        thrown(NodeDoesNotExistException.class)
    }

    def 'create or update credentials for a node, get NodeDoesNotExistException'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList("pippo")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock >> null
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        nscsCapabilityModelServiceMock.getExpectedCredentialsParams(_) >> Arrays.asList("secureusername","rootusername","nodecliusername","normalusername","nwieasecureusername","nwiebsecureusername")
        nscsContextServiceMock.setInputNodeNameContextValue(_) >> null
        readerMock.exists(_) >> null
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser)
        def enablingPredefinedENMLDAPUser = "enable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        thrown(NodeDoesNotExistException.class)
    }

    def 'create or update credentials for a node, get NscsBadRequestException'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList( "rootusername", "normalusername")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def nwieasecureuser = new UserCredentials("nwieaSecureUserName", "nwieaSecurePass")
        def nwiebsecureuser = new UserCredentials("nwiebSecureUserName", "nwiebSecurePass")
        def nodecliuser = new UserCredentials("nodeCliUserName","nodeCliUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser, nwieasecureuser, nwiebsecureuser, nodecliuser)
        def enablingPredefinedENMLDAPUser = null
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'create or update credentials for a node, get InvalidNodeNameException'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList( "pippo")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        nscsContextServiceMock.setInputNodeNameContextValue(_) >> {
            throw new InvalidNodeNameException()
        }
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser)
        def enablingPredefinedENMLDAPUser = "disable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        thrown(InvalidNodeNameException.class)
    }

    def 'create or update credentials for a node, get NetworkElementSecurityNotfoundException'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList("pippo")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        nscsContextServiceMock.setInputNodeNameContextValue(_) >> {
            def exception = new NetworkElementSecurityNotfoundException()
            throw new CredentialServiceException("Errore", exception)
        }
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser)
        def enablingPredefinedENMLDAPUser = "enable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        thrown(NetworkElementSecurityNotfoundException.class)
    }
    // for coverage
    def 'create or update credentials for a node, get NetworkElementSecurityNotfoundException causedBy CredentialServiceException - NetworkElementSecurityNotfoundException'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList("pippo")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        nscsContextServiceMock.setInputNodeNameContextValue(_) >> {
            def exception = new NetworkElementSecurityNotfoundException()
            def credEx = new CredentialServiceException("Errore", exception)
            throw new CredentialServiceException("Errore", credEx)
        }
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser)
        def enablingPredefinedENMLDAPUser = "enable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        thrown(NetworkElementSecurityNotfoundException.class)
    }


    def 'create or update credentials for a node, get NodeDoesNotExistException causedBy InvalidNodeNameException'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList("pippo")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        nscsContextServiceMock.setInputNodeNameContextValue(_) >> {
            def exception = new InvalidNodeNameException()
            throw new CredentialServiceException("Errore", exception)
        }
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser)
        def enablingPredefinedENMLDAPUser = "enable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        thrown(NodeDoesNotExistException.class)
    }
    // for coverage
    def 'create or update credentials for a node, get NscsBadRequestException causedBy CredentialServiceException - InvalidNodeNameException'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList("pippo")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        nscsContextServiceMock.setInputNodeNameContextValue(_) >> {
            def exception = new InvalidNodeNameException()
            def credEx = new CredentialServiceException("Errore", exception)
            throw new CredentialServiceException("Errore", credEx)
        }
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser)
        def enablingPredefinedENMLDAPUser = "enable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        thrown(NodeDoesNotExistException.class)
    }

    def 'create or update credentials for a node, get NscsBadRequestException causedBy CommandSyntaxException'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList("pippo")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        nscsContextServiceMock.setInputNodeNameContextValue(_) >> {
            def exception = new CommandSyntaxException()
            throw new CredentialServiceException("Errore", exception)
        }
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser)
        def enablingPredefinedENMLDAPUser = "enable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        thrown(NscsBadRequestException.class)
    }
    // for coverage
    def 'create or update credentials for a node, get NscsBadRequestException causedBy CredentialServiceException - CommandSyntaxException'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList("pippo")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        nscsContextServiceMock.setInputNodeNameContextValue(_) >> {
            def exception = new CommandSyntaxException()
            def credEx = new CredentialServiceException("Errore", exception)
            throw new CredentialServiceException("Errore", credEx)
        }
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser)
        def enablingPredefinedENMLDAPUser = "enable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'create or update credentials for a node, get NscsBadRequestException causedBy SecurityFunctionMoNotfoundException'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList("pippo")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        nscsContextServiceMock.setInputNodeNameContextValue(_) >> {
            def exception = new SecurityFunctionMoNotfoundException()
            throw new CredentialServiceException("Errore", exception)
        }
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser)
        def enablingPredefinedENMLDAPUser = "enable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        thrown(NscsBadRequestException.class)
    }
    // for coverage
    def 'create or update credentials for a node, get NscsBadRequestException causedBy CredentialServiceException - SecurityFunctionMoNotfoundException'() {
        given:
        def unexpectedAttributeKeys = Arrays.asList("pippo")
        readerMock.getNormalizedNodeReference(_) >> normalizedMock
        nscsCapabilityModelServiceMock.getUnexpectedCredentialsParams(_) >> unexpectedAttributeKeys
        nscsContextServiceMock.setInputNodeNameContextValue(_) >> {
            def exception = new SecurityFunctionMoNotfoundException()
            def credEx = new CredentialServiceException("Errore", exception)
            throw new CredentialServiceException("Errore", credEx)
        }
        def nodeNameorFdn = "Node1"
        def rootUser = new UserCredentials("rootUserName", "rootUserPass")
        def secureUser = new UserCredentials("secureUserName", "secureUserPass")
        def normalUser = new UserCredentials("normalUserName","normalUserPass")
        def credentialAttributes = new CredentialAttributes(rootUser, normalUser, secureUser)
        def enablingPredefinedENMLDAPUser = "enable"
        when:
        manager.createOrUpdateNodeCredentials(nodeNameorFdn,credentialAttributes,enablingPredefinedENMLDAPUser)
        then:
        thrown(NscsBadRequestException.class)
    }

    def 'create or update snmp for a node, no exception'() {
        given:
        def nodeNameorFdn = "Node1"
        def snmpV3Attributes = new SnmpV3Attributes(SnmpAuthProtocol.MD5, "authKey")
        nscsServiceMock.processCommand(_) >> null
        when:
        manager.createOrUpdateNodeSnmp(nodeNameorFdn, snmpV3Attributes, SnmpSecurityLevel.AUTH_NO_PRIV)
        then:
        true == true
    }

    def 'create or update snmp for a node, get NetworkElementSecurityNotfoundException'() {
        given:
        def nodeNameorFdn = "Node1"
        def snmpV3Attributes = new SnmpV3Attributes(SnmpAuthProtocol.MD5, "authKey")
        nscsServiceMock.processCommand(_) >> {
            def exception = new NetworkElementSecurityNotfoundException()
            throw new CredentialServiceException("Errore", exception)
        }
        when:
        manager.createOrUpdateNodeSnmp(nodeNameorFdn, snmpV3Attributes, SnmpSecurityLevel.AUTH_NO_PRIV)
        then:
        thrown(NetworkElementSecurityNotfoundException.class)
    }
}
