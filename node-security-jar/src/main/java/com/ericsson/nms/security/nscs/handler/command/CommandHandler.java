package com.ericsson.nms.security.nscs.handler.command;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;

/**
 * Interface that defines a command handler.
 * <p>
 * A CommandHandler is responsible for performing actions in order to fulfil the command request.
 * </p>
 * 
 * <p>
 * CommandHandlers implementations are called by the NscsService implementation based on the NscsCommandType of the request.
 * </p>
 * 
 * <p>
 * CommadHandlers are associated with a specific NscsCommandType by the use of {@literal @}CommandType annotation
 * </p>
 * 
 * <p>
 * Example:
 * </p>
 * 
 * <code><pre>
 *      {@literal @}CommandType( NscsCommandType.CPP_GET_SL )
 *      public class CppGetSecurityLevelHandler implements CommandHandler&lt;NscsNodeCommand&gt; {
 * 
 *          public NscsCommandResponse process(NscsNodeCommand nodeCommand, CommandContext context) throws NscsServiceException {
 *                // Handler implementation...
 *          }
 *      }
 * </pre></code> Created by emaynes on 01/05/2014.
 */
public interface CommandHandler<T extends NscsPropertyCommand> extends CommandHandlerInterface {

    /**
     * Actual implementation of the command.
     * 
     * @param command a NscsPropertyCommand of subclass
     * @param context current command execution context
     * @return NscsCommandResponse or subclass
     * @throws NscsServiceException
     *
     * @see com.ericsson.nms.security.nscs.api.command.NscsCommandResponse
     * @see com.ericsson.nms.security.nscs.handler.CommandContext
     */
    NscsCommandResponse process(T command, CommandContext context) throws NscsServiceException;
}
