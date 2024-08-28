package com.ericsson.nms.security.nscs.parser.impl.antlr;

import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeListFile;
import com.ericsson.nms.security.nscs.parser.FileAttributeName;
import com.ericsson.nms.security.nscs.parser.NscsFileCommandParser;
import com.ericsson.nms.security.nscs.parser.SecCommandFileLexer;
import com.ericsson.nms.security.nscs.parser.SecCommandFileParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import javax.inject.Inject;
import java.util.Map;
import org.slf4j.Logger;


/**
 * A file parser which parses a file containing a list of nodes. Nodes are
 * added to the parser result map using NscsNodeCommand.NODE_LIST_PROPERTY as the key.
 * @author emaynes.
 */
@FileAttributeName(NscsNodeCommand.NODE_LIST_FILE_PROPERTY)
public class AntlrNodeNamesFileParser implements NscsFileCommandParser {

    @Inject
    private Logger log;

    @Override
    public void parserFile(final String content, final String fileAttributeName, final Map<String, Object> commandParseResult) {
        final SecCommandFileParser.NodeFileListContext nodesFilesContext = parseNodesFiles(content);
        commandParseResult.put(NscsNodeCommand.NODE_LIST_PROPERTY, nodesFilesContext.nodes);
    }

    private SecCommandFileParser.NodeFileListContext parseNodesFiles(final String fileContent) {
        final ANTLRInputStream antlrInputStream = new ANTLRInputStream(fileContent);

        final SecCommandFileLexer lexer = new SecCommandFileLexer(antlrInputStream);
        lexer.addErrorListener(new AntlrCommandParser.ErrorListener());

        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final SecCommandFileParser secCommandFileParser = new SecCommandFileParser(tokens);
        secCommandFileParser.removeErrorListeners();
        secCommandFileParser.addErrorListener(new AntlrCommandParser.ErrorListener());

        SecCommandFileParser.NodeFileListContext nodeFileListContext = null;
        try {
            nodeFileListContext = secCommandFileParser.nodeFileList();
        } catch (Exception e) {
            log.debug("Caught exception during parsing of the file content.", e);
            throw new InvalidNodeListFile();
        }

        if ( secCommandFileParser.getNumberOfSyntaxErrors() > 0 ) {
            log.debug("Caught exception during parsing of the file content: number of errors are {}", secCommandFileParser.getNumberOfSyntaxErrors());
            throw new InvalidNodeListFile();
        }

        return nodeFileListContext;
    }

}
