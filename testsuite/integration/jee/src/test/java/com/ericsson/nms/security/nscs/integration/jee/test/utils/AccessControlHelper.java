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
package com.ericsson.nms.security.nscs.integration.jee.test.utils;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Arrays;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.*;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.classic.EAccessControlBean;

public class AccessControlHelper {

	private static String jbossHome;
	private static String policyDir;
	private static String propertyFile;
	private static EAccessControl accesscontrol = new EAccessControlBean();
	
	public static void setupAccessControl() {
		setAccessControlProperties();
		assertNotNull(accesscontrol);                
		setupUser("toruser");
	}
	/**
	 * Sets properties required by Access Control
	 */
	protected static void setAccessControlProperties() {
		jbossHome = System.getProperty("jboss.home.dir");
		System.out.println("setAccessControlProperties: jboss.home.dir is {} " + jbossHome);

		// property to tell OpenAZ where the policy file is
		policyDir = String.format("%s/../test-classes/policy", jbossHome);
		System.setProperty("com.ericsson.oss.PolicyDirectory", policyDir);

		// property file used to locate and connect to OpenDj
		propertyFile = String.format("%s/../test-classes/accesscontrol.global.properties", jbossHome);
		System.setProperty("configuration.java.properties", propertyFile);
	}

	/**
	 * Sets up a user
	 * @param user
	 * @return
	 */
	public static ESecuritySubject setupUser(final String user) {
		final String tmpDir = System.getProperty("java.io.tmpdir");
		final String useridFile = String.format("%s/currentAuthUser", tmpDir);
		System.out.println("setupUser: user is {}, useridFile is {}" + user + " , " + useridFile);
		try {
			final File uidFile = new File(useridFile);
			uidFile.setWritable(true, false);
			final FileWriter fw = new FileWriter(uidFile);
			fw.write(user);
			fw.close();
		} catch (final Exception e) {
			System.out.println(String.format("Error writing user <%s> to %s, Details %s", user, useridFile, e.getMessage()));
			return null;
		}
		ESecuritySubject aSubject = null;
		try {
			aSubject = accesscontrol.getAuthUserSubject();
		} catch (final SecurityViolationException sve) {
			System.out.println(String.format("SecurityViolationException caught: %s", sve.getMessage()));
		}
		System.out.println("user returned from access control is " + aSubject);
		return aSubject;
	}

	/**
	 * Helper method used to start and stop OpenDj
	 * @param cmd
	 * @return
	 */
	protected static boolean runCmd(final String[] cmd) {
		int exitCode = -1;
		try {
			final Process p = Runtime.getRuntime().exec(cmd);
			exitCode = p.waitFor();
			final BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			final BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			// read the output from the command
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			// read any errors from the attempted command
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
		} catch (final Exception e) {
			System.out.println("exception from cmd: " + cmd);
			return false;
		}
		if (exitCode == 0) {
			return true;
		}
		return false;
	}

	public static void startOpenDj() {

		final String osName = System.getProperty("os.name");
		final String jbossHome = System.getProperty("jboss.home");
		final String opendjHome = String.format("%s/../../opendj-test", jbossHome);
		final String[] startCmd;

		if (osName.equals("Linux")) {
			startCmd = new String[] { String.format("%s/opendj/bin/start-ds", opendjHome) };
		} else {
			startCmd = new String[] { String.format("%s/opendj/bat/start-ds.bat", opendjHome) };
		}

		System.out.println("startOpenDj: startCmd is <{}>" + startCmd);
		final boolean isOpenDjInstalled = Boolean.getBoolean("sfwk.opendj.installed");

		System.out.println(String.format("setupTest: isOpenDjInstalled is %s", Boolean.getBoolean("sfwk.opendj.installed")));
		if (!isOpenDjInstalled) {
			return;
		}
		assertTrue(runCmd(startCmd));
		System.out.println("startOpenDj: OpenDj has been started.");
	}

	public static void stopOpenDj() {
		final String osName = System.getProperty("os.name");
		final String jbossHome = System.getProperty("jboss.home");
		final String opendjHome = String.format("%s/../../opendj-test", jbossHome);
		final String[] stopCmd;
		if (osName.equals("Linux")) {
			stopCmd = new String[] { String.format("%s/opendj/bin/stop-ds", opendjHome) };
		} else {
			stopCmd = new String[] { String.format("%s/opendj/bat/stop-ds.bat", opendjHome) };
		}
		
		System.out.println(String.format("stopOpenDj: stopCmd is <%s>", Arrays.toString(stopCmd)));
		
		final boolean isOpenDjInstalled = Boolean.getBoolean("sfwk.opendj.installed");
		System.out.println("stopOpenDj: isOpenDjInstalled is {}" + (isOpenDjInstalled ? "true" : "false"));
		
		if (!isOpenDjInstalled) {
			return;
		}
		
		assertTrue(runCmd(stopCmd));
		System.out.println("stopOpenDj: OpenDj has been stopped.");
	}
}
