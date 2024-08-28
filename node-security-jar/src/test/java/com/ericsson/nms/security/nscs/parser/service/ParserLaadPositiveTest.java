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
import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;

@Ignore("LAAD not supported yet")
public class ParserLaadPositiveTest extends AbstractParserTest {

	@Test
	public void testCPP_INSTALL_LAAD() {

		parseCommandAndAssertSuccess("laad update -n node1",
				NscsCommandType.CPP_INSTALL_LAAD,
				new HashMap<String, Object>() {
					{
						{
							put(NscsPropertyCommand.COMMAND_TYPE_PROPERTY,
									"CPP_INSTALL_LAAD");
							put(NscsNodeCommand.NODE_LIST_PROPERTY,
									Arrays.asList("node1"));
						}
					}
				});

		parseCommandAndAssertSuccess("laad update *",
				NscsCommandType.CPP_INSTALL_LAAD,
				new HashMap<String, Object>() {
					{
						{
							put(NscsPropertyCommand.COMMAND_TYPE_PROPERTY,
									"CPP_INSTALL_LAAD");
							put(NscsNodeCommand.NODE_LIST_PROPERTY, "*");
						}
					}
				});

		parseCommandAndAssertSuccess("laad update -n node1,node2",
				NscsCommandType.CPP_INSTALL_LAAD,
				new HashMap<String, Object>() {
					{
						{
							put(NscsPropertyCommand.COMMAND_TYPE_PROPERTY,
									"CPP_INSTALL_LAAD");
							put(NscsNodeCommand.NODE_LIST_PROPERTY,
									Arrays.asList("node1", "node2"));
						}
					}
				});
		
        parseCommandAndAssertSuccess("laad update -n NetworkElement=node1",
                NscsCommandType.CPP_INSTALL_LAAD,
                new HashMap<String, Object>() {
                    {
                        {
                            put(NscsPropertyCommand.COMMAND_TYPE_PROPERTY,
                                    "CPP_INSTALL_LAAD");
                            put(NscsNodeCommand.NODE_LIST_PROPERTY,
                                    Arrays.asList("NetworkElement=node1"));
                        }
                    }
                });
	}

}
