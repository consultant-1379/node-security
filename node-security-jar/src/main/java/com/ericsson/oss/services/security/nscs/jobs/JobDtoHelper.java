/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;

/**
 * Auxiliary class to manage conversions between REST DTO and Internal NSCS format of job.
 */
public final class JobDtoHelper {

    private JobDtoHelper() {
    }

    /**
     * Converts a given string containing a comma-separated list of stringified UUIDs (REST DTO format) to a list of UUIDs (NSCS internal format).
     * 
     * @param ids
     *            the string containing a comma-separated list of stringified UUIDs.
     * @return the list of UUIDs.
     * @throws NscsBadRequestException
     *             if conversion fails.
     */
    public static List<UUID> fromUuidListDto(final String ids) {
        if (ids == null || ids.isEmpty()) {
            final String errorMessage = "Null or empty REST DTO list of UUIDs";
            throw new NscsBadRequestException(errorMessage);
        }
        final List<String> idsList = Arrays.asList(ids.split(","));
        final List<UUID> uuids = new ArrayList<>();
        try {
            for (final String s : idsList) {
                uuids.add(fromUuidDto(s));
            }
        } catch (final NscsBadRequestException e) {
            final String errorMessage = String.format("Wrong UUID list [%s] in REST DTO", ids);
            throw new NscsBadRequestException(errorMessage, e, NscsErrorCodes.PLEASE_PROVIDE_COMMA_SEPARATED_UUID_LIST);
        }
        return uuids;
    }

    /**
     * Converts a given stringified UUID (REST DTO format) to a UUID (NSCS internal format).
     * 
     * @param id
     *            the stringified UUID.
     * @return the UUID.
     * @throws NscsBadRequestException
     *             if conversion fails.
     */
    public static UUID fromUuidDto(final String id) {
        if (id == null || id.isEmpty()) {
            final String errorMessage = "Null or empty UUID REST DTO";
            throw new NscsBadRequestException(errorMessage);
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (final IllegalArgumentException e) {
            final String errorMessage = String.format("Wrong UUID [%s] in REST DTO", id);
            throw new NscsBadRequestException(errorMessage, e, NscsErrorCodes.PLEASE_PROVIDE_VALID_UUID);
        }
        return uuid;
    }
}
