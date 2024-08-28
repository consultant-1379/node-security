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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.handler.command.utility.NscsCapabilityDetails.NscsCapabilityValueDetails;

public class CapabilityGetResponseBuilder extends NscsNameMultipleValueResponseBuilder {

    private static final String CAPABILITY_MODEL_NAME_ID = "Model";
    private static final String CAPABILITY_NAME_ID = "Capability";
    private static final String CAPABILITY_DEFAULT_VALUE_ID = "Default";
    private static final String NODE_TYPE_ID = "Node Type";
    private static final String OSS_MODEL_IDENTITIES_ID = "OSS Model Identities";
    private static final String CAPABILITY_VALUE_ID = "Value";
    private static final String CAPABILITY_NOTES_ID = "Notes";
    public static final String CAPABILITY_NULL = "null";
    public static final String CAPABILITY_CONSISTENCY_CHECK_MISMATCH = "[MISMATCH] %s";
    public static final String CAPABILITY_CONSISTENCY_CHECK_ERROR = "[ERROR] %s";

    Logger logger = LoggerFactory.getLogger(getClass());

    private static Map<String, Integer> CapabilityRow = new HashMap<String, Integer>();

    static {
        CapabilityRow.put(CAPABILITY_NAME_ID, 0);
        CapabilityRow.put(CAPABILITY_DEFAULT_VALUE_ID, 1);
        CapabilityRow.put(NODE_TYPE_ID, 2);
        CapabilityRow.put(OSS_MODEL_IDENTITIES_ID, 3);
        CapabilityRow.put(CAPABILITY_VALUE_ID, 4);
        CapabilityRow.put(CAPABILITY_NOTES_ID, 5);
    }

    public static final int CAPABILITY_ROW_SIZE = CapabilityRow.size();

    public CapabilityGetResponseBuilder() {
        super(CAPABILITY_ROW_SIZE);
    }

    /**
     * Add the header row to the name multiple values response.
     *
     */
    public void addHeader() {
        add(CAPABILITY_MODEL_NAME_ID, formatHeader());
    }

    /**
     * Format the header row of the multiple values section of response.
     *
     * @return the formatted header row
     */
    public String[] formatHeader() {
        return formatHeader(CapabilityRow);
    }

    /**
     * Add rows for all capabilities of all security capability models to the name multiple values response.
     *
     * @param allCapabilities
     */
    public void addAllCapabilitiesRows(final List<NscsCapabilityDetails> allCapabilities) {
        for (final NscsCapabilityDetails capability : allCapabilities) {
            addCapabilityRows(capability);
        }
    }

    /**
     * Add rows for the given capability to the name multiple values response.
     *
     * @param capability
     */
    private void addCapabilityRows(final NscsCapabilityDetails capability) {
        addCapabilityHeaderRow(capability.getCapabilityModelName(), capability.getCapabilityName(), capability.getDefaultValue(),
                capability.getNotes());
        for (final String nodeType : capability.getAllValues().keySet()) {
            addNodeCapabilityRows(nodeType, capability.getAllValues().get(nodeType));
        }
    }

    /**
     * Add a header row for the given capability.
     *
     * @param capabilityModelName
     * @param capabilityName
     * @param defaultValue
     * @param notes
     */
    private void addCapabilityHeaderRow(final String capabilityModelName, final String capabilityName, final Object defaultValue,
            final String notes) {
        add(capabilityModelName, formatRow(CapabilityRow, formatCapabilityHeaderRow(capabilityName, defaultValue, notes)));
    }

    /**
     * Add rows for the given capability values for the given node type.
     *
     * @param capabilityModelName
     * @param capabilityName
     * @param defaultValue
     * @param nodeType
     * @param capabilityValues
     */
    private void addNodeCapabilityRows(final String nodeType, final Map<Object, NscsCapabilityValueDetails> capabilityValues) {
        int count = 0;
        for (final Object value : capabilityValues.keySet()) {
            if (count == 0) {
                add(EMPTY_STRING, formatRow(CapabilityRow,
                        formatCapabilityNodeRow(nodeType, value, capabilityValues.get(value).getoMIs(), capabilityValues.get(value).getNotes())));
            } else {
                add(EMPTY_STRING, formatRow(CapabilityRow,
                        formatCapabilityRow(value, capabilityValues.get(value).getoMIs(), capabilityValues.get(value).getNotes())));
            }
            count++;
        }
    }

    /**
     * Format a capability header row for the multiple values section for the given values.
     *
     * @param capabilityName
     * @param defaultValue
     * @param notes
     * @return
     */
    private Map<String, String> formatCapabilityHeaderRow(final String capabilityName, final Object defaultValue, final String notes) {
        final Map<String, String> row = new HashMap<String, String>();
        row.put(CAPABILITY_NAME_ID, capabilityName);
        row.put(CAPABILITY_DEFAULT_VALUE_ID, (defaultValue != null) ? defaultValue.toString() : CAPABILITY_NULL);
        row.put(NODE_TYPE_ID, EMPTY_STRING);
        row.put(CAPABILITY_NOTES_ID, notes);
        return row;
    }

    /**
     * Format a capability value node row for the multiple values section for the given values.
     *
     * @param nodeType
     * @param capabilityValue
     * @param oMIs
     * @param notes
     * @return
     */
    private Map<String, String> formatCapabilityNodeRow(final String nodeType, final Object capabilityValue, final Set<String> oMIs,
            final String notes) {
        final Map<String, String> row = new HashMap<String, String>();
        row.put(NODE_TYPE_ID, nodeType);
        row.put(OSS_MODEL_IDENTITIES_ID, oMIs.toString());
        row.put(CAPABILITY_VALUE_ID, (capabilityValue != null) ? capabilityValue.toString() : CAPABILITY_NULL);
        row.put(CAPABILITY_NOTES_ID, notes);
        return row;
    }

    /**
     * Format a capability value row for the multiple values section for the given values.
     *
     * @param capabilityValue
     * @param oMIs
     * @return
     */
    private Map<String, String> formatCapabilityRow(final Object capabilityValue, final Set<String> oMIs, final String notes) {
        final Map<String, String> row = new HashMap<String, String>();
        row.put(OSS_MODEL_IDENTITIES_ID, oMIs.toString());
        row.put(CAPABILITY_VALUE_ID, (capabilityValue != null) ? capabilityValue.toString() : CAPABILITY_NULL);
        row.put(CAPABILITY_NOTES_ID, notes);
        return row;
    }
}
