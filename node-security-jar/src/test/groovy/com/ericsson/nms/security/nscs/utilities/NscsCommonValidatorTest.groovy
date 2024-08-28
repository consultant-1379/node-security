/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.utilities

import org.apache.commons.validator.routines.InetAddressValidator
import org.junit.Before

import com.ericsson.cds.cdi.support.spock.CdiSpecification

import spock.lang.Shared
import spock.lang.Unroll

class NscsCommonValidatorTest extends CdiSpecification {

    private NscsCommonValidator nscsValidator

    @Shared
    validIPv4Addresses = [
        "0.0.0.0",
        "0.0.0.1",
        "127.0.0.1",
        // 0-9
        "1.2.3.4",
        // 10-99
        "11.1.1.0",
        // 100-199
        "101.1.1.0",
        // 200-249
        "201.1.1.0",
        // 250-255
        "255.255.255.255",
        "192.168.1.1",
        "192.168.1.255",
        "100.100.100.100"
    ]

    @Shared
    invalidIPv4Addresses = [
        // leading 0
        "000.000.000.000",
        // leading 0
        "00.00.00.00",
        // leading 0
        "1.2.3.04",
        // leading 0
        "1.02.03.4",
        // only dot
        ".",
        // only dots
        "..",
        "...",
        // no dot
        "1",
        // 1 dot
        "1.2",
        // 2 dots
        "1.2.3",
        // 4 dots
        "1.2.3.4.5",
        // 4 dots
        "192.168.1.1.1",
        // 256
        "256.1.1.1",
        "1.256.1.1",
        "1.1.256.1",
        "1.1.1.256",
        // -100
        "-100.1.1.1",
        "1.-100.1.1",
        "1.1.-100.1",
        "1.1.1.-100",
        // empty between .
        "1...1",
        "1..1",
        // last dot
        "1.",
        "1.1.",
        "1.1.1.",
        "1.1.1.1.",
        // empty
        "" ,
        // null
        null
    ]

    @Shared
    validIPv6Addresses = [
        // eight segments, each segment from 0000 to ffff (case-insensitive)
        "0000:0000:0000:0000:0000:0000:0000:0000",
        "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff",
        "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF",
        "0:0:0:0:0:0:0:0",
        "1:2:3:4:5:6:7:8",
        // leading zeros in each field can be omitted
        "2001:0db8:85a3:0000:0000:8a2e:0370:7334",
        "2001:db8:85a3:0:0:8a2e:370:7334",
        // square brackets
        "[2001:db8:85a3:0:0:8a2e:370:7334]",
        "[::]",
        // all-zero segments
        // Convention recommends to replace with double colon :: the longest sequence of consecutive all-zero segments
        // in case the address contains multiple occurrences of such sequences, the leftmost one should be compressed
        // but even if the convention is not followed the address can be valid
        "2001:db8:0:0:1:0:0:1",
        "2001:0:0:0:1:0:0:1",
        // following convention
        "2001::1:0:0:1",
        "2001:db8::1:0:0:1",
        // not following convention: the compressed is not the leftmost one but address is valid
        "2001:db8:0:0:1::1",
        "2001:0:0:0:1::1",
        // all all-zero segments
        "0:0:0:0:0:0:0:0",
        // compressed all all-zero segments
        "::",
        // loopback address
        "0:0:0:0:0:0:0:1",
        "::1",
        // initial compressed all-zero segments
        "::2:3:4:5:6:7:8",
        "::3:4:5:6:7:8",
        "::4:5:6:7:8",
        "::5:6:7:8",
        "::6:7:8",
        "::7:8",
        "::8",
        // final compressed all-zero segments
        "1:2:3:4:5:6:7::",
        "1:2:3:4:5:6::",
        "1:2:3:4:5::",
        "1:2:3:4::",
        "1:2:3::",
        "1:2::",
        "1::",
        // double colon used to omit a single all-zero field
        // RFC 5952 requires that a double colon is not to be used to denote an omitted single all-zero field
        // but address is valid
        "::2:3:4:5:6:7:8",
        "1::3:4:5:6:7:8",
        "1:2::4:5:6:7:8",
        "1:2:3::5:6:7:8",
        "1:2:3:4::6:7:8",
        "1:2:3:4:5::7:8",
        "1:2:3:4:5:6::8",
        "1:2:3:4:5:6:7::",
        // IPv4 embedded in IPv6 (last segment can contain a valid IPv4 address)
        "0:0:0:0:0:ffff:192.1.56.10",
        "::ffff:192.1.56.10"
    ]

