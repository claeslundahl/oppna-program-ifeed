package se.vgregion.ifeed.el;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.ifeed.types.IFeed;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;

public class AccessGuardTest {

    User user;
    List<Role> roles;

    private void addAdminRoleToUser() throws SystemException {
        addSomeRoleToUser("Admin-iFeed");
    }

    private void addSomeRoleToUser() throws SystemException {
        addSomeRoleToUser("someRole");
    }

    private void addSomeRoleToUser(String nameOfSomeRole) throws SystemException {
        Role adminRole = mock(Role.class);
        when(adminRole.getName()).thenReturn(nameOfSomeRole);
        user.getRoles().add(adminRole);
    }

    @Before
    public void setUp() throws SystemException {
        user = mock(User.class);
        when(user.getScreenName()).thenReturn("userName");
        roles = new ArrayList<Role>();
        when(user.getRoles()).thenReturn(roles);
    }

    @Test
    public void haveRole_userHaveNoRoleWhatSoEver() throws SystemException {
        String nameOfAccessRole = "missingRole";
        boolean result = AccessGuard.haveRole(user, nameOfAccessRole);
        assertFalse(result);
    }

    @Test
    public void haveRole_userHaveWrongRole() throws SystemException {
        String nameOfAccessRole = "missingRole";
        boolean result = AccessGuard.haveRole(user, nameOfAccessRole);
        addSomeRoleToUser();
        assertFalse(result);
    }

    @Test
    public void haveRole_userHaveRole() throws SystemException {
        String nameOfAccessRole = "someRole";
        addSomeRoleToUser();
        boolean result = AccessGuard.haveRole(user, nameOfAccessRole);
        assertTrue(result);
    }

    @Test
    public void mayEditAllFeeds_not() throws SystemException {
        boolean result = AccessGuard.mayEditAllFeeds(user);
        addSomeRoleToUser();
        assertFalse(result);
    }

    // @Test
    // public void mayEditAllFeeds_yes() throws SystemException {
    // addAdminRoleToUser();
    // boolean result = AccessGuard.mayEditAllFeeds(user);
    // assertTrue(result);
    // }

    @Test
    public void mayEditFeed() {
        IFeed feed = mock(IFeed.class);
        when(feed.getUserId()).thenReturn("userName");
        AccessGuard.mayEditFeed(user, feed);
    }
}
