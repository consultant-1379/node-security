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
package com.ericsson.nms.security.nscs.integration.jee.test.setup;

import com.ericsson.nms.security.nscs.integration.jee.test.rest.RestHelper;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class PIBHelper {
	
	public static final String CERTIFICATE_DIRECTORY = "./target/ericsson/tor/smrs/lte/certificates/";
    public static final String SOFTWARE_DIRECTORY = "./ericsson/tor/smrs/lran/software/erbs/";
    public static final String LICENCE_DIRECTORY = "/ericsson/tor/smrs/lran/licence/erbs/";
    public static final String BACKUP_DIRECTORY = "/ericsson/tor/smrs/lran/backup/erbs/";
    public static final String LAAD_DIRECTORY = "/ericsson/tor/smrs/lran/laad/erbs/";
    
    public static final String paramNameSoft = "softwareDirectory";
    public static final String paramValueSoft = SOFTWARE_DIRECTORY;
    public static final String paramNameLice = "licenceDirectory";
    public static final String paramValueLice = LICENCE_DIRECTORY;
    public static final String paramNameBackup = "backupDirectory";
    public static final String paramValueBackup = BACKUP_DIRECTORY;
    public static final String paramNameLaad = "laadDirectory";
    public static final String paramValueLaad = LAAD_DIRECTORY;
    public static final String paramNameCert = "certificatesDirectory";
    public static final String paramValueCert = CERTIFICATE_DIRECTORY;

    public static void changeConfiguredPropertyValue(final String paramName, final String paramValue) throws ClientProtocolException, IOException {
        final String updateUrlPrefix = RestHelper.getRestHttpUrl("/pib/configurationService/updateConfigParameterValue?paramName=");
        final String updateUrl = updateUrlPrefix + paramName + "&paramValue=" + paramValue;
        final HttpGet httpget = new HttpGet(new URL(updateUrl).toExternalForm());
		final HttpClient httpclient = HttpClientBuilder.create().build();
        httpclient.execute(httpget);        
    }
	public static void changeSmrsFolderConfig() throws Exception {

		changeConfiguredPropertyValue(paramNameCert, paramValueCert);
        changeConfiguredPropertyValue(paramNameSoft, paramValueSoft);
        changeConfiguredPropertyValue(paramNameLice, paramValueLice);
        changeConfiguredPropertyValue(paramNameBackup, paramValueBackup);
        changeConfiguredPropertyValue(paramNameLaad, paramValueLaad);

        //Folder needs to be created before the tests, because Resources API can create the files if the folder(s) already exists
        new File(CERTIFICATE_DIRECTORY).mkdirs();
        new File(SOFTWARE_DIRECTORY).mkdirs();
        new File(LICENCE_DIRECTORY).mkdirs();
        new File(BACKUP_DIRECTORY).mkdirs();
        new File(LAAD_DIRECTORY).mkdirs();
    }
}
