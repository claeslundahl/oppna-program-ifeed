package se.vgregion.ifeed.admin.el;

import se.vgregion.ifeed.admin.identity.SystemException;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.Ownership;
import se.vgregion.ifeed.types.User;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class AccessGuard {

    /*public static boolean haveRole(User user, String nameOfAccessRole) throws SystemException {
        if (user == null) {
            return false;
        }

        if (user.isAdmin()) {
            return true;
        }


        for (Role role : user.getRoles()) {
            if (nameOfAccessRole.equals(role.getName())) {
                return true;
            }
        }

        return false;
    }*/

    public static boolean mayEditAllFeeds(User user) throws SystemException {
        if (user == null) {
            return true;
        }
        return user.isAdmin();
    }

    public static boolean mayEditFeed(User user, IFeed feed) {
        try {
            if (user == null) {
                return false;
            }
            if (mayEditAllFeeds(user)) {
                return true;
            }
            String screenName = user.getId();
            if (feed.getUserId() == null) {
                return true;
            }
            if (screenName.equals(feed.getUserId())) {
                return true;
            }

            for (Ownership ownership : feed.getOwnerships()) {
                if (user.getId().equals(ownership.getUserId())) {
                    return true;
                }
            }

            return false;
        } catch (Exception se) {
            FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, se.getMessage(), se.getLocalizedMessage())
            );
            throw new RuntimeException(se);
        }
    }

}
