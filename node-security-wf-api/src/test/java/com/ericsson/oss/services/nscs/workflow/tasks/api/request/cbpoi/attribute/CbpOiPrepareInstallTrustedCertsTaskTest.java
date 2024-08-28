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

public class CbpOiPrepareInstallTrustedCertsTaskTest {

    @Test
    public void testCbpOiPrepareInstallTrustedCertsTask_Default() {
        final CbpOiPrepareInstallTrustedCertsTask task = new CbpOiPrepareInstallTrustedCertsTask();
        assertNotNull("Null task", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CBPOI_PREPARE_INSTALL_TRUSTED_CERTS, task.getTaskType());
        assertEquals("Unexpected short description", CbpOiPrepareInstallTrustedCertsTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void testCbpOiPrepareInstallTrustedCertsTask_Fdn() {
        final String fdn = "cloud257-vdu";
        final CbpOiPrepareInstallTrustedCertsTask task = new CbpOiPrepareInstallTrustedCertsTask(fdn);
        assertNotNull("Null task", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CBPOI_PREPARE_INSTALL_TRUSTED_CERTS, task.getTaskType());
        assertEquals("Unexpected short description", CbpOiPrepareInstallTrustedCertsTask.SHORT_DESCRIPTION, task.getShortDescription());
        assertEquals("Unexpected fdn", fdn, task.getNodeFdn());
    }

    @Test
    public void testGetOutputParams() {
        final CbpOiPrepareInstallTrustedCertsTask task = new CbpOiPrepareInstallTrustedCertsTask();
        assertNotNull("Null task", task);
        final Map<String, Serializable> paramMap = new HashMap<>();
        paramMap.put("KEY", "PARAM");
        task.setOutputParams(paramMap);
        assertEquals("Wrong Output Params", paramMap, task.getOutputParams());
    }

}
