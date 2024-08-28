/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.api.iscf;

import java.io.Serializable;

/**
 *
 * @author enmadmin
 */
public class SubjectAltNameEdiPartyType extends BaseSubjectAltNameDataType implements Serializable {

    protected String nameAssigner;
    protected String partyName;

    /**
     * Gets the value of the nameAssigner property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getNameAssigner() {
        return nameAssigner;
    }

    /**
     * Sets the value of the nameAssigner property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setNameAssigner(final String value) {
        this.nameAssigner = value;
    }

    /**
     * Gets the value of the partyName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getPartyName() {
        return partyName;
    }

    /**
     * Sets the value of the partyName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setPartyName(final String value) {
        this.partyName = value;
    }

    @Override
    public String toString() {
        return " [ nameAssigner: " + nameAssigner + " partyName: " + partyName + " ] ";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SubjectAltNameEdiPartyType other = (SubjectAltNameEdiPartyType) obj;
        if (nameAssigner == null) {
            if (other.nameAssigner != null) {
                return false;
            }
        } else if (!nameAssigner.equals(other.nameAssigner)) {
            return false;
        }
        if (partyName == null) {
            if (other.partyName != null) {
                return false;
            }
        } else if (!partyName.equals(other.partyName)) {
            return false;
        }
        return true;
    }
    
}
