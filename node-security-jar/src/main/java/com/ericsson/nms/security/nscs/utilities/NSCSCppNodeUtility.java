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
package com.ericsson.nms.security.nscs.utilities;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.cpp.model.CppMOEnrollmentMode;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceBean;
import com.ericsson.nms.security.nscs.data.ModelDefinition.CppManagedElement;
import com.ericsson.nms.security.nscs.data.ModelDefinition.Mo;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;

/**
 * Utility class for handling of CPP nodes
 *
 */
public class NSCSCppNodeUtility {

    @Inject
    private Logger logger;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    public static final String CPP_KEY_LENGTH_1024 = "KEY_1024";
    public static final String CPP_KEY_LENGTH_2048 = "KEY_2048";
    public static final String CPP_KEY_LENGTH_4096 = "KEY_4096";
    public static final String MOPARAMS_KEY_KEYLENGTH = "keyLength";
    public static final String MOPARAMS_KEY_ENROLLMENTMODE = "enrollmentMode";

    /**
     * Convert the given MoParams (as returned in enrollment info) to MoParamas required by CPP MoAction. This is valid for CPP nodes only!
     *
     * @param enrollmentDataParams
     * @return the converted MoParams or null if input parameter is null.
     */
    public static MoParams updateMoParamsUsingNodeModelValues(final MoParams enrollmentDataParams) {

        if (enrollmentDataParams == null) {
            return enrollmentDataParams;
        }

        if (enrollmentDataParams.getParamMap().containsKey(MOPARAMS_KEY_KEYLENGTH)) {
            final String oldValue = enrollmentDataParams.getParamMap().get(MOPARAMS_KEY_KEYLENGTH).getParam().toString();
            final String newValue = convertKeySizeToMoAction(oldValue);
            enrollmentDataParams.addParam(MOPARAMS_KEY_KEYLENGTH, newValue);
        }

        if (enrollmentDataParams.getParamMap().containsKey(MOPARAMS_KEY_ENROLLMENTMODE)) {
            final String oldValue = enrollmentDataParams.getParamMap().get(MOPARAMS_KEY_ENROLLMENTMODE).getParam().toString();
            final String newValue = convertEnrollmentModeToNodeModelEnrollmentMode(oldValue);
            enrollmentDataParams.addParam(MOPARAMS_KEY_ENROLLMENTMODE, newValue);
        }

        return enrollmentDataParams;
    }

    /**
     * Temporary solution for converting the given KeyLength, as numeric value, to the corresponding enum value for performing a CPP MoAction. This is
     * valid for CPP nodes only!
     *
     * @param keySizeValue
     * @return key size enum value
     */
    private static String convertKeySizeToMoAction(final String keySizeValue) {
        String retValue = null;
        if (CppSecurityServiceBean.KeyLength.RSA1024.toString().equals(keySizeValue)) {
            retValue = CPP_KEY_LENGTH_1024;
        }
        if (CppSecurityServiceBean.KeyLength.RSA2048.toString().equals(keySizeValue)) {
            retValue = CPP_KEY_LENGTH_2048;
        }
        if (CppSecurityServiceBean.KeyLength.RSA4096.toString().equals(keySizeValue)) {
            retValue = CPP_KEY_LENGTH_4096;
        }
        return retValue;
    }

    /**
     * Convert Enrollment Mode value to value requested by CPP nodes. It is valid for CPP nodes only!
     *
     * @param enrollmentMode
     * @return converted enrollment mode value
     */
    private static String convertEnrollmentModeToNodeModelEnrollmentMode(final String enrollmentMode) {

        String retValue = null;

        for (final CppMOEnrollmentMode cppMoEnrollmentMode : CppMOEnrollmentMode.values()) {
            if (cppMoEnrollmentMode.name().toLowerCase().equals(enrollmentMode.toLowerCase())) {
                retValue = cppMoEnrollmentMode.name();
                break;
            }
        }

        return retValue;
    }

    /**
     * gets the Security Fdn of specified root MO
     *
     * @param mirrorRootFdn
     *            the FDN of the mirrorRoot MO
     * @param rootMo
     *            the root MO
     *
     * @return the Fdn of involved Security or null
     */
    public String getSecurityFdn(final String mirrorRootFdn, final Mo rootMo) {

        logger.debug("Get securityFdn for mirrorRootFdn[{}] rootMo[{}] ", mirrorRootFdn, rootMo);

        if (mirrorRootFdn == null || mirrorRootFdn.isEmpty() || rootMo == null) {
            logger.error("Get TrustCategoryFdn : invalid value : mirrorRootFdn[{}] rootMo[{}] ", mirrorRootFdn, rootMo);
            return null;
        }

        // Get SecurityMo
        final Mo securityMo = ((CppManagedElement) rootMo).systemFunctions.security;
        final String securityFdn = nscsNodeUtility.getSingleInstanceMoFdn(mirrorRootFdn, securityMo);

        logger.debug("Get securityFdn return[{}]", securityFdn);
        return securityFdn;
    }

}
