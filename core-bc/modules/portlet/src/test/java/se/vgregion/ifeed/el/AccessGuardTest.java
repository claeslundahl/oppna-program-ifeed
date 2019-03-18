package se.vgregion.ifeed.el;

import org.junit.Ignore;

@Ignore
public class AccessGuardTest {

    /*User user;
    List<Role> roles;

    private void addAdminRoleToUser() throws SystemException {
        addSomeRoleToUser("iFeed-admin");
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

    @Test
    public void mayEditAllFeeds_yes() throws SystemException {
        addAdminRoleToUser();
        boolean result = AccessGuard.mayEditAllFeeds(user);
        assertTrue(result);
    }

    @Test
    public void mayEditFeed() {
        IFeed feed = mock(IFeed.class);
        when(feed.getUserId()).thenReturn("userName");
        AccessGuard.mayEditFeed(user, feed);
    }*/
}
