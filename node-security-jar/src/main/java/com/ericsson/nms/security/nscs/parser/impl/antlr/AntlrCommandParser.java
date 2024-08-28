package com.ericsson.nms.security.nscs.parser.impl.antlr;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.antlr.v4.runtime.*;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeListFile;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.parser.*;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>An implementation of NscsCliCommandParser using Antlr</p>
 * Created by emaynes on 01/05/2014.
 */
public class AntlrCommandParser implements NscsCliCommandParser {

    private static final int SECADM_CMD_SIZE = "secadm ".length();
    
    @Inject
    private Logger log;

    @Inject
    private BeanManager beanManager;
    
    @Inject
    private NscsContextService nscsContextService;

    @Override
    public NscsPropertyCommand parseCommand(final NscsCliCommand cliCommand) {
        final String commandString = cliCommand.getCommandText();

        if (commandString.contains("credentials create") || commandString.contains("creds create")) {
            log.debug("Parsing command: " + NscsCommandType.CREATE_CREDENTIALS);
        } else if (commandString.contains("credentials update") || commandString.contains("creds update")) {
            log.debug("Parsing command: " + NscsCommandType.UPDATE_CREDENTIALS);
        } else if (commandString.contains("credentials get") || commandString.contains("creds get")) {
            log.debug("Parsing command: " + NscsCommandType.GET_CREDENTIALS);
        } else {
            // TORF-672133: before successfully parsing the secadm command, the command text shall not be logged
            // to avoid logging of sensitive info (passwords and keys) due to failure of obfuscation mechanism.
            log.debug("Parsing command: *");
        }

        try {

            final SecCommandParser.ParseCommandContext parsedCommand = parseCommand(commandString);

            final Map<String, Object> commandParseResult = parsedCommand.attributes;

            if (!parsedCommand.filesAttributesNames.isEmpty()) {
                log.debug("Found files attributes : " + parsedCommand.filesAttributesNames);
                for (final String attName : parsedCommand.filesAttributesNames) {
                    if (!attName.equals("xmlfile")) {
                        Bean<?> bean = getFileCommandParserForAttName(attName);
                        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
                        try {
                            @SuppressWarnings("unchecked")
                            final NscsFileCommandParser fileCommandParser = (NscsFileCommandParser) beanManager.getReference(bean,
                                    NscsFileCommandParserInterface.class, creationalContext);
                            if (fileCommandParser != null) {
                                log.debug("Using file parser : {}", fileCommandParser);
                                fileCommandParser.parserFile(getFileContentFromCommand(cliCommand, commandParseResult.get(attName)), attName,
                                        commandParseResult);
                            }
                        } finally {
                            creationalContext.release();
                        }
                    }
                }
            }

            final NscsPropertyCommand nodeCommand = new NscsPropertyCommand();

            final NscsCommandType cmdType = NscsCommandType.valueOf((String) commandParseResult.get(NscsPropertyCommand.COMMAND_TYPE_PROPERTY));
            nodeCommand.setCommandType(cmdType);
            if (cliCommand.getProperties() != null) {
                nodeCommand.getProperties().putAll(cliCommand.getProperties());
            }
            nodeCommand.getProperties().putAll(commandParseResult);
            nscsContextService.setCommandTextContextValue(commandString);
            nscsContextService.setCommandTypeContextValue(cmdType.name());

            return nodeCommand;

        } catch (final NscsServiceException e) {
            log.debug("Caught NscsServiceException during parsing of the user command: Syntax error.", e);
            throw e;
        } catch (final Exception e) {
            log.info("Caught Exception during parsing of the user command: Syntax error.", e);
            throw new CommandSyntaxException(e);
        }
    }

    /**
     * Get the instance of file command parser for the given qualifier.
     * 
     * @param attName
     *            the qualifier
     * @return the bean instance or null (on error or if no instance or more than one instance found).
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Bean<?> getFileCommandParserForAttName(final String attName) {
        try {
            Set<Bean<?>> beans = beanManager.getBeans(NscsFileCommandParser.class, new FileAttributeNameQualifier(attName));
            if (beans.size() == 1) {
                Bean<NscsFileCommandParser> bean = (Bean<NscsFileCommandParser>) beans.iterator().next();
                return bean;
            } else if (beans.size() < 1) {
                String msg = "No file parser registered for option " + attName;
                log.warn(msg);
            } else {
                String msg = "Multiple file parser registered for option " + attName;
                log.warn(msg);
            }
        } catch (final Exception e) {
            String msg = "Could not find a file parser registered for option " + attName;
            log.warn(msg);
        }
        return null;
    }

    private String getFileContentFromCommand(final NscsCliCommand command, Object propertyKey) {
        String content = null;
        final Charset charset = Charset.forName("UTF-8");
        if ( propertyKey.toString().startsWith("file=") ) {
            propertyKey = propertyKey.toString().replace("file=", "file:");
        }
        final byte[] file = (byte[]) command.getProperties().get("file:");

        if ( file == null ) {
            log.debug("Could not find file data in [{}]", propertyKey);
            throw new InvalidNodeListFile();
        } else {
            content = new String(file, charset);
        }

        return content;
    }


    private SecCommandParser.ParseCommandContext parseCommand(final String toParse) {
        final ANTLRInputStream antlrInputStream = new ANTLRInputStream(toParse);

        SecCommandParser secCommandParser = null;

        final SecCommandLexer lexer = new SecCommandLexer(antlrInputStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ErrorListener(SECADM_CMD_SIZE));

        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        secCommandParser = new SecCommandParser(tokens);
        secCommandParser.removeErrorListeners();
        secCommandParser.addErrorListener(new ErrorListener(SECADM_CMD_SIZE));

        final SecCommandParser.ParseCommandContext context = secCommandParser.parseCommand();

        if ( secCommandParser.getNumberOfSyntaxErrors() > 0 ) {
            log.debug("Caught exception during parsing of the user command: number of errors are {}", secCommandParser.getNumberOfSyntaxErrors());
            throw new CommandSyntaxException();
        }

        return context;
    }

    static class ErrorListener extends BaseErrorListener {

        private int positionOffset = 0;

        ErrorListener() {
        }

        private ErrorListener(final int positionOffset) {
            this.positionOffset = positionOffset;
        }

        @Override
        public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine, final String msg, final RecognitionException e) {
               throw new CommandSyntaxException();
        }
    }

    private class FileAttributeNameQualifier extends AnnotationLiteral<FileAttributeName> implements FileAttributeName {

        private static final long serialVersionUID = 809622923486134210L;
        private final String value;

        private FileAttributeNameQualifier(final String value) {
            this.value = value;
        }

        @Override
        public String value(){
            return this.value;
        }
    }

}
