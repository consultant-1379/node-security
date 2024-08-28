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
package com.ericsson.nms.security.nscs.utilities

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService
import com.ericsson.nms.security.nscs.data.Model
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils
import com.ericsson.nms.security.nscs.pib.configuration.ConfigurationListener
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse

import spock.lang.Unroll

class NscsComEcimNodeUtilityTest extends CdiSpecification {

    @ObjectUnderTest
    private NSCSComEcimNodeUtility  nscsComEcimNodeUtility

    @MockedImplementation
    private NscsCapabilityModelService capabilityService

    @MockedImplementation
    private ConfigurationListener configurationListener

    @MockedImplementation
    private NscsCMReaderService reader

    def "object under test injection" () {
        expect:
        nscsComEcimNodeUtility != null
    }

    @Unroll
    def 'getNodeCredentialFdn method success case for IPSEC Case' () {
        given :
        String RADIO_NODE_NAME = "LTE01dg2ERBS00002";
        String MIRROR_ROOT_FDN = String.format("ManagedElement=%s", RADIO_NODE_NAME);
        Mo rootMo = Model.ME_CONTEXT.comManagedElement;
        String certificateType = "IPSEC";
        NormalizableNodeReference radioNodeNormNodeRef = MockUtils.createNormalizableNodeRef("RadioNode", "RadioNode_NSCSComEcimNodeUtility");
        capabilityService.isIkev2PolicyProfileSupported(_) >> true
        configurationListener.getEnforcedIKEv2PolicyProfileID() >> IKEv2PolicyProfileID
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();
        attributesMap.put("credential", "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=ipsecNodeCredential");
        attributesMap.put("ikev2PolicyProfileId", IKEv2PolicyProfileIDValue);
        attributesMap.put("trustCategory", "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=ipsecTrustCategory");
        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        String Ikev2PolicyProfileFdn = "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,Transport=1,Ikev2PolicyProfile="+IKEv2PolicyProfileIDValue
        cmObject.setFdn(Ikev2PolicyProfileFdn);
        cmObjects.add(cmObject);
        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        cmResponse.setTargetedCmObjects(cmObjects);
        reader.getMos(_, _,_,_) >> cmResponse
        when :
        String result = nscsComEcimNodeUtility.getNodeCredentialFdn(MIRROR_ROOT_FDN,rootMo,certificateType,radioNodeNormNodeRef)
        then :
        assert (result = expected)
        where :
        IKEv2PolicyProfileID  |IKEv2PolicyProfileIDValue || expected
        "IKEV2"           | "IKEV2"                  || "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=ipsecNodeCredential"
        "NONE"            |    "1"                   || "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=ipsecNodeCredential"
        ""                |    "1"                   || "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=ipsecNodeCredential"
    }

    @Unroll
    def 'getTrustCategoryFdn method success case for IPSEC Case' () {
        given :
        String RADIO_NODE_NAME = "LTE01dg2ERBS00002";
        String MIRROR_ROOT_FDN = String.format("ManagedElement=%s", RADIO_NODE_NAME);
        Mo rootMo = Model.ME_CONTEXT.comManagedElement;
        String certificateType = "IPSEC";
        NormalizableNodeReference radioNodeNormNodeRef = MockUtils.createNormalizableNodeRef("RadioNode", "RadioNode_NSCSComEcimNodeUtility");
        capabilityService.isIkev2PolicyProfileSupported(_) >> true
        configurationListener.getEnforcedIKEv2PolicyProfileID() >> IKEv2PolicyProfileID
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();
        attributesMap.put("credential", "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=ipsecNodeCredential");
        attributesMap.put("ikev2PolicyProfileId", IKEv2PolicyProfileIDValue);
        attributesMap.put("trustCategory", "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=ipsecTrustCategory");
        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        String Ikev2PolicyProfileFdn = "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,Transport=1,Ikev2PolicyProfile="+IKEv2PolicyProfileIDValue
        cmObject.setFdn(Ikev2PolicyProfileFdn);
        cmObjects.add(cmObject);
        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        cmResponse.setTargetedCmObjects(cmObjects);
        reader.getMos(_, _,_,_) >> cmResponse
        when :
        String result = nscsComEcimNodeUtility.getTrustCategoryFdn(MIRROR_ROOT_FDN,rootMo,certificateType,radioNodeNormNodeRef)
        then :
        assert (result = expected)
        where :
        IKEv2PolicyProfileID  |IKEv2PolicyProfileIDValue || expected
        "IKEV2"           | "IKEV2"                  || "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=ipsecTrustCategory"
        "NONE"            |    "1"                   || "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=ipsecTrustCategory"
        ""                |    "1"                   || "MeContext=LTE01dg2ERBS00002,ManagedElement=LTE01dg2ERBS00002,SystemFunctions=1,SecM=1,CertM=1,TrustCategory=ipsecTrustCategory"
    }

    @Unroll
    def 'getIkev2PolicyProfileMOName method success case for IPSEC Case' () {
        given :
        configurationListener.getEnforcedIKEv2PolicyProfileID() >> IKEv2PolicyProfileID
        when :
        String result = nscsComEcimNodeUtility.getIkev2PolicyProfileMOName()
        then:
        assert (result = expected)
        where :
        IKEv2PolicyProfileID  || expected
        "NONE"           ||   "1"
        ""               ||   "1"
        "IKEV2"          ||   "IKEV2"
    }
}
