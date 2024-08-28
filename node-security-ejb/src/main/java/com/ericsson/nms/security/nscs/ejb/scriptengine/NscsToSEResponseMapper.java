package com.ericsson.nms.security.nscs.ejb.scriptengine;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsConfirmationCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsDownloadRequestMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameValueCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsInvalidItemsException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.MultiErrorNodeException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.NodeException;
import com.ericsson.nms.security.nscs.cpp.seclevel.util.CppGetSecurityLevelConstants;
import com.ericsson.oss.services.scriptengine.spi.dtos.LineDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.ResponseDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.RowDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.confirmation.ConfirmationDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.file.FileDownloadRequestDto;
import com.ericsson.oss.services.scriptengine.spi.utils.TableBuilder;

/**
 * NscsToSEResponseMapper is responsible to perform proper translation of NscsCommandResponse objects and NscsServiceException exceptions into
 * expected CM response components.
 *
 * @author emaynes
 */
@ApplicationScoped
class NscsToSEResponseMapper {

    public static final String INVALID_NODES = "Invalid Nodes";
    @Inject
    private Logger logger;

    public static final int NODE_COLUMN = 0;
    public static final int ERROR_CODE_COLUMN = 1;
    public static final int ERROR_MESSAGE_COLUMN = 2;
    public static final int ERROR_SOLUTION_COLUMN = 3;
    public static final int INVALID_ITEMS = 1;
    public static final String ERROR = "Error ";
    public static final String SUGGESTED_SOLUTION = "Suggested Solution : ";
    public static final int SUCCESS_STATUS_CODE = 0;
    public static final String EMPTY_RESULT_LIST_MESSAGE = "Empty result list";
    public static final String INVALID_ITEMS_LIST_ERROR_MESSAGE = "One or more %ss are invalid";
    public static final String SUCCESS_DEFAULT_STATUS_MESSAGE = "Command Executed Successfully";
    public static final String INVALID_ITEMS_HEADER = "Problematic %ss";
    public static final String NODE_COLUMN_HEADER = "Node";
    public static final String ERROR_CODE_COLUMN_HEADER = "Error code";
    public static final String ERROR_MESSAGE_COLUMN_HEADER = "Error message";
    public static final String ERROR_SOLUTION_COLUMN_HEADER = "Suggested Solution";

    private String defaultErrorMessage = "";
    private String defaultSuggestedSolution = "";

