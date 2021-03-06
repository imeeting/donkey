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
 * <p>Java class for dialogLanguage.datatype.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="dialogLanguage.datatype">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="application/moml+xml"/>
 *     &lt;enumeration value="application/voicexml+xml"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "dialogLanguage.datatype")
@XmlEnum
public enum DialogLanguageDatatype {

    @XmlEnumValue("application/moml+xml")
    APPLICATION_MOML_XML("application/moml+xml"),
    @XmlEnumValue("application/voicexml+xml")
    APPLICATION_VOICEXML_XML("application/voicexml+xml");
    private final String value;

    DialogLanguageDatatype(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DialogLanguageDatatype fromValue(String v) {
        for (DialogLanguageDatatype c: DialogLanguageDatatype.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
