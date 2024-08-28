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
package com.ericsson.nms.security.nscs.ldap

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.DuplicateNodeNamesException
import com.ericsson.nms.security.nscs.ldap.entities.NodeSpecificLdapConfiguration

import spock.lang.Unroll

class NodeSpecificLdapConfigurationTest extends CdiSpecification {

    private static final String DEFAULT_NODE_FDN_UNDER_TEST = "LTE08DG2ERBS00001"
    private static final String DEFAULT_OTHER_NODE_NAME_UNDER_TEST = "LTE08DG2ERBS00002"
    private static final String DEFAULT_TLS_MODE_UNDER_TEST = "LDAPS"
    private static final String DEFAULT_STARTTLS_MODE_UNDER_TEST = "STARTTLS"
    private static final String DEFAULT_USER_LABEL_UNDER_TEST = "prova"
    private static final String DEFAULT_OTHER_USER_LABEL_UNDER_TEST = "other"
    private static final Boolean USE_TLS_UNDER_TEST = true
    private static final Boolean DO_NOT_USE_TLS_UNDER_TEST = false
    private static final String BASE_DN_UNDER_TEST = "dc=apache,dc=com"
    private static final String PROXY_BIND_UNDER_TEST = "cn=ProxyAccount_4,ou=Profiles,dc=apache,dc=com"
    private static final String PROXY_BIND_PASSWORD_UNDER_TEST = "osz45rph"
    private static final String PRIMARY_SERVER_UNDER_TEST = "10.10.10.10"
    private static final String SECONDARY_SERVER_UNDER_TEST = "10.10.10.20"
    private static final Integer LDAP_SERVER_UNDER_TEST = 1636

    @ObjectUnderTest
    NodeSpecificLdapConfiguration nodeSpecificLdapConfiguration

    NodeSpecificLdapConfiguration nodeSpecificLdapConfigurationToCompare = new NodeSpecificLdapConfiguration()

    private String nodeFdnUnderTest
    private String tlsModeUnderTest
    private String otherTlsModeUnderTest
    private String userLabelUnderTest
    private String otherUserLabelUnderTest
    private Boolean useTlsUnderTest
    private Boolean otherUseTlsUnderTest
    private String baseDNUnderTest
    private String bindDNUnderTest
    private String bindPasswordUnderTest
    private String primaryLdapServerIPAddressUnderTest
    private String secondaryLdapServerIPAddressUnderTest
    private Integer ldapServerPortUnderTest

    private static final int HASH_CODE_NULL_FIELDS = 31 * 31 * 31 * 31 *
    31 * 31 * 31 * 31 * 31 * 31

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {
    }

    def setup() {
        nodeFdnUnderTest = DEFAULT_NODE_FDN_UNDER_TEST
        tlsModeUnderTest = DEFAULT_TLS_MODE_UNDER_TEST
        otherTlsModeUnderTest = DEFAULT_STARTTLS_MODE_UNDER_TEST
        userLabelUnderTest = DEFAULT_USER_LABEL_UNDER_TEST
        otherUserLabelUnderTest = DEFAULT_OTHER_USER_LABEL_UNDER_TEST
        useTlsUnderTest = USE_TLS_UNDER_TEST
        otherUseTlsUnderTest = DO_NOT_USE_TLS_UNDER_TEST
        baseDNUnderTest = BASE_DN_UNDER_TEST
        bindDNUnderTest = PROXY_BIND_UNDER_TEST
        bindPasswordUnderTest = PROXY_BIND_PASSWORD_UNDER_TEST
        primaryLdapServerIPAddressUnderTest = PRIMARY_SERVER_UNDER_TEST
        secondaryLdapServerIPAddressUnderTest = SECONDARY_SERVER_UNDER_TEST
        ldapServerPortUnderTest = LDAP_SERVER_UNDER_TEST
    }

