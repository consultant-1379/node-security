package com.ericsson.nms.security.nscs.enrollmentinfo.response.model

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification

class CertificateRevocationsTest extends CdiSpecification {
    @ObjectUnderTest
    private CertificateRevocations certificateRevocations

    private String crlsUri = "http://192.168.0.155:8092/pki-cdps?ca_name=ENM_OAM_CA&amp;ca_cert_serialnumber=601e88c1a6b4122"

    def 'set and get certificate revocations object'() {
        given:
        List<CertificateRevocation> certificateRevocationsLst = new ArrayList<>()

        CertificateRevocation certificateRevocation = new CertificateRevocation()
        certificateRevocation.setCrlName("ipva4_crl")
        certificateRevocation.setCrlUri(crlsUri)
        certificateRevocationsLst.add(certificateRevocation)

        certificateRevocations.setCertificateRevocations(certificateRevocationsLst)
        when:
        List<CertificateRevocation> certificateRevocationsRead = certificateRevocations.getCertificateRevocations()
        def crlNameRead = certificateRevocationsRead.get(0).getCrlName()
        def crlUriRead  = certificateRevocationsRead.get(0).getCrlUri()
        then:
        crlNameRead == "ipva4_crl" && crlUriRead == crlsUri
    }
}
