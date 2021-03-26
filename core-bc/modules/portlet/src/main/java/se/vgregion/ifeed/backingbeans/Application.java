package se.vgregion.ifeed.backingbeans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.faces.component.visit.FullVisitContext;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriTemplate;
import se.vgregion.InvocerUtil;
import se.vgregion.common.utils.BeanMap;
import se.vgregion.common.utils.Json;
import se.vgregion.ifeed.el.AccessGuard;
import se.vgregion.ifeed.formbean.Note;
import se.vgregion.ldap.VgrOrganization;
import se.vgregion.ifeed.repository.UserRepository;
import se.vgregion.ifeed.service.ifeed.Filter;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.metadata.MetadataService;
import se.vgregion.ifeed.service.solr.DateFormatter;
import se.vgregion.ifeed.service.solr.IFeedResults;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.service.solr.client.Response;
import se.vgregion.ifeed.service.solr.client.Result;
import se.vgregion.ifeed.service.solr.client.SolrHttpClient;
import se.vgregion.ifeed.shared.ColumnDef;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.shared.DynamicTableSortingDef;
import se.vgregion.ifeed.types.*;
import se.vgregion.ldap.LdapSupportService;
import se.vgregion.ldap.person.LdapPersonService;
import se.vgregion.ldap.person.Person;

import javax.annotation.PostConstruct;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// import se.vgregion.varnish.VarnishClient;

/**
 * Created by clalu4 on 2014-06-10.
 */
