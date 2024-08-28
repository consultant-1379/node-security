/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.laad.service;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.laad.ex.LaadServiceException;
import com.ericsson.nms.security.nscs.laad.FormatterException;
import com.ericsson.nms.security.nscs.laad.LAADData;
import com.ericsson.nms.security.nscs.laad.LAADFile;
import com.ericsson.nms.security.nscs.laad.LAADGenerator;
import com.ericsson.nms.security.nscs.usermanagment.UserManagmentService;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.itpf.sdk.resources.ResourcesException;
import com.ericsson.oss.itpf.smrs.SmrsAccount;
import com.ericsson.oss.itpf.smrs.SmrsService;

/**
 * <p>
 * This implementation of InstallLaad fetches user information from UserManagmentService generates LAAD files and stores them in the Smrs user home
 * directory.
 * </p>
 * 
 * @author enatbol
 *
 *         Should return file name/ smrs account
 */
public class InstallLaadService implements InstallLaad {

    private char[] pass;
    private Set<LAADData> set = new HashSet<>();
    private LAADData[] data;

    @EServiceRef
    private SmrsService smrsService;

    @Inject
    private UserManagmentService userMangmt;

    @Inject
    private SmrsAccount smrsAccount;

    @Inject
    private LAADGenerator laadGenerator;

    @Inject
    private Logger logger;

    @Inject
    private ResourcesBean resourcesBean;

    @Inject
    private LAADFile laadFile;

    private static final String authenticationfile = "laad_authentication.xml";
    private static final String authorizationfile = "laad_authorization.xml";

    /**
     * Perform LAAD files generation and publication. See class javadoc for more info.
     * 
     * @return SmrsAccount used during Laad file installation
     * @throws LaadServiceException
     */
    @Override
    public SmrsAccount installLaad() throws LaadServiceException {
        logger.info("InstallLaadService.installLaad method entered");
        //Get LAAD Data form Usermanagment service
        set = userMangmt.getLaadData();
        //set.toArray(data);
        data = new LAADData[2];
        int i = 0;

        for (final LAADData laad : set) {
            data[i] = laad;
            i++;
        }

        //Generate LAADfile
        try {
            laadFile = generateLaadFile(data);
        } catch (final FormatterException ex) {
            logger.error("InstallLaadService.installLaad {} with FormatterException message {}", LaadServiceException.Error.FAILED_TO_GENERATE_LAAD,
                    ex.getMessage());
            throw new LaadServiceException(LaadServiceException.Error.FAILED_TO_GENERATE_LAAD);

        } catch (final NoSuchAlgorithmException ex) {
            logger.error(" InstallLaadService.installLaad {} with NoSuchAlgorithmException message {}",
                    LaadServiceException.Error.FAILED_TO_GENERATE_LAAD, ex.getMessage());
            throw new LaadServiceException(LaadServiceException.Error.FAILED_TO_GENERATE_LAAD);
        }

        smrsAccount = getSmrsAccount();

        //Store LAAD file
        storeLaad(laadFile.getAuthentication().toByteArray(), smrsAccount, authenticationfile);
        storeLaad(laadFile.getAuthorization().toByteArray(), smrsAccount, authorizationfile);

        //Notify Node for now all we do is log data. Waiting for MO action
        logger.debug(
                "InstallLaadService.installLaad method exiting. The LAAD files have been generated and stored. Nodes can now retrieve the LAAD files from the SMRS");
        return smrsAccount;
    }

    private void storeLaad(final byte[] laadData, final SmrsAccount account, final String fileName) throws LaadServiceException {
        logger.info("InstallLaadServiceCreating.storeLaad() method entered - Creating file: LAAD file");
        Resource file;
        logger.debug("Laad Directory:" + account.getHomeDirectory() + "File name:" + fileName);
        try {
            file = resourcesBean.getFileSystemResource(account.getHomeDirectory() + fileName);
            final int i = file.write(laadData, false);
            logger.debug("InstallLaadServiceCreating.storeLaad() - Resource adaptor write status:" + i);
        } catch (final ResourcesException re) {
            logger.error(
                    "InstallLaadServiceCreating.storeLaad() Caught ResourcesException with message {} while using Resource Adaptor to write file: {} - This is the ResourcesException {}",
                    re.getMessage(), laadData, re);
            throw new LaadServiceException(LaadServiceException.Error.FAILED_TO_GENERATE_LAAD);
        }
        logger.debug("InstallLaadServiceCreating.storeLaad() method exiting");
    }

    private SmrsAccount getSmrsAccount() {
        return smrsAccount = smrsService.getCommonAccount("LAAD", "ERBS");
    }

    private LAADFile generateLaadFile(final LAADData[] laadData) throws FormatterException, NoSuchAlgorithmException {
        final int version = 1;
        return laadGenerator.getLAADFile(laadData, version);
    }

    private Set<LAADData> getLaadData() {
        //return userMangmt.getLaadData();
        final char[] password = "passwordHash".toCharArray();
        final String taskProfile[] = { "Do Stuff", "Do more stuff", "Yet more stuff" };
        LAADData laadData = new LAADData("Captain_Ericsson", password, taskProfile);
        set.add(laadData);
        laadData = new LAADData("Super_Ericsson_Dude", password, taskProfile);
        set.add(laadData);
        return set;
    }

    private char[] getUserPassword() {
        final String password = "This is a top secret user managment password.....shhhh!";
        return password.toCharArray();
    }

}
