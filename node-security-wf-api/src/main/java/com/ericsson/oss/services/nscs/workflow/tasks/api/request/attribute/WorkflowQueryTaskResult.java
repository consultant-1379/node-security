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
package com.ericsson.oss.services.nscs.workflow.tasks.api.request.attribute;

import java.io.Serializable;
import java.util.Map;

/**
 * Auxiliary class modeling the result of a WorkflowQueryTask
 * 
 */
public class WorkflowQueryTaskResult implements Serializable {

    private static final long serialVersionUID = -4330692547990540161L;

    private String result;
    private Map<String, Serializable> outputParams;

    /**
     * @param result the result
     * @param outputParams the outputParams
     */
    public WorkflowQueryTaskResult(String result, Map<String, Serializable> outputParams) {
        this.result = result;
        this.outputParams = outputParams;
    }

    public WorkflowQueryTaskResult(Map<String, Serializable> outputParams) {
        this.outputParams = outputParams;
    }

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result
     *            the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * @return the outputParams
     */
    public Map<String, Serializable> getOutputParams() {
        return outputParams;
    }

    /**
     * @param outputParams
     *            the outputParams to set
     */
    public void setOutputParams(Map<String, Serializable> outputParams) {
        this.outputParams = outputParams;
    }
    //
    //	/**
    //	 * Serialize the object encoding it.
    //	 * 
    //	 * @return the encoded serialized object.
    //	 * @throws IOException
    //	 *             if serialization fails.
    //	 */
    //	public String writeObject() throws IOException {
    //		String encoded = null;
    //		try {
    //			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    //			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
    //					byteArrayOutputStream);
    //			objectOutputStream.writeObject(this);
    //			objectOutputStream.close();
    //			encoded = new String(Base64.encode(byteArrayOutputStream
    //					.toByteArray()));
    //		} catch (IOException e) {
    //			throw e;
    //		}
    //		return encoded;
    //	}
    //
    //	/**
    //	 * Deserialize the encoded object.
    //	 * 
    //	 * @return the object.
    //	 */
    //	public static WorkflowQueryTaskResult readObject(final String encoded) {
    //		WorkflowQueryTaskResult object = null;
    //		byte[] bytes = null;
    //		try {
    //			bytes = Base64.decode(encoded);
    //		} catch (Exception e) {
    //			// Do nothing
    //		}
    //
    //		if (bytes != null) {
    //			try {
    //				ObjectInputStream objectInputStream = new ObjectInputStream(
    //						new ByteArrayInputStream(bytes));
    //				object = (WorkflowQueryTaskResult) objectInputStream
    //						.readObject();
    //			} catch (IOException e) {
    //				// Do nothing
    //			} catch (ClassNotFoundException e) {
    //				// Do nothing
    //			} catch (ClassCastException e) {
    //				// Do nothing
    //			}
    //		}
    //		return object;
    //	}
}
