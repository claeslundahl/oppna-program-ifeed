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

	public static boolean mayEditFeed(User user, IFeed feed) throws SystemException {
		if (user == null) {
			System.out.println("User är null!");
			return false;
		}
		if (mayEditAllFeeds(user)) {
			System.out.println("Får ändra allt!");
			return true;
		}
		System.out.println("user.getScreenName() " + user.getScreenName() + " \n feed.getUserId() "
		        + feed.getUserId());
		return user.getScreenName().equals(feed.getUserId());
	}

}
