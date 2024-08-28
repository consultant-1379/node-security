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
package com.ericsson.oss.services.gdpr.anonymize;

import java.security.NoSuchAlgorithmException;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.ldap.utility.PlatformConfigurationReader;
import com.ericsson.oss.gdpr.anonymize.api.GdprAnonymizer;
import com.ericsson.oss.gdpr.anonymize.exception.GdprAnonymizerException;

@Stateless
public class GdprAnonymizerImpl implements GdprAnonymizer {
    private static final Logger logger = LoggerFactory.getLogger(GdprAnonymizerImpl.class);
    public static final String GDPR_MATCH_CH = ".";

    @Inject
    private PlatformConfigurationReader platformConfigurationReader;

    @Inject
    private GdprHashing gdprHashing;

    @Override
    public String gdprBuildAnonymization(final String s) {
        logger.debug("gdpr BuildAnonymization : starts");

        if( s == null ) {
            throw new GdprAnonymizerException(GdprConstantsUtils.GDPR_INVALID_INPUT_PARMS);
        }

        try {
            final String shuffledMsg = gdprShuffleMsg(gdprBuildSalt(GdprConstantsUtils.GDPR_GLB_PROP_ID_KEY), s);
            return gdprHashing.gdprBuildHashing(shuffledMsg, GdprConstantsUtils.GDPR_HASH_ALGO);
        } catch (NoSuchAlgorithmException e) {
            throw new GdprAnonymizerException(e.getMessage());
        }
    }

    @Override
    public String gdprBuildAnonymization(final String s, final String salt) {
        logger.debug("gdpr BuildAnonymization with salt: starts");

        if(( s == null ) || ( salt == null )){
            throw new GdprAnonymizerException(GdprConstantsUtils.GDPR_INVALID_INPUT_PARMS);
        }

        try {
            return gdprHashing.gdprBuildHashing(gdprShuffleMsg(salt,s), GdprConstantsUtils.GDPR_HASH_ALGO);
        } catch (NoSuchAlgorithmException e) {
            throw new GdprAnonymizerException(e.getMessage());
        }
    }

    /**
     * To enlarge set size to hash PSS asks to combine in some manner input data and
     * identifier of ENM installation.
     * For now rule is simple a string concatenation.
     *
     * @param s1 first data to be shuffled
     * @param s2 second data to be shuffled
     * @return data obtained by shuffle algorithm
     */
    private String gdprShuffleMsg (final String s1, final String s2) {
        return s1 + "_" + s2 ;
    }

    /**
     *
     * @param key key in global.properties for getting indenfier of ENM installation
     * @return the indentifier of the ENM installation
     */
    private String gdprBuildSalt (final String key) {
        final String value = platformConfigurationReader.getProperty(key);
        if ( value == null ) {
            throw new GdprAnonymizerException(GdprConstantsUtils.GDPR_GLB_PROP_ID_NOT_READ);
        }

        if ( !value.contains(GDPR_MATCH_CH) ){
            throw new GdprAnonymizerException(GdprConstantsUtils.GDPR_GLB_PROP_ID_NOT_OK);
        }

        final String salt = value.substring(0,value.indexOf(GDPR_MATCH_CH));

        if( salt.isEmpty() ) {
            throw new GdprAnonymizerException(GdprConstantsUtils.GDPR_GLB_PROP_ID_NOT_OK);
        }

        return salt;
    }
}