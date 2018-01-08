
package vocabularyservices.wsdl.metaservice_vgr_se.v2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import se.vgr.metaservice.schema.response.v1.LastChangeResponseObjectType;
import se.vgr.metaservice.schema.response.v1.LookupResponseObjectType;
import se.vgr.metaservice.schema.response.v1.NodeListResponseObjectType;
import se.vgr.metaservice.schema.response.v1.ResponseObjectType;
import se.vgr.metaservice.schema.response.v1.XMLResponseObjectType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the vocabularyservices.wsdl.metaservice_vgr_se.v2 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetVocabularyResponse_QNAME = new QName("urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", "GetVocabularyResponse");
    private final static QName _UpdateVocabularyNodeResponse_QNAME = new QName("urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", "UpdateVocabularyNodeResponse");
    private final static QName _FindNodesResponse_QNAME = new QName("urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", "FindNodesResponse");
    private final static QName _MoveVocabularyNodeResponse_QNAME = new QName("urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", "MoveVocabularyNodeResponse");
    private final static QName _GetNamespaceXmlResponse_QNAME = new QName("urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", "GetNamespaceXmlResponse");
    private final static QName _AddVocabularyNodeResponse_QNAME = new QName("urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", "AddVocabularyNodeResponse");
    private final static QName _FindNodesByNameResponse_QNAME = new QName("urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", "FindNodesByNameResponse");
    private final static QName _LastChangeResponse_QNAME = new QName("urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", "LastChangeResponse");
    private final static QName _LookupWordResponse_QNAME = new QName("urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", "LookupWordResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: vocabularyservices.wsdl.metaservice_vgr_se.v2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FindNodesByNameRequest }
     * 
     */
    public FindNodesByNameRequest createFindNodesByNameRequest() {
        return new FindNodesByNameRequest();
    }

    /**
     * Create an instance of {@link LookupWordRequest }
     * 
     */
    public LookupWordRequest createLookupWordRequest() {
        return new LookupWordRequest();
    }

    /**
     * Create an instance of {@link AddVocabularyNodeRequest }
     * 
     */
    public AddVocabularyNodeRequest createAddVocabularyNodeRequest() {
        return new AddVocabularyNodeRequest();
    }

    /**
     * Create an instance of {@link FindNodesRequest }
     * 
     */
    public FindNodesRequest createFindNodesRequest() {
        return new FindNodesRequest();
    }

    /**
     * Create an instance of {@link GetVocabularyRequest }
     * 
     */
    public GetVocabularyRequest createGetVocabularyRequest() {
        return new GetVocabularyRequest();
    }

    /**
     * Create an instance of {@link GetNamespaceXmlRequest }
     * 
     */
    public GetNamespaceXmlRequest createGetNamespaceXmlRequest() {
        return new GetNamespaceXmlRequest();
    }

    /**
     * Create an instance of {@link UpdateVocabularyNodeRequest }
     * 
     */
    public UpdateVocabularyNodeRequest createUpdateVocabularyNodeRequest() {
        return new UpdateVocabularyNodeRequest();
    }

    /**
     * Create an instance of {@link MoveVocabularyNodeRequest }
     * 
     */
    public MoveVocabularyNodeRequest createMoveVocabularyNodeRequest() {
        return new MoveVocabularyNodeRequest();
    }

    /**
     * Create an instance of {@link LastChangeRequest }
     * 
     */
    public LastChangeRequest createLastChangeRequest() {
        return new LastChangeRequest();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NodeListResponseObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", name = "GetVocabularyResponse")
    public JAXBElement<NodeListResponseObjectType> createGetVocabularyResponse(NodeListResponseObjectType value) {
        return new JAXBElement<NodeListResponseObjectType>(_GetVocabularyResponse_QNAME, NodeListResponseObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", name = "UpdateVocabularyNodeResponse")
    public JAXBElement<ResponseObjectType> createUpdateVocabularyNodeResponse(ResponseObjectType value) {
        return new JAXBElement<ResponseObjectType>(_UpdateVocabularyNodeResponse_QNAME, ResponseObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NodeListResponseObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", name = "FindNodesResponse")
    public JAXBElement<NodeListResponseObjectType> createFindNodesResponse(NodeListResponseObjectType value) {
        return new JAXBElement<NodeListResponseObjectType>(_FindNodesResponse_QNAME, NodeListResponseObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", name = "MoveVocabularyNodeResponse")
    public JAXBElement<ResponseObjectType> createMoveVocabularyNodeResponse(ResponseObjectType value) {
        return new JAXBElement<ResponseObjectType>(_MoveVocabularyNodeResponse_QNAME, ResponseObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLResponseObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", name = "GetNamespaceXmlResponse")
    public JAXBElement<XMLResponseObjectType> createGetNamespaceXmlResponse(XMLResponseObjectType value) {
        return new JAXBElement<XMLResponseObjectType>(_GetNamespaceXmlResponse_QNAME, XMLResponseObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", name = "AddVocabularyNodeResponse")
    public JAXBElement<ResponseObjectType> createAddVocabularyNodeResponse(ResponseObjectType value) {
        return new JAXBElement<ResponseObjectType>(_AddVocabularyNodeResponse_QNAME, ResponseObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NodeListResponseObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", name = "FindNodesByNameResponse")
    public JAXBElement<NodeListResponseObjectType> createFindNodesByNameResponse(NodeListResponseObjectType value) {
        return new JAXBElement<NodeListResponseObjectType>(_FindNodesByNameResponse_QNAME, NodeListResponseObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LastChangeResponseObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", name = "LastChangeResponse")
    public JAXBElement<LastChangeResponseObjectType> createLastChangeResponse(LastChangeResponseObjectType value) {
        return new JAXBElement<LastChangeResponseObjectType>(_LastChangeResponse_QNAME, LastChangeResponseObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LookupResponseObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:VocabularyServices:wsdl:metaservice.vgr.se:v2", name = "LookupWordResponse")
    public JAXBElement<LookupResponseObjectType> createLookupWordResponse(LookupResponseObjectType value) {
        return new JAXBElement<LookupResponseObjectType>(_LookupWordResponse_QNAME, LookupResponseObjectType.class, null, value);
    }

}
