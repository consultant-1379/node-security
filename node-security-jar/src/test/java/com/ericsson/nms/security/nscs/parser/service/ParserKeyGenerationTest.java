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
import java.util.*;

import org.junit.Ignore;
import org.junit.Test;

import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;

public class ParserKeyGenerationTest extends AbstractParserTest {
	
	private final byte[] FILE_WITH_SPACES = convertFileToByteArray("src/test/resources/testFileSpaces.txt");
    private final byte[] FILE_WITH_TABS = convertFileToByteArray("src/test/resources/testFileTabs.txt");
    private final byte[] FILE_WITH_SEMI_COLONS = convertFileToByteArray("src/test/resources/testFileSemiColons.txt");
    private final byte[] FILE_WITH_NEW_LINE = convertFileToByteArray("src/test/resources/testFileNewLine.txt");
    private final byte[] EMPTY_FILE = convertFileToByteArray("src/test/resources/emptyFile.txt");
    
	private Map<String,Object> properties = new HashMap<String, Object>();
	private final List<String> expectedNodesFromFile = Arrays.asList("node1","node2","node3","node4","node5","MeContext=node6","NetworkElement=node7");
	
	@Test
	public void testSshkeyCreate() {
		assertValidSshkey("sshkey create --algorithm-type-size RSA_1024 --nodelist sgsn123", NscsCommandType.CREATE_SSH_KEY, Arrays.asList("sgsn123"),"RSA_1024");
	}
	
	@Test
	public void testSshkeyCreate_reverseorder() {
		assertValidSshkey("sshkey create --nodelist sgsn123 --algorithm-type-size RSA_1024", NscsCommandType.CREATE_SSH_KEY, Arrays.asList("sgsn123"),"RSA_1024");
	}
	
	@Test
	public void testSshkeyCreate_reverseorder_shortcut() {
		assertValidSshkey("sshkey create -n sgsn123 -t RSA_1024", NscsCommandType.CREATE_SSH_KEY, Arrays.asList("sgsn123"),"RSA_1024");
	}
	
	@Test
	public void testSshkeyCreate__2nodes() {
		assertValidSshkey("sshkey create --algorithm-type-size RSA_4096 --nodelist sgsn123,sgsn987", NscsCommandType.CREATE_SSH_KEY, Arrays.asList("sgsn123", "sgsn987"),"RSA_4096");
	}

	@Test
	public void testSshkeyCreate__2nodes_shortcut() {
		assertValidSshkey("sshkey create -t RSA_8192 --nodelist sgsn123,sgsn987", NscsCommandType.CREATE_SSH_KEY, Arrays.asList("sgsn123", "sgsn987"),"RSA_8192");
	}
	

	@Test
	public void testSshkeyCreate__nodefile__newline() {
//		properties.put("file:", FILE_WITH_TABS);
//		assertValidSshkey("sshkey create -t RSA_8192 --nodefile file:testFileNewLine.txt", NscsCommandType.CREATE_KEY_GEN, "testFileNewLine.txt", FILE_WITH_TABS, "RSA_8192");
		
		properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("sshkey create -t RSA_8192 --nodefile file:testFileNewLine.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
	}
	
	@Test
	public void testSshkeyCreate__nodefile__tab() {
		properties.put("file:", FILE_WITH_TABS);
        final NscsCliCommand command = new NscsCliCommand("sshkey create -t RSA_8192 --nodefile file:testFileTabs.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
	}
	
	@Test
	public void testSshkeyCreate__nodefile__spaces() {
		properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("sshkey create -t RSA_8192 --nodefile file:testFileSpaces.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
	}
	
	@Test
	public void testSshkeyCreate__nodefile__semicolon() {
		properties.put("file:", FILE_WITH_SEMI_COLONS);
        final NscsCliCommand command = new NscsCliCommand("sshkey create -t RSA_8192 --nodefile file:testFileSemiColons.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
	}
	
	@Test
	public void testSshkeyCreate__nodefile__emptyfile() {
		properties.put("file:", EMPTY_FILE);
        final NscsCliCommand command = new NscsCliCommand("sshkey create -t RSA_8192 --nodefile file:emptyFile.txt", properties);
        parseCommandAndAssertFail(command);
	}
	
	@Ignore
	@Test
	public void testSshkeyCreate__2nodes_continue() {
		assertValidSshkey("sshkey create -t RSA_16834 --nodelist sgsn123,sgsn987 --continue", NscsCommandType.CREATE_SSH_KEY, Arrays.asList("sgsn123", "sgsn987"),"RSA_16834");
	}
	
	@Ignore
	@Test
	public void testSshkeyCreate__continue() {
		assertValidSshkey("sshkey create --algorithm-type-size RSA_1024 --nodelist sgsn123 --continue", NscsCommandType.CREATE_SSH_KEY, Arrays.asList("sgsn123"),"RSA_1024");
	}
	
	
	@Test
	public void testSshkeyUpdate() {
		assertValidSshkey("sshkey update --algorithm-type-size RSA_1024 --nodelist sgsn123", NscsCommandType.UPDATE_SSH_KEY, Arrays.asList("sgsn123"),"RSA_1024");
	}
		
	
	@Test
	public void testSshkeyUpdate_reverseorder() {
		assertValidSshkey("sshkey update --nodelist sgsn123 --algorithm-type-size RSA_1024", NscsCommandType.UPDATE_SSH_KEY, Arrays.asList("sgsn123"),"RSA_1024");
	}
	
	
	@Test
	public void testSshkeyUpdate__noalgorithm() {
		assertValidSshkey("sshkey update --nodelist sgsn123", NscsCommandType.UPDATE_SSH_KEY, Arrays.asList("sgsn123"));
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
