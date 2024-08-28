/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.iscf;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.IscfServiceException;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;

/**
 *
 * @author enmadmin
 */
@RunWith(MockitoJUnitRunner.class)
public class IscfServiceValidatorsTest {

    @Mock
    Logger log;

    @Mock
    private NscsCapabilityModelService capabilityService;

    @InjectMocks
    private IscfServiceValidators iscfServiceValidators;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final static SubjectAltNameStringType subjectAltNameStringIpv4Valid = new SubjectAltNameStringType("127.0.0.1");
    private final static SubjectAltNameStringType subjectAltNameStringIpv4NotValid1 = new SubjectAltNameStringType("127.0.0.1.2");
    private final static SubjectAltNameStringType subjectAltNameStringIpv4NotValid2 = new SubjectAltNameStringType("127.0.0.257");
    private final static SubjectAltNameStringType subjectAltNameStringIpv6Valid1 = new SubjectAltNameStringType("2001:1b70:82a1:103::64:22b");
    private final static SubjectAltNameStringType subjectAltNameStringIpv6Valid2 = new SubjectAltNameStringType("1080::800:200C:417A");
    private final static SubjectAltNameStringType subjectAltNameStringIpv6NotValid1 = new SubjectAltNameStringType("1080:0:0:0:8:800:200C:417A:1");
    private final static SubjectAltNameStringType subjectAltNameStringIpv6NotValid2 = new SubjectAltNameStringType("1080:0:0:8:800:200C:417A");
    private final static SubjectAltNameStringType subjectAltNameStringIpv6NotValid3 = new SubjectAltNameStringType("1080::800:200C:ABGH");
    private final static SubjectAltNameParam subjectAltNameParamIpv4Valid = new SubjectAltNameParam(SubjectAltNameFormat.IPV4,
            subjectAltNameStringIpv4Valid);
    private final static SubjectAltNameParam subjectAltNameParamIpv6Valid = new SubjectAltNameParam(SubjectAltNameFormat.IPV6,
            subjectAltNameStringIpv6Valid1);
    private final static String exceptionMessageIpv4 = "not valid IPv4 address";
    private final static String exceptionMessageIpv6 = "not valid IPv6 address";
    private final static String nodeFdn = "VALID_NODE";
    private final static String logicalName = "LOGICAL";
    private final static NodeModelInformation DUSG_MODEL_INFO = new NodeModelInformation("CXP123/45-R67",
            NodeModelInformation.ModelIdentifierType.PRODUCT_NUMBER, "RadioNode");
    private final static NodeModelInformation CPP_15B_MODEL_INFO = new NodeModelInformation("5.1.63",
            NodeModelInformation.ModelIdentifierType.MIM_VERSION, "ERBS");

    @Before
    public void setup() {
        final List<String> supportedSecurityLevels = new ArrayList<>();
        final List<String> supportedEnrollmentModes = new ArrayList<>();
        supportedSecurityLevels.add(SecurityLevel.LEVEL_1.name());
        supportedSecurityLevels.add(SecurityLevel.LEVEL_2.name());
        supportedEnrollmentModes.add(EnrollmentMode.SCEP.toString());
        supportedEnrollmentModes.add(EnrollmentMode.CMPv2_VC.toString());
        when(capabilityService.isTargetTypeSupported(Mockito.eq(TargetTypeInformation.CATEGORY_NODE), anyString())).thenReturn(true);
        when(capabilityService.isCertificateManagementSupported(Mockito.any(NodeModelInformation.class))).thenReturn(true);
        when(capabilityService.getSupportedEnrollmentModes(Mockito.any(NodeModelInformation.class))).thenReturn(supportedEnrollmentModes);
        when(capabilityService.getSupportedSecurityLevels(Mockito.any(NodeModelInformation.class))).thenReturn(supportedSecurityLevels);
    }

