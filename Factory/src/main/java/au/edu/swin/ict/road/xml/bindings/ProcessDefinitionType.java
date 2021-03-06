//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.04 at 06:11:50 PM AEDT 
//


package au.edu.swin.ict.road.xml.bindings;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ProcessDefinitionType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="ProcessDefinitionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CoS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CoT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CollaborationUnitRef" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="InterCollaborationRegulationUnitRef" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="Constraints" type="{http://www.ict.swin.edu.au/serendip/types}ConstraintsType" minOccurs="0"/>
 *         &lt;element name="Monitor" type="{http://www.swin.edu.au/ict/road/monitor}MonitorType" minOccurs="0"/>
 *         &lt;element name="QoS" type="{http://www.ict.swin.edu.au/serendip/types}QoSType" minOccurs="0"/>
 *         &lt;element name="Traffic" type="{http://www.ict.swin.edu.au/serendip/types}TrafficModelType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="descr" type="{http://www.w3.org/2001/XMLSchema}string" default="optional" />
 *       &lt;attribute name="state" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessDefinitionType", propOrder = {
        "coS",
        "coT",
        "collaborationUnitRef",
        "interCollaborationRegulationUnitRef",
        "constraints",
        "monitor",
        "qoS",
        "traffic"
})
public class ProcessDefinitionType {

    @XmlElement(name = "CoS")
    protected String coS;
    @XmlElement(name = "CoT")
    protected String coT;
    @XmlElement(name = "CollaborationUnitRef", required = true)
    protected List<String> collaborationUnitRef;
    @XmlElement(name = "InterCollaborationRegulationUnitRef", required = true)
    protected List<String> interCollaborationRegulationUnitRef;
    @XmlElement(name = "Constraints")
    protected ConstraintsType constraints;
    @XmlElement(name = "Monitor")
    protected MonitorType monitor;
    @XmlElement(name = "QoS")
    protected QoSType qoS;
    @XmlElement(name = "Traffic")
    protected TrafficModelType traffic;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "descr")
    protected String descr;
    @XmlAttribute(name = "state")
    protected String state;

    /**
     * Gets the value of the coS property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCoS() {
        return coS;
    }

    /**
     * Sets the value of the coS property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCoS(String value) {
        this.coS = value;
    }

    /**
     * Gets the value of the coT property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCoT() {
        return coT;
    }

    /**
     * Sets the value of the coT property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCoT(String value) {
        this.coT = value;
    }

    /**
     * Gets the value of the collaborationUnitRef property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the collaborationUnitRef property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCollaborationUnitRef().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     */
    public List<String> getCollaborationUnitRef() {
        if (collaborationUnitRef == null) {
            collaborationUnitRef = new ArrayList<String>();
        }
        return this.collaborationUnitRef;
    }

    /**
     * Gets the value of the interCollaborationRegulationUnitRef property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the interCollaborationRegulationUnitRef property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInterCollaborationRegulationUnitRef().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     */
    public List<String> getInterCollaborationRegulationUnitRef() {
        if (interCollaborationRegulationUnitRef == null) {
            interCollaborationRegulationUnitRef = new ArrayList<String>();
        }
        return this.interCollaborationRegulationUnitRef;
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
     * Gets the value of the qoS property.
     *
     * @return possible object is
     * {@link QoSType }
     */
    public QoSType getQoS() {
        return qoS;
    }

    /**
     * Sets the value of the qoS property.
     *
     * @param value allowed object is
     *              {@link QoSType }
     */
    public void setQoS(QoSType value) {
        this.qoS = value;
    }

    /**
     * Gets the value of the traffic property.
     *
     * @return possible object is
     * {@link TrafficModelType }
     */
    public TrafficModelType getTraffic() {
        return traffic;
    }

    /**
     * Sets the value of the traffic property.
     *
     * @param value allowed object is
     *              {@link TrafficModelType }
     */
    public void setTraffic(TrafficModelType value) {
        this.traffic = value;
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
     * Gets the value of the descr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDescr() {
        if (descr == null) {
            return "optional";
        } else {
            return descr;
        }
    }

    /**
     * Sets the value of the descr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDescr(String value) {
        this.descr = value;
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

}
