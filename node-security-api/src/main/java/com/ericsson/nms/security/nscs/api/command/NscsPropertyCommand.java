package com.ericsson.nms.security.nscs.api.command;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * It is basis for any command in the Nscs component. It consists of a NscsCommandType and all properties needed to perform the actual command
 * execution.
 * </p>
 * <p>
 * Ideally this class should be specialized in order to add convenience methods for needed parameters.
 * </p>
 * 
 * @see com.ericsson.nms.security.nscs.api.command.types.CppSecurityLevelCommand
 * @see com.ericsson.nms.security.nscs.api.command.types.TargetGroupsCommand
 * 
 * @author emaynes on 01/05/2014.
 */
public class NscsPropertyCommand implements NscsCommand {

    private static final long serialVersionUID = -8755949165090394109L;

    public static final String COMMAND_TYPE_PROPERTY = "command";

    private NscsCommandType commandType;
    private Map<String, Object> properties = new HashMap<>();

    public enum NscsPropertyCommandInvoker {
    	CLI, API;
    }

    private NscsPropertyCommandInvoker commandInvokerValue = NscsPropertyCommandInvoker.CLI;

    /**
     * @return the commandInvoker
     */
    public NscsPropertyCommandInvoker getCommandInvokerValue() {
    	return commandInvokerValue;
    }

    /**
     * @param commandInvokerValue the commandInvoker to set
     */
    public void setCommandInvokerValue(NscsPropertyCommandInvoker commandInvokerValue) {
    	this.commandInvokerValue = commandInvokerValue;
    }

    /**
     * @return returns the command type to be executed by Nscs component
     */
    public NscsCommandType getCommandType() {
        return commandType;
    }

    /**
     * Sets the command type to be executed by Nscs component
     * @param commandType the commandInvoker to set
     */
    public void setCommandType(final NscsCommandType commandType) {
        this.commandType = commandType;
    }

    /**
     * @return a Map containing properties needed by the command handler in order to perform it's actions
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Sets a Map containing properties needed by the command handler in order to perform it's actions
     * @param properties Map containing properties needed by the command handler in order to perform it's actions
     */
    public void setProperties(final Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Convenience method to facilitate access to a property value by subclasses
     * 
     * @param property
     *            - name of the property
     * @return the Object associated with the property name or null
     */
    protected Object getValue(final String property) {
        return getProperties().get(property);
    }

    /**
     * Convenience method to facilitate update of a property value by subclasses
     * @param property property name to be included or updated
     * @param value new value of the property
     */
    protected void setValue(final String property, final Object value) {
        getProperties().put(property, value);
    }

    /**
     * Convenience method to facilitate access to a property value by subclasses
     * 
     * @param property
     *            - name of the property
     * @return a String representation of the value or null if there is no property for the provided name
     */
    public String getValueString(final String property) {
        final Object value = getValue(property);
        return value == null ? null : value.toString();
    }

    /**
     * Convenience method to facilitate access to a property value by subclasses
     *
     * @param property property name to be included or updated
     * @param value new value of the property. If value is not null, value.toString() will
     *              be called before insertion into the property Map.
     */
    protected void setValueString(final String property, final Object value) {
        setValue(property, value == null ? null : value.toString());
    }

    /**
     * Convenience method to check if the given property exists in the property Map
     * @param property name of the property
     * @return true if Properties Map contains a property with the given name
     */
    protected boolean hasProperty(final String property) {
        return this.properties.containsKey(property);
    }

    @Override
    public String toString() {
        return String.format("type = %s, properties = %s", commandType, properties);
    }

    public boolean isContinueSupported() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
