package se.vgregion.ifeed.viewhandler;

/**
 * Created by Monica on 2014-05-05.
 */
import com.liferay.portal.util.PortalUtil;

import java.util.Map;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

public class IFeedViewHandlerWrapper extends ViewHandlerWrapper {

    private ViewHandler wrapped;

    public IFeedViewHandlerWrapper(ViewHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ViewHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public String calculateRenderKitId(FacesContext context) {

        ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
        RenderRequest request = (RenderRequest) extcontext.getRequest();
        HttpServletRequest req = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));

      /*  HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();*/
        String userAgent = req.getHeader("user-agent");
        String accept = req.getHeader("Accept");

        if (userAgent != null && accept != null) {
            UserAgentInfo agent = new UserAgentInfo(userAgent, accept);
            if (agent.isMobileDevice()) {
                return "PRIMEFACES_MOBILE";
            }
        }

        return this.wrapped.calculateRenderKitId(context);
    }
}