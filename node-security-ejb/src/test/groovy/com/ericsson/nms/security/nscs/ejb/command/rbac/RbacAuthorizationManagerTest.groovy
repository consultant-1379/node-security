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
package com.ericsson.nms.security.nscs.ejb.command.rbac

import static com.ericsson.nms.security.nscs.api.command.NscsCommandType.CREATE_SSH_KEY
import static com.ericsson.nms.security.nscs.api.command.NscsCommandType.DELETE_SSH_KEY
import static com.ericsson.nms.security.nscs.api.command.NscsCommandType.IMPORT_NODE_SSH_PRIVATE_KEY
import static com.ericsson.nms.security.nscs.api.command.NscsCommandType.LDAP_PROXY_DELETE
import static com.ericsson.nms.security.nscs.api.command.NscsCommandType.LDAP_PROXY_GET
import static com.ericsson.nms.security.nscs.api.command.NscsCommandType.LDAP_PROXY_SET
import static com.ericsson.nms.security.nscs.api.command.NscsCommandType.LDAP_RENEW
import static com.ericsson.nms.security.nscs.api.command.NscsCommandType.UPDATE_SSH_KEY

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.command.NscsCommandType
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand
import com.ericsson.nms.security.nscs.api.command.types.CertificateIssueCommand
import com.ericsson.nms.security.nscs.api.command.types.CertificateReissueCommand
import com.ericsson.nms.security.nscs.api.command.types.GetCertEnrollStateCommand
import com.ericsson.nms.security.nscs.api.command.types.GetCredentialsCommand
import com.ericsson.nms.security.nscs.api.command.types.GetSnmpCommand
import com.ericsson.nms.security.nscs.api.command.types.GetTrustCertInstallStateCommand
import com.ericsson.nms.security.nscs.api.command.types.TestCommand
import com.ericsson.nms.security.nscs.api.command.types.TrustDistributeCommand
import com.ericsson.nms.security.nscs.api.command.types.TrustRemoveCommand

import spock.lang.Unroll

class RbacAuthorizationManagerTest extends CdiSpecification {

    @ObjectUnderTest
    RbacAuthorizationManager rbacAuthorizationManager

    def 'object under test'() {
        expect:
        rbacAuthorizationManager != null
    }

    @Unroll
    def 'ldap command #commandtype'() {
        given:
        def NscsPropertyCommand command = new NscsPropertyCommand()
        command.setCommandType(commandtype)
        when:
        rbacAuthorizationManager.checkAuthorization(command)
        then:
        notThrown(Exception)
        where:
        commandtype << [
            LDAP_PROXY_GET,
            LDAP_PROXY_SET,
            LDAP_PROXY_DELETE,
            LDAP_RENEW
        ]
    }

    def 'test command read'() {
        given:
        def command = mock(TestCommand.class)
        command.getCommandType() >> NscsCommandType.TEST_COMMAND
        command.getWorkflows() >> null
        when:
        rbacAuthorizationManager.checkAuthorization(command)
        then:
        notThrown(Exception)
    }

    def 'test command execute'() {
        given:
        def command = mock(TestCommand.class)
        command.getCommandType() >> NscsCommandType.TEST_COMMAND
        command.getWorkflows() >> "1"
        when:
        rbacAuthorizationManager.checkAuthorization(command)
        then:
        notThrown(Exception)
    }

    @Unroll
    def 'test snmp get plain text #plaintext '() {
        given:
        def command = mock(GetSnmpCommand.class)
        command.getCommandType() >> NscsCommandType.GET_SNMP
        command.getPlainText() >> plaintext
        when:
        rbacAuthorizationManager.checkAuthorization(command)
        then:
        notThrown(Exception)
        where:
        plaintext << [null, "show", "hide"]
    }

    @Unroll
    def 'test trust remove #certype or #trustcat'() {
        given:
        def command = mock(TrustRemoveCommand.class)
        command.getCommandType() >> NscsCommandType.TRUST_REMOVE
        command.getCertType() >> certype
        command.getTrustCategory() >> trustcat
        when:
        rbacAuthorizationManager.checkAuthorization(command)
        then:
        notThrown(Exception)
        where:
        certype << [
            null,
            null,
            null,
            null,
            "IPSEC",
            "IPSEC",
            "IPSEC",
            "IPSEC",
            "LAAD",
            "LAAD",
            "LAAD",
            "LAAD",
            "OAM",
            "OAM",
            "OAM",
            "OAM"
        ]
        trustcat << [
            null,
            "IPSEC",
            "LAAD",
            "OAM",
            null,
            "IPSEC",
            "LAAD",
            "OAM",
            null,
            "IPSEC",
            "LAAD",
            "OAM",
            null,
            "IPSEC",
            "LAAD",
            "OAM"
        ]
    }

