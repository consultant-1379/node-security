package com.ericsson.oss.services.gdpr.anonymize;

import com.ericsson.oss.gdpr.anonymize.api.GdprAnonymizer;
import com.ericsson.oss.gdpr.anonymize.exception.GdprAnonymizerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GdprAnonymizerDelegateImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GdprHashingTest.class);
    private static final String IMEI_IMSI_FILENAME_TEST = "GdprAnonymizerImplTest";
    private static final String IMEI_IMSI_HASHED_FILENAME_TEST = "7xbGsOZSckStgKG2G-v_14eS06lD8gMzha-pkUsZEJc=";
    private static final String ENM_SALT = "enmapachetest";

    @Mock
    private GdprAnonymizer gdprAnonymizerMock;

    @InjectMocks
    private GdprAnonymizerDelegateImpl  gdprAnonymizerDelegateMock;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void gdprBuildAnonymizationDelegateTest() throws GdprAnonymizerException {
        Mockito.when(gdprAnonymizerMock.gdprBuildAnonymization(Mockito.any(String.class))).thenReturn(IMEI_IMSI_HASHED_FILENAME_TEST);
        String hashed = gdprAnonymizerDelegateMock.gdprBuildAnonymization(IMEI_IMSI_FILENAME_TEST);
        final int  hashedLength = 44;

        LOGGER.debug("{}", hashed);

		/* check if hashed value contains zeo or more numbers,letters or characters -=_ */
        boolean hashcheck = (hashed.matches("[a-zA-Z0-9]*") || hashed.matches(".*[-=_].*"));

        assertEquals(hashed.length(), hashedLength);
        assertTrue(hashcheck);
    }

    @Test
    public void gdprBuildAnonymizationWithSaltDelegateTest() throws GdprAnonymizerException {
        Mockito.when(gdprAnonymizerMock.gdprBuildAnonymization(Mockito.any(String.class),Mockito.any(String.class) )).thenReturn(IMEI_IMSI_HASHED_FILENAME_TEST);
        String hashed = gdprAnonymizerDelegateMock.gdprBuildAnonymization(IMEI_IMSI_FILENAME_TEST, ENM_SALT);
        final int  hashedLength = 44;

        LOGGER.debug("{}", hashed);

		/* check if hashed value contains zeo or more numbers,letters or characters -=_ */
        boolean hashcheck = (hashed.matches("[a-zA-Z0-9]*") || hashed.matches(".*[-=_].*"));

        assertEquals(hashed.length(), hashedLength);
        assertTrue(hashcheck);
    }

}