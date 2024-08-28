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
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.ModelDefinition.ComEcimManagedElement
import com.ericsson.nms.security.nscs.data.ModelDefinition.MeContext
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl
import com.ericsson.nms.security.nscs.pib.configuration.ConfigurationListener
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.attribute.ComEcimConfigureTrustUsersTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys

class ComEcimConfigureTrustUsersTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    private ComEcimConfigureTrustUsersTaskHandler taskHandler

    @MockedImplementation
    private NscsLogger nscsLogger

    @Inject
    private NormalizableNodeReference normNodeRef

    @MockedImplementation
    private NscsCMReaderService reader

    @MockedImplementation
    private NscsCapabilityModelService capabilityService

    @MockedImplementation
    private NscsNodeUtility nscsNodeUtility

    @MockedImplementation
    private NscsModelServiceImpl nscsModelServiceImpl

    @MockedImplementation
    private ConfigurationListener configurationListener;


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
                .type("MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,Transport=1,Ikev2PolicyProfile")
                .name("1")
                .build()
    }

    def 'process task executed with success for IPSEC Case without creation ikev2policyprofile MO' () {
        given:
        ComEcimConfigureTrustUsersTask task = new ComEcimConfigureTrustUsersTask()
        final NodeReference nodeRef = new NodeRef(nodeName)
        task.setNode(nodeRef)
        normNodeRef.getNormalizedRef() >> nodeRef
        reader.getNormalizableNodeReference(_) >> normNodeRef
        MeContext ME_CONTEXT = new MeContext();
        capabilityService.getMirrorRootMo(_) >> ME_CONTEXT.comManagedElement;
        task.setTrustedCertCategory("IPSEC")
        Map<String, Serializable> outputParams = new HashMap<String, Serializable>();
        outputParams.put(WorkflowOutputParameterKeys.IS_ONLINE_ENROLLMENT.toString(), "TRUE");
        outputParams.put(WorkflowOutputParameterKeys.TRUST_CATEGORY_FDN.toString(), "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=oamTrustCategory");
        task.setOutputParams(outputParams)
        final Mo certMMo = ((ComEcimManagedElement) ME_CONTEXT.comManagedElement).systemFunctions.secM.certM;
        final Mo transportMo = ((ComEcimManagedElement) ME_CONTEXT.comManagedElement).transport;
        nscsNodeUtility.getSingleInstanceMoFdn(_, certMMo) >> "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1"
        nscsNodeUtility.getSingleInstanceMoFdn(_, transportMo) >> "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,Transport=1"
        capabilityService.isIkev2PolicyProfileSupported(_) >> true
        configurationListener.getEnforcedIKEv2PolicyProfileID()>> "IKEV2"
        List<String> attrs = new ArrayList<>();
        attrs.add("credential=MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=ipsecNodeCredential")
        attrs.add("trustCategory=MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=ipsecTrustCategory")
        NscsModelInfo ikev2PolicyProfileModelInfo = new NscsModelInfo("Ikev2PolicyProfile","RtnIkev2PolicyProfile","1","1.14.0",attrs)
        nscsModelServiceImpl.getModelInfo(_, _, _, _) >> ikev2PolicyProfileModelInfo
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();
        attributesMap.put("credential", "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=ipsecNodeCredential");
        attributesMap.put("ikev2PolicyProfileId", "IKEV2");
        attributesMap.put("trustCategory", "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=ipsecTrustCategory");
        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        String Ikev2PolicyProfileFdn = "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,Transport=1,Ikev2PolicyProfile=IKEV2"
        cmObject.setFdn(Ikev2PolicyProfileFdn);
        cmObjects.add(cmObject);
        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        cmResponse.setTargetedCmObjects(cmObjects);
        reader.getMos(_, _,_, _) >> cmResponse
        when:
        String result = taskHandler.processTask(task)
        then:
        assert (result = "VALID")
    }

    def 'process task executed with success for IPSEC Case with UnexpectedErrorException while creation of ikev2policyprofile MO' () {
        given:
        ComEcimConfigureTrustUsersTask task = new ComEcimConfigureTrustUsersTask()
        final NodeReference nodeRef = new NodeRef(nodeName)
        task.setNode(nodeRef)
        normNodeRef.getNormalizedRef() >> nodeRef
        reader.getNormalizableNodeReference(_) >> normNodeRef
        MeContext ME_CONTEXT = new MeContext();
        capabilityService.getMirrorRootMo(_) >> ME_CONTEXT.comManagedElement;
        task.setTrustedCertCategory("IPSEC")
        Map<String, Serializable> outputParams = new HashMap<String, Serializable>();
        outputParams.put(WorkflowOutputParameterKeys.IS_ONLINE_ENROLLMENT.toString(), "FALSE");
        outputParams.put(WorkflowOutputParameterKeys.TRUST_CATEGORY_FDN.toString(), "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=oamTrustCategory");
        task.setOutputParams(outputParams)
        final Mo certMMo = ((ComEcimManagedElement) ME_CONTEXT.comManagedElement).systemFunctions.secM.certM;
        final Mo transportMo = ((ComEcimManagedElement) ME_CONTEXT.comManagedElement).transport;
        nscsNodeUtility.getSingleInstanceMoFdn(_, certMMo) >> "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1"
        nscsNodeUtility.getSingleInstanceMoFdn(_, transportMo) >> "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,Transport=1"
        capabilityService.isIkev2PolicyProfileSupported(_) >> true
        configurationListener.getEnforcedIKEv2PolicyProfileID()>> "IKEV2"
        List<String> attrs = new ArrayList<>();
        attrs.add("credential=MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=ipsecNodeCredential")
        attrs.add("trustCategory=MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=ipsecTrustCategory")
        NscsModelInfo ikev2PolicyProfileModelInfo = new NscsModelInfo("Ikev2PolicyProfile","RtnIkev2PolicyProfile","1","1.14.0",attrs)
        nscsModelServiceImpl.getModelInfo(_, _, _, _) >> ikev2PolicyProfileModelInfo
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();
        attributesMap.put("credential", "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=ipsecNodeCredential");
        attributesMap.put("ikev2PolicyProfileId", "IKEV2");
        attributesMap.put("trustCategory", "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=ipsecTrustCategory");
        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        String Ikev2PolicyProfileFdn = null
        cmObject.setFdn(Ikev2PolicyProfileFdn);
        cmObjects.add(cmObject);
        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        cmResponse.setTargetedCmObjects(cmObjects);
        reader.getMos(_, _,_, _) >> cmResponse
        when:
        String result = taskHandler.processTask(task)
        then:
        thrown(UnexpectedErrorException)
    }
}
