/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cbpoi.attribute

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute.WorkflowQueryTaskResult
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiCheckRemoveTrustTask
import com.ericsson.oss.services.security.nscs.utils.CbpOiNodeDataSetup

import spock.lang.Unroll

class CbpOiCheckRemoveTrustTaskHandlerTest extends CbpOiNodeDataSetup {

    @ObjectUnderTest
    private CbpOiCheckRemoveTrustTaskHandler cbpOiCheckRemoveTrustTaskHandler

    @MockedImplementation
    private NscsCapabilityModelService nscsCapabilityModelService

    private nodeName = "5G116vDU001"
    private ENM_PKI_ROOT_CA_CERT = "MIIDmQYJKoZIhvcNAQcCoIIDijCCA4YCAQExADALBgkqhkiG9w0BBwGgggNsMIID aDCCAlCgAwIBAgIIdwglEwEAf6UwDQYJKoZIhvcNAQELBQAwUjEYMBYGA1UEAwwP RU5NX1BLSV9Sb290X0NBMQswCQYDVQQGEwJTRTERMA8GA1UECgwIRVJJQ1NTT04x FjAUBgNVBAsMDUJVQ0lfRFVBQ19OQU0wHhcNMjExMTE1MDEzODU0WhcNMzExMTE1 MDEzODU0WjBSMRgwFgYDVQQDDA9FTk1fUEtJX1Jvb3RfQ0ExCzAJBgNVBAYTAlNF MREwDwYDVQQKDAhFUklDU1NPTjEWMBQGA1UECwwNQlVDSV9EVUFDX05BTTCCASIw DQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAIMV+vLjCMcDssovSBwj+2XiUgpC Dj6Evvvsw03ruKl60TLbBlJqQCfrnP7dtPIz4Eu7Fhw35bd4F+R4rFGXPj/OUGAz M+thv+DKZ1l425xF0ZMRqbXVVmBcdVBBfOfXp8qTVcCb2R0hP9yAuf5DNWToSZd3 yZRGl1lidwI2LH6r3pT1ihTHRrgyFfroRYV1necWV6WOnMUN7YpJJ7S4eSzLRRLk UDSoLJooo4ig6TNydDTpRo+nTzCi+C42WUIPJbr5eYhAdjQ1Q1dP0G5VKW/kAwZf fqprCQQLcGC5+5JgL8kU6zJ53eZIM+y2I/ESCr3OaNqPTnKjZBIwGieTNTECAwEA AaNCMEAwHQYDVR0OBBYEFCGUrdO9KqnTmWCnS6iHhkd95glQMA8GA1UdEwEB/wQF MAMBAf8wDgYDVR0PAQH/BAQDAgEGMA0GCSqGSIb3DQEBCwUAA4IBAQAjmsQVwGap CzQVTc9UW9ChC+Ixl+pE3OaoCEgewBWeoNJSW07IyOd5jC000thxbctWsTBl5xEz rRGI2cqxw6M3GIExOFk4A30O2xGqeYIPMH17UkGEkSs0H1netK6OwknbJiTwrSYe DthF74w33E1ICsDdAv1hdbGoMkFC2KplF61Gwpgm64coelsdgstV33ztvh1/mOUQ PyCZxW1atGYukwmKg/WUPy1BjvzUQzlQ/G2lGT12ewO064iQczB1WMTZJPub1bUa vYXMNmGGwdNAUdLbUvY86QW8pP2DelSJHXF9n1xCIHeeATTm91LTPhn3a/VjZjMH ojKWVkjk1ik9oQAxAA=="
    private INVALID_ENM_PKI_ROOT_CA_CERT = "MIIDmQYJKo"
    private ENM_PKI_ROOT_CA_SN = "8577146253878525861"
    private ENM_PKI_ROOT_CA_ISSUER = "OU=BUCI_DUAC_NAM, O=ERICSSON, C=SE, CN=ENM_PKI_Root_CA"

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
        nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(_) >> ["OAM":"oamTrustCategory", "IPSEC":"ipsecTrustCategory"]
        nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(_) >> ["OAM":"oamCmpCaTrustCategory", "IPSEC":"ipsecCmpCaTrustCategory"]
    }

    def 'object under test injection' () {
        expect:
        cbpOiCheckRemoveTrustTaskHandler != null
    }

    def 'process task executed with success with MeContext and ManagedElement and with certificate under OAM trust category' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with truststore under ManagedElement"
        createTruststoreUnderManagedElement()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        and: "with certificate MOs under the OAM trust category"
        createCertificateUnderTruststoreCertificates("oamTrustCategory", "ENM_PKI_Root_CA", ENM_PKI_ROOT_CA_CERT)
        and: "task for node"
        def task = new CbpOiCheckRemoveTrustTask(nodeName)
        and: "task with certificate type OAM"
        task.setCertCategory("CORBA_PEERS")
        and: "task with expected SN and issuer"
        task.setCertificateSerialNumber(ENM_PKI_ROOT_CA_SN)
        task.setIssuer(ENM_PKI_ROOT_CA_ISSUER)
        when:
        def result = cbpOiCheckRemoveTrustTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be REMOVE"
        deserializedTaskResult.getResult() == "REMOVE"
        and: "output parameters should be added with the TRUSTED_CERTIFICATE_FDN correctly set to the list of FDN of the to be removed certificates"
        deserializedTaskResult.getOutputParams().size() == 1
        def String serializedToBeRemovedTrustedCertificateFdns = deserializedTaskResult.getOutputParams().get("TRUSTED_CERTIFICATE_FDN")
        def List<String> toBeRemovedTrustedCertificateFdns = NscsObjectSerializer.readObject(serializedToBeRemovedTrustedCertificateFdns)
        toBeRemovedTrustedCertificateFdns.size() == 1
        toBeRemovedTrustedCertificateFdns.contains("MeContext="+nodeName+",ManagedElement="+nodeName+",truststore=1,certificates=oamTrustCategory,certificate=ENM_PKI_Root_CA")
    }

    def 'process task executed with success with MeContext and ManagedElement and with certificate under OAM and CMP CA trust categories' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with ManagedElement under MeContext"
        createManagedElementUnderMeContext(VDU_TARGET_TYPE, nodeName)
        and: "with truststore under ManagedElement"
        createTruststoreUnderManagedElement()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        and: "with certificate MOs under the OAM trust category"
        createCertificateUnderTruststoreCertificates("oamTrustCategory", "ENM_PKI_Root_CA", ENM_PKI_ROOT_CA_CERT)
        and: "with OAM CMP CA trust category under truststore"
        createCertificatesUnderTruststore("oamCmpCaTrustCategory")
        and: "with certificate MOs under the OAM CMP CA trust category"
        createCertificateUnderTruststoreCertificates("oamCmpCaTrustCategory", "ENM_PKI_Root_CA", ENM_PKI_ROOT_CA_CERT)
        and: "task for node"
        def task = new CbpOiCheckRemoveTrustTask(nodeName)
        and: "task with certificate type OAM"
        task.setCertCategory("CORBA_PEERS")
        and: "task with expected SN and issuer"
        task.setCertificateSerialNumber(ENM_PKI_ROOT_CA_SN)
        task.setIssuer(ENM_PKI_ROOT_CA_ISSUER)
        when:
        def result = cbpOiCheckRemoveTrustTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be REMOVE"
        deserializedTaskResult.getResult() == "REMOVE"
        and: "output parameters should be added with the TRUSTED_CERTIFICATE_FDN correctly set to the list of FDN of the to be removed certificates"
        deserializedTaskResult.getOutputParams().size() == 1
        def String serializedToBeRemovedTrustedCertificateFdns = deserializedTaskResult.getOutputParams().get("TRUSTED_CERTIFICATE_FDN")
        def List<String> toBeRemovedTrustedCertificateFdns = NscsObjectSerializer.readObject(serializedToBeRemovedTrustedCertificateFdns)
        toBeRemovedTrustedCertificateFdns.contains("MeContext="+nodeName+",ManagedElement="+nodeName+",truststore=1,certificates=oamTrustCategory,certificate=ENM_PKI_Root_CA")
        toBeRemovedTrustedCertificateFdns.contains("MeContext="+nodeName+",ManagedElement="+nodeName+",truststore=1,certificates=oamCmpCaTrustCategory,certificate=ENM_PKI_Root_CA")
        toBeRemovedTrustedCertificateFdns.size() == 2
    }

    def 'process task executed with success with ManagedElement and with certificate under OAM trust category' () {
        given: "node created with ManagedElement"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with truststore under ManagedElement"
        createTruststoreUnderManagedElement()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        and: "with certificate MOs under the OAM trust category"
        createCertificateUnderTruststoreCertificates("oamTrustCategory", "ENM_PKI_Root_CA", ENM_PKI_ROOT_CA_CERT)
        and: "task for node"
        def task = new CbpOiCheckRemoveTrustTask(nodeName)
        and: "task with certificate type OAM"
        task.setCertCategory("CORBA_PEERS")
        and: "task with expected SN and issuer"
        task.setCertificateSerialNumber(ENM_PKI_ROOT_CA_SN)
        task.setIssuer(ENM_PKI_ROOT_CA_ISSUER)
        when:
        def result = cbpOiCheckRemoveTrustTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be REMOVE"
        deserializedTaskResult.getResult() == "REMOVE"
        and: "output parameters should be added with the TRUSTED_CERTIFICATE_FDN correctly set to the list of FDN of the to be removed certificates"
        deserializedTaskResult.getOutputParams().size() == 1
        def String serializedToBeRemovedTrustedCertificateFdns = deserializedTaskResult.getOutputParams().get("TRUSTED_CERTIFICATE_FDN")
        def List<String> toBeRemovedTrustedCertificateFdns = NscsObjectSerializer.readObject(serializedToBeRemovedTrustedCertificateFdns)
        toBeRemovedTrustedCertificateFdns.size() == 1
        toBeRemovedTrustedCertificateFdns.contains("ManagedElement="+nodeName+",truststore=1,certificates=oamTrustCategory,certificate=ENM_PKI_Root_CA")
    }

    def 'process task executed with success with ManagedElement and with certificate under OAM and CMP CA trust categories' () {
        given: "node created with ManagedElement"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with truststore under ManagedElement"
        createTruststoreUnderManagedElement()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        and: "with certificate MOs under the OAM trust category"
        createCertificateUnderTruststoreCertificates("oamTrustCategory", "ENM_PKI_Root_CA", ENM_PKI_ROOT_CA_CERT)
        and: "with OAM CMP CA trust category under truststore"
        createCertificatesUnderTruststore("oamCmpCaTrustCategory")
        and: "with certificate MOs under the OAM CMP CA trust category"
        createCertificateUnderTruststoreCertificates("oamCmpCaTrustCategory", "ENM_PKI_Root_CA", ENM_PKI_ROOT_CA_CERT)
        and: "task for node"
        def task = new CbpOiCheckRemoveTrustTask(nodeName)
        and: "task with certificate type OAM"
        task.setCertCategory("CORBA_PEERS")
        and: "task with expected SN and issuer"
        task.setCertificateSerialNumber(ENM_PKI_ROOT_CA_SN)
        task.setIssuer(ENM_PKI_ROOT_CA_ISSUER)
        when:
        def result = cbpOiCheckRemoveTrustTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be REMOVE"
        deserializedTaskResult.getResult() == "REMOVE"
        and: "output parameters should be added with the TRUSTED_CERTIFICATE_FDN correctly set to the list of FDN of the to be removed certificates"
        deserializedTaskResult.getOutputParams().size() == 1
        def String serializedToBeRemovedTrustedCertificateFdns = deserializedTaskResult.getOutputParams().get("TRUSTED_CERTIFICATE_FDN")
        def List<String> toBeRemovedTrustedCertificateFdns = NscsObjectSerializer.readObject(serializedToBeRemovedTrustedCertificateFdns)
        toBeRemovedTrustedCertificateFdns.contains("ManagedElement="+nodeName+",truststore=1,certificates=oamTrustCategory,certificate=ENM_PKI_Root_CA")
        toBeRemovedTrustedCertificateFdns.contains("ManagedElement="+nodeName+",truststore=1,certificates=oamCmpCaTrustCategory,certificate=ENM_PKI_Root_CA")
        toBeRemovedTrustedCertificateFdns.size() == 2
    }

    def 'process task executed with success with MeContext and without ManagedElement and with certificate under OAM trust category' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with truststore under MeContext"
        createTruststoreUnderMeContext()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        and: "with certificate MOs under the OAM trust category"
        createCertificateUnderTruststoreCertificates("oamTrustCategory", "ENM_PKI_Root_CA", ENM_PKI_ROOT_CA_CERT)
        and: "task for node"
        def task = new CbpOiCheckRemoveTrustTask(nodeName)
        and: "task with certificate type OAM"
        task.setCertCategory("CORBA_PEERS")
        and: "task with expected SN and issuer"
        task.setCertificateSerialNumber(ENM_PKI_ROOT_CA_SN)
        task.setIssuer(ENM_PKI_ROOT_CA_ISSUER)
        when:
        def result = cbpOiCheckRemoveTrustTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be REMOVE"
        deserializedTaskResult.getResult() == "REMOVE"
        and: "output parameters should be added with the TRUSTED_CERTIFICATE_FDN correctly set to the list of FDN of the to be removed certificates"
        deserializedTaskResult.getOutputParams().size() == 1
        def String serializedToBeRemovedTrustedCertificateFdns = deserializedTaskResult.getOutputParams().get("TRUSTED_CERTIFICATE_FDN")
        def List<String> toBeRemovedTrustedCertificateFdns = NscsObjectSerializer.readObject(serializedToBeRemovedTrustedCertificateFdns)
        toBeRemovedTrustedCertificateFdns.size() == 1
        toBeRemovedTrustedCertificateFdns.contains("MeContext="+nodeName+",truststore=1,certificates=oamTrustCategory,certificate=ENM_PKI_Root_CA")
    }

    def 'process task executed with success with MeContext and without ManagedElement and with certificate under OAM and CMP CA trust categories' () {
        given: "node created with MeContext"
        createNodeWithMeContext(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with truststore under MeContext"
        createTruststoreUnderMeContext()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        and: "with certificate MOs under the OAM trust category"
        createCertificateUnderTruststoreCertificates("oamTrustCategory", "ENM_PKI_Root_CA", ENM_PKI_ROOT_CA_CERT)
        and: "with OAM CMP CA trust category under truststore"
        createCertificatesUnderTruststore("oamCmpCaTrustCategory")
        and: "with certificate MOs under the OAM CMP CA trust category"
        createCertificateUnderTruststoreCertificates("oamCmpCaTrustCategory", "ENM_PKI_Root_CA", ENM_PKI_ROOT_CA_CERT)
        and: "task for node"
        def task = new CbpOiCheckRemoveTrustTask(nodeName)
        and: "task with certificate type OAM"
        task.setCertCategory("CORBA_PEERS")
        and: "task with expected SN and issuer"
        task.setCertificateSerialNumber(ENM_PKI_ROOT_CA_SN)
        task.setIssuer(ENM_PKI_ROOT_CA_ISSUER)
        when:
        def result = cbpOiCheckRemoveTrustTaskHandler.processTask(task)
        and: "deserializing the result"
        def WorkflowQueryTaskResult deserializedTaskResult = NscsObjectSerializer.readObject(result)
        then: "no exception should be thrown"
        noExceptionThrown()
        and: "result should be not null"
        result != null
        and: "deserialized task result should be not null"
        deserializedTaskResult != null
        and: "task result should be REMOVE"
        deserializedTaskResult.getResult() == "REMOVE"
        and: "output parameters should be added with the TRUSTED_CERTIFICATE_FDN correctly set to the list of FDN of the to be removed certificates"
        deserializedTaskResult.getOutputParams().size() == 1
        def String serializedToBeRemovedTrustedCertificateFdns = deserializedTaskResult.getOutputParams().get("TRUSTED_CERTIFICATE_FDN")
        def List<String> toBeRemovedTrustedCertificateFdns = NscsObjectSerializer.readObject(serializedToBeRemovedTrustedCertificateFdns)
        toBeRemovedTrustedCertificateFdns.contains("MeContext="+nodeName+",truststore=1,certificates=oamTrustCategory,certificate=ENM_PKI_Root_CA")
        toBeRemovedTrustedCertificateFdns.contains("MeContext="+nodeName+",truststore=1,certificates=oamCmpCaTrustCategory,certificate=ENM_PKI_Root_CA")
        toBeRemovedTrustedCertificateFdns.size() == 2
    }

    @Unroll
    def 'process task executed with failure with ManagedElement and with invalid serial number and/or issuer' () {
        given: "node created with ManagedElement"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with truststore under ManagedElement"
        createTruststoreUnderManagedElement()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        and: "with certificate MOs under the OAM trust category"
        createCertificateUnderTruststoreCertificates("oamTrustCategory", "ENM_PKI_Root_CA", ENM_PKI_ROOT_CA_CERT)
        and: "task for node"
        def task = new CbpOiCheckRemoveTrustTask(nodeName)
        and: "task with certificate type OAM"
        task.setCertCategory("CORBA_PEERS")
        and: "task with expected SN and issuer"
        task.setCertificateSerialNumber(serialnumber)
        task.setIssuer(issuer)
        when:
        def result = cbpOiCheckRemoveTrustTaskHandler.processTask(task)
        then: "exception should be thrown"
        thrown(UnexpectedErrorException)
        where:
        serialnumber << [
            null,
            "",
            "1234",
            "1234",
            null,
            ""
        ]
        issuer << [
            "CN=ENM_PKI_Root_CA",
            "CN=ENM_PKI_Root_CA",
            null,
            "",
            null,
            ""
        ]
    }

    def 'process task executed with failure with ManagedElement and with invalid certificate under OAM trust category' () {
        given: "node created with ManagedElement"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with truststore under ManagedElement"
        createTruststoreUnderManagedElement()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        and: "with certificate MOs under the OAM trust category"
        createCertificateUnderTruststoreCertificates("oamTrustCategory", "ENM_PKI_Root_CA", INVALID_ENM_PKI_ROOT_CA_CERT)
        and: "task for node"
        def task = new CbpOiCheckRemoveTrustTask(nodeName)
        and: "task with certificate type OAM"
        task.setCertCategory("CORBA_PEERS")
        and: "task with expected SN and issuer"
        task.setCertificateSerialNumber(ENM_PKI_ROOT_CA_SN)
        task.setIssuer(ENM_PKI_ROOT_CA_ISSUER)
        when:
        def result = cbpOiCheckRemoveTrustTaskHandler.processTask(task)
        then: "exception should be thrown"
        thrown(UnexpectedErrorException)
    }

    @Unroll
    def 'process task executed with failure with ManagedElement and with not present serial number and/or issuer' () {
        given: "node created with ManagedElement"
        createNodeWithManagedElement(VDU_TARGET_TYPE, VDU_TARGET_MODEL_IDENTITY, nodeName)
        and: "with truststore under ManagedElement"
        createTruststoreUnderManagedElement()
        and: "with OAM trust category under truststore"
        createCertificatesUnderTruststore("oamTrustCategory")
        and: "with certificate MOs under the OAM trust category"
        createCertificateUnderTruststoreCertificates("oamTrustCategory", "ENM_PKI_Root_CA", ENM_PKI_ROOT_CA_CERT)
        and: "task for node"
        def task = new CbpOiCheckRemoveTrustTask(nodeName)
        and: "task with certificate type OAM"
        task.setCertCategory("CORBA_PEERS")
        and: "task with expected SN and issuer"
        task.setCertificateSerialNumber(serialnumber)
        task.setIssuer(issuer)
        when:
        def result = cbpOiCheckRemoveTrustTaskHandler.processTask(task)
        then: "exception should be thrown"
        thrown(UnexpectedErrorException)
        where:
        serialnumber << [
            "8577146253878525861",
            "1234",
            "1234"
        ]
        issuer << [
            "ANOTHER_ISSUER_DN",
            "CN=ENM_PKI_Root_CA",
            "ANOTHER_ISSUER_DN"
        ]
    }
}
