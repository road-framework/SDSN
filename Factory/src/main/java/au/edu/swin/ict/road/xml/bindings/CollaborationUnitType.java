//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.04 at 06:11:50 PM AEDT 
//


package au.edu.swin.ict.road.xml.bindings;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for CollaborationUnitType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="CollaborationUnitType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ConfigurationDesign" type="{http://www.ict.swin.edu.au/serendip/types}ConfigurationDesignType"/>
 *         &lt;element name="RegulationDesign" type="{http://www.ict.swin.edu.au/serendip/types}RegulationDesignType"/>
 *         &lt;element name="Constraints" type="{http://www.ict.swin.edu.au/serendip/types}ConstraintsType" minOccurs="0"/>
 *         &lt;element name="Monitor" type="{http://www.swin.edu.au/ict/road/monitor}MonitorType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="monitoringPolicy" type="{http://www.w3.org/2001/XMLSchema}string" />
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
@XmlType(name = "CollaborationUnitType", propOrder = {
        "configurationDesign",
        "regulationDesign",
        "constraints",
        "monitor"
})
public class CollaborationUnitType {

    @XmlElement(name = "ConfigurationDesign", required = true)
    protected ConfigurationDesignType configurationDesign;
    @XmlElement(name = "RegulationDesign", required = true)
    protected RegulationDesignType regulationDesign;
    @XmlElement(name = "Constraints")
    protected ConstraintsType constraints;
    @XmlElement(name = "Monitor")
    protected MonitorType monitor;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "monitoringPolicy")
    protected String monitoringPolicy;
    @XmlAttribute(name = "extension")
    protected String extension;
    @XmlAttribute(name = "state")
    protected String state;
    @XmlAttribute(name = "extends")
    protected String _extends;
    @XmlAttribute(name = "isAbstract")
    protected Boolean isAbstract;

    /**
     * Gets the value of the configurationDesign property.
     *
     * @return possible object is
     * {@link ConfigurationDesignType }
     */
    public ConfigurationDesignType getConfigurationDesign() {
        return configurationDesign;
    }

    /**
     * Sets the value of the configurationDesign property.
     *
     * @param value allowed object is
     *              {@link ConfigurationDesignType }
     */
    public void setConfigurationDesign(ConfigurationDesignType value) {
        this.configurationDesign = value;
    }

    /**
     * Gets the value of the regulationDesign property.
     *
     * @return possible object is
     * {@link RegulationDesignType }
     */
    public RegulationDesignType getRegulationDesign() {
        return regulationDesign;
    }

    /**
     * Sets the value of the regulationDesign property.
     *
     * @param value allowed object is
     *              {@link RegulationDesignType }
     */
    public void setRegulationDesign(RegulationDesignType value) {
        this.regulationDesign = value;
    }

    /**
     * Gets the value of the constraints property.
     *
     * @return possible object is
     * {@link ConstraintsType }
     */
    public ConstraintsType getConstraints() {
        return constraints;
    }

    /**
     * Sets the value of the constraints property.
     *
     * @param value allowed object is
     *              {@link ConstraintsType }
     */
    public void setConstraints(ConstraintsType value) {
        this.constraints = value;
    }

    /**
     * Gets the value of the monitor property.
     *
     * @return possible object is
     * {@link MonitorType }
     */
    public MonitorType getMonitor() {
        return monitor;
    }

    /**
     * Sets the value of the monitor property.
     *
     * @param value allowed object is
     *              {@link MonitorType }
     */
    public void setMonitor(MonitorType value) {
        this.monitor = value;
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
     * Gets the value of the monitoringPolicy property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMonitoringPolicy() {
        return monitoringPolicy;
    }

    /**
     * Sets the value of the monitoringPolicy property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMonitoringPolicy(String value) {
        this.monitoringPolicy = value;
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
