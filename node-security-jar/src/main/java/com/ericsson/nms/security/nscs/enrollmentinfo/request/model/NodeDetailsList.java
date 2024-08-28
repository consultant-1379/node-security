/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.enrollmentinfo.request.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * <p>
 * Nodes is root element in XML holding list of Node(s).
 * 
 * <p>
 * The following schema fragment specifies the XSD Schema of this class.
 * 
 * <pre>
 *   &lt;complexType name="Nodes">
 *    &lt;sequence>
 *        &lt;element name='Node' type='NodeType' minOccurs='0' maxOccurs='unbounded' />
 *    &lt;/sequence>
 *   &lt;/complexType>
 * </pre>
 *
 * @author tcsmave
 * 
 */
@XmlRootElement(name = "Nodes")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Nodes")
public class NodeDetailsList implements Serializable {

    private static final long serialVersionUID = -4910069712449317995L;

    @XmlElement(name = "Node", required = true)
    private List<NodeDetails> nodeDetailsList;

    public List<NodeDetails> getList() {
        return nodeDetailsList;
    }

    public void setList(final List<NodeDetails> nodeDetailsList) {
        this.nodeDetailsList = nodeDetailsList;
    }

}
