
package vocabularyservices.wsdl.metaservice_vgr_se.v2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import se.vgr.metaservice.schema.node.v2.NodeType;
import se.vgr.metaservice.schema.request.v1.IdentificationType;
import se.vgr.metaservice.schema.request.v1.OptionsType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identification" type="{urn:Request.schema.metaservice.vgr.se:v1}IdentificationType"/>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="node" type="{urn:Node.schema.metaservice.vgr.se:v2}NodeType"/>
 *         &lt;element name="options" type="{urn:Request.schema.metaservice.vgr.se:v1}OptionsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identification",
    "requestId",
    "node",
    "options"
})
@XmlRootElement(name = "AddVocabularyNodeRequest")
public class AddVocabularyNodeRequest {

    @XmlElement(required = true)
    protected IdentificationType identification;
    @XmlElement(required = true)
    protected String requestId;
    @XmlElement(required = true)
    protected NodeType node;
    @XmlElement(required = true)
    protected OptionsType options;

    /**
     * Gets the value of the identification property.
     * 
     * @return
     *     possible object is
     *     {@link IdentificationType }
     *     
     */
    public IdentificationType getIdentification() {
        return identification;
    }

    /**
     * Sets the value of the identification property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentificationType }
     *     
     */
    public void setIdentification(IdentificationType value) {
        this.identification = value;
    }

    /**
     * Gets the value of the requestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the node property.
     * 
     * @return
     *     possible object is
     *     {@link NodeType }
     *     
     */
    public NodeType getNode() {
        return node;
    }

    /**
     * Sets the value of the node property.
     * 
     * @param value
     *     allowed object is
     *     {@link NodeType }
     *     
     */
    public void setNode(NodeType value) {
        this.node = value;
    }

    /**
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link OptionsType }
     *     
     */
    public OptionsType getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link OptionsType }
     *     
     */
    public void setOptions(OptionsType value) {
        this.options = value;
    }

}
