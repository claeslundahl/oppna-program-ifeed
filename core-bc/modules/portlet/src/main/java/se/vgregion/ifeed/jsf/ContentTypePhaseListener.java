package se.vgregion.ifeed.jsf;

import com.liferay.portal.kernel.util.PortalUtil;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ContentTypePhaseListener implements PhaseListener {

        public PhaseId getPhaseId()
        {
            return PhaseId.RENDER_RESPONSE;
        }

        public void afterPhase(PhaseEvent event) {

        }

        public void beforePhase(PhaseEvent event) {
            PortletResponse pr = (PortletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
            HttpServletResponse response = PortalUtil.getHttpServletResponse(pr);
            /*response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");*/
        }
    }