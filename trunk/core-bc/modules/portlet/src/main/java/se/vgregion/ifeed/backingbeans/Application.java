package se.vgregion.ifeed.backingbeans;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;
import se.vgregion.ifeed.el.AccessGuard;
import se.vgregion.ifeed.formbean.VgrOrganization;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.metadata.MetadataService;
import se.vgregion.ifeed.service.solr.SolrFacetUtil;
import se.vgregion.ifeed.types.*;
import se.vgregion.ldap.HasCommonLdapFields;
import se.vgregion.ldap.LdapSupportService;
import se.vgregion.ldap.person.LdapPersonService;
import se.vgregion.ldap.person.Person;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.portlet.PortletRequest;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by clalu4 on 2014-06-10.
 */
@Component(value = "app")
@Scope("session")
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private IFeedService iFeedService;
    @Autowired
    private ResourceLocalService resourceLocalService;
    @Autowired
    private LdapPersonService ldapPersonService;
    @Value("#{iFeedModelBean}")
    private IFeedModelBean iFeedModelBean;
    @Autowired
    private MetadataService metadataService;

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



    private String solrServiceUrl;

    private VgrGroup group;

    private FieldInf newFilter;

    private VgrDepartment department;

    private String newOwnershipUserId;

    private int pageSize = 20;

    private int currentPage = 0;

    private boolean inEditMode;

    @Value("#{filter}")
    private FilterModel filter;

    @Value("#{navigationModelBean}")
    private NavigationModelBean navigationModelBean;
    private FieldsInf fieldsInf;
    private List<FieldInf> filters;

    private List<VgrDepartment> selectableDepartments;

    private VgrOrganization organizationToChoose;

    @Autowired
    private LdapSupportService ldapOrganizationService;

    public List<IFeed> list() {
        return iFeedService.getIFeedsByFilter(filter);
    }

    public List<IFeed> page() {
        List<IFeed> result = iFeedService.getIFeedsByFilter(filter);
        int start = Math.min(getCurrentSpanStart(), result.size());
        int end = Math.min(getCurrentSpanEnd(), result.size());
        return result.subList(start, end);
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
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        PortletRequest request = (PortletRequest) externalContext.getRequest();
        User user = getUser(request);
        IFeed feed = iFeedService.getIFeed(id);

        List<FieldsInf> fieldsInfs = iFeedService.getFieldsInfs();
        this.fieldsInf = fieldsInfs.get(fieldsInfs.size() - 1);

        iFeedModelBean.copyValuesFromIFeed(feed);
        fieldsInf.putFieldInfInto(iFeedModelBean.getFilters());

        this.filters = iFeedService.getFieldInfs();

        navigationModelBean.setUiNavigation("VIEW_IFEED");
        setInEditMode(false);
    }

    User getUser(PortletRequest request) throws PortalException, SystemException {
        return PortalUtil.getUser(request);
    }

    public List<String> completeUserName(String incompleteUserName) {
        //List<Person> people = new ArrayList<Person>();
        System.out.println("completeUserName " + incompleteUserName);

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
        Ownership ownership = new Ownership();
        ownership.setUserId(newOwnershipUserId);
        ownership.setIfeedId(iFeedModelBean.getId());
        iFeedModelBean.getOwnershipsAsList().add(ownership);
    }

    public void update() {
        try {
            System.out.println("bean.getOwnershipList().size(): " + iFeedModelBean.getOwnershipList().size());
            IFeed feed = iFeedModelBean.toIFeed();
            iFeedService.update(feed);
            navigationModelBean.setUiNavigation("USER_IFEEDS");
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
    }

    public void cancelNewFilter(FieldInf fieldInf) {
        fieldInf.setExpanded(false);
        fieldInf.setValue("");
        newFilter = null;
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

    public List<String> completeFixText(String apelonKey, String textToComplete) {
        List<String> result = new ArrayList<String>();
        result.add("foo");
        result.add("bar");
        result.add("baz");
        return result;
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

    public void createNewDepartment() {
        setDepartment(new VgrDepartment());
    }

    public void saveDepartment() {
        try {
            iFeedService.save(department);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        cancelDepartment();
    }

    public void cancelDepartment() {
        department = null;
    }

    public List<VgrDepartment> getDepartments() {
        return iFeedService.getVgrDepartments();
    }

    public void deleteDepartment(VgrDepartment department) {
        iFeedService.delete(department);
    }

    public boolean mayEditFeed(PortletRequest request, IFeed feed) throws SystemException, PortalException {
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
        if (externalContext.getUserPrincipal() == null){
            System.out.println("current principal is null");
        }
        else{
            Long id = Long.parseLong(externalContext.getUserPrincipal().getName());
            try {
                u = UserLocalServiceUtil.getUserById(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return u;
    }

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
        group = new VgrGroup();
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

    public List<SelectItemGroup> fieldInfsAsSelectItemGroups() {
        List<SelectItemGroup> result = new ArrayList<SelectItemGroup>();
        if (filters == null) {
            this.filters = iFeedService.getFieldInfs();
        }
        for (FieldInf parent : getFilters()) {
            SelectItemGroup group = new SelectItemGroup(parent.getName());
            SelectItem[] selectItems = new SelectItem[parent.getChildren().size()];
            int c = 0;
            for (FieldInf child : parent.getChildren()) {
                selectItems[c++] = new SelectItem(child.getId(), child.getName());
            }
            group.setSelectItems(selectItems);
            result.add(group);
        }
        return result;
    }

    public void showMyFeeds() {
        filter.setCreatorName(getCurrentUser().getScreenName());
    }

}
