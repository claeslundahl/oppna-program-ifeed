package se.vgregion.ifeed.el;

import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.Ownership;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.print.attribute.standard.Severity;

public class AccessGuard {

	public static boolean haveRole(User user, String nameOfAccessRole) throws SystemException {
        if (user == null) {
            return false;
        }
        for (Role role : user.getRoles()) {
			if (nameOfAccessRole.equals(role.getName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean mayEditAllFeeds(User user) throws SystemException {
		return haveRole(user, "iFeed-admin");
	}

	public static boolean mayEditFeed(User user, IFeed feed) {
		try {
			if (user == null) {
				return false;
			}
			if (mayEditAllFeeds(user)) {
				return true;
			}
            String screenName = user.getScreenName();
            if (feed.getUserId() == null) {
                return true;
            }
            if (screenName.equals(feed.getUserId())) {
                return true;
            }

            for (Ownership ownership : feed.getOwnerships()) {
                if (user.getScreenName().equals(ownership.getUserId())) {
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
