/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl

import javax.inject.Inject

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse
import com.ericsson.nms.security.nscs.api.command.NscsNameValueCommandResponse
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager
import com.ericsson.nms.security.nscs.api.command.types.LdapConfigurationCommand
import com.ericsson.nms.security.nscs.api.command.types.LdapRenewCommand
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException
import com.ericsson.nms.security.nscs.api.exception.LdapConfigureWfException
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes
import com.ericsson.nms.security.nscs.api.exception.PlatformSpecificConfigurationProviderException
import com.ericsson.nms.security.nscs.handler.CommandContext
import com.ericsson.nms.security.nscs.ldap.entities.LdapConfigurations
import com.ericsson.nms.security.nscs.ldap.entities.NodeSpecificLdapConfiguration
import com.ericsson.nms.security.nscs.ldap.utility.LdapConfigurationUnMarshaller
import com.ericsson.nms.security.nscs.ldap.utility.LdapConstants
import com.ericsson.nms.security.nscs.ldap.utility.UserProvidedLdapConfigurationValidator
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementService
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountData
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ComAAInfo
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ConnectionData
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.LdapAddress
import com.ericsson.oss.services.dto.JobStatusRecord
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler

import spock.lang.Shared
import spock.lang.Unroll

class LdapCommandHandlerHelperTest extends CdiSpecification {

    @ObjectUnderTest
    LdapCommandHandlerHelper ldapCommandHandlerHelper

    @MockedImplementation
    private CommandContext context

    @MockedImplementation
    private NscsLogger nscsLogger

    @MockedImplementation
    private LdapConfigurationUnMarshaller ldapConfigurationUnMarshaller;

    @Inject
    private NscsJobCacheHandler nscsJobCacheHandler

    @Shared
    private JobStatusRecord record

    @Shared
    private UUID JOB_UUID

    @Shared
    private LdapConfigurationCommand command

    private final String fileURI = "file:"

    @ImplementationInstance
    UserProvidedLdapConfigurationValidator userProvidedLdapConfigurationValidatorSuccess = [
        validate :  {
            return
        }
    ] as UserProvidedLdapConfigurationValidator

    @ImplementationInstance
    UserProvidedLdapConfigurationValidator userProvidedLdapConfigurationValidatorFailed = [
        validate :  {
            throw new InvalidFileContentException()
        }
    ] as UserProvidedLdapConfigurationValidator

    @ImplementationInstance
    UserProvidedLdapConfigurationValidator userProvidedLdapConfigurationValidator = [
        validate :  { NodeSpecificLdapConfiguration nodeSpecificLdapConfiguration ->
            if (nodeSpecificLdapConfiguration.getNodeFdn() == "LTE01dg2ERBS00001") {
                return;
            } else {
                throw new NodeNotSynchronizedException();
            }
        }
    ] as UserProvidedLdapConfigurationValidator

    @ImplementationInstance
    IdentityManagementService identityManagementService = [
        createProxyAgentAccount : {
            return new ProxyAgentAccountData ("cn=ProxyAccount_4,ou=Profiles,dc=apache,dc=com", "osz45rph")
        }
    ] as IdentityManagementService

    @ImplementationInstance
    ComAAInfo comAAInfo = [
        getConnectionData : {
            LdapAddress ipv4AddressData = new LdapAddress("192.168.0.129", "192.168.0.130")
            LdapAddress ipv6AddressData = new LdapAddress("2001:cdba:0:0:0:0:3257:9652", "2001:cdba:0:0:0:0:3257:9651")
            return new ConnectionData(ipv4AddressData, ipv6AddressData, 1389, 1636)
        }
    ] as ComAAInfo

    @ImplementationInstance
    NscsCommandManager nscsCommandManagerFailed = [
        executeConfigureLdapWfs : { nodes, jobStatusRecord ->
            throw new LdapConfigureWfException()
        },
        executeReconfigureLdapWfs : { nodes, jobStatusRecord ->
            throw new LdapConfigureWfException()
        },
        executeRenewLdapWfs : { nodes, jobStatusRecord ->
            throw new LdapConfigureWfException()
        }
    ] as NscsCommandManager

    @ImplementationInstance
    NscsCommandManager nscsCommandManager = [
        executeConfigureLdapWfs : { nodes, jobStatusRecord ->
            return
        },
        executeReconfigureLdapWfs : { nodes, jobStatusRecord ->
            return
        },
        executeRenewLdapWfs : { nodes, jobStatusRecord ->
            return
        }
    ] as NscsCommandManager

