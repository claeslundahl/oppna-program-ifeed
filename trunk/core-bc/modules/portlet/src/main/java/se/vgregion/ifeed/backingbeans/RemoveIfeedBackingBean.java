package se.vgregion.ifeed.backingbeans;


import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.el.AccessGuard;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.IFeed;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
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
    @Autowired
    private ResourceLocalService resourceLocalService;
    //Set iFeedId in IFeedBackingBean before removing IFeed
    /*    private Long iFeedId;*/

    public RemoveIfeedBackingBean() {}

    User getUser(PortletRequest request) throws PortalException, SystemException {
        return PortalUtil.getUser(request);
    }



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

    public ResourceLocalService getResourceLocalService() {
        return resourceLocalService;
    }

    public void setResourceLocalService(ResourceLocalService resourceLocalService) {
        this.resourceLocalService = resourceLocalService;
    }

    public Long getIFeedId() {
        return IFeedId;
    }

    public void setIFeedId(Long IFeedId) {
        this.IFeedId = IFeedId;
    }

}
