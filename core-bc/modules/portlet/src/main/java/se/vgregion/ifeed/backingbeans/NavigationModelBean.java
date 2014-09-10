package se.vgregion.ifeed.backingbeans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by Monica on 2014-05-23.
 */
@Component(value = "navigationModelBean")
@Scope("session")
public class NavigationModelBean implements Serializable {

    //For ui navigation purposes
    private String uiNavigation = "USER_IFEEDS";

    public String getUiNavigation() {
        return uiNavigation;
    }

    public void setUiNavigation(String uiNavigation) {
        this.uiNavigation = uiNavigation;
    }





}
