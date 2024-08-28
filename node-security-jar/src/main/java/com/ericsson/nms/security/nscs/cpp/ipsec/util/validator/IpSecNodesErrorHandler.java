/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cpp.ipsec.util.validator;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.ericsson.nms.security.nscs.api.rest.IpSecValidityErrorCode;

public class IpSecNodesErrorHandler extends DefaultHandler {

    private final static String ELEMENT_NODEFDN = "NodeFdn";
    private final static String IPADDRESS_ATTRIBUTE_TYPE = "IpAddressType";
    private final static String IPADDRESS_ERROR_PATTERN = " for type '" + IPADDRESS_ATTRIBUTE_TYPE + "'";
    private String element;
    //External Map key: node Fdn
    //Internal Map key: xml invalid attribute. The list is the list of  errors for that element
    private Map<String, HashMap<String, IpSecNodesErrorHandlerData>> elementErrorMap;
    private String currentNode;
    private boolean isNodeFdnElement;

    public IpSecNodesErrorHandler() {
        this.element = "";
        this.elementErrorMap = new HashMap<String, HashMap<String, IpSecNodesErrorHandlerData>>();
        this.currentNode = "";
        this.isNodeFdnElement = false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (localName != null && !localName.isEmpty()) {
            element = localName;
        } else {
            element = qName;
        }

        //when the new element is a node element, set the flag to read the value
        if (element.equalsIgnoreCase(ELEMENT_NODEFDN)) {
            this.isNodeFdnElement = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //when the end element is a node element, reset the flag
        if (element.equalsIgnoreCase(ELEMENT_NODEFDN)) {
            this.isNodeFdnElement = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        if (this.isNodeFdnElement) {
            this.currentNode = new String(ch, start, length);
            //this.isNodeElement = false;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        // throws exception if any error found
        if (this.elementErrorMap.size() > 0) {
            throw new SAXException();
        }
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        String errorMessage = exception.getMessage();

        IpSecValidityErrorCode errorCode = IpSecValidityErrorCode.UNKNOWN;
        if (errorMessage.contains(IPADDRESS_ERROR_PATTERN)) {
            errorCode = IpSecValidityErrorCode.IPADDRESS_TYPE;
        }

        if (this.elementErrorMap.get(this.currentNode) == null) {
            //First invalid field for the node
            //Instance the map and the list
            HashMap<String, IpSecNodesErrorHandlerData> map = new HashMap<String, IpSecNodesErrorHandlerData>();
            map.put(element, new IpSecNodesErrorHandlerData(errorCode, errorMessage));
            this.elementErrorMap.put(this.currentNode, map);
        } else {
            if (this.elementErrorMap.get(this.currentNode).get(element) == null) {
                this.elementErrorMap.get(this.currentNode).put(element, new IpSecNodesErrorHandlerData(errorCode, errorMessage));
            } else {
                //If we are getting more error for an attribute already present in the map
                //we want keep the original error code and replace the error message
                errorCode = this.elementErrorMap.get(this.currentNode).get(element).getErrorCode();
                this.elementErrorMap.get(this.currentNode).put(element, new IpSecNodesErrorHandlerData(errorCode, errorMessage));
            }
        }
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        warning(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        warning(exception);
    }

    public String getElement() {
        return this.element;
    }

    public Map<String, HashMap<String, IpSecNodesErrorHandlerData>> getElementErrorMap() {
        return this.elementErrorMap;
    }

    public boolean getIsNodeFdnElement() {
        return this.isNodeFdnElement;
    }

    public String getCurrentNode() {
        return this.currentNode;
    }
}
