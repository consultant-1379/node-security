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

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;

public class ParserTrustGetTest extends AbstractParserTest {
	
	private final byte[] FILE_WITH_SPACES = convertFileToByteArray("src/test/resources/testFileSpaces.txt");
    private final byte[] FILE_WITH_TABS = convertFileToByteArray("src/test/resources/testFileTabs.txt");
    private final byte[] FILE_WITH_SEMI_COLONS = convertFileToByteArray("src/test/resources/testFileSemiColons.txt");
    private final byte[] FILE_WITH_NEW_LINE = convertFileToByteArray("src/test/resources/testFileNewLine.txt");
    private final byte[] EMPTY_FILE = convertFileToByteArray("src/test/resources/emptyFile.txt");
    
	private Map<String,Object> properties = new HashMap<String, Object>();
	private final String node = "node1";
	private final String nodeList = "node1, node2, node3, node4, node5, MeContext=node6, NetworkElement=node7";
	private final List<String> expectedNodesFromFile = Arrays.asList("node1","node2","node3","node4","node5","MeContext=node6","NetworkElement=node7");
	
	@Test
	public void testTrustGet() {
		final NscsCliCommand command = new NscsCliCommand("trust get --certtype OAM --nodelist " + node);
        parse(command);
    }
	
	@Test(expected = CommandSyntaxException.class)
	public void testTrustGet__reverseorder() {
		final NscsCliCommand command = new NscsCliCommand("trust get --nodelist " + node +" --certtype OAM");
        parse(command);
	}
	
	@Test
	public void testTrustGet__nodeList() {
		final NscsCliCommand command = new NscsCliCommand("trust get --certtype OAM --nodelist " + nodeList);
        parse(command);
    }

	@Test
	public void testTrustGet__certtype_nodelist_shortcut() {
		final NscsCliCommand command = new NscsCliCommand("trust get -ct OAM -n " + node);
        parse(command);
    }
	
	@Test(expected = CommandSyntaxException.class)
	public void testTrustGet__no_certtype() {
		final NscsCliCommand command = new NscsCliCommand("trust get --nodelist " + nodeList);
        parse(command);
    }

	@Test
	public void testTrustGet__nodefile__newline() {	
		properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("trust get --certtype OAM --nodefile file:testFileNewLine.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
	}
	
	@Test
	public void testTrustGet__nodefile__tab() {
		properties.put("file:", FILE_WITH_TABS);
        final NscsCliCommand command = new NscsCliCommand("trust get --certtype OAM --nodefile file:testFileTabs.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
	}
	
	@Test
	public void testTrustGet__nodefile__spaces() {
		properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("trust get --certtype OAM --nodefile file:testFileSpaces.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
	}
	
	@Test
	public void testTrustGet__nodefile__semicolon() {
		properties.put("file:", FILE_WITH_SEMI_COLONS);
        final NscsCliCommand command = new NscsCliCommand("trust get --certtype OAM --nodefile file:testFileSemiColons.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
	}
	
	@Test
	public void testTrustGet__nodefile__emptyfile() {
		properties.put("file:", EMPTY_FILE);
        final NscsCliCommand command = new NscsCliCommand("trust get --certtype OAM --nodefile file:emptyFile.txt", properties);
        parseCommandAndAssertFail(command);
	}
	
	
	private byte[] convertFileToByteArray(final String fileLocation) {
        final File file = new File(fileLocation);
        FileInputStream fileInputStream=null;

        final byte[] fileToBeParsed = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileToBeParsed);
            fileInputStream.close();
        }catch(Exception e){
            
            e.printStackTrace();
        }
        return fileToBeParsed;
    }
	
}
