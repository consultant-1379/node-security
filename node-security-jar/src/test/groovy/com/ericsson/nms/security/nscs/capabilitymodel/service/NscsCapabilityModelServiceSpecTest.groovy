/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.capabilitymodel.service

import javax.enterprise.context.spi.CreationalContext
import javax.enterprise.inject.spi.Bean
import javax.enterprise.inject.spi.BeanManager

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl

import spock.lang.Shared
import spock.lang.Unroll

class NscsCapabilityModelServiceSpecTest extends CdiSpecification {

    @ObjectUnderTest
    private NscsCapabilityModelService nscsCapabilityModelService

    @MockedImplementation
    private NscsCMReaderService reader

    @MockedImplementation
    private NormalizableNodeReference normalized

    @MockedImplementation
    private Bean<?> bean

    @MockedImplementation
    private BeanManager beanManager

    @MockedImplementation
    private CreationalContext creationalContext

    @MockedImplementation
    private NscsCapabilityModel nscsCapabilityModel

    @MockedImplementation
    private NscsModelServiceImpl nscsModelServiceImpl

    @Shared
    def targetcategories = [
        "CATEGORY",
        null,
        "CATEGORY",
        null
    ]

    @Shared
    def targettypes = [
        "TYPE",
        "TYPE",
        null,
        null
    ]

    def setup() {
        beanManager.createCreationalContext(_) >> creationalContext
        beanManager.getReference(_, _, _) >> nscsCapabilityModel
        reader.getNormalizedNodeReference(_) >> normalized
    }

    def 'object under test'() {
        expect:
        nscsCapabilityModelService != null
    }

    @Unroll
    def 'is cli command supported #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isCliCommandSupported(normNodeRef, "CMD")
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get expected credentials #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getExpectedCredentialsParams(normNodeRef)
        then:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get unexpected credentials #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getUnexpectedCredentialsParams(normNodeRef)
        then:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get supported enrollment modes #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getSupportedEnrollmentModes(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get default enrollment mode #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getDefaultEnrollmentMode(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get issue/reissue cert wf #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getIssueOrReissueCertWf(normNodeRef, "OAM")
        then:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get trust distribute wf #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getTrustDistributeWf(normNodeRef, "OAM")
        then:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get trust remove wf #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getTrustRemoveWf(normNodeRef, "OAM")
        then:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'is cert mgmt supported #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isCertificateManagementSupported(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'is cert type supported #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isCertTypeSupported(normNodeRef, "OAM")
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get default algorithm keys #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getDefaultAlgorithmKeys(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get mirror root mo #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getMirrorRootMo(normNodeRef)
        then:
        1 * nscsModelServiceImpl.getRootMoType(_, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'is security level supported #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isSecurityLevelSupported(normNodeRef, "SL2")
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get supported security levels #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getSupportedSecurityLevels(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get MOM type #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getMomType(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'is configured subject name used for enrollment #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isConfiguredSubjectNameUsedForEnrollment(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'is Ikev2 policy profile supported #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isIkev2PolicyProfileSupported(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'is LDAP common user supported #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isLdapCommonUserSupported(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get enrollment CA authorization modes #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getEnrollmentCAAuthorizationModes(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'is deprecated enrollment authority used #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isDeprecatedEnrollmentAuthorityUsed(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'is deprecated authority type supported #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isDeprecatedAuthorityTypeSupported(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get ECIM default node credentials ids #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getComEcimDefaultNodeCredentialIds(normNodeRef)
        then:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get ECIM default trust category ids #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getComEcimDefaultTrustCategoryIds(normNodeRef)
        then:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get crl check wf #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getCrlCheckWf(normNodeRef, "OAM")
        then:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'is cert type supported for crl check #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isCertTypeSupportedforCrlCheck(normNodeRef, "OAM")
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get on demand crl download wf #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getOnDemandCrlDownloadWf(normNodeRef)
        then:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get supported cipher protocol types #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getSupportedCipherProtocolTypes(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get cipher mo attributes #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getCipherMoAttributes(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'is empty value supported for ciphers #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isEmptyValueSupportedForCiphers(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get default password hash algorithm #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getDefaultPasswordHashAlgorithm(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'is trust category type supported for ciphers #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isTrustCategoryTypeSupported(normNodeRef, "OAM")
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get ntp remove wf #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getNtpRemoveWorkflow(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get ntp configure wf #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getNtpConfigureWorkflow(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'is node ssh private key import supported for ciphers #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.isNodeSshPrivateKeyImportSupported(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get ldap configure wf #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getLdapConfigureWorkflow(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get ldap mo name #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getLdapMoName(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get default otp validity period in minutes #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getDefaultOtpValidityPeriodInMinutes(normNodeRef)
        then:
        thrown(NscsCapabilityModelException)
        and:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }

    @Unroll
    def 'get default enrollment ca trust category id #targetcategory #targettype with normalized'() {
        given:
        def NormalizableNodeReference normNodeRef = mock(NormalizableNodeReference)
        normNodeRef.getFdn() >> "FDN"
        normNodeRef.getTargetCategory() >> targetcategory
        normNodeRef.getNeType() >> targettype
        and:
        normalized.getTargetCategory() >> "CATEGORY"
        normalized.getNeType() >> "TYPE"
        when:
        nscsCapabilityModelService.getDefaultEnrollmentCaTrustCategoryId(normNodeRef)
        then:
        1 * nscsCapabilityModel.getCapabilityValue(_, _, _, _, _)
        where:
        targetcategory << targetcategories
        targettype << targettypes
    }
}