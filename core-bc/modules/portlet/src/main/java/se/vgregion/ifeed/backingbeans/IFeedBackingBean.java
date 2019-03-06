package se.vgregion.ifeed.backingbeans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;
import se.vgregion.ifeed.el.AccessGuard;
import se.vgregion.ifeed.repository.UserRepository;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.CachedUser;
import se.vgregion.ifeed.types.Config;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.IFeed;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Monica on 2014-03-25.
 */

@Component(value = "iFeedBackingBean")
@Scope("view")
public class IFeedBackingBean implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(IFeedBackingBean.class);

    private boolean pageNavigation = false;

    private SelectItem[] manufacturerOptions;
    private List<IFeed> filteredIFeeds;
    private List<IFeed> lists;

    //All IFeeds for display purposes
    private List<IFeedModelBean> iFeedModelBeans;
    //All IFeeds for filtering purposes
    private List<IFeedModelBean> filteredIFeedModelBeans;

    //User IFeeds for display purposes
    private List<IFeedModelBean> userIFeedModelBeans;
    //User IFeeds for filtering purposes
    private List<IFeedModelBean> filteredUserIFeedModelBeans;

    private FieldsInf fieldsInf = new FieldsInf();
    private List<FieldInf> fieldInfs;
    private String fieldInfString;
    private String popupConfString;
    private List<String> governance;

    @Value("${environment.text}")
    private String environmentText;

    @Value("#{navigationModelBean}")
    private NavigationModelBean navigationModelBean;

    @Value("#{iFeedModelBean}")
    private IFeedModelBean iFeedModelBean;

    @Autowired
    private IFeedService iFeedService;

    @Autowired
    private UserRepository userRepository;


//    @Autowired
//    private ResourceLocalService resourceLocalService;

    protected UriTemplate iFeedAtomFeed;


    public IFeedBackingBean() {
    }

    @PostConstruct
    public void init() {

        getAllIFeeds();
        getUserIFeeds();
    }


    public List<IFeed> getAllIFeeds() {

        List<IFeed> allIFeeds = null;
        try {
            allIFeeds = iFeedService.getIFeeds();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.out.println("Autowired iFeedService is null---------------------------------------------------: " + e.toString());
            throw new RuntimeException(e);
        }

        iFeedModelBeans = new ArrayList<IFeedModelBean>();
        try {
            for (IFeed iFeed : allIFeeds) {
                IFeedModelBean iFeedModelBean1 = new IFeedModelBean();
                iFeedModelBean1.setDescription(iFeed.getDescription());
                iFeedModelBean1.setName(iFeed.getName());
                //iFeedModelBean1.setOwnerships(iFeed.getOwnerships());
                iFeedModelBean1.getOwnerships().addAll(iFeed.getOwnerships());
                iFeedModelBean1.setId(iFeed.getId());

                iFeedModelBeans.add(iFeedModelBean1);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }


        return allIFeeds;
    }

    public List<IFeed> getUserIFeeds() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

        List<IFeed> userIFeeds = null;
        try {
            userIFeeds = iFeedService.getUserIFeeds(getRemoteUserId(request));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        userIFeedModelBeans = new ArrayList<IFeedModelBean>();
        try {
            for (IFeed iFeed : userIFeeds) {
                IFeedModelBean iFeedModelBean1 = new IFeedModelBean();
                iFeedModelBean1.setDescription(iFeed.getDescription());
                iFeedModelBean1.setName(iFeed.getName());
                //iFeedModelBean1.setOwnerships(iFeed.getOwnerships());
                iFeedModelBean1.getOwnerships().addAll(iFeed.getOwnerships());
                iFeedModelBean1.setId(iFeed.getId());

                userIFeedModelBeans.add(iFeedModelBean1);
            }
        } catch (Exception e) {
            System.out.println("Autowired iFeedService is null---------------------------------------------------: " + e.toString());
            throw new RuntimeException(e);
        }
        return userIFeeds;
    }

    public void removeBook(IFeed iFeed) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

        CachedUser user = getUser(request);

        IFeed feed = iFeedService.getIFeed(iFeed.getId());
        if (!AccessGuard.mayEditFeed(user, feed)) {
            throw new RuntimeException();
        }
        //iFeedService.removeIFeed(iFeed.getId());
        iFeedService.removeIFeed(iFeed);

        // Remove from memory
        iFeedModelBeans.remove(iFeed);
        userIFeedModelBeans.remove(iFeed);
    }

    private String getRemoteUserId(HttpServletRequest request) {
        return request.getRemoteUser();
    }

    public void viewIFeed(Long id) {
        //FacesContext facesContext = FacesContext.getCurrentInstance();
        //ExternalContext externalContext = facesContext.getExternalContext();
        //PortletRequest request = (PortletRequest) externalContext.getRequest();
        IFeed feed = iFeedService.getIFeed(id);
        iFeedModelBean.copyValuesFromIFeed(feed);
        navigationModelBean.setUiNavigation("VIEW_IFEED");
    }


    private CachedUser getUser(HttpServletRequest request) {
        return userRepository.findUser(request.getRemoteUser());
    }

    public void getFieldsInfs() {
        this.fieldInfs = fieldsInf.getFieldInfs();
    }

    public void setPageNavigationToTrue() {
        this.pageNavigation = true;
    }

    public void setPageNavigationToFalse() {
        this.pageNavigation = false;
    }

    public String showIFeeds() {
        return null;
    }

    public String showUserIFeeds() {
        return null;
    }

    public boolean isPageNavigation() {
        return pageNavigation;
    }

    public void setPageNavigation(boolean pageNavigation) {
        this.pageNavigation = pageNavigation;
    }

    public UriTemplate getiFeedAtomFeed() {
        return iFeedAtomFeed;
    }

    public void setiFeedAtomFeed(UriTemplate iFeedAtomFeed) {
        this.iFeedAtomFeed = iFeedAtomFeed;
    }

    public List<IFeed> getFilteredIFeeds() {
        return filteredIFeeds;
    }

    public void setFilteredIFeeds(List<IFeed> filteredIFeeds) {
        this.filteredIFeeds = filteredIFeeds;
    }

    public List<IFeed> getLists() {
        return lists;
    }

    public void setLists(List<IFeed> lists) {
        this.lists = lists;
    }

    public List<IFeedModelBean> getiFeedModelBeans() {
        return iFeedModelBeans;
    }

    public void setiFeedModelBeans(List<IFeedModelBean> iFeedModelBeans) {
        this.iFeedModelBeans = iFeedModelBeans;
    }

