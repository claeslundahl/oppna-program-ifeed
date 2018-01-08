package se.vgregion.ifeed.admin.identity;

import se.vgregion.ifeed.types.User;

import javax.servlet.ServletRequest;

public class PortalUtil {

    public static User getUser(ServletRequest request) throws PortalException, SystemException {
        return new User("DummyUserName");
    }

    public static User getUser() {
        /*
        FacesContext context = FacesContext.getCurrentInstance();
        Application bean = context.getApplication().evaluateExpressionGet(context, "#{app}", Application.class);

        return bean.getUser();*/

        User user = new User();
        user.setId("Dummy-user");
        return user;
    }

}