    @Unroll
    def 'test trust get #certype or #trustcat'() {
        given:
        def command = mock(GetTrustCertInstallStateCommand.class)
        command.getCommandType() >> NscsCommandType.GET_TRUST_CERT_INSTALL_STATE
        command.getCertType() >> certype
        command.getTrustCategory() >> trustcat
        when:
        rbacAuthorizationManager.checkAuthorization(command)
        then:
        notThrown(Exception)
        where:
        certype << [
            null,
            null,
            null,
            null,
            "IPSEC",
            "IPSEC",
            "IPSEC",
            "IPSEC",
            "LAAD",
            "LAAD",
            "LAAD",
            "LAAD",
            "OAM",
            "OAM",
            "OAM",
            "OAM"
        ]
        trustcat << [
            null,
            "IPSEC",
            "LAAD",
            "OAM",
            null,
            "IPSEC",
            "LAAD",
            "OAM",
            null,
            "IPSEC",
            "LAAD",
            "OAM",
            null,
            "IPSEC",
            "LAAD",
            "OAM"
        ]
    }

    @Unroll
    def 'test trust distribute #certype or #trustcat'() {
        given:
        def command = mock(TrustDistributeCommand.class)
        command.getCommandType() >> NscsCommandType.TRUST_DISTRIBUTE
        command.getCertType() >> certype
        command.getTrustCategory() >> trustcat
        when:
        rbacAuthorizationManager.checkAuthorization(command)
        then:
        notThrown(Exception)
        where:
        certype << [
            null,
            null,
            null,
            null,
            "IPSEC",
            "IPSEC",
            "IPSEC",
            "IPSEC",
            "LAAD",
            "LAAD",
            "LAAD",
            "LAAD",
            "OAM",
            "OAM",
            "OAM",
            "OAM"
        ]
        trustcat << [
            null,
            "IPSEC",
            "LAAD",
            "OAM",
            null,
            "IPSEC",
            "LAAD",
            "OAM",
            null,
            "IPSEC",
            "LAAD",
            "OAM",
            null,
            "IPSEC",
            "LAAD",
            "OAM"
        ]
    }

    @Unroll
    def 'test certificate get #certype'() {
        given:
        def command = mock(GetCertEnrollStateCommand.class)
        command.getCommandType() >> NscsCommandType.GET_CERT_ENROLL_STATE
        command.getCertType() >> certype
        when:
        rbacAuthorizationManager.checkAuthorization(command)
        then:
        notThrown(Exception)
        where:
        certype << [
            null,
            "IPSEC",
            "OAM"
        ]
    }

    @Unroll
    def 'test certificate reissue #certype'() {
        given:
        def command = mock(CertificateReissueCommand.class)
        command.getCommandType() >> NscsCommandType.CERTIFICATE_REISSUE
        command.getCertType() >> certype
        when:
        rbacAuthorizationManager.checkAuthorization(command)
        then:
        notThrown(Exception)
        where:
        certype << [
            null,
            "IPSEC",
            "OAM"
        ]
    }

    @Unroll
    def 'test certificate issue #certype'() {
        given:
        def command = mock(CertificateIssueCommand.class)
        command.getCommandType() >> NscsCommandType.CERTIFICATE_ISSUE
        command.getCertType() >> certype
        when:
        rbacAuthorizationManager.checkAuthorization(command)
        then:
        notThrown(Exception)
        where:
        certype << [
            null,
            "IPSEC",
            "OAM"
        ]
    }

    @Unroll
    def 'test credentials get plain text #plaintext '() {
        given:
        def command = mock(GetCredentialsCommand.class)
        command.getCommandType() >> NscsCommandType.GET_CREDENTIALS
        command.getPlainText() >> plaintext
        when:
        rbacAuthorizationManager.checkAuthorization(command)
        then:
        notThrown(Exception)
        where:
        plaintext << [null, "show", "hide"]
    }

    @Unroll
    def 'sshkey command #commandtype'() {
        given:
        def NscsPropertyCommand command = new NscsPropertyCommand()
        command.setCommandType(commandtype)
        when:
        rbacAuthorizationManager.checkAuthorization(command)
        then:
        notThrown(Exception)
        where:
        commandtype << [
            CREATE_SSH_KEY,
            UPDATE_SSH_KEY,
            DELETE_SSH_KEY,
            IMPORT_NODE_SSH_PRIVATE_KEY
        ]
    }
}