/*
    public ResourceLocalService getResourceLocalService() {
        return resourceLocalService;
    }

    public void setResourceLocalService(ResourceLocalService resourceLocalService) {
        this.resourceLocalService = resourceLocalService;
    }*/

    public IFeedService getIFeedService() {
        return iFeedService;
    }

    public void setIFeedService(IFeedService iFeedService) {
        this.iFeedService = iFeedService;
    }


/*    public RemoveIfeedBackingBean getRemoveIfeedBackingBean() {
        return removeIfeedBackingBean;
    }

    public void setRemoveIfeedBackingBean(RemoveIfeedBackingBean removeIfeedBackingBean) {
        this.removeIfeedBackingBean = removeIfeedBackingBean;
    }*/

    public List<FieldInf> getFieldInfs() {
        this.fieldInfs = iFeedService.getFieldInfs();
        return fieldInfs;
    }

    public void setFieldInfs(List<FieldInf> fieldInfs) {
        this.fieldInfs = fieldInfs;
    }

    public IFeedModelBean getIFeedModelBean() {
        return iFeedModelBean;
    }

    public void setIFeedModelBean(IFeedModelBean iFeedModelBean) {
        this.iFeedModelBean = iFeedModelBean;
    }

    public List<IFeedModelBean> getFilteredIFeedModelBeans() {
        return filteredIFeedModelBeans;
    }

    public void setFilteredIFeedModelBeans(List<IFeedModelBean> filteredIFeedModelBeans) {
        this.filteredIFeedModelBeans = filteredIFeedModelBeans;
    }

//    public ResourceLocalService getResourceLocalService() {
//        return resourceLocalService;
//    }

    public void saveFieldInfString() {
        FieldsInf inf = new FieldsInf();
        inf.setText(fieldInfString);
        FieldsInf latest = getLatestFieldInf();
        if (latest != null) {
            inf.setVersion(getLatestFieldInf().getVersion() + 1);
        } else {
            inf.setVersion(0l);
        }
        iFeedService.storeFieldsInf(inf);
        FieldsInf.putDataIntoCache(fieldInfString);
        this.navigationModelBean.setUiNavigation("USER_IFEEDS");
    }

    public void savePopupConfString() {
        Config inf = new Config();
        inf.setSetting(popupConfString);
        inf.setId("popup");

        iFeedService.save(inf);
        this.navigationModelBean.setUiNavigation("USER_IFEEDS");
    }

    private FieldsInf getLatestFieldInf() {
        List<FieldsInf> infs = iFeedService.getFieldsInfs();
        if (infs.isEmpty()) {
            return null;
        }
        return infs.get(infs.size() - 1);
    }

    public String getFieldInfString() {
        FieldsInf f = getLatestFieldInf();
        if (f == null) {
            fieldInfString = "";
        } else {
            fieldInfString = f.getText();
        }
        return fieldInfString;
    }

    public void setFieldInfString(String fieldInfString) {
        this.fieldInfString = fieldInfString;
    }

    public List<String> getGovernance() {
        governance = new ArrayList<String>();
        governance.add("Bacon");
        governance.add("Bananas");
        governance.add("Pinapple");
        governance.add("Toast");
        return governance;
    }

    public void setGovernance(List<String> governance) {
        this.governance = governance;
    }

    public NavigationModelBean getNavigationModelBean() {
        return navigationModelBean;
    }

    public void setNavigationModelBean(NavigationModelBean navigationModelBean) {
        this.navigationModelBean = navigationModelBean;
    }

    public List<IFeedModelBean> getUserIFeedModelBeans() {
        return userIFeedModelBeans;
    }

    public void setUserIFeedModelBeans(List<IFeedModelBean> userIFeedModelBeans) {
        this.userIFeedModelBeans = userIFeedModelBeans;
    }

    public List<IFeedModelBean> getFilteredUserIFeedModelBeans() {
        return filteredUserIFeedModelBeans;
    }

    public void setFilteredUserIFeedModelBeans(List<IFeedModelBean> filteredUserIFeedModelBeans) {
        this.filteredUserIFeedModelBeans = filteredUserIFeedModelBeans;
    }

    public String getPopupConfString() {
        if (popupConfString == null) {
            popupConfString = iFeedService.getConfig("popup").getSetting();
        }
        return popupConfString;
    }

    public String getEnvironmentText() {
        return environmentText;
    }

    public void setEnvironmentText(String environmentText) {
        this.environmentText = environmentText;
    }

    public void setPopupConfString(String popupConfString) {
        this.popupConfString = popupConfString;
    }
}