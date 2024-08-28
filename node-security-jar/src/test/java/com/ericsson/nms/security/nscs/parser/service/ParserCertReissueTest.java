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


import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * Test class to validate Certificate Reissue related commands
 * 
 * @author enmadmin
 *
 */

public class ParserCertReissueTest extends AbstractParserTest {

    private final Logger log = LoggerFactory.getLogger(ParserCertReissueTest.class);
    
    private final byte[] FILE_WITH_SPACES = convertFileToByteArray("src/test/resources/testFileSpaces.txt");
    private final byte[] FILE_WITH_TABS = convertFileToByteArray("src/test/resources/testFileTabs.txt");
    private final byte[] FILE_WITH_SEMI_COLONS = convertFileToByteArray("src/test/resources/testFileSemiColons.txt");
    private final byte[] FILE_WITH_NEW_LINE = convertFileToByteArray("src/test/resources/testFileNewLine.txt");
    private final byte[] EMPTY_FILE = convertFileToByteArray("src/test/resources/emptyFile.txt");
    
	private final String nodeList = "node1; node2, node3; node4; node5, MeContext=node6; NetworkElement=node7";
	private final String caList = "ca1";
	private final String caList2 = "ca1,ca2;ca3";
	
	private final String serialNumber = "1234567890";
	private final String reason = "unspecified";
	
    private final Map<String,Object> properties = new HashMap<>();
    
    
    /**
     * Positive Test with 
     * extended reissue: certificate reissue
     * extended CertType: --certtype
     * extended Nodelist: --nodelist
     * CertType value: IPSEC
     */
    @Test
    public void testCertReissueCommandWithNodelist__extendedDistribute__extendedCertType__extendedNodelist__IPSEC() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --nodelist " + nodeList);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * extended reissue: certificate reissue
     * extended CertType: --certtype
     * extended Nodelist: --nodelist
     * CertType value: OAM
     */
    @Test
    public void testCertReissueCommandWithNodelist__extendedDistribute__extendedCertType__extendedNodelist__OAM() {
    	final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype OAM --nodelist " + nodeList);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * shorten reissue: cert reissue
     * extended CertType: --certtype
     * extended Nodelist: --nodelist
     * CertType value: IPSEC
     */
    @Test
    public void testCertReissueCommandWithNodelist__shortenDistribute__extendedCertType__extendedNodelist__IPSEC() {
        final NscsCliCommand command = new NscsCliCommand("cert reissue --certtype IPSEC --nodelist " + nodeList);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * extended reissue: certificate reissue
     * shorten CertType: -ct
     * extended Nodelist: --nodelist
     * CertType value: IPSEC
     */
    @Test
    public void testCertReissueCommandWithNodelist__extendedDistribute__shortenCertType__extendedNodelist__IPSEC() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct IPSEC --nodelist " + nodeList);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * extended reissue: certificate reissue
     * extended CertType: --certtype
     * shorten Nodelist: -n
     * CertType value: IPSEC
     */
    @Test
    public void testCertReissueCommandWithNodelist__extendedDistribute__extendedCertType__shortenNodelist__IPSEC() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC -n " + nodeList);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * extended reissue: certificate reissue
     * shorten CertType: -ct
     * shorten Nodelist: -n
     * CertType value: IPSEC
     */
    @Test
    public void testCertReissueCommandWithNodelist__extendedDistribute__shortenCertType__shortenNodelist__IPSEC() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct IPSEC -n " + nodeList);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * extended reissue: certificate reissue
     * extended CertType: --certtype
     * extended Nodelist: --nodelist
     * extended Reason: --reason
     */
    @Test
    public void testCertReissueCommandWithNodelist__extendedDistribute__extendedCertType__extendedNodelist__extendedReason() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --nodelist " + nodeList + " --reason " + reason);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * extended reissue: certificate reissue
     * extended CertType: --certtype
     * extended Nodelist: --nodelist
     * shorten Reason: -r
     */
    @Test
    public void testCertReissueCommandWithNodelist__extendedDistribute__extendedCertType__extendedNodelist__shortenReason() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --nodelist " + nodeList + " -r " + reason);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * Nodefile property: --nodefile file: testFileSpaces.txt
     */
    @Test
    public void testCertReissueCommandWithNodelist__nodefile_with_spaces() {
    	properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --nodefile file:testFileSpaces.txt", properties);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * Nodefile property: --nodefile file: testFileSemiColons.txt
     */
    @Test
    public void testCertReissueCommandWithNodelist__nodefile_semi_colons() {
    	properties.put("file:", FILE_WITH_SEMI_COLONS);
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --nodefile file:testFileSemiColons.txt", properties);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * Nodefile property: --nodefile file: testFileTabs.txt
     */
    @Test
    public void testCertReissueCommandWithNodelist__nodefile_with_tabs() {
    	properties.put("file:", FILE_WITH_TABS);
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --nodefile file:testFileTabs.txt", properties);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * Nodefile property: --nodefile file: testFileNewLine.txt
     */
    @Test
    public void testCertReissueCommandWithNodelist__nodefile_new_line() {
    	properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --nodefile file:testFileNewLine.txt", properties);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * Nodefile property: --nodefile file: testFileSpaces.txt
     * Reason property: --reason
     */
    @Test
    public void testCertReissueCommandWithNodelist__nodefile_with_spaces__withReason() {
    	properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --nodefile file:testFileSpaces.txt --reason " + reason , properties);
        parse(command);
    }

    /**
     * Negative Test with
     * invalid CertType: --certype
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist____InvalidCertype() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certype IPSEC --nodelist " + nodeList);
        parse(command);
    }
    
    /**
   * Negative Test with 
   * invalid CertTypeValue: OEM . OEM is an invalid value but test does not fail. Values checks are made by command handler validators
   * and not by g4 parser validator.
   */
  //@Test(expected = CommandSyntaxException.class)
    @Test
    public void testCertReissueCommandWithNodelist____InvalidCertypeValue() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype OEM --nodelist " + nodeList);
        parse(command);
    }
    
    /**
     * Negative Test with
     * invalid shorten CertType: --ct
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__InvalidShortenParam() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --ct IPSEC --nodelist " + nodeList);
        parse(command);
    }

    /**
	* Negative Test with 
	* missing param: --certtype
	*/
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__MissingParam() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --nodelist " + nodeList);
        parse(command);
    }
    
    /**
	* Negative Test with 
	* missing param: --nodelist
	*/
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__MissingNodelistOrFile() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC");
        parse(command);
    }
    
    /**
	* Negative Test with 
	* missing param: --nodelist
	*/
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__MissingNodelistOrFile__withReason() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --reason " + reason);
        parse(command);
    }
    
    /**
	* Negative Test with 
	* missing param: --nodelist
	* extra unwanted param: --serialnumber
	*/
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__MissingNodelistOrFile__unwantedSerialNumber() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --serialnumber " + serialNumber);
        parse(command);
    }
    
    /**
	* Negative Test with 
	* missing param: --nodelist
	* extra unwanted param: --serialnumber
	* reason param: --reason
	*/
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__MissingNodelistOrFile__unwantedSerialNumber__WithReason() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --serialnumber " + serialNumber + " --reason " + reason);
        parse(command);
    }
    
