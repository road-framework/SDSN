//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.04 at 06:11:50 PM AEDT 
//


package au.edu.swin.ict.road.xml.bindings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for InterProcessRegulationUnitsType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="InterProcessRegulationUnitsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InterProcessRegulationUnit" type="{http://www.ict.swin.edu.au/serendip/types}InterProcessRegulationUnitType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InterProcessRegulationUnitsType", propOrder = {
        "interProcessRegulationUnit"
})
public class InterProcessRegulationUnitsType {

    @XmlElement(name = "InterProcessRegulationUnit", required = true)
    protected List<InterProcessRegulationUnitType> interProcessRegulationUnit;

    /**
     * Gets the value of the interProcessRegulationUnit property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the interProcessRegulationUnit property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInterProcessRegulationUnit().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link InterProcessRegulationUnitType }
     */
    public List<InterProcessRegulationUnitType> getInterProcessRegulationUnit() {
        if (interProcessRegulationUnit == null) {
            interProcessRegulationUnit = new ArrayList<InterProcessRegulationUnitType>();
        }
        return this.interProcessRegulationUnit;
    }

}
