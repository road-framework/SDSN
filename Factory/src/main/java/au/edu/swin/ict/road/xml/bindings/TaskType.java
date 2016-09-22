//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.04 at 06:11:50 PM AEDT 
//


package au.edu.swin.ict.road.xml.bindings;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for TaskType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="TaskType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Out" type="{http://www.ict.swin.edu.au/serendip/types}OutMsgType" minOccurs="0"/>
 *         &lt;element name="In" type="{http://www.ict.swin.edu.au/serendip/types}InMsgType" minOccurs="0"/>
 *         &lt;element name="QoS" type="{http://www.ict.swin.edu.au/serendip/types}QoSType" minOccurs="0"/>
 *         &lt;element name="Traffic" type="{http://www.ict.swin.edu.au/serendip/types}TrafficModelType" minOccurs="0"/>
 *         &lt;element name="SrcMsgs" type="{http://www.ict.swin.edu.au/serendip/types}SrcMsgsType" minOccurs="0"/>
 *         &lt;element name="ResultMsgs" type="{http://www.ict.swin.edu.au/serendip/types}ResultMsgsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mep" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="msgAnalyser" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isMsgDriven" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="state" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskType", propOrder = {
        "out",
        "in",
        "qoS",
        "traffic",
        "srcMsgs",
        "resultMsgs"
})
public class TaskType {

    @XmlElement(name = "Out")
    protected OutMsgType out;
    @XmlElement(name = "In")
    protected InMsgType in;
    @XmlElement(name = "QoS")
    protected QoSType qoS;
    @XmlElement(name = "Traffic")
    protected TrafficModelType traffic;
    @XmlElement(name = "SrcMsgs")
    protected SrcMsgsType srcMsgs;
    @XmlElement(name = "ResultMsgs")
    protected ResultMsgsType resultMsgs;
    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "mep")
    protected String mep;
    @XmlAttribute(name = "msgAnalyser")
    protected String msgAnalyser;
    @XmlAttribute(name = "isMsgDriven")
    protected Boolean isMsgDriven;
    @XmlAttribute(name = "state")
    protected String state;

    /**
     * Gets the value of the out property.
     *
     * @return possible object is
     * {@link OutMsgType }
     */
    public OutMsgType getOut() {
        return out;
    }

    /**
     * Sets the value of the out property.
     *
     * @param value allowed object is
     *              {@link OutMsgType }
     */
    public void setOut(OutMsgType value) {
        this.out = value;
    }

    /**
     * Gets the value of the in property.
     *
     * @return possible object is
     * {@link InMsgType }
     */
    public InMsgType getIn() {
        return in;
    }

    /**
     * Sets the value of the in property.
     *
     * @param value allowed object is
     *              {@link InMsgType }
     */
    public void setIn(InMsgType value) {
        this.in = value;
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
     * Gets the value of the srcMsgs property.
     *
     * @return possible object is
     * {@link SrcMsgsType }
     */
    public SrcMsgsType getSrcMsgs() {
        return srcMsgs;
    }

    /**
     * Sets the value of the srcMsgs property.
     *
     * @param value allowed object is
     *              {@link SrcMsgsType }
     */
    public void setSrcMsgs(SrcMsgsType value) {
        this.srcMsgs = value;
    }

    /**
     * Gets the value of the resultMsgs property.
     *
     * @return possible object is
     * {@link ResultMsgsType }
     */
    public ResultMsgsType getResultMsgs() {
        return resultMsgs;
    }

    /**
     * Sets the value of the resultMsgs property.
     *
     * @param value allowed object is
     *              {@link ResultMsgsType }
     */
    public void setResultMsgs(ResultMsgsType value) {
        this.resultMsgs = value;
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
     * Gets the value of the mep property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMep() {
        return mep;
    }

    /**
     * Sets the value of the mep property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMep(String value) {
        this.mep = value;
    }

    /**
     * Gets the value of the msgAnalyser property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMsgAnalyser() {
        return msgAnalyser;
    }

    /**
     * Sets the value of the msgAnalyser property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMsgAnalyser(String value) {
        this.msgAnalyser = value;
    }

    /**
     * Gets the value of the isMsgDriven property.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isIsMsgDriven() {
        return isMsgDriven;
    }

    /**
     * Sets the value of the isMsgDriven property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsMsgDriven(Boolean value) {
        this.isMsgDriven = value;
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
