/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task;

/**
 * This enum class have the result of WFTask
 * 
 * @author emehsau
 * 
 */
public enum WFTaskResult {
	TRUE("true"), FALSE("false"), TIMEOUT("timeout"), SUCCESS("success"), FAILURE("failure");

	private String taskResult;

	private WFTaskResult(final String name) {
		this.taskResult = name;
	}

	/**
	 * Method to get task result from WFTaskResult
	 * 
	 * @return {@link String} the taskResult
	 */
	public String getTaskResult() {
		return taskResult;
	}

	/**
	 * To get the WFTaskResult result from name
	 * 
	 * @param name
	 *            : name of WFTaskResult
	 * @return {@link WFTaskResult}
	 */
	public static WFTaskResult getWFTaskResult(final String name) {
		WFTaskResult result = null;
		if (name != null) {
			for (final WFTaskResult taskResultObj : WFTaskResult.values()) {
				if (name.equalsIgnoreCase(taskResultObj.taskResult)) {
					result = taskResultObj;
				}
			}
		}
		return result;
	}

}
