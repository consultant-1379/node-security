package com.ericsson.nms.security.nscs.handler.command.impl;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.TargetGroupsCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidTargetGroupException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.TargetGroupsUpdateException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustBeNormalizableValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustExistValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustHaveNetworkElementSecurityMoValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import org.slf4j.Logger;

import javax.ejb.Local;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.ericsson.nms.security.nscs.data.Model.NETWORK_ELEMENT;

/**
 * <p>
 * Adds one or more target groups into NetworkElementSecurity Mo respecting provided list of nodes.
 * </p>
 * 
 * Created by emaynes on 13/05/2014.
 */
@CommandType(NscsCommandType.ADD_TARGET_GROUPS)
@Local(CommandHandlerInterface.class)
@UseValidator({NoDuplNodeNamesAllowedValidator.class, NodeMustExistValidator.class, NodeMustBeNormalizableValidator.class, NodeMustHaveNetworkElementSecurityMoValidator.class })
public class AddTargetGroupsHandler implements CommandHandler<TargetGroupsCommand>, CommandHandlerInterface {

    public static final String TARGET_GROUPS_SUCCESSFULLY_ADDED = "Target groups successfully added";
    @Inject
    private Logger logger;

    @Inject
    private NscsCMWriterService writer;

    @Inject
    private NscsCMReaderService reader;

    /**
     * 
     * @param command a TargetGroupsCommand instance representing the command to be performed
     * @param context a CommandContext instance
     * @return NscsMessageCommandResponse in case of success
     * @throws TargetGroupsUpdateException
     *             - if update fail for some node
     * @throws InvalidTargetGroupException
     *             - if an invalid target group is found
     */
    @Override
    public NscsCommandResponse process(final TargetGroupsCommand command, final CommandContext context) throws NscsServiceException {

        final ArrayList<String> groupsToAdd = new ArrayList<>();
        groupsToAdd.addAll(command.getTargetGroup());

        logger.info("Target groups to add into NetworkElementSecurity : {}", groupsToAdd);

        checkTargetGroups(groupsToAdd);

        final List<NodeReference> nodes = context.toNormalizedRef(context.getValidNodes());

        final CmResponse myCmResponse = reader.getMOAttribute(nodes, NETWORK_ELEMENT.securityFunction.networkElementSecurity.type(),
                NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace(), NETWORK_ELEMENT.securityFunction.networkElementSecurity.TARGET_GROUPS);
        logger.debug("Querying targetgroups on nodes [[{}]] ====> got response : {}", nodes, myCmResponse);
        checkTargetGroupAlreadyAssigned(myCmResponse, groupsToAdd);

        final List<String> addTargetGroupsFailedNodes = new LinkedList<>();
        for (final CmObject cmObject : myCmResponse.getCmObjects()) {
            final List<String> existingTargetGroups = (List<String>) cmObject.getAttributes().get(
                    NETWORK_ELEMENT.securityFunction.networkElementSecurity.TARGET_GROUPS);
            existingTargetGroups.addAll(groupsToAdd);

            try {
                logger.debug("Updating targetgroups in NetworkElementSecurity MO {}", cmObject.getFdn());
                writer.withSpecification(cmObject.getFdn()).setAttribute(NETWORK_ELEMENT.securityFunction.networkElementSecurity.TARGET_GROUPS, existingTargetGroups)
                        .updateMO();
            } catch (final Exception e) {
                logger.info("Update of NetworkElementSecurity MO failed!", e);
                addTargetGroupsFailedNodes.add(NETWORK_ELEMENT.extractName(cmObject.getFdn()));
            }
        }

        if (!addTargetGroupsFailedNodes.isEmpty()) {
            logger.info("Some MOs could not be updated : {}", addTargetGroupsFailedNodes);
            throw new TargetGroupsUpdateException(addTargetGroupsFailedNodes);
        }

        return NscsCommandResponse.message(TARGET_GROUPS_SUCCESSFULLY_ADDED);
    }

    private void checkTargetGroupAlreadyAssigned(final CmResponse cmResponse, final List<String> groupsToAdd) {
        final List<String> invalidNodes = new LinkedList<>();
        for (final CmObject cmObject : cmResponse.getCmObjects()) {
            final List<String> existingTargetGroups = new ArrayList<>((Collection<? extends String>) cmObject.getAttributes().get(
                    NETWORK_ELEMENT.securityFunction.networkElementSecurity.TARGET_GROUPS));

            logger.debug("Existing targetgroups in node [{}] are [{}]", cmObject.getFdn(), existingTargetGroups);
            existingTargetGroups.retainAll(groupsToAdd);

            if (!existingTargetGroups.isEmpty()) {
                logger.error("node [{}] have these target groups already set - {}", cmObject.getFdn(), existingTargetGroups);
                invalidNodes.add(NETWORK_ELEMENT.extractName(cmObject.getFdn()));
            }
        }

        if (!invalidNodes.isEmpty()) {
            logger.info("Error: there are nodes with target groups already assigned. {}", invalidNodes);
            throw new TargetGroupsUpdateException("Node has one or more target groups already set", invalidNodes);
        }
    }

    private void checkTargetGroups(final List<String> groupsToAdd) {
        final List<String> invalidTargetGroups = new ArrayList<>();
        for (final String group : groupsToAdd) {
            // TODO Will use injected target group validation API here
            // if(invalid) { add to invalidTargetGroups }
        }

        if (!invalidTargetGroups.isEmpty()) {
            logger.info("Invalid target groups found : {}", invalidTargetGroups);
            throw new InvalidTargetGroupException(invalidTargetGroups);
        }
    }
}
