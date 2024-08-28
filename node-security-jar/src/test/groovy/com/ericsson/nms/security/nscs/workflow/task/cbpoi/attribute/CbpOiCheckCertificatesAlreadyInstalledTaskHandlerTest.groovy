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

package com.ericsson.nms.security.nscs.workflow.task.cbpoi.attribute

import java.nio.charset.StandardCharsets
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.DataAccessException
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.util.NscsCbpOiTrustedEntityInfo
import com.ericsson.nms.security.nscs.utilities.NscsCbpOiNodeUtility
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiCheckCertificatesAlreadyInstalledTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys
import com.ericsson.oss.services.security.nscs.dps.NscsDpsUtils

import spock.lang.Shared

class CbpOiCheckCertificatesAlreadyInstalledTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    CbpOiCheckCertificatesAlreadyInstalledTaskHandler taskHandler

    @MockedImplementation
    NscsCMReaderService readerService;

    @MockedImplementation
    NscsLogger nscsLogger

    @MockedImplementation
    NscsCapabilityModelService nscsCapabilityModelService;

    @MockedImplementation
    NscsCbpOiNodeUtility nscsCbpOiNodeUtility;

    @MockedImplementation
    NscsDpsUtils nscsDpsUtils;

    @MockedImplementation
    NormalizableNodeReference normNode

    @MockedImplementation
    ManagedObject truststoreMO

    @MockedImplementation
    ManagedObject certificatesMO

    @MockedImplementation
    ManagedObject cmpCaCertificatesMO

    static def certificateBegin = "-----BEGIN CERTIFICATE-----\n";
    static def certificateEnd = "\n-----END CERTIFICATE-----";

    static def enmOamCaCertificatePemString = certificateBegin +
    "MIIEhDCCA2ygAwIBAgIIC5Km6u749ZMwDQYJKoZIhvcNAQELBQAwWDEeMBwGA1UE" +
    "AwwVRU5NX0luZnJhc3RydWN0dXJlX0NBMQswCQYDVQQGEwJTRTEWMBQGA1UECwwN" +
    "QlVDSV9EVUFDX05BTTERMA8GA1UECgwIRVJJQ1NTT04wHhcNMjAwOTI0MTEzMjQy" +
    "WhcNMjgwOTI0MTEwMTQyWjBNMRMwEQYDVQQDDApFTk1fT0FNX0NBMQswCQYDVQQG" +
    "EwJTRTEWMBQGA1UECwwNQlVDSV9EVUFDX05BTTERMA8GA1UECgwIRVJJQ1NTT04w" +
    "ggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCS/YsK8Pc7e8l3z6znLlVX" +
    "rT3mcdnQ+X9n/1flPDFg3q6eEzPD/ZjqIxyp+bJ5bqasm3zkP2F9K3zcB4+f0yJk" +
    "R1u1Qy9MFSeTH94MkQ2Z6oUizl7yYP4uL8ovuYLCW1UyQ79RegC0PhpL3cQ/ZpxJ" +
    "qZHdnuB87zPC5Mj2GGLydkAMnBUZ68iUH0I37YpEreVHeO1dwNOSfGqrXZfIrueJ" +
    "HIksrVuwfenlB9LL5orSw+EtW90uYOjI4sZ659ymI5TF0p+OqDUINbjtD8gi6Kgt" +
    "KfNfyGgAg9KjnuQkjFqpKP5jft2tw+dS+6mc8gwBNyL3ZVGAJVOTliiqw5sRLJOP" +
    "AgMBAAGjggFbMIIBVzCB8wYDVR0fBIHrMIHoMGygaqBohmZodHRwOi8vMTkyLjE2" +
    "OC4wLjE1NTo4MDkyL3BraS1jZHBzP2NhX25hbWU9RU5NX0luZnJhc3RydWN0dXJl" +
    "X0NBJmNhX2NlcnRfc2VyaWFsbnVtYmVyPTY1Njg3YzYzMDYzYWU5MjMweKB2oHSG" +
    "cmh0dHA6Ly9bMjAwMToxYjcwOjgyYTE6MTAzOjoxODFdOjgwOTIvcGtpLWNkcHM/" +
    "Y2FfbmFtZT1FTk1fSW5mcmFzdHJ1Y3R1cmVfQ0EmY2FfY2VydF9zZXJpYWxudW1i" +
    "ZXI9NjU2ODdjNjMwNjNhZTkyMzAdBgNVHQ4EFgQULaYWNZlkq72vBErY+XyfwIYA" +
    "3oYwDwYDVR0TAQH/BAUwAwEB/zAfBgNVHSMEGDAWgBRzB213X2VUnXl5z1LVOfZv" +
    "RKFYbDAOBgNVHQ8BAf8EBAMCAQYwDQYJKoZIhvcNAQELBQADggEBAHF+SshMeYII" +
    "AGiTRVtFr+8WBu12O8g0E4rrxJC/5sNvCri6ZvxCTwKqZBsFAEpLq3Kzcw/1FLBw" +
    "HZi4hetdoCixUw7B1FROouUzTXyz1rkPFl9QxN2lzkcY5oXd0HVyIIDjgHWbH2sM" +
    "2q/pJwwDIZFHpbIR8zncaA3nDM81a6ctF/1aLKMRT0VDp01Wj8p8HLvkp0vUrfW3" +
    "F0x347B+VXXlpjU02vPVa2HBWTEs+sGMOAWE97EGWAzB3eUy7kToA+ACwwuIsYCc" +
    "YeNgX0xHzUhg5raKxqX0zhFCwGgaAEG3RU2nTmkR09D6gZsNWcO4eTvqewCnzdpT" +
    "+ReZuxS+iXM=" + certificateEnd

    static def neOamCaCertificatePemString = certificateBegin +
    "MIIEcTCCA1mgAwIBAgIIDTLy9lXr13cwDQYJKoZIhvcNAQELBQAwUjEYMBYGA1UE" +
    "AwwPRU5NX1BLSV9Sb290X0NBMRYwFAYDVQQLDA1CVUNJX0RVQUNfTkFNMQswCQYD" +
    "VQQGEwJTRTERMA8GA1UECgwIRVJJQ1NTT04wHhcNMjAxMDIxMjMzMjE4WhcNMjgx" +
    "MDIxMjMzMjE4WjBMMRIwEAYDVQQDDAlORV9PQU1fQ0ExFjAUBgNVBAsMDUJVQ0lf" +
    "RFVBQ19OQU0xCzAJBgNVBAYTAlNFMREwDwYDVQQKDAhFUklDU1NPTjCCASIwDQYJ" +
    "KoZIhvcNAQEBBQADggEPADCCAQoCggEBAPJda1J4GK8fo+WOOZ/fLLucbxx87bvg" +
    "i+7gsQLEfi0zRwtPmUTCCRMrAnnHDnAPWSOp8ued7OE4Gmidu2BmCIOSC1S+b3z2" +
    "M7VTaLZ0nqnUTBWZwaNDtVsWQ6SY0sJxUDZdLtxvrTPX8JmeGflScDMWMqUF64UD" +
    "46+jEqi2koD1WNNucdZinyl9RQ1wuZ6UjaYabmf7Zil7YfGDFBDcHNCHJOrC3k1k" +
    "1iQ8/PhbsDVsrAxdjtmiN46G0/81RekLieR98Df136CfATGZjXcVE4F3ZCMRbiSD" +
    "dzORGDL8tnsgvCsxqQb0wXmab+JrQ27nG3dNl5SSE7Qf0vCI14ff2jMCAwEAAaOC" +
    "AU8wggFLMIHnBgNVHR8Egd8wgdwwZqBkoGKGYGh0dHA6Ly8xOTIuMTY4LjAuMTU1" +
    "OjgwOTIvcGtpLWNkcHM/Y2FfbmFtZT1FTk1fUEtJX1Jvb3RfQ0EmY2FfY2VydF9z" +
    "ZXJpYWxudW1iZXI9NTA0ODM0ZDZlZDNmMWExZjByoHCgboZsaHR0cDovL1syMDAx" +
    "OjFiNzA6ODJhMToxMDM6OjE4MV06ODA5Mi9wa2ktY2Rwcz9jYV9uYW1lPUVOTV9Q" +
    "S0lfUm9vdF9DQSZjYV9jZXJ0X3NlcmlhbG51bWJlcj01MDQ4MzRkNmVkM2YxYTFm" +
    "MB0GA1UdDgQWBBSEEOvSER+Aj1Hb8EJ1lbMqx1Xk9TAPBgNVHRMBAf8EBTADAQH/" +
    "MB8GA1UdIwQYMBaAFA2M3hO0tdjyPo0tTPdD5AjdY7BPMA4GA1UdDwEB/wQEAwIB" +
    "BjANBgkqhkiG9w0BAQsFAAOCAQEAiJrj6mV9DUWQob5XMj83Rbf1wQ+hZBsByZug" +
    "DkpwXru2fwCcTtiYrSJQj3vcv+YgLS5sHlX3fZgyE7bWnCZDfJcAnH1J4LUehRI1" +
    "Oa/gUnrnqOQKDXuQUkhFTruF8XPNEr1aN+yoBTHIU+rKZ2lZnYB29QVxrsUobIR7" +
    "yjBXnDc8fQxtDPN8reJI0T5ExEGvMEdgyxY+S4Eb2lMHcbzLaIuc1U/8k6aHL8jc" +
    "DPSAfBvLGkSMFHJTx3cGsLwJbX9oBPTuDUBMUToQoIynp9OMTS+pv/kuyqg2jVuU" +
    "4j9Ph+4hX+7SBGY7vMhbmoQ4YEscY2vy9lLEJUVBiSXSbNF4gg==" + certificateEnd

    static def enrollmentCaName = "NE_OAM_CA"
    static def enmOamCaName = "ENM_OAM_CA"
    static def nodeName = "cloud257-vdu"
    static def oamTrustCategory = "oamTrustCategory"
    static def oamCmpCaTrustCategory = "oamCmpCaTrustCategory"
    static def truststoreFdn = "ManagedElement=" + nodeName + ",truststore=1,"
    def trustNameSuffix = 1
    def defaultTrustNameSuffix = '-' + trustNameSuffix.toString()

    @Shared
    X509Certificate x509enmOamCaInstalledCertificate

    @Shared
    X509Certificate x509neOamCaInstalledCertificate

    @Shared
    NscsCbpOiTrustedEntityInfo oamCmpCaTrustedEntityInfo

    @Shared
    NscsCbpOiTrustedEntityInfo oamTrustedEntityInfo

    @Shared
    def task = new CbpOiCheckCertificatesAlreadyInstalledTask()

    def setup() {
        task.getParameters().clear()
        NodeReference nodeRef = Mock()
        nodeRef.getName() >> nodeName
        readerService.getNormalizableNodeReference(_) >> normNode
        normNode.getFdn() >> nodeName
        task.setNode(nodeRef)
        InputStream targetPkcs7Stream = new ByteArrayInputStream(enmOamCaCertificatePemString.getBytes(StandardCharsets.UTF_8))
        x509enmOamCaInstalledCertificate =
                (X509Certificate) (CertificateFactory.getInstance("X.509").generateCertificate(targetPkcs7Stream))
        oamTrustedEntityInfo = new NscsCbpOiTrustedEntityInfo(enmOamCaName, x509enmOamCaInstalledCertificate.getSerialNumber(),
                x509enmOamCaInstalledCertificate.getIssuerDN().toString(), enmOamCaCertificatePemString)
        oamTrustedEntityInfo.setTrustCategoryName(oamTrustCategory)
        oamTrustedEntityInfo.setTrustCategoryFdn(truststoreFdn + "certificates=" + oamTrustCategory)
        targetPkcs7Stream = new ByteArrayInputStream(neOamCaCertificatePemString.getBytes(StandardCharsets.UTF_8))
        x509neOamCaInstalledCertificate =
                (X509Certificate) (CertificateFactory.getInstance("X.509").generateCertificate(targetPkcs7Stream))
        oamCmpCaTrustedEntityInfo = new NscsCbpOiTrustedEntityInfo(enrollmentCaName, x509neOamCaInstalledCertificate.getSerialNumber(),
                x509neOamCaInstalledCertificate.getIssuerDN().toString(), neOamCaCertificatePemString)
        oamCmpCaTrustedEntityInfo.setTrustCategoryName(oamCmpCaTrustCategory)
        oamCmpCaTrustedEntityInfo.setTrustCategoryFdn(truststoreFdn + "certificates=" + oamCmpCaTrustCategory)
    }

    def 'When task handler receives Trusted Entities list, and no trusts are installed on node, then all Entities are serialized to next task'() {
        given: 'handler receives list with 2 Trusted Entities in output parameters'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject((Set)[
            oamTrustedEntityInfo,
            oamCmpCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        task.setOutputParams(outputParams)

        and: 'no trusts are installed on node'
        nscsDpsUtils.getNodeHierarchyTopMo(normNode, _, "truststore", _) >> truststoreMO
        nscsDpsUtils.getChildMo(truststoreMO, normNode, "certificates", _ as String) >> certificatesMO
        nscsDpsUtils.getChildMos(certificatesMO, normNode, "certificate") >> []

        when: 'task handler is invoked'
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be NOT_ALL_INSTALLED"
        deserializedTaskResult.getResult() == "NOT_ALL_INSTALLED"
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 1
        and: "TRUSTED_CA_ENTITY_LIST output parameter should be unchanged"
        Set<NscsCbpOiTrustedEntityInfo> deserializedTrustEntities = NscsObjectSerializer.readObject(deserializedTaskResult.getOutputParams().get("TRUSTED_CA_ENTITY_LIST"))
        deserializedTrustEntities != null
        deserializedTrustEntities.size() == 2
        List<NscsCbpOiTrustedEntityInfo> trustEntitiesList = new ArrayList<String>();
        trustEntitiesList.addAll(deserializedTrustEntities);
        trustEntitiesList.get(0).getName() == (enmOamCaName + defaultTrustNameSuffix) || trustEntitiesList.get(0).getName() == (enrollmentCaName + defaultTrustNameSuffix)
        trustEntitiesList.get(1).getName() == (enmOamCaName + defaultTrustNameSuffix) || trustEntitiesList.get(1).getName() == (enrollmentCaName + defaultTrustNameSuffix)
    }

    def 'When task handler receives Trusted Entities list, and truststore is not present on node, then all Entities are serialized to next task'() {
        given: 'handler receives list with 2 Trusted Entities in output parameters'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject((Set)[
            oamTrustedEntityInfo,
            oamCmpCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        task.setOutputParams(outputParams)

        and: 'truststore is not present on node'
        nscsDpsUtils.getNodeHierarchyTopMo(normNode, _, "truststore", _) >> null

        when: 'task handler is invoked'
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be NOT_ALL_INSTALLED"
        deserializedTaskResult.getResult() == "NOT_ALL_INSTALLED"
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 1
        and: "TRUSTED_CA_ENTITY_LIST output parameter should be unchanged"
        Set<NscsCbpOiTrustedEntityInfo> deserializedTrustEntities = NscsObjectSerializer.readObject(deserializedTaskResult.getOutputParams().get("TRUSTED_CA_ENTITY_LIST"))
        deserializedTrustEntities != null
        deserializedTrustEntities.size() == 2
        List<NscsCbpOiTrustedEntityInfo> trustEntitiesList = new ArrayList<String>();
        trustEntitiesList.addAll(deserializedTrustEntities);
        trustEntitiesList.get(0).getName() == (enmOamCaName + defaultTrustNameSuffix) || trustEntitiesList.get(0).getName() == (enrollmentCaName + defaultTrustNameSuffix)
        trustEntitiesList.get(1).getName() == (enmOamCaName + defaultTrustNameSuffix) || trustEntitiesList.get(1).getName() == (enrollmentCaName + defaultTrustNameSuffix)
    }

    def 'When task handler receives Trusted Entities list, and trust categories are not present on node, then all Entities are serialized to next task'() {
        given: 'handler receives list with 2 Trusted Entities in output parameters'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject((Set)[
            oamTrustedEntityInfo,
            oamCmpCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        task.setOutputParams(outputParams)

        and: 'trust categories are not present on node'
        nscsDpsUtils.getNodeHierarchyTopMo(normNode, _, "truststore", _) >> truststoreMO
        nscsDpsUtils.getChildMo(truststoreMO, normNode, "certificates", _ as String) >> null

        when: 'task handler is invoked'
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be NOT_ALL_INSTALLED"
        deserializedTaskResult.getResult() == "NOT_ALL_INSTALLED"
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 1
        and: "TRUSTED_CA_ENTITY_LIST output parameter should be unchanged"
        Set<NscsCbpOiTrustedEntityInfo> deserializedTrustEntities = NscsObjectSerializer.readObject(deserializedTaskResult.getOutputParams().get("TRUSTED_CA_ENTITY_LIST"))
        deserializedTrustEntities != null
        deserializedTrustEntities.size() == 2
        List<NscsCbpOiTrustedEntityInfo> trustEntitiesList = new ArrayList<String>();
        trustEntitiesList.addAll(deserializedTrustEntities);
        trustEntitiesList.get(0).getName() == (enmOamCaName + defaultTrustNameSuffix) || trustEntitiesList.get(0).getName() == (enrollmentCaName + defaultTrustNameSuffix)
        trustEntitiesList.get(1).getName() == (enmOamCaName + defaultTrustNameSuffix) || trustEntitiesList.get(1).getName() == (enrollmentCaName + defaultTrustNameSuffix)
    }

    def 'When task handler receives Trusted Entities, and get installed trusts throws exception, then workflow ends with Unexpected Error'() {
        given: 'handler receives Trusted Entities list in output parameters'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject((Set)[oamTrustedEntityInfo])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        task.setOutputParams(outputParams)

        and: 'get of already installed trusts throws exception'
        nscsDpsUtils.getNodeHierarchyTopMo(normNode, _, "truststore", _) >> truststoreMO
        nscsDpsUtils.getChildMo(truststoreMO, normNode, "certificates", _ as String) >> certificatesMO
        nscsDpsUtils.getChildMos(certificatesMO, normNode, "certificate") >> {throw new DataAccessException("DPS access error")}

        when: 'task handler is invoked'
        taskHandler.processTask(task)

        then: 'DataAccessException is thrown by handler'
        thrown(DataAccessException)
    }

    def 'When task handler receives Trusted Entities list, and trusts are installed on node but cannot be converted, then all Entities are serialized to next task'() {
        given: 'handler receives list with 2 Trusted Entities in output parameters'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject((Set)[
            oamTrustedEntityInfo,
            oamCmpCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        task.setOutputParams(outputParams)

        and: 'no trusts are installed on node'
        nscsDpsUtils.getNodeHierarchyTopMo(normNode, _, "truststore", _) >> truststoreMO
        nscsDpsUtils.getChildMo(truststoreMO, normNode, "certificates", _ as String) >> certificatesMO
        ManagedObject certificateMO = Mock()
        certificateMO.getAttribute("cert") >> enmOamCaCertificatePemString
        certificateMO.getAttribute("name") >> enmOamCaName
        nscsCbpOiNodeUtility.convertToX509Cert(_ as String) >> {throw new CertificateException()}
        nscsDpsUtils.getChildMos(certificatesMO, normNode, "certificate") >> [certificateMO]

        when: 'task handler is invoked'
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be NOT_ALL_INSTALLED"
        deserializedTaskResult.getResult() == "NOT_ALL_INSTALLED"
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 1
        and: "TRUSTED_CA_ENTITY_LIST output parameter should be unchanged"
        Set<NscsCbpOiTrustedEntityInfo> deserializedTrustEntities = NscsObjectSerializer.readObject(deserializedTaskResult.getOutputParams().get("TRUSTED_CA_ENTITY_LIST"))
        deserializedTrustEntities != null
        deserializedTrustEntities.size() == 2
        List<NscsCbpOiTrustedEntityInfo> trustEntitiesList = new ArrayList<String>();
        trustEntitiesList.addAll(deserializedTrustEntities);
        trustEntitiesList.get(0).getName() == (enmOamCaName + defaultTrustNameSuffix) || trustEntitiesList.get(0).getName() == (enrollmentCaName + defaultTrustNameSuffix)
        trustEntitiesList.get(1).getName() == (enmOamCaName + defaultTrustNameSuffix) || trustEntitiesList.get(1).getName() == (enrollmentCaName + defaultTrustNameSuffix)
    }

    def 'When task handler receives Trusted Entities list, and one trust is installed on node, then only uninstalled Entities are serialized to next task'() {
        given: 'handler receives two Trusted Entities in output parameters'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject((Set)[
            oamTrustedEntityInfo,
            oamCmpCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        task.setOutputParams(outputParams)

        and: 'one trust certificate is already installed on node'
        nscsDpsUtils.getNodeHierarchyTopMo(normNode, _, "truststore", _) >> truststoreMO
        nscsDpsUtils.getChildMo(truststoreMO, normNode, "certificates", oamTrustCategory) >> certificatesMO
        ManagedObject certificateMO = Mock()
        certificateMO.getAttribute("cert") >> enmOamCaCertificatePemString
        certificateMO.getAttribute("name") >> enmOamCaName
        nscsCbpOiNodeUtility.convertToX509Cert(_ as String) >> x509enmOamCaInstalledCertificate
        nscsDpsUtils.getChildMos(certificatesMO, normNode, "certificate") >> [certificateMO]
        nscsDpsUtils.getChildMo(truststoreMO, normNode, "certificates", oamCmpCaTrustCategory) >> cmpCaCertificatesMO
        nscsDpsUtils.getChildMos(cmpCaCertificatesMO, normNode, "certificate") >> []

        when: 'task handler is invoked'
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be NOT_ALL_INSTALLED"
        deserializedTaskResult.getResult() == "NOT_ALL_INSTALLED"
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 1
        and: "TRUSTED_CA_ENTITY_LIST output parameter should be unchanged"
        Set<NscsCbpOiTrustedEntityInfo> deserializedTrustEntities = NscsObjectSerializer.readObject(deserializedTaskResult.getOutputParams().get("TRUSTED_CA_ENTITY_LIST"))
        deserializedTrustEntities != null
        deserializedTrustEntities.size() == 1
        List<NscsCbpOiTrustedEntityInfo> trustEntitiesList = new ArrayList<String>();
        trustEntitiesList.addAll(deserializedTrustEntities);
        trustEntitiesList.get(0).getTrustCategoryName() == oamCmpCaTrustCategory
        trustEntitiesList.get(0).getName() == enrollmentCaName + defaultTrustNameSuffix
    }

    def 'When task handler receives Trusted Entities list, and all trusts are already installed, then no Entity is serialized to next task'() {
        given: 'handler receives two Trusted Entities in output parameters'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject((Set)[
            oamTrustedEntityInfo,
            oamCmpCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        task.setOutputParams(outputParams)

        and: 'both trust certificate are already installed on node'
        nscsDpsUtils.getNodeHierarchyTopMo(normNode, _, "truststore", _) >> truststoreMO
        nscsDpsUtils.getChildMo(truststoreMO, normNode, "certificates", _) >> certificatesMO
        ManagedObject certificateMO = Mock()
        certificateMO.getAttribute("cert") >>> [
            enmOamCaCertificatePemString,
            neOamCaCertificatePemString
        ]
        certificateMO.getAttribute("name") >>> [
            enmOamCaName + defaultTrustNameSuffix,
            enrollmentCaName + defaultTrustNameSuffix
        ]
        nscsDpsUtils.getChildMos(certificatesMO, normNode, "certificate") >> [certificateMO]
        nscsCbpOiNodeUtility.convertToX509Cert(_ as String) >>> [
            x509enmOamCaInstalledCertificate,
            x509neOamCaInstalledCertificate
        ]

        when: 'task handler is invoked'
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be INSTALLED"
        deserializedTaskResult.getResult() == "INSTALLED"
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 0
    }

    def 'When one trust certificate has duplicated name, then the name is updated with incremental numeric suffix'() {
        given: 'handler receives two Trusted Entities in output parameters'
        def trustedEntitiesInfoStr = NscsObjectSerializer.writeObject((Set)[
            oamTrustedEntityInfo,
            oamCmpCaTrustedEntityInfo
        ])
        def outputParams = [(WorkflowOutputParameterKeys.TRUSTED_CA_ENTITY_LIST.toString()) : trustedEntitiesInfoStr]
        task.setOutputParams(outputParams)

        and: 'one received certificate has same name but different S/N than trust already installed on node'
        nscsDpsUtils.getNodeHierarchyTopMo(normNode, _, "truststore", _) >> truststoreMO
        nscsDpsUtils.getChildMo(truststoreMO, normNode, "certificates", _) >> certificatesMO
        ManagedObject certificateMO = Mock()
        certificateMO.getAttribute("cert") >>> [
            enmOamCaCertificatePemString,
            neOamCaCertificatePemString
        ]
        certificateMO.getAttribute("name") >>> [
            enmOamCaName + defaultTrustNameSuffix,
            enrollmentCaName + defaultTrustNameSuffix
        ]
        nscsDpsUtils.getChildMos(certificatesMO, normNode, "certificate") >> [certificateMO]
        X509Certificate x509oamCaModSerNumCertificate = Mock()
        x509oamCaModSerNumCertificate.getSerialNumber() >> (x509enmOamCaInstalledCertificate.getSerialNumber() + 4)
        x509oamCaModSerNumCertificate.getIssuerDN() >> x509enmOamCaInstalledCertificate.getIssuerDN()
        x509oamCaModSerNumCertificate.getSubjectDN() >> x509enmOamCaInstalledCertificate.getSubjectDN()
        nscsCbpOiNodeUtility.convertToX509Cert(_ as String) >>> [
            x509oamCaModSerNumCertificate,
            x509neOamCaInstalledCertificate
        ]

        when: 'task handler is invoked'
        def result = taskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result);

        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be NOT_ALL_INSTALLED"
        deserializedTaskResult.getResult() == "NOT_ALL_INSTALLED"
        and: "output parameters should have been changed"
        deserializedTaskResult.getOutputParams().size() == 1
        and: "TRUSTED_CA_ENTITY_LIST output parameter should be unchanged"
        Set<NscsCbpOiTrustedEntityInfo> deserializedTrustEntities = NscsObjectSerializer.readObject(deserializedTaskResult.getOutputParams().get("TRUSTED_CA_ENTITY_LIST"))
        deserializedTrustEntities != null
        deserializedTrustEntities.size() == 1
        List<NscsCbpOiTrustedEntityInfo> trustEntitiesList = new ArrayList<String>();
        trustEntitiesList.addAll(deserializedTrustEntities);
        trustEntitiesList.get(0).getTrustCategoryName() == oamTrustCategory
        trustEntitiesList.get(0).getName() == (enmOamCaName + '-' + (trustNameSuffix + 1).toString())
    }

    def 'When no output params are received, then exception is thrown' () {
        given: 'no output params are received'
        task.getOutputParams() >> null
        when: 'task handler is invoked'
        taskHandler.processTask(task)
        then: 'exception is thrown'
        thrown(WorkflowTaskException)
    }
}
