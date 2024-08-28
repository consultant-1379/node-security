/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api.command;

import java.util.LinkedList;
import java.util.List;

/**
 * Extends NscsCommandResponse to represent a confirmation command response.
 */
public class NscsConfirmationCommandResponse extends NscsCommandResponse {

    private static final long serialVersionUID = -6077962039097702840L;

    private String confirmationMessage;
    private List<String> additionalConfirmationMessages;

    /**
     * @param confirmationMessage
     *            the confirmationMessage
     */
    public NscsConfirmationCommandResponse(final String confirmationMessage) {
        this.confirmationMessage = confirmationMessage;
        this.additionalConfirmationMessages = new LinkedList<>();
    }

    @Override
    public NscsCommandResponseType getResponseType() {
        return NscsCommandResponseType.CONFIRMATION;
    }

    /**
     * @return the confirmationMessage
     */
    public String getConfirmationMessage() {
        return confirmationMessage;
    }

    /**
     * @param confirmationMessage
     *            the confirmationMessage to set
     */
    public void setConfirmationMessage(final String confirmationMessage) {
        this.confirmationMessage = confirmationMessage;
    }

    /**
     * @return the additionalConfirmationMessages
     */
    public List<String> getAdditionalConfirmationMessages() {
        return new LinkedList<>(this.additionalConfirmationMessages);
    }

    /**
     * @param additionalConfirmationMessages
     *            the additionalConfirmationMessages to set
     */
    public void setAdditionalConfirmationMessages(final String... additionalConfirmationMessages) {
        this.additionalConfirmationMessages = null;
        this.additionalConfirmationMessages = new LinkedList<>();
        for (final String additionalConfirmationMessage : additionalConfirmationMessages) {
            this.additionalConfirmationMessages.add(additionalConfirmationMessage);
        }
    }

}
