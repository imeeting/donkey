//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-548 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.29 at 03:35:44 ���� CST 
//


package com.richitec.donkey.msml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for duplex.datatype.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="duplex.datatype">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="half"/>
 *     &lt;enumeration value="full"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "duplex.datatype")
@XmlEnum
public enum DuplexDatatype {

    @XmlEnumValue("half")
    HALF("half"),
    @XmlEnumValue("full")
    FULL("full");
    private final String value;

    DuplexDatatype(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DuplexDatatype fromValue(String v) {
        for (DuplexDatatype c: DuplexDatatype.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}