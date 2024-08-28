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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.ericsson.nms.security.nscs.api.rest.IpSecValidityErrorCode;

public class IpSecNodesErrorHandlerTest {

    private final static String IPADDRESS_ATTRIBUTE_TYPE = "IpAddressType";
    private final static String IPADDRESS_ERROR_PATTERN = " for type '" + IPADDRESS_ATTRIBUTE_TYPE + "'";

    private static final String ELEMENT_LOCALNAME = "local";
    private static final String ELEMENT_QNAME = "qName";
    private static final String ELEMENT_NODENAME = "NodeFdn";
    private static final String NODE_NAME1 = "Name1";
    private static final String NODE_NAME2 = "Name2";
    private static final String ELEMENT_INVALID1 = "invalidAttribute";
    private static final String ELEMENT_INVALID2 = "invalidAttribute2";
    private static final String ELEMENT_INVALID1_GENERICERRORMESSAGE = "Generic exception";
    private static final String ELEMENT_INVALID2_IPADDRESERRORMESSAGE = "Error data in " + IPADDRESS_ERROR_PATTERN;
    private static final IpSecValidityErrorCode ERRORCODE_UNKNOWN = IpSecValidityErrorCode.UNKNOWN;
    private static final IpSecValidityErrorCode ERRORCODE_IPADDRESS_TYPE = IpSecValidityErrorCode.IPADDRESS_TYPE;

    @Test
    public void test_Constructor() {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        assertTrue("Element must be empty", handler.getElement().isEmpty());
        assertNotNull("Error map is null", handler.getElementErrorMap());
    }

