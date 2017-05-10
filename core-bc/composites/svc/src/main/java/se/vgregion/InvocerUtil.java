package se.vgregion;

import org.apache.solr.client.solrj.SolrServer;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.ifeed.IFeedServiceImpl;
import se.vgregion.ifeed.service.solr.IFeedResults;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.service.solr.SolrServerFactory;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


/**
 * Run this to test functionality of this svc.
 * Example of start command would be:
 * java -cp "*" se.vgregion.IntegrationTest http://vgas1499.vgregion.se:9090/solr/ifeed *test*
 * The following jar:s have to be present in the class-path.
 * <pre>
 * cglib-2.2.jar              httpcore-4.3.3.jar
 * cglib-nodep-2.2.jar        httpmime-4.2.3.jar
 * commons-lang-2.4.jar       iFeed-core-bc-composite-svc-1.12-SNAPSHOT.jar
 * commons-logging-1.1.1.jar  iFeed-core-bc-composite-types-1.12-SNAPSHOT.jar
 * dao-framework-3.5.jar      noggit-0.5.jar
 * gson-2.3.1.jar             slf4j-api-1.6.1.jar
 * httpclient-4.3.6.jar       solr-solrj-4.5.1.jar
 * </pre>
 */
public class InvocerUtil {

    private final String url;
    private int maxResultCount = 10;

    public InvocerUtil(String url) {
        this.url = url;
    }

