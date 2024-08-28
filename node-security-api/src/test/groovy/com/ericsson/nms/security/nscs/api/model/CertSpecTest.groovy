/*--------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *--------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.model

import java.security.InvalidKeyException
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.SecureRandom
import java.security.Security;
import java.security.SignatureException
import java.security.cert.X509Certificate

import javax.security.auth.x500.X500Principal

import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.x509.X509V3CertificateGenerator

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;

import spock.lang.Shared
import spock.lang.Unroll

class CertSpecTest extends CdiSpecification {

    @ObjectUnderTest
    CertSpec certSpec

    @Shared
    X509Certificate x509Certificate

    @Shared
    X509Certificate x509CertificateWithCommaInCN

    def setup() {
        x509Certificate = generateV3CertificateWithCn()
        x509CertificateWithCommaInCN = generateV3CertificateWithCommaInCN()
    }

    def 'Initializing parameterized constructor'() {
        given: 'instantiating x509 certificate with cn value is null'
        X509Certificate x509Certificate = generateV3CertificateWithCnNull()
        when: 'Calling parameterized constructor'
        new CertSpec(x509Certificate)
        then: 'UnexpectedErrorException is thrown'
        thrown(UnexpectedErrorException)
    }

    @Unroll
    def 'Initializing parameterized constructor with commonName in certificate'() {
        given: 'instantiating x509 certificate with correct cn value '
        when: 'Calling parameterized constructor'
        new CertSpec(Certificate)

        then: 'Getting subject/issuer DN values'
        assert expectedResult

        where:
        Certificate                  | expectedResult
        x509Certificate              |  true
        x509CertificateWithCommaInCN |  true
    }

    @Unroll
    def "get commonname from DN with valid DN"() {
        given:
        when: "getting commonname from DN #dn"
        def commonName = certSpec.getCNfromDN(dn)
        then: "CommonName should equal #expected"
        commonName == expected
        where:
        dn                                                  | expected
        "  CN=  NE OAM CA\\  ,  O  =  ERICSSON\\, Inc.  "   | "NE OAM CA "
        "OU=RANI, O=KI, C=SE, CN=NE_OAM_CA"                 | "NE_OAM_CA"
        "CN=NE_OAM_CA,O=ERICSSON"                           | "NE_OAM_CA"
        "CN=NE OAM CA,O=ERICSSON"                           | "NE OAM CA"
        "CN=NE OAM CA,  O  =  ERICSSON  "                   | "NE OAM CA"
        "CN=NE OAM CA,  O  =  ERICSSON\\, Inc.  "           | "NE OAM CA"
    }

    @Unroll
    def "get commonname from DN with insensitiveness values #dn" () {
        given:
        when: "getting commonname from DN #dn"
        def commonName = certSpec.getCNfromDN(dn)
        then: "CommonName should equal #expected"
        commonName == expected
        where:
        dn                | expected
        "CN=myCommonName" | "myCommonName"
        "cn=myCommonName" | "myCommonName"
        "cN=myCommonName" | "myCommonName"
        "Cn=myCommonName" | "myCommonName"
    }

    @Unroll
    def "get commonname from DN having empty or containg only escaped characters #dn" () {
        given:
        when: "getting commonname from DN #dn"
        def commonName = certSpec.getCNfromDN(dn)
        then: "CommonName should equal #expected"
        commonName == expected
        where:
        dn                         | expected
        "CN=\\NE_OAM_CA "          | "NE_OAM_CA"
        "CN=\\,NE_OAM_CA"          | ",NE_OAM_CA"
        "CN=\\,\\NE_OAM_CA "       | ",NE_OAM_CA"
        "CN=\\, \\NE_OAM_CA "      | ", NE_OAM_CA"
        "CN=\\  \\ NE_OAM_CA"      | "   NE_OAM_CA"
        "CN=NE_OAM_CA"             | "NE_OAM_CA"
        "CN= NE_OAM_CA"            | "NE_OAM_CA"
    }

    @Unroll
    def "get CN from DN without having commonName field #dn" () {
        given:
        when: "getting commonname from DN #dn"
        def commonName = certSpec.getCNfromDN(dn)
        then: "UnexpectedErrorException is thrown"
        thrown(UnexpectedErrorException)
        where:
        dn                            | expected
        "OU=Organization_unit"        | null
        "O=Organization"              | null
        "C=IN"                        | null
        "T=title"                     | null
        "DC=myDomainComponent"        | null
        "SURNAME=mySurname"           | null
        "SERIALNUMBER=mySerialNumber" | null
        null                          | null
    }

    @Unroll
    def 'Check fingerprint is the same as calculated using iaik'() {
        given: 'instantiating CertSpec from x509 certificate'
        def CertSpec cert = new CertSpec(x509Cert)
        when: 'getting the CertSpec fingerprint'
        def byte[] fingerPrint = cert.getFingerPrint()
        and: 'getting the certificate fingerprint using iaik'
        def iaik.x509.X509Certificate iaikCert = new iaik.x509.X509Certificate(x509Cert.getEncoded());
        def byte[] iaikFingerPrint = iaikCert.getFingerprint();
        then: 'the two fingerprints should be equal'
        Arrays.equals(fingerPrint, iaikFingerPrint) == expectedResult
        where:
        x509Cert                     | expectedResult
        x509Certificate              |  true
        x509CertificateWithCommaInCN |  true
    }

    private static X509Certificate generateV3CertificateWithCnNull()
    throws InvalidKeyException, NoSuchProviderException, SignatureException, NoSuchAlgorithmException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        final X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(new X500Principal("CN=TestIssuerDN"));
        certGen.setSubjectDN(new X500Principal("O=TestSubDN"));
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 10000));
        kpGen.initialize(1024, new SecureRandom());
        final KeyPair pair = kpGen.generateKeyPair();
        certGen.setPublicKey(pair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
        certGen.addExtension(X509Extensions.SubjectAlternativeName, false,
                new GeneralNames(new GeneralName(GeneralName.rfc822Name, "test@test.test")));
        return certGen.generateX509Certificate(pair.getPrivate(), "BC");
    }

    private static X509Certificate generateV3CertificateWithCn()
    throws InvalidKeyException, NoSuchProviderException, SignatureException, NoSuchAlgorithmException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        final X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(new X500Principal("CN=TestIssuerDN"));
        certGen.setSubjectDN(new X500Principal("CN=TestSubDN"));
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 10000));
        kpGen.initialize(1024, new SecureRandom());
        final KeyPair pair = kpGen.generateKeyPair();
        certGen.setPublicKey(pair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
        certGen.addExtension(X509Extensions.SubjectAlternativeName, false,
                new GeneralNames(new GeneralName(GeneralName.rfc822Name, "test@test.test")));
        return certGen.generateX509Certificate(pair.getPrivate(), "BC");
    }

    private static X509Certificate generateV3CertificateWithCommaInCN()
    throws InvalidKeyException, NoSuchProviderException, SignatureException, NoSuchAlgorithmException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        final X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(new X500Principal("CN=Test\\,IssuerDN"));
        certGen.setSubjectDN(new X500Principal("CN=Test\\,SubDN"));
        final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 10000));
        kpGen.initialize(1024, new SecureRandom());
        final KeyPair pair = kpGen.generateKeyPair();
        certGen.setPublicKey(pair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
        certGen.addExtension(X509Extensions.SubjectAlternativeName, false,
                new GeneralNames(new GeneralName(GeneralName.rfc822Name, "test@test.test")));
        return certGen.generateX509Certificate(pair.getPrivate(), "BC");
    }
}