    @Shared
    invalidIPv6Addresses = [
        // unbalanced square brackets
        "[2001:0db8:85a3:0000:0000:8a2e:0370:7334",
        "2001:0db8:85a3:0000:0000:8a2e:0370:7334]",
        // invalid length
        "[]",
        // invalid IPv6
        "[:]",
        // uri or url
        "[2001:db8::1:0:0:1]:443",
        "[2001:db8::1:0:0:1:]443",
        "[2001:db8::1:0:0:1:]",
        // less than 8 segments
        "1:2:3:4:5:6:7",
        // more than 8 segments
        "1:2:3:4:5:6:7:8:0",
        // each segment shall contain at least one digit
        "1:2:::::7:8",
        // address cannot start or end with a single colon
        ":1:2",
        "1:2:",
        ":2:3:4:5:6:7:8",
        "1:2:3:4:5:6:7:",
        // the compressed two colons notation shall be present only once
        "1::4::8",
        "::4::8",
        "1::4::",
        // multiple consecutive empty segments are not allowed
        "1:::8",
        // IPv4 embedded in dual IPv6 shall be the last segment
        "0:0:0:0:0:192.1.56.10:ffff",
        "1:2:3:4:5:192.1.56.10:6",
        // 8-equivalent segments shall be present for dual IPv6
        "1:2:3:4:5:192.1.56.10",
        "1:2:3:4:5:6:7:192.1.56.10",
        // IPv4 embedded in IPv6 shall be a valid IPv4 address
        "0:0:0:0:0:ffff:1.2.3.04",
        "0:0:0:0:0:ffff:-100.1.1.1",
        // each field shall have a max length of 4 digits
        "2001::1:ffff0:0:1",
        // each field shall be an integer between 0 and 0xffff
        "2001::1:-100:0:1",
        "2001::1:1*2:0:1",
        "2001::1:1+2:0:1",
        // whitespaces are not allowed
        "1 : 2 : 3 : 4 : 5 : 6 : 7 : 8",
        " 1:2:3:4:5:6:7:8",
        "1:2:3:4:5:6:7:8 ",
        ": :1",
        ":: 1",
        // empty
        "" ,
        // null
        null
    ]

    @Shared
    validDomainNames = [
        // hostname is composed of a sequence of labels concatenated with dots
        "domain.tld",
        "subdomain.domain.tld",
        // overall max length is 253
        "ab.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.domain.tld",
        // domain label length is from 1 to 63
        "a.tld",
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.tld",
        // allowed characters for domain labels are a-z | A-Z | 0-9 and hyphen(-)
        "aA-0.tld",
        // domain labels can start with a digit
        "4Z-z.tld",
        // TLD length is from 2 to 6
        "domain-1.ab",
        "domain-1.abcdef",
        // allowed characters for TLD are a-z | A-Z
        "domain-1.gHkY",
        // hostname is case-insensitive
        "Domain-1.TLD"
    ]

    @Shared
    invalidDomainNames = [
            // hostname is composed of a sequence of labels concatenated with dots
            "label",
            "label.",
            ".tld",
            ".",
            "..",
            // overall max length is 253
            "abc.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.subdomain.domain.tld",
            // domain label length is from 1 to 63
            "a..tld",
            "Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.tld",
            // allowed characters for domain labels are a-z | A-Z | 0-9 and hyphen(-)
            "aA-0 .tld",
            "a_A-0.tld",
            // domain labels cannot start with a hyphen(-)
            "-4Z-z.tld",
            // domain labels cannot end with a hyphen(-)
            "4Z-z-.tld",
            // TLD length is from 2 to 6
            "domain-1.a",
            "domain-1.abcdefg",
            // allowed characters for TLD are a-z | A-Z
            "domain-1.tld2",
            "domain-1.4tld",
            "domain-1.-tld",
            "domain-1.tld-",
            // empty
            "" ,
            // null
            null
    ]

    @Shared
    unsupportedInternationalizedDomainNames = [
        // internationalized domain names are not supported
        "清华大学.cn",
        "www.транспорт.com"
    ]


    @Shared
    validRfc822Names = [
            // rfcName is composed of a email
            "user@radio.com",
            // rfcName is case-insensitive
            "User@RAdio.TL",
            "user@radio.abcdef"


    ]

    @Shared
    invalidRfc822Names = [
            // rfcName is composed of a non email
            "label.",
            ".tld",
            ".",
            "..",
            "aA-0 .tld",
            "a_A-0.tld!",
            // empty
            "" ,
            "user@radio.abcdefg",
            // null
            null
    ]

    @Shared
    unsupportedInternationalizedRfc822Names = [
            // internationalized domain names on email not supported
            "yh@清华大学.cn",
            "www@транспорт.com"
    ]

