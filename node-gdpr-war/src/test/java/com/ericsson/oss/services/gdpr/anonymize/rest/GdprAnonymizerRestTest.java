package com.ericsson.oss.services.gdpr.anonymize.rest;


import com.ericsson.oss.gdpr.anonymize.exception.GdprAnonymizerException;
import com.ericsson.oss.services.gdpr.anonymize.GdprAnonymizerDelegate;
import com.ericsson.oss.services.gdpr.anonymize.GdprAnonymizerDelegateImpl;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

@RunWith(MockitoJUnitRunner.class)
public class GdprAnonymizerRestTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GdprAnonymizerRestTest.class);

    private static final String IMEI_IMSI_FILENAME_TEST = "GdprAnonymizerImplTest";
    private static final String ENM_SALT = "enmapachetest";

    @Mock
    private GdprAnonymizerDelegate gdprAnonymizerDelegateMock;

    @InjectMocks
    private GdprAnonymizerRest gdprAnonymizerRestMock;

    private GdprAnonymizerDto gdprAnonymizerDto;

    @Before
    public void setUp() throws Exception {
        gdprAnonymizerDto = new GdprAnonymizerDto();
        gdprAnonymizerDto.setFilename(IMEI_IMSI_FILENAME_TEST);
        gdprAnonymizerDto.setSalt(ENM_SALT);
    }

    @Test
    public void getGdprAnonymizedName() throws Exception {
        final Response noParamResponse = gdprAnonymizerRestMock.getGdprAnonymizedName(gdprAnonymizerDto);
        Mockito.verify(gdprAnonymizerDelegateMock, Mockito.atLeastOnce()).gdprBuildAnonymization(Mockito.any(String.class));
        Assert.assertEquals(Response.status(Response.Status.OK).build().getStatus(), noParamResponse.getStatus());

    }

    @Test
    public void getGdprAnonymizedNameWithSalt() throws Exception {
        final Response noParamResponse = gdprAnonymizerRestMock.getGdprAnonymizedNameWithSalt(gdprAnonymizerDto);
        Mockito.verify(gdprAnonymizerDelegateMock, Mockito.atLeastOnce()).gdprBuildAnonymization(Mockito.any(String.class),Mockito.any(String.class));
        Assert.assertEquals(Response.status(Response.Status.OK).build().getStatus(), noParamResponse.getStatus());
    }

    @Test
    public void getGdprAnonymizedNameNullFilename() throws Exception {
        gdprAnonymizerDto.setFilename(null);
        gdprAnonymizerDto.setSalt(null);

        final Response noParamResponse = gdprAnonymizerRestMock.getGdprAnonymizedName(gdprAnonymizerDto);
        Assert.assertEquals(Response.status(Response.Status.BAD_REQUEST).build().getStatus(), noParamResponse.getStatus());
    }

    @Test
    public void getGdprAnonymizedNameNullSalt() throws Exception {
        gdprAnonymizerDto.setFilename(IMEI_IMSI_FILENAME_TEST);
        gdprAnonymizerDto.setSalt(null);

        final Response noParamResponse = gdprAnonymizerRestMock.getGdprAnonymizedNameWithSalt(gdprAnonymizerDto);
        Assert.assertEquals(Response.status(Response.Status.BAD_REQUEST).build().getStatus(), noParamResponse.getStatus());
    }

    @Test
    public void getGdprAnonymizedNameInternalException() {
        GdprAnonymizerException e = Mockito.mock(GdprAnonymizerException.class);
        Mockito.when(gdprAnonymizerDelegateMock.gdprBuildAnonymization(Mockito.any(String.class))).thenThrow(e);

        final Response noParamResponse = gdprAnonymizerRestMock.getGdprAnonymizedName(gdprAnonymizerDto);
        Assert.assertEquals(Response.status(Response.Status.NOT_ACCEPTABLE).build().getStatus(), noParamResponse.getStatus());
    }

    @Test
    public void getGdprAnonymizedNameWithSaltInternalException() {
        GdprAnonymizerException e = Mockito.mock(GdprAnonymizerException.class);
        Mockito.when(gdprAnonymizerDelegateMock.gdprBuildAnonymization(Mockito.any(String.class), Mockito.any(String.class))).thenThrow(e);

        final Response noParamResponse = gdprAnonymizerRestMock.getGdprAnonymizedNameWithSalt(gdprAnonymizerDto);
        Assert.assertEquals(Response.status(Response.Status.NOT_ACCEPTABLE).build().getStatus(), noParamResponse.getStatus());
    }

    @Test
    public void getGdprAnonymizedNameWithSaltForTest() throws Exception {
    }

}