    LdapConfigurations ldapConfigurations = new LdapConfigurations()
    LdapConfigurations emptyLdapConfigurations = new LdapConfigurations()
    NodeSpecificLdapConfiguration ldapConfiguration = new NodeSpecificLdapConfiguration()
    List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfigurationList = new ArrayList<NodeSpecificLdapConfiguration>()

    @Override
    Object addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {
    }

    def setup() {
        record = new JobStatusRecord()
        JOB_UUID = UUID.randomUUID()
        record.setJobId(JOB_UUID)
        ldapConfiguration.setUseTls(true);
        ldapConfiguration.setTlsMode("LDAPS");
        ldapConfiguration.setNodeFdn("LTE44dg2ERBS00001");
        nodeSpecificLdapConfigurationList.add(ldapConfiguration);
        ldapConfigurations.setConfigurations(nodeSpecificLdapConfigurationList);
    }

    def "When everything is correct task should configure LDAP"() {
        given:
        ldapCommandHandlerHelper.userProvidedLdapConfigurationValidator = userProvidedLdapConfigurationValidatorSuccess
        command = setupCommandLDAP("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", false)
        nscsJobCacheHandler.insertJob(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE.getNscsCommandType()) >> {
            return record
        }
        ldapCommandHandlerHelper.nscsCommandManager = nscsCommandManager
        and:
        ldapConfigurationUnMarshaller.buildLdapConfigurationFromXMLContent(_ as String) >> ldapConfigurations
        when:
        NscsMessageCommandResponse response = ldapCommandHandlerHelper.processActivate(command,context,LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE)
        then:
        String responseMessageWithJobId = String.format(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE.getExecutedMessage(),
                record.getJobId().toString())
        response.getMessage().contains(responseMessageWithJobId)
    }

    def "When everything is correct task should reconfigure LDAP"() {
        given:
        ldapCommandHandlerHelper.userProvidedLdapConfigurationValidator = userProvidedLdapConfigurationValidatorSuccess
        command = setupCommandLDAP("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", false)
        nscsJobCacheHandler.insertJob(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RECONFIGURE.getNscsCommandType()) >> {
            return record
        }
        ldapCommandHandlerHelper.nscsCommandManager = nscsCommandManager
        and:
        ldapConfigurationUnMarshaller.buildLdapConfigurationFromXMLContent(_ as String) >> ldapConfigurations
        when:
        NscsMessageCommandResponse response = ldapCommandHandlerHelper.processActivate(command,context,LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RECONFIGURE)
        then:
        String responseMessageWithJobId = String.format(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RECONFIGURE.getExecutedMessage(),
                record.getJobId().toString())
        response.getMessage().contains(responseMessageWithJobId)
    }

    def "When XML file is invalid task should thrown InvalidInputXMLFileException "() {
        given:
        command = setupCommandLDAP("src/test/resources/ldap/LdapConfiguration_Invalid.xml", false)
        nscsJobCacheHandler.insertJob(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE.getNscsCommandType()) >> {
            return record
        }
        when:
        ldapCommandHandlerHelper.processActivate(command,context,LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE)
        then:
        InvalidInputXMLFileException e = thrown()
        e.getMessage() == NscsErrorCodes.INVALID_INPUT_XML_FILE
    }

    def "When command is MANUAL should return only config data"() {
        given:
        command = setupCommandLDAP("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", true)
        System.properties[LdapConstants.CONFIGURATION_JAVA_PROPERTIES]='src/test/resources/ldap/global.properties'
        when:
        NscsNameValueCommandResponse response = ldapCommandHandlerHelper.processActivate(command,context,LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE)
        then:
        response.isNameValueResponseType()
    }

    def "When command is MANUAL and bad data in global properties thrown exception"() {
        given:
        command = setupCommandLDAP("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", true)
        System.properties[LdapConstants.CONFIGURATION_JAVA_PROPERTIES]='src/test/resources/notexistpath/global.properties'
        when:
        NscsNameValueCommandResponse response = ldapCommandHandlerHelper.processActivate(command,context,LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE)
        then:
        PlatformSpecificConfigurationProviderException e = thrown()
        e.getMessage().contains(PlatformSpecificConfigurationProviderException.LDAP_BASE_DN_INVALID)
    }

