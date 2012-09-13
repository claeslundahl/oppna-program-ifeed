package se.vgregion.ldap.person;

import java.util.List;

public interface LdapPersonService {
    List<Person> getPeople(final String filter, final int maxResults);
}
