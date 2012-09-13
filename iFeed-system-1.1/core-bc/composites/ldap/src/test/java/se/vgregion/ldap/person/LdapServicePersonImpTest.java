package se.vgregion.ldap.person;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;

import java.util.Arrays;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;


public class LdapServicePersonImpTest {

    @Mock
    private LdapTemplate ldapTemplate;
    private LdapPersonServiceImp ldapPersonService;

    @Before
    public void doSetup() throws Exception {
        MockitoAnnotations.initMocks(this);
        ldapPersonService = new LdapPersonServiceImp();
        ldapPersonService.setLdapTemplate(ldapTemplate);
    }

    //StringUtils.EMPTY, searchFilter.encode(), searchControls, attributesMapper
    @Test
    public void shouldFindPeopleByFilter() throws Exception {
        List<Person> people = Arrays.asList(
                new Person("Firstname1", "Lastname1", "userName1", "organisation1", "email1"),
                new Person("Firstname2", "Lastname2", "userName2", "organisation2", "email2")
                );
        given(ldapTemplate.search(anyString(), anyString(), any(SearchControls.class), any(AttributesMapper.class))).willReturn(people);

        List<Person> result = ldapPersonService.getPeople("foo", 123);

        assertEquals(people, result);
    }


}
