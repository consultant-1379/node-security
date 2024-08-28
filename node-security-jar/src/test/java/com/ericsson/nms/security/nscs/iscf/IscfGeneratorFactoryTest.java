/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.iscf;

import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;

/**
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class IscfGeneratorFactoryTest {

	private final static String CPP_NODE_TYPE = "ERBS";
	private final static String CPP_MIM_VERSION = "E.1.49";
	private final static NodeModelInformation modelInfo = new NodeModelInformation(CPP_MIM_VERSION, ModelIdentifierType.MIM_VERSION, CPP_NODE_TYPE);
        private final static SubjectAltNameStringType subjectAltNameString = 
                new SubjectAltNameStringType("");
        private final static SubjectAltNameParam subjectAltNameParam  = 
            new SubjectAltNameParam(SubjectAltNameFormat.NONE, subjectAltNameString);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(IscfGeneratorFactory.class);

    @Mock
    SecurityLevelIscfGenerator secLevelGenerator;

    @Mock
    IpsecIscfGenerator ipsecGenerator;

    @Mock
    CombinedIscfGenerator combinedGenerator;

    @InjectMocks
    IscfGeneratorFactory beanUnderTest;

    @Test
    public void testGetSecLevelGenerator() {
        beanUnderTest.getSecLevelGenerator("", "", SecurityLevel.LEVEL_1, SecurityLevel.LEVEL_1, EnrollmentMode.SCEP, modelInfo);
        verify(secLevelGenerator).initGenerator(SecurityLevel.LEVEL_1, SecurityLevel.LEVEL_1, "", "", EnrollmentMode.SCEP, modelInfo);
    }

    @Test
    public void testGetIpsecGenerator() {
        beanUnderTest.getIpsecGenerator("", "", "", subjectAltNameParam, 
                null, EnrollmentMode.SCEP, modelInfo);
        verify(ipsecGenerator).initGenerator("", "", "", subjectAltNameParam, null, 
                EnrollmentMode.SCEP, modelInfo);
    }

    @Test
    public void testGetCombinedGenerator() {
        beanUnderTest.getCombinedGenerator("", "", SecurityLevel.LEVEL_1, SecurityLevel.LEVEL_1,
                "", subjectAltNameParam, null, EnrollmentMode.SCEP, modelInfo);
        verify(combinedGenerator).initGenerator(SecurityLevel.LEVEL_1, SecurityLevel.LEVEL_1,
                 "", "", "", subjectAltNameParam, null, EnrollmentMode.SCEP, modelInfo);
    }

}
