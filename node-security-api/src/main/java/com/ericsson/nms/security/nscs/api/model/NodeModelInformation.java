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
package com.ericsson.nms.security.nscs.api.model;

import java.io.Serializable;

public class NodeModelInformation implements Serializable {

	private static final long serialVersionUID = 3675398934837455854L;
	
	private String modelIdentifier = null;
	private ModelIdentifierType modelIdentifierType = null;
	private String nodeType = null;

	/**
	 * @param modelIdentifier the modelIdentifier
	 * @param modelIdentifierType the modelIdentifierType
	 * @param nodeType the nodeType
	 */
	public NodeModelInformation(String modelIdentifier,
			ModelIdentifierType modelIdentifierType, String nodeType) {
		this.modelIdentifier = modelIdentifier;
		this.modelIdentifierType = modelIdentifierType;
//		this.nodeType = nodeType.toUpperCase();
		this.nodeType = nodeType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("modelId=");
		builder.append(this.modelIdentifier);
		builder.append(" modelIdType=");
		builder.append(this.modelIdentifierType == null ? this.modelIdentifierType : this.modelIdentifierType.toString());
		builder.append(" nodeType=");
		builder.append(this.nodeType);
		return builder.toString();
	}

	/**
	 * @return the modelIdentifier
	 */
	public String getModelIdentifier() {
		return modelIdentifier;
	}

	/**
	 * @param modelIdentifier the modelIdentifier to set
	 */
	public void setModelIdentifier(String modelIdentifier) {
		this.modelIdentifier = modelIdentifier;
	}

	/**
	 * @return the modelIdentifierType
	 */
	public ModelIdentifierType getModelIdentifierType() {
		return modelIdentifierType;
	}

	/**
	 * @param modelIdentifierType the modelIdentifierType to set
	 */
	public void setModelIdentifierType(ModelIdentifierType modelIdentifierType) {
		this.modelIdentifierType = modelIdentifierType;
	}

	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public static enum ModelIdentifierType {
		MIM_VERSION,
		PRODUCT_NUMBER,
		OSS_IDENTIFIER,
		UNKNOWN
	}

}
