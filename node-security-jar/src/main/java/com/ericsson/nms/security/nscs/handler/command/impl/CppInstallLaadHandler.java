package com.ericsson.nms.security.nscs.handler.command.impl;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.LaadFileInstallationException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.laad.ex.LaadServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustExistValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustHaveSecurityMoValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;
import com.ericsson.nms.security.nscs.laad.service.InstallLaad;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import org.slf4j.Logger;

import javax.ejb.Local;
import javax.inject.Inject;
import java.util.List;

/**
 * <p>
 * Installs LAAD file into specified nodes
 * </p>
 * Created by emaynes on 02/05/2014.
 */
@UseValidator({NoDuplNodeNamesAllowedValidator.class, NodeMustExistValidator.class, NodeMustHaveSecurityMoValidator.class})
@CommandType(NscsCommandType.CPP_INSTALL_LAAD)
@Local(CommandHandlerInterface.class)
public class CppInstallLaadHandler implements CommandHandler<NscsNodeCommand>, CommandHandlerInterface {

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    InstallLaad laadService;

    private static final String expectedSecurityLevel = "LEVEL_3";

    public static final String INSTALL_LAAD_INITIATED = "NEs have been validated, Install of laad user file beginning. Use Logviewer for results.";

    /**
     * 
     * @param nodeCommand
     *            NscsNodeCommand instance
     * @param context a CommandContext instance
     * @return NscsMessageCommandResponse with a success message
     * @throws com.ericsson.nms.security.nscs.api.exception.LaadFileInstallationException
     *             - if installation fails
     */
    @Override
    public NscsCommandResponse process(final NscsNodeCommand nodeCommand, final CommandContext context) throws NscsServiceException {

        checkAllCppNodesAreOnSecLevel3(context.getValidNodes());

        try {
            logger.debug("Trying to install LAAD file...");
            laadService.installLaad();
        } catch (final LaadServiceException ex) {
            logger.error("Error installing LAAD file.", ex);
            throw new LaadFileInstallationException();
        }

        return NscsCommandResponse.message(INSTALL_LAAD_INITIATED);
    }

    private void checkAllCppNodesAreOnSecLevel3(final List<? extends NodeReference> nodes) {
        logger.info("Checking operationalSecuritylevel for all level 3 nodes");

        final CmResponse cmresponse = reader.getMOAttribute(nodes, Model.ME_CONTEXT.managedElement.systemFunctions.security.type(),
                Model.ME_CONTEXT.managedElement.systemFunctions.security.namespace(), Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL);

        String securityLevel = null;
        // Loop and check all node are at level 3
        for (final CmObject cmObject : cmresponse.getCmObjects()) {
            try {
                securityLevel = (String) cmObject.getAttributes().get(Model.ME_CONTEXT.managedElement.systemFunctions.security.OPERATIONAL_SECURITY_LEVEL);
            } catch (final NullPointerException exception) {
                logger.debug("No attribute details returned!");
            }

            if (!expectedSecurityLevel.equals(securityLevel)) {
                logger.error("NetworkElement [{}] not in security level 3", cmObject.getFdn());
                throw new LaadFileInstallationException(NscsErrorCodes.INSTALL_LAAD_ERROR);
            }

        }

        logger.info("Checking for operationalSecuritylevel finished.");
    }
}
