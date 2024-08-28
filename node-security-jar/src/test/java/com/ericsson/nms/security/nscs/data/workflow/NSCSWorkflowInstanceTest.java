/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.data.workflow;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.nscs.workflow.NSCSWorkflowInstance;
import com.ericsson.oss.services.nscs.workflow.NSCSWorkflowInstance.WorkflowInstanceStatus;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowCategory;
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowNames;

public class NSCSWorkflowInstanceTest {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getSimpleName());
	
	private final static String WORKFLOW_EXECUTIONID = "33332222111";
	private final static String WORKFLOW_INSTANCENAME = WorkflowNames.WORKFLOW_COMECIM_ComIssueTrustCert.getWorkflowName();
	private final static WorkflowCategory EXPECTED_WORKFLOW_CATEGORY = WorkflowNames.WORKFLOW_COMECIM_ComIssueTrustCert.getCategory(); 
	private final static String WORKFLOW_BUSINESSKEY = "Business_key";
	private final static WorkflowInstanceStatus EXPECTED_WORKFLOW_STATE = WorkflowInstanceStatus.RUNNING;

	@Test
	public void test_NSCSWorkflowInstance_ConstructorWithParameter() {
		log.info("test_NSCSWorkflowInstance_ConstructorWithParameter");
		NSCSWorkflowInstance nscswi = new NSCSWorkflowInstance(WORKFLOW_EXECUTIONID, WORKFLOW_INSTANCENAME, WORKFLOW_BUSINESSKEY);
		assertNotNull("NSCSWorkflowInstance is null!", nscswi);
	}
	
	@Test
	public void test_NSCSWorkflowInstance_ConstructorWithParameterCheckValues() {
		log.info("test_NSCSWorkflowInstance_ConstructorWithParameterCheckValues");
		NSCSWorkflowInstance nscswi = new NSCSWorkflowInstance(WORKFLOW_EXECUTIONID, WORKFLOW_INSTANCENAME, WORKFLOW_BUSINESSKEY);
		assertNotNull("NSCSWorkflowInstance is null!", nscswi);
		assertEquals("Execution ID doesnt match", WORKFLOW_EXECUTIONID, nscswi.getExecutionId());
		assertEquals("Instance name doesnt match", WORKFLOW_INSTANCENAME, nscswi.getWorkflowInstanceName());
		assertEquals("Businesskey doesnt match", WORKFLOW_BUSINESSKEY, nscswi.getBusinessKey());
		assertEquals("Category doesnt match", EXPECTED_WORKFLOW_CATEGORY, nscswi.getCategory());
		assertEquals("State doesnt match", EXPECTED_WORKFLOW_STATE, nscswi.getState());
		assertNotNull("Start time Date is null", nscswi.getStartTime());
		assertNull("End time Date must be null", nscswi.getEndTime());
	}

	@Test
	public void test_NSCSWorkflowInstance_setEndTime() {
		log.info("test_NSCSWorkflowInstance_setEndTime");
		NSCSWorkflowInstance nscswi = new NSCSWorkflowInstance(WORKFLOW_EXECUTIONID, WORKFLOW_INSTANCENAME, WORKFLOW_BUSINESSKEY);
		assertNotNull("NSCSWorkflowInstance is null!", nscswi);
		final Date endTime = new Date(System.currentTimeMillis());
		nscswi.setEndTime(endTime);
		assertEquals("End Time doesnt match", endTime, nscswi.getEndTime());
	}
	

}
