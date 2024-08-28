/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.nms.security.nscs.iscf;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ealemca
 */
public class IscfSchemaValidationEventHandler implements ValidationEventHandler {

    /**
     * The logger is instantiated explicitly here rather than injected because
     * this class is instantiated anonymously for the JAXB marshaller by
     * {@link com.ericsson.nms.security.nscs.iscf.ISCFCreatorBean}, e.g.
     *
     * <code>marshaller.setEventHandler(new IscfSchemaValidationEventHandler());</code>
     */
    private final Logger log = LoggerFactory.getLogger(IscfSchemaValidationEventHandler.class);

    /**
     * Simple logging implementation of
     * {@link javax.xml.bind.ValidationEventHandler}.
     *
     * There is no "default" switch case handler since anything other than the
     * provided severity levels throws an <code>IllegalArgumentException</code>
     *
     * @param event
     * @return <code>true</code> on validation warning or error,
     * <code>false</code> on fatal error.
     */
    @Override
    public boolean handleEvent(final ValidationEvent event) {
        boolean shouldFailValidation = true;
        switch (event.getSeverity()) {
            case ValidationEvent.FATAL_ERROR:
                log.error("ISCF XML validation error (IscfSchemaValidationEventHandler): {}",
                        event.getLinkedException());
                shouldFailValidation = false;
                break;
            case ValidationEvent.WARNING:
            case ValidationEvent.ERROR:
                log.warn("ISCF XML validation warning (IscfSchemaValidationEventHandler): {}",
                        event.getLinkedException());
                break;
        }
        return shouldFailValidation;
    }

}
