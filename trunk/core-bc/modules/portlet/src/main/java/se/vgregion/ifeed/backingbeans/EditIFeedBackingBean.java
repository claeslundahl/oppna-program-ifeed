package se.vgregion.ifeed.backingbeans;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.theme.ThemeDisplay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.Ownership;
import se.vgregion.ldap.person.LdapPersonService;
import se.vgregion.ldap.person.Person;

import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.portlet.PortletRequest;
import java.io.Serializable;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Monica on 2014-03-28.
 */
@Component(value = "editIFeedBackingBean")
@Scope("request")
public class EditIFeedBackingBean implements Serializable{

    @Autowired
    private IFeedService iFeedService;
    @Autowired
    private ResourceLocalService resourceLocalService;
    @Autowired
    private LdapPersonService ldapPersonService;

    @Value("#{iFeedModelBean}")
    private IFeedModelBean iFeedModelBean;

    @Value("#{navigationModelBean}")
    private NavigationModelBean navigationModelBean;
    private String newOwnershipName;

    public EditIFeedBackingBean() {
    }

    public void addIFeed(){

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();

        IFeed iFeed = new IFeed();
        iFeed.setName(iFeedModelBean.getName());
        iFeed.setDescription(iFeedModelBean.getDescription());
        iFeed.setUserId(getRemoteUserId(portletRequest));

        Ownership ownership = new Ownership();
        ownership.setIfeed(iFeed);
        ownership.setUserId(getRemoteUserId(portletRequest));
        iFeed.getOwnerships().add(ownership);

        // For testing purposes. TODO: remove this when ready...
        Ownership ownership2 = new Ownership();

        ownership2.setIfeed(iFeed);
        ownership2.setUserId("Jane Doe");
        iFeed.getOwnerships().add(ownership2);

        for(Ownership ownership1 : iFeed.getOwnerships()){
            String id = ownership1.getUserId();
        }

        iFeed = iFeedService.addIFeed(iFeed);
        try {
            ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
            long companyId = themeDisplay.getCompanyId();
            long userId = themeDisplay.getUserId();
            resourceLocalService.addResources(companyId, 0, userId, IFeed.class.getName(), iFeed.getId().longValue(), false, false, true);
        } catch (PortalException e) {

            e.printStackTrace();
        } catch (SystemException e) {

            e.printStackTrace();
        }

        navigationModelBean.setUiNavigation("ADD_FINAL_STEP");

     /*   iFeedModelBean.setName("");
        iFeedModelBean.setDescription("");*/

    }


    private String getRemoteUserId(PortletRequest request) {
        @SuppressWarnings("unchecked")
        Map<String, ?> userInfo = (Map<String, ?>) request.getAttribute(PortletRequest.USER_INFO);
        String userId = "";
        if (userInfo != null) {
            userId = (String) userInfo.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
        }
        return userId;
    }

    public IFeedService getIFeedService() {
        return iFeedService;
    }

    public void setIFeedService(IFeedService iFeedService) {
        this.iFeedService = iFeedService;
    }

    public ResourceLocalService getResourceLocalService() {
        return resourceLocalService;
    }

    public void setResourceLocalService(ResourceLocalService resourceLocalService) {
        this.resourceLocalService = resourceLocalService;
    }

    public IFeedModelBean getiFeedModelBean() {
        return iFeedModelBean;
    }

    public void setiFeedModelBean(IFeedModelBean iFeedModelBean) {
        this.iFeedModelBean = iFeedModelBean;
    }

    public void addOwnership() {
        Set<Ownership> target = iFeedModelBean.getOwnerships();
        Ownership ownership = new Ownership();
        ownership.setName(newOwnershipName);
        target.add(ownership);
    }

    public List<Person> completeUser(String incompleteUserName) {
        //List<Person> people = ldapPersonService.getPeople(incompleteUserName + "*", 10);
        List<Person> people = new ArrayList<Person>();
        for (int i = 0; i < 10; i++) {
            Person person = new Person("A" + i, "B" + i, "C" + i, "D" + i, "E" + i);
            people.add(person);
        }

        return people;
        /*List<String> result = new ArrayList<String>();
        for (Person person : people) {
            result.add(person.getUserName());
        }
        return result;*/
    }

    public List<String> completeUserName(String incompleteUserName) {
        //List<Person> people = new ArrayList<Person>();
        System.out.println("completeUserName " + incompleteUserName);

        /*
        for (int i = 0; i < 10; i++) {
            Person person = new Person("A" + i, "B" + i, "C" + i, "D" + i, "E" + i);
            people.add(person);
        }*/


        List<Person> people = ldapPersonService.getPeople(incompleteUserName + "*", 10);

        List<String> result = new ArrayList<String>();
        for (Person person : people) {
            result.add(person.getUserName());
        }
        return result;
    }

    private String fetchNameOfPersonIfMatch(String key) {
        try {
            List<Person> persons = ldapPersonService.getPeople(key, 2);
            if (persons.size() == 1) {
                Person person = persons.get(0);
                return person.getFirstName() + " " + person.getLastName();
            }
        } catch (Exception e) {

        }
        return "";
    }


    public void goBackToIFeedList(){
        navigationModelBean.setUiNavigation("USER_IFEEDS");
        iFeedModelBean.clearBean();

    }

    public String getNewOwnershipName() {
        return newOwnershipName;
    }

    public void setNewOwnershipName(String newOwnershipName) {
        this.newOwnershipName = newOwnershipName;
    }

    public LdapPersonService getLdapPersonService() {
        return ldapPersonService;
    }

    public void setLdapPersonService(LdapPersonService ldapPersonService) {
        this.ldapPersonService = ldapPersonService;
    }

    public NavigationModelBean getNavigationModelBean() {
        return navigationModelBean;
    }

    public void setNavigationModelBean(NavigationModelBean navigationModelBean) {
        this.navigationModelBean = navigationModelBean;
    }
}
