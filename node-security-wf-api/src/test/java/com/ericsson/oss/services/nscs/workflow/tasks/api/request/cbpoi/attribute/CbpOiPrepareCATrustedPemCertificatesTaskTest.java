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

public class CbpOiPrepareCATrustedPemCertificatesTaskTest {

    @Test
    public void testCbpOiPrepareCATrustedPemCertificatesTask_Default() {
        final CbpOiPrepareCATrustedPemCertificatesTask task = new CbpOiPrepareCATrustedPemCertificatesTask();
        assertNotNull("Null task", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CBPOI_PREPARE_CA_TRUSTED_PEM_CERTIFICATES, task.getTaskType());
        assertEquals("Unexpected short description", CbpOiPrepareCATrustedPemCertificatesTask.SHORT_DESCRIPTION, task.getShortDescription());
    }

    @Test
    public void testCbpOiPrepareCATrustedPemCertificatesTask_Fdn() {
        final String fdn = "cloud257-vdu";
        final CbpOiPrepareCATrustedPemCertificatesTask task = new CbpOiPrepareCATrustedPemCertificatesTask(fdn);
        assertNotNull("Null task", task);
        assertEquals("Unexpected Task type", WorkflowTaskType.CBPOI_PREPARE_CA_TRUSTED_PEM_CERTIFICATES, task.getTaskType());
        assertEquals("Unexpected short description", CbpOiPrepareCATrustedPemCertificatesTask.SHORT_DESCRIPTION, task.getShortDescription());
        assertEquals("Unexpected fdn", fdn, task.getNodeFdn());
    }

    @Test
    public void testGetOutputParams() {
        final CbpOiPrepareCATrustedPemCertificatesTask task = new CbpOiPrepareCATrustedPemCertificatesTask();
        assertNotNull("Null task", task);
        final Map<String, Serializable> paramMap = new HashMap<>();
        paramMap.put("KEY", "PARAM");
        task.setOutputParams(paramMap);
        assertEquals("Wrong Output Params", paramMap, task.getOutputParams());
    }

    @Test
    public void testGetTrustedCertificateAuthority() {
        final CbpOiPrepareCATrustedPemCertificatesTask task = new CbpOiPrepareCATrustedPemCertificatesTask();
        assertNotNull("Null task", task);
        final String caName = "NE_OAM_CA";
        task.setTrustedCertificateAuthority(caName);
        assertEquals("Wrong Trusted Certificate Authority", caName, task.getTrustedCertificateAuthority());
    }

    @Test
    public void testGetEntityProfileName() {
        final CbpOiPrepareCATrustedPemCertificatesTask task = new CbpOiPrepareCATrustedPemCertificatesTask();
        assertNotNull("Null task", task);
        final String epName = "DUSGen2OAM_CHAIN_EP";
        task.setEntityProfileName(epName);
        assertEquals("Wrong Entity Profile Name", epName, task.getEntityProfileName());
    }

    @Test
    public void testGetIsReissue() {
        final CbpOiPrepareCATrustedPemCertificatesTask task = new CbpOiPrepareCATrustedPemCertificatesTask();
        assertNotNull("Null task", task);
        final String isReissue = "true";
        task.setIsReissue(isReissue);
        assertEquals("Wrong Reissue flag", isReissue, task.getIsReissue());
    }

}
