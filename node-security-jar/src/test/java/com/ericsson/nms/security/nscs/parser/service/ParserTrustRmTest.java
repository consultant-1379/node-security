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


import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;


/**
 * 
 * Test class to validate Trust Distribute related commands
 * 
 * @author enmadmin
 *
 */

public class ParserTrustRmTest extends AbstractParserTest {

	private final Logger log = LoggerFactory.getLogger(ParserTrustRmTest.class);

	//private static final byte[] EMPTY_BYTE_ARRAY = "".getBytes(Charset.forName("UTF-8"));

	private final byte[] FILE_WITH_SPACES = convertFileToByteArray("src/test/resources/testFileSpaces.txt");
	private final byte[] FILE_WITH_TABS = convertFileToByteArray("src/test/resources/testFileTabs.txt");
	private final byte[] FILE_WITH_SEMI_COLONS = convertFileToByteArray("src/test/resources/testFileSemiColons.txt");
	private final byte[] FILE_WITH_NEW_LINE = convertFileToByteArray("src/test/resources/testFileNewLine.txt");
	private final byte[] EMPTY_FILE = convertFileToByteArray("src/test/resources/emptyFile.txt");
	private final byte[] FILE_WITH_ALL = convertFileToByteArray("src/test/resources/testFileAll.txt");

	//private final List<String> expectedNodesFromFile = Arrays.asList("node1","node2","node3","node4","node5","MeContext=node6","NetworkElement=node7");
	private final String nodeList = "node1, node2, node3, node4, node5, MeContext=node6, NetworkElement=node7";
	private final String all = "all";
	private final String caName = "ca1";
	private final String serialNum = "12345";
	private final String certType = "OAM";
	private final String issuerDistinguishName = "issuerDn1";

	private final Map<String,Object> properties = new HashMap<>();


