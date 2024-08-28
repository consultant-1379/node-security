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

package com.ericsson.nms.security.nscs.cpp.ipsec.wf;
import com.ericsson.nms.security.nscs.cpp.ipsec.input.xml.Nodes.Node;



/**
 * Request object which will hold the details of operation, target etc.
 * 
 * @author emehsau
 * 
 */
public class IpSecRequest {


	private String nodeFdn;

	private boolean isForceUpdate;

	private Node xmlRepresntationOfNode;
	
	private IpSecRequestType ipSecRequestType;
	
	/**
	 * Method to get ipSecRequestType
	 * 
	 * @return the ipSecRequestType
	 */
	public IpSecRequestType getIpSecRequestType() {
		return ipSecRequestType;
	}

	/**
	 * Method to set ipSecRequestType
	 * 
	 * @param ipSecRequestType : the ipSecRequestType
	 */
	public void setIpSecRequestType(final IpSecRequestType ipSecRequestType) {
		this.ipSecRequestType = ipSecRequestType;
	}

	/**
	 * Method to get isForceUpdate
	 * 
	 * @return the isForceUpdate
	 */
	public boolean isForceUpdate() {
		return isForceUpdate;
	}

	/**
	 * 
	 * Method to set isForceUpdate
	 * 
	 * @param isForceUpdate
	 *            : the isForceUpdate to set
	 * 
	 */
	public void setForceUpdate(final boolean isForceUpdate) {
		this.isForceUpdate = isForceUpdate;
	}

	/**

	 * @return the nodeFdn
	 */
	public String getNodeFdn() {
		return nodeFdn;
	}

	/**
	 * @param nodeFdn
	 *            the nodeFdn to set
	 */
	public void setNodeFdn(final String nodeFdn) {
		this.nodeFdn = nodeFdn;
	}

	/**
	 * @return the xmlRepresntationOfNode
	 */
	public Node getXmlRepresntationOfNode() {
		return xmlRepresntationOfNode;
	}

	/**
	 * @param xmlRepresntationOfNode
	 *            the xmlRepresntationOfNode to set
	 */
	public void setXmlRepresntationOfNode(final Node xmlRepresntationOfNode) {
		this.xmlRepresntationOfNode = xmlRepresntationOfNode;
	}	
}
