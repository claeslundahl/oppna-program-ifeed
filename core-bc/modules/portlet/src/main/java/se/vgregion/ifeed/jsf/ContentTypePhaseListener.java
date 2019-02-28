package se.vgregion.ifeed.jsf;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

public class ContentTypePhaseListener implements PhaseListener {

        public PhaseId getPhaseId()
        {
            return PhaseId.RENDER_RESPONSE;
        }

        public void afterPhase(PhaseEvent event) {

        }

        public void beforePhase(PhaseEvent event) {
            HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
            /*response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");*/
        }
    }