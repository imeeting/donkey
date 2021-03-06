//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-548 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.29 at 03:35:44 ���� CST 
//


package com.richitec.donkey.msml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for audiomixType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="audiomixType">
 *   &lt;complexContent>
 *     &lt;extension base="{}basicAudioMixType">
 *       &lt;all>
 *         &lt;element name="asn" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="ri" use="required" type="{}posDuration.datatype" />
 *                 &lt;attribute ref="{http://convedia.com/ext}asth default="-96""/>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="n-loudest" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="n" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
 *                       &lt;minInclusive value="1"/>
 *                       &lt;maxInclusive value="16"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "audiomixType", propOrder = {
    "asn",
    "nLoudest"
})
public class AudiomixType
    extends BasicAudioMixType
{

    protected AudiomixType.Asn asn;
    @XmlElement(name = "n-loudest")
    protected AudiomixType.NLoudest nLoudest;

    /**
     * Gets the value of the asn property.
     * 
     * @return
     *     possible object is
     *     {@link AudiomixType.Asn }
     *     
     */
    public AudiomixType.Asn getAsn() {
        return asn;
    }

    /**
     * Sets the value of the asn property.
     * 
     * @param value
     *     allowed object is
     *     {@link AudiomixType.Asn }
     *     
     */
    public void setAsn(AudiomixType.Asn value) {
        this.asn = value;
    }

    /**
     * Gets the value of the nLoudest property.
     * 
     * @return
     *     possible object is
     *     {@link AudiomixType.NLoudest }
     *     
     */
    public AudiomixType.NLoudest getNLoudest() {
        return nLoudest;
    }

    /**
     * Sets the value of the nLoudest property.
     * 
     * @param value
     *     allowed object is
     *     {@link AudiomixType.NLoudest }
     *     
     */
    public void setNLoudest(AudiomixType.NLoudest value) {
        this.nLoudest = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="ri" use="required" type="{}posDuration.datatype" />
     *       &lt;attribute ref="{http://convedia.com/ext}asth default="-96""/>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Asn {

        @XmlAttribute(required = true)
        protected String ri;
        @XmlAttribute(namespace = "http://convedia.com/ext")
        protected Integer asth;

        /**
         * Gets the value of the ri property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRi() {
            return ri;
        }

        /**
         * Sets the value of the ri property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRi(String value) {
            this.ri = value;
        }

        /**
         * Gets the value of the asth property.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public int getAsth() {
            if (asth == null) {
                return -96;
            } else {
                return asth;
            }
        }

        /**
         * Sets the value of the asth property.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setAsth(Integer value) {
            this.asth = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="n" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
     *             &lt;minInclusive value="1"/>
     *             &lt;maxInclusive value="16"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class NLoudest {

        @XmlAttribute(required = true)
        protected int n;

        /**
         * Gets the value of the n property.
         * 
         */
        public int getN() {
            return n;
        }

        /**
         * Sets the value of the n property.
         * 
         */
        public void setN(int value) {
            this.n = value;
        }

    }

}
