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
package com.ericsson.nms.security.nscs.handler.command.impl;

import javax.ejb.Local;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.SshPrivateKeyImportCommand;
import com.ericsson.nms.security.nscs.api.exception.ImportNodeSshPrivateKeyHandlerException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.nms.security.nscs.handler.validation.impl.ImportNodeSshPrivateKeyValidator;
import com.ericsson.nms.security.nscs.utilities.Constants;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>
 * updates enmSshPrivateKey attribute in NetworkElementSecurity MO.
 * </p>
 *
 * Created by zkttmnk.
 */
@CommandType(NscsCommandType.IMPORT_NODE_SSH_PRIVATE_KEY)
@Local(CommandHandlerInterface.class)
public class ImportNodeSshPrivateKeyHandler implements CommandHandler<SshPrivateKeyImportCommand>, CommandHandlerInterface {

    private static final String IMPORT_NODE_SSH_PRIVATE_KEY_SUCCESS_MSG = "SshPrivatekey import command executed Successfully";
    private static final String UNSUPPORTED_NODE_TYPE = "ERROR:Requested nodeType : %s does not have sshkey import support";

    @Inject
    private Logger logger;

    @Inject
    private NscsCMWriterService writer;

    @Inject
    private PasswordHelper passwordHelper;

    @Inject
    private ImportNodeSshPrivateKeyValidator importNodeSshPrivateKeyValidator;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private NscsContextService nscsContextService;

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.nms.security.nscs.handler.command.CommandHandler#process (com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand,
     * com.ericsson.nms.security.nscs.handler.CommandContext)
     */
    @Override
    public NscsCommandResponse process(final SshPrivateKeyImportCommand command, final CommandContext context) {

        final long startTime = System.currentTimeMillis();

        try {

            final String fileName = command.getFileName();
            importNodeSshPrivateKeyValidator.verifyFileNameAndExtension(fileName, Constants.FILE_EXT_TXT);

            logger.debug("ImportNodeSshPrivateKeyHandler privateKeyFile is not empty : {}", !command.getSshPrivateKeyFile().isEmpty());

            final NodeReference inputNodeReference = new NodeRef(command.getNodeName());
            final NormalizableNodeReference normNoderef = importNodeSshPrivateKeyValidator.validateNode(inputNodeReference);

            logger.debug("valid node to perform sshprivatekey import command : {}", normNoderef);

            if (!nscsCapabilityModelService.isNodeSshPrivateKeyImportSupported(normNoderef)) {
                final String errmsg = String.format(UNSUPPORTED_NODE_TYPE, normNoderef.getNeType());
                throw new InvalidArgumentValueException(errmsg);
            }

            nscsContextService.initItemsStatsForSyncCommand(Integer.valueOf(1), Integer.valueOf(0));

            logger.debug("ImportNodeSshPrivateKeyHandler nodeFDN : {}, nodeName : {}, neType : {}", normNoderef.getFdn(), normNoderef.getName(),
                    normNoderef.getNeType());
            final String encryptedPrivateKey = passwordHelper.encryptEncode(command.getSshPrivateKeyFile());
            final String networkElementSecurityFdn = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(normNoderef.getName())
                    .fdn();

            writer.withSpecification(networkElementSecurityFdn)
                    .setAttribute(ModelDefinition.NetworkElementSecurity.ENM_SSH_PRIVATE_KEY, encryptedPrivateKey).updateMO();

            nscsContextService.updateItemsResultStatsForSyncCommand(Integer.valueOf(1), Integer.valueOf(0));

        } catch (final Exception ex) {
            nscsContextService.initItemsStatsForSyncCommand(Integer.valueOf(0), Integer.valueOf(1));
            throw new ImportNodeSshPrivateKeyHandlerException(ex.getMessage());
        } finally {
            final long endTime = System.currentTimeMillis();
            logger.info("Total elapsed time for Import Key Handler: " + String.format("%.3f", (endTime - startTime) / 1000f));
        }

        return NscsCommandResponse.message(IMPORT_NODE_SSH_PRIVATE_KEY_SUCCESS_MSG);
    }

}