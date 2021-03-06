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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rootType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rootType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="size" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="CIF"/>
 *             &lt;enumeration value="QCIF"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute ref="{http://convedia.com/ext}codec use="required""/>
 *       &lt;attribute ref="{http://convedia.com/ext}bandwidth use="required""/>
 *       &lt;attribute ref="{http://convedia.com/ext}mpi use="required""/>
 *       &lt;attribute ref="{http://convedia.com/ext}bpp"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rootType")
public class RootType {

    @XmlAttribute(required = true)
    protected String size;
    @XmlAttribute(namespace = "http://convedia.com/ext", required = true)
    protected String codec;
    @XmlAttribute(namespace = "http://convedia.com/ext", required = true)
    protected int bandwidth;
    @XmlAttribute(namespace = "http://convedia.com/ext", required = true)
    protected int mpi;
    @XmlAttribute(namespace = "http://convedia.com/ext")
    protected String bpp;

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSize(String value) {
        this.size = value;
    }

    /**
     * Gets the value of the codec property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodec() {
        if (codec == null) {
            return "H263";
        } else {
            return codec;
        }
    }

    /**
     * Sets the value of the codec property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodec(String value) {
        this.codec = value;
    }

    /**
     * Gets the value of the bandwidth property.
     * 
     */
    public int getBandwidth() {
        return bandwidth;
    }

    /**
     * Sets the value of the bandwidth property.
     * 
     */
    public void setBandwidth(int value) {
        this.bandwidth = value;
    }

    /**
     * Gets the value of the mpi property.
     * 
     */
    public int getMpi() {
        return mpi;
    }

    /**
     * Sets the value of the mpi property.
     * 
     */
    public void setMpi(int value) {
        this.mpi = value;
    }

    /**
     * Gets the value of the bpp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBpp() {
        return bpp;
    }

    /**
     * Sets the value of the bpp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBpp(String value) {
        this.bpp = value;
    }

}
