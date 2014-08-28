package se.vgregion.ifeed.el;

import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.Ownership;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;

public class AccessGuard {

	public static boolean haveRole(User user, String nameOfAccessRole) throws SystemException {
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
		} catch (SystemException se) {
			throw new RuntimeException(se);
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
	}

}
