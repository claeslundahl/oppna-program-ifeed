package se.vgregion.ifeed.el;

import se.vgregion.ifeed.types.CachedUser;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.Ownership;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class AccessGuard {

    public static boolean mayEditAllFeeds(CachedUser user) {
        return Boolean.TRUE.equals(user.isAdmin());
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
            if (screenName == null) {
                return false;
            }
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
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, se.getMessage(), se.getLocalizedMessage()));
            throw new RuntimeException(se);
        }
    }

}
