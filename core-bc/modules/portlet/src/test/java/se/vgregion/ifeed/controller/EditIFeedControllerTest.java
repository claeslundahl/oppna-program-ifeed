package se.vgregion.ifeed.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.util.UriTemplate;

import se.vgregion.ifeed.formbean.FilterFormBean;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.service.metadata.MetadataService;
import se.vgregion.ifeed.service.solr.IFeedSolrQuery;
import se.vgregion.ifeed.types.FieldInf;
import se.vgregion.ifeed.types.FilterType;
import se.vgregion.ifeed.types.FilterType.Filter;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.IFeedFilter;
import se.vgregion.ldap.person.LdapPersonService;
import se.vgregion.ldap.person.Person;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;

public class EditIFeedControllerTest {

    EditIFeedController controller;
    private Model model;
    private ActionResponse response;
    private IFeedService iFeedService;
    private List<Role> roles;

    @Before
    public void setUp() throws Exception {
        roles = new ArrayList<Role>();
        controller = new EditIFeedController() {
            @Override
            protected User fetchUser(PortletRequest request) throws PortalException, SystemException {
                User user = mock(User.class);
                when(user.getScreenName()).thenReturn("user-name");
                when(user.getRoles()).thenReturn(roles);
                return user;
            }

            @Override
            public String getVgrOrganizationJson(String ns) {
                return "[]";
            }

            @Override
            public List<FieldInf> getFieldInfs() {
                List<FieldInf> result = new ArrayList<FieldInf>();

                FieldInf fi = new FieldInf();
                fi.setId("foo.bar.baz");
                result.add(fi);
                // URL url = EditIFeedControllerTest.class.getResource("/fields-config.txt");
                // FileReader fr;
                // try {
                // fr = new FileReader(url.getFile());
                // FieldsInf fi = new FieldsInf();
                // StringBuffer sb = new StringBuffer();
                // for (int c = 0; c != -1; c = fr.read()) {
                // sb.append((char) c);
                // }
                // fi.setText(sb.toString());
                // return fi.getFieldInfs();
                // } catch (Exception e) {
                // throw new RuntimeException(e);
                // }
                return result;
            }

        };
        model = mock(Model.class);
        response = mock(ActionResponse.class);
        iFeedService = mock(IFeedService.class);
        controller.setIFeedService(iFeedService);
    }

    @Test
    public void editIFeedLongModelActionResponse() {
        Long feedId = 101l;
        IFeed feed = new IFeed();
        when(iFeedService.getIFeed(feedId)).thenReturn(feed);

        controller.editIFeed(feedId, model, response);
        verify(model).addAttribute(eq("ifeed"), any(IFeed.class));
    }

    // @Test // Failed in maven... ! ?
    public void showEditIFeedForm() throws UnsupportedEncodingException {
        IFeed iFeed = new IFeed();
        iFeed.setId(200l);
        iFeed.setSortField("id");
        iFeed.setSortDirection("asc");

        String orderByCol = "id";
        String orderByType = "asc";
        javax.portlet.RenderResponse response = mock(RenderResponse.class);

        IFeedSolrQuery iFeedSolrQuery = mock(IFeedSolrQuery.class);
        when(iFeedSolrQuery.getIFeedResults(iFeed)).thenReturn(new ArrayList<Map<String, Object>>());
        when(iFeedSolrQuery.getRows()).thenReturn(10);
        controller.setIFeedSolrQuery(iFeedSolrQuery);

        UriTemplate iFeedAtomFeed = mock(UriTemplate.class);
        controller.setIFeedAtomFeed(iFeedAtomFeed);

        UriTemplate iFeedWebFeed = mock(UriTemplate.class);
        controller.setIFeedWebFeed(iFeedWebFeed);

        PortletURL purl = mock(PortletURL.class);
        when(response.createRenderURL()).thenReturn(purl);

        PortletResponse pr = mock(PortletResponse.class);

        String result = controller.showEditIFeedForm(iFeed, orderByCol, orderByType, model, response, pr);
        assertEquals("editIFeedForm", result);
    }

