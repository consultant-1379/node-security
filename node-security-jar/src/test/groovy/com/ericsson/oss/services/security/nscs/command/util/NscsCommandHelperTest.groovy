/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.command.util

import com.ericsson.cds.cdi.support.spock.CdiSpecification

import spock.lang.Unroll

class NscsCommandHelperTest extends CdiSpecification {

    def 'constructor is not allowed'() {
        given:
        when:
        new NscsCommandHelper()
        then:
        thrown(IllegalStateException.class)
    }

    def 'obfuscate null command text'() {
        given:
        def String commandText = null
        when:
        def obfuscatedCommandText = NscsCommandHelper.obfuscateCommandText(commandText)
        then:
        obfuscatedCommandText == null
    }

    @Unroll
    def 'obfuscate command text #text with syntax error'() {
        given:
        def String commandText = text
        when:
        def obfuscatedCommandText = NscsCommandHelper.obfuscateCommandText(commandText)
        then:
        obfuscatedCommandText == expectedtext
        where:
        text << [
            "secadm crdentials create --secureusername netsim --secureuserpassword netsim --nodelist NODE1,NODE2",
            "secadm credentials crete --secureusername netsim --secureuserpassword netsim --nodelist NODE1,NODE2",
            "secadm credentialscreate --secureusername netsim --secureuserpassword netsim --nodelist NODE1,NODE2",
            "secadm credentials create --secureusername netsim -secureuserpassword netsim --nodelist NODE1,NODE2",
            "secadm credentials create --secureusername netsim --secreuserpassword netsim --nodelist NODE1,NODE2"
        ]
        expectedtext << [
            "secadm crdentials create --secureusername netsim --secureuserpassword netsim --nodelist NODE1,NODE2",
            "secadm credentials crete --secureusername netsim --secureuserpassword netsim --nodelist NODE1,NODE2",
            "secadm credentialscreate --secureusername netsim --secureuserpassword netsim --nodelist NODE1,NODE2",
            "secadm credentials create --secureusername ******* -secureuserpassword netsim --nodelist NODE1,NODE2",
            "secadm credentials create --secureusername ******* --secreuserpassword netsim --nodelist NODE1,NODE2"
        ]
    }

