/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.integration.jee.test.rest;

public class RestHelper {

    private static final String CONTAINER_ADDRESS_PROPERTY = "container.ip";
    public static final String NODE_SECURITY_TEST_PATH = "/node-security/test";
    public static final String NODE_SECURITY_JOB_PATH = String.format("%s/job", NODE_SECURITY_TEST_PATH);
    public static final String NODE_SECURITY_WORKFLOW_PATH = String.format("%s/workflow", NODE_SECURITY_TEST_PATH);

    public static final String getLocalHostAddr() {
        String localHostAddress = System.getProperty(CONTAINER_ADDRESS_PROPERTY);
        if ((localHostAddress == null) || localHostAddress.isEmpty()) {
            localHostAddress = "localhost";
        }
        return localHostAddress;
    }
    
    public static final String getRestHttpUrl(final String restPath) {
        return "http://" + getLocalHostAddr() + ":8080" + restPath;
    }
    
    
}
