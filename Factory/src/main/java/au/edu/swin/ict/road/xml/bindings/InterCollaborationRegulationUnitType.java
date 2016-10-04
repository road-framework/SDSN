//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.04 at 06:11:50 PM AEDT 
//


package au.edu.swin.ict.road.xml.bindings;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for InterCollaborationRegulationUnitType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="InterCollaborationRegulationUnitType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Routing" type="{http://www.ict.swin.edu.au/serendip/types}RegulationRuleRef" minOccurs="0"/>
 *         &lt;element name="Synchronization" type="{http://www.ict.swin.edu.au/serendip/types}RegulationRuleRef" minOccurs="0"/>
 *         &lt;element name="Passthrough" type="{http://www.ict.swin.edu.au/serendip/types}RegulationRuleRef" minOccurs="0"/>
 *         &lt;element name="Global" type="{http://www.ict.swin.edu.au/serendip/types}RegulationRuleRef" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="extension" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="state" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="extends" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isAbstract" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InterCollaborationRegulationUnitType", propOrder = {
        "routing",
        "synchronization",
        "passthrough",
        "global"
})
public class InterCollaborationRegulationUnitType {

    @XmlElement(name = "Routing")
    protected RegulationRuleRef routing;
    @XmlElement(name = "Synchronization")
    protected RegulationRuleRef synchronization;
    @XmlElement(name = "Passthrough")
    protected RegulationRuleRef passthrough;
    @XmlElement(name = "Global")
    protected RegulationRuleRef global;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "extension")
    protected String extension;
    @XmlAttribute(name = "state")
    protected String state;
    @XmlAttribute(name = "extends")
    protected String _extends;
    @XmlAttribute(name = "isAbstract")
    protected Boolean isAbstract;

    /**
     * Gets the value of the routing property.
     *
     * @return possible object is
     * {@link RegulationRuleRef }
     */
    public RegulationRuleRef getRouting() {
        return routing;
    }

    /**
     * Sets the value of the routing property.
     *
     * @param value allowed object is
     *              {@link RegulationRuleRef }
     */
    public void setRouting(RegulationRuleRef value) {
        this.routing = value;
    }

    /**
     * Gets the value of the synchronization property.
     *
     * @return possible object is
     * {@link RegulationRuleRef }
     */
    public RegulationRuleRef getSynchronization() {
        return synchronization;
    }

    /**
     * Sets the value of the synchronization property.
     *
     * @param value allowed object is
     *              {@link RegulationRuleRef }
     */
    public void setSynchronization(RegulationRuleRef value) {
        this.synchronization = value;
    }

    /**
     * Gets the value of the passthrough property.
     *
     * @return possible object is
     * {@link RegulationRuleRef }
     */
    public RegulationRuleRef getPassthrough() {
        return passthrough;
    }

    /**
     * Sets the value of the passthrough property.
     *
     * @param value allowed object is
     *              {@link RegulationRuleRef }
     */
    public void setPassthrough(RegulationRuleRef value) {
        this.passthrough = value;
    }

    /**
     * Gets the value of the global property.
     *
     * @return possible object is
     * {@link RegulationRuleRef }
     */
    public RegulationRuleRef getGlobal() {
        return global;
    }

    /**
     * Sets the value of the global property.
     *
     * @param value allowed object is
     *              {@link RegulationRuleRef }
     */
    public void setGlobal(RegulationRuleRef value) {
        this.global = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the extension property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setExtension(String value) {
        this.extension = value;
    }

    /**
     * Gets the value of the state property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Gets the value of the extends property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getExtends() {
        return _extends;
    }

    /**
     * Sets the value of the extends property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setExtends(String value) {
        this._extends = value;
    }

    /**
     * Gets the value of the isAbstract property.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public boolean isIsAbstract() {
        if (isAbstract == null) {
            return false;
        } else {
            return isAbstract;
        }
    }

    /**
     * Sets the value of the isAbstract property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsAbstract(Boolean value) {
        this.isAbstract = value;
    }

}