package com.ericsson.nms.security.nscs.api.exception;

import java.util.List;

/**
 * Exception thrown when a target group update fails for
 * one or more nodes
 * Created by emaynes on 13/05/2014.
 */
public class TargetGroupsUpdateException extends NscsInvalidItemsException {

    private static final long serialVersionUID = -6542869147526674460L;

    {{
        setItemType(NscsErrorCodes.NODE);
    }}

    public TargetGroupsUpdateException() {
        super(NscsErrorCodes.UPDATE_FAILED);
    }

    public TargetGroupsUpdateException(final String message) {
        super(message);
    }

    public TargetGroupsUpdateException(final String message, final List<String> invalidNodesList) {
        super(message);
        setItemsList(invalidNodesList);
    }

    public TargetGroupsUpdateException(final List<String> invalidNodesList) {
        super(NscsErrorCodes.UPDATE_FAILED);
        setItemsList(invalidNodesList);
    }

    /**
     *
     * @return ErrorType.TARGET_GROUP_UPDATE_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.TARGET_GROUP_UPDATE_ERROR;
    }
}