    def "When XML file is valid but validation on node fails should thrown exception"() {
        given:
        ldapCommandHandlerHelper.userProvidedLdapConfigurationValidator = userProvidedLdapConfigurationValidatorFailed
        command = setupCommandLDAP("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", false)
        and:
        ldapConfigurationUnMarshaller.buildLdapConfigurationFromXMLContent(_ as String) >> ldapConfigurations
        when:
        NscsCommandResponse response = ldapCommandHandlerHelper.processActivate(command,context,LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE)
        then:
        notThrown(Exception.class)
        and:
        response != null
        response.isNameMultipleValueResponseType() == true
    }

    def "When XML file is valid but no nodes specified should thrown exception"() {
        given:
        command = setupCommandLDAP("src/test/resources/ldap/LdapConfiguration_NoNodes.xml", false)
        and:
        ldapConfigurationUnMarshaller.buildLdapConfigurationFromXMLContent(_ as String) >> emptyLdapConfigurations
        when:
        NscsCommandResponse response = ldapCommandHandlerHelper.processActivate(command,context,LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE)
        then:
        thrown(InvalidInputXMLFileException.class)
    }

    def "When Ldap Workflow starting is failed thrown exception"() {
        given:
        ldapCommandHandlerHelper.userProvidedLdapConfigurationValidator = userProvidedLdapConfigurationValidatorSuccess
        ldapCommandHandlerHelper.nscsCommandManager = nscsCommandManagerFailed
        command = setupCommandLDAP("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", false)
        and:
        ldapConfigurationUnMarshaller.buildLdapConfigurationFromXMLContent(_ as String) >> ldapConfigurations
        when:
        NscsNameValueCommandResponse response = ldapCommandHandlerHelper.processActivate(command,context,LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE)
        then:
        LdapConfigureWfException e = thrown()
        e.getMessage() == NscsErrorCodes.LDAP_CONFIGURE_WF_FAILED + " : " + LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_CONFIGURE.getFailedMessage()
    }