    NscsToSEResponseMapper() {
        defaultErrorMessage = String.format("Error Code %d : %s", NscsServiceException.ErrorType.UNEXPECTED_ERROR.toInt(),
                NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
        defaultSuggestedSolution = String.format("Suggested Solution : %s", NscsErrorCodes.THIS_IS_AN_UNEXPECTED_SYSTEM_ERROR);
    }

    /**
     * Transform a NscsCommandResponse into a CommandResponseDto
     * 
     * @param nscsCommandResponse
     *            NscsCommandResponse returned by a CommandHandler
     * @param commandName
     *            the command name.
     * @return Translated CommandResponseDto
     */
    public void convertoToCommandResponseDto(final ResponseDto responseDto, final NscsCommandResponse nscsCommandResponse, final String commandName) {

        if (nscsCommandResponse.isMessageResponseType()) {
            convertMessageCommandResponseToResponseDto(responseDto, (NscsMessageCommandResponse) nscsCommandResponse);
        } else if (nscsCommandResponse.isDownloadRequestMessageResponseType()) {
            convertDownloadRequestMessageCommandResponseToResponseDto(responseDto, (NscsDownloadRequestMessageCommandResponse) nscsCommandResponse);
        } else if (nscsCommandResponse.isConfirmationResponseType()) {
            convertConfirmationCommandResponseToResponseDto(responseDto, (NscsConfirmationCommandResponse) nscsCommandResponse, commandName);
        } else if (nscsCommandResponse.isNameValueResponseType()) {
            convertNameValueCommandResponseToResponseDto(responseDto, (NscsNameValueCommandResponse) nscsCommandResponse);
        } else if (nscsCommandResponse.isNameMultipleValueResponseType()) {
            convertNameMultipleValueCommandResponseToResponseDto(responseDto, (NscsNameMultipleValueCommandResponse) nscsCommandResponse);
        } else {
            logger.error("Unexpected response type found : {}", nscsCommandResponse);
            addMessages(responseDto, "Command executed successfully, but it is not possible to display the result.");
        }
    }

    /**
     * Convert a {@link NscsMessageCommandResponse} into a {@link ResponseDto}.
     * 
     * @param responseDto
     *            the {@link ResponseDto} DTO.
     * @param messageResponse
     *            the {@link NscsMessageCommandResponse} DTO returned by a CommandHandler.
     */
    private void convertMessageCommandResponseToResponseDto(final ResponseDto responseDto, final NscsMessageCommandResponse messageResponse) {
        logger.info("Command response type is NscsMessageCommandResponse. Messages length is {}",
                messageResponse.getMessages() != null ? messageResponse.getMessages().length : 0);
        addMessages(responseDto, messageResponse.getMessages());
    }

    /**
     * Convert a {@link NscsDownloadRequestMessageCommandResponse} into a {@link ResponseDto}.
     * 
     * @param responseDto
     *            the {@link ResponseDto} DTO.
     * @param downloadRequestMessageResponse
     *            the {@link NscsDownloadRequestMessageCommandResponse} DTO returned by a CommandHandler.
     */
    private void convertDownloadRequestMessageCommandResponseToResponseDto(final ResponseDto responseDto,
            final NscsDownloadRequestMessageCommandResponse downloadRequestMessageResponse) {
        if (downloadRequestMessageResponse.getAdditionalInformation().isEmpty()) {
            addDownLoadRequestMessage(responseDto, downloadRequestMessageResponse);
        } else {
            prepareResponseTable(responseDto, downloadRequestMessageResponse);
        }
    }

    /**
     * Convert a {@link NscsConfirmationCommandResponse} into a {@link ResponseDto}.
     * 
     * @param responseDto
     *            the {@link ResponseDto} DTO.
     * @param confirmationResponse
     *            the {@link NscsConfirmationCommandResponse} DTO returned by a CommandHandler.
     * @param commandName
     *            the command name.
     */
    private void convertConfirmationCommandResponseToResponseDto(final ResponseDto responseDto,
            final NscsConfirmationCommandResponse confirmationResponse, final String commandName) {
        responseDto.getElements().add(createLineDto(confirmationResponse.getConfirmationMessage()));
        for (final String additionalConfirmationMessage : confirmationResponse.getAdditionalConfirmationMessages()) {
            responseDto.getElements().add(createLineDto(additionalConfirmationMessage));
        }
        responseDto.getElements().add(createConfirmationDto(confirmationResponse.getConfirmationMessage(), commandName));
    }

    /**
     * Creates a confirmation DTO.
     * 
     * @param confirmationMessage
     *            the confirmation message.
     * @param commandName
     *            the command name.
     * @return the confirmation DTO.
     */
    private ConfirmationDto createConfirmationDto(final String confirmationMessage, final String commandName) {
        return new ConfirmationDto(confirmationMessage, commandName);
    }

    /**
     * Convert a {@link NscsNameValueCommandResponse} into a {@link ResponseDto}.
     * 
     * @param responseDto
     *            the {@link ResponseDto} DTO.
     * @param nameValueResponse
     *            the {@link NscsNameValueCommandResponse} DTO returned by a CommandHandler.
     */
    private void convertNameValueCommandResponseToResponseDto(final ResponseDto responseDto, final NscsNameValueCommandResponse nameValueResponse) {
        if (nameValueResponse.isEmpty()) {
            addMessages(responseDto, EMPTY_RESULT_LIST_MESSAGE);
        } else {
            final TableBuilder tableBuilder = new TableBuilder();

            int rowNumber = 0;
            final Iterator<NscsNameValueCommandResponse.Entry> iterator = nameValueResponse.iterator();
            final NscsNameValueCommandResponse.Entry header = iterator.next();
            final String firstColumnTitle = header.getName();
            final String secondColumnTitle = header.getValue();

            final List<NscsNameValueCommandResponse.Entry> pairsWithHeaderRemoved = getPairsWithHeaderRemoved(iterator);
            final NscsNameValueCommandResponse.EntryComparator entryComparator = nameValueResponse.new EntryComparator();
            Collections.sort(pairsWithHeaderRemoved, entryComparator);

            final Iterator<NscsNameValueCommandResponse.Entry> iter = pairsWithHeaderRemoved.iterator();
            tableBuilder.withHeader(0, firstColumnTitle);
            tableBuilder.withHeader(1, secondColumnTitle);

            while (iter.hasNext()) {
                final NscsNameValueCommandResponse.Entry item = iter.next();
                tableBuilder.withCell(rowNumber, 0, item.getName());
                tableBuilder.withCell(rowNumber, 1, item.getValue());
                rowNumber++;
            }

            addTable(responseDto, tableBuilder);
            addMessages(responseDto, "", SUCCESS_DEFAULT_STATUS_MESSAGE);
        }
    }

    /**
     * Convert a {@link NscsNameMultipleValueCommandResponse} into a {@link ResponseDto}.
     * 
     * @param responseDto
     *            the {@link ResponseDto} DTO.
     * @param nameMultipleValueResponse
     *            the {@link NscsNameMultipleValueCommandResponse} DTO returned by a CommandHandler.
     */
    private void convertNameMultipleValueCommandResponseToResponseDto(final ResponseDto responseDto,
            final NscsNameMultipleValueCommandResponse nameMultipleValueResponse) {
        if (nameMultipleValueResponse.isEmpty()) {
            logger.debug("Empty response");
            addMessages(responseDto, EMPTY_RESULT_LIST_MESSAGE);

        } else {
            final int valueSize = nameMultipleValueResponse.getValueSize();
            logger.debug("valueSize {}", valueSize);

            final TableBuilder tableBuilder = new TableBuilder();

            final Iterator<NscsNameMultipleValueCommandResponse.Entry> iterator = nameMultipleValueResponse.iterator();
            final NscsNameMultipleValueCommandResponse.Entry header = iterator.next();

            int colNumber = 0;
            tableBuilder.withHeader(colNumber, header.getName());
            final String[] headerValue = header.getValues();
            for (String headerText : headerValue) {
                tableBuilder.withHeader(++colNumber, headerValue[colNumber - 1]);
            }

            int rowNumber = 0;
            while (iterator.hasNext()) {
                final NscsNameMultipleValueCommandResponse.Entry item = iterator.next();

                tableBuilder.withCell(rowNumber, 0, item.getName());

                final String[] itemValue = item.getValues();
                for (int i = 0; i < itemValue.length; i++) {
                    tableBuilder.withCell(rowNumber, i + 1, itemValue[i]);
                }
                rowNumber++;
            }
            logger.debug("rowNumber {}", rowNumber);
            if (!nameMultipleValueResponse.getAdditionalInformation().isEmpty()) {
                addMessages(responseDto, nameMultipleValueResponse.getAdditionalInformation());
            }
            addTable(responseDto, tableBuilder);
            addMessages(responseDto, "", SUCCESS_DEFAULT_STATUS_MESSAGE);
        }
    }

    /**
     * Constructs a CommandResponseDto containing a ResponseDto with an array of LineDtos. Each LineDto contains one message
     * 
     * @param responseDto
     *            The responseDto into which the messages are added.
     * @param messages
     *            Each message as a single String
     * @return CommandResponseDto
     */
    public void constructMessageResponse(final ResponseDto responseDto, final String... messages) {
        addMessages(responseDto, messages);
    }

    /**
     * Adds a Table to the ResponseDto passed in, containing multiple error scenarios on different nodes.
     * 
     * @param responseDto
     *            The responseDto into which the node and error details are.
     * @param exception
     *            MultiErrorNodeException exception containing the different nodes and their errors
     */
    public void createErrorCommandResponse(final ResponseDto responseDto, final MultiErrorNodeException exception) {

        final TableBuilder tableBuilder = new TableBuilder();

        tableBuilder.withHeader(NODE_COLUMN, NODE_COLUMN_HEADER);
        tableBuilder.withHeader(ERROR_CODE_COLUMN, ERROR_CODE_COLUMN_HEADER);
        tableBuilder.withHeader(ERROR_MESSAGE_COLUMN, ERROR_MESSAGE_COLUMN_HEADER);
        tableBuilder.withHeader(ERROR_SOLUTION_COLUMN, ERROR_SOLUTION_COLUMN_HEADER);

        int rowNumber = 0;
        for (MultiErrorNodeException.Entry entry : exception) {

            tableBuilder.withCell(rowNumber, NODE_COLUMN, entry.getNode().getFdn());
            tableBuilder.withCell(rowNumber, ERROR_CODE_COLUMN, String.valueOf(entry.getException().getErrorCode()));
            tableBuilder.withCell(rowNumber, ERROR_MESSAGE_COLUMN, entry.getException().getMessage());
            tableBuilder.withCell(rowNumber, ERROR_SOLUTION_COLUMN, entry.getException().getSuggestedSolution());

            rowNumber++;

            if (entry.getException() instanceof NscsInvalidItemsException) {
                final NscsInvalidItemsException itemsException = (NscsInvalidItemsException) entry.getException();
                tableBuilder.withCell(rowNumber++, ERROR_MESSAGE_COLUMN, String.format(INVALID_ITEMS_HEADER, itemsException.getItemType()));
                tableBuilder.withCell(rowNumber++, ERROR_MESSAGE_COLUMN, join(itemsException.getItemsList()));
            }
        }
        responseDto.setDtoName(INVALID_NODES);
        addTable(responseDto, tableBuilder);
        addMessages(responseDto, "", String.valueOf(ERROR + exception.getErrorCode()) + " : " + exception.getMessage(),
                SUGGESTED_SOLUTION + exception.getSuggestedSolution());
    }

    /**
     * Adds a Table containing a list of nodes for a single exception to the ResponseDto passed in.
     * 
     * @param responseDto
     *            The responseDto into which the node and error details are.
     * @param exception
     *            NodeException containing the error and the list of nodes
     */
    public void createErrorCommandResponse(final ResponseDto responseDto, final NodeException exception) {

        final NscsServiceException original = (NscsServiceException) exception.getCause();

        final TableBuilder tableBuilder = new TableBuilder();

        if (original instanceof NscsInvalidItemsException) {
            tableBuilder.withHeader(NODE_COLUMN, NODE_COLUMN_HEADER);
            tableBuilder.withHeader(INVALID_ITEMS, INVALID_ITEMS_HEADER);

            final NscsInvalidItemsException itemsException = (NscsInvalidItemsException) original;

            final String itemCol = String.format(INVALID_ITEMS_HEADER, itemsException.getItemType());

            int rowNumber = 0;
            for (MultiErrorNodeException.Entry entry : exception) {
                tableBuilder.withCell(rowNumber, NODE_COLUMN, entry.getNode().getFdn());
                tableBuilder.withCell(rowNumber, INVALID_ITEMS, join(((NscsInvalidItemsException) entry.getException()).getItemsList()));
                rowNumber++;
            }
            tableBuilder.withCell(rowNumber, NODE_COLUMN, itemCol);

            responseDto.setDtoName(INVALID_NODES);
            addTable(responseDto, tableBuilder);
            addMessages(responseDto, "", String.valueOf(ERROR + original.getErrorCode()) + " : " + original.getMessage(),
                    SUGGESTED_SOLUTION + original.getSuggestedSolution());

        } else {

            tableBuilder.withHeader(NODE_COLUMN, NODE_COLUMN_HEADER);

            int rowNumber = 0;
            for (MultiErrorNodeException.Entry entry : exception) {
                tableBuilder.withCell(rowNumber, NODE_COLUMN, entry.getNode().getFdn());
                rowNumber++;
            }

            responseDto.setDtoName(INVALID_NODES);
            addTable(responseDto, tableBuilder);
            addMessages(responseDto, "", String.valueOf(ERROR + original.getErrorCode()) + " : " + original.getMessage(),
                    SUGGESTED_SOLUTION + original.getSuggestedSolution());
        }
    }

    /**
     * Receives a Table (in TableBuilder format), builds the table and adds it to the ResponseDto passed in.
     * 
     * @param responseDto
     *            The responseDto into which the table is added.
     * @param tableBuilder
     *            The TableBuilder containing the RowDtos and HeaderRowDto
     */
    public void addTable(final ResponseDto responseDto, final TableBuilder tableBuilder) {
        final List<RowDto> rows = tableBuilder.build();
        responseDto.getElements().addAll(rows);
    }

    /**
     * Adds a list of items, the errorMessages and iten types to the ResponseDto passed in.
     * 
     * @param responseDto
     *            The responseDto into which the items, itemType and error messages are added.
     * @param items
     *            The list of items
     * @param errorMessage
     *            The errormessage for the items
     * @param itemType
     *            The itemType for each item
     * @return CommandResponseDto containing the updated responseDto
     */
    public void constructResponseForInvalidItems(final ResponseDto responseDto, final List<String> items, final String errorMessage,
            final String itemType) {
        responseDto.setDtoName(INVALID_NODES);
        for (final String node : items) {
            addMessages(responseDto, node + " " + errorMessage + " " + itemType);
        }
    }

    /**
     * Creates a CommandResponseDto with one error message
     * 
     * @param responseDto
     *            The responseDto into which the error message will be added
     * @param statusMessage
     *            The error message to be added
     * @return CommandResponseDto
     */
    public void createErrorCommandResponse(final ResponseDto responseDto, final String statusMessage) {
        constructMessageResponse(responseDto, statusMessage);
    }

    /**
     * Add one or more messages to a ResponseDto to be displayed by ScriptEngine to the CLI UI.
     * 
     * @param responseDto
     *            The responseDto into which the messages will be added
     * @param messages
     *            The messages to be added
     */
    public void addMessages(final ResponseDto responseDto, final String... messages) {
        for (String message : messages) {
            responseDto.getElements().add(createLineDto(message));
        }
    }

    /**
     * Add the Standard Error Message to the ResponseDto passed in.
     * 
     * @param responseDto
     *            The responseDto into which the standard Error message will be added
     */
    public void createErrorCommandResponse(final ResponseDto responseDto) {
        addMessages(responseDto, defaultErrorMessage, defaultSuggestedSolution);
    }

    private LineDto createLineDto(final String lineEntry) {
        final LineDto lineDto = new LineDto(lineEntry);
        return lineDto;
    }

    private String join(final Collection<String> parts) {
        final StringBuilder buffer = new StringBuilder();
        if (parts != null) {
            for (String part : parts) {
                if (buffer.length() > 0) {
                    buffer.append(", ");
                }
                buffer.append(part);
            }
        }
        return buffer.toString();
    }

    private List<NscsNameValueCommandResponse.Entry> getPairsWithHeaderRemoved(final Iterator<NscsNameValueCommandResponse.Entry> iterator) {

        final List<NscsNameValueCommandResponse.Entry> pairsWithHeaderRemoved = new LinkedList<>();

        while (iterator.hasNext()) {
            final NscsNameValueCommandResponse.Entry item = iterator.next();
            if (CppGetSecurityLevelConstants.NODE_SECURITY_LEVEL_HEADER.equalsIgnoreCase(item.getValue())) {
                continue;
            } else {
                pairsWithHeaderRemoved.add(item);
            }
        }

        return pairsWithHeaderRemoved;
    }

    private void addDownLoadRequestMessage(final ResponseDto responseDto, final NscsDownloadRequestMessageCommandResponse downloadMessageResponse) {
        responseDto.getElements().add(new FileDownloadRequestDto("secadm", downloadMessageResponse.getFileIdentifier()));
        responseDto.getElements().add(createLineDto(downloadMessageResponse.getMessage()));
    }

    private void prepareResponseTable(final ResponseDto responseDto,
            final NscsDownloadRequestMessageCommandResponse nscsDownloadRequestMessageCommandResponse) {

        final TableBuilder tableBuilder = new TableBuilder();

        final Iterator<NscsDownloadRequestMessageCommandResponse.Entry> iterator = nscsDownloadRequestMessageCommandResponse.iterator();
        final NscsDownloadRequestMessageCommandResponse.Entry header = iterator.next();

        int colNumber = 0;
        tableBuilder.withHeader(colNumber, header.getName());
        final String[] headerValue = header.getValues();
        for (String headerText : headerValue) {
            tableBuilder.withHeader(++colNumber, headerValue[colNumber - 1]);
        }

        int rowNumber = 0;
        while (iterator.hasNext()) {
            final NscsDownloadRequestMessageCommandResponse.Entry item = iterator.next();

            tableBuilder.withCell(rowNumber, 0, item.getName());

            final String[] itemValue = item.getValues();
            for (int i = 0; i < itemValue.length; i++) {
                tableBuilder.withCell(rowNumber, i + 1, itemValue[i]);
            }
            rowNumber++;
        }
        logger.debug("rowNumber {}", rowNumber);
        if (!nscsDownloadRequestMessageCommandResponse.getAdditionalInformation().isEmpty()) {
            addMessages(responseDto, nscsDownloadRequestMessageCommandResponse.getAdditionalInformation());
            addDownLoadRequestMessage(responseDto, nscsDownloadRequestMessageCommandResponse);
        }
        addTable(responseDto, tableBuilder);
        addMessages(responseDto, "", SUCCESS_DEFAULT_STATUS_MESSAGE);
    }
}
