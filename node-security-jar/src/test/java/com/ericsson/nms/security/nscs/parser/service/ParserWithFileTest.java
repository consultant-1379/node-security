package com.ericsson.nms.security.nscs.parser.service;

import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by emaynes on 22/05/2014.
 */
@Ignore("TORF-38537 - not supported in 14B")
public class ParserWithFileTest extends AbstractParserTest {

    private final Logger log = LoggerFactory.getLogger(ParserWithFileTest.class);;

    private static final byte[] BYTE_ARRAY_SEMI_COLONS = "node1;node2;MeContext=node3;NetworkElement=node4".getBytes(Charset.forName("UTF-8"));
    private static final byte[] BYTE_ARRAY_SPACES = "node1 node2 MeContext=node3 NetworkElement=node4".getBytes(Charset.forName("UTF-8"));
    private static final byte[] BYTE_ARRAY_TABS = "node1\tnode2\tMeContext=node3\tNetworkElement=node4".getBytes(Charset.forName("UTF-8"));
    private static final byte[] BYTE_ARRAY_CARRIAGE_RETURN = "node1\rnode2\rMeContext=node3\rNetworkElement=node4".getBytes(Charset.forName("UTF-8"));
    private static final byte[] BYTE_ARRAY_NEW_LINE = "node1\nnode2\nMeContext=node3\nNetworkElement=node4".getBytes(Charset.forName("UTF-8"));
    private static final byte[] EMPTY_BYTE_ARRAY = "".getBytes(Charset.forName("UTF-8"));

    private final byte[] FILE_WITH_SPACES = convertFileToByteArray("src/test/resources/testFileSpaces.txt");
    private final byte[] FILE_WITH_TABS = convertFileToByteArray("src/test/resources/testFileTabs.txt");
    private final byte[] FILE_WITH_SEMI_COLONS = convertFileToByteArray("src/test/resources/testFileSemiColons.txt");
    private final byte[] FILE_WITH_NEW_LINE = convertFileToByteArray("src/test/resources/testFileNewLine.txt");
    private final byte[] EMPTY_FILE = convertFileToByteArray("src/test/resources/emptyFile.txt");

    private final Map<String,Object> properties = new HashMap<>();
    private final List<String> expectedNodes = Arrays.asList("node1","node2","MeContext=node3","NetworkElement=node4");
    private final List<String> expectedNodesFromFile = Arrays.asList("node1","node2","node3","node4","node5","MeContext=node6","NetworkElement=node7");

    @Test
    public void testFileSupplied() {
        parseCommandAndAssertFail("sl set 1 file=abc.txt");
    }

    @Test
    public void testCommandWithFileParserNotNull() {
        properties.put("file:", BYTE_ARRAY_SEMI_COLONS);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 -nf file:abc.txt", properties);
        parse(command);
    }

    @Test
    public void testFileParsingSemiColons() {
        properties.put("file:", BYTE_ARRAY_SEMI_COLONS);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 --nodefile file:abc.txt", properties );
        parseCommandAndAssertSuccess(command, expectedNodes);
    }

    @Test
    public void testFileParsingSpaces() {
        properties.put("file:", BYTE_ARRAY_SPACES);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 -nf file:abc.txt", properties );
        parseCommandAndAssertSuccess(command, expectedNodes);
    }

    @Test
    public void testFileParsingTabs() {
        properties.put("file:", BYTE_ARRAY_TABS);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 -nf file:abc.txt", properties );
        parseCommandAndAssertSuccess(command, expectedNodes);
    }

    @Test
    public void testFileParsingCarriageReturn() {
        properties.put("file:", BYTE_ARRAY_CARRIAGE_RETURN);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 -nf file:abc.txt", properties );
        parseCommandAndAssertSuccess(command, expectedNodes);
    }

    @Test
    public void testFileParsingNewLine() {
        properties.put("file:", BYTE_ARRAY_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 -nf file:abc.txt", properties );
        parseCommandAndAssertSuccess(command, expectedNodes);
    }

    @Test
    public void testCommandWithFileParserFileWithSpaces() {
        properties.put("file:", FILE_WITH_SPACES);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 -nf file:abc.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
    }

    @Test
    public void testCommandWithFileParserFileWithTabs() {
        properties.put("file:", FILE_WITH_TABS);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 --nodefile file:abc.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
    }

    @Test
    public void testCommandWithFileParserFileWithSemiColon() {
        properties.put("file:", FILE_WITH_SEMI_COLONS);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 -nf file:abc.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
    }

    @Test
    public void testCommandWithFileParserFileWithNewLine() {
        properties.put("file:", FILE_WITH_NEW_LINE);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 -nf file:abc.txt", properties);
        parseCommandAndAssertSuccess(command, expectedNodesFromFile);
    }


    @Test
    public void testFileSuppliedBadFileName() {
        parseCommandAndAssertFail("sl set -l 1 -nf abc.txt");
        parseCommandAndAssertFail("sl set -l 1 -nf .txt");
        parseCommandAndAssertFail("sl set -l 1 -nf file:abc");
        parseCommandAndAssertFail("sl set -l 1 -nf £$.txt");
    }

    @Test
    public void testFileDoesNotExist() {
        properties.put("file:", EMPTY_BYTE_ARRAY);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 -nf file:abc.txt", properties);
        parseCommandAndAssertFail(command);
    }

    @Test
    public void testFileFileEmpty() {
        properties.put("file:", EMPTY_FILE);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 -nf file:abc.txt", properties);
        parseCommandAndAssertFail(command);
    }

    @Test
    public void testFileWithRubbish() {
        properties.put("file:", EMPTY_FILE);
        final NscsCliCommand command = new NscsCliCommand("sl set -l 1 -nf file:.txt", properties);
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
            log.error("File passed in was empty");
            e.printStackTrace();
        }
        return fileToBeParsed;
    }
}
