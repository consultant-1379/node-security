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
package com.ericsson.nms.security.nscs.handler.command.utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NscsCapabilityDetails {
    private String capabilityModelName;
    private String capabilityName;
    private Object defaultValue;
    private String notes;
    /**
     * Map each node type to the capability values map (map each capability value to the set of OSS model identities and notes)
     */
    private Map<String, Map<Object, NscsCapabilityValueDetails>> allValues;

    /**
     * @param capabilityModelName
     * @param capabilityName
     * @param defaultValue
     */
    public NscsCapabilityDetails(final String capabilityModelName, final String capabilityName, final Object defaultValue) {
        super();
        this.capabilityModelName = capabilityModelName;
        this.capabilityName = capabilityName;
        this.defaultValue = defaultValue;
        this.notes = null;
        this.allValues = new HashMap<>();
    }

    /**
     * @return the capabilityModelName
     */
    public String getCapabilityModelName() {
        return capabilityModelName;
    }

    /**
     * @param capabilityModelName
     *            the capabilityModelName to set
     */
    public void setCapabilityModelName(final String capabilityModelName) {
        this.capabilityModelName = capabilityModelName;
    }

    /**
     * @return the capabilityName
     */
    public String getCapabilityName() {
        return capabilityName;
    }

    /**
     * @param capabilityName
     *            the capabilityName to set
     */
    public void setCapabilityName(final String capabilityName) {
        this.capabilityName = capabilityName;
    }

    /**
     * @return the defaultValue
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue
     *            the defaultValue to set
     */
    public void setDefaultValue(final Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes
     *            the notes to set
     */
    public void setNotes(final String notes) {
        this.notes = notes;
    }

    /**
     * @return the values
     */
    public Map<String, Map<Object, NscsCapabilityValueDetails>> getAllValues() {
        return allValues;
    }

    /**
     * Add a given value for the given node type and OSS model identity and notes.
     * 
     * If the node type is null no value is added.
     * 
     * The OSS model identity and notes can be null.
     * 
     * @param value
     *            the capability value.
     * @param nodeType
     *            the node type.
     * @param oMI
     *            the OSS model identity.
     * @param notes
     *            the notes.
     */
    public void addValue(final Object value, final String nodeType, final String oMI, final String notes) {
        if (nodeType != null) {
            if (this.allValues.containsKey(nodeType)) {
                final Map<Object, NscsCapabilityValueDetails> values = this.allValues.get(nodeType);
                if (values.containsKey(value)) {
                    values.get(value).getoMIs().add(oMI);
                    values.get(value).setNotes(notes);
                } else {
                    final Set<String> oMIs = new HashSet<>();
                    oMIs.add(oMI);
                    values.put(value, new NscsCapabilityValueDetails(oMIs, notes));
                }
            } else {
                final Map<Object, NscsCapabilityValueDetails> values = new HashMap<>();
                final Set<String> oMIs = new HashSet<String>();
                oMIs.add(oMI);
                values.put(value, new NscsCapabilityValueDetails(oMIs, notes));
                this.allValues.put(nodeType, values);
            }
        }
    }

    public class NscsCapabilityValueDetails {
        private Set<String> oMIs;
        private String notes;

        /**
         * @param oMIs
         * @param notes
         */
        public NscsCapabilityValueDetails(final Set<String> oMIs, final String notes) {
            super();
            this.oMIs = oMIs;
            this.notes = notes;
        }

        /**
         * @return the oMIs
         */
        public Set<String> getoMIs() {
            return oMIs;
        }

        /**
         * @param oMIs
         *            the oMIs to set
         */
        public void setoMIs(final Set<String> oMIs) {
            this.oMIs = oMIs;
        }

        /**
         * @return the notes
         */
        public String getNotes() {
            return notes;
        }

        /**
         * @param notes
         *            the notes to set
         */
        public void setNotes(final String notes) {
            this.notes = notes;
        }
    }
}
