package com.ericsson.nms.security.nscs.integration.jee.test.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.rest.DTOIpSecConfigSwitchStatus;
import com.ericsson.nms.security.nscs.api.rest.DTOIpSecConfigValidityStatus;
import com.ericsson.nms.security.nscs.api.rest.IpSecConfigInvalidElement;
import com.ericsson.nms.security.nscs.api.rest.IpSecConfigSwitchStatus;
import com.ericsson.nms.security.nscs.api.rest.IpSecConfigValidityStatus;
import com.ericsson.nms.security.nscs.api.rest.IpSecValidityErrorCode;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.integration.jee.test.utils.data.NodeSecurityDataSetup;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.smrs.SmrsAccount;
import com.ericsson.oss.itpf.smrs.SmrsAddressRequest;
import com.ericsson.oss.services.enums.WfStatusEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestTest implements RestTests {

    public static enum IPSEC_JSON_DATA_TYPE {
        VALID,
        INVALID_CONFIG,
        INVALID_FORMAT_JSONPARSEEXCEPTION,
        INVALID_FORMAT_UNRECOGNIZEDPROPERTYEXCEPTION,
        INVALID_FORMAT_JSONMAPPINGEXCEPTION
    }

    final static String jsonDataPrefix = "{\"node\": [";
    final static String jsonDataSuffix = "] }";

    final static String node01 = "LTE06ERBS00001";
    final static String node02 = "LTE06ERBS00002";
    final static String node03 = "LTE06ERBS00003";

    final static String INVALID_ATTRIBUTE = "dnsServer1";

    final static String INVALID_ATTRIBUTE_JSONPARSEEXCEPTION = "aaaaaaa";
    final static String INVALID_ATTRIBUTE_UNRECOGNIZEDPROPERTYEXCEPTION = INVALID_ATTRIBUTE_JSONPARSEEXCEPTION;
    final static String INVALID_ATTRIBUTE_JSONMAPPINGEXCEPTION = "removeTrustOnFailure";
    final static String INVALID_ATTRIBUTE_JSONMAPPINGEXCEPTION_VALUE = "123";

    final static String IPADDRESS_VALID = "192.168.0.100";
    final static String IPADDRESS_INVALID = "192.168";

    final static String jsonDataConfig1_invalid_JSONPARSEEXCEPTION_format = "{%s \"nodeFdn\": \"LTE06ERBS00048\", \"subAltName\": \"DEMO_SUB_ALT_NAME_001\", \"subAltNameType\":\"a\", \"enableOMConfiguration1\": {\"removeTrustOnFailure\": \"false\", \"trustedCertificateFilePath\": \"string\", \"dnsServer1\": \"192.168.0.1\", \"dnsServer2\": \"192.168.1.1\", \"ipAddressOaMInner\": \"192.168.32.1\", \"networkPrefixLength\": \"10\", \"ipAccessHostEtId\": \"1\", \"defaultrouter0\": \"192.168.20.1\", \"ipAddressOaMOuter\": \"192.168.32.2\", \"remoteIpAddress\": \"192.168.99.8\", \"remoteIpAddressMask\": \"20\", \"peerOaMIpAddress\": \"192.168.51.1\", \"peerIdentityIdFqdn\": \"192.168.2.2\", \"peerIdentityIdType\": \"string\", \"tsLocalIpAddressMask\": \"32\", \"tsRemoteIpAddressRanges\": [{\"ipAddress\": \"192.168.88.2\", \"mask\": \"32\"}, {\"ipAddress\": \"192.168.88.3\", \"mask\": \"32\"} ] } }, {\"nodeFdn\": \"LTE06ERBS00050\", \"subAltName\": \"DEMO_SUB_ALT_NAME_002\", \"subAltNameType\":\"a\", \"enableOMConfiguration2\": {\"removeTrustOnFailure\": \"true\", \"trustedCertificateFilePath\": \"string\", \"dnsServer1\": \"192.168.0.2\", \"dnsServer2\": \"192.168.1.2\", \"ipAddressOaMInner\": \"192.168.32.3\", \"networkPrefixLength\": \"10\", \"ipAccessHostEtRef\": \"string\", \"peerOaMIpAddress\": \"192.168.2.2\", \"peerIdentityIdFqdn\": \"192.168.2.2\", \"peerIdentityIdType\": \"string\", \"tsLocalIpAddressMask\": \"32\", \"tsRemoteIpAddressRanges\": [{\"ipAddress\": \"192.168.88.2\", \"mask\": \"32\"}, {\"ipAddress\": \"192.168.88.3\", \"mask\": \"32\"} ] } }, {\"nodeFdn\": \"LTE06ERBS00051\", \"subAltName\": \"DEMO_SUB_ALT_NAME_004\", \"subAltNameType\":\"a\", \"disableOMConfiguration\": {\"removeCert\": \"false\", \"removeTrust\": {\"serialNumber\": \"10001\", \"issuer\": \"ERICSSON\"}, \"dnsServer1\": \"192.168.0.5\", \"dnsServer2\": \"192.168.1.5\", \"ipAddressOaMOuter\": \"192.168.32.7\", \"defaultRouter0\": \"192.168.20.3\", \"networkPrefixLength\": \"23\", \"remoteIpAddress\": \"192.168.20.3\", \"remoteIpAddressMask\": \"20\"} }";
    final static String jsonDataConfig1_invalid_JSONPARSEEXCEPTION = String.format(jsonDataConfig1_invalid_JSONPARSEEXCEPTION_format,
            INVALID_ATTRIBUTE_JSONPARSEEXCEPTION);
    final static String JSONPARSEEXCEPTION_MESSAGE = "Unexpected character";

    final static String jsonDataConfig1_invalid_UNRECOGNIZEDPROPERTYEXCEPTION_format = "{\"%s\": \"123\", \"nodeFdn\": \"LTE06ERBS00048\", \"subAltName\": \"DEMO_SUB_ALT_NAME_001\", \"subAltNameType\":\"a\", \"enableOMConfiguration1\": {\"removeTrustOnFailure\": \"false\", \"trustedCertificateFilePath\": \"string\", \"dnsServer1\": \"192.168.0.1\", \"dnsServer2\": \"192.168.1.1\", \"ipAddressOaMInner\": \"192.168.32.1\", \"networkPrefixLength\": \"10\", \"ipAccessHostEtId\": \"1\", \"defaultrouter0\": \"192.168.20.1\", \"ipAddressOaMOuter\": \"192.168.32.2\", \"remoteIpAddress\": \"192.168.99.8\", \"remoteIpAddressMask\": \"20\", \"peerOaMIpAddress\": \"192.168.51.1\", \"peerIdentityIdFqdn\": \"192.168.2.2\", \"peerIdentityIdType\": \"string\", \"tsLocalIpAddressMask\": \"32\", \"tsRemoteIpAddressRanges\": [{\"ipAddress\": \"192.168.88.2\", \"mask\": \"32\"}, {\"ipAddress\": \"192.168.88.3\", \"mask\": \"32\"} ] } }, {\"nodeFdn\": \"LTE06ERBS00050\", \"subAltName\": \"DEMO_SUB_ALT_NAME_002\", \"subAltNameType\":\"a\", \"enableOMConfiguration2\": {\"removeTrustOnFailure\": \"true\", \"trustedCertificateFilePath\": \"string\", \"dnsServer1\": \"192.168.0.2\", \"dnsServer2\": \"192.168.1.2\", \"ipAddressOaMInner\": \"192.168.32.3\", \"networkPrefixLength\": \"10\", \"ipAccessHostEtRef\": \"string\", \"peerOaMIpAddress\": \"192.168.2.2\", \"peerIdentityIdFqdn\": \"192.168.2.2\", \"peerIdentityIdType\": \"string\", \"tsLocalIpAddressMask\": \"32\", \"tsRemoteIpAddressRanges\": [{\"ipAddress\": \"192.168.88.2\", \"mask\": \"32\"}, {\"ipAddress\": \"192.168.88.3\", \"mask\": \"32\"} ] } }, {\"nodeFdn\": \"LTE06ERBS00051\", \"subAltName\": \"DEMO_SUB_ALT_NAME_004\", \"subAltNameType\":\"a\", \"disableOMConfiguration\": {\"removeCert\": \"false\", \"removeTrust\": {\"serialNumber\": \"10001\", \"issuer\": \"ERICSSON\"}, \"dnsServer1\": \"192.168.0.5\", \"dnsServer2\": \"192.168.1.5\", \"ipAddressOaMOuter\": \"192.168.32.7\", \"defaultRouter0\": \"192.168.20.3\", \"networkPrefixLength\": \"23\", \"remoteIpAddress\": \"192.168.20.3\", \"remoteIpAddressMask\": \"20\"} }";
    final static String jsonDataConfig1_invalid_UNRECOGNIZEDPROPERTYEXCEPTION = String
            .format(jsonDataConfig1_invalid_UNRECOGNIZEDPROPERTYEXCEPTION_format, INVALID_ATTRIBUTE_UNRECOGNIZEDPROPERTYEXCEPTION);
    final static String UNRECOGNIZEDPROPERTYEXCEPTION_MESSAGE = "The field is not recognized by the system";

    final static String jsonDataConfig1_invalid_JSONMAPPINGEXCEPTION_format = "{\"nodeFdn\": \"LTE06ERBS00048\", \"subAltName\": \"DEMO_SUB_ALT_NAME_001\", \"subAltNameType\":\"a\", \"enableOMConfiguration1\": {\"%s\": \"%s\", \"trustedCertificateFilePath\": \"string\", \"dnsServer1\": \"192.168.0.1\", \"dnsServer2\": \"192.168.1.1\", \"ipAddressOaMInner\": \"192.168.32.1\", \"networkPrefixLength\": \"10\", \"ipAccessHostEtId\": \"1\", \"defaultrouter0\": \"192.168.20.1\", \"ipAddressOaMOuter\": \"192.168.32.2\", \"remoteIpAddress\": \"192.168.99.8\", \"remoteIpAddressMask\": \"20\", \"peerOaMIpAddress\": \"192.168.51.1\", \"peerIdentityIdFqdn\": \"192.168.2.2\", \"peerIdentityIdType\": \"string\", \"tsLocalIpAddressMask\": \"32\", \"tsRemoteIpAddressRanges\": [{\"ipAddress\": \"192.168.88.2\", \"mask\": \"32\"}, {\"ipAddress\": \"192.168.88.3\", \"mask\": \"32\"} ] } }, {\"nodeFdn\": \"LTE06ERBS00050\", \"subAltName\": \"DEMO_SUB_ALT_NAME_002\", \"subAltNameType\":\"a\", \"enableOMConfiguration2\": {\"removeTrustOnFailure\": \"true\", \"trustedCertificateFilePath\": \"string\", \"dnsServer1\": \"192.168.0.2\", \"dnsServer2\": \"192.168.1.2\", \"ipAddressOaMInner\": \"192.168.32.3\", \"networkPrefixLength\": \"10\", \"ipAccessHostEtRef\": \"string\", \"peerOaMIpAddress\": \"192.168.2.2\", \"peerIdentityIdFqdn\": \"192.168.2.2\", \"peerIdentityIdType\": \"string\", \"tsLocalIpAddressMask\": \"32\", \"tsRemoteIpAddressRanges\": [{\"ipAddress\": \"192.168.88.2\", \"mask\": \"32\"}, {\"ipAddress\": \"192.168.88.3\", \"mask\": \"32\"} ] } }, {\"nodeFdn\": \"LTE06ERBS00051\", \"subAltName\": \"DEMO_SUB_ALT_NAME_004\", \"subAltNameType\":\"a\", \"disableOMConfiguration\": {\"removeCert\": \"false\", \"removeTrust\": {\"serialNumber\": \"10001\", \"issuer\": \"ERICSSON\"}, \"dnsServer1\": \"192.168.0.5\", \"dnsServer2\": \"192.168.1.5\", \"ipAddressOaMOuter\": \"192.168.32.7\", \"defaultRouter0\": \"192.168.20.3\", \"networkPrefixLength\": \"23\", \"remoteIpAddress\": \"192.168.20.3\", \"remoteIpAddressMask\": \"20\"} }";
    final static String jsonDataConfig1_invalid_JSONMAPPINGEXCEPTION = String.format(jsonDataConfig1_invalid_JSONMAPPINGEXCEPTION_format,
            INVALID_ATTRIBUTE_JSONMAPPINGEXCEPTION, INVALID_ATTRIBUTE_JSONMAPPINGEXCEPTION_VALUE);
    final static String JSONMAPPINGEXCEPTION_MESSAGE = String.format("Value [%s] is not valid", INVALID_ATTRIBUTE_JSONMAPPINGEXCEPTION_VALUE);

    final static String jsonDataConfig1_format = "{\"nodeFdn\": \"%s\", \"subAltName\": \"DEMO_SUB_ALT_NAME_001\", \"subAltNameType\":\"a\", \"enableOMConfiguration1\": {\"removeTrustOnFailure\": \"false\", \"trustedCertificateFilePath\": \"string\", \""
            + INVALID_ATTRIBUTE
            + "\": \"%s\", \"dnsServer2\": \"192.168.1.1\", \"ipAddressOaMInner\": \"192.168.32.1\", \"networkPrefixLength\": \"10\", \"ipAccessHostEtId\": \"string\", \"defaultrouter0\": \"192.168.20.1\", \"ipAddressOaMOuter\": \"192.168.32.2\", \"remoteIpAddress\": \"192.168.99.8\", \"remoteIpAddressMask\": \"20\", \"peerOaMIpAddress\": \"192.168.51.1\", \"peerIdentityIdFqdn\": \"192.168.2.2\", \"peerIdentityIdType\": \"string\", \"tsLocalIpAddressMask\": \"32\", \"tsRemoteIpAddressRanges\": [{\"ipAddress\": \"192.168.88.2\", \"mask\": \"32\"}, {\"ipAddress\": \"192.168.88.3\", \"mask\": \"32\"} ],\"ipSecTunnelAllowedTransforms\": {\"ipSecTunnelAllowedTransform\": [{\"encryptionAlgorithm\": \"AES_CBC_128\",\"integrityAlgorithm\": \"HMAC_SHA_1_96\"}] } ,\"ikePeerAllowedTransforms\": {\"ikePeerAllowedTransform\": [{\"pseudoRandomFunction\": \"HMAC_SHA_1\",\"diffieHellmanGroup\": \"GROUP_2\",\"integrityAlgorithm\": \"HMAC_SHA_1_96\",\"encryptionAlgorithm\": \"AES_CBC_128\"}]}  } }";
    final static String jsonDataConfig2_format = "{\"nodeFdn\": \"%s\", \"subAltName\": \"DEMO_SUB_ALT_NAME_002\", \"subAltNameType\":\"a\", \"enableOMConfiguration2\": {\"removeTrustOnFailure\": \"true\", \"trustedCertificateFilePath\": \"string\", \""
            + INVALID_ATTRIBUTE
            + "\": \"%s\", \"dnsServer2\": \"192.168.1.2\", \"ipAddressOaMInner\": \"192.168.32.3\", \"networkPrefixLength\": \"10\", \"ipAccessHostEtRef\": \"string\", \"peerOaMIpAddress\": \"192.168.2.2\", \"peerIdentityIdFqdn\": \"192.168.2.2\", \"peerIdentityIdType\": \"string\", \"tsLocalIpAddressMask\": \"32\", \"tsRemoteIpAddressRanges\": [{\"ipAddress\": \"192.168.88.2\", \"mask\": \"32\"}, {\"ipAddress\": \"192.168.88.3\", \"mask\": \"32\"} ] } }";
    final static String jsonDataConfigDisabled_format = "{\"nodeFdn\": \"%s\", \"subAltName\": \"DEMO_SUB_ALT_NAME_004\", \"subAltNameType\":\"a\", \"disableOMConfiguration\": {\"removeCert\": \"false\", \"removeTrust\": {\"serialNumber\": \"10001\", \"issuer\": \"ERICSSON\"}, \""
            + INVALID_ATTRIBUTE
            + "\": \"%s\", \"dnsServer2\": \"192.168.1.5\", \"ipAddressOaMOuter\": \"192.168.32.7\", \"defaultRouter0\": \"192.168.20.3\", \"networkPrefixLength\": \"23\", \"remoteIpAddress\": \"192.168.20.3\", \"remoteIpAddressMask\": \"20\"} }";

    final static String jsonDataConfig1_valid = String.format(jsonDataConfig1_format, node01, IPADDRESS_VALID);
    final static String jsonDataConfig2_valid = String.format(jsonDataConfig2_format, node02, IPADDRESS_VALID);
    final static String jsonDataConfigDisabled_valid = String.format(jsonDataConfigDisabled_format, node03, IPADDRESS_VALID);
    final static String jsonDataCombined_valid = jsonDataConfig1_valid + "," + jsonDataConfig2_valid + "," + jsonDataConfigDisabled_valid;

    private static final String jsonDataConfig1_invalid = String.format(jsonDataConfig1_format, node01, IPADDRESS_INVALID);
    private static final String jsonDataConfig2_invalid = String.format(jsonDataConfig1_format, node02, IPADDRESS_INVALID);
    private static final String jsonDataConfigDisabled_invalid = String.format(jsonDataConfig1_format, node03, IPADDRESS_INVALID);
    private static final String jsonDataCombined_invalid = jsonDataConfig1_invalid + "," + jsonDataConfig2_invalid + ","
            + jsonDataConfigDisabled_invalid;
    private final static String TEST_NODE = "LTE03ERBS00003";
    private static final String uuidPattern = "([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})";

    @Inject
    private Logger logger;

    @Inject
    private NodeSecurityDataSetup nodeData;

    // The MeContext name!
    private static final String NODE_123 = "node123";
    private static final String NODE_456 = "node456";

    @Override
    public void testRestSmrsAccount() throws Exception {

        logger.info("-----------testRestSmrsAccount starts--------------");

        SmrsAddressRequest addressRequest = new SmrsAddressRequest();
        addressRequest.setAccountType("CERTIFICATES");
        addressRequest.setNeType("ERBS");
        addressRequest.setNeName(NODE_123);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(addressRequest);
        final HttpResponse response = invokeJsonPostTestRest("smrs/account", jsonData);
        assertEquals("Status code should have the expected value", 200, response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        String content = EntityUtils.toString(entity);
        final SmrsAccount account = objectMapper.readValue(content, SmrsAccount.class);
        assertEquals("Account type should have the expected value", "CERTIFICATES", account.getAccountType());
        assertEquals("Home directory should have the expected value", "target/resources/smrs/smrsroot/certificates/erbs/node123/",
                account.getHomeDirectory());
        assertEquals("Network type should have the expected value", null, account.getNetworkType());
        assertEquals("NE type should have the expected value", "ERBS", account.getNeType());
        assertEquals("User name should have the expected value", "mm-cert-arquillian", account.getUserName());
        assertEquals("SMRS root directory should have the expected value", "target/resources/smrs/", account.getSmrsRootDirectory());
        assertEquals("Relative path should have the expected value", "smrsroot/certificates/erbs/node123/", account.getRelativePath());
        assertEquals("NE name should have the expected value", "node123", account.getNeName());
        assertEquals("User home directory should have the expected value", null, account.getUserHomeDir());
        assertEquals("Password should have the expected value", "secret", account.getPassword());

        logger.info("-----------testRestSmrsAccount ends--------------");
    }

    @Override
    public void testRestSmrsAddress() throws Exception {

        logger.info("-----------testRestSmrsAddress starts--------------");

        SmrsAddressRequest addressRequest = new SmrsAddressRequest();
        addressRequest.setAccountType("CERTIFICATES");
        addressRequest.setNeType("ERBS");
        addressRequest.setNeName(NODE_123);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(addressRequest);
        final HttpResponse response = invokeJsonPostTestRest("smrs/address", jsonData);
        assertEquals("Status code should have the expected value", 200, response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        String fileServerAddress = EntityUtils.toString(entity);
        assertEquals("File server address should have the expected value", "localhost", fileServerAddress);

        logger.info("-----------testRestSmrsAddress ends--------------");
    }

    @Override
    public void testRestSmrsDeleteAccount() throws Exception {

        logger.info("-----------testRestSmrsDeleteAccount starts--------------");

        SmrsAccount account = new SmrsAccount();
        account.setAccountType("CERTIFICATES");
        account.setNeType("ERBS");
        account.setNeName(NODE_123);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(account);
        final HttpResponse response = invokeJsonPostTestRest("smrs/delete", jsonData);
        assertEquals("Status code should have the expected value", 200, response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        String deleteResult = EntityUtils.toString(entity);
        assertEquals("Delete result should have the expected value", "true", deleteResult);

        logger.info("-----------testRestSmrsDeleteAccount ends--------------");
    }

    @Override
    public void testRestMoActionWithoutParam() throws Exception {

        logger.info("-----------testRestMoActionWithoutParam starts--------------");

        setup(NODE_123);

        final String networkElementName = NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_123);
        final String path = "moaction/" + MoActionWithoutParameter.Security_cancelCertEnrollment + "/" + networkElementName;
        final HttpResponse response = invokeGetRest(path);
        assertEquals("Status code should have the expected value", 200, response.getStatusLine().getStatusCode());

        tearDown();

        logger.info("-----------testRestMoActionWithoutParam ends--------------");
    }

    @Override
    public void testRestMoActionInitCertEnrollment() throws Exception {

        logger.info("-----------testRestMoActionInitCertEnrollment starts--------------");

        setup(NODE_123);

        final String networkElementName = NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_123);
        final String path = "moactionparams/" + MoActionWithParameter.Security_initCertEnrollment + "/" + networkElementName;
        final HttpResponse response = invokeGetRest(path);
        assertEquals("Status code should have the expected value", 200, response.getStatusLine().getStatusCode());

        tearDown();

        logger.info("-----------testRestMoActionInitCertEnrollment ends--------------");
    }

    @Override
    public void testRestMoActionInstallTrustedCertificates() throws Exception {

        logger.info("-----------testRestMoActionInstallTrustedCertificates starts--------------");

        setup(NODE_123);

        final String networkElementName = NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_123);
        final String path = "moactionparams/" + MoActionWithParameter.Security_installTrustedCertificates + "/" + networkElementName;
        final HttpResponse response = invokeGetRest(path);
        assertEquals("Status code should have the expected value", 200, response.getStatusLine().getStatusCode());

        tearDown();

        logger.info("-----------testRestMoActionInstallTrustedCertificates ends--------------");
    }

    @Override
    public void testRestNodesList() throws Exception {

        logger.info("-----------testRestNodesList starts--------------");

        setup(NODE_456);
        Long poId = null;
        final PersistenceObject collection = nodeData.createStaticCollection(NODE_456);
        if (collection != null) {
            poId = collection.getPoId();
        }

        final String jsonData = "{\"collectionIds\":[\"" + poId + "\"],\"filter\":{\"name\":\"\",\"securityLevel\":[\"LEVEL_1\"]}}";
        final HttpResponse response = invokeJsonPostRest("nodes/", jsonData);
        assertEquals("Status code should have the expected value", 200, response.getStatusLine().getStatusCode());

        nodeData.deleteCollection(new ArrayList<>(Arrays.asList(poId)));
        tearDown();

        logger.info("-----------testRestNodesList ends--------------");
    }

    @Override
    public void testRestNodesCount() throws Exception {

        logger.info("-----------testRestNodesCount starts--------------");

        setup(NODE_123);
        Long poId = null;
        final PersistenceObject collection = nodeData.createStaticCollection(NODE_123);
        if (collection != null) {
            poId = collection.getPoId();
        }

        final String jsonData = "{\"collectionIds\":[\"" + poId + "\"],\"filter\":{\"name\":\"\",\"securityLevel\":[\"LEVEL_1\"]}}";
        final HttpResponse response = invokeJsonPostRest("nodes/count", jsonData);
        assertEquals("Status code should have the expected value", 200, response.getStatusLine().getStatusCode());

        nodeData.deleteCollection(new ArrayList<Long>(Arrays.asList(poId)));
        tearDown();

        logger.info("-----------testRestNodesCount ends--------------");
    }

    @Override
    public void testRestNodesIpsec() throws Exception {

        logger.info("-----------testRestNodesIpsec starts--------------");

        final ObjectMapper mapper = new ObjectMapper();

        final List<String> jsonDataToExecuteList = populateJsonDataForIpsec(IPSEC_JSON_DATA_TYPE.VALID);

        HttpResponse response = null;
        String jsonResponse = null;
        List<String> expectedNodesNameList = null;
        for (final String json : jsonDataToExecuteList) {
            response = null;
            jsonResponse = null;
            logger.info("---json {}", json);
            response = invokeJsonPostRest("nodes/ipsec/", jsonDataPrefix + json + jsonDataSuffix);
            assertEquals(String.format("Status code should be 200 for json data %s", json), 200, response.getStatusLine().getStatusCode());
            jsonResponse = EntityUtils.toString(response.getEntity());
            logger.info("---json response {}", jsonResponse);
            assertTrue("Json response shall not be null", jsonResponse != null);
            assertTrue("Json response shall not be empty", !jsonResponse.isEmpty());
            //check if response is as expected List<IpSecConfigSwitchStatus>
            final DTOIpSecConfigSwitchStatus data = mapper.readValue(jsonResponse, new TypeReference<DTOIpSecConfigSwitchStatus>() {
            });
            assertTrue("Json response is null", data != null);
            assertTrue("List<IpSecConfigSwitchStatus> is null", data.getSwitchStatusList() != null);
            assertTrue("List<IpSecConfigSwitchStatus> is empty", !data.getSwitchStatusList().isEmpty());

            expectedNodesNameList = new ArrayList<String>();
            if (jsonDataConfig1_valid.equals(json)) {
                expectedNodesNameList.add(node01);
            } else if (jsonDataConfig2_valid.equals(json)) {
                expectedNodesNameList.add(node02);
            } else if (jsonDataConfigDisabled_valid.equals(json)) {
                expectedNodesNameList.add(node03);
            } else if (jsonDataCombined_valid.equals(json)) {
                expectedNodesNameList.add(node01);
                expectedNodesNameList.add(node02);
                expectedNodesNameList.add(node03);
            }
            assertTrue(String.format("List<IpSecConfigSwitchStatus> size is wrong, expected %s, actual %s", expectedNodesNameList.size(),
                    data.getSwitchStatusList().size()), expectedNodesNameList.size() == data.getSwitchStatusList().size());
            for (final IpSecConfigSwitchStatus iscss : data.getSwitchStatusList()) {
                assertTrue(String.format("List<IpSecConfigSwitchStatus> missing the expected node name %s", iscss.getName()),
                        expectedNodesNameList.contains(iscss.getName()));
                assertTrue("Ipsecconfig is empty string", !iscss.getIpsecconfig().isEmpty());
                assertTrue("Ne Type is empty", !iscss.getType().isEmpty());
            }
        }
        logger.info("-----------testRestNodesIpsec ends--------------");
    }

    @Override
    public void testRestNodesVerifyIpsec() throws Exception {

        logger.info("-----------testRestNodesVerifyIpsec starts--------------");

        final ObjectMapper mapper = new ObjectMapper();

        final List<String> jsonDataToExecuteList = populateJsonDataForIpsec(IPSEC_JSON_DATA_TYPE.VALID);

        HttpResponse response = null;
        String jsonResponse = null;
        List<String> expectedNodesNameList = null;
        for (final String json : jsonDataToExecuteList) {
            response = null;
            jsonResponse = null;
            logger.info("---verification json {}", json);
            response = invokeJsonPostRest("nodes/verify/ipsec/", jsonDataPrefix + json + jsonDataSuffix);

            assertEquals(String.format("Status code should be %s for json data %s", 200, json), 200, response.getStatusLine().getStatusCode());

            jsonResponse = EntityUtils.toString(response.getEntity());
            logger.info("---verification json response {}", jsonResponse);
            assertTrue("Json response shall not be null", jsonResponse != null);
            assertTrue("Json response shall not be empty", !jsonResponse.isEmpty());
            //check if response is as expected List<IpSecConfigSwitchStatus>
            final DTOIpSecConfigValidityStatus data = mapper.readValue(jsonResponse, new TypeReference<DTOIpSecConfigValidityStatus>() {
            });
            logger.info("---verification mapper read value");

            assertTrue("Json response is null", data != null);
            assertTrue("List<IpSecConfigValidityStatus> is null", data.getIpSecConfigValidityStatus() != null);
            assertTrue("List<IpSecConfigValidityStatus> is empty", !data.getIpSecConfigValidityStatus().isEmpty());

            expectedNodesNameList = new ArrayList<String>();
            if (jsonDataConfig1_valid.equals(json)) {
                expectedNodesNameList.add(node01);
            } else if (jsonDataConfig2_valid.equals(json)) {
                expectedNodesNameList.add(node02);
            } else if (jsonDataConfigDisabled_valid.equals(json)) {
                expectedNodesNameList.add(node03);
            } else if (jsonDataCombined_valid.equals(json)) {
                expectedNodesNameList.add(node01);
                expectedNodesNameList.add(node02);
                expectedNodesNameList.add(node03);
            }
            assertTrue(String.format("List<IpSecConfigValidityStatus> size is wrong, expected %s, actual %s", expectedNodesNameList.size(),
                    data.getIpSecConfigValidityStatus().size()), expectedNodesNameList.size() == data.getIpSecConfigValidityStatus().size());
            for (final IpSecConfigValidityStatus iscss : data.getIpSecConfigValidityStatus()) {
                assertTrue("List<IpSecConfigValidityStatus> missing the expected node name " + iscss.getName(),
                        expectedNodesNameList.contains(iscss.getName()));
                assertTrue("IpsecConfigInvalidElementsList should be empty for the valid node " + iscss.getName(),
                        iscss.getIpsecConfigInvalidElements().isEmpty());
            }
        }
        logger.info("-----------testRestNodesVerifyIpsec ends--------------");
    }

    @Override
    public void testRestNodesVerifyInvalidIpsec() throws Exception {

        logger.info("-----------testRestNodesVerifyInvalidIpsec starts--------------");

        final ObjectMapper mapper = new ObjectMapper();

        final List<String> jsonDataToExecuteList = populateJsonDataForIpsec(IPSEC_JSON_DATA_TYPE.INVALID_CONFIG);

        HttpResponse response = null;
        String jsonResponse = null;
        List<String> expectedNodesNameList = null;
        for (final String json : jsonDataToExecuteList) {
            response = null;
            jsonResponse = null;
            logger.info("---verification json {}", json);
            response = invokeJsonPostRest("nodes/verify/ipsec/", jsonDataPrefix + json + jsonDataSuffix);

            assertEquals(String.format("Status code should be %s for invalid json data %s", 400, json), 400,
                    response.getStatusLine().getStatusCode());

            jsonResponse = EntityUtils.toString(response.getEntity());
            logger.info("---verification json response {}", jsonResponse);
            assertTrue("Json response shall not be null", jsonResponse != null);
            assertTrue("Json response shall not be empty", !jsonResponse.isEmpty());
            //check if response is as expected List<IpSecConfigSwitchStatus>
            final DTOIpSecConfigValidityStatus data = mapper.readValue(jsonResponse, new TypeReference<DTOIpSecConfigValidityStatus>() {
            });
            logger.info("---verification mapper read value");

            assertTrue("Json response is null", data != null);
            assertTrue("List<IpSecConfigValidityStatus> is null", data.getIpSecConfigValidityStatus() != null);
            assertTrue("List<IpSecConfigValidityStatus> is empty", !data.getIpSecConfigValidityStatus().isEmpty());

            expectedNodesNameList = new ArrayList<String>();
            if (jsonDataConfig1_invalid.equals(json)) {
                expectedNodesNameList.add(node01);
            } else if (jsonDataConfig2_invalid.equals(json)) {
                expectedNodesNameList.add(node02);
            } else if (jsonDataConfigDisabled_invalid.equals(json)) {
                expectedNodesNameList.add(node03);
            } else if (jsonDataCombined_invalid.equals(json)) {
                expectedNodesNameList.add(node01);
                expectedNodesNameList.add(node02);
                expectedNodesNameList.add(node03);
            }
            assertTrue(String.format("List<IpSecConfigValidityStatus> size is wrong, expected %s, actual %s", expectedNodesNameList.size(),
                    data.getIpSecConfigValidityStatus().size()), expectedNodesNameList.size() == data.getIpSecConfigValidityStatus().size());
            for (final IpSecConfigValidityStatus iscss : data.getIpSecConfigValidityStatus()) {
                logger.info("iscss = {} expectedNodesNameList= {}", iscss.getName(), expectedNodesNameList);
                assertTrue("List<IpSecConfigValidityStatus> missing the expected node name " + iscss.getName(),
                        expectedNodesNameList.contains(iscss.getName()));
                assertTrue("IpsecConfigInvalidElementsList should be populated with invalid data for the valid node " + iscss.getName(),
                        !iscss.getIpsecConfigInvalidElements().isEmpty());
                assertTrue("IpsecConfigInvalidElementsList should contain all invalid data for the valid node " + iscss.getName(),
                        iscss.getIpsecConfigInvalidElements().size() == 1);
                final IpSecConfigInvalidElement invalidElement = iscss.getIpsecConfigInvalidElements().get(0);
                assertTrue("Invalid attribute in list is null", invalidElement != null);
                assertTrue("IpsecConfigInvalidElementsList should contain attribute '" + INVALID_ATTRIBUTE + "' marked as invalid for the valid node "
                        + iscss.getName(), invalidElement.getElementName().equalsIgnoreCase(INVALID_ATTRIBUTE));
                assertTrue("IpSecConfigInvalidElement attribute '" + INVALID_ATTRIBUTE + "' has empty message for the valid node " + iscss.getName(),
                        !invalidElement.getErrorMessage().isEmpty());
                assertTrue(
                        "IpSecConfigInvalidElement attribute '" + INVALID_ATTRIBUTE + "' expected error code "
                                + IpSecValidityErrorCode.IPADDRESS_TYPE.name() + " but found " + invalidElement.getErrorCode().name(),
                        IpSecValidityErrorCode.IPADDRESS_TYPE.name().equals(invalidElement.getErrorCode().name()));
            }
        }
        logger.info("-----------testRestNodesVerifyInvalidIpsec ends--------------");
    }

    @Override
    public void testRestNodesVerifyInvalidIpsec_JsonMappingExceptionMapper() throws Exception {

        logger.info("-----------testRestNodesVerifyInvalidIpsec_JsonMappingExceptionMapper starts--------------");

        final List<String> jsonDataToExecuteList = populateJsonDataForIpsec(IPSEC_JSON_DATA_TYPE.INVALID_FORMAT_JSONMAPPINGEXCEPTION);

        HttpResponse response = null;
        String jsonResponse = null;
        for (final String json : jsonDataToExecuteList) {
            response = null;
            jsonResponse = null;
            logger.info("---verification json {}", json);
            response = invokeJsonPostRest("nodes/verify/ipsec/", jsonDataPrefix + json + jsonDataSuffix);

            assertEquals(String.format("Status code should be %s for invalid json data %s", 400, json), 400,
                    response.getStatusLine().getStatusCode());

            jsonResponse = EntityUtils.toString(response.getEntity());
            logger.info("---verification json response {}", jsonResponse);
            assertTrue("Json response shall not be null", jsonResponse != null);
            assertTrue("Json response shall not be empty", !jsonResponse.isEmpty());
        }
        logger.info("-----------testRestNodesVerifyInvalidIpsec_JsonMappingExceptionMapper ends--------------");
    }

    @Override
    public void testRestNodesVerifyInvalidIpsec_UnrecognizedPropertyExceptionMapper() throws Exception {

        logger.info("-----------testRestNodesVerifyInvalidIpsec_UnrecognizedPropertyExceptionMapper starts--------------");

        final List<String> jsonDataToExecuteList = populateJsonDataForIpsec(IPSEC_JSON_DATA_TYPE.INVALID_FORMAT_UNRECOGNIZEDPROPERTYEXCEPTION);

        HttpResponse response = null;
        for (final String json : jsonDataToExecuteList) {
            response = null;
            logger.info("---verification json {}", json);
            response = invokeJsonPostRest("nodes/verify/ipsec/", jsonDataPrefix + json + jsonDataSuffix);
            logger.info("---RESPONSE {}", response);

            assertEquals(String.format("Status code should be %s for invalid json data %s", 400, json), 400,
                    response.getStatusLine().getStatusCode());

        }
        logger.info("-----------testRestNodesVerifyInvalidIpsec_UnrecognizedPropertyExceptionMapper ends--------------");
    }

    @Override
    public void testRestNodesVerifyInvalidIpsec_JsonParseExceptionMapper() throws Exception {

        logger.info("-----------testRestNodesVerifyInvalidIpsec_JsonParseExceptionMapper starts--------------");

        final List<String> jsonDataToExecuteList = populateJsonDataForIpsec(IPSEC_JSON_DATA_TYPE.INVALID_FORMAT_JSONPARSEEXCEPTION);

        HttpResponse response = null;
        for (final String json : jsonDataToExecuteList) {
            response = null;

            logger.info("---verification json {}", json);
            response = invokeJsonPostRest("nodes/verify/ipsec/", jsonDataPrefix + json + jsonDataSuffix);
            logger.info("---RESPONSE {}", response);

            assertEquals(String.format("Status code should be %s for invalid json data %s", 400, json), 400,
                    response.getStatusLine().getStatusCode());
        }
        logger.info("-----------testRestNodesVerifyInvalidIpsec_JsonParseExceptionMapper ends--------------");
    }

    @Override
    public void testRestPIBModelRestResource_neCertAutoRenewalTimer() throws Exception {

        logger.info("-----------testRestPIBModelRestResource_neCertAutoRenewalTimer starts--------------");

        final String confParam = "neCertAutoRenewalTimer";
        final HttpResponse response = invokeGetRest("pib/confparam/" + confParam);
        assertEquals(200, response.getStatusLine().getStatusCode());

        final String stringResponse = EntityUtils.toString(response.getEntity());
        assertTrue("Invalid response: " + stringResponse, stringResponse.contains(confParam));

        logger.info("-----------testRestPIBModelRestResource_neCertAutoRenewalTimer ends--------------");
    }

    @Override
    public void testRestPIBModelRestResource_neCertAutoRenewalEnabled() throws Exception {

        logger.info("-----------testRestPIBModelRestResource_neCertAutoRenewalEnabled starts--------------");

        final String confParam = "neCertAutoRenewalEnabled";
        final HttpResponse response = invokeGetRest("pib/confparam/" + confParam);
        assertEquals(200, response.getStatusLine().getStatusCode());

        final String stringResponse = EntityUtils.toString(response.getEntity());
        assertTrue("Invalid response: " + stringResponse, stringResponse.contains(confParam));

        logger.info("-----------testRestPIBModelRestResource_neCertAutoRenewalEnabled ends--------------");
    }

    @Override
    public void testRestPIBModelRestResource_neCertAutoRenewalMax() throws Exception {

        logger.info("-----------testRestPIBModelRestResource_neCertAutoRenewalMax starts--------------");

        final String confParam = "neCertAutoRenewalMax";
        final HttpResponse response = invokeGetRest("pib/confparam/" + confParam);
        assertEquals(200, response.getStatusLine().getStatusCode());

        final String stringResponse = EntityUtils.toString(response.getEntity());
        assertTrue("Invalid response: " + stringResponse, stringResponse.contains(confParam));

        logger.info("-----------testRestPIBModelRestResource_neCertAutoRenewalMax ends--------------");
    }

    @Override
    public void testRestPIBModelRestResource_wfCongestionThreshold() throws Exception {

        logger.info("-----------testRestPIBModelRestResource_wfCongestionThreshold starts--------------");

        final String confParam = "wfCongestionThreshold";
        final HttpResponse response = invokeGetRest("pib/confparam/" + confParam);
        assertEquals(200, response.getStatusLine().getStatusCode());

        final String stringResponse = EntityUtils.toString(response.getEntity());
        assertTrue("Invalid response: " + stringResponse, stringResponse.contains(confParam));

        logger.info("-----------testRestPIBModelRestResource_wfCongestionThreshold ends--------------");
    }

    @Override
    public void testRestPIBModelRestResource_BadRequest() throws Exception {

        logger.info("-----------testRestPIBModelRestResource_BadRequest starts--------------");

        final String confParam = "asdfgh";
        final HttpResponse response = invokeGetRest("pib/confparam/" + confParam);
        assertEquals(400, response.getStatusLine().getStatusCode());

        final String stringResponse = EntityUtils.toString(response.getEntity());
        assertTrue("Invalid response: " + stringResponse, stringResponse.contains(confParam));

        logger.info("-----------testRestPIBModelRestResource_BadRequest ends--------------");
    }

    /**
     * Test Job Management Cache handler
     */

    private static final String REST_CREATE_JOB_ID_MESSAGE = "Created job";
    private static final String REST_STATUS_MESSAGE = "There are 1";
    public static final String NODE_NAME_REST = "LTE03ERBS00003";

    private static final String WF_WAKE_ID = UUID.randomUUID().toString();

    @Override
    public void testRestJobGetPendingWorkflowsTest() throws Exception {

        logger.info("-----------testRestJobGetPendingWorkflowsTest starts--------------");

        performJobCacheResetRest();

        final String methodName = getClass() + ".testRestJobGetPendingWorkflowsTest";
        //create Job
        HttpResponse response = performJobCacheCreateRest();

        String stringResponse = EntityUtils.toString(response.getEntity());

        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
        final String jobStringId = stringResponse.substring(stringResponse.lastIndexOf(" ") + 1);

        //now create a pending workflow
        response = performWfCacheUpdateRest(jobStringId, WF_WAKE_ID, WfStatusEnum.PENDING.name());

        response = performJobCacheGetRest("pending");
        stringResponse = EntityUtils.toString(response.getEntity());
        logger.info(stringResponse);
        assertTrue(stringResponse.contains(REST_STATUS_MESSAGE));

        logger.info("-----------testRestJobGetPendingWorkflowsTest ends--------------");
    }

    @Override
    public void testRestJobGetRunningWorkflowCountTest() throws Exception {

        logger.info("-----------testRestJobGetRunningWorkflowCountTest starts--------------");

        performJobCacheResetRest();

        final String methodName = getClass() + ".testRestJobGetRunningWorkflowCountTest";
        //create Job
        HttpResponse response = performJobCacheCreateRest();

        String stringResponse = EntityUtils.toString(response.getEntity());

        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
        final String stringUUID = stringResponse.substring(stringResponse.lastIndexOf(" ") + 1);

        //now create a pending workflow
        response = performWfCacheUpdateRest(stringUUID, WF_WAKE_ID, WfStatusEnum.RUNNING.name());

        response = performJobCacheGetRest("running");
        stringResponse = EntityUtils.toString(response.getEntity());
        logger.info(stringResponse);
        assertTrue(stringResponse.contains(REST_STATUS_MESSAGE));

        logger.info("-----------testRestJobGetRunningWorkflowCountTest ends--------------");
    }

    @Override
    public void testRestJobCheckNoRunningWFbyNodeNameTest() throws Exception {

        logger.info("-----------testRestJobCheckNoRunningWFbyNodeNameTest starts--------------");

        performJobCacheResetRest();

        final String methodName = getClass() + ".testRestJobCheckNoRunningWFbyNodeNameTest";
        //create Job
        HttpResponse response = performJobCacheCreateRest();

        String stringResponse = EntityUtils.toString(response.getEntity());

        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
        final String stringUUID = extractUuId(stringResponse);

        //now create a pending workflow
        response = performWfCacheUpdateRest(stringUUID, TEST_NODE, WfStatusEnum.RUNNING.name());
        stringResponse = EntityUtils.toString(response.getEntity());

        logger.info(stringResponse);

        response = performJobCacheGetRest("runningOnTestNode/" + TEST_NODE);
        stringResponse = EntityUtils.toString(response.getEntity());
        logger.info(stringResponse);
        assertTrue(stringResponse.contains(REST_STATUS_MESSAGE));

        logger.info("-----------testRestJobCheckNoRunningWFbyNodeNameTest ends--------------");
    }

    @Override
    public void testRestJobEvictionTest() throws Exception {

        logger.info("-----------testRestJobEvictionTest starts--------------");

        performJobCacheResetRest();

        final String methodName = getClass() + ".testRestJobEvictionTest";
        //create Job
        HttpResponse response = performJobCacheCreateRest();

        String stringResponse = EntityUtils.toString(response.getEntity());

        logger.info(methodName + " Response: " + stringResponse);
        assertTrue(stringResponse.contains(REST_CREATE_JOB_ID_MESSAGE));
        final String stringUUID = extractUuId(stringResponse);

        //now create a pending workflow
        response = performWfCacheUpdateRest(stringUUID, TEST_NODE, WfStatusEnum.SUCCESS.name());

        Thread.sleep(10000);
        response = performJobCacheDeleteRest("8000");
        //get all jobs
        response = performJobCacheGetRest(null);
        logger.info("testRestJobEvictionTest - response:" + response);

        stringResponse = EntityUtils.toString(response.getEntity());
        logger.info("testRestJobEvictionTest - stringResponse:" + stringResponse);
        assertTrue(stringResponse.equals("[]"));

        logger.info("-----------testRestJobEvictionTest ends--------------");
    }

    private void setup(final String nodename) {
        try {
            nodeData.deleteAllNodes();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        try {
            nodeData.createNode(nodename);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void tearDown() throws Exception {
        nodeData.deleteAllNodes();
    }

    private HttpResponse invokeGetRest(final String path) {

        final String restPath = String.format("%s/%s", RestHelper.NODE_SECURITY_TEST_PATH, path);
        final String url = RestHelper.getRestHttpUrl(restPath);
        HttpGet httpget = null;
        try {
            httpget = new HttpGet(new URL(url).toExternalForm());
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
        final HttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
        } catch (final ClientProtocolException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private HttpResponse invokeJsonPostRest(final String path, final String jsonPostData) {

        final Map<String, String> header = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;

            {
                put("X-Tor-UserID", "Administrator");
            }
        };
        return invokeJsonPostRest(path, jsonPostData, header);
    };

    private HttpResponse invokeJsonPostRest(final String path, final String jsonPostData, final Map<String, String> header) {

        final String url = String.format("http://%s:8080/node-security/%s", RestHelper.getLocalHostAddr(), path);
        final HttpPost post = new HttpPost(url);
        if (header != null) {
            for (final Entry<String, String> e : header.entrySet()) {
                post.addHeader(e.getKey(), e.getValue());
            }

        }
        post.addHeader("content-type", "application/json");
        final HttpClient httpclient = HttpClientBuilder.create().build();
        StringEntity params = null;
        try {
            params = new StringEntity(jsonPostData);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("Exception while building StringEntity, {}", e.getClass().getName());
        }
        post.setEntity(params);
        HttpResponse response = null;
        try {
            response = httpclient.execute(post);
        } catch (final ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing post http request");
        } catch (final IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing post http request");
        }
        return response;
    }

    private HttpResponse invokeJsonPostTestRest(final String path, final String jsonPostData) {

        final Map<String, String> header = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;

            {
                put("X-Tor-UserID", "Administrator");
            }
        };
        return invokeJsonPostTestRest(path, jsonPostData, header);
    };

    private HttpResponse invokeJsonPostTestRest(final String path, final String jsonPostData, final Map<String, String> header) {

        final String restPath = String.format("%s/%s", RestHelper.NODE_SECURITY_TEST_PATH, path);
        final String url = RestHelper.getRestHttpUrl(restPath);
        final HttpPost post = new HttpPost(url);
        if (header != null) {
            for (final Entry<String, String> e : header.entrySet()) {
                post.addHeader(e.getKey(), e.getValue());
            }

        }
        post.addHeader("content-type", "application/json");
        final HttpClient httpclient = HttpClientBuilder.create().build();
        StringEntity params = null;
        try {
            params = new StringEntity(jsonPostData);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("Exception while building StringEntity, {}", e.getClass().getName());
        }
        post.setEntity(params);
        HttpResponse response = null;
        try {
            response = httpclient.execute(post);
        } catch (final ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing post http request");
        } catch (final IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing post http request");
        }
        return response;
    }

    private List<String> populateJsonDataForIpsec(final IPSEC_JSON_DATA_TYPE jsontype) {
        final List<String> jsonDataToExecuteList = new ArrayList<String>();
        switch (jsontype) {
        case VALID:
            jsonDataToExecuteList.add(jsonDataConfig1_valid);
            jsonDataToExecuteList.add(jsonDataConfig2_valid);
            jsonDataToExecuteList.add(jsonDataConfigDisabled_valid);
            jsonDataToExecuteList.add(jsonDataCombined_valid);
            break;
        case INVALID_CONFIG:
            jsonDataToExecuteList.add(jsonDataConfig1_invalid);
            jsonDataToExecuteList.add(jsonDataConfig2_invalid);
            jsonDataToExecuteList.add(jsonDataConfigDisabled_invalid);
            jsonDataToExecuteList.add(jsonDataCombined_invalid);
            break;
        case INVALID_FORMAT_JSONMAPPINGEXCEPTION:
            jsonDataToExecuteList.add(jsonDataConfig1_invalid_JSONMAPPINGEXCEPTION);
            break;
        case INVALID_FORMAT_JSONPARSEEXCEPTION:
            jsonDataToExecuteList.add(jsonDataConfig1_invalid_JSONPARSEEXCEPTION);
            break;
        case INVALID_FORMAT_UNRECOGNIZEDPROPERTYEXCEPTION:
            jsonDataToExecuteList.add(jsonDataConfig1_invalid_UNRECOGNIZEDPROPERTYEXCEPTION);
            break;
        default:
            break;
        }

        return jsonDataToExecuteList;
    }

    /**
     * Delete the job id cache
     */
    private void performJobCacheResetRest() {
        final Map<String, String> header = new HashMap<String, String>() {

            private static final long serialVersionUID = 1L;

            {
                put("X-Tor-UserID", "Administrator");
            }
        };

        HttpResponse response = null;
        logger.info("-----------performJobCacheResetRest starts--------------");
        final String url = RestHelper.getRestHttpUrl(RestHelper.NODE_SECURITY_JOB_PATH);
        final HttpDelete delete = new HttpDelete(url);
        if (header != null) {
            for (final Entry<String, String> e : header.entrySet()) {
                delete.addHeader(e.getKey(), e.getValue());
            }

        }
        //post.addHeader("content-type", "application/json");
        final HttpClient httpclient = HttpClientBuilder.create().build();
        try {
            response = httpclient.execute(delete);
        } catch (final ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing http request [{}]", delete.getURI().toString());
        } catch (final IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing http request [{}]", delete.getURI().toString());
        }
    }

    private HttpResponse performWfCacheUpdateRest(final String stringUUID, final String nodeName, final String wfState) {

        final Map<String, String> header = new HashMap<String, String>() {
            /**
            *
            */
            private static final long serialVersionUID = 1L;

            {
                put("X-Tor-UserID", "Administrator");
            }
        };

        HttpResponse response = null;
        final String url = RestHelper.getRestHttpUrl(RestHelper.NODE_SECURITY_JOB_PATH) + "/" + stringUUID + "/" + nodeName + "/" + wfState;
        logger.info("-----------performJobCacheUpdateRest starts url [{}] --------------", url);
        final HttpPut put = new HttpPut(url);
        if (header != null) {
            for (final Entry<String, String> e : header.entrySet()) {
                put.addHeader(e.getKey(), e.getValue());
            }

        }

        final HttpClient httpclient = HttpClientBuilder.create().build();
        try {
            response = httpclient.execute(put);
        } catch (final ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing http request [{}]", put.getURI().toString());
        } catch (final IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing http request [{}]", put.getURI().toString());
        }
        return response;

    }

    private HttpResponse performJobCacheCreateRest() {
        final Map<String, String> header = new HashMap<String, String>() {
            /**
            *
            */
            private static final long serialVersionUID = 1L;

            {
                put("X-Tor-UserID", "Administrator");
            }
        };

        HttpResponse response = null;
        logger.info("-----------performJobCacheCreateRest starts--------------");
        final String url = RestHelper.getRestHttpUrl(RestHelper.NODE_SECURITY_JOB_PATH);
        final HttpPost post = new HttpPost(url);
        if (header != null) {
            for (final Entry<String, String> e : header.entrySet()) {
                post.addHeader(e.getKey(), e.getValue());
            }

        }
        final HttpClient httpclient = HttpClientBuilder.create().build();

        try {
            response = httpclient.execute(post);
        } catch (final ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing http request [{}]", post.getURI().toString());
        } catch (final IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing http request [{}]", post.getURI().toString());
        }
        return response;
    }

    private HttpResponse performJobCacheGetRest(String path) {

        if (path == null) {
            path = "";
        }

        final Map<String, String> header = new HashMap<String, String>() {

            private static final long serialVersionUID = 1L;

            {
                put("X-Tor-UserID", "Administrator");
            }
        };

        HttpResponse response = null;
        logger.info("-----------performJobCacheGetRest starts--------------");
        final String url = RestHelper.getRestHttpUrl(RestHelper.NODE_SECURITY_JOB_PATH) + "/" + path;
        final HttpGet get = new HttpGet(url);
        if (header != null) {
            for (final Entry<String, String> e : header.entrySet()) {
                get.addHeader(e.getKey(), e.getValue());
            }

        }
        final HttpClient httpclient = HttpClientBuilder.create().build();

        try {
            response = httpclient.execute(get);
        } catch (final ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing http request [{}]", get.getURI().toString());
        } catch (final IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing http request [{}]", get.getURI().toString());
        }
        return response;
    }

    private HttpResponse performJobCacheDeleteRest(String path) {

        if (path == null) {
            path = "";
        }

        final Map<String, String> header = new HashMap<String, String>() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
                put("X-Tor-UserID", "Administrator");
            }
        };

        HttpResponse response = null;
        logger.info("-----------performJobCacheCreateRest starts--------------");
        final String url = RestHelper.getRestHttpUrl(RestHelper.NODE_SECURITY_JOB_PATH) + "/" + path;
        final HttpDelete delete = new HttpDelete(url);
        if (header != null) {
            for (final Entry<String, String> e : header.entrySet()) {
                delete.addHeader(e.getKey(), e.getValue());
            }

        }
        final HttpClient httpclient = HttpClientBuilder.create().build();

        try {
            response = httpclient.execute(delete);
        } catch (final ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Exception ClientProtocolException while executing http request [{}]", delete.getURI().toString());
        } catch (final IOException e) {
            e.printStackTrace();
            logger.error("Exception IOException while executing http request [{}]", delete.getURI().toString());
        }
        return response;
    }

    public String extractUuId(final String response) {
        String c = null;
        final Pattern p = Pattern.compile(uuidPattern);
        final Matcher m = p.matcher(response);
        if (m.find()) {
            c = m.group(0);
        } else {
            logger.error("Job ID not found");
        }
        return c;
    }

}
