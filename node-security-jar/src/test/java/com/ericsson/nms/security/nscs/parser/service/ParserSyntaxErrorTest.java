package com.ericsson.nms.security.nscs.parser.service;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by emaynes on 12/05/2014.
 */
public class ParserSyntaxErrorTest extends AbstractParserTest {

	@Test
        @Ignore("TORF-38537 - not supported in 14B")
	public void testAddTargetGroupWrongSyntax() {
		//Many commas as separators also trailing commas
		parseCommandAndAssertFail("targetgroup add -tg group1,group2 -n \"MeContext=node1\",,,,node2,,");
	}
	
	@Test
        @Ignore("TORF-38537 - not supported in 14B")
	public void testGetAndSetSecLevelWrongSyntax() {
		//Many commas as separators also trailing commas
		parseCommandAndAssertFail("sl set -l 1 -n \"MeContext=node1\",,,node2,");
		parseCommandAndAssertFail("securitylevel set -l 1 --nodelist node1,,,node2,");
		// Wrong type
		parseCommandAndAssertFail("s set -n node1");
		parseCommandAndAssertFail("securityleve set -n node1");
		parseCommandAndAssertFail("securitylevel 2 -n node1");
		// Wrong level
		parseCommandAndAssertFail("sl set -l 4 -n node1");
		parseCommandAndAssertFail("sl set -l 3 -n node1");
		parseCommandAndAssertFail("sl get -l 3 -n node1");
		parseCommandAndAssertFail("sl get -l -1 -n node1");
		parseCommandAndAssertFail("securitylevel get -l 0 -n node1");
		// Set level missing
		parseCommandAndAssertFail("sl set -n=node1");
		// Wrong list/node
		parseCommandAndAssertFail("sl get -l 2 ;node1;node2");
		parseCommandAndAssertFail("sl get -l 2 -n=node1");
		parseCommandAndAssertFail("sl get -l 2 -n ;node1;node2");
		parseCommandAndAssertFail("sl set -l 2");
		parseCommandAndAssertFail("sl get");
		parseCommandAndAssertFail("sl set -l 2 -XX node1");
		parseCommandAndAssertFail("sl get -l 2 -n file:abc.txt");
	}

	@Test
        @Ignore("TORF-38537 - not supported in 14B")
	public void testWrongInstallLaadCommandsSyntax() {
		//Many commas as separators also trailing commas
		parseCommandAndAssertFail("laad update --nodelist \"MeContext=node1\",,,node2,");
		parseCommandAndAssertFail("laad update 3 -n=node1");
		parseCommandAndAssertFail("laad -n node1");
		parseCommandAndAssertFail("laad install -n=node1");
		// Wrong list/node
		parseCommandAndAssertFail("laad update ;node1;node2");
		parseCommandAndAssertFail("laad update -nX node1,node");
		parseCommandAndAssertFail("laad update");
	}

	@Test
	public void testWrongCredentialsCommand() {		
		parseCommandAndAssertFail("credentials create");
		parseCommandAndAssertFail("credentials create --rootusername value --rootuserpassword val2 --rootuserpassword val2 --nodelist node1,node2");
		parseCommandAndAssertFail("credentials create -rn=value -rp=val2 -sn=val3 -sp=val4 -nn= val5   -np= val6 -n node1,node2");
		parseCommandAndAssertFail("credentials create -XXXX value  -n node1,node2");
		parseCommandAndAssertFail("credentials create -run value -rp val2 -sn val3 -sp val4 -nn val5   -np val6  -nX node1,node2");
	}
	
	@Test
	public void testCreateCredentialsWrongSyntax() {
		//Many commas as separators also trailing commas
		parseCommandAndAssertFail("credentials create --rootusername value --rootuserpassword val2  --nodelist \"MeContext=node1\",,\"MeContext=node2\",,");
	}
	
}