    /**
     * Test of validateGenerateOam method, of class IscfServiceValidators.
     */
    @Test
    public void testValidateGenerateOam() {
        final SecurityLevel wantedSecLevel = SecurityLevel.LEVEL_2;
        final SecurityLevel minimumSecLevel = SecurityLevel.LEVEL_1;
        try {
            iscfServiceValidators.validateGenerateOam(logicalName, nodeFdn, wantedSecLevel, minimumSecLevel, EnrollmentMode.SCEP, CPP_15B_MODEL_INFO);
        } catch (final IscfServiceException ex) {
            fail("Unexpected IscfServiceException: " + ex);
        }
    }

    /**
     * Test of validateGenerateSecurityDataOam method, of class IscfServiceValidators.
     */
    @Test
    public void testValidateGenerateSecurityDataOam() {
        try {
            iscfServiceValidators.validateGenerateSecurityDataOam(nodeFdn, EnrollmentMode.SCEP, DUSG_MODEL_INFO);
        } catch (final IscfServiceException ex) {
            fail("Unexpected IscfServiceException: " + ex);
        }
    }

    /**
     * Test of validateGenerateIpsec method, of class IscfServiceValidators.
     */
    @Test
    @Ignore
    public void testValidateGenerateIpsec() {
        final String ipsecUserLabel = "USER_LABEL";
        final SubjectAltNameParam subjectAltNameParam = null;
        final Set<IpsecArea> wantedIpSecAreas = null;
        try {
            iscfServiceValidators.validateGenerateIpsec(logicalName, nodeFdn, ipsecUserLabel, subjectAltNameParam, wantedIpSecAreas,
                    EnrollmentMode.SCEP, CPP_15B_MODEL_INFO);
        } catch (final IscfServiceException ex) {
            fail("Unexpected IscfServiceException: " + ex);
        }
    }

