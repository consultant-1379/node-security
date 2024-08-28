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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.MockitoAnnotations.initMocks;

import java.net.UnknownHostException;
import java.security.cert.CertificateEncodingException;
import java.util.*;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.IscfServiceException;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import java.io.UnsupportedEncodingException;

/**
 * Class for testing all exceptions thrown by CombinedDataCollector
 * @author ealemca
 */
@RunWith(Parameterized.class)
public class CombinedDataCollectorExceptionsTest extends IscfTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Spy
    private final Logger log = LoggerFactory.getLogger(CombinedDataCollector.class);

    @Spy
    private RicGenerator ricGenerator;

    @Mock
    private CombinedDataCollector collector;

    @Mock
    private CombinedIscfCreator creator;

    @InjectMocks
    private CombinedIscfGenerator beanUnderTest;

    private final String name;
    private final Exception exception;

    public CombinedDataCollectorExceptionsTest(String name, Exception exception) {
        this.name = name;
        this.exception = exception;
    }

    @Parameterized.Parameters
    public static List<Object[]> thrownExceptions() {
        return Arrays.asList(new Object[][] {
            {"ISCFEncryption", new IscfEncryptionException()},
            {"SecurityLevelNotSupportedException", new SecurityLevelNotSupportedException("")},
            {"UnsupportedEncodingException", new UnsupportedEncodingException()},
//            {"PKIManagerException", new PKIManagerException(PKIManagerException.Reason.FAILED_TO_PUBLISH_CERT)},
            {"CppSecurityServiceException", new CppSecurityServiceException("")},
            {"CertificateEncodingException", new CertificateEncodingException()},
            {"UnknownHostException", new UnknownHostException("")}
        });
    }

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doReturn(ISCF_RIC_STRING.getBytes(IscfConstants.UTF8_CHARSET)).when(ricGenerator).generateSalt();
        doReturn(ISCF_RIC_STRING).when(ricGenerator).generateRIC();
    }

    @Test
    public void testAllDataCollectorExceptions() throws Exception {
        log.debug("Test catching exceptions thrown by data collector: [{}]", this.name);
        thrown.expect(IscfServiceException.class);
        log.debug("Throwing " + this.exception.getClass().getSimpleName());
        doThrow(this.exception).when(collector).getNodeAIData(Mockito.any(String.class),
            Mockito.any(String.class),
            Mockito.any(SecurityLevel.class),
            Mockito.any(SecurityLevel.class),
            Mockito.any(String.class),
            Mockito.any(BaseSubjectAltNameDataType.class),
            Mockito.any(SubjectAltNameFormat.class),
            Mockito.any(HashSet.class),
            Mockito.any(EnrollmentMode.class),
            Mockito.any(NodeModelInformation.class),
            Mockito.any(byte[].class));
        beanUnderTest.generate();
    }

}