    public InvocerUtil() {
        String userHome = System.getProperty("user.home");
        Properties properties = new Properties();
        try (InputStream is = Files.newInputStream(Paths.get(userHome, ".hotell", "ifeed", "config.properties"))) {
            properties.load((is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.url = properties.getProperty("solr.service");
    }

    public IFeedResults findByDocumentName(String solrPattern) {
        return find("dc.title", solrPattern);
    }

    public IFeedResults find(String key, String value) {
        SolrServer solrServer = SolrServerFactory.create(url);
        IFeedService iFeedService = new IFeedServiceImpl() {
            @Override
            public List<FieldsInf> getFieldsInfs() {
                List<FieldsInf> result = new ArrayList<FieldsInf>();
                FieldsInf item = new FieldsInf();
                item.setText(getFieldsConfig());
                result.add(item);
                return result;
            }
        };

        IFeedSolrQuery iFeedSolrQuery = new IFeedSolrQuery(solrServer, iFeedService);
        IFeed feed = new IFeed();
        IFeedFilter filter = new IFeedFilter(value, key);
        feed.addFilter(filter);
        IFeedResults result = iFeedSolrQuery.getIFeedResults(feed, null);

        FieldsInf fieldsInf = new FieldsInf();
        fieldsInf.setText(getFieldsConfig());
        return result;
    }

    public String getFieldsConfig() {
        return "id|name|help|type|filter|apelonKey|inHtmlView\n" +
                "|Dokumentbeskrivning|||||yes\n" +
                "DC.title|Titel (autokomplettering)|Namn på dokument, handling eller annan typ av resurs. (Fritext)|d:text_facet|yes||yes\n" +
                "DC.title.filename|Filnamn, utgivet/publicerat |Namn på utgiven/publicerad fil. Filen är en identisk pdf/a-variant av orginalfilen som laddas upp i dokumentlagret. Gäller konverterbara filformat, t ex Office-dokument (Systemstyrt)|d:text|no||yes\n" +
                "DC.title.filename.native|Filnamn, original|Namn på fil i orginal. Laddas upp och hanteras i källsystemet och kan publiceras till dokumentlagretfilen, där det kopplas till sin identiska utgivna pdf/a-variant om det är ett filformat som konverteras, t ex Office-dokument. (Systemstyrt)|d:text|no||yes\n" +
                "DC.title.alternative|Alternativ titel|Alternativ till den formella titeln. Kan tex vara en förkortning eller översättning av titeln. (Fritext)|d:text|yes||yes\n" +
                "DC.description|Beskrivning|En kort redogörelse för dokumentet, handlingen eller resursens innehåll. (Fritext)|d:mltext|no||yes\n" +
                "DC.type.document|Gruppering av handlingstyper|Grupp av handlingstyper, används för att enklare hitta handlingstyp|d:text_fix|yes|Dokumenttyp VGR|yes\n" +
                "DC.type.document.structure|Dokumentstruktur VGR|Gemensam regional dokumentstruktur för styrande, redovisande och informerande dokument. Byggs successivt med start kring styrande dokument.|d:text_facet|yes|Dokumentstruktur VGR|yes\n" +
                "DC.type.document.structure.id|Dokumentstruktur VGR ID|ID för gemensam regional dokumentstruktur för styrande, redovisande och informerande dokument. Byggs successivt med start kring styrande dokument.|d:text_facet|yes|Dokumentstruktur VGR ID|yes\n" +
                "DC.type.record|Handlingstyp (autokomplettering)|Klassificering av dokumentets innehåll eller typ enligt de dokumenthanteringsplaner som finns inom VGR. ”Ospecificerad” används om handlingstyp saknas.|d:text_facet|yes|Handlingstyp|yes\n" +
                "DC.type.record.id|Handlingstyp ID|Klassificering av dokumentets innehåll eller typ enligt de dokumenthanteringsplaner som finns inom VGR. ”Ospecificerad” används om handlingstyp saknas.|d:text_facet|no|Handlingstyp|no\n" +
                "DC.coverage.hsacode|Verksamhetskod enligt HSA|Kod och klartext för den/de verksamheter dokumentet, handlingen eller resursens innehåll beskriver (HSA-Nationell katalogtjänst)|d:text_fix|yes|Verksamhetskod|yes\n" +
                "DC.coverage.hsacode.id|Verksamhetskod enligt HSA ID|Kod och klartext för den/de verksamheter dokumentet, handlingen eller resursens innehåll beskriver (HSA-Nationell katalogtjänst)|d:text_facet|no|Verksamhetskod|no\n" +
                "dcterms.audience|Målgrupp HoS (autokomplettering)|Den/de målgrupper som dokumentet, handlingen eller resursen riktas till. Gäller Hälso- och Sjukvårdspersonal|d:text_facet|yes||yes\n" +
                "dc.audience|Målgrupp HoS (autokomplettering)|Den/de målgrupper som dokumentet, handlingen eller resursen riktas till. Gäller Hälso- och Sjukvårdspersonal|d:text_facet|no||yes\n" +
                "dcterms.audience.id|Målgrupp HoS|Den/de målgrupper som dokumentet, handlingen eller resursen riktas till. Gäller Hälso- och Sjukvårdspersonal|d:text|no||no\n" +
                "DC.identifier.version|Version|Den version dokumentet, handlingen eller resursen har. (Systemstyrt). Mindre ändringar får vanligen version 0.1, 0.2 osv och större ändringar 1.0, 2.0 osv|d:text|no||yes\n" +
                "DC.contributor.savedby|Sparat av|Inloggad person som sparat dokumentet, handlingen eller resursen i systemet där namn och organisationstillhörighet hämtas från katalogen. (Systemstyrt). |d:text|no||yes\n" +
                "DC.contributor.savedby.id|Sparat av ID|Id för inloggad person som sparat dokumentet, handlingen eller resursen i systemet där namn och organisationstillhörighet hämtas från katalogen. (Systemstyrt). Sätts i Alfresco Lager|d:ldap_value|yes||yes\n" +
                "DC.date.saved|Sparat datum|Tidpunkt då dokumentet, handlingen eller resursen sparades i systemet. (Systemstyrt)|d:datetime|no||yes\n" +
                "vgregion.status.document|Dokumentstatus|Statusmärkning för dokumentet, handlingen eller resursen som beskriver var i processen dokumentet finns. Ex: Arbetsmaterial, remissversion, beslutad, arkiverad|d:text_fix|yes|Dokumentstatus|yes\n" +
                "vgregion.status.document.id|Dokumentstatus|Id för statusmärkning för dokumentet, handlingen eller resursen som beskriver var i processen dokumentet finns. Ex: Arbetsmaterial, remissversion, beslutad, arkiverad|d:text_fix|no|Dokumentstatus|no\n" +
                "VGR.status.document|Dokumentstatus|Statusmärkning för dokumentet, handlingen eller resursen som beskriver var i processen dokumentet finns. Ex: Arbetsmaterial, remissversion, beslutad, arkiverad|d:text_fix|no|Dokumentstatus|yes\n" +
                "VGR.status.document.id|Dokumentstatus|Id för statusmärkning för dokumentet, handlingen eller resursen som beskriver var i processen dokumentet finns. Ex: Arbetsmaterial, remissversion, beslutad, arkiverad|d:text_fix|no|Dokumentstatus|yes\n" +
                "DC.source.documentid|Dokumentid källa|För systemet unikt id för dokumentet. (Systemstyrt)|d:text_facet|yes||yes\n" +
                "DC.source|Länk till dokumentets källa|Länk till dokumentet, handlingen eller resursen i källsystemet. (Systemstyrt)|d:text|no||yes\n" +
                "|Skapat av och för|||||yes\n" +
                "DC.creator|Skapat av|Den eller de personer som skapat eller uppdaterat innehållet, där namn och organisationstillhörighet hämtas från katalogen|d:text|no||yes\n" +
                "DC.creator.id|Skapat av ID|Id för den eller de personer som skapat eller uppdaterat innehållet, där namn och organisationstillhörighet hämtas från katalogen. Sätts i Alfresco Lager|d:ldap_value|yes||yes\n" +
                "DC.creator.freetext|Skapat av (Fritext)|Den eller de personer som skapat eller uppdaterat innehållet och inte finns upplagda i katalogen.|d:text|yes||yes\n" +
                "DC.creator.forunit|Skapat av enhet (autokomplettering)|Den eller de enheter som dokumentet, handlingen eller resursen är skapad av. Förvalt sätts enheten i organisationsstrukturen för den person som angivits i ”Skapat av” (kan ändras) |d:text_facet|no||yes\n" +
                "DC.creator.forunit.id|Skapat av enhet ID (VGR:s organisationsträd)|Id för den eller de enheter som dokumentet, handlingen eller resursen är skapad av. Förvalt sätts enheten i organisationsstrukturen för den person som angivits i ”Skapat av” (kan ändras) |d:ldap_org_value|yes||yes\n" +
                "DC.creator.project-assignment |Skapat av Projekt/Uppdrag/Grupp|Det projekt eller annan gruppering som dokumentet, handlingen eller resursen är skapad av|d:text|yes||yes\n" +
                "|Ansvariga|||||yes\n" +
                "DC.creator.document|Innehållsansvarig/Dokumentansvarig|Den eller de personer som tar ansvar för dokumentets innehåll. Namn och organisationstillhörighet hämtas från katalogen. Kan t. ex vara medicinskt ansvarig, ansvarig linjechef eller uppdragsgivare.|d:text|no||yes\n" +
                "DC.creator.document.id|Innehållsansvarig/Dokumentansvarig ID|Id för den eller de personer som tar ansvar för dokumentets innehåll. Namn och organisationstillhörighet hämtas från katalogen. Kan t. ex vara medicinskt ansvarig, ansvarig linjechef eller uppdragsgivare.  Sätts i Alfresco Lager|d:ldap_value|yes||yes\n" +
                "DC.creator.function|Funktionsansvar|Den funktion eller roll som ansvarar för innehållet i dokumentet. Funktionen eller rollen skrivs in som fritext t.ex: Lex Maria-ansvarig, Tjänsteman i beredskap eller Reception.|d:text|yes||yes\n" +
                "DC.creator.recordscreator|Arkivbildare (autokomplettering)|Ansvarig myndighet (t.ex förvaltning) för dokumentet, handlingen eller resursen sätts enligt KIV. Är systemsatt från den eller de enheter som anges under ”Skapat för enhet|d:text_facet|yes||yes\n" +
                "DC.creator.recordscreator.id|Arkivbildare ID (VGR:s organisationsträd)|ID för ansvarig myndighet (t.ex förvaltning) för dokumentet, handlingen eller resursen sätts enligt KIV. Är systemsatt från den eller de enheter som anges under ”Skapat för enhet|d:ldap_org_value|yes||yes\n" +
                "|Giltighet och tillgänglighet|||||yes\n" +
                "DC.date.validfrom|Giltighetsdatum from|Startdatum för giltigheten för innehållet i dokumentet, handlingen eller resursen. Skilj från ”Tillgänglighetsdatum”|d:date|yes||yes\n" +
                "DC.date.validto|Giltighetsdatum tom|Slutdatum för giltigheten för innehållet i dokumentet, handlingen eller resursens. Skilj från ”Tillgänglighetsdatum”|d:date|yes||yes\n" +
                "DC.date.availablefrom|Tillgänglighetsdatum from|Startdatum för tillgängligheten för dokumentet, handlingen eller resursen. Kan vara avgränsad till viss behörighetsgrupp. Skilj från ”Giltighetsdatum”|d:date|yes||yes\n" +
                "DC.date.availableto|Tillgänglighetsdatum tom|Slutdatum för tillgängligheten för dokumentet, handlingen eller resursen. Kan vara avgränsad till viss behörighetsgrupp. Skilj från ”Giltighetsdatum”.|d:date|yes||yes\n" +
                "DC.date.copyrighted|Copyrightdatum|Tidpunkt för förvärvad copyright/upphovsrätt|d:datetime|no||yes\n" +
                "|Granskat/Godkänt|||||yes\n" +
                "DC.contributor.acceptedby|Godkänt av|Den eller de personer som godkänner innehållet där namn och organisationstillhörighet hämtas från katalogen|d:text|no||yes\n" +
                "DC.contributor.acceptedby.id|Godkänt av ID|Id för den eller de personer som godkänner innehållet där namn och organisationstillhörighet hämtas från katalogen. Sätts i Alfresco Lager|d:ldap_value|yes||yes\n" +
                "DC.contributor.acceptedby.freetext|Godkänt av (Fritext)|Den eller de personer som godkänner innehållet och inte finns upplagda i katalogen|d:text|yes||yes\n" +
                "DC.date.accepted|Godkänt datum|Tidpunkt för godkännande av dokumentets, handlingens eller resursens innehåll. (Systemstyrt)|d:date|no||yes\n" +
                "DC.contributor.acceptedby.role|Godkänt av Egenskap/Roll|I den egenskap/roll som man godkänt, granskat eller lagt upp dokumentet, handlingen eller resursen. (Fritext)|d:text|yes||yes\n" +
                "DC.contributor.acceptedby.unit.freetext|Enhet (Fritext)|En enhet anges för varje person. OBS! Inte dokumentet, handlingen eller resursens enhet.|d:text|no||yes\n" +
                "DC.contributor.controlledby|Granskat av|Den eller de personer som granskar innehållet där namn och organisationstillhörighet hämtas från katalogen|d:text|no||yes\n" +
                "DC.contributor.controlledby.id|Granskat av ID|Id för den eller de personer som granskar innehållet där namn och organisationstillhörighet hämtas från katalogen. Sätts i Alfresco Lager|d:ldap_value|yes||yes\n" +
                "DC.contributor.controlledby.freetext|Granskat av (Fritext)|Den eller de personer som granskar innehållet och inte finns upplagda i katalogen|d:text|yes||yes\n" +
                "DC.date.controlled|Granskningsdatum|Tidpunkt för granskning av dokumentets, handlingens eller resursens innehåll (Systemstyrt)|d:date|no||yes\n" +
                "DC.contributor.controlledby.role|Granskat av Egenskap/Roll|I den egenskap/roll som man godkänt, granskat eller lagt upp dokumentet, handlingen eller resursen. (Fritext)|d:text|yes||yes\n" +
                "DC.contributor.controlledby.unit.freetext|Enhet (Fritext)|En enhet anges för varje person. OBS! Inte dokumentet, handlingen eller resursens enhet.|d:text|no||yes\n" +
                "|Publicerat|||||yes\n" +
                "DC.publisher.forunit|Publicerat för enhet (autokomplettering)|Den eller de enheter som dokumentet, handlingen eller resursen är publicerad för. Förvalt sätts enheten i organisationsstrukturen för den person som angivits i ”Publicerat av” (kan ändras)|d:text_facet|no||yes\n" +
                "DC.publisher.forunit.id|Publicerat för enhet ID (VGR:s organisationsträd)|ID för den eller de enheter som dokumentet, handlingen eller resursen är publicerad för. Förvalt sätts enheten i organisationsstrukturen för den person som angivits i ”Publicerat av” (kan ändras)|d:ldap_org_value|yes||yes\n" +
                "DC.publisher.project-assignment|Publicerat för Projekt/Uppdrag/Grupp|Det projekt eller annan gruppering som dokumentet, handlingen eller resursen är publicerad för. (Fritext)|d:text|yes||yes\n" +
                "DC.rights.accessrights|Publik åtkomsträtt|Hur den publicerade informationen blir tillgänglig: Internet och/eller Intranät|d:text|yes||yes\n" +
                "DC.publisher|Publicerat av|Inloggad person som gjort dokumentet tillgängligt och sökbart på t.ex intranät, internet eller riktat i portal. Namn och organisationstillhörighet hämtas från katalogen|d:text|no||yes\n" +
                "DC.publisher.id|Publicerat av ID|Id för inloggad person som gjort dokumentet tillgängligt och sökbart på t.ex intranät, internet eller riktat i portal. Namn och organisationstillhörighet hämtas från katalogen. Sätts i Alfresco Lager|d:text|yes||yes\n" +
                "DC.date.issued|Publiceringsdatum|Tidpunkt för publicering/utgivning av dokumentet, handlingen eller resursen på t ex internet, intranät eller portalen. (Systemstyrt). Tillgänglighetsintervallet styr om dokumentet, handlingen eller resursen kommer visas efter publiceringen.|d:date|yes||yes\n" +
                "DC.identifier.documentid|Dokumentid till publicerat dokument|För systemet unikt id till det publicerade dokumentet. (Systemstyrt)|d:text_facet|no||no\n" +
                "DC.identifier|Länk till publicerat/utgivet dokument|Länk till det publicerade dokumentet, handlingen eller resursen, pdf/a-utgåva. Se filnamn (Systemstyrt)|d:text|yes||yes\n" +
                "DC.identifier.native|Länk till utgivet orginaldokument|Länk till det publicerade dokumentet, handlingen eller resursen, original-format. Se filnamn. (Systemstyrt)|d:text|no||yes\n" +
                "|Sammanhang|||||yes\n" +
                "DC.type.process.name|Processnamn|Sammanhållen hantering som avgränsas och namnges av verksamheten. (Fritext)|d:text|yes||yes\n" +
                "DC.type.file.process|Ärendetyp|Beskrivning av en serie/följd av åtgärder. (Fritext). Ex supportärende, bidragsansökan|d:text|yes||yes\n" +
                "DC.type.file|Ärende|Enskild åtgärd. (Fritext)|d:text|yes||yes\n" +
                "DC.identifier.diarie.id|Diarienummer|Diarienummer, skrivs in när man vet vilket nummer dokumentet, handlingen eller resursen fått|d:text|yes||yes\n" +
                "DC.type.document.serie|Dokumentserie|Beskrivning av en serie/följd av dokument. För att kunna använda dokumentserie vid utsökning måste exakt samma värde sättas på alla dokument, handlingar eller resurser som ingår i dokumentserien. (Fritext). Ex: återkommande protokoll för viss nämnd |d:text_facet|yes||yes\n" +
                "DC.type.document.id|Referensnummer i dokumentserie|Ett värde som anger det enskilda dokumentet i dokumentserien. Kan uttryckas som nummer, datum osv. (Fritext)|d:text_facet|yes||yes\n" +
                "|Nyckelord|||||yes\n" +
                "DC.subject.keywords|Nyckelord (autokomplettering)|Det eller de enstaka ord som kan beskriva innehållet. Hämtas från kodverk.|d:text_facet|yes||yes\n" +
                "DC.subject.authorkeywords|Författarens nyckelord|Det eller de enstaka ord som kan beskriva innehållet. Sätts av den som skapar eller uppdaterar dokumentet, handlingen eller resursen. (Fritext)|d:text|yes||yes\n" +
                "|Övrigt|||||yes\n" +
                "language|Språk|Det/de språk som innehållet är skrivet på|d:text_fix|yes|Språk|yes\n" +
                "DC.language|Språk|Det/de språk som innehållet är skrivet på|d:text_fix|no|Språk|no\n" +
                "DC.relation.isversionof|Alternativ variant av|Länk (URI) till en version, upplaga eller anpassning av det dokument, handling eller resurs man refererar till. Ex populärversionen länkar till sitt huvuddokument|d:text|no||yes\n" +
                "DC.relation.replaces|Ersätter|Länk (URI) till det dokument, handling eller resurs som ersatts|d:text|no||yes\n" +
                "DC.format.extent|Omfattning|Storlek på eller varaktighet för resursen. Ex för media 90 minuter, för dokumentet 100 sidor. (Fritext).|d:text|no||yes\n" +
                "DC.identifier.location|Fysisk placering|Vart man fysiskt kan hitta dokumentet, handlingen eller resursen. (Fritext)|d:text|yes||yes\n" +
                "DC.type.templatename|Mallnamn|Namn och version på den mall som använts för dokumentet, handlingen eller resursen|d:text|no||yes\n" +
                "DC.format.extent.mimetype|Mimetyp, utgivet/publicerat |Typ av dokument, handling eller resurs för utgiven/publicerad fil. (Systemstyrt). Ex Microsoft Word, PNG image|d:text|no||yes\n" +
                "DC.format.extent.mimetype.native|Mimetyp, original|Typ av dokument, handling eller resurs för original fil. (Systemstyrt). Ex Microsoft Word, PNG image|d:text|no||yes\n" +
                "DC.format.extension|Filändelse, utgivet/publicerat |Format på utgiven/publicerad fil, kan användas för att bestämma vilken mjukvara som är nödvändig för att kunna visa eller använda resursen. (Systemstyrt). Ex doc, png|d:text|yes||yes\n" +
                "DC.format.extension.native|Filändelse, original|Format på original fil kan användas för att bestämma vilken mjukvara som är nödvändig för att kunna visa eller använda resursen. (Systemstyrt). Ex doc, png|d:text|no||yes\n" +
                "DC.identifier.checksum|Kontrollsumma dokument, utgivet/publicerat|Kontrollsumma på utgivet/publicerat dokumentet, handlingen eller resursen. Varje version av dokumentet får ett unikt värde. (Systemstyrt)|d:text|no||yes\n" +
                "DC.identifier.checksum.native|Kontrollsumma dokument, original|Kontrollsumma på orgnial dokumentet, handlingen eller resursen. Varje version av dokumentet får ett unikt värde. (Systemstyrt)|d:text|no||yes\n" +
                "DC.source.origin|Källsystem|Källsystem till dokumentet, handlingen eller resursen. (Systemstyrt)|d:text|yes||yes\n";
    }

    public int getMaxResultCount() {
        return maxResultCount;
    }

    public void setMaxResultCount(int maxResultCount) {
        this.maxResultCount = maxResultCount;
    }

    public static Set<String> getKeysWithMultiValues() {
        InvocerUtil invocerUtil = new InvocerUtil();
        IFeedResults result = invocerUtil.findByDocumentName("*");
        for (Map<String, Object> map : result) {
            System.out.println(getKeysWithMultiValues(map));
            return getKeysWithMultiValues(map);
        }
        return null;
    }

    public static Set<String> getKeysWithMultiValues(Map<String, Object> fromThisSample) {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, Object> entry : fromThisSample.entrySet()) {
            if (entry.getValue() instanceof List) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

}