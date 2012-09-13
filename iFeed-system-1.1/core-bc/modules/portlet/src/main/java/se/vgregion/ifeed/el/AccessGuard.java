package se.vgregion.ifeed.el;

import se.vgregion.ifeed.types.IFeed;

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
		return user.getScreenName().equals(feed.getUserId());
	}

}
