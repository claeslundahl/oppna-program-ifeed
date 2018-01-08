package se.vgr.metaservice.schema;

import se.vgr.metaservice.schema.node.v2.NodeType;
import se.vgr.metaservice.schema.response.v1.NodeListResponseObjectType;
import se.vgregion.metaservice.vocabularyservice.intsvc.VocabularyService;
import vocabularyservices.wsdl.metaservice_vgr_se.v2.GetVocabularyRequest;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2017-01-30.
 */
public class ApelonClient {

    private String wsdlUrl = "http://metadataservice.vgregion.se/vocabularyservice4/VocabularyService?wsdl";

    public ApelonClient() {
        super();
    }

    public ApelonClient(String wsdlUrl) {
        super();
        this.wsdlUrl = wsdlUrl;
    }

    public List<String> fetchVocabulary(String withThatPath) {
        try {
            return fetchVocabularyImpl(withThatPath);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> fetchVocabularyImpl(String withThatPath) throws MalformedURLException {
        URL url = new URL(wsdlUrl);
        //1st argument service URI, refer to wsdl document above
        //2nd argument is service name, refer to wsdl document above
        QName qname = new QName("http://intsvc.vocabularyservice.metaservice.vgregion.se/",
                "VocabularyServiceIntServiceImplService");

        Service service = Service.create(url, qname);
        VocabularyService vs = service.getPort(VocabularyService.class);
        GetVocabularyRequest params = new GetVocabularyRequest();
        params.setPath(withThatPath);

        List<String> result = new ArrayList<>();
        NodeListResponseObjectType nodes = vs.getVocabulary(params);
        for (NodeType nodeType : nodes.getNodeList().getNode()) {
            result.add(nodeType.getName());
        }
        return result;
    }

    public static void main(String[] args) {
        ApelonClient production = new ApelonClient();

        System.out.println(production.fetchVocabulary("Dokumentstatus"));

        ApelonClient test = new ApelonClient(
                "http://centoswb02.vgregion.se:8080/vocabularyservice4/VocabularyService?wsdl");

        System.out.println(test.fetchVocabulary("Dokumentstatus"));
    }

}
