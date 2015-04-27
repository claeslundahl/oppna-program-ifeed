package se.vgregion.ifeed.jsf;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
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
            /*FacesContext facesContext = event.getFacesContext();
            Object foo = FacesContext.getCurrentInstance().getExternalContext().getResponse();
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            HttpServletRequest origRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();


            *//*HttpServletResponse response = (HttpServletResponse) facesContext
                    .getExternalContext().getResponse();*//*
            response.addHeader("Content-Type", "text/html; charset=UTF-8");
            response.addHeader("Access-Control-Allow-Origin", "http://localhost:8081");*/
        }
    }