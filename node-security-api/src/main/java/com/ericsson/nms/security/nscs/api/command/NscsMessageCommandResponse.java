package com.ericsson.nms.security.nscs.api.command;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * A subclass of NscsCommandResponse representing a Message response as result of a command execution
 * </p>
 * Created by emaynes on 01/05/2014.
 */
public class NscsMessageCommandResponse extends NscsCommandResponse {

    private static final long serialVersionUID = -3681672091985814412L;

    private List<String> messages;

    public NscsMessageCommandResponse() {
        this.messages = new LinkedList<>();
    }

    public NscsMessageCommandResponse(final String... messages) {
        this.messages = new LinkedList<>();
        for (final String message : messages) {
            this.messages.add(message);
        }
    }

    /**
     * @return the response message.
     */
    public String getMessage() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String message : messages) {
            stringBuilder.append(message);
        }
        return stringBuilder.toString();
    }

    /**
     * @return the response messages.
     */
    public String[] getMessages() {
        return messages != null ? messages.toArray(new String[0]) : null;
    }

    /**
     * Sets the response message.
     * 
     * @param message
     *            the response message
     */
    public void setMessage(final String message) {
        this.messages.add(message);
    }

    /**
     * Sets the response messages.
     * 
     * @param messages
     *            the response messages
     */
    public void setMessages(final String... messages) {
        this.messages = null;
        this.messages = new LinkedList<>();
        for (final String message : messages) {
            this.messages.add(message);
        }
    }

    /**
     * Always returns NscsCommandResponseType.MESSAGE
     * 
     * @return NscsCommandResponseType.MESSAGE
     */
    @Override
    public NscsCommandResponseType getResponseType() {
        return NscsCommandResponseType.MESSAGE;
    }
}
