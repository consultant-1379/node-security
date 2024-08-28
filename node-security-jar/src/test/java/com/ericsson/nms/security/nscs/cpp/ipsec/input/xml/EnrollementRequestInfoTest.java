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
package com.ericsson.nms.security.nscs.cpp.ipsec.input.xml;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.spockframework.util.Assert;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.EnrollmentRequestInfo;

public class EnrollementRequestInfoTest {

    EnrollmentRequestInfo other;
    EnrollmentRequestInfo other1;

    @Before
    public void setUp() {
        other = new EnrollmentRequestInfo();
        other.setCertType("OAM");
        other.setCommonName("OtherCommonName");
        other.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other.setEntityProfile("Sample_EP");
        other.setKeySize("RSA_2048");
        other.setNodeIdentifier(null);
        other.setSubjectAltNameParam(null);
    }

    @Test
    public void testHashCode() {
        other.hashCode();
        Assert.notNull(other.toString());
        assertFalse(other.equals(other1));
        other1 = new EnrollmentRequestInfo();
        other1.setCertType("OAM");
        other1.setCommonName("OtherCommonName");
        other1.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other1.setEntityProfile("Sample_EP123");
        other1.setKeySize("RSA_4096");
        other1.setNodeIdentifier(null);
        other1.setSubjectAltNameParam(null);
        assertFalse(other.equals(other1));

        other.setCertType("OAM");
        other.setCommonName("OtherCommonName");
        other.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other.setEntityProfile(null);
        other.setKeySize("RSA_2048");
        other.setNodeIdentifier(null);
        other.setSubjectAltNameParam(null);
        assertFalse(other.equals(other1));

        other1.setEntityProfile("Sample_EP");
        other1.setKeySize("RSA_4096");
        assertFalse(other.equals(other1));

        other.setCertType("OAM");
        other.setCommonName("OtherCommonName");
        other.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other.setEntityProfile("Sample_EP");
        other.setKeySize(null);
        other.setNodeIdentifier(null);
        other.setSubjectAltNameParam(null);
        assertFalse(other.equals(other1));

        other.setCertType(null);
        other.setCommonName("OtherCommonName");
        other.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other.setEntityProfile("Sample_EP");
        other.setKeySize("RSA_2048");
        other.setNodeIdentifier(null);
        other.setSubjectAltNameParam(null);
        assertFalse(other.equals(other1));

        other1.setCertType("OAM");
        other1.setCommonName(null);
        other1.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other1.setEntityProfile("Sample_EP");
        other1.setKeySize("RSA_4096");
        other1.setNodeIdentifier(null);
        other1.setSubjectAltNameParam(null);
        assertFalse(other.equals(other1));

        other.setCertType("OAM");
        other.setCommonName("OtherCommonName");
        other.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other.setEntityProfile("Sample_EP");
        other.setKeySize("RSA_2048");
        other.setNodeIdentifier(null);
        other.setSubjectAltNameParam(null);

        other1.setCertType("OAM");
        other1.setCommonName("OtherCommonName");
        other1.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other1.setEntityProfile("Sample_EP");
        other1.setKeySize("RSA_4096");
        other1.setNodeIdentifier(null);
        other1.setSubjectAltNameParam(null);
        assertFalse(other.equals(other1));

        final NodeIdentifier nodeIdentifier = new NodeIdentifier("nodeFdn", null);
        other.setNodeIdentifier(nodeIdentifier);
        assertFalse(other.equals(other1));

        other1.setCertType("OAM");
        other1.setCommonName(null);
        other1.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other1.setEntityProfile("Sample_EP");
        other1.setKeySize("RSA_4096");
        other1.setNodeIdentifier(null);
        other1.setSubjectAltNameParam(null);
        assertFalse(other.equals(other1));

        other.setCertType("OAM");
        other.setCommonName(null);
        other.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other.setEntityProfile("Sample_EP");
        other.setKeySize("RSA_4096");
        other.setNodeIdentifier(null);
        other.setSubjectAltNameParam(null);

        other1.setCertType("OAM");
        other1.setCommonName("OtherCommonName");
        other1.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other1.setEntityProfile("Sample_EP");
        other1.setKeySize("RSA_4096");
        other1.setNodeIdentifier(null);
        other1.setSubjectAltNameParam(null);
        assertFalse(other.equals(other1));

        other.setCertType("OAM");
        other.setCommonName("CommonName");
        other.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other.setEntityProfile("Sample_EP");
        other.setKeySize("RSA_4096");
        other.setNodeIdentifier(null);
        other.setSubjectAltNameParam(null);

        other1.setCertType("OAM");
        other1.setCommonName("OtherCommonName");
        other1.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other1.setEntityProfile("Sample_EP");
        other1.setKeySize("RSA_4096");
        other1.setNodeIdentifier(null);
        other1.setSubjectAltNameParam(null);
        assertFalse(other.equals(other1));

        other.setCertType("OAM");
        other.setCommonName("OtherCommonName");
        other.setEnrollmentMode(null);
        other.setEntityProfile("Sample_EP");
        other.setKeySize("RSA_4096");
        other.setNodeIdentifier(null);
        other.setSubjectAltNameParam(null);

        other1.setCertType("OAM");
        other1.setCommonName("OtherCommonName");
        other1.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other1.setEntityProfile("Sample_EP");
        other1.setKeySize("RSA_4096");
        other1.setNodeIdentifier(null);
        other1.setSubjectAltNameParam(null);
        assertFalse(other.equals(other1));

        other.setEnrollmentMode(EnrollmentMode.CMPv2_UPDATE);
        assertFalse(other.equals(other1));

        String subjectAltName = "192.12.12.12";
        SubjectAltNameFormat subjectAltNameFormat = SubjectAltNameFormat.IPV4;
        SubjectAltNameParam subjectAltNameParam = null;
        if (subjectAltName != null && subjectAltNameFormat != null) {
            final SubjectAltNameStringType subjectAltNameString = new SubjectAltNameStringType(subjectAltName);
            subjectAltNameParam = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameString);
        }

