//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.04 at 06:11:50 PM AEDT 
//


package au.edu.swin.ict.road.xml.bindings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SrcMsgType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="SrcMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="contractId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="termId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isResponse" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SrcMsgType")
public class SrcMsgType {

    @XmlAttribute(name = "contractId")
    protected String contractId;
    @XmlAttribute(name = "termId")
    protected String termId;
    @XmlAttribute(name = "isResponse")
    protected Boolean isResponse;

    /**
     * Gets the value of the contractId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getContractId() {
        return contractId;
    }

    /**
     * Sets the value of the contractId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setContractId(String value) {
        this.contractId = value;
    }

    /**
     * Gets the value of the termId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTermId() {
        return termId;
    }

    /**
     * Sets the value of the termId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTermId(String value) {
        this.termId = value;
    }

    /**
     * Gets the value of the isResponse property.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public boolean isIsResponse() {
        if (isResponse == null) {
            return false;
        } else {
            return isResponse;
        }
    }

    /**
     * Sets the value of the isResponse property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsResponse(Boolean value) {
        this.isResponse = value;
    }

}