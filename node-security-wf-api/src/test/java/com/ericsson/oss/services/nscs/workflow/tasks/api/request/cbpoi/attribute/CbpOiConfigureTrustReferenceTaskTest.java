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
import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CbpOiConfigureTrustReferenceTaskTest {

    @Test
    public void testCbpOiConfigureTrustReferenceTask_Default() {
        final CbpOiConfigureTrustReferenceTask task = new CbpOiConfigureTrustReferenceTask();
        assertNotNull("Null task", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CBPOI_CONFIGURE_SERVICES_TRUST_REFERENCE, task.getTaskType());
        assertEquals("Unexpected short description", CbpOiConfigureTrustReferenceTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void testCbpOiConfigureTrustReferenceTask_Fdn() {
        final String fdn = "cloud257-vdu";
        final CbpOiConfigureTrustReferenceTask task = new CbpOiConfigureTrustReferenceTask(fdn);
        assertNotNull("Null task", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CBPOI_CONFIGURE_SERVICES_TRUST_REFERENCE, task.getTaskType());
        assertEquals("Unexpected short description", CbpOiConfigureTrustReferenceTask.SHORT_DESCRIPTION, task.getShortDescription());
        assertEquals("Unexpected fdn", fdn, task.getNodeFdn());
    }

    @Test
    public void testGetOutputParams() {
        final CbpOiConfigureTrustReferenceTask task = new CbpOiConfigureTrustReferenceTask();
        assertNotNull("Null task", task);
        final Map<String, Serializable> paramMap = new HashMap<>();
        paramMap.put("KEY", "PARAM");
        task.setOutputParams(paramMap);
        assertEquals("Wrong Output Params", paramMap, task.getOutputParams());
    }
}
