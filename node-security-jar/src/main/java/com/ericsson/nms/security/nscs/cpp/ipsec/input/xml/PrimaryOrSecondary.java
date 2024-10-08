//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.10.13 at 04:44:08 PM IST 
//


package com.ericsson.nms.security.nscs.cpp.ipsec.input.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for primaryOrSecondary.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;simpleType name="primaryOrSecondary"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="primary"/&gt;
 *     &lt;enumeration value="secondary"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "primaryOrSecondary")
@XmlEnum
public enum PrimaryOrSecondary {

    @XmlEnumValue("primary")
    PRIMARY("primary"),
    @XmlEnumValue("secondary")
    SECONDARY("secondary");
    private final String value;

    PrimaryOrSecondary(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PrimaryOrSecondary fromValue(String v) {
        for (PrimaryOrSecondary c: PrimaryOrSecondary.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
