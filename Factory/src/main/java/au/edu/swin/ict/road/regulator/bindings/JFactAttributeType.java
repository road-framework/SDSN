//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.14 at 06:32:41 PM EST 
//


package au.edu.swin.ict.road.regulator.bindings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JFactAttributeType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="JFactAttributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="attributeKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attributeValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JFactAttributeType", propOrder = {
        "attributeKey",
        "attributeValue"
})
public class JFactAttributeType {

    @XmlElement(required = true)
    protected String attributeKey;
    @XmlElement(required = true)
    protected String attributeValue;

    /**
     * Gets the value of the attributeKey property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAttributeKey() {
        return attributeKey;
    }

    /**
     * Sets the value of the attributeKey property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAttributeKey(String value) {
        this.attributeKey = value;
    }

    /**
     * Gets the value of the attributeValue property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAttributeValue() {
        return attributeValue;
    }

    /**
     * Sets the value of the attributeValue property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAttributeValue(String value) {
        this.attributeValue = value;
    }

}
