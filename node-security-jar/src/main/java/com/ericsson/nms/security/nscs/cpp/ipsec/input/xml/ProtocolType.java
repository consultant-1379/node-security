//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.10.13 at 04:44:08 PM IST 
//


package com.ericsson.nms.security.nscs.cpp.ipsec.input.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for protocolType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;simpleType name="protocolType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ANY"/&gt;
 *     &lt;enumeration value="ICMP"/&gt;
 *     &lt;enumeration value="TCP"/&gt;
 *     &lt;enumeration value="UDP"/&gt;
 *     &lt;enumeration value="SCTP"/&gt;
 *     &lt;enumeration value="RAWIP"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "protocolType")
@XmlEnum
public enum ProtocolType {

    ANY,
    ICMP,
    TCP,
    UDP,
    SCTP,
    RAWIP;

    public String value() {
        return name();
    }

    public static ProtocolType fromValue(String v) {
        return valueOf(v);
    }

}
