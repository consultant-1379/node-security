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
 * Test class to validate Trust Distribute related commands
 *
 * @author enmadmin
 *
 */

public class ParserTrustDistrTest extends AbstractParserTest {

    private final Logger log = LoggerFactory.getLogger(ParserTrustDistrTest.class);

    // private static final byte[] EMPTY_BYTE_ARRAY = "".getBytes(Charset.forName("UTF-8"));

    private final byte[] FILE_WITH_SPACES = convertFileToByteArray("src/test/resources/testFileSpaces.txt");
    private final byte[] FILE_WITH_TABS = convertFileToByteArray("src/test/resources/testFileTabs.txt");
    private final byte[] FILE_WITH_SEMI_COLONS = convertFileToByteArray("src/test/resources/testFileSemiColons.txt");
    private final byte[] FILE_WITH_NEW_LINE = convertFileToByteArray("src/test/resources/testFileNewLine.txt");
    private final byte[] EMPTY_FILE = convertFileToByteArray("src/test/resources/emptyFile.txt");

    // private final List<String> expectedNodesFromFile = Arrays.asList("node1","node2","node3","node4","node5","MeContext=node6","NetworkElement=node7");
    private final String nodeList = "node1, node2, node3, node4, node5, MeContext=node6, NetworkElement=node7";
    private final String caList = "ca1";
    private final String caList2 = "ca1,ca2,ca3";

    private final Map<String, Object> properties = new HashMap<>();

