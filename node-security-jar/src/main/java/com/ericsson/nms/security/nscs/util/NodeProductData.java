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
package com.ericsson.nms.security.nscs.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.nms.security.nscs.api.exception.InvalidNodeProductDataException;

public class NodeProductData {

	private static final Pattern NODE_PRODUCT_DATA_PATTERN = Pattern.compile(String.format("(CXP\\d+\\/\\d+)-(R\\w+)"), Pattern.CASE_INSENSITIVE);

	private String nodeProductData = null;
	private String identity = null;
	private String revision = null;

	/**
	 * @param nodeProductData
	 */
	public NodeProductData(String nodeProductData)
			throws InvalidNodeProductDataException {
		this.nodeProductData = nodeProductData;
		if (nodeProductData != null && !nodeProductData.isEmpty()) {
			final Matcher matcher = NODE_PRODUCT_DATA_PATTERN.matcher(nodeProductData);
			if (matcher.find()) {
				this.identity = matcher.group(1);
				this.revision = matcher.group(2);
			} else {
				String errorMsg = String.format("Invalid nodeProductData[%s]", nodeProductData);
				throw new InvalidNodeProductDataException(errorMsg);
			}
		} else {
			String errorMsg = String.format("Null or empty nodeProductData[%s]", nodeProductData);
			throw new InvalidNodeProductDataException(errorMsg);
		}
	}

	/**
	 * @return the nodeProductData
	 */
	public String getNodeProductData() {
		return nodeProductData;
	}

	/**
	 * @param nodeProductData the productNumber to set
	 */
	public void setNodeProductData(String nodeProductData) {
		this.nodeProductData = nodeProductData;
	}

	/**
	 * @return the identity
	 */
	public String getIdentity() {
		return identity;
	}

	/**
	 * @param identity the identity to set
	 */
	public void setIdentity(String identity) {
		this.identity = identity;
	}

	/**
	 * @return the revision
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * @param revision the revision to set
	 */
	public void setRevision(String revision) {
		this.revision = revision;
	}

	/**
	 * Check whether NodeProductData is valid or not.
	 * 
	 * @return
	 */
	public boolean isValid() {
		if (this.identity == null || this.revision == null)
			return false;
		return true;
	}

}