    @Test
    public void test_startElement_LocalName() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", ELEMENT_LOCALNAME, "", null);
        assertTrue("Element must be set", ELEMENT_LOCALNAME.equals(handler.getElement()));
    }

    @Test
    public void test_startElement_LocalNameNull() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", null, ELEMENT_QNAME, null);
        assertTrue("Element must be set", ELEMENT_QNAME.equals(handler.getElement()));
    }

    @Test
    public void test_startElement_LocalNameEmpty() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", "", ELEMENT_QNAME, null);
        assertTrue("Element must be set", ELEMENT_QNAME.equals(handler.getElement()));
    }

    @Test
    public void test_startElement_getIsNodeFdnElementReturnsTrue() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", ELEMENT_NODENAME, ELEMENT_QNAME, null);
        assertTrue("getIsNodeFdnElement must be true", handler.getIsNodeFdnElement());
    }

    @Test
    public void test_startElement_getIsNodeFdnElementReturnsFalse() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", ELEMENT_QNAME, ELEMENT_QNAME, null);
        assertTrue("getIsNodeFdnElement must be false", !handler.getIsNodeFdnElement());
    }

    @Test
    public void test_endElement_resetIsNodeFdnElementToFalse() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", ELEMENT_NODENAME, ELEMENT_QNAME, null);
        assertTrue("Element node name must be true", handler.getIsNodeFdnElement());
        handler.endElement("", "", "");
        assertTrue("Element node name must be false", !handler.getIsNodeFdnElement());
    }

    @Test
    public void test_characters_getEmptyCurrentNode() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.characters(NODE_NAME1.toCharArray(), 1, 1);
        assertTrue("CurrentNode must be empty", handler.getCurrentNode().isEmpty());
    }

    @Test
    public void test_characters_getNotEmptyCurrentNode() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", ELEMENT_NODENAME, ELEMENT_QNAME, null);
        assertTrue("Element node name must be true", handler.getIsNodeFdnElement());
        handler.characters(NODE_NAME1.toCharArray(), 0, NODE_NAME1.length());
        assertTrue("Element node name must be true", handler.getIsNodeFdnElement());
        handler.endElement("", "", "");
        assertTrue("Element node name must be false", !handler.getIsNodeFdnElement());
        assertTrue("CurrentNode mismatch! Expected " + NODE_NAME1 + " found " + handler.getCurrentNode(), NODE_NAME1.equals(handler.getCurrentNode()));
    }

    @Test
    public void test_warning_addNodeToMap() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", ELEMENT_NODENAME, ELEMENT_QNAME, null);
        handler.characters(NODE_NAME1.toCharArray(), 0, NODE_NAME1.length());
        handler.endElement("", "", "");
        handler.startElement("", ELEMENT_INVALID1, ELEMENT_QNAME, null);
        handler.warning(new SAXParseException(ELEMENT_INVALID1_GENERICERRORMESSAGE, null));
        handler.endElement("", "", "");

        assertTrue("Node Map size mismatched", handler.getElementErrorMap().size() == 1);
        assertNotNull("Node expected in map", handler.getElementErrorMap().get(NODE_NAME1));

        HashMap<String, IpSecNodesErrorHandlerData> map = handler.getElementErrorMap().get(NODE_NAME1);
        assertTrue("Attribute Map size mismatched", map.size() == 1);
        assertNotNull("Attribute expected in map", map.get(ELEMENT_INVALID1));
        assertTrue("Attribute error message mismatched", ELEMENT_INVALID1_GENERICERRORMESSAGE.equals(map.get(ELEMENT_INVALID1).getErrorMessage()));
        assertTrue("Attribute error code mismatched", ERRORCODE_UNKNOWN.equals(map.get(ELEMENT_INVALID1).getErrorCode()));
    }

    @Test
    public void test_warning_add2NodesToMap() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", ELEMENT_NODENAME, ELEMENT_QNAME, null);
        handler.characters(NODE_NAME1.toCharArray(), 0, NODE_NAME1.length());
        handler.endElement("", "", "");
        handler.startElement("", ELEMENT_INVALID1, ELEMENT_QNAME, null);
        handler.warning(new SAXParseException(ELEMENT_INVALID1_GENERICERRORMESSAGE, null));
        handler.endElement("", "", "");

        handler.startElement("", ELEMENT_NODENAME, ELEMENT_QNAME, null);
        handler.characters(NODE_NAME2.toCharArray(), 0, NODE_NAME2.length());
        handler.endElement("", "", "");
        handler.startElement("", ELEMENT_INVALID1, ELEMENT_QNAME, null);
        handler.warning(new SAXParseException(ELEMENT_INVALID1_GENERICERRORMESSAGE, null));
        handler.endElement("", "", "");

        assertTrue("Node Map size mismatched", handler.getElementErrorMap().size() == 2);
        assertNotNull("Node expected in map", handler.getElementErrorMap().get(NODE_NAME1));
        assertNotNull("Node expected in map", handler.getElementErrorMap().get(NODE_NAME2));

        HashMap<String, IpSecNodesErrorHandlerData> map = handler.getElementErrorMap().get(NODE_NAME1);
        assertTrue("Attribute Map size mismatched", map.size() == 1);
        assertNotNull("Attribute expected in map", map.get(ELEMENT_INVALID1));
        assertTrue("Attribute error message mismatched", ELEMENT_INVALID1_GENERICERRORMESSAGE.equals(map.get(ELEMENT_INVALID1).getErrorMessage()));
        assertTrue("Attribute error code mismatched", ERRORCODE_UNKNOWN.equals(map.get(ELEMENT_INVALID1).getErrorCode()));

        map = handler.getElementErrorMap().get(NODE_NAME2);
        assertTrue("Attribute Map size mismatched", map.size() == 1);
        assertNotNull("Attribute expected in map", map.get(ELEMENT_INVALID1));
        assertTrue("Attribute error message mismatched", ELEMENT_INVALID1_GENERICERRORMESSAGE.equals(map.get(ELEMENT_INVALID1).getErrorMessage()));
        assertTrue("Attribute error code mismatched", ERRORCODE_UNKNOWN.equals(map.get(ELEMENT_INVALID1).getErrorCode()));

    }

    @Test
    public void test_warning_add2AttributesToNodeToMapWithDefaultAndIpaddressErrors() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", ELEMENT_NODENAME, ELEMENT_QNAME, null);
        handler.characters(NODE_NAME1.toCharArray(), 0, NODE_NAME1.length());
        handler.endElement("", "", "");
        handler.startElement("", ELEMENT_INVALID1, ELEMENT_QNAME, null);
        handler.warning(new SAXParseException(ELEMENT_INVALID1_GENERICERRORMESSAGE, null));
        handler.endElement("", "", "");

        handler.startElement("", ELEMENT_NODENAME, ELEMENT_QNAME, null);
        handler.characters(NODE_NAME2.toCharArray(), 0, NODE_NAME2.length());
        handler.endElement("", "", "");
        handler.startElement("", ELEMENT_INVALID1, ELEMENT_QNAME, null);
        handler.warning(new SAXParseException(ELEMENT_INVALID1_GENERICERRORMESSAGE, null));
        handler.endElement("", "", "");

        handler.startElement("", ELEMENT_INVALID2, ELEMENT_QNAME, null);
        handler.warning(new SAXParseException(ELEMENT_INVALID2_IPADDRESERRORMESSAGE, null));
        handler.endElement("", "", "");

        assertTrue("Node Map size mismatched", handler.getElementErrorMap().size() == 2);
        assertNotNull("Node expected in map", handler.getElementErrorMap().get(NODE_NAME1));
        assertNotNull("Node expected in map", handler.getElementErrorMap().get(NODE_NAME2));

        HashMap<String, IpSecNodesErrorHandlerData> map = handler.getElementErrorMap().get(NODE_NAME1);
        assertTrue("Attribute Map size mismatched", map.size() == 1);
        assertNotNull("Attribute expected in map", map.get(ELEMENT_INVALID1));
        assertTrue("Attribute error message mismatched", ELEMENT_INVALID1_GENERICERRORMESSAGE.equals(map.get(ELEMENT_INVALID1).getErrorMessage()));
        assertTrue("Attribute error code mismatched", ERRORCODE_UNKNOWN.equals(map.get(ELEMENT_INVALID1).getErrorCode()));

        map = handler.getElementErrorMap().get(NODE_NAME2);
        assertTrue("Attribute Map size mismatched", map.size() == 2);
        assertNotNull("Attribute expected in map", map.get(ELEMENT_INVALID1));
        assertTrue("Attribute error message mismatched", ELEMENT_INVALID1_GENERICERRORMESSAGE.equals(map.get(ELEMENT_INVALID1).getErrorMessage()));
        assertTrue("Attribute error code mismatched", ERRORCODE_UNKNOWN.equals(map.get(ELEMENT_INVALID1).getErrorCode()));
        assertNotNull("Attribute expected in map", map.get(ELEMENT_INVALID2));
        assertTrue("Attribute error message mismatched", ELEMENT_INVALID2_IPADDRESERRORMESSAGE.equals(map.get(ELEMENT_INVALID2).getErrorMessage()));
        assertTrue("Attribute error code mismatched", ERRORCODE_IPADDRESS_TYPE.equals(map.get(ELEMENT_INVALID2).getErrorCode()));

    }

    @Test
    public void test_warning_add2AttributesToNodeToMapWithMultipleErrorsForTheSameAttribute() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", ELEMENT_NODENAME, ELEMENT_QNAME, null);
        handler.characters(NODE_NAME1.toCharArray(), 0, NODE_NAME1.length());
        handler.endElement("", "", "");
        handler.startElement("", ELEMENT_INVALID1, ELEMENT_QNAME, null);
        handler.warning(new SAXParseException(ELEMENT_INVALID1_GENERICERRORMESSAGE, null));
        handler.endElement("", "", "");

        handler.startElement("", ELEMENT_NODENAME, ELEMENT_QNAME, null);
        handler.characters(NODE_NAME2.toCharArray(), 0, NODE_NAME2.length());
        handler.endElement("", "", "");
        handler.startElement("", ELEMENT_INVALID1, ELEMENT_QNAME, null);
        handler.warning(new SAXParseException(ELEMENT_INVALID1_GENERICERRORMESSAGE, null));
        handler.endElement("", "", "");

        handler.startElement("", ELEMENT_INVALID2, ELEMENT_QNAME, null);
        handler.warning(new SAXParseException(ELEMENT_INVALID2_IPADDRESERRORMESSAGE, null));
        handler.warning(new SAXParseException(ELEMENT_INVALID1_GENERICERRORMESSAGE, null));

        handler.endElement("", "", "");

        assertTrue("Node Map size mismatched", handler.getElementErrorMap().size() == 2);
        assertNotNull("Node expected in map", handler.getElementErrorMap().get(NODE_NAME1));
        assertNotNull("Node expected in map", handler.getElementErrorMap().get(NODE_NAME2));

        HashMap<String, IpSecNodesErrorHandlerData> map = handler.getElementErrorMap().get(NODE_NAME1);
        assertTrue("Attribute Map size mismatched", map.size() == 1);
        assertNotNull("Attribute expected in map", map.get(ELEMENT_INVALID1));
        assertTrue("Attribute error message mismatched", ELEMENT_INVALID1_GENERICERRORMESSAGE.equals(map.get(ELEMENT_INVALID1).getErrorMessage()));
        assertTrue("Attribute error code mismatched", ERRORCODE_UNKNOWN.equals(map.get(ELEMENT_INVALID1).getErrorCode()));

        map = handler.getElementErrorMap().get(NODE_NAME2);
        assertTrue("Attribute Map size mismatched", map.size() == 2);
        assertNotNull("Attribute expected in map", map.get(ELEMENT_INVALID1));
        assertTrue("Attribute error message mismatched", ELEMENT_INVALID1_GENERICERRORMESSAGE.equals(map.get(ELEMENT_INVALID1).getErrorMessage()));
        assertTrue("Attribute error code mismatched", ERRORCODE_UNKNOWN.equals(map.get(ELEMENT_INVALID1).getErrorCode()));
        assertNotNull("Attribute expected in map", map.get(ELEMENT_INVALID2));
        assertTrue("Attribute error message mismatched", ELEMENT_INVALID1_GENERICERRORMESSAGE.equals(map.get(ELEMENT_INVALID2).getErrorMessage()));
        assertTrue("Attribute error code mismatched", ERRORCODE_IPADDRESS_TYPE.equals(map.get(ELEMENT_INVALID2).getErrorCode()));

    }

    @Test
    public void test_endDocument_NoErrors() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", ELEMENT_NODENAME, ELEMENT_QNAME, null);
        handler.characters(NODE_NAME1.toCharArray(), 0, NODE_NAME1.length());
        handler.endElement("", "", "");
        handler.startElement("", ELEMENT_INVALID1, ELEMENT_QNAME, null);
        handler.endElement("", "", "");
        handler.endDocument();
    }

    @Test(expected = SAXException.class)
    public void test_endDocument_WithErrors() throws SAXException {
        IpSecNodesErrorHandler handler = new IpSecNodesErrorHandler();
        assertNotNull("Null object", handler);
        handler.startElement("", ELEMENT_NODENAME, ELEMENT_QNAME, null);
        handler.characters(NODE_NAME1.toCharArray(), 0, NODE_NAME1.length());
        handler.endElement("", "", "");
        handler.startElement("", ELEMENT_INVALID1, ELEMENT_QNAME, null);
        handler.warning(new SAXParseException(ELEMENT_INVALID1_GENERICERRORMESSAGE, null));
        handler.endElement("", "", "");
        handler.endDocument();
    }

}
