package se.vgregion.ifeed.backingbeans;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.sun.faces.component.visit.FullVisitContext;
import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriTemplate;
import se.vgregion.ifeed.el.AccessGuard;
import se.vgregion.ifeed.formbean.VgrOrganization;
import se.vgregion.ifeed.service.ifeed.Filter;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.metadata.MetadataService;
import se.vgregion.ifeed.service.solr.IFeedResults;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.shared.ColumnDef;
import se.vgregion.ifeed.shared.DynamicTableDef;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by clalu4 on 2014-06-10.
 */
@Component(value = "app")
@Scope("session")
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Value("${ifeed.metadata.base.url}")
    private String metadataBaseUrl;
    /*    @Value("${ifeed.web.script.url}")
        private String webScriptUrl;*/
    @Value("${ifeed.web.script.json.url}")
    private String webScriptJsonUrl;
    //@Value("#{tableDef}")
    //private TableDefModel tableDefModel;

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

    private VgrGroup group;

    private FieldInf newFilter;

    private VgrDepartment department;

    private String newOwnershipUserId;

    private String newCompositeName;

    private int pageSize = 25;

    private int currentPage = 0;

    private boolean inEditMode;

    private boolean limitOnResultCount;

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

    public List<IFeed> list() {
        int start = (getCurrentSpanStart());
        int end = (getCurrentSpanEnd());
        List<IFeed> result = iFeedService.getIFeedsByFilter(filter, start, end);
        return result;
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

            List<IFeed> result = iFeedService.getIFeedsByFilter(filter, start, end);

            //page = result.subList(start, end);
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
        return getMaxPageCountImp(list(), pageSize);
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
        IFeed feed = iFeedService.getIFeed(id);
        newFilter = null;
        iFeedModelBean.copyValuesFromIFeed(feed);

        List<FieldsInf> fieldsInfs = iFeedService.getFieldsInfs();
        if (!fieldsInfs.isEmpty()) {
            this.fieldsInf = fieldsInfs.get(fieldsInfs.size() - 1);
            fieldsInf.putFieldInfInto(iFeedModelBean.getFilters());
        }

        this.filters = iFeedService.getFieldInfs();

        navigationModelBean.setUiNavigation("VIEW_IFEED");
        setInEditMode(false);
        findResultsByCurrentFeedConditions();

        setFilters(filters);

        for (IFeedFilter filter : iFeedModelBean.getFilters()) {
            FieldInf field = getFieldsByNameIndex().get(filter.getFilterKey());
            if ("d:ldap_org_value".equals(field.getType())) {
                VgrOrganization organization = vgrOrganizationsHome.findVgrOrganizationByHsaId(filter.getFilterQuery());
                if (organization != null) {
                    filter.setFilterQueryForDisplay(organization.getLabel());
                }
            }
            filter.setFieldInf(field);
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
        for (FieldInf parent : filters) {
            fieldsByNameIndex.put(parent.getId(), parent);
            if (!parent.getChildren().isEmpty()) {
                for (FieldInf child : parent.getChildren()) {
                    fieldsByNameIndex.put(child.getId(), child);
                }
            }
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
            System.out.println("current principal is null");
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
        this.currentResult = iFeedSolrQuery.getIFeedResults(getIFeedModelBean());
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
        String result = String.valueOf(iFeedExcelFeed.expand("ID", iFeedModelBean.getSortField(), iFeedModelBean.getSortDirection()));
        result = result.replaceFirst(Pattern.quote("ID"), iFeedModelBean.toJson());
        return result;
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

    public List<SelectItemGroup> fieldInfsAsSelectItemGroups() {
        List<SelectItemGroup> result = new ArrayList<SelectItemGroup>();
        if (filters == null) {
            this.filters = iFeedService.getFieldInfs();
        }
        for (FieldInf parent : getFilters()) {
            SelectItemGroup group = new SelectItemGroup(parent.getName());
            //SelectItem[] selectItems = new SelectItem[parent.getChildren().size()];
            List<SelectItem> items = new ArrayList<SelectItem>();
            int c = 0;
            for (FieldInf child : parent.getChildren()) {
                if (child.isInHtmlView()) {
                    //selectItems[c++] = new SelectItem(child.getId(), child.getName());
                    items.add(new SelectItem(child.getId(), child.getName()));
                }
            }
            //group.setSelectItems(selectItems);
            group.setSelectItems(items.toArray(new SelectItem[items.size()]));
            result.add(group);
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
                    if ("j_idt192".equals(component.getId())) {
                        System.out.println("Found the shit!");
                    }
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
        System.out.println(longId);
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
            iFeedModelBean.removeFilter(filter);
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

    public void putFlowInFeed(DynamicTableDef dynamicTableDef) {
        BeanMap bm = new BeanMap(dynamicTableDef);
        boolean found = false;
        for (DynamicTableDef item : iFeedModelBean.getDynamicTableDefs()) {
            if (dynamicTableDef.getId() != null && (item.getId() == dynamicTableDef.getId() || dynamicTableDef.getId().equals(item.getId()))) {
                found = true;
                new BeanMap(item).putAllWriteable(bm);
                item.setColumnDefs(dynamicTableDef.getColumnDefs());
                break;
            }
        }
        if (!found) {
            DynamicTableDef instance = new DynamicTableDef();
            new BeanMap(instance).putAllWriteable(bm);
            instance.setColumnDefs(dynamicTableDef.getColumnDefs());
            iFeedModelBean.getDynamicTableDefs().add(instance);
        }
        navigationModelBean.setUiNavigation("VIEW_IFEED");
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
        }
        return names;
    }

    public void createNewComposite() {
        if (newCompositeName != null && !newCompositeName.isEmpty()) {
            Filter filter = new FilterModel();
            String[] parts = newCompositeName.split("\\(|\\)");
            String idPart = parts[parts.length - 1];
            //filter.setName(newCompositeName);
            filter.setId(Long.parseLong(idPart));
            List<IFeed> nf = iFeedService.getIFeedsByFilter(filter, 0, 1);
            if (!nf.isEmpty()) {
                iFeedModelBean.getComposites().add(nf.get(0));
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

}
