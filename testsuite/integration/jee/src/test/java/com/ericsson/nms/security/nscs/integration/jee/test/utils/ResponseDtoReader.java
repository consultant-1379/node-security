/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.utils;

import com.ericsson.oss.services.scriptengine.spi.dtos.*;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ResponseDtoReader {

    private Logger logger;

    private List<String> getRowsAsListOfConcatenatedStrings(List<AbstractDto> elements) {
        List<String> rowsAsConcatenatedStrings = new ArrayList<>();

        for (AbstractDto dto : elements) {
            if (dto instanceof RowDto) {
                RowDto rowDto = (RowDto) dto;
                StringBuilder row = new StringBuilder();
                for (RowCell cell : rowDto.getElements()) {
                    row.append(cell.getValue() + " ");
                }
                //logger.info("ResponseDtoReader Row Elements: "+row.toString());
                rowsAsConcatenatedStrings.add(row.toString());
            } else {
                LineDto lineDto = (LineDto) dto;
                String value = lineDto.getValue();
                //logger.info("ResponseDtoReader Line Elements: "+value);
                rowsAsConcatenatedStrings.add(value);
            }
        }
        return rowsAsConcatenatedStrings;
    }

    public List<String> extractListOfRowsFromCommandResponseDto(CommandResponseDto commandResponseDto) {

        ResponseDto responseDto = commandResponseDto.getResponseDto();
        List<String> rowsInResponse = getRowsAsListOfConcatenatedStrings(responseDto.getElements());

        return rowsInResponse;
    }

    public boolean messageIsContainedInList(String message, List<String> list) {
        if (list.isEmpty()) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            if ((list.get(i) != null) && (list.get(i).contains(message))) {
                return true;
            }
        }
        return false;
    }
}
