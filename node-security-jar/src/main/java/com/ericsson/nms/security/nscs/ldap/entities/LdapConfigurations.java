/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ldap.entities;

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
 * 
 * 
 */
@XmlRootElement(name = "Nodes")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Nodes")
public class LdapConfigurations implements Serializable {

    private static final long serialVersionUID = 7398529893474224249L;

    @XmlElement(name = "Node", required = true)
    private List<NodeSpecificLdapConfiguration> ldapConfigurationList;

    public List<NodeSpecificLdapConfiguration> getList() {
        return ldapConfigurationList;
    }

    public void setConfigurations(List<NodeSpecificLdapConfiguration> ldapConfigurationList) {
        this.ldapConfigurationList = ldapConfigurationList;
    }

}
