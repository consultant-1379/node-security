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
package com.ericsson.nms.security.nscs.parser.service;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;

@Ignore("TORF-38537 - not supported in 14B")
public class ParserLevelsPositiveTest extends AbstractParserTest {
	
	@Test
	public void testCPP_GET_SL() {

		assertValidSLCommand(
				"sl get --nodelist node1",
				NscsCommandType.CPP_GET_SL, Arrays.asList("node1"), null);						

		assertValidSLCommand(
				"sl get *", 
				NscsCommandType.CPP_GET_SL, "*", null);

		assertValidSLCommand(
				"sl get -a", 
				NscsCommandType.CPP_GET_SL,"*", null);
						
		assertValidSLCommand(
				"sl get --all",
				NscsCommandType.CPP_GET_SL, "*", null);
						
		assertValidSLCommand(
				"sl get -l 1 -n node1",
				NscsCommandType.CPP_GET_SL, Arrays.asList("node1"), 1);						

		assertValidSLCommand(
				"sl get --level 1 --nodelist \"MeContext = node1\"",
				NscsCommandType.CPP_GET_SL, Arrays.asList("MeContext = node1"),1);						

		assertValidSLCommand(
				"securitylevel get --level 1 --nodelist node1,node2",
				NscsCommandType.CPP_GET_SL, Arrays.asList("node1", "node2"), 1);
						
		assertValidSLCommand(
        		"sl get --level 1 --nodelist \"NetworkElement = node1\"", 
        		NscsCommandType.CPP_GET_SL, Arrays.asList("NetworkElement = node1"), 1);
        
	}

	@Test
	public void testCPP_SET_SL() {
		assertValidSLCommand(
				"sl set -l 1 --nodelist node1",
				NscsCommandType.CPP_SET_SL, Arrays.asList("node1"), 1);						

		assertValidSLCommand(
				"sl set --level 2 *",
				NscsCommandType.CPP_SET_SL, "*", 2);							

		assertValidSLCommand(
				"securitylevel set --level 1 --nodelist node1,node2",
				NscsCommandType.CPP_SET_SL, Arrays.asList("node1", "node2"), 1);
						
		assertValidSLCommand(
				"sl set -l 1 --nodelist \"MeContext=node1\",node2",
				NscsCommandType.CPP_SET_SL, Arrays.asList("MeContext=node1", "node2"), 1);
								
		assertValidSLCommand(
                "sl set -l 1 --nodelist \"NetworkElement=node1\",node2",
                NscsCommandType.CPP_SET_SL, Arrays.asList("NetworkElement=node1", "node2"), 1);
	}	
}
