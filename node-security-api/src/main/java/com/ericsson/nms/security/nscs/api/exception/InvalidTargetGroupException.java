package com.ericsson.nms.security.nscs.api.exception;

import java.util.List;

/**
 * <p>Exception thrown when one or more target group name in the command is
 * invalid or does not exists.</p>
 * Created by emaynes on 13/05/2014.
 */
public class InvalidTargetGroupException extends NscsInvalidItemsException {


    private static final long serialVersionUID = -6494395586000156485L;

    public InvalidTargetGroupException(final List<String> targetGrupos) {
        super(NscsErrorCodes.INVALID_TARGET_GROUP_ERROR);
        setItemType(NscsErrorCodes.TARGET_GROUP);
        setItemsList(targetGrupos);
        setSuggestedSolution(NscsErrorCodes.PROVIDE_EXISTING_TARGET_GROUPS_ONLY);
    }

    /**
     * @return ErrorType.INVALID_TARGET_GROUP_ERROR
     */
    @Override
    public ErrorType getErrorType() {
        return ErrorType.INVALID_TARGET_GROUP_ERROR;
    }
}
