package com.ericsson.nms.security.nscs.ejb.scriptengine;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsConfirmationCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsDownloadRequestMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsNameValueCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.InvalidTargetGroupException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.wrapper.MultiErrorNodeException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.NodeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.oss.services.scriptengine.spi.dtos.AbstractDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.LineDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.ResponseDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.RowCell;
import com.ericsson.oss.services.scriptengine.spi.dtos.RowDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.confirmation.ConfirmationDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.file.FileDownloadRequestDto;

/**
 * @author emaynes.
 */
@RunWith(MockitoJUnitRunner.class)
public class NscsToSEResponseMapperTest {

    @Spy
    private Logger mapperLogger = LoggerFactory.getLogger(NscsToSEResponseMapper.class);

    @InjectMocks
    private NscsToSEResponseMapper mapper;

    @Test
    public void messageResponseTest() {
        final String message = "command executed successfully";

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());

        mapper.convertoToCommandResponseDto(responseDto, NscsCommandResponse.message(message), null);

        assertEquals(((LineDto) responseDto.getElements().get(0)).getValue(), message);

    }

    @Test
    public void nameValueResponseTest() {
        final String name1 = "Node Name", value1 = "Node Security Level";
        final String name2 = "name1", value2 = "value1";

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());

        mapper.convertoToCommandResponseDto(responseDto, NscsCommandResponse.nameValue().add(name1, value1).add(name2, value2), null);

        final List<String> rowsAsListOfStrings = getRowsAsListOfConcatenatedStrings(responseDto.getElements());

        assert (rowsAsListOfStrings.get(0).contains("Node Name"));
        assert (rowsAsListOfStrings.get(0).contains("Node Security Level"));
        assert (rowsAsListOfStrings.get(1).contains(name2));
        assert (rowsAsListOfStrings.get(1).contains(value2));

    }

    @Test
    public void nameMultipleValueResponse_Empty_Test() {
        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());
        mapper.convertoToCommandResponseDto(responseDto, NscsCommandResponse.nameMultipleValue(0), null);
        final List<String> rowsAsListOfStrings = getRowsAsListOfConcatenatedStrings(responseDto.getElements());
        final int expectedSize = 1;
        assert (expectedSize == rowsAsListOfStrings.size());
        assert (rowsAsListOfStrings.get(0) == NscsToSEResponseMapper.EMPTY_RESULT_LIST_MESSAGE);
    }

    @Test
    public void nameMultipleValueResponse_WithAdditionalInfo_Test() {
        final String nameHeader = "NAME";
        final String[] valuesHeader = new String[] { "VALUE1", "VALUE2" };
        final String name = "this is the name";
        final String[] values = new String[] { "this is the first value", "this is the second value" };

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());
        final NscsNameMultipleValueCommandResponse nscsResponse = NscsCommandResponse.nameMultipleValue(2);
        nscsResponse.add(nameHeader, valuesHeader).add(name, values);
        nscsResponse.setAdditionalInformation("this is the additional info");
        mapper.convertoToCommandResponseDto(responseDto, nscsResponse, null);
        final List<String> rowsAsListOfStrings = getRowsAsListOfConcatenatedStrings(responseDto.getElements());
        assert (rowsAsListOfStrings.get(0) == "this is the additional info");
        assert (rowsAsListOfStrings.get(1).contains(nameHeader));
        assert (rowsAsListOfStrings.get(1).contains(valuesHeader[0]));
        assert (rowsAsListOfStrings.get(1).contains(valuesHeader[1]));
        assert (rowsAsListOfStrings.get(2).contains(name));
        assert (rowsAsListOfStrings.get(2).contains(values[0]));
        assert (rowsAsListOfStrings.get(2).contains(values[1]));
    }

    @Test
    public void nameMultipleValueResponseTest() {
        final String name1 = "NODE";
        final String[] value1 = new String[] { "IPSEC_OM", "IPSEC_TRAFFIC" };
        final String name2 = "name2";
        final String[] value2 = new String[] { "value2", "value3" };

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());

        mapper.convertoToCommandResponseDto(responseDto, NscsCommandResponse.nameMultipleValue(2).add(name1, value1).add(name2, value2), null);

        final List<String> rowsAsListOfStrings = getRowsAsListOfConcatenatedStrings(responseDto.getElements());

        assert (rowsAsListOfStrings.get(1).contains(name2));
        assert (rowsAsListOfStrings.get(1).contains(value2[0]));
        assert (rowsAsListOfStrings.get(1).contains(value2[1]));
    }

    @Test
    public void nameValueResponseTestMultipleEntries() {
        final String name1 = "Node Name", value1 = "Node Security Level";
        final String name2 = "name1", value2 = "value1";
        final String name3 = "name2", value3 = "value2";

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());

        mapper.convertoToCommandResponseDto(responseDto, NscsCommandResponse.nameValue().add(name1, value1).add(name2, value2).add(name3, value3),
                null);

        final List<String> rowsAsListOfStrings = getRowsAsListOfConcatenatedStrings(responseDto.getElements());

        assert (rowsAsListOfStrings.get(1).contains("name1"));
        assert (rowsAsListOfStrings.get(1).contains(value2));
        assert (rowsAsListOfStrings.get(2).contains("name2"));
        assert (rowsAsListOfStrings.get(2).contains(value3));
    }

    @Test
    public void nameValueResponseTestMultipleEntriesSorted() {
        final String name1 = "Node Name", value1 = "Node Security Level";
        final String name2 = "name1", value2 = "value1";
        final String name3 = "name2", value3 = "value2";
        final String name4 = "name3", value4 = "value3";
        final String name5 = "name4", value5 = "value4";
        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());
        mapper.convertoToCommandResponseDto(responseDto,
                NscsCommandResponse.nameValue().add(name1, value1).add(name4, value4).add(name2, value2).add(name5, value5).add(name3, value3), null);

        assertNotNull("should have a CommandResponseDto.", responseDto);

        final List<String> rowsAsListOfStrings = getRowsAsListOfConcatenatedStrings(responseDto.getElements());

        assert (rowsAsListOfStrings.get(1).contains(name2));
        assert (rowsAsListOfStrings.get(1).contains(value2));
        assert (rowsAsListOfStrings.get(2).contains(name3));
        assert (rowsAsListOfStrings.get(2).contains(value3));
        assert (rowsAsListOfStrings.get(3).contains(name4));
        assert (rowsAsListOfStrings.get(3).contains(value4));
        assert (rowsAsListOfStrings.get(4).contains(name5));
        assert (rowsAsListOfStrings.get(4).contains(value5));

    }

    @Test
    public void wrongResponseTypeTest() {

        final NscsMessageCommandResponse nscsCommandResponse = Mockito.mock(NscsMessageCommandResponse.class);
        when(nscsCommandResponse.isMessageResponseType()).thenReturn(Boolean.FALSE);
        when(nscsCommandResponse.isNameValueResponseType()).thenReturn(Boolean.FALSE);

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());

        mapper.convertoToCommandResponseDto(responseDto, nscsCommandResponse, null);

        assertEquals(((LineDto) responseDto.getElements().get(0)).getValue(),
                "Command executed successfully, but it is not possible to display the result.");
    }

    @Test
    public void emptyNameValueTest() {

        final NscsNameValueCommandResponse nscsNameValueCommandResponse = Mockito.mock(NscsNameValueCommandResponse.class);
        when(nscsNameValueCommandResponse.isMessageResponseType()).thenReturn(Boolean.FALSE);
        when(nscsNameValueCommandResponse.isNameValueResponseType()).thenReturn(Boolean.TRUE);
        when(nscsNameValueCommandResponse.getResponseType()).thenReturn(NscsCommandResponse.NscsCommandResponseType.NAME_VALUE);
        when(nscsNameValueCommandResponse.isEmpty()).thenReturn(Boolean.TRUE);

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());

        mapper.convertoToCommandResponseDto(responseDto, nscsNameValueCommandResponse, null);

        assertEquals(((LineDto) responseDto.getElements().get(0)).getValue(), NscsToSEResponseMapper.EMPTY_RESULT_LIST_MESSAGE);

    }

    @Test
    public void errorResponseTest() {
        final String message = "command failed";

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());

        mapper.createErrorCommandResponse(responseDto, message);

        assertEquals(((LineDto) responseDto.getElements().get(0)).getValue(), message);
    }

    @Test
    public void listOfErrorNodesResponseTest() {
        final String message = "command failed";
        final List<String> errNodes = Arrays.asList("node1", "node2");
        final String itemType = "Node";

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());
        mapper.constructResponseForInvalidItems(responseDto, errNodes, message, itemType);

        final String line1 = ((LineDto) responseDto.getElements().get(0)).getValue();
        final String line2 = ((LineDto) responseDto.getElements().get(1)).getValue();

        assertTrue(line1.contains("node1 command failed Node"));
        assertTrue(line2.contains("node2 command failed Node"));
    }

    @Test
    public void nonItemNodeExceptionTest() {
        final InvalidNodeNameException invalidNodeNameException = new InvalidNodeNameException();
        final NodeReference node1 = new NodeRef("node1");
        final NodeException nodeException = new NodeException(Arrays.asList(node1, new NodeRef("node2")), invalidNodeNameException);

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());
        mapper.createErrorCommandResponse(responseDto, nodeException);

        final List<String> rowsAsListOfStrings = getRowsAsListOfConcatenatedStrings(responseDto.getElements());

        assert (rowsAsListOfStrings.get(4).contains(NscsErrorCodes.THE_NETWORK_ELEMENT_SPECIFIED_DOES_NOT_EXIST));
        assert (rowsAsListOfStrings.get(5).contains(NscsErrorCodes.PLEASE_SPECIFY_A_VALID_NETWORK_ELEMENT_THAT_EXISTS_IN_THE_SYSTEM));
    }

    @Test
    public void itemNodeExceptionTest() {
        final String invalidGroup = "ErrorGroup";
        final InvalidTargetGroupException invalidTargetGroupException = new InvalidTargetGroupException(Arrays.asList(invalidGroup));
        final String columnName = String.format(NscsToSEResponseMapper.INVALID_ITEMS_HEADER, invalidTargetGroupException.getItemType());
        final NodeReference node1 = new NodeRef("node1");
        final NodeException nodeException = new NodeException(Arrays.asList(node1, new NodeRef("node2")), invalidTargetGroupException);

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());
        mapper.createErrorCommandResponse(responseDto, nodeException);

        final List<String> rowsAsListOfStrings = getRowsAsListOfConcatenatedStrings(responseDto.getElements());

        assert (rowsAsListOfStrings.get(0).contains(NscsToSEResponseMapper.NODE_COLUMN_HEADER));
        assert (rowsAsListOfStrings.get(1).contains(invalidGroup));
        assert (rowsAsListOfStrings.get(2).contains("node2"));
    }

    @Test
    public void multiErrorNodeExceptionTest() {
        final MultiErrorNodeException multiErrorNodeException = new MultiErrorNodeException();

        final InvalidNodeNameException invalidNodeNameException = new InvalidNodeNameException();
        final NodeNotSynchronizedException notSynchronizedException = new NodeNotSynchronizedException();
        final NodeReference node1 = new NodeRef("node1");
        final NodeReference node2 = new NodeRef("node2");
        multiErrorNodeException.addException(node1, invalidNodeNameException);
        multiErrorNodeException.addException(node2, notSynchronizedException);

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());
        mapper.createErrorCommandResponse(responseDto, multiErrorNodeException);

        final List<AbstractDto> elements = responseDto.getElements();
        final List<String> rowsAsConcatenatedStrings = getRowsAsListOfConcatenatedStrings(elements);

        assert (rowsAsConcatenatedStrings.get(0).contains(NscsToSEResponseMapper.NODE_COLUMN_HEADER));
        assert (rowsAsConcatenatedStrings.get(1).contains(NscsErrorCodes.THE_NETWORK_ELEMENT_SPECIFIED_DOES_NOT_EXIST));
        assert (rowsAsConcatenatedStrings.get(2).contains(NscsErrorCodes.PLEASE_ENSURE_THE_NODE_SPECIFIED_IS_SYNCHRONIZED));
    }

    @Test
    public void genericErrorTest() {

        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());
        mapper.createErrorCommandResponse(responseDto);

        assertTrue(((LineDto) responseDto.getElements().get(0)).getValue().contains("Unexpected Internal Error"));
        assertTrue(((LineDto) responseDto.getElements().get(1)).getValue()
                .contains("This is an unexpected system error, please check the error log for more details."));
    }

    @Test
    public void messageCommandResponseTest() {
        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());
        final NscsMessageCommandResponse nscsMessageCommandResponse = new NscsMessageCommandResponse("this is the first message",
                "this is the second message");
        mapper.convertoToCommandResponseDto(responseDto, nscsMessageCommandResponse, "this is the command name");
        final int expectedResponseElements = 2;
        assert (expectedResponseElements == responseDto.getElements().size());
        assert ("this is the first message".equals(((LineDto) responseDto.getElements().get(0)).getValue()));
        assert ("this is the second message".equals(((LineDto) responseDto.getElements().get(1)).getValue()));
    }

    @Test
    public void messageCommandResponse_WithNoMessagesTest() {
        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());
        final NscsMessageCommandResponse nscsMessageCommandResponse = new NscsMessageCommandResponse();
        mapper.convertoToCommandResponseDto(responseDto, nscsMessageCommandResponse, "this is the command name");
        final int expectedResponseElements = 0;
        assert (expectedResponseElements == responseDto.getElements().size());
    }

    @Test
    public void downloadRequestnResponseTest() {
        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());
        final NscsDownloadRequestMessageCommandResponse nscsDownloadRequestMessageCommandResponse = new NscsDownloadRequestMessageCommandResponse(1,
                "this is the file identifier", "this is the message");
        mapper.convertoToCommandResponseDto(responseDto, nscsDownloadRequestMessageCommandResponse, "this is the command name");
        final int expectedResponseElements = 2;
        assert (expectedResponseElements == responseDto.getElements().size());
        assert ("this is the file identifier".equals(((FileDownloadRequestDto) responseDto.getElements().get(0)).getFileId()));
        assert ("secadm".equals(((FileDownloadRequestDto) responseDto.getElements().get(0)).getApplicationId()));
        assert ("this is the message".equals(((LineDto) responseDto.getElements().get(1)).getValue()));
    }

    @Test
    public void confirmationResponseTest() {
        final ResponseDto responseDto = new ResponseDto(new ArrayList<AbstractDto>());
        final NscsConfirmationCommandResponse nscsConfirmationCommandResponse = new NscsConfirmationCommandResponse(
                "this is the confirmation response");
        nscsConfirmationCommandResponse.setAdditionalConfirmationMessages("this is the first additional confirmation message",
                "this is the second additional confirmation message");
        mapper.convertoToCommandResponseDto(responseDto, nscsConfirmationCommandResponse, "this is the command name");
        final int expectedResponseElements = 4;
        assert (expectedResponseElements == responseDto.getElements().size());
        assert ("this is the confirmation response".equals(((LineDto) responseDto.getElements().get(0)).getValue()));
        assert ("this is the first additional confirmation message".equals(((LineDto) responseDto.getElements().get(1)).getValue()));
        assert ("this is the second additional confirmation message".equals(((LineDto) responseDto.getElements().get(2)).getValue()));
        final ConfirmationDto confirmationDto = (ConfirmationDto) responseDto.getElements().get(3);
        assert ("this is the confirmation response".equals(confirmationDto.getConfirmationMessage()));
        assert ("this is the command name".equals(confirmationDto.getCommand()));
    }

    private List<String> getRowsAsListOfConcatenatedStrings(final List<AbstractDto> elements) {
        final List<String> rowsAsConcatenatedStrings = new ArrayList<>();

        for (final AbstractDto dto : elements) {

            if (dto instanceof RowDto) {
                final RowDto rowDto = (RowDto) dto;
                final StringBuilder row = new StringBuilder();
                for (final RowCell cell : rowDto.getElements()) {
                    row.append(cell.getValue() + " ");
                }
                rowsAsConcatenatedStrings.add(row.toString());
            } else {
                final LineDto lineDto = (LineDto) dto;
                rowsAsConcatenatedStrings.add(lineDto.getValue());
            }

        }
        return rowsAsConcatenatedStrings;
    }
}
