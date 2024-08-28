package com.ericsson.nms.security.nscs.api.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Subclass of NscsCommand which expects a command line type of argument to be parsed.
 * 
 * Created by emaynes on 01/05/2014.
 */
public class NscsCliCommand implements NscsCommand {

    private static final long serialVersionUID = 5140273685494829960L;

    private String commandText;

    private Map<String, Object> properties = new HashMap<>();

    public NscsCliCommand() {
    }

    public NscsCliCommand(final String commandText) {
        this.commandText = commandText;
    }

    public NscsCliCommand(final String commandText, final Map<String, Object> properties) {
        this.commandText = commandText;
        this.properties = properties;
    }

    /**
     * 
     * @return command line text provided E.g.: cpp-set-sl securityLevel=1 nodeList=node1
     */
    public String getCommandText() {
        return commandText;
    }

    /**
     * Sets command line text to be executed E.g.: cpp-set-sl securityLevel=1 nodeList=node1
     * @param commandText
     *          -the command line text provided E.g.: cpp-set-sl securityLevel=1 nodeList=node1
     */
    public void setCommandText(final String commandText) {
        this.commandText = commandText;
    }

    /**
     * @return a Map with the associated additional properties of the command. E.g.: could be an attached file
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Sets a Map with the properties that should be associated with this command
     * @param properties  Map with additional properties of the command
     */
    public void setProperties(final Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return commandText;
    }
}
