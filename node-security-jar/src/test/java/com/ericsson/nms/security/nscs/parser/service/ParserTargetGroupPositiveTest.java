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
public class ParserTargetGroupPositiveTest extends AbstractParserTest {
	
	@Test
	public void testADD_TARGET_GROUPS_CMD() {
		assertValidTGCommand(
				"targetgroup add --targetgroups group1 -n node1",
				NscsCommandType.ADD_TARGET_GROUPS,Arrays.asList("node1"),Arrays.asList("group1"));
						
		assertValidTGCommand(
				"targetgroup add --targetgroups \"group1\" -n node1",
				NscsCommandType.ADD_TARGET_GROUPS,Arrays.asList("node1"),Arrays.asList("group1"));
						
		assertValidTGCommand(
				"targetgroup add --targetgroups group1,group2 --nodelist node1",
				NscsCommandType.ADD_TARGET_GROUPS,Arrays.asList("node1"),Arrays.asList("group1", "group2"));
						
		assertValidTGCommand(
				"targetgroup add --targetgroups group1,group2 --nodelist node1,node2",
				NscsCommandType.ADD_TARGET_GROUPS,Arrays.asList("node1", "node2"),Arrays.asList("group1", "group2"));
						
		assertValidTGCommand(
				"targetgroup add -tg group1,group2 -n node1,node2",
				NscsCommandType.ADD_TARGET_GROUPS,Arrays.asList("node1", "node2"),Arrays.asList("group1", "group2"));
						
		assertValidTGCommand(
                "targetgroup add -tg group1,group2 -n NetworkElement=node1",
                NscsCommandType.ADD_TARGET_GROUPS,Arrays.asList("NetworkElement=node1"),Arrays.asList("group1", "group2"));                       
	}
}