@Component(value = "app")
@Scope("session")
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    static List<String> sofiaSystems = new ArrayList<>(Arrays.asList("SOFIA", "SISOM"));
    @Autowired
    IFeedSolrQuery iFeedSolrQuery;
    long count;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    @Value("${ifeed.metadata.base.url}")
    private String metadataBaseUrl;
    @Value("${ifeed.web.script.json.url}")
    private String webScriptJsonUrl;
    @Autowired
    private IFeedService iFeedService;
    @Autowired
    private LdapPersonService ldapPersonService;
    @Value("#{iFeedModelBean}")
    private IFeedModelBean iFeedModelBean;
    @Value("#{vgrOrganizationsHome}")
    private VgrOrganizationsHome vgrOrganizationsHome;
    @Autowired
    private MetadataService metadataService;
    @Autowired
    private IFeedBackingBean iFeedBackingBean;
    //@Resource(name = "iFeedAtomFeed")
    @Autowired
    private UriTemplate iFeedAtomFeed;
    //@Resource(name = "rssIFeedAtomFeed")
    @Autowired
    private UriTemplate rssIFeedAtomFeed;
    //@Resource(name = "iFeedWebFeed")
    @Autowired
    private UriTemplate iFeedWebFeed;
    //@Resource(name = "iFeedJsonFeed")
    @Autowired
    private UriTemplate iFeedJsonFeed;
    @Autowired
    private UriTemplate iFeedExcelFeed;

    @Autowired
    private UserRepository userRepository;

    private String solrServiceUrl;
    private String selectedFieldInfRootName;
    private VgrGroup group;
    private FieldInf newFilter;
    private VgrDepartment department;
    private String newOwnershipUserId;
    private String newCompositeName;
    private int pageSize = 25;
    private int currentPage = 0;
    private boolean inEditMode;
    private boolean limitOnResultCount;
    private WeakReference<Set<String>> multiValueKeys = new WeakReference<Set<String>>(null);
    @Value("#{filter}")
    private FilterModel filter;
    @Value("#{navigationModelBean}")
    private NavigationModelBean navigationModelBean;
    private FieldsInf fieldsInf;

    //@Value("#{tableDef}")    private TableDefModel tableDef;
    private List<FieldInf> filters;
    private List<VgrDepartment> selectableDepartments;
    private VgrOrganization organizationToChoose;
    @Autowired
    private LdapSupportService ldapOrganizationService;
    private List<IFeed> page;
    private IFeedResults currentResult = new IFeedResults();
    private Map<String, FieldInf> fieldsByNameIndex;
    private List<Map<String, Object>> searchResults;
    private Map<String, FieldInf> filtersMap;
    private IFeedFilter oldFilterVersion;
    @Autowired
    private SolrServer solrServer;
    private CachedUser user;

    {
        try {
            // this.varnishClient = VarnishClient.newVarnishClient();
        } catch (Exception e) {
            // Do nothing. The client might not be needed.
        }
    }

    public Application() {
        super();
    }

    public Application(IFeedService iFeedService) {
        super();
        this.iFeedService = iFeedService;
    }

    private static void cleanFieldsNotFilters(List<FieldInf> filters) {
        filters.removeIf(next -> !next.isFilter());
    }

    public static String urlToPostForm(String url, String id) {
        StringBuilder sb = new StringBuilder();

        String[] urlAndParams = url.split("[?]", 2);
        String[] params = urlAndParams[1].split("[&]");

        sb.append("<form action='");
        sb.append(urlAndParams[0]);
        sb.append("' method=post target='_blank' style='display:none;' id='");
        sb.append(id);
        sb.append("'>");
        for (String paramAndValue : params) {
            String[] paramAndValueSplit = paramAndValue.split(Pattern.quote("="), 2);
            sb.append("<input name='");
            sb.append(paramAndValueSplit[0]);
            sb.append("' value='");
            sb.append(paramAndValueSplit[1]);
            sb.append("' />");
        }

        sb.append("</form>");

        return sb.toString();
    }

    public static Object formatDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return DateFormatter.format((Date) value);
        } else if (value instanceof String) {
            return DateFormatter.formatTextDate((String) value);
        }
        return value;
    }

    /*public static List<Note> toTooltipRows(Map<String, Object> item) {
        if (item == null) {
            return Arrays.asList(new Note("Ingen information", "Metadata för den här posten saknas."));
        }
        String sourceSystem = (String) item.get("vgr:VgrExtension.vgr:SourceSystem");

        if (sourceSystem != null && sofiaSystems.contains(sourceSystem)) {
            return toSofiaTooltipRows(item);
        } else {
            return toAlfrescoBariumTooltipRows(item);
        }
    }*/

    /*public static List<Note> toSofiaTooltipRows(Map<String, Object> item) {
        List<Note> result = new ArrayList<>();

        toTooltipRow(item, "core:ArchivalObject.idType", "N/A", result);
        toTooltipRow(item, "core:ArchivalObject.id", "N/A", result);
        toTooltipRow(item, "core:ArchivalObject.core:CreatedDateTime", "Upprättad datum", result);
        toTooltipRow(item, "core:ArchivalObject.core:PreservationPlanning.action", "Bevarande och gallringsåtgärd", result);
        toTooltipRow(item, "core:ArchivalObject.core:PreservationPlanning.RDA", "Bevarande och gallringsbeslut", result);
        toTooltipRow(item, "revisiondate", "Gallringsdatum", result);
        toTooltipRow(item, "core:ArchivalObject.core:AccessRight", "Åtkomsträtt i slutarkiv", result);
        toTooltipRow(item, "core:ArchivalObject.core:Description", "Dokumentbeskrivning i Sharepoint, Beskrivning i Mellanarkivet", result);
        toTooltipRow(item, "core:ArchivalObject.core:ObjectType", "Handlingstyp", result);
        toTooltipRow(item, "core:ArchivalObject.core:ObjectType.id", "", result);
        toTooltipRow(item, "core:ArchivalObject.core:ObjectType.filePlan", "Dokumenthanteringsplan", result);
        toTooltipRow(item, "core:ArchivalObject.core:Classification.core:Classification.id", "Id på klassificering", result);
        toTooltipRow(item, "core:ArchivalObject.core:Classification.core:Classification.classCode", "Punktnotation på klassificering", result);
        toTooltipRow(item, "core:ArchivalObject.core:Classification.core:Classification.level", "Nivå på klassificering", result);
        toTooltipRow(item, "core:ArchivalObject.core:Classification.core:Classification.name", "Namn på klassificering", result);
        toTooltipRow(item, "core:ArchivalObject.core:Unit", "Rubrik", result);
        toTooltipRow(item, "core:ArchivalObject.core:Unit.refcode", "Signum", result);
        toTooltipRow(item, "core:ArchivalObject.core:Unit.level", "Nivå i arkivförteckningen", result);
        toTooltipRow(item, "core:ArchivalObject.core:Producer", "Myndighet/Arkivbildare", result);
        toTooltipRow(item, "core:ArchivalObject.core:Producer.idType", "", result);
        toTooltipRow(item, "core:ArchivalObject.core:Producer.id", "Myndighetens HSA-ID", result);
        toTooltipRow(item, "vgr:VgrExtension.itemId", "Arkivobjekt-ID", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:SourceSystem", "Källsystem", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:SourceSystem.id", "Källsystem-ID", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:Source.id", "Käll-ID", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:Source.version", "Version i källsystem", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:Source.versionId", "N/A", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:Title", "Rubrik i Sharepoint, Titel i Mellanarkivet", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:AvailableFrom", "Tillgänglig från", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:AvailableTo", "Tillgänglig till", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:RevisedAvailableFrom", "Reviderat tillgänglig från", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:RevisedAvailableTo", "Reviderat tillgänglig till", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:SecurityClass", "Åtkomsträtt", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:RestrictionCode", "Skyddskod", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:LegalParagraph", "Lagparagraf", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:CreatedByUnit", "Upprättad av enhet", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:CreatedByUnit.id", "", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:PublishedForUnit", "Upprättad för enhet", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:PublishedForUnit.id", "", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:CreatedBy", "Upprättad av", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:CreatedBy.id", "Upprättad av (vgrid)", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:CreatedBy.org", "Upprättad av (org)", result);
        toTooltipRow(item, "vgr:VgrExtension.vgr:Tag", "Företagsnyckelord i Sharepoint, Nyckelord i Mellanarkivet", result);
        toTooltipRow(item, "vgrsy:DomainExtension.itemId", "", result);
        toTooltipRow(item, "vgrsy:DomainExtension.domain", "Domännamn", result);
        toTooltipRow(item, "vgrsy:DomainExtension.vgrsy:SubjectClassification", "Regional ämnesindelning", result);
        toTooltipRow(item, "vgrsy:DomainExtension.vgrsy:SubjectLocalClassification", "Egen ämnesindelning", result);
        toTooltipRow(item, "vgrsy:DomainExtension.domain", "", result);
        toTooltipRow(item, "core:ArchivalObject.core:CreatedDateTime", "Skapad datum", result);
        toTooltipRow(item, "core:ArchivalObject.core:PreservationPlanning.action", "Bevarande och gallringsåtgärd", result);
        toTooltipRow(item, "core:ArchivalObject.core:PreservationPlanning.RDA", "Bevarande och gallringsbeslut", result);
        toTooltipRow(item, "revisiondate", "Gallringsdatum", result);
        toTooltipRow(item, "core:ArchivalObject.core:AccessRight", "Åtkomsträtt i slutarkiv", result);
        toTooltipRow(item, "core:ArchivalObject.core:Description", "", result);
        toTooltipRow(item, "core:ArchivalObject.core:CreatedDateTime", "Skapad datum", result);

        if (result.isEmpty()) {
            return Arrays.asList(new Note("Ingen information", "Metadata för den här posten saknas."));
        }
        return result;
    }*/

    /*public static List<Note> toAlfrescoBariumTooltipRows(Map<String, Object> item) {
        List<Note> result = new ArrayList<>();

        //toTooltipRow(item, "dc.title", "Titel", result);
        toTooltipRow(item, "title", "Titel", result);
        toTooltipRow(item, "dc.description", "Beskrivning", result);
        toTooltipRow(item, "dc.creator.document", "Innehållsansvarig", result);
        toTooltipRow(item, "dc.creator.function", "Innehållsansvarig, roll", result);
        toTooltipRow(item, "dc.contributor.acceptedby", "Godkänt av", result);
        toTooltipRow(item, "dc.date.validfrom", "Giltig fr o m", result);
        toTooltipRow(item, "dc.date.validto", "Giltig t o m", result);
        toTooltipRow(item, "dc.type.document.structure", "Dokumentstruktur VGR", result);

        if (result.isEmpty()) {
            return Arrays.asList(new Note("Ingen information", "Metadata för den här posten saknas."));
        }
        return result;
    }*/

    private static void toTooltipRow(Map<String, Object> item, String key, String label, List<Note> notes) {
        Note note = toTooltipRow(item, key, label);
        if (note != null) {
            notes.add(note);
        }
    }

    public static Note toTooltipRow(Map<String, Object> item, String key, String label) {
        Object value = item.get(key);
        if ("undefined".equals(value) || value == null || value.toString().isEmpty()) {
            return null;
        }
        return new Note(label, String.valueOf(value));
    }

    public static String encode(String raw) {
        try {
            return URLEncoder.encode(raw, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void init() {
        if (getCurrentUser() != null) {
            filter.setUserId(getCurrentUser().getId());
        }
        setFilters(iFeedService.getFieldInfs());
        if (filter != null) {
            updateQuery();
        }
    }

    public List<IFeed> updateFilterQuery() {
        setCurrentPage(0);
        return updateQuery();
    }

    public List<IFeed> updateQuery() {
        try {
            //int start = Math.min(getCurrentSpanStart(), result.size());
            //int end = Math.min(getCurrentSpanEnd(), result.size());

            int start = (getCurrentSpanStart());
            int end = (getCurrentSpanEnd());

            long now = System.currentTimeMillis();
            List<IFeed> result = new ArrayList<>(iFeedService.getIFeedsByFilter(filter, start, end));

            return page = result;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getLocalizedMessage()));
            throw new RuntimeException(e);
        }
    }

    public void nextPage() {
        if (currentPage < getMaxPageCount()) {
            currentPage++;
            updateQuery();
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            updateQuery();
        }
    }

    public int getMaxPageCount() {
        return getMaxPageCountImp(page, pageSize);
    }

    int getMaxPageCountImp(Collection list, int pageSize) {
        int r = iFeedService.getLatestFilterQueryTotalCount() / pageSize;
        if (iFeedService.getLatestFilterQueryTotalCount() % pageSize > 0) {
            r++;
        }
        return r;
    }

    public List<IFeed> page() {
        return page;
    }

    private int getCurrentSpanStart() {
        return currentPage * pageSize;
    }

    private int getCurrentSpanEnd() {
        return getCurrentSpanStart() + pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void viewIFeed(Long id) {
        viewIFeedImp(id);
        initFieldsInsideModel();
    }

    private void viewIFeedImp(Long id) {

        final IFeed feed = iFeedService.getIFeed(id);
        newFilter = null;
        iFeedModelBean.copyValuesFromIFeed(feed);

        List<FieldsInf> fieldsInfs = iFeedService.getFieldsInfs();
        if (!fieldsInfs.isEmpty()) {
            this.fieldsInf = fieldsInfs.get(fieldsInfs.size() - 1);
            fieldsInf.putFieldInfInto(iFeedModelBean.getFilters());
        }

        this.filters = iFeedService.getFieldInfs();
        cleanFieldsNotFilters(filters);
        if (!filters.isEmpty()) {
            selectedFieldInfRootName = filters.get(0).getName();
        } else {
            selectedFieldInfRootName = "N/A";
        }
        this.filtersMap = new HashMap<>();
        for (FieldInf root : filters) {
            filtersMap.put(root.getName(), root);
        }

        navigationModelBean.setUiNavigation("VIEW_IFEED");
        setInEditMode(false);

        setFilters(filters);

    }

    private void initFieldsInsideModel() {

        for (IFeedFilter filter : iFeedModelBean.getFilters()) {
            FieldInf field = getFieldsByNameIndex().get(filter.getFilterKey());
            if ("d:ldap_org_value".equals(field.getType())) {
                VgrOrganization organization = vgrOrganizationsHome.findVgrOrganizationByHsaId(filter.getFilterQuery());
                if (organization != null) {
                    filter.setFilterQueryForDisplay(organization.getLabel());
                }
            }
            filter.initFieldInfs(getFieldsByNameIndex().values());
        }

        /*try {
            Files.write(Paths.get(System.getProperty("user.home"), "feed.json"), Json.toJson(feed).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        // Tro to find what list of filters to display initially.
        for (IFeedFilter filter : iFeedModelBean.getFilters()) {
            FieldInf field = getFieldInfById(filter.getFilterKey());
            if (field != null && field.getParent() != null && field.getParent().getParent() != null) {
                selectedFieldInfRootName = field.getParent().getParent().getName();
                break;
            }
        }

    }

    private FieldInf getFieldInfById(String toLookFor) {
        return getFieldInfById(toLookFor, filters);
    }

    private FieldInf getFieldInfById(String toLookFor, Collection<FieldInf> inHere) {
        if (toLookFor == null) {
            return null;
        }
        for (FieldInf item : inHere) {
            if (item == null) {
                continue;
            }
            if (toLookFor.equalsIgnoreCase(item.getId())) {
                return item;
            }
            FieldInf result = getFieldInfById(toLookFor, item.getChildren());
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    CachedUser getUser(HttpServletRequest request) {
        return getCurrentUser(); // TODO Correct? It's not about the requested user?
    }

    public List<String> completeUserName(String incompleteUserName) {
        try {
            List<Person> people = ldapPersonService.getPeople(incompleteUserName + "*", 10);
            List<String> result = new ArrayList<String>();
            for (Person person : people) {
                result.add(person.getUserName());
            }
            return result;

        } catch (Exception e) {
            return Arrays.asList("a", "b", "c");
        }
    }

    public List<Person> completeUser(String incompleteUserName) {
        List<Person> people = ldapPersonService.getPeople(incompleteUserName + "*", 10);
        return people;
    }

    public String fetchLdapPersonFullName(String vgrId) {
        try {
            List<Person> people = ldapPersonService.getPeople(vgrId, 1);
            Person person = people.get(0);
            return (person.getFirstName() + " " + person.getLastName()).trim();
        } catch (Exception e) {
            return "[Inget namn funnet]";
        }
    }

    public String getNewOwnershipUserId() {
        return newOwnershipUserId;
    }

    public void setNewOwnershipUserId(String newOwnershipUserId) {
        this.newOwnershipUserId = newOwnershipUserId;
    }

    public void createNewOwnership() {
        if (newOwnershipUserId != null && !newOwnershipUserId.trim().isEmpty()) {
            boolean addedAsOwnerAllready = false;
            for (Ownership ownership : iFeedModelBean.getOwnerships()) {
                if (ownership.getUserId().equals(newOwnershipUserId)) {
                    addedAsOwnerAllready = true;
                }
            }
            if (!addedAsOwnerAllready) {
                Ownership ownership = new Ownership();
                ownership.setUserId(newOwnershipUserId);
                newOwnershipUserId = "";
                ownership.setIfeedId(iFeedModelBean.getId());
                iFeedModelBean.getOwnershipsAsList().add(ownership);
            }
        }
    }

    @Transactional
    public void update() {
        try {
            IFeed feed = iFeedModelBean.toIFeed();
            IFeed updated = iFeedService.update(feed);
            iFeedModelBean.copyValuesFromIFeed(updated);
            iFeedBackingBean.viewIFeed(updated.getId());
            setInEditMode(false);
            updateQuery();
        } catch (Exception e) {
            LOGGER.error("Trying to update IFeed failed. " + iFeedModelBean == null
                    ? "iFeedModelBean where null." : "iFeedModelBean.id = " + iFeedModelBean.getId(), e);
            throw new RuntimeException(e);
        }
    }

    public List<FieldInf> getFilters() {
        return filters;
    }

    public void setFilters(List<FieldInf> filters) {
        this.filters = filters;
        this.fieldsByNameIndex = new HashMap<String, FieldInf>() {
            @Override
            public FieldInf get(Object key) {
                FieldInf result = super.get(key);
                if (result == null) {
                    result = new FieldInf();
                    result.setName((String) key);
                }
                return result;
            }
        };
        initFieldsByNameIndex(filters);
    }

    void initFieldsByNameIndex(Iterable<FieldInf> fields) {
        initFieldsByNameIndex(fields, new HashSet<>());
    }

    void initFieldsByNameIndex(Iterable<FieldInf> fields, Set<FieldInf> blacklist) {
        for (FieldInf child : fields) {
            if (blacklist.contains(child)) {
                continue;
            }
            blacklist.add(child);
            fieldsByNameIndex.put(child.getId(), child);
            initFieldsByNameIndex(child.getChildren());
        }
    }

    public void cancelNewFilter(FieldInf fieldInf) {
        fieldInf.setExpanded(false);
        fieldInf.setValue("");
        newFilter = null;
        if (oldFilterVersion != null) {
            iFeedModelBean.addFilter(oldFilterVersion);
            oldFilterVersion = null;
        }
        findResultsByCurrentFeedConditions();
    }

    public void addNewFilter(FieldInf fieldInf) {
        if (fieldInf == null || "".equals(fieldInf.getValue())) {
            fieldInf.setExpanded(false);
            return;
        }
        IFeedFilter newFilter = new IFeedFilter(null, fieldInf.getValue(), fieldInf.getId());
        newFilter.setOperator(fieldInf.getOperator());
        newFilter.setFieldInf(fieldInf);
        fieldInf.setExpanded(false);
        iFeedModelBean.addFilter(newFilter);
        oldFilterVersion = null;
        cancelNewFilter(fieldInf);
    }

    public List<String> getVocabulary(String key) {
        List<String> vocabulary = new ArrayList<String>(metadataService.getVocabulary(key));
        return vocabulary;
    }

    public List<VgrDepartment> getVgrDepartments() {
        return iFeedService.getVgrDepartments();
    }

    public FieldInf getNewFilter() {
        return newFilter;
    }

    public void setNewFilter(FieldInf newFilter) {
        oldFilterVersion = null;
        this.newFilter = newFilter;
    }

    public boolean isInEditMode() {
        return inEditMode;
    }

    public void setInEditMode(boolean inEditMode) {
        this.inEditMode = inEditMode;
    }

    public VgrDepartment getDepartment() {
        return department;
    }

    public void setDepartment(VgrDepartment department) {
        this.department = department;
    }

    public void editDepartment(VgrDepartment department) {
        if (department.getId() != null) {
            department = iFeedService.loadDepartment(department.getId());
        }
        setDepartment(department);
        navigationModelBean.setUiNavigation("EDIT_DEPARTMENT");
    }

    public void createNewDepartment() {
        editDepartment(new VgrDepartment());
    }

    @Transactional
    public void saveDepartment(VgrDepartment department) {
        iFeedService.saveDepartment(department);
        cancelDepartment();
        cancelDepartment();
    }

    public void removeFeed(IFeed iFeed) {
        iFeedBackingBean.removeBook(iFeed);
        updateFilterQuery();
    }

    public void saveDepartment() {
        iFeedService.saveDepartment(department);
        cancelDepartment();
    }

    public void cancelDepartment() {
        department = null;
        navigationModelBean.setUiNavigation("ADMIN_DEPARTMENTS");
    }

    public List<VgrDepartment> getDepartments() {
        return iFeedService.getVgrDepartments();
    }

    public void deleteDepartment(VgrDepartment department) {
        iFeedService.deleteDepartmentGroups(department);
        iFeedService.deleteDepartmentEntity(department);
    }

    public boolean mayEditFeed(HttpServletRequest request, IFeed feed) {
        if (iFeedModelBean.getInitalFeed() != null && iFeedModelBean.getInitalFeed().getId().equals(feed.getId())) {
            return AccessGuard.mayEditFeed(getUser(request), iFeedModelBean.getInitalFeed());
        }
        return AccessGuard.mayEditFeed(getUser(request), feed);
    }

    public boolean isSuperUser() {
        return AccessGuard.mayEditAllFeeds(getCurrentUser());
    }

    public CachedUser getCurrentUser() {
        if (user == null) {
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext externalContext = fc.getExternalContext();
            user = userRepository.findUser(externalContext.getUserPrincipal().getName());
            return user;
        } else {
            return user;
        }
    }

    public void findResultsByCurrentFeedConditions() {
        try {
            // this.currentResult = iFeedSolrQuery.getIFeedResults(getIFeedModelBean(), null);
            this.updateSearchResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void removeDepartmentsGroup(VgrGroup item) {
        department.getVgrGroups().remove(item);
    }

    public VgrGroup getGroup() {
        return group;
    }

    public void setGroup(VgrGroup group) {
        this.group = group;
    }

    public void createGroup() {
        //group = new VgrGroup();
        getDepartment().getVgrGroups().add(new VgrGroup());
    }

    public void cancelGroup() {
        group = null;
    }

    public void insertGroup() {
        try {
            department.getVgrGroups().add(group);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        cancelGroup();
    }

    public List<String> newFilterSuggestion(String value) {
        try {
            final Set<IFeedFilter> presentFilters = new HashSet<IFeedFilter>(iFeedModelBean.getFilters());
            IFeedFilter currentDraft = new IFeedFilter(value + "*", newFilter.getId());
            presentFilters.add(currentDraft);
            IFeed feed = new IFeed();
            feed.getFilters().addAll(presentFilters);

            List<String> result = iFeedService.fetchFilterSuggestion(feed, newFilter.getId(), value + "*");
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<VgrDepartment> getSelectableDepartments() {
        return selectableDepartments;
    }

    public void setSelectableDepartments(List<VgrDepartment> selectableDepartments) {

        this.selectableDepartments = selectableDepartments;
    }

    public void putDepartmentAndGroup(VgrDepartment department, VgrGroup group) {
        iFeedModelBean.setDepartment(department);
        iFeedModelBean.setGroup(group);
        selectableDepartments = null;
    }

    public UriTemplate getiFeedAtomFeed() {
        return iFeedAtomFeed;
    }

    public void setiFeedAtomFeed(UriTemplate iFeedAtomFeed) {
        this.iFeedAtomFeed = iFeedAtomFeed;
    }

    public UriTemplate getRssIFeedAtomFeed() {
        return rssIFeedAtomFeed;
    }

    public void setRssIFeedAtomFeed(UriTemplate rssIFeedAtomFeed) {
        this.rssIFeedAtomFeed = rssIFeedAtomFeed;
    }

    public UriTemplate getiFeedWebFeed() {
        return iFeedWebFeed;
    }

    public void setiFeedWebFeed(UriTemplate iFeedWebFeed) {
        this.iFeedWebFeed = iFeedWebFeed;
    }

    public UriTemplate getiFeedJsonFeed() {
        return iFeedJsonFeed;
    }

    public void setiFeedJsonFeed(UriTemplate iFeedJsonFeed) {
        this.iFeedJsonFeed = iFeedJsonFeed;
    }

    public java.net.URI getAtomFeedLink() {
        return iFeedAtomFeed.expand(iFeedModelBean.getId(), iFeedModelBean.getSortField(), iFeedModelBean.getSortDirection());
    }

    public java.net.URI getRssFeedLink() {
        return rssIFeedAtomFeed.expand(iFeedModelBean.getId(), iFeedModelBean.getSortField(), iFeedModelBean.getSortDirection());
    }

    public java.net.URI getWebFeedLink() {
        return iFeedWebFeed.expand(iFeedModelBean.getId(), iFeedModelBean.getSortField(), iFeedModelBean.getSortDirection());
    }

    public java.net.URI getJsonFeedLink() {
        return iFeedJsonFeed.expand(iFeedModelBean.getId(), iFeedModelBean.getSortField(), iFeedModelBean.getSortDirection());
    }

    public String getExcelFeedLink() {
        try {
            String result = String.valueOf(iFeedExcelFeed.expand("ID", iFeedModelBean.getSortField(), iFeedModelBean.getSortDirection()));
            if (isInEditMode()) {
                String json = iFeedModelBean.toJson();
                json = URLEncoder.encode(json, "UTF-8").replaceAll("\\+", "%20");
                result = result.replaceFirst(Pattern.quote("ID"), json);
            } else {
                if (iFeedModelBean != null && iFeedModelBean.getId() != null) {
                    result = result.replaceFirst(Pattern.quote("ID"), iFeedModelBean.getId().toString());
                    result += "&name=" + URLEncoder.encode(iFeedModelBean.getName(), "UTF-8").replaceAll("\\+", "%20");
                }
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getExcelFeedLinkForm() {
        return urlToPostForm(getExcelFeedLink(), "excelForm").replace("\"", "\\\"");
    }

    public void onTypeChangeInColumnDef(TableDefModel tableDefModel, ColumnDef columnDef) {
        FieldInf fi = getFieldsByNameIndex().get(columnDef.getName());
        columnDef.setLabel(fi.getName());
        tableDefModel.toTableMarkup();
    }

    public List<SelectItem> getRootFieldInfs() {
        List<SelectItem> result = new ArrayList<>();
        for (FieldInf fi : getFilters()) {
            if (fi.getParent() != null && fi.getParent().isFilter()) {
                result.add(new SelectItem(fi.getId(), fi.getName()));
            }

        }
        return result;
    }

    public static boolean isBlendingMetadataSpecifications(IFeed feed, List<FieldInf> fields) {
        final Set<IFeedFilter> allFilters = feed.getAllNestedFeedsFlattly()
                .stream().map(f -> f.getFilters()).flatMap(Collection::stream).collect(Collectors.toSet());

        final Set<String> allFilterKeys = allFilters.stream()
                .map(fi -> fi.getFilterKey()).collect(Collectors.toSet());

        int hits = 0;
        for (FieldInf field : fields) {
            if (field.isFilter()) {
                final MutableBoolean hit = new MutableBoolean(false);
                field.visit(item -> {
                    if (allFilterKeys.contains(item.getId())) {
                        hit.setValue(true);
                    }
                });
                if (hit.getValue().equals(true)) {
                    hits++;
                }
            }
        }

        return hits > 1;
    }

    static boolean hasAnyCounterpartsValues(List<FieldInf> fromThese) {
        MutableBoolean result = new MutableBoolean(false);
        for (FieldInf item : fromThese) {
            item.visit(i -> {
                if (!i.getCounterparts().isEmpty()) {
                    result.setValue(true);
                }
            });
        }
        return (boolean) result.getValue();
    }

    static List<FieldInf> getFieldSuitableForSorting(IFeed forThat, List<FieldInf> fromThese) {
        if (isBlendingMetadataSpecifications(forThat, fromThese) && hasAnyCounterpartsValues(fromThese)) {
            FieldInf oneWithBlendedFields = new FieldInf();
            oneWithBlendedFields.setName("Fält");
            oneWithBlendedFields.getChildren().addAll(getFilterFieldsForBothMetadataSets(fromThese));
            FieldInf oneWithBlendedFieldsRoot = new FieldInf();
            oneWithBlendedFieldsRoot.setName("Alfresco-Barium/SOFIA");
            oneWithBlendedFieldsRoot.getChildren().add(oneWithBlendedFields);
            return Arrays.asList(oneWithBlendedFieldsRoot);
        }

        return getMetadataSetAlreadyInFeed(forThat, fromThese);
    }

    static List<FieldInf> getMetadataSetAlreadyInFeed(IFeed forThat, List<FieldInf> fromThese) {
        Set<String> ifeedFilterNames = new HashSet<>();
        for (IFeed iFeed : forThat.getAllNestedFeedsFlattly()) {
            for (IFeedFilter filter : iFeed.getFilters()) {
                ifeedFilterNames.add(filter.getFilterKey());
            }
        }

        List<FieldInf> result = new ArrayList();

        boolean found = false;
        for (FieldInf parent : fromThese) {
            FieldInf root = parent.toDetachedCopy();
            for (FieldInf child : parent.getChildren()) {
                FieldInf group = child.toDetachedCopy();

                List<FieldInf> items = new ArrayList<>();

                for (FieldInf grandChild : child.getChildren()) {
                    if (grandChild.isFilter()) {
                        items.add(grandChild.toDetachedCopy());
                        if (ifeedFilterNames.isEmpty() || ifeedFilterNames.contains(grandChild.getId())) {
                            found = true;
                        }
                    } else {
                        group.getChildren().remove(grandChild);
                    }
                }
                group.getChildren().addAll(items);
                root.getChildren().add(group);
            }
            if (found) {
                result.add(root);
                found = false;
            }
        }

        for (FieldInf fieldInf : result) {
            fieldInf.init();
        }

        return result;
    }

    public List<FieldInf> getFieldSuitableForSorting() {
        return getFieldSuitableForSorting(getIFeedModelBean(), getFilters());
    }

    public List<FieldInf> getFieldSuitableForSorting_old() {
        Set<String> ifeedFilterNames = new HashSet<>();
        for (IFeed iFeed : iFeedModelBean.getAllNestedFeedsFlattly()) {
            for (IFeedFilter filter : iFeed.getFilters()) {
                ifeedFilterNames.add(filter.getFilterKey());
            }
        }

        List<FieldInf> result = new ArrayList();
        if (filters == null) {
            this.filters = new ArrayList<>(iFeedService.getFieldInfs());
        }

        boolean found = false;
        for (FieldInf parent : getFilters()) {
            FieldInf root = parent.toDetachedCopy();
            for (FieldInf child : parent.getChildren()) {
                FieldInf group = child.toDetachedCopy();

                List<FieldInf> items = new ArrayList<>();

                for (FieldInf grandChild : child.getChildren()) {
                    if (grandChild.isInHtmlView()) {
                        items.add(grandChild.toDetachedCopy());
                        if (ifeedFilterNames.isEmpty() || ifeedFilterNames.contains(grandChild.getId())) {
                            found = true;
                        }
                    }
                }
                group.getChildren().addAll(items);
                root.getChildren().add(group);
            }
            if (found) {
                result.add(root);
                found = false;
            }
        }

        for (FieldInf fieldInf : result) {
            fieldInf.init();
        }

        return result;
    }

    static Set<String> toAllFieldsSet(FieldInf fieldInf) {
        Set<String> result = new TreeSet<>(fieldInf.getCounterparts());
        result.add(fieldInf.getId());
        return result;
    }

    public static List<FieldInf> getFilterFieldsForBothMetadataSets(List<FieldInf> fromThese) {
        List<FieldInf> result = new ArrayList<>();

        Map<Set<String>, List<FieldInf>> id2fields = new HashMap<>() {
            @Override
            public List<FieldInf> get(Object key) {
                if (!containsKey(key)) {
                    put((Set<String>) key, new ArrayList<>());
                }
                return super.get(key);
            }
        };

        for (FieldInf fieldInf : fromThese) {
            if (fieldInf.isFilter()) {
                fieldInf.visit(item -> {
                    if (item.getId() != null && !item.getId().trim().isEmpty() && !item.getCounterparts().isEmpty()) {
                        id2fields.get(toAllFieldsSet(item)).add(item);
                    }
                });
            }
        }

        for (Set<String> key : id2fields.keySet()) {
            FieldInf first = id2fields.get(key).get(0);
            result.add(first);
        }

        return result;
    }


    public List<SelectItemGroup> fieldInfsAsSelectItemGroups() {
        List<SelectItemGroup> result = new ArrayList<>();

        for (FieldInf top : getFieldSuitableForSorting()) {

            for (FieldInf field : top.getChildren()) {
                List<SelectItem> items = new ArrayList<>();
                SelectItemGroup g = new SelectItemGroup(field.getName() + " (" + top.getName() + ")");
                for (FieldInf leaf : field.getChildren()) {
                    items.add(new SelectItem(leaf.getId(), leaf.getName()));
                }
                g.setSelectItems(items.toArray(new SelectItem[items.size()]));
                result.add(g);
            }
        }
        return result;
        // return fieldInfsAsSelectItemGroupsOriginal();
    }

    public List<SelectItemGroup> fieldInfsAsSelectItemGroupsOriginal() {
        Set<String> ifeedFilterNames = new HashSet<>();
        for (IFeed iFeed : iFeedModelBean.getAllNestedFeedsFlattly()) {
            for (IFeedFilter filter : iFeed.getFilters()) {
                ifeedFilterNames.add(filter.getFilterKey());
            }
        }

        Set<String> blackList = getMultiValueKeys();
        List<SelectItemGroup> result = new ArrayList();
        if (filters == null) {
            this.filters = new ArrayList<>(iFeedService.getFieldInfs());
        }
        for (FieldInf parent : getFilters()) {

            boolean found = false;

            for (FieldInf child : parent.getChildren()) {
                SelectItemGroup group = new SelectItemGroup(child.getName() + " (" + parent.getName() + ")");
                List<SelectItem> items = new ArrayList<SelectItem>();

                for (FieldInf grandChild : child.getChildren()) {
                    if (grandChild.isInHtmlView()) {
                        if (!blackList.contains(grandChild.getId())) {
                            items.add(new SelectItem(grandChild.getId(), grandChild.getName()));
                        }
                        if (ifeedFilterNames.isEmpty() || ifeedFilterNames.contains(grandChild.getId())) {
                            found = true;
                        }
                    }
                }

                if (found) {
                    group.setSelectItems(items.toArray(new SelectItem[items.size()]));
                    result.add(group);
                }
            }

        }
        return result;
    }

    public List<FieldInf> getInHtmlFilters() {
        List<FieldInf> fieldInfs = getFilters();
        fieldInfs = clone(fieldInfs);

        for (FieldInf baseItem : fieldInfs) {
            List<FieldInf> children = new ArrayList<FieldInf>(baseItem.getChildren());
            for (int i = children.size() - 1; i >= 0; i--) {
                FieldInf childItem = children.get(i);
                if (!childItem.isInHtmlView()) {
                    baseItem.getChildren().remove(i);
                }
            }
        }

        return fieldInfs;
    }

    private <T> T clone(T thisInstance) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(thisInstance);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void showMyFeeds() {
        filter.setCreatorName(getCurrentUser().getId());
    }

    public void goToAddIfeed() {
        iFeedModelBean.clearBean();
        navigationModelBean.setUiNavigation("ADD_IFEED");
        setInEditMode(true);
    }

    public void setFilterUserId(String id) {
        filter.setUserId(id);
        updateFilterQuery();
    }

    public void clearFilter() {
        filter.clear();
        updateFilterQuery();
    }

    public String fixDublicateIdProblem() {
        FacesContext.getCurrentInstance().getViewRoot().setTransient(true);
        return "";
    }

    public String traverse() {
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot root = context.getViewRoot();
        final Set<String> passed = new TreeSet<String>();
        root.visitTree(new FullVisitContext(context), new VisitCallback() {
            @Override
            public VisitResult visit(VisitContext context, UIComponent component) {
                if (passed.contains(component.getId() + "")) {
                    passed.add(component.getId() + "(DUBBEL)");
                } else {
                    passed.add(component.getId() + "");
                }

                return VisitResult.ACCEPT;
            }
        });

        return passed.toString();
    }

    public long nextCount() {
        return count++;
    }

    public UIComponent findComponent(final String id) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot root = context.getViewRoot();
        final UIComponent[] found = new UIComponent[1];
        final List<String> idPath = new ArrayList<String>();
        root.visitTree(new FullVisitContext(context), new VisitCallback() {
            @Override
            public VisitResult visit(VisitContext context, UIComponent component) {
                if (component.getId().equals(id)) {
                    found[0] = component;
                    return VisitResult.COMPLETE;
                }
                return VisitResult.ACCEPT;
            }
        });
        String longId = findComponentLongId(found[0]);

        return found[0];
    }

    public String findComponentLongId(UIComponent component) {
        if (component.getParent() == null) {
            return "";
        }
        return findComponentLongId(component.getParent()) + ":" + component.getId();
    }

    public IFeedModelBean getIFeedModelBean() {
        return iFeedModelBean;
    }

    public IFeedResults getCurrentResult() {
        return currentResult;
    }

    public void setCurrentResult(IFeedResults currentResult) {
        this.currentResult = currentResult;
    }

    public String getMetadataBaseUrl() {
        return metadataBaseUrl;
    }

    public void setMetadataBaseUrl(String metadataBaseUrl) {
        this.metadataBaseUrl = metadataBaseUrl;
    }

    public String toStringDate(Object date) {
        if (date instanceof Date) {
            return format.format(date);
        }
        String s = (String) date;
        s = s.substring(0, 10);
        return s;
    }

    public Map<String, FieldInf> getFieldsByNameIndex() {
        return fieldsByNameIndex;
    }

    public String getWebScriptJsonUrl() {
        return webScriptJsonUrl;
    }

    public void setWebScriptJsonUrl(String webScriptJsonUrl) {
        this.webScriptJsonUrl = webScriptJsonUrl;
    }

    public void editFilterValue(IFeedFilter filter) {
        if (iFeedModelBean.getFilters().contains(filter)) {
            newFilter = filter.getFieldInf();
            if (newFilter == null) {
                newFilter = fieldsByNameIndex.get(filter.getFilterKey());
            }
            newFilter.setValue(filter.getFilterQuery());
            newFilter.setOperator(filter.getOperator());
            if (filter != null && (filter.getFieldInf() == null || !"d:ldap_org_value".equals(filter.getFieldInf().getType()))) {
                iFeedModelBean.removeFilter(filter);
                oldFilterVersion = filter;
            }
        } else {
            throw new RuntimeException();
        }
    }

    public boolean isLimitOnResultCount() {
        return limitOnResultCount;
    }

    public void setLimitOnResultCount(boolean limitOnResultCount) {
        this.limitOnResultCount = limitOnResultCount;
    }

    @Transactional
    public void putFlowInFeed(DynamicTableDef dynamicTableDef) {
        BeanMap bm = new BeanMap(dynamicTableDef);
        boolean found = false;
        for (DynamicTableDef item : iFeedModelBean.getDynamicTableDefs()) {
            if (dynamicTableDef.getId() != null && (item.getId() == dynamicTableDef.getId() || dynamicTableDef.getId().equals(item.getId()))) {
                found = true;
                new BeanMap(item).putAllApplicable(bm);
                item.setColumnDefs(dynamicTableDef.getColumnDefs());
                iFeedService.save(item);
                break;
            }
        }
        if (!found) {
            DynamicTableDef instance = new DynamicTableDef();
            new BeanMap(instance).putAllApplicable(bm);
            instance.setColumnDefs(new ArrayList<>(dynamicTableDef.getColumnDefs()));
            instance.setExtraSorting(new ArrayList<>(dynamicTableDef.getExtraSorting()));
            iFeedModelBean.getDynamicTableDefs().add(instance);
            instance.setIfeed(iFeedModelBean.getInitalFeed());
            instance.setFkIfeedId(iFeedModelBean.getInitalFeed().getId());
            for (DynamicTableSortingDef extraSort : instance.getExtraSorting()) {
                extraSort.setTableDef(instance);
            }
            iFeedService.save(instance);
        } else {
            DynamicTableDef dtd = new DynamicTableDef();
            new BeanMap(dtd).putAllApplicable(bm);
            iFeedService.save(dtd);
        }

        navigationModelBean.setUiNavigation("VIEW_IFEED");

    }

    public void copy() {

        setInEditMode(true);
    }

    public void removeFlow(DynamicTableDef dynamicTableDef) {
        this.getIFeedModelBean().getDynamicTableDefs().remove(dynamicTableDef);
    }

    // private VarnishClient varnishClient;

    public List<String> completeFeedName(String incompleteName) {
        Filter sample = new FilterModel();
        sample.setName(incompleteName);
        List<IFeed> result = iFeedService.getIFeedsByFilter(sample, 0, 10);
        List<String> names = new ArrayList<String>();
        for (IFeed iFeed : result) {
            names.add(iFeed.getName().trim() + " (" + iFeed.getId() + ")");
            //iFeed.toJson();
        }

        System.out.println();
        for (String name : names) {
            System.out.println(name);
        }
        System.out.println();

        return names;
    }

    @Transactional
    public void createNewComposite() {
        if (newCompositeName != null && !newCompositeName.isEmpty()) {
            Filter filter = new FilterModel();
            String[] parts = newCompositeName.split("\\(|\\)");
            String idPart = parts[parts.length - 1];
            //filter.setName(newCompositeName);
            filter.setId(Long.parseLong(idPart));
            //List<IFeed> nf = iFeedService.getIFeedsByFilter(filter, 0, 1);
            IFeed otherFeed = iFeedService.getIFeed(Long.parseLong(idPart));

            //if (!nf.isEmpty()) {
            if (otherFeed != null) {
        /*IFeed first = iFeedService.getIFeed(nf.get(0).getId());
        first.toJson();
        iFeedModelBean.getComposites().add(first);*/
                Json.toJson(otherFeed);
                // otherFeed.toJson();
                iFeedModelBean.getComposites().add(otherFeed);
            }
            newCompositeName = "";
        }
    }

    public String getNewCompositeName() {
        return newCompositeName;
    }

    public void setNewCompositeName(String newCompositeName) {
        this.newCompositeName = newCompositeName;
    }

    private String getPartOfTextRepresentation(IFeed that) {
        return getTextRepresentation(that.getPartOf());
    }

    private String getCompositesTextRepresentation(IFeed that) {
        return getTextRepresentation(that.getComposites());
    }

    private String getTextRepresentation(List<IFeed> that) {
        List<String> composites = new ArrayList<String>();
        for (IFeed peers : that) {
            composites.add(peers.getName() + " (" + peers.getId() + ")");
        }
        return Filter.join(composites, ", ");
    }

    public String getTextCompositesWarning(IFeed feed) {
        StringBuilder sb = new StringBuilder();
        //feed = iFeedService.getIFeed(feed.getId());
        if (!feed.getComposites().isEmpty()) {
            sb.append("\\n* Flöden som används av det här flödet: ");
            sb.append(getCompositesTextRepresentation(feed));
        }
        if (!feed.getPartOf().isEmpty()) {
            sb.append("\\n* Flöden som använder det här flödet: ");
            sb.append(getPartOfTextRepresentation(feed));
        }
        return sb.toString();
    }

    public void cancelEdit() {
        if (iFeedModelBean.getId() != null) {
            viewIFeed(iFeedModelBean.getId());
            setInEditMode(false);
        }
    }

    public Set<String> getMultiValueKeys() {
        if (multiValueKeys.get() == null) {
            multiValueKeys = new WeakReference<>(InvocerUtil.getKeysWithMultiValues(iFeedService.getFieldsInfs()));
        }
        return multiValueKeys.get();
    }

    public String uncache(String thatUrl) {
/*        LOGGER.debug("How many times is this called?");
        if (varnishClient == null) {
            return thatUrl;
        }
        varnishClient.clear(thatUrl);*/
        return thatUrl;
    }

    public String uncacheJson(String thatUrl) {

        /*LOGGER.debug("How many times is this called?");
        if (varnishClient == null) {
            return thatUrl;
        }
        if (getIFeedModelBean().getFilters().isEmpty()) {
            return thatUrl;
        }
        varnishClient.clearJson(thatUrl);*/
        return thatUrl;
    }

    public String urlEncode(String s) throws UnsupportedEncodingException {
        if (!isInEditMode()) {
            return getIFeedModelBean().getId() + "";
        }
        return URLEncoder.encode(s, "UTF-8");
    }

    public String join(String... frag) {
        StringBuilder sb = new StringBuilder();
        for (String s : frag) {
            sb.append(s);
        }
        return sb.toString();
    }

    public void copyAndPersistFeed(HttpServletRequest request, Long withThatKey) {
        CachedUser user = getUser(request);
        Long clone = iFeedService.copyAndPersistFeed(withThatKey, user.getId()).getId();

        viewIFeed(clone);
        setInEditMode(true);
    }

    public void updateSearchResults() {
        if (searchResults == null) {
            searchResults = new ArrayList<>();
        }
        if (iFeedModelBean.toQuery(client.fetchFields()).isEmpty()) {
            searchResults.clear();
        } else {
            updateSearchResults(iFeedModelBean);
        }
    }

    static SolrHttpClient client = SolrHttpClient.newInstanceFromConfig();

    static List<Map<String, Object>> findResultToUpdateSearchResults(IFeed retrievedFeed, List<FieldInf> fields) {
        List<Map<String, Object>> result = null;

        Result fromSolr = client.query(
                retrievedFeed.toQuery(client.fetchFields()), 0, 501, null, null,
                FieldInf.toIdsList(
                        fields,
                        "title", "dc.date.issued",
                        "core:ArchivalObject.core:CreatedDateTime", "id", "url", "dc.date.issued"
                )
        );
        if (fromSolr != null && fromSolr.getResponse() != null && fromSolr.getResponse().getDocs() != null) {
            Response response = fromSolr.getResponse();
            result = response.getDocs();
        } else {
            System.out.println("No response from solr: " +
                    new GsonBuilder().setPrettyPrinting().create().toJson(fromSolr)
            );
        }

        return result;
    }


    public void updateSearchResults(IFeed retrievedFeed) {
        if (retrievedFeed.toQuery(client.fetchFields()).trim().equals("")) {
            this.searchResults = new ArrayList<>();
            return;
        }

        this.searchResults = findResultToUpdateSearchResults(retrievedFeed, getFilters());
    }

    private void formatDate(String withThatKey, Map<String, Object> insideHere) {
        String issued = (String) insideHere.get(withThatKey);
        if (issued != null && issued.contains("T")) {
            String[] parts = issued.split(Pattern.quote("T"));
            issued = parts[0];
        }
        insideHere.put(withThatKey, issued);
    }

    private void formatDates(Map<String, Object> within) {
        formatDate("dc.date.validfrom", within);
        formatDate("dc.date.issued", within);
        formatDate("dc.date.validto", within);
    }

    public List<Map<String, Object>> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Map<String, Object>> searchResults) {
        this.searchResults = searchResults;
    }

    public String getSelectedFieldInfRootName() {
        return selectedFieldInfRootName;
    }

    public void setSelectedFieldInfRootName(String selectedFieldInfRootName) {
        this.selectedFieldInfRootName = selectedFieldInfRootName;
    }

    static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void onSelectedFieldInfRootNameChange(ValueChangeEvent e) {
        FieldInf item = getFiltersMap().get(e.getNewValue());
        System.out.println("onSelectedFieldInfRootNameChange");
        System.out.println(gson.toJson(e));
        System.out.println(
                gson.toJson(item.getDefaultFilters())
        );
        if (item != null && item.getDefaultFilters() != null) {
            getIFeedModelBean().getFilters().addAll(item.getDefaultFilters());
            /*getIFeedModelBean().setFilters(new HashSet<>(
                    getIFeedModelBean().getFilters()
            ));*/
            getIFeedModelBean().getFiltersAsList().addAll(item.getDefaultFilters());
            System.out.println("ListCount: " + getIFeedModelBean().getFiltersAsList().size());
            System.out.println("SetCount: " + getIFeedModelBean().getFilters().size());
            //b FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(":filtersList");
        }
        if (e.getOldValue() != null && !e.getOldValue().toString().trim().equals("")) {
            item = getFiltersMap().get(e.getOldValue());
            if (item != null && item.getDefaultFilters() != null)
                for (IFeedFilter df : item.getDefaultFilters()) {
                    for (IFeedFilter filterInBean : iFeedModelBean.getFilters()) {
                        if (isKeyAndQueryEquals(df, filterInBean)) {
                            iFeedModelBean.getFilters().remove(filterInBean);
                            iFeedModelBean.getFiltersAsList().remove(filterInBean);
                        }
                    }
                }

            /*for (IFeedFilter filter : new ArrayList<IFeedFilter>(iFeedModelBean.getFilters())) {
                if (filter.getFilterKey().equals(e.getOldValue()) && filter.getFilterKey().equals(e.getOldValue())) {
                    System.out.println("Removes: " + filter);
                    iFeedModelBean.getFiltersAsList().remove(filter);
                    iFeedModelBean.getFilters().remove(filter);
                }
            }*/
        }
    }

    public boolean isFilter(IFeedFilter iff) {
        final MutableBoolean result = new MutableBoolean(false);
        for (FieldInf fi : filters) {
            fi.visit(item -> {
                if (iff.getFilterKey().equals(item.getId())) {
                    if (item.isFilter()) {
                        result.setValue(true);
                    }
                }
            });
        }
        return result.isTrue();
    }

    private static boolean isKeyAndQueryEquals(IFeedFilter f1, IFeedFilter f2) {
        return f1.getFilterKey().equals(f2.getFilterKey()) && f1.getFilterQuery().equals(f2.getFilterQuery());
    }

    public Map<String, FieldInf> getFiltersMap() {
        return filtersMap;
    }

    public void setFiltersMap(Map<String, FieldInf> filtersMap) {
        this.filtersMap = filtersMap;
    }

    public void setiFeedModelBean(IFeedModelBean iFeedModelBean) {
        this.iFeedModelBean = iFeedModelBean;
    }

    public static String toCssFriendlyFormat(String fromThat) {
        if (fromThat == null || fromThat.trim().equals("")) {
            return "";
        }
        return fromThat.replaceAll("[^a-ö0-9]", "-");
    }

    @Deprecated
    public String getApplicationUri() {
        try {
            FacesContext ctxt = FacesContext.getCurrentInstance();
            ExternalContext ext = ctxt.getExternalContext();
            URI uri = new URI(ext.getRequestScheme(),
                    null, ext.getRequestServerName(), ext.getRequestServerPort(),
                    ext.getRequestContextPath(), null, null);
            return uri.toASCIIString();
        } catch (URISyntaxException e) {
            throw new FacesException(e);
        }
    }

    public String getHost() {
        FacesContext ctxt = FacesContext.getCurrentInstance();
        ExternalContext ext = ctxt.getExternalContext();
        return ext.getRequestServerName();
    }

}