        other.setCertType("OAM");
        other.setCommonName("OtherCommonName");
        other.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other.setEntityProfile("Sample_EP");
        other.setKeySize("RSA_4096");
        other.setNodeIdentifier(null);
        other.setSubjectAltNameParam(null);

        other1.setCertType("OAM");
        other1.setCommonName("OtherCommonName");
        other1.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other1.setEntityProfile("Sample_EP");
        other1.setKeySize("RSA_4096");
        other1.setNodeIdentifier(null);
        other1.setSubjectAltNameParam(subjectAltNameParam);
        assertFalse(other.equals(other1));

        final SubjectAltNameStringType subjectAltNameString1 = new SubjectAltNameStringType("192.12.12.1");
        SubjectAltNameParam subjectAltNameParam1 = new SubjectAltNameParam(subjectAltNameFormat, subjectAltNameString1);
        other.setCertType("OAM");
        other.setCommonName("OtherCommonName");
        other.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other.setEntityProfile("Sample_EP");
        other.setKeySize("RSA_4096");
        other.setNodeIdentifier(null);
        other.setSubjectAltNameParam(subjectAltNameParam1);

        other1.setCertType("OAM");
        other1.setCommonName("OtherCommonName");
        other1.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other1.setEntityProfile("Sample_EP");
        other1.setKeySize("RSA_4096");
        other1.setNodeIdentifier(null);
        other1.setSubjectAltNameParam(subjectAltNameParam);
        assertFalse(other.equals(other1));
        
        other.setCertType("OAM");
        other.setNodeName(null);
        other.setCommonName("OtherCommonName");
        other.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other.setEntityProfile("Sample_EP");
        other.setKeySize("RSA_4096");
        other.setNodeIdentifier(null);
        other.setSubjectAltNameParam(subjectAltNameParam);

        other1.setCertType("OAM");
        other1.setNodeName("nodeFdn");
        other1.setCommonName("OtherCommonName");
        other1.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other1.setEntityProfile("Sample_EP");
        other1.setKeySize("RSA_4096");
        other1.setNodeIdentifier(null);
        other1.setSubjectAltNameParam(subjectAltNameParam);
        assertFalse(other.equals(other1));

        other.setCertType("OAM");
        other.setNodeName("NodeFDn");
        other.setCommonName("OtherCommonName");
        other.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other.setEntityProfile("Sample_EP");
        other.setKeySize("RSA_4096");
        other.setNodeIdentifier(null);
        other.setSubjectAltNameParam(subjectAltNameParam);

        other1.setCertType("OAM");
        other.setNodeName("NodeFDns");
        other1.setCommonName("OtherCommonName");
        other1.setEnrollmentMode(EnrollmentMode.CMPv2_INITIAL);
        other1.setEntityProfile("Sample_EP");
        other1.setKeySize("RSA_4096");
        other1.setNodeIdentifier(null);
        other1.setSubjectAltNameParam(subjectAltNameParam);
        assertFalse(other.equals(other1));

    }

}