    /**
     * Test of validateGenerateSecurityDataIpsec method, of class IscfServiceValidators.
     */
    @Test
    public void testValidateGenerateSecurityDataIpsec_Ipv4Valid() {
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        try {
            iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeFdn, subjectAltNameParamIpv4Valid, wantedEnrollmentMode, DUSG_MODEL_INFO);
        } catch (final IscfServiceException ex) {
            fail("Unexpected IscfServiceException: " + ex);
        }
    }

    /**
     * Test of validateGenerateSecurityDataIpsec method, of class IscfServiceValidators.
     */
    @Test
    public void testValidateGenerateSecurityDataIpsec_Ipv4NotValid() {
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4, subjectAltNameStringIpv6Valid1);
        exception.expect(IscfServiceException.class);
        exception.expectMessage(exceptionMessageIpv4);
        iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeFdn, subjectAltNameParam, wantedEnrollmentMode, DUSG_MODEL_INFO);
    }

    @Test
    public void testValidateGenerateSecurityDataIpsec_Ipv4NotValid1() {
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4, subjectAltNameStringIpv4NotValid1);
        exception.expect(IscfServiceException.class);
        exception.expectMessage(exceptionMessageIpv4);
        iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeFdn, subjectAltNameParam, wantedEnrollmentMode, DUSG_MODEL_INFO);
    }

    @Test
    public void testValidateGenerateSecurityDataIpsec_Ipv4NotValid2() {
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4, subjectAltNameStringIpv4NotValid2);
        exception.expect(IscfServiceException.class);
        exception.expectMessage(exceptionMessageIpv4);
        iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeFdn, subjectAltNameParam, wantedEnrollmentMode, DUSG_MODEL_INFO);
    }

    /**
     * Test of validateGenerateSecurityDataIpsec method, of class IscfServiceValidators.
     */
    @Test
    public void testValidateGenerateSecurityDataIpsec_Ipv6Valid1() {
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        try {
            iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeFdn, subjectAltNameParamIpv6Valid, wantedEnrollmentMode, DUSG_MODEL_INFO);
        } catch (final IscfServiceException ex) {
            fail("Unexpected IscfServiceException: " + ex);
        }
    }

    /**
     * Test of validateGenerateSecurityDataIpsec method, of class IscfServiceValidators.
     */
    @Test
    public void testValidateGenerateSecurityDataIpsec_Ipv6Valid2() {
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV6, subjectAltNameStringIpv6Valid2);
        try {
            iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeFdn, subjectAltNameParam, wantedEnrollmentMode, DUSG_MODEL_INFO);
        } catch (final IscfServiceException ex) {
            fail("Unexpected IscfServiceException: " + ex);
        }
    }

    /**
     * Test of validateGenerateSecurityDataIpsec method, of class IscfServiceValidators.
     */
    @Test
    public void testValidateGenerateSecurityDataIpsec_Ipv6NotValid() {
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV6, subjectAltNameStringIpv4Valid);
        exception.expect(IscfServiceException.class);
        exception.expectMessage(exceptionMessageIpv6);
        iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeFdn, subjectAltNameParam, wantedEnrollmentMode, DUSG_MODEL_INFO);
    }

    /**
     * Test of validateGenerateSecurityDataIpsec method, of class IscfServiceValidators.
     */
    @Test
    public void testValidateGenerateSecurityDataIpsec_Ipv6NotValid1() {
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV6, subjectAltNameStringIpv6NotValid1);
        exception.expect(IscfServiceException.class);
        exception.expectMessage(exceptionMessageIpv6);
        iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeFdn, subjectAltNameParam, wantedEnrollmentMode, DUSG_MODEL_INFO);
    }

    /**
     * Test of validateGenerateSecurityDataIpsec method, of class IscfServiceValidators.
     */
    @Test
    public void testValidateGenerateSecurityDataIpsec_Ipv6NotValid2() {
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV6, subjectAltNameStringIpv6NotValid2);
        exception.expect(IscfServiceException.class);
        exception.expectMessage(exceptionMessageIpv6);
        iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeFdn, subjectAltNameParam, wantedEnrollmentMode, DUSG_MODEL_INFO);
    }

    /**
     * Test of validateGenerateSecurityDataIpsec method, of class IscfServiceValidators.
     */
    @Test
    public void testValidateGenerateSecurityDataIpsec_Ipv6NotValid3() {
        final EnrollmentMode wantedEnrollmentMode = EnrollmentMode.SCEP;
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV6, subjectAltNameStringIpv6NotValid3);
        exception.expect(IscfServiceException.class);
        exception.expectMessage(exceptionMessageIpv6);
        iscfServiceValidators.validateGenerateSecurityDataIpsec(nodeFdn, subjectAltNameParam, wantedEnrollmentMode, DUSG_MODEL_INFO);
    }

    /**
     * Test of validateGenerateCombo method, of class IscfServiceValidators.
     */
    @Test
    @Ignore
    public void testValidateGenerateCombo() {
        final SecurityLevel wantedSecLevel = null;
        final SecurityLevel minimumSecLevel = null;
        final String ipsecUserLabel = "";
        final SubjectAltNameParam subjectAltNameParam = null;
        final Set<IpsecArea> wantedIpSecAreas = null;
        final EnrollmentMode wantedEnrollmentMode = null;
        final NodeModelInformation modelInfo = null;
        iscfServiceValidators.validateGenerateCombo(logicalName, nodeFdn, wantedSecLevel, minimumSecLevel, ipsecUserLabel, subjectAltNameParam,
                wantedIpSecAreas, wantedEnrollmentMode, modelInfo);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of validateGenerateSecurityDataCombo method, of class IscfServiceValidators.
     */
    @Test
    @Ignore
    public void testValidateGenerateSecurityDataCombo() {
        final SubjectAltNameParam subjectAltNameParam = null;
        final EnrollmentMode wantedEnrollmentMode = null;
        final NodeModelInformation modelInfo = null;
        iscfServiceValidators.validateGenerateSecurityDataCombo(nodeFdn, subjectAltNameParam, wantedEnrollmentMode, modelInfo);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