    @Unroll
    def 'obfuscate command text #text'() {
        given:
        def String commandText = text
        when:
        def obfuscatedCommandText = NscsCommandHelper.obfuscateCommandText(commandText)
        then:
        obfuscatedCommandText == expectedtext
        where:
        text << [
            "",
            "secadm",
            "secadm credentials create",
            "secadm credentials create --secureusername",
            "secadm credentials create --secureusername ",
            "secadm credentials create --secureusername netsim --secureuserpassword netsim --rootusername netsim --rootuserpassword netsim --normalusername netsim --normaluserpassword netsim --nodecliusername netsim --nodecliuserpassword netsim --nodelist NODE1,NODE2",
            "secadm credentials create -sn netsim -sp netsim -rn netsim -rp netsim -nn netsim -np netsim -ncn netsim -ncp netsim --nodelist NODE1,NODE2",
            "secadm credentials create --secureusername netsim --secureuserpassword netsim --nwieasecureusername netsim --nwieasecureuserpassword netsim --nwiebsecureusername netsim --nwiebsecureuserpassword netsim --nodecliusername netsim --nodecliuserpassword netsim --nodelist NODE1,NODE2",
            "secadm credentials create -sn netsim -sp netsim -nasn netsim -nasp netsim -nbsn netsim -nbsp netsim -ncn netsim -ncp netsim --nodelist NODE1,NODE2",
            "secadm creds create --secureusername netsim --secureuserpassword netsim --rootusername netsim --rootuserpassword netsim --normalusername netsim --normaluserpassword netsim --nodecliusername netsim --nodecliuserpassword netsim --nodelist NODE1,NODE2",
            "secadm creds create -sn netsim -sp netsim -rn netsim -rp netsim -nn netsim -np netsim -ncn netsim -ncp netsim --nodelist NODE1,NODE2",
            "secadm creds create --secureusername netsim --secureuserpassword netsim --nwieasecureusername netsim --nwieasecureuserpassword netsim --nwiebsecureusername netsim --nwiebsecureuserpassword netsim --nodecliusername netsim --nodecliuserpassword netsim --nodelist NODE1,NODE2",
            "secadm creds create -sn netsim -sp netsim -nasn netsim -nasp netsim -nbsn netsim -nbsp netsim -ncn netsim -ncp netsim --nodelist NODE1,NODE2",
            "secadm credentials update --secureusername netsim --secureuserpassword netsim --rootusername netsim --rootuserpassword netsim --normalusername netsim --normaluserpassword netsim --nodecliusername netsim --nodecliuserpassword netsim --nodelist NODE1,NODE2",
            "secadm credentials update -sn netsim -sp netsim -rn netsim -rp netsim -nn netsim -np netsim -ncn netsim -ncp netsim --nodelist NODE1,NODE2",
            "secadm credentials update --secureusername netsim --secureuserpassword netsim --nwieasecureusername netsim --nwieasecureuserpassword netsim --nwiebsecureusername netsim --nwiebsecureuserpassword netsim --nodecliusername netsim --nodecliuserpassword netsim --nodelist NODE1,NODE2",
            "secadm credentials update -sn netsim -sp netsim -nasn netsim -nasp netsim -nbsn netsim -nbsp netsim -ncn netsim -ncp netsim --nodelist NODE1,NODE2",
            "secadm creds update --secureusername netsim --secureuserpassword netsim --rootusername netsim --rootuserpassword netsim --normalusername netsim --normaluserpassword netsim --nodecliusername netsim --nodecliuserpassword netsim --nodelist NODE1,NODE2",
            "secadm creds update -sn netsim -sp netsim -rn netsim -rp netsim -nn netsim -np netsim -ncn netsim -ncp netsim --nodelist NODE1,NODE2",
            "secadm creds update --secureusername netsim --secureuserpassword netsim --nwieasecureusername netsim --nwieasecureuserpassword netsim --nwiebsecureusername netsim --nwiebsecureuserpassword netsim --nodecliusername netsim --nodecliuserpassword netsim --nodelist NODE1,NODE2",
            "secadm creds update -sn netsim -sp netsim -nasn netsim -nasp netsim -nbsn netsim -nbsp netsim -ncn netsim -ncp netsim --nodelist NODE1,NODE2",
            "secadm snmp authnopriv --auth_algo ALGO --auth_password netsim --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authnopriv --auth_algo ALGO -ap netsim --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authnopriv --auth_algo ALGO --priv_password netsim --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authnopriv --auth_algo ALGO -pp netsim --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authpriv --auth_algo ALGO --auth_password netsim --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authpriv --auth_algo ALGO -ap netsim --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authpriv --auth_algo ALGO --priv_password netsim --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authpriv --auth_algo ALGO -pp netsim --nodelist --nodelist NODE1,NODE2"
        ]
        expectedtext << [
            "",
            "secadm",
            "secadm credentials create",
            "secadm credentials create --secureusername",
            "secadm credentials create --secureusername ",
            "secadm credentials create --secureusername ******* --secureuserpassword ******* --rootusername ******* --rootuserpassword ******* --normalusername ******* --normaluserpassword ******* --nodecliusername ******* --nodecliuserpassword ******* --nodelist NODE1,NODE2",
            "secadm credentials create -sn ******* -sp ******* -rn ******* -rp ******* -nn ******* -np ******* -ncn ******* -ncp ******* --nodelist NODE1,NODE2",
            "secadm credentials create --secureusername ******* --secureuserpassword ******* --nwieasecureusername ******* --nwieasecureuserpassword ******* --nwiebsecureusername ******* --nwiebsecureuserpassword ******* --nodecliusername ******* --nodecliuserpassword ******* --nodelist NODE1,NODE2",
            "secadm credentials create -sn ******* -sp ******* -nasn ******* -nasp ******* -nbsn ******* -nbsp ******* -ncn ******* -ncp ******* --nodelist NODE1,NODE2",
            "secadm creds create --secureusername ******* --secureuserpassword ******* --rootusername ******* --rootuserpassword ******* --normalusername ******* --normaluserpassword ******* --nodecliusername ******* --nodecliuserpassword ******* --nodelist NODE1,NODE2",
            "secadm creds create -sn ******* -sp ******* -rn ******* -rp ******* -nn ******* -np ******* -ncn ******* -ncp ******* --nodelist NODE1,NODE2",
            "secadm creds create --secureusername ******* --secureuserpassword ******* --nwieasecureusername ******* --nwieasecureuserpassword ******* --nwiebsecureusername ******* --nwiebsecureuserpassword ******* --nodecliusername ******* --nodecliuserpassword ******* --nodelist NODE1,NODE2",
            "secadm creds create -sn ******* -sp ******* -nasn ******* -nasp ******* -nbsn ******* -nbsp ******* -ncn ******* -ncp ******* --nodelist NODE1,NODE2",
            "secadm credentials update --secureusername ******* --secureuserpassword ******* --rootusername ******* --rootuserpassword ******* --normalusername ******* --normaluserpassword ******* --nodecliusername ******* --nodecliuserpassword ******* --nodelist NODE1,NODE2",
            "secadm credentials update -sn ******* -sp ******* -rn ******* -rp ******* -nn ******* -np ******* -ncn ******* -ncp ******* --nodelist NODE1,NODE2",
            "secadm credentials update --secureusername ******* --secureuserpassword ******* --nwieasecureusername ******* --nwieasecureuserpassword ******* --nwiebsecureusername ******* --nwiebsecureuserpassword ******* --nodecliusername ******* --nodecliuserpassword ******* --nodelist NODE1,NODE2",
            "secadm credentials update -sn ******* -sp ******* -nasn ******* -nasp ******* -nbsn ******* -nbsp ******* -ncn ******* -ncp ******* --nodelist NODE1,NODE2",
            "secadm creds update --secureusername ******* --secureuserpassword ******* --rootusername ******* --rootuserpassword ******* --normalusername ******* --normaluserpassword ******* --nodecliusername ******* --nodecliuserpassword ******* --nodelist NODE1,NODE2",
            "secadm creds update -sn ******* -sp ******* -rn ******* -rp ******* -nn ******* -np ******* -ncn ******* -ncp ******* --nodelist NODE1,NODE2",
            "secadm creds update --secureusername ******* --secureuserpassword ******* --nwieasecureusername ******* --nwieasecureuserpassword ******* --nwiebsecureusername ******* --nwiebsecureuserpassword ******* --nodecliusername ******* --nodecliuserpassword ******* --nodelist NODE1,NODE2",
            "secadm creds update -sn ******* -sp ******* -nasn ******* -nasp ******* -nbsn ******* -nbsp ******* -ncn ******* -ncp ******* --nodelist NODE1,NODE2",
            "secadm snmp authnopriv --auth_algo ALGO --auth_password ******* --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authnopriv --auth_algo ALGO -ap ******* --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authnopriv --auth_algo ALGO --priv_password ******* --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authnopriv --auth_algo ALGO -pp ******* --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authpriv --auth_algo ALGO --auth_password ******* --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authpriv --auth_algo ALGO -ap ******* --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authpriv --auth_algo ALGO --priv_password ******* --nodelist --nodelist NODE1,NODE2",
            "secadm snmp authpriv --auth_algo ALGO -pp ******* --nodelist --nodelist NODE1,NODE2"
        ]
    }
}
