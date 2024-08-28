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
package com.ericsson.nms.security.nscs.utilities;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

/**
 * Customized handler for SAX parsing errors.
 * 
 * This class is responsible to capture all the error messages when XSD validates the input XML. It should register an instance with the XML reader using the
 * {@link org.xml.sax.XMLReader#setErrorHandler setErrorHandler} method. The parser will then report all errors and warnings through this class.
 * 
 * @author zlaxsri
 */
public class XsdErrorHandler implements ErrorHandler {

    private boolean isValid = true;
    private final List<String> xsdErrorMessages = new ArrayList<String>();

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        handleMessage(Constants.WARNING, exception);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        this.isValid = false;
        handleMessage(Constants.ERROR, exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        this.isValid = false;
        handleMessage(Constants.FATAL, exception);
    }

    /**
     * Formats the error messages with new line character appending to each message
     * 
     * @return string - containing all the error messages
     */
    public String formatErrorMessages() {
        final StringBuilder errorMessagesBuilder = new StringBuilder("");
        if (this.xsdErrorMessages != null && !this.xsdErrorMessages.isEmpty()) {
            for (String errorMessage : xsdErrorMessages) {
                errorMessagesBuilder.append(errorMessage + System.lineSeparator());
            }
        }

        return errorMessagesBuilder.toString();
    }

    private void handleMessage(String level, SAXParseException exception) {
        int lineNumber = exception.getLineNumber();
        int columnNumber = exception.getColumnNumber();
        String message = exception.getMessage();
        if (message != null) {
            int index = message.indexOf(":", 0);
            if (index != -1)
                message = message.substring(index + 1);
        }
        xsdErrorMessages.add("[" + level + "] Line no:" + lineNumber + ", Column no:" + columnNumber + ", Message:" + message);
    }

    public List<String> getXsdErrorMessages() {
        return xsdErrorMessages;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean isValid() {
        return this.isValid;
    }

}

