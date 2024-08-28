/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.parser.service;


import org.junit.Ignore;
import org.junit.Test;

import com.ericsson.nms.security.nscs.parser.service.AbstractParserTest;

/**
 * 
 * @author emehsau
 *
 */
@Ignore("TORF-38537 - not supported in 14B")
public class IpSecCommandParserTest extends AbstractParserTest {

	@Test
	public void testIPSecWrongSyntax() {
		// Wrong type
		parseCommandAndAssertFail("ipseca transport enable file=abc.txt");
		parseCommandAndAssertFail("ipsecadm transport enables file=abc.txt");
		parseCommandAndAssertFail("ips transport enables file=abc.txt");
		// Wrong operation
		parseCommandAndAssertFail("ipsec transport enables file=abc.txt");
		parseCommandAndAssertFail("ipsec transport disables file=abc.txt");
		parseCommandAndAssertFail("ipsec transport activate file=abc.txt");
		parseCommandAndAssertFail("ipsec transport deactivate file=abc.txt");
		//Operation type is missing
		parseCommandAndAssertFail("ipsec transport file=abc.txt");
		parseCommandAndAssertFail("ipsec om file=abc.txt");
		// Missing file or different  wrong input
		parseCommandAndAssertFail("ipsec transport deactivate ;node1;node2");
		parseCommandAndAssertFail("ipsec transport deactivate -n=node1");
		parseCommandAndAssertFail("ipsec transport deactivate");
		parseCommandAndAssertFail("ipsec transport deactivate file=abc.txt -l 3");
		parseCommandAndAssertFail("ipsec om");
		parseCommandAndAssertFail("ipsec transport deactivate file=abc.txt -l 2 -XX node1");
		parseCommandAndAssertFail("ipsec transport deactivate file=abc.txt -l 3 -n file:abc.txt");
	}


	@Test
	public void testWrongIpSecOMCommandsSyntax() {
		parseCommandAndAssertFail("ipsec om enables file=abc.txt");
		
		parseCommandAndAssertFail("ipsec om transport enable ");
		parseCommandAndAssertFail("ipsecs om enables");
		parseCommandAndAssertFail("ipsec om enable -var=value");
		parseCommandAndAssertFail("ipsec om enable -var=value; -var2= value2");
	}


	@Test
	public void testWrongIpSecTransportCommand() {
		parseCommandAndAssertFail("ipsec transport enables --continue");
		parseCommandAndAssertFail("ipsec transport disables");
		parseCommandAndAssertFail("ipsec om transport enable ");
		parseCommandAndAssertFail("ipsecs transport enables");
		parseCommandAndAssertFail("ipsec transports enable -var=value");
		parseCommandAndAssertFail("ipsec transport enable -var=value; -var2= value2");
	}

}


