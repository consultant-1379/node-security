package com.ericsson.nms.security.nscs.parser;

import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;

/**
 * <p>
 * This interface defines a command parser capable of interpreting text inside NscsCliCommand, and create a correspondent NscsPropertyCommand
 * </p>
 * 
 * Created by emaynes on 01/05/2014.
 */
public interface NscsCliCommandParser{

    /**
     * Perform parsing of the command.
     * 
     * @param command NscsCliCommand with the command text in it
     * @return translated NscsPropertyCommand instance
     * @throws CommandSyntaxException
     */
    NscsPropertyCommand parseCommand(NscsCliCommand command) throws CommandSyntaxException;
}