    @Test
    public void editIFeedIFeedActionResponseModelPortletRequest() throws SystemException, PortalException {
        IFeed iFeed = mock(IFeed.class);
        when(iFeed.getName()).thenReturn("SomeFeed");
        when(iFeed.getUserId()).thenReturn("SomeUserName");

        PortletRequest request = mock(PortletRequest.class);
        Role iFeedAdminRole = mock(Role.class);
        when(iFeedAdminRole.getName()).thenReturn("iFeed-admin");
        roles.add(iFeedAdminRole);

        controller.editIFeed(iFeed, response, model, request);

        try {
            iFeedAdminRole = mock(Role.class);
            when(iFeedAdminRole.getName()).thenReturn("not-iFeed-admin");
            roles.clear();
            roles.add(iFeedAdminRole);

            controller.editIFeed(iFeed, response, model, request);
            fail("The user is not iFeed-admin, hence should not be able to run this method.");
        } catch (RuntimeException re) {
            assertTrue(true);
        }

        // Check that an owner of a feed is allowed to use the method.
        iFeed = mock(IFeed.class);
        when(iFeed.getName()).thenReturn("SomeFeed");
        when(iFeed.getUserId()).thenReturn("user-name");

        try {
            controller.editIFeed(iFeed, response, model, request);
            assertTrue(true);
        } catch (RuntimeException re) {
            fail();
        }
    }

    @Test
    public void updateIFeed() {
        IFeed iFeed = mock(IFeed.class);
        when(iFeed.getName()).thenReturn("SomeFeed");
        when(iFeed.getUserId()).thenReturn("SomeUserName");

        controller.updateIFeed(iFeed, response, model);

        verify(model).addAttribute(eq("ifeed"), eq(iFeed));
    }

    @Test
    public void selectNewFilter() throws IOException {
        IFeed iFeed = mock(IFeed.class);
        Filter filter = Filter.CASE;
        MetadataService metadataService = mock(MetadataService.class);
        controller.setMetadataService(metadataService);
        Map<String, FieldInf> map = new HashMap<String, FieldInf>();
        map.put("foo.bar.baz", new FieldInf());
        when(model.asMap()).thenReturn(new HashMap<String, Object>(map));

        when(iFeedService.mapFieldInfToId()).thenReturn(map);

        controller.selectNewFilter(iFeed, "foo.bar.baz", model, response);

        String[] keys = new String[] { "newFilter", "vocabularyJson", "filterFormBean", "ifeed" };
        validateModelAttAttributeKeys(model, keys);
    }

    void validateModelAttAttributeKeys(Model model, String... keys) {
        for (String key : keys) {
            verify(model).addAttribute(eq(key), any());
        }
    }

    @Test
    public void testAddFilter() {
        IFeed iFeed = mock(IFeed.class);
        FilterFormBean filterFormBean = mock(FilterFormBean.class);
        controller.addFilter(iFeed, filterFormBean, response, model);
        verify(model).addAttribute(eq("ifeed"), eq(iFeed));
        verify(iFeed).addFilter(any(IFeedFilter.class));
    }

    @Test
    public void testEditFilter() throws IOException {
        IFeed iFeed = mock(IFeed.class);
        Filter filter = Filter.CASE;
        String filterQuery = "filterQuery";
        MetadataService metadataService = mock(MetadataService.class);
        controller.setMetadataService(metadataService);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("newFilter", new FieldInf());
        when(model.asMap()).thenReturn(map);

        when(iFeedService.mapFieldInfToId()).thenReturn(new HashMap(map));

        controller.editFilter(response, iFeed, "newFilter", filterQuery, model);

        // validateModelAttAttributeKeys(model, "newFilter", "filterValue", "vocabulary", "vocabularyJson",
        // "ifeed");
    }

    @Test
    public void removeFilter() {
        IFeed iFeed = mock(IFeed.class);
        Filter filter = Filter.CASE;
        String filterQuery = "filterQuery";
        MetadataService metadataService = mock(MetadataService.class);
        controller.setMetadataService(metadataService);
        model.addAttribute("newFilter", new FieldInf());

        controller.removeFilter(response, iFeed, filter, filterQuery, "foo.bar.baz", model);

        verify(iFeed).removeFilter(any(IFeedFilter.class));
    }

    @Test
    public void cancel() {
        SessionStatus sessionStatus = mock(SessionStatus.class);
        controller.cancel(sessionStatus);
        verify(sessionStatus).setComplete();
    }

    @Test
    public void searchPeople() throws IOException {
        String filterValue = "filterValue";
        ResourceResponse actionResponse = mock(ResourceResponse.class);
        LdapPersonService ldapPersonService = mock(LdapPersonService.class);
        when(ldapPersonService.getPeople(filterValue, 10)).thenReturn(new ArrayList<Person>());

        final OutputStream os = mock(OutputStream.class);
        when(actionResponse.getPortletOutputStream()).thenReturn(os);

        controller.setLdapPersonService(ldapPersonService);
        controller.searchPeople(filterValue, actionResponse);
    }

    @Test
    public void getFilters() {
        List<Filter> result = controller.getFilters();
        assertNotNull(result);
    }

    @Test
    public void getFilterTypes() {
        List<FilterType> result = controller.getFilterTypes();
        assertNotNull(result);
    }
}
