/*-----------------------------------------------------------------------------
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
package com.ericsson.nms.security.nscs.ejb.command;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.NscsService;
import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.CouldNotFindCommandHandlerException;
import com.ericsson.nms.security.nscs.api.exception.NscsSecurityViolationException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.NscsSystemException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.ejb.command.context.CommandContextImpl;
import com.ericsson.nms.security.nscs.ejb.command.context.exception.ExistingInvalidNodesException;
import com.ericsson.nms.security.nscs.ejb.command.node.NodeFetcher;
import com.ericsson.nms.security.nscs.ejb.command.node.NodeList;
import com.ericsson.nms.security.nscs.ejb.command.rbac.RbacAuthorizationManager;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidatorInterface;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.logger.NscsSystemRecorder;
import com.ericsson.nms.security.nscs.parser.NscsCliCommandParser;
import com.ericsson.oss.itpf.sdk.recording.CommandPhase;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import com.ericsson.oss.services.security.nscs.command.util.NscsCommandConstants;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * Main implementation of NscsService responsible to initiate the command validation and processing.
 * 
 * @author emaynes on 01/05/2014.
 */
@Stateless
public class NscsServiceBean implements NscsService {

    @Inject
    private NscsLogger logger;

    @Inject
    private NscsSystemRecorder systemRecorder;

    @Inject
    private NscsCliCommandParser commandParser;

    @Inject
    private NodeFetcher nodeFetcher;

    @Inject
    private RbacAuthorizationManager rbacAuthorizationManager;

    @Inject
    private BeanManager beanManager;

    @Inject
    private NscsContextService nscsContextService;

    /**
     * Process a text command by parsing it and calling processCommand(NscsPropertyCommand)
     * 
     * @param commandObject
     *            - the commandObject
     * @return
     * @throws NscsServiceException
     */
    @Override
    public NscsCommandResponse processCommand(final NscsCliCommand commandObject) throws NscsServiceException {

        NscsPropertyCommand nscsCommand = null;
        final long startTime = System.currentTimeMillis();
        float partialElapsedParsing = 0.0F;
        try {
            nscsCommand = commandParser.parseCommand(commandObject);
        } catch (final Exception e) {
            logger.commandStarted(NscsCommandConstants.SECADM_COMMAND_PREFIX, null);
            systemRecorder.recordError("Node Security Service", ErrorSeverity.INFORMATIONAL, "Starting the Validation of Security Command",
                    "NETWORK.INITIAL_NODE_ACCESS", "PARSING_ERROR");
            logger.info("Command syntax error: ", e);
            logger.commandFinishedWithError(NscsCommandConstants.SECADM_COMMAND_PREFIX, null, "Command syntax error");
            throw e;
        } finally {
            final long endTime = System.currentTimeMillis();
            partialElapsedParsing = (endTime - startTime) / 1000F;
            logger.info("Partial elapsed time for command parsing g4 grammar: {}", String.format(Locale.ROOT, "%.3f", partialElapsedParsing));
        }

        logger.commandStarted(NscsCommandConstants.SECADM_COMMAND_PREFIX, commandObject);

        try {
            Bean<?> bean = getCommandHandlerBeanForType(nscsCommand.getCommandType());
            CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
            final CommandHandler<?> commandHandler = (CommandHandler<?>) beanManager.getReference(bean, CommandHandlerInterface.class, creationalContext);

            try {
                final NscsPropertyCommand targetCommandObject = createExpectedCommandForHandler(commandHandler, nscsCommand);

                checkRbacAuthorization(targetCommandObject);

                NscsCommandResponse response = null;
                try {
                    response = this.processCommand(nscsCommand);
                    logger.commandFinishedWithSuccess(NscsCommandConstants.SECADM_COMMAND_PREFIX, commandObject, "Any VALIDATION was successful");
                } finally {
                    final long endTime = System.currentTimeMillis();
                    final float totalElapsedTime = (endTime - startTime) / 1000F;
                    final float commandElapsedTime = totalElapsedTime - partialElapsedParsing;
                    logger.info("Partial elapsed time for command processing: {}", String.format(Locale.ROOT, "%.3f", commandElapsedTime));
                    logger.info("Total elapsed time for command parsing and processing: {}", String.format(Locale.ROOT, "%.3f", totalElapsedTime));
                }

                return response;
            } finally {
                creationalContext.release();
            }
        } catch (final Exception e) {
            logger.commandFinishedWithError(NscsCommandConstants.SECADM_COMMAND_PREFIX, commandObject, e.getMessage());
            throw e;
        }
    }

