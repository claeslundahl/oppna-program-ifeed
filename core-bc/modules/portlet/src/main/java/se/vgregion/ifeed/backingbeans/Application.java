package se.vgregion.ifeed.backingbeans;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.sun.faces.component.visit.FullVisitContext;
import org.apache.commons.beanutils.BeanMap;
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
import se.vgregion.common.utils.Json;
import se.vgregion.ifeed.el.AccessGuard;
import se.vgregion.ifeed.formbean.Note;
import se.vgregion.ifeed.formbean.VgrOrganization;
import se.vgregion.ifeed.service.ifeed.Filter;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.metadata.MetadataService;
import se.vgregion.ifeed.service.solr.IFeedResults;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.shared.ColumnDef;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.shared.DynamicTableSortingDef;
import se.vgregion.ifeed.types.*;
import se.vgregion.ldap.LdapSupportService;
import se.vgregion.ldap.person.LdapPersonService;
import se.vgregion.ldap.person.Person;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.portlet.PortletRequest;
import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static se.vgregion.common.utils.CommonUtils.getEnum;

// import se.vgregion.varnish.VarnishClient;

/**
 * Created by clalu4 on 2014-06-10.
 */
@Component(value = "app")
@Scope("session")
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Value("${ifeed.metadata.base.url}")
    private String metadataBaseUrl;

    @Value("${ifeed.web.script.json.url}")
    private String webScriptJsonUrl;

    @Autowired
    private IFeedService iFeedService;
    @Autowired
    private ResourceLocalService resourceLocalService;
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
    @Autowired
    IFeedSolrQuery iFeedSolrQuery;
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

    private List<FieldInf> filters;

    private List<VgrDepartment> selectableDepartments;

    private VgrOrganization organizationToChoose;

    //@Value("#{tableDef}")    private TableDefModel tableDef;

    @Autowired
    private LdapSupportService ldapOrganizationService;
    private List<IFeed> page;
    private IFeedResults currentResult = new IFeedResults();
    private Map<String, FieldInf> fieldsByNameIndex;
    private List<Map<String, Object>> searchResults;
    private Map<String, FieldInf> filtersMap;

    public Application() {
        super();
    }

    public Application(IFeedService iFeedService) {
        super();
        this.iFeedService = iFeedService;
    }

    @PostConstruct
    public void init() {
        if (getCurrentUser() != null) {
            filter.setUserId(getCurrentUser().getScreenName());
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

    public void viewIFeed(Long id) throws PortalException, SystemException {
        final IFeed feed = iFeedService.getIFeed(id);
        newFilter = null;
        iFeedModelBean.copyValuesFromIFeed(feed);

        List<FieldsInf> fieldsInfs = iFeedService.getFieldsInfs();
        if (!fieldsInfs.isEmpty()) {
            this.fieldsInf = fieldsInfs.get(fieldsInfs.size() - 1);
            fieldsInf.putFieldInfInto(iFeedModelBean.getFilters());
        }

        this.filters = iFeedService.getFieldInfs();
        selectedFieldInfRootName = filters.get(0).getName();
        this.filtersMap = new HashMap<>();
        for (FieldInf root : filters) {
            filtersMap.put(root.getName(), root);
        }

        navigationModelBean.setUiNavigation("VIEW_IFEED");
        setInEditMode(false);
        //findResultsByCurrentFeedConditions();

        setFilters(filters);

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

        try {
            Files.write(Paths.get(System.getProperty("user.home"), "feed.json"), Json.toJson(feed).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    User getUser(PortletRequest request) throws PortalException, SystemException {
        return PortalUtil.getUser(request);
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
            return person.getFirstName() + " " + person.getLastName();
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
        /*for (FieldInf parent : filters) {
            fieldsByNameIndex.put(parent.getId(), parent);
            if (!parent.getChildren().isEmpty()) {
                for (FieldInf child : parent.getChildren()) {
                    fieldsByNameIndex.put(child.getId(), child);
                }
            }
        }*/
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

    public void removeFeed(IFeed iFeed) throws SystemException, PortalException {
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

    public boolean mayEditFeed(PortletRequest request, IFeed feed) throws SystemException, PortalException {
        if (iFeedModelBean.getInitalFeed() != null && iFeedModelBean.getInitalFeed().getId().equals(feed.getId())) {
            return AccessGuard.mayEditFeed(getUser(request), iFeedModelBean.getInitalFeed());
        }
        return AccessGuard.mayEditFeed(getUser(request), feed);
    }

    public boolean isSuperUser() {
        try {
            return AccessGuard.mayEditAllFeeds(getCurrentUser());
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }

    public User getCurrentUser() {
        User u = null;
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext externalContext = fc.getExternalContext();
        if (externalContext.getUserPrincipal() == null) {

        } else {
            Long id = Long.parseLong(externalContext.getUserPrincipal().getName());
            try {
                u = UserLocalServiceUtil.getUserById(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return u;
    }

    public void findResultsByCurrentFeedConditions() {
        this.currentResult = iFeedSolrQuery.getIFeedResults(getIFeedModelBean(), null);
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
        final Set<IFeedFilter> presentFilters = new HashSet<IFeedFilter>(iFeedModelBean.getFilters());
        IFeedFilter currentDraft = new IFeedFilter(value + "*", newFilter.getId());
        presentFilters.add(currentDraft);
        IFeed feed = new IFeed() {
            @Override
            public Set<IFeedFilter> getFilters() {
                return presentFilters;
            }
        };

        return iFeedService.fetchFilterSuggestion(feed, newFilter.getId());
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

    public void onTypeChangeInColumnDef(TableDefModel tableDefModel, ColumnDef columnDef) {
        FieldInf fi = getFieldsByNameIndex().get(columnDef.getName());
        columnDef.setLabel(fi.getName());
        tableDefModel.toTableMarkup();
    }

    public List<SelectItem> getRootFieldInfs() {
        List<SelectItem> result = new ArrayList<>();
        for (FieldInf fi : getFilters()) {
            result.add(new SelectItem(fi.getId(), fi.getName()));
        }
        return result;
    }

    public List<SelectItemGroup> fieldInfsAsSelectItemGroups() {
        Set<String> blackList = getMultiValueKeys();
        List<SelectItemGroup> result = new ArrayList();
        if (filters == null) {
            this.filters = iFeedService.getFieldInfs();
        }
        for (FieldInf parent : getFilters()) {

            for (FieldInf child : parent.getChildren()) {
                SelectItemGroup group = new SelectItemGroup(child.getName() + " (" + parent.getName() + ")");
                List<SelectItem> items = new ArrayList<SelectItem>();

                for (FieldInf grandChild : child.getChildren()) {
                    if (grandChild.isInHtmlView()) {
                        if (!blackList.contains(grandChild.getId())) {
                            items.add(new SelectItem(grandChild.getId(), grandChild.getName()));
                        }
                    }
                }

                group.setSelectItems(items.toArray(new SelectItem[items.size()]));
                result.add(group);
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
        filter.setCreatorName(getCurrentUser().getScreenName());
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

    long count;

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

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

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

/*
    public String getWebScriptUrl() {
        return webScriptUrl;
    }

    public void setWebScriptUrl(String webScriptUrl) {
        this.webScriptUrl = webScriptUrl;
    }
*/

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
                new BeanMap(item).putAllWriteable(bm);
                item.setColumnDefs(dynamicTableDef.getColumnDefs());
                iFeedService.save(item);
                break;
            }
        }
        if (!found) {
            DynamicTableDef instance = new DynamicTableDef();
            new BeanMap(instance).putAllWriteable(bm);
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
            new BeanMap(dtd).putAllWriteable(bm);
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

    public List<String> completeFeedName(String incompleteName) {
        Filter sample = new FilterModel();
        sample.setName(incompleteName);
        List<IFeed> result = iFeedService.getIFeedsByFilter(sample, 0, 10);
        List<String> names = new ArrayList<String>();
        for (IFeed iFeed : result) {
            names.add(iFeed.getName() + " (" + iFeed.getId() + ")");
            //iFeed.toJson();
        }
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

    public void cancelEdit() throws SystemException, PortalException {
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

    // private VarnishClient varnishClient;

    {
        try {
            // this.varnishClient = VarnishClient.newVarnishClient();
        } catch (Exception e) {
            // Do nothing. The client might not be needed.
        }
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

    public void copyAndPersistFeed(PortletRequest request, Long withThatKey)
            throws SystemException, PortalException {
        User user = getUser(request);
        viewIFeed(iFeedService.copyAndPersistFeed(withThatKey, user.getScreenName()).getId());
        setInEditMode(true);
    }


    @Autowired
    private SolrServer solrServer;

    public void updateSearchResults() {
        if (searchResults == null) {
            searchResults = new ArrayList<>();
        }
        if (iFeedModelBean.getFilters().isEmpty()) {
            searchResults.clear();
        } else {
            updateSearchResults(iFeedModelBean);
        }
    }

    public void updateSearchResults(IFeed retrievedFeed) {
        updateSearchResults(
                retrievedFeed,
                "dc.title",
                "asc",
                0,
                500,
                new String[]{
                        "dc.title",
                        "dc.date.issued"
                }
        );
    }

    public void updateSearchResults(IFeed retrievedFeed,
                                    String sortField,
                                    String sortDirection,
                                    Integer startBy,
                                    Integer endBy,
                                    String[] fieldToSelect) {
        if (retrievedFeed == null) {
            // Throw 404 if the feed doesn't exist
            throw new RuntimeException("404");
        }

        IFeedSolrQuery solrQuery = new IFeedSolrQuery(solrServer, iFeedService);
        if (startBy != null && startBy >= 0) {
            solrQuery.setStart(startBy);
            if (endBy != null && endBy > startBy) {
                solrQuery.setRows(endBy - startBy);
            }
        } else {
            solrQuery.setRows(25000);
        }
        solrQuery.setShowDebugInfo(true);
        List<Map<String, Object>> result = null;

        result = solrQuery.getIFeedResults(retrievedFeed, sortField,
                getEnum(IFeedSolrQuery.SortDirection.class, sortDirection), fieldToSelect);

        for (Map<String, Object> item : result) {
            String alfrescoId = (String) item.get("dc.identifier.documentid");
            alfrescoId = alfrescoId.replace("workspace://SpacesStore/", "");
            item.put("alfrescoId", alfrescoId);
            formatDates(item);
        }

        this.searchResults = result;
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

    public static List<Note> toTooltipRows(Map<String, Object> item) {
        if (item == null) {
            return Arrays.asList(new Note("Ingen information", "Metadata för den här posten saknas."));
        }

        List<Note> result = new ArrayList<>();

        toTooltipRow(item, "dc.title", "Titel", result);
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
    }

    private static void toTooltipRow(Map<String, Object> item, String key, String label, List<Note> notes) {
        Note note = toTooltipRow(item, key, label);
        if (note != null) {
            notes.add(note);
        }
    }

    public static Note toTooltipRow(Map<String, Object> item, String key, String label) {
        Object value = item.get(key);
        if (value == null || value.toString().isEmpty()) {
            return null;
        }
        return new Note(label, String.valueOf(value));
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

    public Map<String, FieldInf> getFiltersMap() {
        return filtersMap;
    }

    public void setFiltersMap(Map<String, FieldInf> filtersMap) {
        this.filtersMap = filtersMap;
    }

}
