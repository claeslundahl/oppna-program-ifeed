package se.vgregion.ifeed.admin.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class ContentTypePhaseListener implements PhaseListener {

    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

    public void afterPhase(PhaseEvent event) {

    }

    public void beforePhase(PhaseEvent event) {

    }

}