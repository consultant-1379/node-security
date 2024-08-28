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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParam;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParamSecret;
import com.ericsson.nms.security.nscs.workflow.task.data.moaction.WorkflowMoParams;

/**
 * Holder class for MO action parameters.
 * 
 * Example usages:
 * <h4>Simple</h4>
 * 
 * <pre>
 * {
 * 	&#64;code
 * 	final MoParams params = new MoParams();
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
 * 	final List<MoParams> certSpecList = new ArrayList<MoParams>();
 * 	for (final CertSpec c : certSpecs) {
 * 		final CPPCertSpec cmCertSpec = new CPPCertSpec(c, category);
 * 		certSpecList.add(cmCertSpec.toMoParams());
 * 	}
 * 	final MoParams params = new MoParams();
 * 	params.addParam("certSpecList", certSpecList);
 * 	params.addParam("startTime", startTime);
 * }
 * </pre>
 * 
 * @author egbobcs
 *
 */
public class MoParams extends MoParam {

	/**
	 * Constructs an MoParams object
	 */
	public MoParams() {
		super(new HashMap<String, MoParam>());
		this.p = ParamType.MAP;
	}

	public MoParams(final WorkflowMoParams workflowMoParams) {
		super(new HashMap<String, MoParam>());
		final Map<String, WorkflowMoParam> workflowParamMap = workflowMoParams.getParamMap();
		for (Map.Entry<String, WorkflowMoParam> entry : workflowParamMap.entrySet()) {
			final String key = entry.getKey();
			WorkflowMoParam workflowMoParam = entry.getValue();
			if (workflowMoParam instanceof WorkflowMoParamSecret) {
				this.getParamMap().put(key, new MoParamSecret((WorkflowMoParamSecret)workflowMoParam));
			} else {
				this.getParamMap().put(key, new MoParam(workflowMoParam));
			}
		}
	}

	/**
	 * Adds a String value to the MoParams with the specified key.
	 * 
	 * @param key
	 * @param value
	 * @param isSecret
	 *            should be set true if the String is a "secret". In this case
	 *            the value wont be logged.
	 */
	public void addParam(final String key, final String value, final boolean isSecret) {
		if (isSecret) {
			this.getParamMap().put(key, new MoParamSecret(value));
		} else {
			this.getParamMap().put(key, new MoParam(value));
		}
	}

	/**
	 * Adds a String value to the MoParams with the specified key.
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
	 * Adds a char[] value to the MoParams with the specified key.
	 * 
	 * @param key
	 * @param value
	 * @param isSecret
	 *            should be set true if the value is a "secret". In this case it
	 *            is set to true, value wont be logged.
	 */
	public void addParam(final String key, final char[] value, final boolean isSecret) {
		if (isSecret) {
			this.getParamMap().put(key, new MoParamSecret(value));
		} else {
			this.getParamMap().put(key, new MoParam(value));
		}
	}

	/**
	 * Adds a String value to the MoParams with the specified key.
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
	 * Adds a byte[] value to the MoParams with the specified key.
	 * 
	 * @param key
	 * @param value
	 * @param isSecret
	 *            should be set true if the value is a "secret". In this case it
	 *            is set to true, value wont be logged.
	 */
	public void addParam(final String key, final byte[] value, final boolean isSecret) {
		if (isSecret) {
			this.getParamMap().put(key, new MoParamSecret(value));
		} else {
			this.getParamMap().put(key, new MoParam(value));
		}
	}

	/**
	 * Adds a byte[] value to the MoParams with the specified key.
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
	 * Adds an int value to the MoParams with the specified key.
	 * 
	 * @param key
	 * @param value
	 */
	public void addParam(final String key, final int value) {
		this.getParamMap().put(key, new MoParam(value));
	}

	/**
	 * Adds a LIST value to the MoParams with the specified key.
	 * 
	 * @param key
	 * @param value
	 */
	public void addParam(final String key, final List<MoParams> value) {
		this.getParamMap().put(key, new MoParam(value));
	}

    /**
     * Adds a LIST of String value to the MoParams with the specified key.
     * 
     * @param key
     *            contains name of MO action parameter
     * @param list
     *            contains MO action parameter
     */
    public void addListParam(final String key, final List<String> list) {
        final ArrayList<MoParam> paramsList = new ArrayList<>();
        for (final String entry : list) {
            paramsList.add(new MoParam(entry));
        }
        this.getParamMap().put(key, new MoParam(paramsList));
    }

    /**
     * Adds a String array value to the MoParams with the specified key.
     * 
     * @param key
     *            contains name of MO action parameter
     * @param value
     *            contains MO action parameter
     */
    public void addParam(final String key, final String[] value) {
        this.getParamMap().put(key, new MoParam(value));
    }

	/**
	 * Adds a MoParam value to the MoParams with the specified key.
	 * 
	 * @param key
	 * @param value
	 */
	public void addParam(final String key, final MoParams value) {
		this.getParamMap().put(key, value);
	}

	/**
	 * Gets the map representing the MoParams.
	 * 
	 * @return
	 */
	public Map<String, MoParam> getParamMap() {
		return MoParam.getParamMap(this);
	}
}
