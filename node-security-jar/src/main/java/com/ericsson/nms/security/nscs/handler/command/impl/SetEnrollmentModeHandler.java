package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.SetEnrollmentCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnsupportedNodeTypeException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustBeNormalizableValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustHaveNetworkElementSecurityMoValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustHaveSecurityFunctionMoValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NormalizedNodeMustExistValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.StarIsNotAllowedValidator;

/**
 * <p>
 * Creates a NetworkElementSecurity Mo associated to each of the specified nodes.
 * </p>
 * Created by emaynes on 02/05/2014.
 */
@UseValidator({ NoDuplNodeNamesAllowedValidator.class, StarIsNotAllowedValidator.class, NormalizedNodeMustExistValidator.class,
        NodeMustBeNormalizableValidator.class, NodeMustHaveSecurityFunctionMoValidator.class, NodeMustHaveNetworkElementSecurityMoValidator.class })
@CommandType(NscsCommandType.SET_ENROLLMENT)
@Local(CommandHandlerInterface.class)
public class SetEnrollmentModeHandler implements CommandHandler<SetEnrollmentCommand>, CommandHandlerInterface {

    public static final String ENROLLMENT_MODE_SET_SUCCESSFULLY = "Enrollment mode has been successfully set";
    public static final String ENROLLMENT_MODE_SET_ERROR_NOT_WRITTEN = "Enrollment mode successfully set for all nodes except the following ones: ";
    public static final String ENROLLMENT_MODE_SET_ERROR_NO_VALID_NODES = "Error in setting Enrollment mode attribute : no valid nodes provided";
    public static final String ENROLLMENT_MODE_SET_ERROR = "Enrollment mode required not supported";

    @Inject
    private Logger logger;

    @Inject
    private NscsCMWriterService writer;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    /**
     * @param command
     *            SetEnrollmentCommand instance
     * @param context
     *            a CommandContext instance
     * @return NscsMessageCommandResponse
     */
    @Override
    public NscsCommandResponse process(final SetEnrollmentCommand command, final CommandContext context) throws NscsServiceException {
        logger.info("Set enrollment mode process invoked by " + command.getCommandInvokerValue().toString());
        logger.info("Starting the process of creating NetworkElementSecurity MO for nodes");

        boolean enrollmentSupported = true;
        if (context.getValidNodes().isEmpty()) {
            return NscsCommandResponse.message(ENROLLMENT_MODE_SET_ERROR_NO_VALID_NODES);
        }

        for (final NormalizableNodeReference node : context.getValidNodes()) {

            final boolean isCommandSupported = nscsCapabilityModelService.isCliCommandSupported(node, NscsCapabilityModelService.ENROLLMENT_COMMAND);
            if (!isCommandSupported) {
                logger.error("Node [{}] doesn't support set enrollment mode.", node.getNormalizedRef().getFdn());
                throw new UnsupportedNodeTypeException("This command is not supported for the selected node.");
            }

            final List<String> supportedEnrollmentModes = nscsCapabilityModelService.getSupportedEnrollmentModes(node);
            if (!supportedEnrollmentModes.contains(command.getValueString(SetEnrollmentCommand.ENROLLMENT_MODE_PROPERTY))) {
                logger.error(NscsErrorCodes.INVALID_ARGUMENT_VALUE);
                final String errmsg = String.format(" Invalid parameter %s for node %s with neType %s . Accepted arguments are %s",
                        SetEnrollmentCommand.ENROLLMENT_MODE_PROPERTY, node.getNormalizedRef().getFdn(), node.getNeType(), supportedEnrollmentModes);
                logger.error(errmsg);
                context.setAsInvalidOrFailed(node, new InvalidArgumentValueException(errmsg));
                enrollmentSupported = false;
            }
        }

        if (enrollmentSupported) {
            final List<String> enrollmentModeFailedNodes = new LinkedList<>();
            for (final NormalizableNodeReference node : context.getValidNodes()) {
                try {
                    logger.info("Updating Enrollment Mode in NetworkElementSecurity MO {}", node.getFdn());

                    final String networkElementSecurityFdn = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(node.getName())
                            .fdn();

                    logger.info("Updating Enrollment Mode in NetworkElementSecurity MO {} NES fdn", networkElementSecurityFdn);

                    writer.withSpecification(networkElementSecurityFdn).setAttribute(NetworkElementSecurity.ENROLLMENT_MODE,
                            command.getValueString(SetEnrollmentCommand.ENROLLMENT_MODE_PROPERTY)).updateMO();

                    logger.info("EnrolmentMode succesfully set for node {}", node.getFdn());

                } catch (final Exception e) {
                    logger.info("Update of Enrollment mode in NetworkElementSecurity MO failed!", e);
                    enrollmentModeFailedNodes.add(node.getFdn());
                }
            }

            if (!enrollmentModeFailedNodes.isEmpty()) {
                logger.info("Some MOs could not be updated : {}", enrollmentModeFailedNodes);
                return NscsCommandResponse.message(ENROLLMENT_MODE_SET_ERROR_NOT_WRITTEN + enrollmentModeFailedNodes);

            }
            return NscsCommandResponse.message(ENROLLMENT_MODE_SET_SUCCESSFULLY);
        }

        return NscsCommandResponse.message(ENROLLMENT_MODE_SET_ERROR);
    }

}