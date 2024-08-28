package com.ericsson.nms.security.nscs.handler.validation;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.handler.CommandContext;

/**
 * Interface that defines a command validator.
 * <p>
 * A CommandValidator is responsible for performing any kind of validation or pre-condition check before the CommandHandler execution.
 * </p>
 * 
 * <p>
 * Ideally a validator is created whenever there is a common check that needs to be performed by more than one command.
 * </p>
 * 
 * <p>
 * CommandHandlers can declare what validations are required by the use of the {@literal @}UseValidator annotation
 * </p>
 * 
 * <p>
 * Example:
 * </p>
 * 
 * <code><pre>
 *      {@literal @}CommandType( NscsCommandType.CPP_GET_SL )
 *      {@literal @}UseValidator(NodeMustHaveSecurityMoValidator.class)
 *      public class CppGetSecurityLevelHandler implements CommandHandler&lt;NscsNodeCommand&gt; {
 * 
 *          public NscsCommandResponse process(NscsNodeCommand nodeCommand) throws NscsServiceException {
 *                // Handler implementation...
 *          }
 *      }
 * </pre></code>
 * 
 * <p>
 * Multiple validators are allowed:
 * </p>
 * 
 * <code><pre>
 *      {@literal @}CommandType( NscsCommandType.CREATE_CREDENTIALS )
 *      {@literal @}UseValidator({StarIsNotAllowedValidator.class, NodeMustExistValidator.class})
 *      public class CreateCredentialsHandler implements CommandHandler&lt;CreateCredentialsCommand&gt; {
 * 
 *          public NscsCommandResponse process(CreateCredentialsCommand command) throws NscsServiceException {
 *                // Handler implementation...
 *          }
 *      }
 * </pre></code>
 * 
 * @author emaynes
 */
public interface CommandValidator extends CommandValidatorInterface {

    /**
     * Perform command validation.
     * 
     * @param command NscsPropertyCommand instance
     * @param context current command execution context
     * @throws NscsServiceException
     * @see com.ericsson.nms.security.nscs.handler.CommandContext
     */
    void validate(NscsPropertyCommand command, CommandContext context) throws NscsServiceException;

}
