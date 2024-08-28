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
package com.ericsson.nms.security.nscs.workflow.task.data.moaction;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Internal class to handle serializable MO action parameters as passed through
 * workflow tasks.
 * 
 * The parameters can be constructed using <b>WorkflowMoParams</b> class.
 * 
 * This class is needed since the MoParam class is not serializable.
 * 
 * @author emaborz
 *
 */
public class WorkflowMoParam implements Serializable {

	private static final long serialVersionUID = -2406556849321215528L;

	enum WorkFlowParamType {
		MAP, // Holder of parameters for example Map<String, Object)>
		LIST, // List of parameters for example List<String>
		SIMPLE // Simple parameter for example String or char[]
	}

	protected WorkFlowParamType p;

	// All the parameters are stored as Object. The constructors and the public
	// methods make sure that this is used
	// in a type safe manner
	protected Object o;

	WorkflowMoParam(final String s) {
		this.o = s;
		this.p = WorkFlowParamType.SIMPLE;
	}

	WorkflowMoParam(final char[] c) {
		this.o = c;
		this.p = WorkFlowParamType.SIMPLE;
	}

	WorkflowMoParam(final int i) {
		this.o = i;
		this.p = WorkFlowParamType.SIMPLE;
	}

	WorkflowMoParam(final byte[] b) {
		this.o = b;
		this.p = WorkFlowParamType.SIMPLE;
	}

	WorkflowMoParam(final Map<String, WorkflowMoParam> m) {
		this.o = m;
		this.p = WorkFlowParamType.MAP;
	}

	WorkflowMoParam(final List<WorkflowMoParams> l) {
		this.o = l;
		this.p = WorkFlowParamType.LIST;
	}

	public Object getParam() {
		return o;
	}

	public WorkFlowParamType getParamType() {
		return this.p;
	}

	public boolean isMap() {
		return WorkFlowParamType.MAP.equals(this.p);
	}

	public boolean isList() {
		return WorkFlowParamType.LIST.equals(this.p);
	}

	public boolean isSimple() {
		return WorkFlowParamType.SIMPLE.equals(this.p);
	}

	@SuppressWarnings("unchecked")
	static List<WorkflowMoParam> getList(final WorkflowMoParam param) {
		if (WorkFlowParamType.LIST.equals(param.getParamType())) {
			// CAST is type safe because constructor won't allow anything else
			return (List<WorkflowMoParam>) param.getParam();
		} else {
			throw new IllegalArgumentException();
		}
	}

	@SuppressWarnings("unchecked")
	static Map<String, WorkflowMoParam> getParamMap(final WorkflowMoParam param) {
		if (WorkFlowParamType.MAP.equals(param.getParamType())) {
			// CAST is type safe because constructor won't allow anything else
			return (Map<String, WorkflowMoParam>) param.getParam();
		} else {
			throw new IllegalArgumentException();
		}
	}
}
