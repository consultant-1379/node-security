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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Auxiliary class modeling serializable MO action parameters as passed through
 * workflow tasks.
 * 
 * This class is needed since the MoParams class is not serializable.
 * 
 * Example usages:
 * <h4>Simple</h4>
 * 
 * <pre>
 * {
 * 	&#64;code
 * 	final WorkflowMoParams params = new WorkflowMoParams();
 * 	params.addParam("password", password, true);
 * 	params.addParam("remoteHost", remoteHost);
 * 	params.addParam("userID", userID);
 * }
 * </pre>
 * 
 * <h4>Complex with lists:</h4>
 * 
 * <pre>
 * {
 * 	&#64;code
 * 	final List<WorkflowMoParams> certSpecList = new ArrayList<WorkflowMoParams>();
 * 	for (final CertSpec c : certSpecs) {
 * 		final CPPCertSpec cmCertSpec = new CPPCertSpec(c, category);
 * 		certSpecList.add(cmCertSpec.toMoParams());
 * 	}
 * 	final WorkflowMoParams params = new WorkflowMoParams();
 * 	params.addParam("certSpecList", certSpecList);
 * 	params.addParam("startTime", startTime);
 * }
 * </pre>
 * 
 * @author emaborz
 *
 */
public class WorkflowMoParams extends WorkflowMoParam {

	private static final long serialVersionUID = 8565567494166978263L;

	/**
	 * Constructs a WorkflowMoParams object
	 */
	public WorkflowMoParams() {
		super(new HashMap<String, WorkflowMoParam>());
		this.p = WorkFlowParamType.MAP;
	}

	/**
	 * Adds a String value to the WorkflowMoParams with the specified key.
	 * 
	 * @param key
	 * @param value
	 * @param isSecret
	 *            should be set true if the String is a "secret". In this case
	 *            the value wont be logged.
	 */
	public void addParam(final String key, final String value, final boolean isSecret) {
		if (isSecret) {
			this.getParamMap().put(key, new WorkflowMoParamSecret(value));
		} else {
			this.getParamMap().put(key, new WorkflowMoParam(value));
		}
	}

	/**
	 * Adds a String value to the WorkflowMoParams with the specified key.
	 * 
	 * isSecret will be set "false" which means the parameter value might be
	 * logged.
	 * 
	 * @param key
	 * @param value
	 */
	public void addParam(final String key, final String value) {
		addParam(key, value, false);
	}

	/**
	 * Adds a char[] value to the WorkflowMoParams with the specified key.
	 * 
	 * @param key
	 * @param value
	 * @param isSecret
	 *            should be set true if the value is a "secret". In this case it
	 *            is set to true, value wont be logged.
	 */
	public void addParam(final String key, final char[] value, final boolean isSecret) {
		if (isSecret) {
			this.getParamMap().put(key, new WorkflowMoParamSecret(value));
		} else {
			this.getParamMap().put(key, new WorkflowMoParam(value));
		}
	}

	/**
	 * Adds a String value to the WorkflowMoParams with the specified key.
	 * 
	 * isSecret will be set "false" which means the parameter value might be
	 * logged.
	 * 
	 * @param key
	 * @param value
	 */
	public void addParam(final String key, final char[] value) {
		addParam(key, value, false);
	}

	/**
	 * Adds a byte[] value to the WorkflowMoParams with the specified key.
	 * 
	 * @param key
	 * @param value
	 * @param isSecret
	 *            should be set true if the value is a "secret". In this case it
	 *            is set to true, value wont be logged.
	 */
	public void addParam(final String key, final byte[] value, final boolean isSecret) {
		if (isSecret) {
			this.getParamMap().put(key, new WorkflowMoParamSecret(value));
		} else {
			this.getParamMap().put(key, new WorkflowMoParam(value));
		}
	}

	/**
	 * Adds a byte[] value to the WorkflowMoParams with the specified key.
	 * 
	 * isSecret will be set "false" which means the parameter value might be
	 * logged.
	 * 
	 * @param key
	 * @param value
	 */
	public void addParam(final String key, final byte[] value) {
		addParam(key, value, false);
	}

	/**
	 * Adds an int value to the WorkflowMoParams with the specified key.
	 * 
	 * @param key
	 * @param value
	 */
	public void addParam(final String key, final int value) {
		this.getParamMap().put(key, new WorkflowMoParam(value));
	}

	/**
	 * Adds a LIST value to the WorkflowMoParams with the specified key.
	 * 
	 * @param key
	 * @param value
	 */
	public void addParam(final String key, final List<WorkflowMoParams> value) {
		this.getParamMap().put(key, new WorkflowMoParam(value));
	}

	/**
	 * Adds a WorkflowMoParam value to the WorkflowMoParams with the specified
	 * key.
	 * 
	 * @param key
	 * @param value
	 */
	public void addParam(final String key, final WorkflowMoParams value) {
		this.getParamMap().put(key, value);
	}

	/**
	 * Gets the map representing the WorkflowMoParams.
	 * 
	 * @return
	 */
	public Map<String, WorkflowMoParam> getParamMap() {
		return WorkflowMoParam.getParamMap(this);
	}
}