	/**
	 * Positive Test with 
	 * extended remove: remove
	 * shorten caName: -ca
	 * shorten serialNumber: -sn
	 * shorten certType: -ct
	 * extended Nodelist: --nodelist
	 */
	@Test
	public void testTrustRmCommand__extendedRemove__CAname__SerialNumber__CertType__extendedNodelist() {
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType + " -ca "+ caName +" -sn "+ serialNum +" --nodelist " + nodeList);
		parse(command);
	}

	/**
	 * Positive Test with 
	 * extended remove: remove
	 * shorten issuer Distinguish Name: -isdn
	 * shorten serialNumber: -sn
	 * shorten certType: -ct
	 * extended Nodelist: --nodelist
	 */
	@Test
	public void testTrustRmCommand__extendedRemove__IssuerDn__SerialNumber__CertType__extendedNodelist() {
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -isdn "+ issuerDistinguishName +" -sn "+ serialNum +" --nodelist " + nodeList);
		parse(command);
	}
	
	/**
	 * Positive Test with 
	 * extended remove: remove
	 * extended issuer Distinguish Name: -isdn
	 * shorten serialNumber: -sn
	 * shorten certType: -ct
	 * extended Nodelist: --nodelist
	 */
	@Test
	public void testTrustRmCommand__extendedRemove__extendedIssuerDn__SerialNumber__CertType__extendedNodelist() {
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" --issuer-dn "+ issuerDistinguishName +" -sn "+ serialNum +" --nodelist " + nodeList);
		parse(command);
	}

	/**
	 * Positive Test with 
	 * extended remove: remove
	 * shorten caName: -ca
	 * shorten serialNumber: -sn
	 * shorten certType: -ct
	 */
	@Test
	public void testTrustRmCommand__extendedRemove__CAname__SerialNumber__CertType__allNodelist() {
		final NscsCliCommand command = new NscsCliCommand("trust remove  -ct "+ certType +" -ca "+ caName +" -sn "+ serialNum +" --nodelist " + all);
		parse(command);
	}

	/**
	 * Positive Test with 
	 * extended remove: remove
	 * shorten issuer Distinguish Name: -isdn
	 * shorten serialNumber: -sn
	 * shorten certType: -ct
	 */
	@Test
	public void testTrustRmCommand__extendedRemove__IssuerDn__SerialNumber__CertType__allNodelist() {
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -isdn "+ issuerDistinguishName +" -sn "+ serialNum +" --nodelist " + all);
		parse(command);
	}

	/**
	 * Positive Test with 
	 * extended remove: remove
	 * shorten caName: -ca
	 * shorten serialNumber: -sn
	 * missing certType: -ct
	 * extended Nodelist: --nodelist
	 */
	@Test (expected = CommandSyntaxException.class)
	public void testTrustRmCommand__extendedRemove__CAname__SerialNumber__MissingCertType__extendedNodelist() {
		final NscsCliCommand command = new NscsCliCommand("trust remove -ca "+ caName +" -sn "+ serialNum +" --nodelist " + nodeList);
		parse(command);
	}
	
	/**
	 * Negative Test with
	 * invalid combination: -ca and -isdn
	 */
	@Test(expected = CommandSyntaxException.class)
	public void testTrustRmCommand__extendedRemove__CAname__IssuerDn__SerialNumber__CertType__extendedNodelist_negative() {
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -ca "+ caName +" -isdn "+ issuerDistinguishName +" -sn "+ serialNum +" --nodelist " + nodeList);
		parse(command);
	}

	/**
	 * Negative Test with
	 * invalid combination: -ca and -isdn
	 */
	@Test(expected = CommandSyntaxException.class)
	public void testTrustRmCommand__extendedRemove__IssuerDn__CAname__SerialNumber__CertType__extendedNodelist_negative() {
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -isdn "+ issuerDistinguishName +" -ca "+ caName +" -sn "+ serialNum +" --nodelist " + nodeList);
		parse(command);
	}

	/**
	 * Negative Test with
	 * Reversed order of parameters: -ca and -sn
	 */
	@Test(expected = CommandSyntaxException.class)
	public void testTrustRmCommand__extendedRemove__CAname__SerialNumber__CertType__extendedNodelist__reverseOrder_negative() {
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -sn "+ serialNum +" -ca "+ caName +" --nodelist " + nodeList);
		parse(command);
	}

	/**
	 * Negative Test with
	 * Reversed order of parameters: -isdn and -sn
	 */
	@Test(expected = CommandSyntaxException.class)
	public void testTrustRmCommand__extendedRemove__IssuerDn__SerialNumber__CertType__extendedNodelist__reverseOrder_negative() {
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -sn "+ serialNum +" -isdn "+ issuerDistinguishName +" --nodelist " + nodeList);
		parse(command);
	}
	
	/**
	 * Negative Test with 
	 * missing parameter: --nodelist
	 */
	@Test(expected = CommandSyntaxException.class)
	public void testTrustRmCommand__extendedRemove__IssuerDn__SerialNumber__CertType__missingNodelist() {
		final NscsCliCommand command = new NscsCliCommand("trust remove  -ct "+ certType +" -isdn "+ issuerDistinguishName +" -sn "+ serialNum);
		parse(command);
	}
	

	/**
	 * Positive Test with 
	 * Nodefile property: --nodefile file: testFileSpaces.txt
	 */
	@Test
	public void testTrustRemoveCommandWithNodelist__nodefile_with_spaces() {
		properties.put("file:", FILE_WITH_SPACES);
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -isdn "+ issuerDistinguishName +" -sn "+ serialNum +" --nodefile file:testFileSpaces.txt", properties);
		parse(command);
	}

	/**
	 * Positive Test with 
	 * Nodefile property: --nodefile file: testFileSemiColons.txt
	 */
	@Test
	public void testTrustRemoveCommandWithNodelist__nodefile_semi_colons() {
		properties.put("file:", FILE_WITH_SEMI_COLONS);
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -isdn "+ issuerDistinguishName +" -sn "+ serialNum +" --nodefile file:testFileSemiColons.txt", properties);
		parse(command);
	}

	/**
	 * Positive Test with 
	 * Nodefile property: --nodefile file: testFileTabs.txt
	 */
	@Test
	public void testTrustRemoveCommandWithNodelist__nodefile_with_tabs() {
		properties.put("file:", FILE_WITH_TABS);
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -isdn "+ issuerDistinguishName +" -sn "+ serialNum +" --nodefile file:testFileTabs.txt", properties);
		parse(command);
	}

	/**
	 * Positive Test with 
	 * Nodefile property: --nodefile file: testFileNewLine.txt
	 */
	@Test
	public void testTrustRemoveCommandWithNodelist__nodefile_new_line() {
		properties.put("file:", FILE_WITH_NEW_LINE);
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -isdn "+ issuerDistinguishName +" -sn "+ serialNum +" --nodefile file:testFileNewLine.txt", properties);
		parse(command);
	}

	/**
	 * Negative Test with 
	 * Nodefile property: --nodefile file:emptyFile.txt
	 */
	@Test(expected = CommandSyntaxException.class)
	public void testTrustRemoveCommandWithNodelist__nodefile_with_empty_file() {
		properties.put("file:", EMPTY_FILE);
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -isdn "+ issuerDistinguishName +" -sn "+ serialNum +" --nodefile file:emptyFile.txt", properties);
		parse(command);
	}


	/**
	 * Negative Test with 
	 * Nodefile property: missing file:
	 */
	@Test(expected = CommandSyntaxException.class)
	public void testTrustRemoveeCommandWithNodelist__missing_file_property() {
		properties.put("file:", FILE_WITH_SPACES);
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -isdn "+ issuerDistinguishName +" -sn "+ serialNum +" --nodefile testFileSpaces.txt", properties);
		parse(command);
	}
	
	/**
	 * Positive Test with 
	 * Nodefile property: --nodefile file: testFileNewLine.txt
	 */
	@Test
	public void testTrustRemoveCommandWithNodelist__nodefile_all() {
		properties.put("file:", FILE_WITH_ALL);
		final NscsCliCommand command = new NscsCliCommand("trust remove -ct "+ certType +" -isdn "+ issuerDistinguishName +" -sn "+ serialNum +" --nodefile file:testFileAll.txt", properties);
		parse(command);
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
			log.error("File passed in was empty");
			e.printStackTrace();
		}
		return fileToBeParsed;
	}

}
