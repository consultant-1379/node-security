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
package com.ericsson.nms.security.nscs.data.moaction.param;

import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParamSecret;

/**
 * Internal class to represent "secret" MoParam.
 * 
 * The toString will NOT print the value to the logs.
 * 
 * @author egbobcs
 *
 */
class MoParamSecret extends MoParam {
	MoParamSecret(final char[] c) {
		super(c);
		this.p = ParamType.SIMPLE;
	}

	MoParamSecret(final String s) {
		super(s);
		this.p = ParamType.SIMPLE;
	}

	MoParamSecret(final byte[] b) {
		super(b);
		this.p = ParamType.SIMPLE;
	}

	MoParamSecret(final WorkflowMoParamSecret workflowMoParam) {
		super(workflowMoParam);
	}

	/**
	 * Returns <code>"{*****}"</code> instead of the actual value.
	 */
	@Override
	public String toString() {
		return "{*****}";
	}
}