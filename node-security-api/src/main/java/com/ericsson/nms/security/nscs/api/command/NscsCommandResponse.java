package com.ericsson.nms.security.nscs.api.command;

import java.io.Serializable;

/**
 * Abstract class representing the result of a command execution.
 * <P>
 * Ideally there will be a subclass of this class for each data-structure that needs to be returned as a result of a command execution.
 * </P>
 * 
 * @see com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse
 * @see com.ericsson.nms.security.nscs.api.command.NscsNameValueCommandResponse
 * 
 * @author emaynes on 01/05/2014.
 */
public abstract class NscsCommandResponse implements Serializable {

    private static final long serialVersionUID = -4509415053579546437L;

    /**
     * @return the response type represented by this instance
     */
    public abstract NscsCommandResponseType getResponseType();

    /**
     * Convenience method to create a NscsMessageCommandResponse
     * 
     * @param message
     *            String with the message to be return to client
     * @return NscsMessageCommandResponse with the provided message
     */
    public static NscsMessageCommandResponse message(final String message) {
        return new NscsMessageCommandResponse(message);
    }

    /**
     * Convenience method to create a NscsMessageCommandResponse
     * 
     * @param messages
     *            Array of String with the messages to be returned to client
     * @return NscsMessageCommandResponse with the provided messages
     */
    public static NscsMessageCommandResponse messages(final String... messages) {
        return new NscsMessageCommandResponse(messages);
    }

    /**
     * Convenience method to create a NscsNameValueCommandResponse
     * 
     * @return NscsNameValueCommandResponse
     */
    public static NscsNameValueCommandResponse nameValue() {
        return new NscsNameValueCommandResponse();
    }

    /**
     * Convenience method to create a NscsNameMultipleValueCommandResponse
     *
     * @param numberOfCoulmns
     *            -the numberOfCoulmns
     * @return NscsNameMultipleValueCommandResponse
     */
    public static NscsNameMultipleValueCommandResponse nameMultipleValue(final int numberOfCoulmns) {
        return new NscsNameMultipleValueCommandResponse(numberOfCoulmns);
    }

    /**
     * Auxiliary method to check if this NscsCommandResponse is of NscsNameValueCommandResponse type
     * 
     * @return true - if getResponseType() == NscsCommandResponseType.NAME_VALUE
     */
    public boolean isNameValueResponseType() {
        return NscsCommandResponseType.NAME_VALUE.equals(getResponseType());
    }

    /**
     * Auxiliary method to check if this NscsCommandResponse is of NscsNameMultipleValueCommandResponse type
     *
     * @return true - if getResponseType() == NscsCommandResponseType.NAME_MULTIPLE_VALUE
     */
    public boolean isNameMultipleValueResponseType() {
        return NscsCommandResponseType.NAME_MULTIPLE_VALUE.equals(getResponseType());
    }

    /**
     * Auxiliary method to check if this NscsCommandResponse is of NscsMessageCommandResponse type
     * 
     * @return true - if getResponseType() == NscsCommandResponseType.MESSAGE
     */
    public boolean isMessageResponseType() {
        return NscsCommandResponseType.MESSAGE.equals(getResponseType());
    }

    /**
     * Auxiliary method to check if this NscsCommandResponse is of NscsDownloadRequestMessageCommandResponse type
     * 
     * @return true - if getResponseType() == NscsCommandResponseType.DOWNLOAD_REQ_MESSAGE
     */
    public boolean isDownloadRequestMessageResponseType() {
        return NscsCommandResponseType.DOWNLOAD_REQ_MESSAGE.equals(getResponseType());
    }

    /**
     * Auxiliary method to check if this NscsCommandResponse is of NscsConfirmationCommandResponse type
     * 
     * @return true - if getResponseType() == NscsCommandResponseType.CONFIRMATION
     */
    public boolean isConfirmationResponseType() {
        return NscsCommandResponseType.CONFIRMATION.equals(getResponseType());
    }

    /**
     * Enumeration of the possibles nscs response types
     */
    public enum NscsCommandResponseType {
        MESSAGE,
        NAME_VALUE,
        NAME_MULTIPLE_VALUE,
        DOWNLOAD_REQ_MESSAGE,
        CONFIRMATION;
    }
}