    def 'unforced ldap renew with xmlfile' () {
        given:
        command = setupCommandLDAPRenew("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", false)
        when:
        NscsCommandResponse response = ldapCommandHandlerHelper.processActivate(command, context, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isConfirmationResponseType() == true
    }

    def 'successful forced ldap renew with xmlfile' () {
        given:
        command = setupCommandLDAPRenew("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", true)
        and:
        ldapConfigurationUnMarshaller.buildLdapConfigurationFromXMLContent(_ as String) >> ldapConfigurations
        and:
        ldapCommandHandlerHelper.userProvidedLdapConfigurationValidator = userProvidedLdapConfigurationValidatorSuccess
        and:
        nscsJobCacheHandler.insertJob(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW.getNscsCommandType()) >> {
            return record
        }
        and:
        ldapCommandHandlerHelper.nscsCommandManager = nscsCommandManager
        when:
        NscsCommandResponse response = ldapCommandHandlerHelper.processActivate(command, context, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isMessageResponseType() == true
    }

    def 'all failed forced ldap renew with xmlfile' () {
        given:
        command = setupCommandLDAPRenew("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", true)
        and:
        ldapConfigurationUnMarshaller.buildLdapConfigurationFromXMLContent(_ as String) >> ldapConfigurations
        and:
        ldapCommandHandlerHelper.userProvidedLdapConfigurationValidator = userProvidedLdapConfigurationValidatorFailed
        when:
        NscsCommandResponse response = ldapCommandHandlerHelper.processActivate(command, context, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isNameMultipleValueResponseType() == true
    }

    def 'partially successful forced ldap renew with xmlfile' () {
        given:
        command = setupCommandLDAPRenew("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", true)
        and:
        LdapConfigurations ldapConfigurations1 = new LdapConfigurations()
        List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfigurationList1 = new ArrayList<NodeSpecificLdapConfiguration>()
        NodeSpecificLdapConfiguration ldapConfiguration1 = new NodeSpecificLdapConfiguration()
        ldapConfiguration1.setUseTls(true);
        ldapConfiguration1.setTlsMode("LDAPS");
        ldapConfiguration1.setNodeFdn("LTE01dg2ERBS00001");
        nodeSpecificLdapConfigurationList1.add(ldapConfiguration1);
        NodeSpecificLdapConfiguration ldapConfiguration2 = new NodeSpecificLdapConfiguration()
        ldapConfiguration2.setUseTls(true);
        ldapConfiguration2.setTlsMode("LDAPS");
        ldapConfiguration2.setNodeFdn("LTE01dg2ERBS00002");
        nodeSpecificLdapConfigurationList1.add(ldapConfiguration2);
        ldapConfigurations1.setConfigurations(nodeSpecificLdapConfigurationList1);
        ldapConfigurationUnMarshaller.buildLdapConfigurationFromXMLContent(_ as String) >> ldapConfigurations1
        and:
        ldapCommandHandlerHelper.userProvidedLdapConfigurationValidator = userProvidedLdapConfigurationValidator
        and:
        nscsJobCacheHandler.insertJob(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW.getNscsCommandType()) >> {
            return record
        }
        and:
        ldapCommandHandlerHelper.nscsCommandManager = nscsCommandManager
        when:
        NscsCommandResponse response = ldapCommandHandlerHelper.processActivate(command, context, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isNameMultipleValueResponseType() == true
    }

    @Unroll
    def 'successful forced ldap renew with xmlfile containing duplicate node #nodenameorfdn with same parameters' () {
        given:
        command = setupCommandLDAPRenew("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", true)
        and:
        LdapConfigurations ldapConfigurations1 = new LdapConfigurations()
        List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfigurationList1 = new ArrayList<NodeSpecificLdapConfiguration>()
        NodeSpecificLdapConfiguration ldapConfiguration1 = new NodeSpecificLdapConfiguration()
        ldapConfiguration1.setUseTls(true);
        ldapConfiguration1.setTlsMode("LDAPS");
        ldapConfiguration1.setNodeFdn("LTE01dg2ERBS00001");
        ldapConfiguration1.setUserLabel("test");
        nodeSpecificLdapConfigurationList1.add(ldapConfiguration1);
        NodeSpecificLdapConfiguration ldapConfiguration2 = new NodeSpecificLdapConfiguration()
        ldapConfiguration2.setUseTls(true);
        ldapConfiguration2.setTlsMode("LDAPS");
        ldapConfiguration2.setNodeFdn(nodenameorfdn);
        ldapConfiguration2.setUserLabel("test");
        nodeSpecificLdapConfigurationList1.add(ldapConfiguration2);
        ldapConfigurations1.setConfigurations(nodeSpecificLdapConfigurationList1);
        ldapConfigurationUnMarshaller.buildLdapConfigurationFromXMLContent(_ as String) >> ldapConfigurations1
        and:
        ldapCommandHandlerHelper.userProvidedLdapConfigurationValidator = userProvidedLdapConfigurationValidator
        and:
        nscsJobCacheHandler.insertJob(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW.getNscsCommandType()) >> {
            return record
        }
        and:
        ldapCommandHandlerHelper.nscsCommandManager = nscsCommandManager
        when:
        NscsCommandResponse response = ldapCommandHandlerHelper.processActivate(command, context, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW)
        then:
        notThrown(Exception.class)
        and:
        response != null
        and:
        response.isMessageResponseType() == true
        where:
        nodenameorfdn << [
            "LTE01dg2ERBS00001",
            "NetworkElement=LTE01dg2ERBS00001"
        ]
    }

    @Unroll
    def 'failed forced ldap renew with xmlfile containing duplicate node #nodenameorfdn with different parameters' () {
        given:
        command = setupCommandLDAPRenew("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", true)
        and:
        LdapConfigurations ldapConfigurations1 = new LdapConfigurations()
        List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfigurationList1 = new ArrayList<NodeSpecificLdapConfiguration>()
        NodeSpecificLdapConfiguration ldapConfiguration1 = new NodeSpecificLdapConfiguration()
        ldapConfiguration1.setUseTls(true);
        ldapConfiguration1.setTlsMode("STARTTLS");
        ldapConfiguration1.setNodeFdn("LTE01dg2ERBS00001");
        ldapConfiguration1.setUserLabel("test");
        nodeSpecificLdapConfigurationList1.add(ldapConfiguration1);
        NodeSpecificLdapConfiguration ldapConfiguration2 = new NodeSpecificLdapConfiguration()
        ldapConfiguration2.setUseTls(true);
        ldapConfiguration2.setTlsMode("LDAPS");
        ldapConfiguration2.setNodeFdn(nodenameorfdn);
        ldapConfiguration2.setUserLabel("test");
        nodeSpecificLdapConfigurationList1.add(ldapConfiguration2);
        ldapConfigurations1.setConfigurations(nodeSpecificLdapConfigurationList1);
        ldapConfigurationUnMarshaller.buildLdapConfigurationFromXMLContent(_ as String) >> ldapConfigurations1
        and:
        ldapCommandHandlerHelper.userProvidedLdapConfigurationValidator = userProvidedLdapConfigurationValidator
        and:
        nscsJobCacheHandler.insertJob(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW.getNscsCommandType()) >> {
            return record
        }
        and:
        ldapCommandHandlerHelper.nscsCommandManager = nscsCommandManager
        when:
        NscsCommandResponse response = ldapCommandHandlerHelper.processActivate(command, context, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW)
        then:
        thrown(InvalidInputXMLFileException.class)
        where:
        nodenameorfdn << [
            "LTE01dg2ERBS00001",
            "NetworkElement=LTE01dg2ERBS00001"
        ]
    }

    def 'partially successful forced ldap renew with xmlfile and start workflow fails' () {
        given:
        command = setupCommandLDAPRenew("src/test/resources/ldap/LdapConfiguration_ValidLdaps.xml", true)
        and:
        LdapConfigurations ldapConfigurations1 = new LdapConfigurations()
        List<NodeSpecificLdapConfiguration> nodeSpecificLdapConfigurationList1 = new ArrayList<NodeSpecificLdapConfiguration>()
        NodeSpecificLdapConfiguration ldapConfiguration1 = new NodeSpecificLdapConfiguration()
        ldapConfiguration1.setUseTls(true);
        ldapConfiguration1.setTlsMode("LDAPS");
        ldapConfiguration1.setNodeFdn("LTE01dg2ERBS00001");
        nodeSpecificLdapConfigurationList1.add(ldapConfiguration1);
        NodeSpecificLdapConfiguration ldapConfiguration2 = new NodeSpecificLdapConfiguration()
        ldapConfiguration2.setUseTls(true);
        ldapConfiguration2.setTlsMode("LDAPS");
        ldapConfiguration2.setNodeFdn("LTE01dg2ERBS00002");
        nodeSpecificLdapConfigurationList1.add(ldapConfiguration2);
        ldapConfigurations1.setConfigurations(nodeSpecificLdapConfigurationList1);
        ldapConfigurationUnMarshaller.buildLdapConfigurationFromXMLContent(_ as String) >> ldapConfigurations1
        and:
        ldapCommandHandlerHelper.userProvidedLdapConfigurationValidator = userProvidedLdapConfigurationValidator
        and:
        nscsJobCacheHandler.insertJob(LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW.getNscsCommandType()) >> {
            return record
        }
        and:
        ldapCommandHandlerHelper.nscsCommandManager = nscsCommandManagerFailed
        when:
        NscsCommandResponse response = ldapCommandHandlerHelper.processActivate(command, context, LdapCommandHandlerHelper.LDAPConfigurationMode.LDAP_RENEW)
        then:
        thrown(LdapConfigureWfException.class)
    }

    private LdapConfigurationCommand setupCommandLDAP(String xmlPath, boolean isManual) {
        final LdapConfigurationCommand command = new LdapConfigurationCommand()
        command.setCommandType(NscsCommandType.LDAP_CONFIGURATION)
        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray(xmlPath)

        final Map<String, Object> commandMap = new HashMap<String, Object>()
        commandMap.put(LdapConfigurationCommand.XML_FILE_PROPERTY, fileURI)
        commandMap.put(fileURI, INPUT_FILE_CONTENT)
        if(isManual) {
            commandMap.put(LdapConstants.MANUAL,"")
        }
        command.setProperties(commandMap)
        return command
    }

    private LdapConfigurationCommand setupCommandLDAPRenew(String xmlPath, boolean isForced) {
        final LdapConfigurationCommand command = new LdapRenewCommand()
        final byte[] INPUT_FILE_CONTENT = convertFileToByteArray(xmlPath)

        final Map<String, Object> commandMap = new HashMap<String, Object>()
        commandMap.put(LdapConfigurationCommand.XML_FILE_PROPERTY, fileURI)
        commandMap.put(fileURI, INPUT_FILE_CONTENT)
        if (isForced) {
            commandMap.put("force", null)
        }
        command.setProperties(commandMap)
        return command
    }

    private static byte[] convertFileToByteArray(final String fileLocation) {
        final File file = new File(fileLocation)
        FileInputStream fileInputStream = null

        final byte[] fileToBeParsed = new byte[(int) file.length()]

        try {
            fileInputStream = new FileInputStream(file)
            fileInputStream.read(fileToBeParsed)
        } catch (final Exception e) {
            e.printStackTrace()
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (IOException e) {
                    e.printStackTrace()
                }
            }
        }
        return fileToBeParsed
    }
}