    /**
	* Negative Test with 
	* missing param: --nodelist
	* extra unwanted param: --serialnumber
	* Note: This is a Command Syntax Exception. Not covered by g4 parser: handled in command handler.
	*/
    @Test
    //@Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__MissingNodelistOrFile__unwantedSerialNumber__withNodeList() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC -n "+ nodeList +" --serialnumber " + serialNumber);
        parse(command);
    }
    
    /**
	* Negative Test with 
	* missing param: --nodelist
	* extra unwanted param: --serialnumber
	* reason param: --reason
	* Note: This is a Command Syntax Exception. Not covered by g4 parser: handled in command handler.
	*/
    @Test
    //@Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__MissingNodelistOrFile__unwantedSerialNumber__withNodeList__WithReason() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC -n "+ nodeList +" --serialnumber " + serialNumber + " --reason " + reason);
        parse(command);
    }
    
    /**
	* Negative Test with
	* extra unwanted params SerialNumber and Nodelist reversed order: --serialnumber, --nodelist 
	* reason param: --reason
	*/
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__WithUnwantedCombination_Nodelist_SerialNumber__ReverseOrder() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --serialnumber " + serialNumber +" --nodelist " + nodeList);
        parse(command);
    }

    /**
     * Negative Test with 
     * reverse order
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__ReverseOrder() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --nodelist "+ nodeList +" --certtype IPSEC" );
        parse(command);
    }
    
    /**
     * Negative Test with 
     * reverse order for reason
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__ReasonReverseOrder() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --reason " + reason + " --nodelist "+ nodeList );
        parse(command);
    }
    
    /**
     * Negative Test with 
     * Nodefile property: --nodefile file:emptyFile.txt
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__nodefile_with_empty_file() {
    	properties.put("file:", EMPTY_FILE);
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --nodefile file:emptyFile.txt", properties);
        parse(command);
    }
    
    /**
     * Negative Test with 
     * Nodefile property: missing file:
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__missing_file_property() {
    	properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --nodefile testFileSpaces.txt", properties);
        parse(command);
    }
    
    
    /**
	* Negative Test with 
	* missing param: --nodelist (unwanted case but supported by g4)
	* Note: This is a Command Syntax Exception because g4 does not support duplicate reason.
	*/
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithNodelist__MissingNodelistOrFile__withDoubleReason() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue --certtype IPSEC --reason " + reason + " -r " + reason);
        parse(command);
    }
    
    
    
    
    /**
     * Negative Test with
     * ca property: -ca 
     * missing param: -ct
     * Nodelist property: --nodelist
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithCA__missingCerttype_withNodeList() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ca " + caList + " --nodelist " + nodeList);
        parse(command);
    }
    
    /**
     * Positive Test with
     * certificate type property: -ct
     * ca property: -ca 
     * Nodelist property: --nodelist
     */
    @Test
    public void testCertReissueCommandWithCA__withNodeList() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList + " --nodelist " + nodeList);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * certificate type property: -ct
     * ca property: -ca 
     * Nodefile property: --nodefile file: testFileSpaces.txt
     */
    @Test
    public void testCertReissueCommandWithCA__withNodeFile() {
    	properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList + " --nodefile file:testFileSpaces.txt", properties);
        parse(command);
    }
    
    /**
     * Positive Test with
     * certificate type property: -ct
     * ca property: -ca 
     * Nodelist property: --nodelist
     * Reason property: --reason
     */
    @Test
    public void testCertReissueCommandWithCA__withNodeList__withReason() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList + " --nodelist " + nodeList + " --reason " + reason);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * certificate type property: -ct
     * ca property: -ca 
     * Nodefile property: --nodefile file: testFileSpaces.txt
     * Reason property: --reason
     */
    @Test
    public void testCertReissueCommandWithCA__withNodeFile__withReason() {
    	properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList + " --nodefile file:testFileSpaces.txt --reason " + reason, properties);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * certificate type property: -ct
     * ca property: -ca 
     */
    @Test
    public void testCertReissueCommandWithCA__missingNodeList() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * certificate type property: -ct
     * ca property: -ca 
     */
    @Test
    public void testCertReissueCommandWithCA__missingNodeList__multipleCAs() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList2);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * certificate type property: -ct
     * ca property: -ca 
     * Reason property: --reason
     */
    @Test
    public void testCertReissueCommandWithCA__missingNodeList__withReason() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList + " --reason " + reason);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * certificate type property: -ct
     * ca property: -ca 
     * Reason property: --reason
     */
    @Test
    public void testCertReissueCommandWithCA__missingNodeList__multipleCAs_withReason() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList2 + " --reason " + reason);
        parse(command);
    }
    
    /**
     * Negative Test with
     * invalid parameter for CA: --ca
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWrongCA() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM --ca " + caList);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * certificate type property: -ct
     * ca property: -ca 
     * extended SerialNumber: --serialnumber
     */
    @Test
    public void testCertReissueCommandWithCA__withSerialNumber() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList + " --serialnumber " + serialNumber);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * certificate type property: -ct
     * ca property: -ca
     * shorten SerialNumber: -sn
     */
    @Test
    public void testCertReissueCommandWithCA__withShortenSerialNumber() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList + " -sn " + serialNumber);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * certificate type property: -ct
     * ca property: -ca 
     * extended SerialNumber: --serialnumber
     * Reason property: --reason
     */
    @Test
    public void testCertReissueCommandWithCA__withSerialNumber__withReason() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList + " --serialnumber " + serialNumber + " --reason " + reason);
        parse(command);
    }
    
    /**
     * Positive Test with 
     * certificate type property: -ct
     * ca property: -ca
     * shorten SerialNumber: -sn
     * Reason property: --reason
     */
    @Test
    public void testCertReissueCommandWithCA__withShortenSerialNumber__withReason() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList + " -sn " + serialNumber + " --reason " + reason);
        parse(command);
    }

    /**
     * Negative Test with
     * invalid reverse order certtype and CA: -ct , -ca
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithCA__WithCertType() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ca " + caList+ "  -ct OAM");
        parse(command);
    }  
    
    /**
     * Negative Test with
     * invalid extended SerialNumber: -serialnumber
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithCA__withInvalidSerialNumber() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList + " -serialnumber " + serialNumber);
        parse(command);
    }
    
    /**
     * Negative Test with reverse order
     * property SerialNumber: -sn
     * property CA: -ca
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithCA__withSerialNumber__reverseOrder() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -sn " + serialNumber + " -ca " + caList);
        parse(command);
    }
    
    /**
     * Negative Test with double parameter
     * property reason: --reason
     * property CA: -ca
     */
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithCA__withdoubleReason() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca " + caList + " --reason " + reason
        		+ " -r " + reason);
        parse(command);
    }
    
    /**
	* Negative Test with
	* extra unwanted params SerialNumber and Nodelist: --serialnumber, --nodelist 
	* reason param: --reason
	*/
    @Test(expected = CommandSyntaxException.class)
    public void testCertReissueCommandWithCA__WithUnwantedCombination_Nodelist_SerialNumber() {
        final NscsCliCommand command = new NscsCliCommand("certificate reissue -ct OAM -ca "+ caList +" --nodelist " + nodeList + " --serialnumber " + serialNumber);
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
