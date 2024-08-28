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
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute

import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.MoObject
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.ModelDefinition.MeContext
import com.ericsson.nms.security.nscs.data.ModelDefinition.TrustCategory
import com.ericsson.nms.security.nscs.data.NscsCMWriterService.WriterSpecificationBuilder
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl
import com.ericsson.nms.security.nscs.utilities.*
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimConfigureCredentialUsersTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys

class ComEcimConfigureCredentialUsersTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    private ComEcimConfigureCredentialUsersTaskHandler taskHandler

    @MockedImplementation
    private NscsLogger nscsLogger

    @MockedImplementation
    private NscsCMReaderService reader

    @MockedImplementation
    private NscsCapabilityModelService capabilityService

    @MockedImplementation
    WriterSpecificationBuilder nodeCredentialSpec

    @Inject
    private NormalizableNodeReference normNodeRef

    @MockedImplementation
    private NscsNodeUtility nscsNodeUtility;

    @MockedImplementation
    private NscsModelServiceImpl nscsModelServiceImpl;

    @MockedImplementation
    private NodeValidatorUtility nodeValidatorUtility;

    @MockedImplementation
    private NSCSComEcimNodeUtility nscsComEcimNodeUtility;

    @MockedImplementation
    private MoObject moObject;

    private RuntimeConfigurableDps runtimeConfigurableDps
    private DataPersistenceService dataPersistenceService
    private ManagedObject neObject
    private ManagedObject meContext
    private nodeName = "LTE01dg2ERBS00002"
    private neType = "RadioNode"
    private ossModelIdentity = "1.0.0"
    private platform = "ComEcim"
    String COM_SEC_M_NS = "ComSecM"
    String COM_CERT_M_NS = "RcsCertM"

    def setup() {
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        meContext = runtimeConfigurableDps.addManagedObject()
                .namespace("ComTop")
                .type("MeContext")
                .name(nodeName)
                .build()
        neObject = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("NetworkElement")
                .name(nodeName)
                .version("2.0.0")
                .addAttribute("neType", neType)
                .addAttribute("ossModelIdentity", ossModelIdentity)
                .addAttribute("platformType", platform)
                .build()
        neObject.addAssociation("association", meContext)
        neObject = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential")
                .name("oamNodeCredential")
                .build()
        neObject = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SysM=1,NetconfTls")
                .name("1")
                .build()
        neObject = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,Transport")
                .name("1")
                .build()
        neObject = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,Transport=1,Ikev2PolicyProfile")
                .name("1")
                .build()
        neObject = runtimeConfigurableDps.addManagedObject()
                .namespace("OSS_NE_DEF")
                .type("MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory")
                .name("oamTrustCategory")
                .build()
    }

    def 'process task executed with success for OAM Case' () {
        given:
        ComEcimConfigureCredentialUsersTask task = new ComEcimConfigureCredentialUsersTask()
        final NodeReference nodeRef = new NodeRef(nodeName)
        task.setNode(nodeRef)
        normNodeRef.getNormalizedRef() >> nodeRef
        reader.getNormalizableNodeReference(_) >> normNodeRef
        MeContext ME_CONTEXT = new MeContext();
        capabilityService.getMirrorRootMo(_) >> ME_CONTEXT.comManagedElement;
        task.setTrustedCertCategory("CORBA_PEERS")
        task.setIsTrustDistributionRequired("TRUE")
        task.setCertificateEnrollmentCa("EXTERNAL_CA")
        Map<String, Serializable> outputParams = new HashMap<String, Serializable>();
        outputParams.put(WorkflowOutputParameterKeys.RENEWAL_MODE.toString(), "AUTOMATIC");
        outputParams.put(WorkflowOutputParameterKeys.NODE_CREDENTIAL_FDN.toString(), "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=oamNodeCredential");
        outputParams.put(WorkflowOutputParameterKeys.TRUST_CATEGORY_FDN.toString(), "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=oamTrustCategory");
        capabilityService.isCliCommandSupported(_, _) >> true
        task.setOutputParams(outputParams)
        task.setCertificateEnrollmentCa("EXTERNAL_CA")
        task.setInterfaceFdn("dummy")
        nscsNodeUtility.getSingleInstanceMoFdn(_, _, _, _)>> "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SysM=1,NetconfTls=1"
        nodeValidatorUtility.validateNodeTypeForExtCa(_) >> true
        reader.getMoObjectByFdn(_) >> moObject
        moObject.getAttribute(TrustCategory.CRL_INTERFACE) >> "dummy1"
        when:
        String result = taskHandler.processTask(task)
        then:
        assert (result = "VALID")
    }

    def 'process task executed with success for IPSEC Case without creation ikev2policyprofile MO' () {
        given:
        ComEcimConfigureCredentialUsersTask task = new ComEcimConfigureCredentialUsersTask()
        final NodeReference nodeRef = new NodeRef(nodeName)
        task.setNode(nodeRef)
        normNodeRef.getNormalizedRef() >> nodeRef
        reader.getNormalizableNodeReference(_) >> normNodeRef
        MeContext ME_CONTEXT = new MeContext();
        capabilityService.getMirrorRootMo(_) >> ME_CONTEXT.comManagedElement;
        task.setTrustedCertCategory("IPSEC")
        Map<String, Serializable> outputParams = new HashMap<String, Serializable>();
        outputParams.put(WorkflowOutputParameterKeys.RENEWAL_MODE.toString(), "AUTOMATIC");
        outputParams.put(WorkflowOutputParameterKeys.NODE_CREDENTIAL_FDN.toString(), "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=oamNodeCredential");
        outputParams.put(WorkflowOutputParameterKeys.TRUST_CATEGORY_FDN.toString(), "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=oamTrustCategory");
        task.setOutputParams(outputParams)
        task.setCertificateEnrollmentCa("NONE")
        nscsNodeUtility.getSingleInstanceMoFdn(_, _)>> "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,Transport=1"
        capabilityService.isIkev2PolicyProfileSupported(_) >> true
        List<String> attrs = new ArrayList<>();
        attrs.add("credential=MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=ipsecNodeCredential")
        attrs.add("trustCategory=MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=ipsecTrustCategory")
        NscsModelInfo ikev2PolicyProfileModelInfo = new NscsModelInfo("Ikev2PolicyProfile","RtnIkev2PolicyProfile","1","1.14.0",attrs)
        nscsModelServiceImpl.getModelInfo(_, _, _, _) >> ikev2PolicyProfileModelInfo
        nscsComEcimNodeUtility.getIkev2PolicyProfileFdn(_, _, _, _,_) >> "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,Transport=1,Ikev2PolicyProfile=1"
        when:
        String result = taskHandler.processTask(task)
        then:
        assert (result = "VALID")
    }


    def 'process task executed with success for IPSEC Case with creation of ikev2policyprofile MO' () {
        given:
        ComEcimConfigureCredentialUsersTask task = new ComEcimConfigureCredentialUsersTask()
        final NodeReference nodeRef = new NodeRef(nodeName)
        task.setNode(nodeRef)
        normNodeRef.getNormalizedRef() >> nodeRef
        reader.getNormalizableNodeReference(_) >> normNodeRef
        MeContext ME_CONTEXT = new MeContext();
        capabilityService.getMirrorRootMo(_) >> ME_CONTEXT.comManagedElement;
        task.setTrustedCertCategory("IPSEC")
        Map<String, Serializable> outputParams = new HashMap<String, Serializable>();
        outputParams.put(WorkflowOutputParameterKeys.RENEWAL_MODE.toString(), "AUTOMATIC");
        outputParams.put(WorkflowOutputParameterKeys.NODE_CREDENTIAL_FDN.toString(), "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=oamNodeCredential");
        outputParams.put(WorkflowOutputParameterKeys.TRUST_CATEGORY_FDN.toString(), "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=oamTrustCategory");
        task.setOutputParams(outputParams)
        task.setCertificateEnrollmentCa("NONE")
        nscsNodeUtility.getSingleInstanceMoFdn(_, _)>> "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,Transport=1"
        capabilityService.isIkev2PolicyProfileSupported(_) >> true
        List<String> attrs = new ArrayList<>();
        attrs.add("credential=MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=ipsecNodeCredential")
        attrs.add("trustCategory=MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=ipsecTrustCategory")
        NscsModelInfo ikev2PolicyProfileModelInfo = new NscsModelInfo("Ikev2PolicyProfile","RtnIkev2PolicyProfile","1","1.14.0",attrs)
        nscsModelServiceImpl.getModelInfo(_, _, _, _) >> ikev2PolicyProfileModelInfo
        when:
        String result = taskHandler.processTask(task)
        then:
        thrown(UnexpectedErrorException)
    }
}