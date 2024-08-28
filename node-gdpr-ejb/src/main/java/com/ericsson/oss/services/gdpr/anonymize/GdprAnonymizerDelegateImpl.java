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

import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.gdpr.anonymize.api.GdprAnonymizer;

import javax.ejb.Stateless;

@Stateless
public class GdprAnonymizerDelegateImpl implements GdprAnonymizerDelegate{

    @EServiceRef
    private GdprAnonymizer gdprAnonymizer;

    @Override
    @Authorize(resource = "scripting_anonymizer", action = "execute")
    public String gdprBuildAnonymization(final String s) {
         return gdprAnonymizer.gdprBuildAnonymization(s);
    }

    @Override
    @Authorize(resource = "scripting_anonymizer", action = "execute")
    public String gdprBuildAnonymization(final String s, final String salt)  {
        return gdprAnonymizer.gdprBuildAnonymization(s,salt);
    }
}