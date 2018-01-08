package se.vgregion.ifeed.admin.el;

import org.junit.Before;
import org.junit.Test;
import se.vgregion.ifeed.admin.identity.Role;
import se.vgregion.ifeed.admin.identity.SystemException;
import se.vgregion.ifeed.types.User;
import se.vgregion.ifeed.types.IFeed;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AccessGuardTest {

    User user;
    List<Role> roles;

    private void addAdminRoleToUser() throws SystemException {
       user.setAdmin(true);
    }

    @Before
    public void setUp() throws SystemException {
        // user = mock(User.class);
        user = new User();
        //when(user.getScreenName()).thenReturn("userName");
        roles = new ArrayList<Role>();
    }

    @Test
    public void mayEditAllFeeds_not() throws SystemException {
        boolean result = AccessGuard.mayEditAllFeeds(user);
        assertFalse(result);
    }

    @Test
    public void mayEditAllFeeds_yes() throws SystemException {
        addAdminRoleToUser();
        boolean result = AccessGuard.mayEditAllFeeds(user);
        assertTrue(result);
    }

    @Test
    public void mayEditFeed() {
        IFeed feed = new IFeed();
        // when(feed.getUserId()).thenReturn("userName");
        AccessGuard.mayEditFeed(user, feed);
    }
}
