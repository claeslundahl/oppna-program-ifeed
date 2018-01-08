
package se.vgr.metaservice.schema.request.v1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OptionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OptionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="includeSourceIds" form="qualified">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="entry" maxOccurs="unbounded" form="qualified">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" form="qualified"/>
 *                             &lt;element name="value" type="{urn:Request.schema.metaservice.vgr.se:v1}IncludeSourceIdsListType" minOccurs="0" form="qualified"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="filterByProperties" form="qualified">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="entry" maxOccurs="unbounded" form="qualified">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *                             &lt;element name="value" type="{urn:Request.schema.metaservice.vgr.se:v1}filterByPropertiesListType" minOccurs="0" form="qualified"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="wordsToReturn" type="{http://www.w3.org/2001/XMLSchema}int" form="qualified"/>
 *         &lt;element name="inputWords" type="{http://www.w3.org/2001/XMLSchema}int" form="qualified"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="matchSynonyms" type="{http://www.w3.org/2001/XMLSchema}boolean" form="qualified"/>
 *         &lt;element name="synomize" type="{http://www.w3.org/2001/XMLSchema}boolean" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OptionsType", propOrder = {
    "includeSourceIds",
    "filterByProperties",
    "wordsToReturn",
    "inputWords",
    "url",
    "matchSynonyms",
    "synomize"
})
public class OptionsType {

    @XmlElement(required = true, nillable = true)
    protected OptionsType.IncludeSourceIds includeSourceIds;
    @XmlElement(required = true, nillable = true)
    protected OptionsType.FilterByProperties filterByProperties;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer wordsToReturn;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer inputWords;
    @XmlElement(required = true, nillable = true)
    protected String url;
    @XmlElement(required = true, type = Boolean.class, nillable = true)
    protected Boolean matchSynonyms;
    @XmlElement(required = true, type = Boolean.class, nillable = true)
    protected Boolean synomize;

    /**
     * Gets the value of the includeSourceIds property.
     * 
     * @return
     *     possible object is
     *     {@link OptionsType.IncludeSourceIds }
     *     
     */
    public OptionsType.IncludeSourceIds getIncludeSourceIds() {
        return includeSourceIds;
    }

    /**
     * Sets the value of the includeSourceIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link OptionsType.IncludeSourceIds }
     *     
     */
    public void setIncludeSourceIds(OptionsType.IncludeSourceIds value) {
        this.includeSourceIds = value;
    }

    /**
     * Gets the value of the filterByProperties property.
     * 
     * @return
     *     possible object is
     *     {@link OptionsType.FilterByProperties }
     *     
     */
    public OptionsType.FilterByProperties getFilterByProperties() {
        return filterByProperties;
    }

    /**
     * Sets the value of the filterByProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link OptionsType.FilterByProperties }
     *     
     */
    public void setFilterByProperties(OptionsType.FilterByProperties value) {
        this.filterByProperties = value;
    }

    /**
     * Gets the value of the wordsToReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWordsToReturn() {
        return wordsToReturn;
    }

    /**
     * Sets the value of the wordsToReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWordsToReturn(Integer value) {
        this.wordsToReturn = value;
    }

    /**
     * Gets the value of the inputWords property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getInputWords() {
        return inputWords;
    }

    /**
     * Sets the value of the inputWords property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setInputWords(Integer value) {
        this.inputWords = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the matchSynonyms property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMatchSynonyms() {
        return matchSynonyms;
    }

    /**
     * Sets the value of the matchSynonyms property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMatchSynonyms(Boolean value) {
        this.matchSynonyms = value;
    }

    /**
     * Gets the value of the synomize property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSynomize() {
        return synomize;
    }

    /**
     * Sets the value of the synomize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSynomize(Boolean value) {
        this.synomize = value;
    }


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
     *         &lt;element name="entry" maxOccurs="unbounded" form="qualified">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
     *                   &lt;element name="value" type="{urn:Request.schema.metaservice.vgr.se:v1}filterByPropertiesListType" minOccurs="0" form="qualified"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "entry"
    })
    public static class FilterByProperties {

        @XmlElement(required = true)
        protected List<OptionsType.FilterByProperties.Entry> entry;

        /**
         * Gets the value of the entry property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the entry property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEntry().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OptionsType.FilterByProperties.Entry }
         * 
         * 
         */
        public List<OptionsType.FilterByProperties.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<OptionsType.FilterByProperties.Entry>();
            }
            return this.entry;
        }


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
         *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
         *         &lt;element name="value" type="{urn:Request.schema.metaservice.vgr.se:v1}filterByPropertiesListType" minOccurs="0" form="qualified"/>
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
            "key",
            "value"
        })
        public static class Entry {

            protected String key;
            protected FilterByPropertiesListType value;

            /**
             * Gets the value of the key property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKey() {
                return key;
            }

            /**
             * Sets the value of the key property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKey(String value) {
                this.key = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link FilterByPropertiesListType }
             *     
             */
            public FilterByPropertiesListType getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link FilterByPropertiesListType }
             *     
             */
            public void setValue(FilterByPropertiesListType value) {
                this.value = value;
            }

        }

    }


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
     *         &lt;element name="entry" maxOccurs="unbounded" form="qualified">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" form="qualified"/>
     *                   &lt;element name="value" type="{urn:Request.schema.metaservice.vgr.se:v1}IncludeSourceIdsListType" minOccurs="0" form="qualified"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "entry"
    })
    public static class IncludeSourceIds {

        @XmlElement(required = true)
        protected List<OptionsType.IncludeSourceIds.Entry> entry;

        /**
         * Gets the value of the entry property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the entry property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEntry().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OptionsType.IncludeSourceIds.Entry }
         * 
         * 
         */
        public List<OptionsType.IncludeSourceIds.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<OptionsType.IncludeSourceIds.Entry>();
            }
            return this.entry;
        }


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
         *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" form="qualified"/>
         *         &lt;element name="value" type="{urn:Request.schema.metaservice.vgr.se:v1}IncludeSourceIdsListType" minOccurs="0" form="qualified"/>
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
            "key",
            "value"
        })
        public static class Entry {

            protected Integer key;
            protected IncludeSourceIdsListType value;

            /**
             * Gets the value of the key property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getKey() {
                return key;
            }

            /**
             * Sets the value of the key property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setKey(Integer value) {
                this.key = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link IncludeSourceIdsListType }
             *     
             */
            public IncludeSourceIdsListType getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link IncludeSourceIdsListType }
             *     
             */
            public void setValue(IncludeSourceIdsListType value) {
                this.value = value;
            }

        }

    }

}
