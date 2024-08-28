package com.ericsson.nms.security.nscs.utilities

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.CouldNotReadMoAttributeException
import com.ericsson.nms.security.nscs.data.Model
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.handler.command.utility.WebServerStatus
import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse
import spock.lang.Unroll

class MoAttributeHandlerImplTest extends CdiSpecification{

    @ObjectUnderTest
    private MoAttributeHandlerImpl moAttributeHandler

    @MockedImplementation
    private NscsCMReaderService nscsCMReaderEmpty

    @ImplementationInstance
    NscsCMReaderService nscsCMReaderNotEmpty = [

            getMOAttribute : {final String node, final String moType,
                              final String namespace, final String attribute ->
                CmResponse cmResponse = new CmResponse()
                CmObject cmObject = new CmObject()
                Map<String, Object> attributes = new LinkedHashMap(0)
                attributes.put(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.ENM_SSH_PUBLIC_KEY,"enm_ssh_public_key_fortest"
                        )
                cmObject.setAttributes(attributes)
                Collection<CmObject> cmObjects = new ArrayList<>()
                cmObjects.add(cmObject)
                cmResponse.setTargetedCmObjects(cmObjects)
                return cmResponse
            }
    ] as NscsCMReaderService

    @ImplementationInstance
    NscsCMReaderService nscsCMReaderNotEmptyAttributeNull = [

            getMOAttribute : {final String node, final String moType,
                              final String namespace, final String attribute ->
                CmResponse cmResponse = new CmResponse()
                CmObject cmObject = new CmObject()
                Map<String, Object> attributes = new LinkedHashMap(0)
                cmObject.setAttributes(attributes)
                Collection<CmObject> cmObjects = new ArrayList<>()
                cmObjects.add(cmObject)
                cmResponse.setTargetedCmObjects(cmObjects)
                return cmResponse
            }
    ] as NscsCMReaderService

    @ImplementationInstance
    NscsCMReaderService nscsCMReaderNotEmptyBooleanAttribute = [

            getMOAttribute : {final String node, final String moType,
                              final String namespace, final String attribute ->
                CmResponse cmResponse = new CmResponse()
                CmObject cmObject = new CmObject()
                Map<String, Object> attributes = new LinkedHashMap(0)
                attributes.put(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.ENM_SSH_PUBLIC_KEY,true
                )
                cmObject.setAttributes(attributes)
                Collection<CmObject> cmObjects = new ArrayList<>()
                cmObjects.add(cmObject)
                cmResponse.setTargetedCmObjects(cmObjects)
                return cmResponse
            }
    ] as NscsCMReaderService

    CmResponse cmResponse = new CmResponse()
    def setup() {
        cmResponse.setStatusCode(0)
        cmResponse.getCmObjects() >> Collections.emptyList()
    }

    @Override
    def addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {}

    def "get successfully MO attribute"() {
        given:
        moAttributeHandler.reader = nscsCMReaderNotEmpty
        def attributeName = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.ENM_SSH_PUBLIC_KEY
        when:
        String moAttrValueRead = moAttributeHandler.getMOAttributeValue(_ as String, _ as String , _ as String , attributeName)
        then:
        moAttrValueRead == "enm_ssh_public_key_fortest"
    }

    def "get successfully MO attribute null "() {
        given:
        moAttributeHandler.reader = nscsCMReaderNotEmptyAttributeNull
        def attributeName = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.ENM_SSH_PUBLIC_KEY
        when:
        String moAttrValueRead = moAttributeHandler.getMOAttributeValue(_ as String, _ as String , _ as String , attributeName)
        then:
        moAttrValueRead == null
    }

    def "get successfully MO attribute boolean "() {
        given:
        moAttributeHandler.reader = nscsCMReaderNotEmptyBooleanAttribute
        def attributeName = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.ENM_SSH_PUBLIC_KEY
        when:
        String moAttrValueRead = moAttributeHandler.getMOAttributeValue(_ as String, _ as String , _ as String , attributeName)
        then:
        moAttrValueRead == "true"
    }

    def "read MO attribute but raise exception"() {
        given:
        nscsCMReaderEmpty.getMOAttribute(_ as String, _ as String , _ as String , _ as String) >> cmResponse
        def attributeName = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.ENM_SSH_PUBLIC_KEY
        when:
        moAttributeHandler.getMOAttributeValue(_ as String, _ as String , _ as String , attributeName)
        then:
        CouldNotReadMoAttributeException e = thrown()
        e.getMessage().contains(attributeName)
    }

    @Unroll
    def "test matching between #webServerStatus and #httpsAttributeInCppConnectivityInfo"() {
        given:
        when:
        String match = moAttributeHandler.match(webServerStatus, httpsAttributeInCppConnectivityInfo)
        then:
        match == output
        where:
        webServerStatus | httpsAttributeInCppConnectivityInfo | output
        WebServerStatus.HTTPS | true | "MATCH"
        WebServerStatus.HTTPS | false | "MISMATCH"
        WebServerStatus.HTTP | true | "MISMATCH"
        WebServerStatus.HTTP | false | "MATCH"

    }
}