    def "test and set "() {
        given:
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)

        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)

        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)
        when:

        String nodeFdn = nodeSpecificLdapConfiguration.getNodeFdn()
        String tlsMode = nodeSpecificLdapConfiguration.getTlsMode()
        String userLabel = nodeSpecificLdapConfiguration.getUserLabel()
        Boolean useTls = nodeSpecificLdapConfiguration.getUseTls()

        String baseDN = nodeSpecificLdapConfiguration.getBaseDN()
        String bindDN = nodeSpecificLdapConfiguration.getBindDN()
        String bindPassword = nodeSpecificLdapConfiguration.getBindPassword()
        String primaryLdapServerIPAddress = nodeSpecificLdapConfiguration.getPrimaryLdapServerIPAddress()
        String secondaryLdapServerIPAddress = nodeSpecificLdapConfiguration.getSecondaryLdapServerIPAddress()
        Integer ldapServerPort = nodeSpecificLdapConfiguration.getLdapServerPort()

        then:
        nodeFdn == nodeFdnUnderTest && tlsMode == tlsModeUnderTest &&
                userLabel == userLabelUnderTest && useTls == useTlsUnderTest &&
                baseDN == baseDNUnderTest && bindDN == bindDNUnderTest &&
                bindPassword == bindPasswordUnderTest &&
                primaryLdapServerIPAddress == primaryLdapServerIPAddressUnderTest &&
                secondaryLdapServerIPAddress == secondaryLdapServerIPAddressUnderTest &&
                ldapServerPort && ldapServerPortUnderTest
    }

    def 'verify NodeSpecificLdapConfiguration  hashcode when field are not null  '() {
        given:
        int resultUnderTest = HASH_CODE_NULL_FIELDS
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)

        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)

        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        when:
        int result = nodeSpecificLdapConfiguration.hashCode()
        then:
        result != resultUnderTest
    }

    def 'verify NodeSpecificLdapConfiguration  hashcode when field are null  '() {
        given:
        int resultUnderTest = HASH_CODE_NULL_FIELDS

        nodeSpecificLdapConfiguration.setNodeFdn(null)
        nodeSpecificLdapConfiguration.setTlsMode(null)
        nodeSpecificLdapConfiguration.setUserLabel(null)
        nodeSpecificLdapConfiguration.setUseTls(null)

        nodeSpecificLdapConfiguration.setBaseDN(null)
        nodeSpecificLdapConfiguration.setBindDN(null)
        nodeSpecificLdapConfiguration.setBindPassword(null)

        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(null)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(null)
        nodeSpecificLdapConfiguration.setLdapServerPort(null)
        when:
        int result = nodeSpecificLdapConfiguration.hashCode()
        then:
        result == resultUnderTest
    }

    def 'verify NodeSpecificLdapConfiguration are equal '() {
        given:
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfigurationToCompare.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setLdapServerPort(ldapServerPortUnderTest)
        when:
        boolean result = nodeSpecificLdapConfiguration == nodeSpecificLdapConfigurationToCompare
        then:
        result
    }

    def 'verify NodeSpecificLdapConfiguration is equal to itself '() {
        given:
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        when:
        boolean result = nodeSpecificLdapConfiguration == nodeSpecificLdapConfiguration
        then:
        result
    }

    def 'verify NodeSpecificLdapConfiguration is not equal when compare to null object '() {
        given:
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        when:
        def result = nodeSpecificLdapConfiguration.equals(null)
        then:
        !result
    }

    def 'verify NodeSpecificLdapConfiguration is not equal when compare to different class '() {
        given:
        String str = new String()
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        when:
        def result = str.equals(nodeSpecificLdapConfiguration)
        then:
        !result
    }

    def 'verify NodeSpecificLdapConfiguration is not equal with different nodeFdn'() {
        given:
        String nodeFdnCompare = "LTE08DG2ERBS00002"
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(nodeFdnCompare)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfigurationToCompare.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setLdapServerPort(ldapServerPortUnderTest)

        when:
        def result = nodeSpecificLdapConfiguration == nodeSpecificLdapConfigurationToCompare
        then:
        !result
    }

    def 'verify NodeSpecificLdapConfiguration is not equal with different tlsMode'() {
        given:
        String tlsModeCompare = "STARTTLS"
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeCompare)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfigurationToCompare.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setLdapServerPort(ldapServerPortUnderTest)

        when:
        boolean result = nodeSpecificLdapConfiguration == nodeSpecificLdapConfigurationToCompare
        then:
        !result
    }

    def 'verify NodeSpecificLdapConfiguration is not equal with different userLabel'() {
        given:
        String userLabelCompare = "Test2"
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelCompare)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfigurationToCompare.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setLdapServerPort(ldapServerPortUnderTest)

        when:
        boolean result = nodeSpecificLdapConfiguration == nodeSpecificLdapConfigurationToCompare
        then:
        !result
    }

    def 'verify NodeSpecificLdapConfiguration is not equal with different useTls'() {
        given:
        Boolean useTlsCompare = false
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsCompare)
        nodeSpecificLdapConfigurationToCompare.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfigurationToCompare.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setLdapServerPort(ldapServerPortUnderTest)

        when:
        boolean result = nodeSpecificLdapConfiguration == nodeSpecificLdapConfigurationToCompare
        then:
        !result
    }

    def 'verify NodeSpecificLdapConfiguration is not equal with different baseDN'() {
        given:
        String baseDNCompare = "dc=ericsson,dc=com"
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBaseDN(baseDNCompare)
        nodeSpecificLdapConfigurationToCompare.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfigurationToCompare.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setLdapServerPort(ldapServerPortUnderTest)

        when:
        boolean result = nodeSpecificLdapConfiguration == nodeSpecificLdapConfigurationToCompare
        then:
        !result
    }

    def 'verify NodeSpecificLdapConfiguration is not equal with different bindDN'() {
        given:
        String bindDNCompare = "cn=ProxyAccount_2,ou=Profiles,dc=apache,dc=com"
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindDN(bindDNCompare)
        nodeSpecificLdapConfigurationToCompare.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfigurationToCompare.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setLdapServerPort(ldapServerPortUnderTest)

        when:
        boolean result = nodeSpecificLdapConfiguration == nodeSpecificLdapConfigurationToCompare
        then:
        !result
    }

    def 'verify NodeSpecificLdapConfiguration is not equal with different bindPassword'() {
        given:
        String bindPasswordCompare = "apq68idf"
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindPassword(bindPasswordCompare)
        nodeSpecificLdapConfigurationToCompare.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setLdapServerPort(ldapServerPortUnderTest)

        when:
        boolean result = nodeSpecificLdapConfiguration == nodeSpecificLdapConfigurationToCompare
        then:
        !result
    }

    def 'verify NodeSpecificLdapConfiguration is not equal with different primaryLdapServerIPAddress'() {
        given:
        String primaryLdapServerIPAddressCompare = "10.10.10.11"
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfigurationToCompare.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressCompare)
        nodeSpecificLdapConfigurationToCompare.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setLdapServerPort(ldapServerPortUnderTest)

        when:
        boolean result = nodeSpecificLdapConfiguration == nodeSpecificLdapConfigurationToCompare
        then:
        !result
    }

    def 'verify NodeSpecificLdapConfiguration is not equal with different secondaryLdapServerIPAddress'() {
        given:
        String secondaryLdapServerIPAddressCompare = "10.10.10.21"
        nodeSpecificLdapConfiguration.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfiguration.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfiguration.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfiguration.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfiguration.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfiguration.setLdapServerPort(ldapServerPortUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(nodeFdnUnderTest)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBaseDN(baseDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindDN(bindDNUnderTest)
        nodeSpecificLdapConfigurationToCompare.setBindPassword(bindPasswordUnderTest)
        nodeSpecificLdapConfigurationToCompare.setPrimaryLdapServerIPAddress(primaryLdapServerIPAddressUnderTest)
        nodeSpecificLdapConfigurationToCompare.setSecondaryLdapServerIPAddress(secondaryLdapServerIPAddressCompare)
        nodeSpecificLdapConfigurationToCompare.setLdapServerPort(ldapServerPortUnderTest)

        when:
        boolean result = nodeSpecificLdapConfiguration == nodeSpecificLdapConfigurationToCompare
        then:
        !result
    }

    @Unroll
    def 'compare user-defined params for node #nodenameorfdn with itself'() {
        given:
        nodeSpecificLdapConfiguration.setNodeFdn(nodenameorfdn)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        when:
        int compare = nodeSpecificLdapConfiguration.compareUserDefinedLdapParams(nodeSpecificLdapConfiguration)
        then:
        compare == 0
        where:
        nodenameorfdn << [
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST
        ]
    }

    @Unroll
    def 'compare user-defined params for node #nodenameorfdn with other node #othernodenameorfdn'() {
        given:
        nodeSpecificLdapConfiguration.setNodeFdn(nodenameorfdn)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfigurationToCompare.setNodeFdn(othernodenameorfdn)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)
        when:
        int compare = nodeSpecificLdapConfiguration.compareUserDefinedLdapParams(nodeSpecificLdapConfigurationToCompare)
        then:
        noExceptionThrown()
        and:
        compare != 0
        where:
        nodenameorfdn << [
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST,
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST
        ]
        othernodenameorfdn << [
            DEFAULT_OTHER_NODE_NAME_UNDER_TEST,
            DEFAULT_OTHER_NODE_NAME_UNDER_TEST,
            "NetworkElement="+DEFAULT_OTHER_NODE_NAME_UNDER_TEST,
            "NetworkElement="+DEFAULT_OTHER_NODE_NAME_UNDER_TEST
        ]
    }

    @Unroll
    def 'compare user-defined params for node #nodenameorfdn with not conflicting duplicate #samenodenameorfdn'() {
        given:
        nodeSpecificLdapConfiguration.setNodeFdn(nodenameorfdn)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)
        nodeSpecificLdapConfigurationToCompare.setNodeFdn(samenodenameorfdn)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)

        when:
        int compare = nodeSpecificLdapConfiguration.compareUserDefinedLdapParams(nodeSpecificLdapConfigurationToCompare)
        then:
        compare == 0
        where:
        nodenameorfdn << [
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST,
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST
        ]
        samenodenameorfdn << [
            DEFAULT_NODE_FDN_UNDER_TEST,
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST
        ]
    }

    @Unroll
    def 'compare user-defined params for node #nodenameorfdn with conflicting duplicate #samenodenameorfdn due to tls mode'() {
        given:
        nodeSpecificLdapConfiguration.setNodeFdn(nodenameorfdn)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(samenodenameorfdn)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(otherTlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)

        when:
        int compare = nodeSpecificLdapConfiguration.compareUserDefinedLdapParams(nodeSpecificLdapConfigurationToCompare)
        then:
        thrown(DuplicateNodeNamesException.class)
        where:
        nodenameorfdn << [
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST,
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST
        ]
        samenodenameorfdn << [
            DEFAULT_NODE_FDN_UNDER_TEST,
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST
        ]
    }

    @Unroll
    def 'compare user-defined params for node #nodenameorfdn with conflicting duplicate #samenodenameorfdn due to user label'() {
        given:
        nodeSpecificLdapConfiguration.setNodeFdn(nodenameorfdn)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(samenodenameorfdn)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(otherUserLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(useTlsUnderTest)

        when:
        int compare = nodeSpecificLdapConfiguration.compareUserDefinedLdapParams(nodeSpecificLdapConfigurationToCompare)
        then:
        thrown(DuplicateNodeNamesException.class)
        where:
        nodenameorfdn << [
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST,
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST
        ]
        samenodenameorfdn << [
            DEFAULT_NODE_FDN_UNDER_TEST,
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST
        ]
    }

    @Unroll
    def 'compare user-defined params for node #nodenameorfdn with conflicting duplicate #samenodenameorfdn due to use tls'() {
        given:
        nodeSpecificLdapConfiguration.setNodeFdn(nodenameorfdn)
        nodeSpecificLdapConfiguration.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfiguration.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfiguration.setUseTls(useTlsUnderTest)

        nodeSpecificLdapConfigurationToCompare.setNodeFdn(samenodenameorfdn)
        nodeSpecificLdapConfigurationToCompare.setTlsMode(tlsModeUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUserLabel(userLabelUnderTest)
        nodeSpecificLdapConfigurationToCompare.setUseTls(otherUseTlsUnderTest)

        when:
        int compare = nodeSpecificLdapConfiguration.compareUserDefinedLdapParams(nodeSpecificLdapConfigurationToCompare)
        then:
        thrown(DuplicateNodeNamesException.class)
        where:
        nodenameorfdn << [
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST,
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST
        ]
        samenodenameorfdn << [
            DEFAULT_NODE_FDN_UNDER_TEST,
            DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST,
            "NetworkElement="+DEFAULT_NODE_FDN_UNDER_TEST
        ]
    }
}

