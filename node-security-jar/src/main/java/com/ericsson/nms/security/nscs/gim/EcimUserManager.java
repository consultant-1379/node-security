/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.gim;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.ldap.EcimPwdFileManager;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.security.genericidentitymgmtserviceapi.ecimuser.EcimUserRemote;
import com.ericsson.oss.services.security.genericidentitymgmtserviceapi.exceptions.EntityNotFoundException;
import com.ericsson.oss.services.security.genericidentitymgmtserviceapi.exceptions.InternalUnexpectedException;

@ApplicationScoped
public class EcimUserManager {

    public static final String ECIM_COMMON_USER_NAME_BASE = "ldapApplicationUser";
    private static final int CLUSTERLOCK_TIMEOUT = 15;
    private static final String LDPA_APPLICATION_USER_CLUSTER_LOCK = "ldpaApplicationUserClusterLock";
    public static int ECIM_COMMON_USER_PASSWORD_LENGTH = 10;

    @Inject
    private Logger logger;

    @Inject
    private LockManager lockManager;

    @EJB
    private EcimPwdFileManager ecimPwdFileManager;

    @Inject
    private PasswordHelper passwordHelper;

    @EServiceRef
    private EcimUserRemote ecimUserRemote;

    private String username;

    @PostConstruct
    void init() {
        username = ECIM_COMMON_USER_NAME_BASE;
    }

    /**
     * Retrieves the ENM LDAP User password.
     *
     * @return the password of the ENM LDAP User and the boolean flag for the propagation.
     */
    public Map<String, Boolean> provideEcimSecurePassword() {
        final Map<String, Boolean> ret = new HashMap<String, Boolean>();
        String passw = retrieveEcimUserPwd();
        if (passw == null) {
            try {
                if (!obtainPolicyLock()) {
                    logger.warn("Failed to obtain " + LDPA_APPLICATION_USER_CLUSTER_LOCK + " lock");
                }
                passw = retrieveEcimUserPwd();
                if (passw != null) {
                    ret.put(passw, false);
                } else {
                    ret.put(generateNewEcimUser(), true);
                }
            } finally {
                releasePolicyLock();
            }
        } else {
            ret.put(passw, false);
        }
        return ret;
    }


    private String retrieveEcimUserPwd() {
        String password = null;
        try {
            password = ecimUserRemote.getEcimUserCryptoPassword();
            if (password == null) {
                logger.info("Password for EcimUser retrieved on db is null");
                // EcimUser present in db but pwd null
                // read the password from filesystem and write it on db
                password = ecimPwdFileManager.readPassword(ECIM_COMMON_USER_NAME_BASE);
                if (password != null) {
                    logger.info("Password for EcimUser retrieved from filesystem");
                    if (!ecimUserRemote.setEcimUserCryptoPassword(password)) {
                        return null;
                    }
                }
                return password;
            }
        } catch (final EntityNotFoundException ex) {
            logger.info("EntityNotFoundException : {}", ex.getMessage() != null ? ex.getMessage() : "");
            return password;
        } catch (final InternalUnexpectedException ex) {
            logger.info("InternalUnexpectedException : {}", ex.getMessage() != null ? ex.getMessage() : "");
            throw ex;
        }
        return passwordHelper.decryptDecode(password);
    }

    private String generateNewEcimUser() {
        final String newPassword = ecimUserRemote.generateECIMUserPassword(username);
        final boolean userCreated = ecimUserRemote.checkECIMUserExist();
        if (userCreated) {
            logger.info("deleting ECIM CommonUser...");
            ecimUserRemote.deleteECIMUser();
        }
        ecimUserRemote.createECIMUser(username, newPassword);
        return newPassword;
    }

    /**
     * @return the ldap username used by this node security instance
     */
    public String getUsername() {
        return username;
    }

    private boolean obtainPolicyLock() {
        try {
            logger.info("EcimUserManager About to obtain " + LDPA_APPLICATION_USER_CLUSTER_LOCK + " lock");
            final boolean result = lockManager.getDistributedLock(LDPA_APPLICATION_USER_CLUSTER_LOCK).tryLock(CLUSTERLOCK_TIMEOUT, TimeUnit.SECONDS);
            logger.info("EcimUserManager Obtain " + LDPA_APPLICATION_USER_CLUSTER_LOCK + " lock result = " + result);
            return result;
        } catch (final InterruptedException exception) {
            logger.warn("Failed to obtain " + LDPA_APPLICATION_USER_CLUSTER_LOCK + " lock");
            return false;
        }
    }

    private void releasePolicyLock() {
        logger.info("EcimUserManager About to release " + LDPA_APPLICATION_USER_CLUSTER_LOCK + " lock");
        lockManager.getDistributedLock(LDPA_APPLICATION_USER_CLUSTER_LOCK).unlock();
        logger.info("EcimUserManager Released " + LDPA_APPLICATION_USER_CLUSTER_LOCK + " lock");
    }
}
