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
 * <p>Java class for TelecomStandardEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;simpleType name="TelecomStandardEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="OPTION_I"/&gt;
 *     &lt;enumeration value="OPTION_II"/&gt;
 *     &lt;enumeration value="OPTION_III"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TelecomStandardEnum")
@XmlEnum
public enum TelecomStandardEnum {

    OPTION_I,
    OPTION_II,
    OPTION_III;

    public String value() {
        return name();
    }

    public static TelecomStandardEnum fromValue(String v) {
        return valueOf(v);
    }

}
