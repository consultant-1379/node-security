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

import java.math.BigInteger;

import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;

/**
 *
 * @author ealemca
 */
public class IscfTest {
    
    protected final static String XML_HEADER_STR = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    protected final static String ISCF_OPENING_TAG = "</secConfData>";
    protected final static String ISCF_CLOSING_TAG = "</secConfData>";
    protected final static String ISCF_MINIMAL_XML = XML_HEADER_STR + ISCF_OPENING_TAG + ISCF_CLOSING_TAG;
    protected final static String ISCF_IPSEC_TRANSPORT_XML = "type=\"Traffic\"";
    protected final static String ISCF_IPSEC_OAM_XML = "type=\"OAM\"";
    protected final static String ISCF_TEST_FDN = "SubNetwork=ONRMROOTMO,SubNetwork=NWISN1,ManagedElement=NWIE1";
    protected final static String ISCF_TEST_RIC_STR = "TEST_RIC";
    protected final static String ISCF_TEST_LOGICAL_NAME = "NWIE1";
    protected final static String ISCF_RIC_STRING = "testRbsIntegrityCode";
    protected final static String ISCF_TEST_LOGON_URI = "https://localhost:8443/app/resource";
    protected final static String ISCF_TEST_ENROLLMENT_URI = "https://localhost:8443/app/resource";
    protected final static String ISCF_TEST_FINGERPRINT = "SHA-1 Fingerprint=SO:ME:FI:NG:ER:PR:IN:TT";
    protected final static String ISCF_TEST_FINGERPRINT_CONTENT = "the-fingerprint";
    protected final static String ISCF_TEST_ROOT_CA_FINGERPRINT_CONTENT = "the-Root-CA-fingerprint";
    protected final static String ISCF_TEST_TDPS_URL = " http://192.168.0.155:8093/pki-ra-tdps/ca_entity/ENM_Infrastructure_CA/86c9e38d8934968/active/ENM_PKI_Root_CA";
    protected final static String ISCF_TEST_DN = "CN=Ericsson";
    protected final static String ISCF_TEST_NAME = "Ericsson";
    protected final static String ISCF_TEST_CATEGORY = "corbaPeer";
    protected final static String ISCF_TEST_SERIAL_NUMBER = String.valueOf(System.currentTimeMillis());
    protected final static String ISCF_TEST_OTP_STR = "test_one_time_pword";
    protected final static String ISCF_TEST_CERT_STR = "test_certificate_content";
    protected final static String ISCF_TEST_KEYSIZE_STR = "2048";
    protected final static BigInteger ISCF_TEST_ITERATION_COUNT = BigInteger.valueOf(1024L);
    protected final static int ISCF_TEST_KEY_LENGTH_MIN = 0;
    protected final static int ISCF_TEST_KEY_LENGTH_MAX = 1;
    protected final static int ISCF_TEST_ENROLLMENT_TIME_MIN = 120;
    protected final static int ISCF_TEST_ENROLLMENT_TIME_MAX = 1800;
    protected final static int ISCT_TEST_ROLLBACK_TIMEOUT = 10;
    protected static final String ISCF_TEST_CATEGORY_CORBA = "corbaPeer";
    protected static final String ISCF_TEST_CATEGORY_IPSEC = "ipsecPeer";
    protected static final String ISCF_TEST_SUBJECT_ALT_NAME = "testSubjectAltName";
    protected static final String ISCF_TEST_IPV4_SUBJECT_ALT_NAME = "100.100.100.100";
    protected static final String ISCF_TEST_IPV6_SUBJECT_ALT_NAME = "2001:cdba:0000:0000:0000:0000:3257:9652";
    protected static final String ISCF_TEST_USER_LABEL = "testUserLabel";
    protected final static String ISCF_CPP_NODE_TYPE = "ERBS";
    protected final static String ISCF_CPP_MIM_VERSION = "E.1.63";
    protected final static NodeModelInformation ISCF_CPP_MODEL_INFO = new NodeModelInformation(ISCF_CPP_MIM_VERSION, ModelIdentifierType.MIM_VERSION, ISCF_CPP_NODE_TYPE);
    protected final static String ISCF_COMECIM_NODE_TYPE = "RadioNode";
    protected final static String ISCF_COMECIM_MIM_VERSION = "E.1.1";
    protected final static NodeModelInformation ISCF_COMECIM_MODEL_INFO = new NodeModelInformation(ISCF_COMECIM_MIM_VERSION, ModelIdentifierType.MIM_VERSION, ISCF_COMECIM_NODE_TYPE);
    protected static final String IPSEC_ENTITY_PROFILE_NAME = "MicroRBSIPSec_SAN_CHAIN_EP";
    protected final static String OAM_ENTITY_PROFILE_NAME = "MicroRBSOAM_CHAIN_EP";

}
