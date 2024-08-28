/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.parser.impl.antlr;

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.types.SshPrivateKeyImportCommand;
import com.ericsson.nms.security.nscs.parser.FileAttributeName;
import com.ericsson.nms.security.nscs.parser.NscsFileCommandParser;

/**
 * A file parser which parses a file containing a privateKey for a specific node. file content is added to the parser result map using
 * NscsNodeCommand.SSH_PRIVATE_KEY_FILE_PROPERTY as the key.
 *
 * @author zkttmnk.
 */
@FileAttributeName(SshPrivateKeyImportCommand.SSH_PRIVATE_KEY_FILE_PROPERTY)
public class AntlrSshPrivateKeyFileParser implements NscsFileCommandParser {

    @Override
    public void parserFile(final String content, final String fileAttributeName, final Map<String, Object> commandParseResult) {
        commandParseResult.put(SshPrivateKeyImportCommand.SSH_PRIVATE_KEY_FILE_PROPERTY, content);
    }

}