    /**
     * Positive Test with extended Distribute: distribute extended CertType: --certtype extended Nodelist: --nodelist CertType value: IPSEC
     */
    @Test
    public void testTrustDistributeCommandWithNodelist__extendedDistribute__extendedCertType__extendedNodelist__IPSEC() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC --nodelist " + nodeList);
        parse(command);
    }

    /**
     * Positive Test with extended Distribute: distribute extended CertType: --certtype extended Nodelist: --nodelist CertType value: OAM
     */
    @Test
    public void testTrustDistributeCommandWithNodelist__extendedDistribute__extendedCertType__extendedNodelist__OAM() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype OAM --nodelist " + nodeList);
        parse(command);
    }

    /**
     * Positive Test with shorten Distribute: distr extended CertType: --certtype extended Nodelist: --nodelist CertType value: IPSEC
     */
    @Test
    public void testTrustDistributeCommandWithNodelist__shortenDistribute__extendedCertType__extendedNodelist__IPSEC() {
        final NscsCliCommand command = new NscsCliCommand("trust distr --certtype IPSEC --nodelist " + nodeList);
        parse(command);
    }

    /**
     * Positive Test with extended Distribute: distribute shorten CertType: -ct extended Nodelist: --nodelist CertType value: IPSEC
     */
    @Test
    public void testTrustDistributeCommandWithNodelist__extendedDistribute__shortenCertType__extendedNodelist__IPSEC() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute -ct IPSEC --nodelist " + nodeList);
        parse(command);
    }

    /**
     * Positive Test with extended Distribute: distribute extended CertType: --certtype shorten Nodelist: -n CertType value: IPSEC
     */
    @Test
    public void testTrustDistributeCommandWithNodelist__extendedDistribute__extendedCertType__shortenNodelist__IPSEC() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC -n " + nodeList);
        parse(command);
    }

    /**
     * Positive Test with extended Distribute: distribute shorten CertType: -ct shorten Nodelist: -n CertType value: IPSEC
     */
    @Test
    public void testTrustDistributeCommandWithNodelist__extendedDistribute__shortenCertType__shortenNodelist__IPSEC() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute -ct IPSEC -n " + nodeList);
        parse(command);
    }

    /**
     * Positive Test with Nodefile property: --nodefile file: testFileSpaces.txt
     */
    @Test
    public void testTrustDistributeCommandWithNodelist__nodefile_with_spaces() {
        properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC --nodefile file:testFileSpaces.txt", properties);
        parse(command);
    }

    /**
     * Positive Test with Nodefile property: --nodefile file: testFileSemiColons.txt
     */
    @Test
    public void testTrustDistributeCommandWithNodelist__nodefile_semi_colons() {
        properties.put("file:", FILE_WITH_SEMI_COLONS);
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC --nodefile file:testFileSemiColons.txt", properties);
        parse(command);
    }

    /**
     * Positive Test with Nodefile property: --nodefile file: testFileTabs.txt
     */
    @Test
    public void testTrustDistributeCommandWithNodelist__nodefile_with_tabs() {
        properties.put("file:", FILE_WITH_TABS);
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC --nodefile file:testFileTabs.txt", properties);
        parse(command);
    }

    /**
     * Positive Test with Nodefile property: --nodefile file: testFileNewLine.txt
     */
    @Test
    public void testTrustDistributeCommandWithNodelist__nodefile_new_line() {
        properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC --nodefile file:testFileNewLine.txt", properties);
        parse(command);
    }

    /**
     * Negative Test with invalid CertType: --certype
     */
    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommandWithNodelist____InvalidCertype() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certype IPSEC --nodelist " + nodeList);
        parse(command);
    }

    /**
     * Negative Test with invalid CertTypeValue: OEM . OEM is an invalid value but test does not fail. Values checks are made by command validators and not by parser validator.
     */
    //@Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommandWithNodelist____InvalidCertypeValue() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype OEM --nodelist " + nodeList);
        parse(command);
    }

    /**
     * Negative Test with invalid shorten CertType: --ct
     */
    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommandWithNodelist__InvalidShortenParam() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --ct IPSEC --nodelist " + nodeList);
        parse(command);
    }

    /**
     * Negative Test with missing param: --certtype
     */
    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommandWithNodelist__MissingParam() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --nodelist " + nodeList);
        parse(command);
    }

    /**
     * Negative Test with missing param: --nodelist
     */
    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommandWithNodelist__MissingNodelistOrFile() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC");
        parse(command);
    }

    /**
     * Negative Test with reverse order
     */
    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommandWithNodelist__ReverseOrder() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --nodelist " + nodeList + " --certtype IPSEC");
        parse(command);
    }

    /**
     * Negative Test with Nodefile property: --nodefile file:emptyFile.txt
     */
    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommandWithNodelist__nodefile_with_empty_file() {
        properties.put("file:", EMPTY_FILE);
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC --nodefile file:emptyFile.txt", properties);
        parse(command);
    }

    /**
     * Negative Test with Nodefile property: missing file:
     */
    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommandWithNodelist__missing_file_property() {
        properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC --nodefile testFileSpaces.txt", properties);
        parse(command);
    }

    @Test
    public void testTrustDistributeCommandWithCA__withNodeList() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC -ca " + caList + " --nodelist " + nodeList);
        parse(command);
    }

    @Test
    public void testTrustDistributeCommandWithCA__withNodeFile() {
        properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC -ca " + caList + " --nodefile file:testFileSpaces.txt", properties);
        parse(command);
    }

    @Test
    public void testTrustDistributeCommandWithCA__missingNodeList() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC -ca " + caList);
        parse(command);
    }

    // @Test
    // public void testTrustDistributeCommandWithCA__missingNodeList__multipleCAs() {
    // final NscsCliCommand command = new NscsCliCommand("trust distribute  --certtype IPSEC -ca " + caList2);
    // parse(command);
    // }

    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommandWithCA__WithCertType() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute -ca " + caList);
        parse(command);
    }

    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommandWrongCA() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --certtype IPSEC --ca " + caList);
        parse(command);
    }

    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommandDuplicatedNodeList() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute -ct OAM --nodelist " + nodeList + " --nodelist " + nodeList);
        parse(command);
    }

    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommand_reverse_order_ct_nodelist() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute --nodelist " + nodeList + " -ct OAM");
        parse(command);
    }

    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommand_reverse_order_ca_nodelist() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute -ct OAM --nodelist " + nodeList + " -ca " + caList);
        parse(command);
    }

    @Test(expected = CommandSyntaxException.class)
    public void testTrustDistributeCommand_reverse_order_ca_ct() {
        final NscsCliCommand command = new NscsCliCommand("trust distribute  -ca " + caList + " -ct OAM --nodelist " + nodeList);
        parse(command);
    }

    // It passes syntax parser but is verified inside CommandHandler
    @Test
    public void testTrustDistributeCommandNodeListNodeFile() {
        properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("trust distribute -ct OAM --nodelist " + nodeList + " --nodefile file:testFileSpaces.txt", properties);
        parse(command);
    }

    private byte[] convertFileToByteArray(final String fileLocation) {
        final File file = new File(fileLocation);
        FileInputStream fileInputStream = null;

        final byte[] fileToBeParsed = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileToBeParsed);
            fileInputStream.close();
        } catch (Exception e) {
            log.error("File passed in was empty");
            e.printStackTrace();
        }
        return fileToBeParsed;
    }

}
