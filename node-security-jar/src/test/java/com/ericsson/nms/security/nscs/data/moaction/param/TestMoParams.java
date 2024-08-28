/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.data.moaction.param;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class TestMoParams {

    /*
     * In the scope of FOSS activities, the com.google.guava.guava library has been removed from node-security EAR. In the MoParam class, the
     * com.google.common.base.Joiner has been replaced with java.util.stream.Collectors.joining class. The following constants contains the
     * stringified results generated with the guava Joiner and they are used to verify the backward-compatibility of the correspondent stringified
     * results generated with the java class.
     */
    private static final String GUAVA_CERT_ENROLLMENT_PARAMS = "{data:{challengePassword:{*****},"
            + "caFingerPrint:{caFingerPrint}," + "distinguishedName:{distinguishedName}," + "rollbackTimeOut:{60},"
            + "enrollmentServerURL:{enrollmentServerURL}}}";
    private static final String GUAVA_TRUST_INSTALL_PARAMS = "{duration:{0}," + "startTime:{0},"
            + "accountInfoList:{["
            + "{password:{*****},remoteHost:{dummyHost0},userID:{dummyUser0}}, "
            + "{password:{*****},remoteHost:{dummyHost1},userID:{dummyUser1}}]},"
            + "certSpecList:{["
            + "{fileName:{abc.cer0},serialNumber:{dummySerial0},fingerprint:{dummyFingerprint0},category:{CORBA_PEERS}}, "
            + "{fileName:{abc.cer1},serialNumber:{dummySerial1},fingerprint:{dummyFingerprint1},category:{CORBA_PEERS}}, "
            + "{fileName:{abc.cer2},serialNumber:{dummySerial2},fingerprint:{dummyFingerprint2},category:{CORBA_PEERS}}, "
            + "{fileName:{abc.cer3},serialNumber:{dummySerial3},fingerprint:{dummyFingerprint3},category:{CORBA_PEERS}}, "
            + "{fileName:{abc.cer4},serialNumber:{dummySerial4},fingerprint:{dummyFingerprint4},category:{CORBA_PEERS}}]}}";

    @Spy
    private final Logger log = LoggerFactory.getLogger(TestMoParams.class);

    @Test
    public void testParamCertEnrollment() {
        //GET params
        final MoParams params = getInitCertEnrollmentParams();
        assertEquals(MoParam.ParamType.MAP, params.getParamType());
        assertEquals(1, params.getParamMap().entrySet().size());
        assertEquals(GUAVA_CERT_ENROLLMENT_PARAMS, params.toString());

        //GET "data"		
        final Map<String, MoParam> data = MoParams.getParamMap(params.getParamMap().get("data"));
        assertEquals(5, data.entrySet().size());

        //GET each param		
        assertEquals("caFingerPrint", data.get("caFingerPrint").getParam());

        final MoParam pw = data.get("challengePassword");
        assertEquals("challengePassword", String.valueOf((char[]) pw.getParam()));//value
        assertEquals("{*****}", pw.toString());//toString

        assertEquals("distinguishedName", data.get("distinguishedName").getParam());
        assertEquals("enrollmentServerURL", data.get("enrollmentServerURL").getParam());
        assertEquals(60, data.get("rollbackTimeOut").getParam());
    }

    @Test
    public void testParamInstallTrustedCerts() {
        //GET params
        final MoParams params = getInstallTrustedCertsParams();
        assertEquals(MoParam.ParamType.MAP, params.getParamType());
        assertEquals(4, params.getParamMap().entrySet().size());
        assertEquals(GUAVA_TRUST_INSTALL_PARAMS, params.toString());

        //Get "data"
        final Map<String, MoParam> data = params.getParamMap();
        assertEquals(4, data.entrySet().size());

        //GET each simple param		
        assertEquals("0", data.get("startTime").getParam());
        assertEquals("0", data.get("duration").getParam());

        //GET certSpecList
        final MoParam cl = data.get("certSpecList");
        assertEquals(MoParam.ParamType.LIST, cl.getParamType());

        final List<MoParam> certList = MoParam.getList(cl);
        assertEquals(5, certList.size());

        final MoParam cert0 = certList.get(0);
        assertEquals(MoParam.ParamType.MAP, cert0.getParamType());

        final Map<String, MoParam> cert0data = MoParams.getParamMap(cert0);
        assertEquals("CORBA_PEERS", cert0data.get("category").getParam());

        //GET accountInfoList
        final MoParam accountInfoList = data.get("accountInfoList");
        assertEquals(MoParam.ParamType.LIST, accountInfoList.getParamType());
    }

    @Test
    public void testNotContainsNotDefined() {
        assertFalse(getInitCertEnrollmentParams().toString().contains("NotDefined"));
        assertFalse(getInstallTrustedCertsParams().toString().contains("NotDefined"));
    }

    @Test
    public void testParamCertEnrollmentString() {
        assertEquals(GUAVA_CERT_ENROLLMENT_PARAMS, getInitCertEnrollmentParams().toString());
    }

    @Test
    public void testParamTrustedCertsString() {
        assertEquals(GUAVA_TRUST_INSTALL_PARAMS, getInstallTrustedCertsParams().toString());
    }

    public static MoParams getInitCertEnrollmentParams() {
        final MoParams params = new MoParams();
        final MoParams data = new MoParams();
        data.addParam("caFingerPrint", "caFingerPrint");
        data.addParam("challengePassword", "challengePassword".toCharArray(), true);
        data.addParam("distinguishedName", "distinguishedName");
        data.addParam("enrollmentServerURL", "enrollmentServerURL");
        data.addParam("rollbackTimeOut", 60);
        params.addParam("data", data);
        return params;
    }

    public static MoParams getInstallTrustedCertsParams() {
        final MoParams params = new MoParams();
        //certSpecList
        final List<MoParams> certSpecList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final MoParams certSpec = new MoParams();
            certSpec.addParam("category", "CORBA_PEERS");
            certSpec.addParam("fileName", "abc.cer" + i);
            certSpec.addParam("fingerprint", "dummyFingerprint" + i);
            certSpec.addParam("serialNumber", "dummySerial" + i);
            certSpecList.add(certSpec);
        }
        params.addParam("certSpecList", certSpecList);
        //startTime
        params.addParam("startTime", "0");
        //duration
        params.addParam("duration", "0");
        //accountInfoList
        final List<MoParams> accountInfoList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            final MoParams accountInfo = new MoParams();
            accountInfo.addParam("password", ("dummyPassword" + i).toCharArray(), true);
            accountInfo.addParam("remoteHost", "dummyHost" + i);
            accountInfo.addParam("userID", "dummyUser" + i);
            accountInfoList.add(accountInfo);
        }
        params.addParam("accountInfoList", accountInfoList);
        return params;
    }
}
