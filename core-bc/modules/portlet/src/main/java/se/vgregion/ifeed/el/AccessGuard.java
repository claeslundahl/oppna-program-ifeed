package se.vgregion.ifeed.el;

import se.vgregion.ifeed.types.CachedUser;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.Ownership;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class AccessGuard {

    public static boolean haveRole(CachedUser user, String nameOfAccessRole) {
        if (user == null) {
            return false;
        }
        /*for (Role role : user.getRoles()) {
            if (nameOfAccessRole.equals(role.getName())) {
                return true;
            }
        }*/ // TODO
        return false;
    }

    public static boolean mayEditAllFeeds(CachedUser user) {
        return haveRole(user, "iFeed-admin");
    }

    public static boolean mayEditFeed(CachedUser user, IFeed feed) {
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
                if (user.getDisplayName().equals(ownership.getUserId())) {
                    return true;
                }
            }

            return false;
        } catch (Exception se) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, se.getMessage(), se.getLocalizedMessage()));
            throw new RuntimeException(se);
        }
    }

}
