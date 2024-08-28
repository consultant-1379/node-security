package com.ericsson.nms.security.nscs.parser;

import java.util.Map;

/**
 * This interface defines a component which is responsible for parsing
 * the content of a file uploaded through the CLI.
 * @author emaynes.
 */
public interface NscsFileCommandParser extends NscsFileCommandParserInterface {

    void parserFile(String content, String fileAttributeName, Map<String, Object> commandParseResult);
}
