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

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParam;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParamSecret;

/**
 * Internal class to handle MoParam parameters used to invoke MO Actions
 * 
 * The parameters can be constructed using <b>MoParams</b> class.
 * 
 * @author egbobcs
 *
 */
public class MoParam {

	enum ParamType {
		MAP, // Holder of parameters for example Map<String, Object)>
		LIST, // List of parameters for example List<String>
		SIMPLE// Simple parameter for example String or char[]
	}

	protected ParamType p;

	// All the parameters are stored as Object. The constructors and the public
	// methods make sure that this is used
	// in a type safe manner
	protected Object o;

	MoParam(final String s) {
		this.o = s;
		this.p = ParamType.SIMPLE;
	}

	MoParam(final char[] c) {
		this.o = c;
		this.p = ParamType.SIMPLE;
	}

	MoParam(final int i) {
		this.o = i;
		this.p = ParamType.SIMPLE;
        }

	    MoParam(final String[] l) {
	        this.o = l;
	        this.p = ParamType.SIMPLE;
	}

	MoParam(final byte[] b) {
		this.o = b;
		this.p = ParamType.SIMPLE;
	}

	MoParam(final Map<String, MoParam> m) {
		this.o = m;
		this.p = ParamType.MAP;
	}

	MoParam(final List<MoParams> l) {
		this.o = l;
		this.p = ParamType.LIST;
	}

	MoParam(final ArrayList<MoParam> l) {
	        this.o = l;
	        this.p = ParamType.LIST;
	}

	MoParam(final WorkflowMoParam workflowMoParam) {
		if (workflowMoParam.isSimple()) {
			this.o = workflowMoParam.getParam();
			this.p = ParamType.SIMPLE;
		} else if (workflowMoParam.isList()) {
			List<MoParam> paramList = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<WorkflowMoParam> workflowParamList = (List<WorkflowMoParam>) workflowMoParam.getParam();
			for (final WorkflowMoParam workflowParam : workflowParamList) {
				// Recursive call for each items in the LIST
				MoParam param = null;
				if (workflowParam instanceof WorkflowMoParamSecret) {
					param = new MoParamSecret((WorkflowMoParamSecret) workflowParam);
				} else {
					param = new MoParam(workflowParam);
				}
				paramList.add(param);
			}
			this.o = paramList;
			this.p = ParamType.LIST;
		} else if (workflowMoParam.isMap()) {
			Map<String, MoParam> paramMap = new HashMap<>();
			@SuppressWarnings("unchecked")
			Map<String, WorkflowMoParam> workflowParamMap = (Map<String, WorkflowMoParam>) workflowMoParam.getParam();
			for (final String key : workflowParamMap.keySet()) {
				// Recursive call for each items in the MAP
				WorkflowMoParam workflowParam = workflowParamMap.get(key);
				MoParam param = null;
				if (workflowParam instanceof WorkflowMoParamSecret) {
					param = new MoParamSecret((WorkflowMoParamSecret) workflowParam);
				} else {
					param = new MoParam(workflowParam);
				}
				paramMap.put(key, param);
			}
			this.o = paramMap;
			this.p = ParamType.MAP;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public Object getParam() {
		return o;
	}

	public ParamType getParamType() {
		return this.p;
	}

	public boolean isMap() {
		return ParamType.MAP.equals(this.p);
	}

	public boolean isList() {
		return ParamType.LIST.equals(this.p);
	}

	public boolean isSimple() {
		return ParamType.SIMPLE.equals(this.p);
	}

	@Override
	public String toString() {
		String value = "";
		switch (this.p) {
		case SIMPLE:
			value = String.valueOf(o);
			break;
		case MAP:
			value = getParamMap(this).entrySet()
					.stream()
					.map(e -> e.getKey()+":"+e.getValue())
					.collect(joining(","));
			break;
		case LIST:
			value = Arrays.toString((getList(this).toArray()));
			break;
		default:
			return "NotDefined";
		}
		return "{" + value + "}";
	}

	@SuppressWarnings("unchecked")
	static List<MoParam> getList(final MoParam param) {
		if (ParamType.LIST.equals(param.getParamType())) {
			// CAST is type safe because constructor won't allow anything else
			return (List<MoParam>) param.getParam();
		} else {
			throw new IllegalArgumentException();
		}
	}

	@SuppressWarnings("unchecked")
	static Map<String, MoParam> getParamMap(final MoParam param) {
		if (ParamType.MAP.equals(param.getParamType())) {
			// CAST is type safe because constructor won't allow anything else
			return (Map<String, MoParam>) param.getParam();
		} else {
			throw new IllegalArgumentException();
		}
	}

}
