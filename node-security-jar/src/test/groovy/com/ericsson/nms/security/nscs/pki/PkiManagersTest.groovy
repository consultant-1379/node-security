package com.ericsson.nms.security.nscs.pki

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.providers.custom.sfwk.PropertiesForTest
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.util.PropertiesReader
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.CACertificateManagementService
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.EntityCertificateManagementService
import com.ericsson.oss.itpf.security.pki.manager.certificatemanagement.api.ExtCACertificateManagementService
import com.ericsson.oss.itpf.security.pki.manager.configurationmanagement.api.PKIConfigurationManagementService
import com.ericsson.oss.itpf.security.pki.manager.crlmanagement.api.RevocationService
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.EntityManagementService
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.ExtCAManagementService
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.ProfileManagementService
import com.ericsson.oss.itpf.security.pki.manager.profilemanagement.api.custom.EntityManagementCustomService
import org.mockito.Mock


class PkiManagersTest extends CdiSpecification {

    @ObjectUnderTest
    PkiApiManagers pkiApiManagers

    @Mock
    private EntityManagementService pkiEntityManagerMock;

    @Mock
    private EntityManagementCustomService pkiEntityCustomManagerMock;

    @Mock
    private ProfileManagementService pkiProfileManagementServiceMock;

    @Mock
    private CACertificateManagementService pkiCACertificateManagerMock;

    @Mock
    private EntityCertificateManagementService pkiEntityCertificateManagementServiceMock;

    @Mock
    private PKIConfigurationManagementService pkiConfigurationManagementServiceMock;

    @Mock
    private ExtCAManagementService pkiExtCAManagementServiceMock;

    @Mock
    private ExtCACertificateManagementService pkiExtCACertificateManagementServiceMock;

    @Mock
    private RevocationService pkiRevocationServiceMock;

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup() {}

    def "get real pki managers"() {
        given:
        when:
        RevocationService revocationService = pkiApiManagers.getPkiRevocationService()
        ExtCACertificateManagementService extCACertificateManagementService = pkiApiManagers.getExtCACertificateManagementService()
        ExtCAManagementService extCAManagementService = pkiApiManagers.getExtCAManagementService()
        PKIConfigurationManagementService pkiConfigurationManagementService = pkiApiManagers.getConfigurationManagementService()
        EntityCertificateManagementService entityCertificateManagementService = pkiApiManagers.getEntityCertificateManagementService()
        CACertificateManagementService caCertificateManagementService = pkiApiManagers.getCACertificateManagementService()
        ProfileManagementService profileManagementService = pkiApiManagers.getProfileManagementService()
        EntityManagementCustomService entityManagementCustomService = pkiApiManagers.getEntityManagementCustomService()
        EntityManagementService entityManagementService = pkiApiManagers.getEntityManagementService()
        then:
        revocationService !=null &&
                extCACertificateManagementService !=null &&
                extCAManagementService !=null &&
                pkiConfigurationManagementService !=null &&
                entityCertificateManagementService !=null &&
                caCertificateManagementService !=null &&
                profileManagementService !=null &&
                entityManagementCustomService !=null &&
                entityManagementService !=null
    }

    @PropertiesForTest(propertyFile = "pki/nscs.test.properties")
    def "raise exception when useMockEntityManager with EntityManagementService"() {
        given:
            System.setProperty(PropertiesReader.NSCS_PROPERTY_NAME, "pki/nscs.test.properties")
        when:
            pkiApiManagers.getEntityManagementService()
        then:
            UnsupportedOperationException e = thrown()
            e.getMessage() == "Not supported anymore"
    }

    @PropertiesForTest(propertyFile = "pki/nscs.test.properties")
    def "raise exception when useMockEntityManager with EntityManagementCustomService"() {
        given:
        System.setProperty(PropertiesReader.NSCS_PROPERTY_NAME, "pki/nscs.test.properties")
        when:
        pkiApiManagers.getEntityManagementCustomService()
        then:
        UnsupportedOperationException e = thrown()
        e.getMessage() == "Not supported anymore"
    }

    @PropertiesForTest(propertyFile = "pki/nscs.test.properties")
    def "raise exception when useMockCertificateManager with CACertificateManagementService"() {
        given:
        System.setProperty(PropertiesReader.NSCS_PROPERTY_NAME, "pki/nscs.test.properties")
        when:
        pkiApiManagers.getCACertificateManagementService()
        then:
        UnsupportedOperationException e = thrown()
        e.getMessage() == "Not supported anymore"
    }

    @PropertiesForTest(propertyFile = "pki/nscs.test.properties")
    def "raise exception when useMockProfileManager with ProfileManagementService"() {
        given:
        System.setProperty(PropertiesReader.NSCS_PROPERTY_NAME, "pki/nscs.test.properties")
        when:
        pkiApiManagers.getProfileManagementService()
        then:
        UnsupportedOperationException e = thrown()
        e.getMessage() == "Not supported anymore"
    }

    @PropertiesForTest(propertyFile = "pki/nscs.test.properties")
    def "raise exception when useMockPkiConfigurationManager with PKIConfigurationManagementService"() {
        given:
        System.setProperty(PropertiesReader.NSCS_PROPERTY_NAME, "pki/nscs.test.properties")
        when:
        pkiApiManagers.getConfigurationManagementService()
        then:
        UnsupportedOperationException e = thrown()
        e.getMessage() == "Not supported anymore"
    }
}
