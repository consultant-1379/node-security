package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys;

/**
 * <p>
 * Workflow task representing a request to clean M2M user and SMRS files and directory after an install trusted certificates..
 * </p>
 * <p>
 * Default WorkflowTaskType used is WorkflowTaskType.CPP_CLEANUP_M2M_USER_AND_SMRS
 * </p>
 *
 * @author emaborz
 */
public class CleanupM2MUserAndSmrsTask extends WorkflowActionTask {

    private static final long serialVersionUID = -5214992566679334602L;

    /**
     * Key of the trusted category in the map
     */
    public static final String TRUSTED_CATEGORY_KEY = WorkflowParameterKeys.TRUSTED_CATEGORY.toString();

    public static final String SHORT_DESCRIPTION = "Cleanup M2M-SMRS account";

    public CleanupM2MUserAndSmrsTask() {
        super(WorkflowTaskType.CPP_CLEANUP_M2M_USER_AND_SMRS);
        setShortDescription(SHORT_DESCRIPTION);
    }

    public CleanupM2MUserAndSmrsTask(final String fdn) {
        super(WorkflowTaskType.CPP_CLEANUP_M2M_USER_AND_SMRS, fdn);
        setShortDescription(SHORT_DESCRIPTION);
    }

    /**
     * @return the Trusted Category
     */
    public String getTrustedCategory() {
        return (String) getValue(TRUSTED_CATEGORY_KEY);
    }

    /**
     * @param trustedCategory
     *            the Trusted Category
     */
    public void setTrustedCategory(final String trustedCategory) {
        setValue(TRUSTED_CATEGORY_KEY, trustedCategory);
    }

}