    @Shared
    validDnsNames = [
            // dnsName can be string
            "radio",
            // dnsName can be with domain extension
            "radio.com",
            // dnsName is case-insensitive
            "RAdio.TL",
            // dnsName can have multiple sub domains
            "radio.abcdef.com"


    ]

    @Shared
    invalidDnsNames = [
            //dnsName should not have consecutive period
            "..",
            // empty
            "" ,
            // null
            null
    ]

    @Shared
    unsupportedInternationalizedDnsNames = [
            // internationalized domain names on email not supported
            "清华大学.cn",
            "транспорт.com"
    ]
    @Before
    void before() {
        nscsValidator = NscsCommonValidator.getInstance()
    }

    def "object under test injection" () {
        expect:
        nscsValidator != null
    }

    @Unroll
    def "#ipAddress is a valid IP address" () {
        given:
        expect:
        nscsValidator.isValidIPAddress(ipAddress) == true
        where:
        ipAddress << validIPv4Addresses + validIPv6Addresses
    }

    @Unroll
    def "#ipAddress is an invalid IP address" () {
        given:
        expect:
        nscsValidator.isValidIPAddress(ipAddress) == false
        where:
        ipAddress << invalidIPv4Addresses + invalidIPv6Addresses
    }

    @Unroll
    def "#ipAddress is a valid IPv4 address" () {
        given:
        expect:
        nscsValidator.isValidIPv4Address(ipAddress) == true
        and:
        nscsValidator.isValidIPv6Address(ipAddress) == false
        where:
        ipAddress << validIPv4Addresses
    }

    @Unroll
    def "#ipAddress is an invalid IPv4 address" () {
        given:
        expect:
        nscsValidator.isValidIPv4Address(ipAddress) == false
        where:
        ipAddress << invalidIPv4Addresses + validIPv6Addresses + invalidIPv6Addresses
    }

    @Unroll
    def "#ipAddress is a valid IPv6 address" () {
        given:
        expect:
        nscsValidator.isValidIPv6Address(ipAddress) == true
        and:
        nscsValidator.isValidIPv4Address(ipAddress) == false
        where:
        ipAddress << validIPv6Addresses
    }

    @Unroll
    def "#ipAddress is an invalid IPv6 address" () {
        given:
        expect:
        nscsValidator.isValidIPv6Address(ipAddress) == false
        where:
        ipAddress << invalidIPv6Addresses + validIPv4Addresses + invalidIPv4Addresses
    }

    @Unroll
    def "#ipAddress is valid according to commons-validator" () {
        given:
        expect:
        if (ipAddress != null) {
            nscsValidator.isValidIPAddress(ipAddress) == InetAddressValidator.getInstance().isValid(ipAddress)
        }
        where:
        ipAddress << invalidIPv4Addresses + validIPv6Addresses + invalidIPv6Addresses
    }

    @Unroll
    def "#domainName is a valid domain name" () {
        given:
        expect:
        nscsValidator.isValidDomainHostname(domainName) == true
        where:
        domainName << validDomainNames
    }

    @Unroll
    def "#domainName is an invalid domain name" () {
        given:
        expect:
        nscsValidator.isValidDomainHostname(domainName) == false
        where:
        domainName << invalidDomainNames
    }

    @Unroll
    def "#domainName is an unsupported internationalized domain name" () {
        given:
        expect:
        nscsValidator.isValidDomainHostname(domainName) == false
        where:
        domainName << unsupportedInternationalizedDomainNames
    }

    @Unroll
    def "#rfcName is a valid rfc822 name" () {
        given:
        expect:
        nscsValidator.isValidRFC822Name(rfc822Name) == true
        where:
        rfc822Name << validRfc822Names
    }

    @Unroll
    def "#rfcName is an invalid rfc822 name" () {
        given:
        expect:
        nscsValidator.isValidRFC822Name(rfc822Name) == false
        where:
        rfc822Name << invalidRfc822Names
    }

    @Unroll
    def "#rfcName is an unsupported internationalized rfc822 name" () {
        given:
        expect:
        nscsValidator.isValidRFC822Name(rfc822Name) == false
        where:
        rfc822Name << unsupportedInternationalizedRfc822Names
    }

    @Unroll
    def "#dnsName is a valid dnsName" () {
        given:
        expect:
        nscsValidator.isValidDNSName(dnsName) == true
        where:
        dnsName << validDnsNames
    }

    @Unroll
    def "#dnsName is an invalid dnsName" () {
        given:
        expect:
        nscsValidator.isValidDNSName(dnsName) == false
        where:
        dnsName << invalidDnsNames
    }

    @Unroll
    def "#dnsName is an unsupported internationalized dnsName" () {
        given:
        expect:
        nscsValidator.isValidDNSName(dnsName) == false
        where:
        dnsName << unsupportedInternationalizedDnsNames
    }
}
