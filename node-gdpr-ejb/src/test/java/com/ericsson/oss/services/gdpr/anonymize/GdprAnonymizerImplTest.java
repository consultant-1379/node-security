package com.ericsson.oss.services.gdpr.anonymize;

import com.ericsson.nms.security.nscs.ldap.utility.PlatformConfigurationReader;
import com.ericsson.oss.gdpr.anonymize.exception.GdprAnonymizerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GdprAnonymizerImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GdprHashingTest.class);
    private static final String IMEI_IMSI_FILENAME_TEST = "GdprAnonymizerImplTest";
    private static final String ENM_SALT = "enmapachetest";
    private String url;

    @Mock
    private PlatformConfigurationReader platformConfigurationReaderMock;

    @Spy
    private GdprHashing gdprHashingMock = new GdprHashing();

    @InjectMocks
    private GdprAnonymizerImpl gdprAnonymizerMock;

    @Before
    public void setUp() throws Exception {
        url = "enmapache.athtem.eei.ericsson.se";
    }


    @Test
    public void gdprBuildAnonymizationTest() throws GdprAnonymizerException {
        final int  hashedLength = 44;

        Mockito.when(platformConfigurationReaderMock.getProperty(Mockito.any(String.class))).thenReturn(url);
        String hashed = gdprAnonymizerMock.gdprBuildAnonymization(IMEI_IMSI_FILENAME_TEST);
        LOGGER.debug("{}", hashed);

		/* check if hashed value contains zeo or more numbers,letters or characters -=_ */
        boolean hashcheck = (hashed.matches("[a-zA-Z0-9]*") || hashed.matches(".*[-=_].*"));

        assertEquals(hashed.length(), hashedLength);
        assertTrue(hashcheck);
    }

    @Test
    public void gdprBuildAnonymizationWithSaltTest() throws GdprAnonymizerException {
        final int  hashedLength = 44;

        String hashed = gdprAnonymizerMock.gdprBuildAnonymization(IMEI_IMSI_FILENAME_TEST, ENM_SALT);
        LOGGER.debug("{}", hashed);

		/* check if hashed value contains zeo or more numbers,letters or characters -=_ */
        boolean hashcheck = (hashed.matches("[a-zA-Z0-9]*") || hashed.matches(".*[-=_].*"));

        assertEquals(hashed.length(), hashedLength);
        assertTrue(hashcheck);
    }

    @Test(expected = GdprAnonymizerException.class)
    public void gdprBuildAnonymizationInvalidInputParms() throws NoSuchAlgorithmException {
        Mockito.when(platformConfigurationReaderMock.getProperty(Mockito.any(String.class))).thenReturn(url);
        gdprAnonymizerMock.gdprBuildAnonymization(null);
    }

    @Test(expected = GdprAnonymizerException.class)
    public void gdprBuildAnonymizationNullSalt() throws NoSuchAlgorithmException {
        Mockito.when(platformConfigurationReaderMock.getProperty(Mockito.any(String.class))).thenReturn(null);
        gdprAnonymizerMock.gdprBuildAnonymization(IMEI_IMSI_FILENAME_TEST);
    }

    @Test(expected = GdprAnonymizerException.class)
    public void gdprBuildAnonymizationSaltFormatError() throws NoSuchAlgorithmException {
        String urlFormatErrror = "urlwithoutcommacharacter";

        Mockito.when(platformConfigurationReaderMock.getProperty(Mockito.any(String.class))).thenReturn(urlFormatErrror);
        gdprAnonymizerMock.gdprBuildAnonymization(IMEI_IMSI_FILENAME_TEST);
    }

    @Test(expected = GdprAnonymizerException.class)
    public void gdprBuildAnonymizationWithSaltInvalidInputParms() throws NoSuchAlgorithmException {
        Mockito.when(platformConfigurationReaderMock.getProperty(Mockito.any(String.class))).thenReturn(url);
        gdprAnonymizerMock.gdprBuildAnonymization(null, ENM_SALT);
    }

    @Test(expected = GdprAnonymizerException.class)
    public void gdprBuildAnonymizationNoAlgo() throws NoSuchAlgorithmException {
        Mockito.when(platformConfigurationReaderMock.getProperty(Mockito.any(String.class))).thenReturn(url);
        final NoSuchAlgorithmException e = Mockito.mock(NoSuchAlgorithmException.class);

        Mockito.when(gdprAnonymizerMock.gdprBuildAnonymization(IMEI_IMSI_FILENAME_TEST)).thenThrow(e);
        gdprAnonymizerMock.gdprBuildAnonymization(IMEI_IMSI_FILENAME_TEST);
    }

    @Test(expected = GdprAnonymizerException.class)
    public void gdprBuildAnonymizationWithSaltNoAlgo() throws NoSuchAlgorithmException {
        final NoSuchAlgorithmException e = Mockito.mock(NoSuchAlgorithmException.class);

        Mockito.when(gdprAnonymizerMock.gdprBuildAnonymization(IMEI_IMSI_FILENAME_TEST, ENM_SALT)).thenThrow(e);
        gdprAnonymizerMock.gdprBuildAnonymization(IMEI_IMSI_FILENAME_TEST,ENM_SALT);
    }
}