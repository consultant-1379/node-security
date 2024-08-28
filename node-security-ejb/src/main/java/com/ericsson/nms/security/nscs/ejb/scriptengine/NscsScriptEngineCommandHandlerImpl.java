/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ejb.scriptengine;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.NscsService;
import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.exception.NscsInvalidItemsException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.NscsSystemException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.MultiErrorNodeException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.NodeException;
import com.ericsson.nms.security.nscs.util.DownloadFileHolder;
import com.ericsson.nms.security.nscs.util.ExportCacheItemsHolder;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceQualifier;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.scriptengine.spi.CommandHandler;
import com.ericsson.oss.services.scriptengine.spi.FileDownloadHandler;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.ResponseDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.file.FileDownloadResponseDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.file.InMemoryFileDto;
import com.ericsson.oss.services.security.nscs.command.util.NscsCommandConstants;

/**
 * The hook to script engine api, the main job of commandhandler
 * <li>is to pass the command object</li> and
 * <li>to return the response object</li>
 *
 * @author eabdsin
 *
 */
@Stateless
@EServiceQualifier("secadm")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class NscsScriptEngineCommandHandlerImpl implements CommandHandler, FileDownloadHandler {

    private static final String TORF_597790 = "TORF-597790";
    public static final String GOT_ERROR_FROM_NSCS_SERVICE = "Got error from NscsService.";

    @EServiceRef
    private NscsService nscsService;

    @Inject
    private NscsToSEResponseMapper nscsToSEResponseMapper;

    @Inject
    private Logger logger;

    @Inject
    ExportCacheItemsHolder exportCacheItemsHolder;

    @SuppressWarnings("deprecation")
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CommandResponseDto execute(final Command command) {

        dumpCommandString(command.getCommand());
        logger.info("Command properties are : {} ", command.getProperties());

        final CommandResponseDto secadmResponseDTO = new CommandResponseDto();

        final ResponseDto responseDto = new ResponseDto(new ArrayList<>());

        try {
            final NscsCliCommand nscsCliCommand = new NscsCliCommand(command.getCommand());
            if (command.getProperties() != null) {
                nscsCliCommand.setProperties(command.getProperties());
            }

            /**
             * To troubleshoot the https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/TORF-597790 related to wrong userId present in this EJB
             * invocation (ejbuser), an extra property "TORF-597790" containing as value the involved userId has been added to any command by the
             * script-engine. This extra property shall be removed before invoking the command handler since some command handlers perform strict
             * validation checks on the properties causing a failure if any unexpected property is present.
             */
            removeExtraProperties(nscsCliCommand);

            final NscsCommandResponse commandResponse = nscsService.processCommand(nscsCliCommand);

            final String commandName = String.format("%s %s", NscsCommandConstants.SECADM_COMMAND_PREFIX, nscsCliCommand.getCommandText());
            nscsToSEResponseMapper.convertoToCommandResponseDto(responseDto, commandResponse, commandName);

        } catch (final NscsSystemException se) {
            nscsToSEResponseMapper.createErrorCommandResponse(responseDto);
            logger.error(GOT_ERROR_FROM_NSCS_SERVICE, se);
        } catch (final NodeException e) {
            nscsToSEResponseMapper.createErrorCommandResponse(responseDto, e);
            logger.error(GOT_ERROR_FROM_NSCS_SERVICE, e);
        } catch (final MultiErrorNodeException e) {
            logger.error(GOT_ERROR_FROM_NSCS_SERVICE, e);
            nscsToSEResponseMapper.createErrorCommandResponse(responseDto, e);
        } catch (final NscsInvalidItemsException ine) {
            nscsToSEResponseMapper.constructResponseForInvalidItems(responseDto, ine.getItemsList(), ine.getMessage(), ine.getItemType());
            logger.error(GOT_ERROR_FROM_NSCS_SERVICE, ine);
        } catch (final NscsServiceException se) {
            nscsToSEResponseMapper.constructMessageResponse(responseDto, "Error " + se.getErrorCode() + " : " + se.getMessage(),
                    "Suggested Solution : " + se.getSuggestedSolution());
            logger.error(GOT_ERROR_FROM_NSCS_SERVICE, se);
        } catch (final Exception e) {
            nscsToSEResponseMapper.createErrorCommandResponse(responseDto);
            logger.error("Got unexpected error from NscsService.", e);
        }

        secadmResponseDTO.setCommand(NscsCommandConstants.SECADM_COMMAND_PREFIX + command.getCommand());
        secadmResponseDTO.setResponseDto(responseDto);

        logger.debug("Returning response to script-engine : {} ", secadmResponseDTO);
        return secadmResponseDTO;
    }

    /**
     * To troubleshoot the https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/TORF-597790 related to wrong userId present in this EJB
     * invocation (ejbuser), an extra property "TORF-597790" containing as value the involved userId has been added to any command by the
     * script-engine. This extra property shall be removed before invoking the command handler since some command handlers perform strict validation
     * checks on the properties causing a failure if any unexpected property is present.
     * 
     * @param nscsCliCommand
     *            the NSCS command.
     */
    public void removeExtraProperties(final NscsCliCommand nscsCliCommand) {
        nscsCliCommand.getProperties().entrySet().removeIf(entry -> entry.getKey().equals(TORF_597790));
    }

    private void dumpCommandString(final String commandString) {
        if (commandString.contains("credentials create") || commandString.contains("creds create")) {
            logger.info("Command Processing starts for Node Security Administration ....* {} ", NscsCommandType.CREATE_CREDENTIALS);
        } else if (commandString.contains("credentials update") || commandString.contains("creds update")) {
            logger.info("Command Processing starts for Node Security Administration ....* {} ", NscsCommandType.UPDATE_CREDENTIALS);
        } else if (commandString.contains("credentials get") || commandString.contains("creds get")) {
            logger.info("Command Processing starts for Node Security Administration ....* {} ", NscsCommandType.GET_CREDENTIALS);
        } else if (commandString.contains("snmp authpriv")) {
            logger.info("Command Processing starts for Node Security Administration ....* {} ", NscsCommandType.SNMP_AUTHPRIV);
        } else if (commandString.contains("snmp authnopriv")) {
            logger.info("Command Processing starts for Node Security Administration ....* {} ", NscsCommandType.SNMP_AUTHNOPRIV);
        } else {
            // TORF-672133: before successfully parsing the secadm command, the command text shall not be logged
            // to avoid logging of sensitive info (passwords and keys) due to failure of obfuscation mechanism.
            logger.info("Command Processing starts for Node Security Administration ....* ");
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public FileDownloadResponseDto execute(final String fileId) {
        logger.info("download request from script-engine key: {} ", fileId);
        InMemoryFileDto inMemoryFileDto = null;
        try {
            final DownloadFileHolder downloadFileHolder = (DownloadFileHolder) exportCacheItemsHolder.fetch(fileId);
            if (downloadFileHolder == null) {
                throw new IllegalArgumentException(String.format("file content is not found with key %s", fileId));
            }
            // build DTO cloning the file content before potentially deleting the cache item
            inMemoryFileDto = new InMemoryFileDto(downloadFileHolder.getContentToBeDownloaded().clone(), downloadFileHolder.getFileName(),
                    downloadFileHolder.getContentType());
            if (downloadFileHolder.isDeletable()) {
                exportCacheItemsHolder.getCache().remove(fileId);
                logger.info("content deleted from cache with key: {}", fileId);
            } else {
                logger.info("setting content to be deleted from cache with key: {}", fileId);
                ((DownloadFileHolder) exportCacheItemsHolder.fetch(fileId)).setDeletable(true);
            }
        } catch (final Exception e) {
            final String errorMsg = String.format("Exception %s: %s occurred executing file download request from script-engine with key %s",
                    e.getClass().getCanonicalName(), e.getMessage(), fileId);
            logger.error(errorMsg, e);
        }
        return inMemoryFileDto;
    }

}
