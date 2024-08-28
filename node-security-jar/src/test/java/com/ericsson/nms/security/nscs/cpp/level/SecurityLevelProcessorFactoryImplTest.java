package com.ericsson.nms.security.nscs.cpp.level;

/**
 * 
 */

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.cpp.model.TrustStoreInfo;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * Tests the SecLevelProcessor, Factory and implementation that starts a security level activation/deactivation for a list of nodes
 * 
 * @see SecurityLevelProcessorFactory
 * @see SecurityLevelProcessorFactoryImpl
 * 
 * @author eabdsin
 */
@RunWith(MockitoJUnitRunner.class)
public class SecurityLevelProcessorFactoryImplTest {

    @Spy
    private final Logger logger = LoggerFactory.getLogger(SecurityLevelProcessorFactoryImpl.class);

    @Mock
    SecLevelProcessor deactivateStatusCommandHandler;

    @Mock
    SecLevelProcessor activateStatusCommandHandler;

    @InjectMocks
    private SecurityLevelProcessorFactoryImpl beanUnderTest;

    @Test
    public void testCreateSecLevelProcessor() throws Exception {

        final SecLevelRequest cmd = new SecLevelRequest();
        cmd.setSecLevelRequestType(SecLevelRequestType.ACTIVATE_SECURITY_LEVEL);
        SecLevelProcessor secLevelProcessor = beanUnderTest.createSecLevelProcessor(cmd);
        assertTrue(secLevelProcessor == activateStatusCommandHandler);

        cmd.setSecLevelRequestType(SecLevelRequestType.DEACTIVATE_SECURITY_LEVEL);
        secLevelProcessor = beanUnderTest.createSecLevelProcessor(cmd);

        assertTrue(secLevelProcessor == deactivateStatusCommandHandler);

    }

    public TrustStoreInfo getTrustStoreInfo() {
        final List<SmrsAccountInfo> accounts = new ArrayList<>();
        final Set<CertSpec> certSpecs = new HashSet<CertSpec>();
        final TrustStoreInfo trustStoreInfo = new TrustStoreInfo(TrustedCertCategory.CORBA_PEERS, certSpecs, 
                accounts, DigestAlgorithm.SHA1);
        return trustStoreInfo;
    }

}
