/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute;

import java.util.Map;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Work flow task is used to build required data to set mo attributes on the node.
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.SET_MO_ATTRIBUTES
 * </p>
 *
 * @author xchimvi
 */
public class SetMoAttributesTask extends WorkflowQueryTask {

    private static final long serialVersionUID = 1277048956469711256L;

    public static String MO_ATTRIBUTES_KEY_VALUES = WorkflowParameterKeys.MO_ATTRIBUTES_KEY_VALUES.toString();

    public static final String SHORT_DESCRIPTION = "Set MO";

    public SetMoAttributesTask() {
        super(WorkflowTaskType.SET_MO_ATTRIBUTES);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public SetMoAttributesTask(final String fdn) {
        super(WorkflowTaskType.SET_MO_ATTRIBUTES, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return the map MoAttributes
     */
    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Object>> getMoAttributes() {
        return (Map<String, Map<String, Object>>) getValue(MO_ATTRIBUTES_KEY_VALUES);
    }

    /**
     * @param moAttributeKeyValues
     *            the moAttributesKeyValues to set
     */
    public void setMoAttributes(final Map<String, Map<String, Object>> moAttributeKeyValues) {
        setValue(MO_ATTRIBUTES_KEY_VALUES, moAttributeKeyValues);
    }

}
