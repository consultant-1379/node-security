package com.ericsson.nms.security.nscs.workflow.task.data.moaction;

/**
 * Auxiliary class modeling a serializable 'secret' MO action parameter (e.g. a
 * password) as passed through workflow tasks.
 * 
 * This class is needed since the MoParamSecret class is not serializable.
 * 
 * @author emaborz
 */
public class WorkflowMoParamSecret extends WorkflowMoParam {

	private static final long serialVersionUID = -6668602207722565617L;

	WorkflowMoParamSecret(final char[] c) {
		super(c);
		this.p = WorkFlowParamType.SIMPLE;
	}

	WorkflowMoParamSecret(final String s) {
		super(s);
		this.p = WorkFlowParamType.SIMPLE;
	}

	WorkflowMoParamSecret(final byte[] b) {
		super(b);
		this.p = WorkFlowParamType.SIMPLE;
	}

}
