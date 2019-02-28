package se.vgregion.ifeed.backingbeans;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.service.ifeed.IFeedService;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;


/**
 * Created by Monica on 2014-
 */
@Component(value = "removeIfeedBackingBean")
@Scope("view")
public class RemoveIfeedBackingBean implements Serializable{
    private Long IFeedId;
    @Autowired
    private IFeedService iFeedService;
//    @Autowired
//    private ResourceLocalService resourceLocalService;
    //Set iFeedId in IFeedBackingBean before removing IFeed
    /*    private Long iFeedId;*/

    public RemoveIfeedBackingBean() {}

    /*User getUser(HttpServletRequest request) throws PortalException, SystemException {
        return PortalUtil.getUser(request);
    }*/



    /*public void removeBook(Long id) throws PortalException, SystemException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        PortletRequest request = (PortletRequest) externalContext.getRequest();

        User user = getUser(request);

        IFeed feed = iFeedService.getIFeed(id);
        if (!AccessGuard.mayEditFeed(user, feed)) {
            throw new RuntimeException();
        }
        iFeedService.removeIFeed(id);

        try {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            long companyId = themeDisplay.getCompanyId();
            resourceLocalService.deleteResource(companyId, IFeed.class.getName(),
                    ResourceConstants.SCOPE_INDIVIDUAL, id);
        } catch (PortalException e) {
            e.printStackTrace();
        } catch (SystemException e) {
            e.printStackTrace();
        }

    }*/

    public IFeedService getiFeedService() {
        return iFeedService;
    }

    public void setiFeedService(IFeedService iFeedService) {
        this.iFeedService = iFeedService;
    }

    /*public ResourceLocalService getResourceLocalService() {
        return resourceLocalService;
    }

    public void setResourceLocalService(ResourceLocalService resourceLocalService) {
        this.resourceLocalService = resourceLocalService;
    }*/

    public Long getIFeedId() {
        return IFeedId;
    }

    public void setIFeedId(Long IFeedId) {
        this.IFeedId = IFeedId;
    }

}
