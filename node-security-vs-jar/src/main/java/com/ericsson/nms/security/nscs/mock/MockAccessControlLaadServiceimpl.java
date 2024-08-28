/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.security.accesscontrol.AccessControlLaadService;
import com.ericsson.oss.services.security.accesscontrol.LaadData;
import com.ericsson.oss.services.security.accesscontrol.exception.LaadExceededUsersException;
import com.ericsson.oss.services.security.accesscontrol.exception.LaadNoSecurityManagmentUserException;
import com.ericsson.oss.services.security.accesscontrol.exception.LaadTargetNotFoundException;

/**
 * Mocked implementation of AccessControlLaadService to provides Laad data for cucumber scenarios</p>
 * 
 * @author xpradks
 */

@Stateless
public class MockAccessControlLaadServiceimpl implements AccessControlLaadService {

    Logger log = LoggerFactory.getLogger(MockAccessControlLaadServiceimpl.class);

    @Override
    public List<LaadData> getLaadData(String nodeName, String hashAlgorithm) throws LaadExceededUsersException, LaadNoSecurityManagmentUserException,
            LaadTargetNotFoundException {
        final List<LaadData> laadDataList = new ArrayList<>();
        final List<String> taskProfile = Arrays.asList("SecurityManagement", "CM-Normal", "FM-Normal");
        LaadData laadData = new LaadData();

        switch (nodeName) {
        case "RNC04LaadDistribution":
        case "RNC05LaadRedistribution":
            laadData.setUserName("Captain_Ericsson");
            laadData.setPasswordHash("pwdHash");
            laadData.setTaskProfiles(taskProfile);
            laadDataList.add(laadData);
            break;
        case "RNC01NeWithExceedMaxUsers":
            throw new LaadExceededUsersException("Maximum users exceded for the node " + nodeName);
        case "RNC02NeWithNoSecurityUsers":
            throw new LaadNoSecurityManagmentUserException("No security user found for the node  " + nodeName);
        case "RNC03NeWithNoTargetGroup":
            throw new LaadTargetNotFoundException("No target group found for the node " + nodeName);
        default:
            laadData.setUserName("Default user");
            laadData.setPasswordHash("pwdHash");
            laadData.setTaskProfiles(taskProfile);
            laadDataList.add(laadData);
            break;
        }
        log.info("Mocked Laad Data for the node : {} is {}", nodeName, laadDataList);
        return laadDataList;
    }

    @Override
    public List<String> getTaskProfiles() {
        return Arrays.asList("SecurityManagement", "CM-Normal", "FM-Normal");
    }
}