    /**
     * Executes a NscsPropertyCommand or subclass.
     * <p>
     * This class actually finds the proper command handler implementation based on the getCommandType property of the provided NscsPropertyCommand
     * and dispatches the command to it, after performing any required validation.
     * </p>
     * 
     * @param commandObject
     * @return
     * @throws NscsServiceException
     */
    @Override
    public NscsCommandResponse processCommand(final NscsPropertyCommand commandObject) throws NscsServiceException {
        NodeList nodeList = null;
        final NscsCommandType commandType = (commandObject != null) ? commandObject.getCommandType() : null;
        logger.info("NscsService starting processCommand: {} invoked by {}", commandType, commandObject.getCommandInvokerValue().toString());

        systemRecorder.recordSecurityEvent("Node Security Service",
                "Starting the Processing of Security Command invoked by " + commandObject.getCommandInvokerValue().toString(), "anonymous",
                "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.NOTICE, "IN-PROGRESS");

        final ExecutionContext executionContext = createExecutionContexts(commandObject);

        final String entityCommandSource = (commandObject.getCommandInvokerValue().equals(NscsPropertyCommand.NscsPropertyCommandInvoker.API))
                ? "Node Security API" : "Node Security Application";

        if (NscsNodeCommand.isNscsNodeCommand(executionContext.getCommand())) {

            final NscsNodeCommand nodeCommand = (NscsNodeCommand) executionContext.getCommand();

            systemRecorder.recordCommand(executionContext.getCommand().getCommandType().toString(), CommandPhase.STARTED, entityCommandSource,
                    (nodeCommand.getNodes() != null ? nodeCommand.getNodes().toString() : null), null);

            if (!nodeCommand.isAllNodes()) {
                // If * then Collection.empty
                // If --nodefile return Collection<String> getValue(NODE_LIST_PROPERTY)
                // If --nodelist return Collection<String> getValue(NODE_LIST_PROPERTY)
                final List<NodeReference> nodes = nodeCommand.getNodes();
                if (nodes != null) {
                    logger.debug("Fetching node list started: {}", nodes);
                } else {
                    logger.debug("Fetching node list started: No nodes to fetch");
                }
                nodeList = nodeFetcher.fetchNodes(nodes);
                logger.debug("Fetched node list is: {}", nodeList);
                executionContext.getContext().setValidNodes(nodeList.extractAllRemainingNodes());
                executionContext.getContext().setNodesNotFound(nodeList.getInvalidNodes());
            }

        } else {
            systemRecorder.recordCommand(executionContext.getCommand().toString(), CommandPhase.STARTED, entityCommandSource, null, null);
        }

        validateCommand(executionContext);
        logger.debug("All validators executed successfully, executing CommandHandler now {}",
                executionContext.getCommandHandler().getClass().getName());

        try {
            final NscsCommandResponse response = executionContext.getCommandHandler().process(executionContext.getCommand(),
                    executionContext.getContext());
            throwIfHasInvalidNode(executionContext);

            return response;
        } catch (final NscsSystemException e) {
            systemRecorder.recordError("Node Security Service", ErrorSeverity.ERROR, "Executing Security Command", "NETWORK.INITIAL_NODE_ACCESS",
                    e.getErrorType().toString());
            logger.error("Error during command execution. Re-throwing", e);
            throw e;
        } catch (final NscsServiceException e) {
            systemRecorder.recordError("Node Security Service", ErrorSeverity.INFORMATIONAL, "Executing Security Command",
                    "NETWORK.INITIAL_NODE_ACCESS", e.getErrorType().toString());
            logger.warn("Error during command execution. Re-throwing", e);
            throw e;
        } catch (final ExistingInvalidNodesException e) {
            systemRecorder.recordError("Node Security Service", ErrorSeverity.ERROR, "Executing Security Command", "NETWORK.INITIAL_NODE_ACCESS",
                    e.getMessage());
            logger.error("Error during command execution", e);
            throwIfHasInvalidNode(executionContext);
        } catch (final SecurityViolationException e) {
            logger.error("Security Violation Exception", e);
            throw new NscsSecurityViolationException();
        } catch (final Exception e) {
            systemRecorder.recordError("Node Security Service", ErrorSeverity.ERROR, "Executing Security Command", "NETWORK.INITIAL_NODE_ACCESS",
                    "UNEXPECTED ERROR");
            logger.error("Unexpected error during command execution. Throwing UnexpectedErrorException", e);
            throw new UnexpectedErrorException(e);
        }

        return null;
    }

    /**
     * Check in the command execution context if command has invalid nodes that cause an exception to be thrown and, if this is true, update the EJB
     * context with number of valid and invalid items (for CAL purposes) and throw the proper exception.
     * 
     * @param executionContext
     *            the command execution context.
     */
    private void throwIfHasInvalidNode(final ExecutionContext executionContext) {
        final Map<String, Integer> itemCounters = new HashMap<>();
        if (executionContext.getContext().hasInvalidNode(itemCounters)) {
            logger.info("CAL_DEBUG : throwing exception due to invalid nodes : {}", itemCounters);
            nscsContextService.setNumValidItemsContextValue(itemCounters.get("VALID"));
            nscsContextService.setNumInvalidItemsContextValue(itemCounters.get("INVALID"));
            executionContext.getContext().throwIfHasInvalidNode();
        }
    }

    /**
     * Check RBAC authorization.
     * 
     * @param nscsPropertyCommand
     *            the command.
     * @throws {@link
     *             NscsSecurityViolationException} if user is not authorized to perform the command.
     */
    private void checkRbacAuthorization(final NscsPropertyCommand nscsPropertyCommand) {
        final NscsCommandType commandType = nscsPropertyCommand.getCommandType();
        logger.info("NscsService: checking authorization with rbacAuthorizationManager of command: [{}]", commandType);
        try {
            rbacAuthorizationManager.checkAuthorization(nscsPropertyCommand);
        } catch (final SecurityViolationException | EJBTransactionRolledbackException e) {
            logger.error("Exception   -  message : ", e.getMessage());
            logger.error("Exception   -  cause : ", e.getCause());
            logger.error("Exception   -  class : " + e.getClass());
            throw new NscsSecurityViolationException();
        }
    }

    private void validateCommand(final ExecutionContext executionContext) {
        // final List<CommandValidator> validators = getValidatorsForCommandHandler(executionContext.getCommandHandler());

        // Retrieve Valildator instances
        CommandHandler<NscsPropertyCommand> commandHandler = executionContext.getCommandHandler();
        if (commandHandler.getClass().isAnnotationPresent(UseValidator.class)) {
            final UseValidator validatorsAnnotation = commandHandler.getClass().getAnnotation(UseValidator.class);
            for (final Class<? extends CommandValidator> validatorClazz : validatorsAnnotation.value()) {

                try {
                    Set<Bean<?>> beans = beanManager.getBeans(validatorClazz);
                    if (beans.size() == 1) {
                        Bean<?> bean = (Bean<?>) beans.iterator().next();
                        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
                        final CommandValidator validatorInstance = (CommandValidator) beanManager.getReference(bean, CommandValidatorInterface.class,
                                creationalContext);
                        try {
                            logger.debug("Executing validator: {}", validatorClazz.getSimpleName());
                            validatorInstance.validate(executionContext.getCommand(), executionContext.getContext());
                        } catch (final NscsSystemException e) {
                            systemRecorder.recordError("Node Security Service", ErrorSeverity.ERROR, "Starting the Validation of Security Command",
                                    "NETWORK.INITIAL_NODE_ACCESS", e.getErrorType().toString());
                            logger.error("Error during command validation. Re-throwing", e);
                            throw e;
                        } catch (final NscsServiceException e) {
                            systemRecorder.recordError("Node Security Service", ErrorSeverity.INFORMATIONAL,
                                    "Starting the Validation of Security Command", "NETWORK.INITIAL_NODE_ACCESS", e.getErrorType().toString());
                            logger.warn("Error during command validation. Re-throwing", e);
                            throw e;
                        } catch (final ExistingInvalidNodesException e) {
                            systemRecorder.recordError("Node Security Service", ErrorSeverity.ERROR, "Starting the Validation of Security Command",
                                    "NETWORK.INITIAL_NODE_ACCESS", e.getMessage());
                            logger.error("Error during command validation.", e);
                            throwIfHasInvalidNode(executionContext);
                        } catch (final Exception e) {
                            systemRecorder.recordError("Node Security Service", ErrorSeverity.ERROR, "Starting the Validation of Security Command",
                                    "NETWORK.INITIAL_NODE_ACCESS", e.getMessage());
                            logger.error("Unexpected error during command validation. Throwing UnexpectedErrorException", e);
                            throw new UnexpectedErrorException(e);
                        } finally {
                            creationalContext.release();
                        }
                    } else if (beans.size() < 1) {
                        String msg = "No Validator found for class " + validatorClazz.getSimpleName();
                        systemRecorder.recordError("Node Security Service", ErrorSeverity.ERROR, "Starting the Validation of Security Command",
                                "NETWORK.INITIAL_NODE_ACCESS", msg);
                        logger.error("Error during command validation. {}", msg);
                        throw new UnexpectedErrorException(msg);
                    } else {
                        String msg = "Multiple Validators found for class " + validatorClazz.getSimpleName();
                        systemRecorder.recordError("Node Security Service", ErrorSeverity.ERROR, "Starting the Validation of Security Command",
                                "NETWORK.INITIAL_NODE_ACCESS", msg);
                        logger.error("Error during command validation. {}", msg);
                        throw new UnexpectedErrorException(msg);
                    }
                } catch (final Exception e) {
                    logger.error("Error applying validator [" + validatorClazz.getSimpleName() + "] for commandType ["
                            + executionContext.getCommand().getCommandType() + "]");
                    throw e;
                }
            }
        }
        // check for invalid nodes
        throwIfHasInvalidNode(executionContext);
    }

    @SuppressWarnings("unchecked")
    private ExecutionContext createExecutionContexts(final NscsPropertyCommand commandObject) {

        ExecutionContext executionContext = null;

        Bean<?> bean = getCommandHandlerBeanForType(commandObject.getCommandType());
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        final CommandHandler<NscsPropertyCommand> commandHandler = (CommandHandler<NscsPropertyCommand>) beanManager.getReference(bean,
                CommandHandlerInterface.class, creationalContext);

        try {
            logger.debug("CommandHandler for command type {} is {}", commandObject.getCommandType(), commandHandler);

            final CommandContextImpl commandContext = createCommandContext(null);

            if (commandContext != null) {
                final NscsPropertyCommand targetCommandObject = createExpectedCommandForHandler(commandHandler, commandObject);
                executionContext = new ExecutionContext(targetCommandObject, commandHandler, commandContext);
            }

            return executionContext;
        } finally {
            creationalContext.release();
        }
    }

    private CommandContextImpl createCommandContext(final NodeList nodeList) {

        CommandContextImpl context = null;

        if (nodeList == null) {
            context = new CommandContextImpl();
        } else {
            context = new CommandContextImpl(nodeList.extractAllRemainingNodes(), nodeList.getInvalidNodes());
            if (logger.isDebugEnabled()) {
                logger.debug("Context created with {} valid nodes and {} nodes not found", context.getValidNodes().size(),
                        context.getNodesNotFound().size());
            }
        }

        return context;
    }

    private NscsPropertyCommand createExpectedCommandForHandler(final CommandHandler<?> commandHandler, final NscsPropertyCommand sourceCommand) {
        try {
            final Class<? extends NscsPropertyCommand> expectedType = getExpectedCommandTypeForHandler(commandHandler);
            final NscsPropertyCommand newCommand = expectedType.newInstance();
            newCommand.setCommandType(sourceCommand.getCommandType());
            newCommand.setProperties(sourceCommand.getProperties());

            return newCommand;
        } catch (final Exception e) {
            logger.error("Error creating expected Property command for handler [{}] ", commandHandler.getClass().getName());
            throw new UnexpectedErrorException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends NscsPropertyCommand> getExpectedCommandTypeForHandler(final CommandHandler<?> commandHandler) {
        Class<? extends NscsPropertyCommand> commandClazz = null;

        for (final Type type : commandHandler.getClass().getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                final ParameterizedType pType = (ParameterizedType) type;
                if (pType.getRawType() instanceof Class) {
                    final Class<?> interfaceClazz = (Class<?>) pType.getRawType();
                    if (CommandHandler.class.isAssignableFrom(interfaceClazz)) {
                        commandClazz = (Class<? extends NscsPropertyCommand>) pType.getActualTypeArguments()[0];
                    }
                }
            }
        }

        if (commandClazz == null) {
            commandClazz = NscsPropertyCommand.class;
        }

        return commandClazz;
    }

    /**
    * Get the instance of command handler for the given qualifier.
    * 
    * @param nscsCommandType
    *            the qualifier (the command type).
    * @return the bean instance.
    * @throws CouldNotFindCommandHandlerException
    *             if no instance or more than one instance found.
    */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Bean<CommandHandler> getCommandHandlerBeanForType(final NscsCommandType nscsCommandType) throws CouldNotFindCommandHandlerException {
        try {
            Set<Bean<?>> beans = beanManager.getBeans(CommandHandlerInterface.class, new CommandTypeQualifier(nscsCommandType));
            if (beans.size() == 1) {
                Bean<CommandHandler> bean = (Bean<CommandHandler>) beans.iterator().next();
                return bean;
            } else if (beans.size() < 1) {
                String msg = "No CommandHandler registered for commandType " + nscsCommandType;
                logger.error(msg);
                throw new CouldNotFindCommandHandlerException(msg);
            } else {
                String msg = "Multiple CommandHandler implementation found for commandType " + nscsCommandType;
                logger.error(msg);
                throw new CouldNotFindCommandHandlerException(msg);
            }
        } catch (final CouldNotFindCommandHandlerException e) {
            throw e;
        } catch (final Exception e) {
            logger.error("Internal Error retrieving CommandHandler for commandType [{}].", nscsCommandType);
            throw new CouldNotFindCommandHandlerException(e);
        }
    }

    private class CommandTypeQualifier extends AnnotationLiteral<CommandType> implements CommandType {

        private static final long serialVersionUID = 6834336125012984711L;
        private final NscsCommandType commandType;

        private CommandTypeQualifier(final NscsCommandType commandType) {
            this.commandType = commandType;
        }

        @Override
        public NscsCommandType value() {
            return commandType;
        }
    }

    private class ExecutionContext {
        private NscsPropertyCommand command;
        private CommandHandler<NscsPropertyCommand> commandHandler;
        private CommandContextImpl context;

        private ExecutionContext(final NscsPropertyCommand command, final CommandHandler<NscsPropertyCommand> commandHandler,
                final CommandContextImpl context) {
            this.command = command;
            this.commandHandler = commandHandler;
            this.context = context;
        }

        public NscsPropertyCommand getCommand() {
            return command;
        }

        public void setCommand(final NscsPropertyCommand command) {
            this.command = command;
        }

        public CommandHandler<NscsPropertyCommand> getCommandHandler() {
            return commandHandler;
        }

        public void setCommandHandler(final CommandHandler<NscsPropertyCommand> commandHandler) {
            this.commandHandler = commandHandler;
        }

        public CommandContextImpl getContext() {
            return context;
        }

        public void setContext(final CommandContextImpl context) {
            this.context = context;
        }
    }
}
