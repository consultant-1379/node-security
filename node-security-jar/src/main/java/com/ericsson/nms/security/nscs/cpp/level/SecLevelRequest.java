package com.ericsson.nms.security.nscs.cpp.level;

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.certificate.issue.input.xml.Nodes.Node;

/**
 * <p>Security level change request.</p>
 * <p>Encapsulate information need by a SecLevelProcessor implementation</p>
 * Created by emaynes on 04/05/2014.
 */
public class SecLevelRequest {

    private SecurityLevel requiredSecurityLevel;
    private SecurityLevel currentSecurityLevel;
    private SecLevelRequestType secLevelRequestType;
    private String nodeName;
    private String nodeFDN;
    private Node xmlRepresntationOfNode;
    
    private String enrollmentMode;
    

    /**
     * Gets target security level of the node
     * @return requested new security level
     */
    public SecurityLevel getRequiredSecurityLevel() {
        return requiredSecurityLevel;
    }

    /**
     * Sets target security level of the node
     * @param requiredSecurityLevel target CPPSecurityLevel
     */
    public void setRequiredSecurityLevel(final SecurityLevel requiredSecurityLevel) {
        this.requiredSecurityLevel = requiredSecurityLevel;
    }

    /**
     * Gets the current security level of the node
     * @return CPPSecurityLevel representing node's current security level
     */
    public SecurityLevel getCurrentSecurityLevel() {
        return currentSecurityLevel;
    }

    /**
     * Sets the current security level of the node
     * @param currentSecurityLevel currentSecurityLevel
     */
    public void setCurrentSecurityLevel(final SecurityLevel currentSecurityLevel) {
        this.currentSecurityLevel = currentSecurityLevel;
    }

    /**
     * Gets SecLevelRequestType that initiated this SecLevelRequest change
     * @return SecLevelRequestType
     */
    public SecLevelRequestType getSecLevelRequestType() {
        return secLevelRequestType;
    }

    /**
     * Sets SecLevelRequestType that initiated this SecLevelRequest change
     * @param secLevelRequestType instance of SecLevelRequestType indicating
     *                            security level change to be made
     */
    public void setSecLevelRequestType(final SecLevelRequestType secLevelRequestType) {
        this.secLevelRequestType = secLevelRequestType;
    }

    /**
     * Gets the target node simple name
     * @return String of node's simple name. E.g.: node1
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * Sets the target node simple name
     * @param nodeName target node simple name
     */
    public void setNodeName(final String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * Gets target node FDN
     * @return target node FDN
     */
    public String getNodeFDN() {
        return nodeFDN;
    }

    /**
     * Sets target node FDN
     * @param nodeFDN String with the node FDN
     */
    public void setNodeFDN(final String nodeFDN) {
        this.nodeFDN = nodeFDN;
    }
    
    //dheeraj code
    
    /**
	 * @return the xmlRepresntationOfNode
	 */
	public Node getXmlRepresntationOfNode() {
		return xmlRepresntationOfNode;
	}

	/**
	 * @param xmlRepresntationOfNode the xmlRepresntationOfNode to set
	 */
	public void setXmlRepresntationOfNode(Node xmlRepresntationOfNode) {
		this.xmlRepresntationOfNode = xmlRepresntationOfNode;
	}
	

	/**
	 * @return the enrollmentMode
	 */
	public String getEnrollmentMode() {
		return enrollmentMode;
	}

	/**
	 * @param enrollmentMode the enrollmentMode to set
	 */
	public void setEnrollmentMode(String enrollmentMode) {
		this.enrollmentMode = enrollmentMode;
	}

	@Override
    public String toString() {
        return "SecLevelRequest [requiredSecurityLevel=" + requiredSecurityLevel + ", currentSecurityLevel=" + currentSecurityLevel
                + ", secLevelRequestType=" + secLevelRequestType + ", nodeName=" + nodeName + ", nodeFDN=" + nodeFDN + "]";
    }

}
