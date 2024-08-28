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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute;

import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class CbpOiCheckCreateTrustCategoriesTaskTest {

    @Test
    public void testCbpOiCheckCreateTrustCategoriesTask_Default() {
        final CbpOiCheckCreateTrustCategoriesTask task = new CbpOiCheckCreateTrustCategoriesTask();
        assertNotNull("Null task", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CBPOI_CHECK_CREATE_TRUST_CATEGORIES, task.getTaskType());
        assertEquals("Unexpected short description", CbpOiCheckCreateTrustCategoriesTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void testCbpOiCheckCreateTrustCategoriesTask_Fdn() {
        final String fdn = "cloud257-vdu";
        final CbpOiCheckCreateTrustCategoriesTask task = new CbpOiCheckCreateTrustCategoriesTask(fdn);
        assertNotNull("Null task", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CBPOI_CHECK_CREATE_TRUST_CATEGORIES, task.getTaskType());
        assertEquals("Unexpected short description", CbpOiCheckCreateTrustCategoriesTask.SHORT_DESCRIPTION, task.getShortDescription());
        assertEquals("Unexpected fdn", fdn, task.getNodeFdn());
    }

    @Test
    public void testGetOutputParams() {
        final CbpOiCheckCreateTrustCategoriesTask task = new CbpOiCheckCreateTrustCategoriesTask();
        assertNotNull("Null task", task);
        final Map<String, Serializable> paramMap = new HashMap<>();
        paramMap.put("KEY", "PARAM");
        task.setOutputParams(paramMap);
        assertEquals("Wrong Output Params", paramMap, task.getOutputParams());
    }

}
