//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.04 at 06:11:50 PM AEDT 
//


package au.edu.swin.ict.road.xml.bindings;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for InMsgType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="InMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Operation" type="{http://www.swin.edu.au/ict/road/term}OperationType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="isResponse" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InMsgType", propOrder = {
        "operation"
})
public class InMsgType {

    @XmlElement(name = "Operation", required = true)
    protected OperationType operation;
    @XmlAttribute(name = "isResponse")
    protected Boolean isResponse;

    /**
     * Gets the value of the operation property.
     *
     * @return possible object is
     * {@link OperationType }
     */
    public OperationType getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     *
     * @param value allowed object is
     *              {@link OperationType }
     */
    public void setOperation(OperationType value) {
        this.operation = value;
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
