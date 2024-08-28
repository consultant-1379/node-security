package com.ericsson.nms.security.nscs.integration.jee.test.rest;

public interface RestTests {

    void testRestSmrsAccount() throws Exception;

    void testRestSmrsAddress() throws Exception;

    void testRestSmrsDeleteAccount() throws Exception;

    void testRestMoActionWithoutParam() throws Exception;

    void testRestMoActionInitCertEnrollment() throws Exception;

    void testRestMoActionInstallTrustedCertificates() throws Exception;

    void testRestNodesList() throws Exception;

    void testRestNodesCount() throws Exception;

    void testRestNodesIpsec() throws Exception;

    void testRestNodesVerifyIpsec() throws Exception;

    void testRestNodesVerifyInvalidIpsec() throws Exception;

    void testRestNodesVerifyInvalidIpsec_JsonMappingExceptionMapper() throws Exception;

    void testRestNodesVerifyInvalidIpsec_UnrecognizedPropertyExceptionMapper() throws Exception;

    void testRestNodesVerifyInvalidIpsec_JsonParseExceptionMapper() throws Exception;

    void testRestPIBModelRestResource_neCertAutoRenewalTimer() throws Exception;

    void testRestPIBModelRestResource_neCertAutoRenewalEnabled() throws Exception;

    void testRestPIBModelRestResource_neCertAutoRenewalMax() throws Exception;

    void testRestPIBModelRestResource_wfCongestionThreshold() throws Exception;

    void testRestPIBModelRestResource_BadRequest() throws Exception;

    void testRestJobGetPendingWorkflowsTest() throws Exception;

    void testRestJobCheckNoRunningWFbyNodeNameTest() throws Exception;

    void testRestJobGetRunningWorkflowCountTest() throws Exception;

    void testRestJobEvictionTest() throws Exception;

}
