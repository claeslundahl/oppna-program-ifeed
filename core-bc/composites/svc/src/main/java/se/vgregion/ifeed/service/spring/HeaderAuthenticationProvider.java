package se.vgregion.ifeed.service.spring;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthority;
import se.vgregion.ifeed.service.ifeed.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HeaderAuthenticationProvider implements AuthenticationProvider {

    private UserService userService;
    private BaseLdapPathContextSource contextSource;
    private String searchFilter;
    private String searchBase;

    public HeaderAuthenticationProvider(UserService userService, BaseLdapPathContextSource contextSource, String searchFilter, String searchBase) {
        this.userService = userService;
        this.contextSource = contextSource;
        this.searchFilter = searchFilter;
        this.searchBase = searchBase;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {


        FilterBasedLdapUserSearch search = new FilterBasedLdapUserSearch(searchBase, searchFilter, contextSource);

        UserDetails  principal = (UserDetails) authentication.getPrincipal();
        DirContextOperations dirContextOperations = search.searchForUser((principal).getUsername());

        HashMap<String, List<String>> attributes = new HashMap<>();
        attributes.put("displayName", Collections.singletonList(dirContextOperations.getStringAttribute("displayName")));
        attributes.put("userId", Collections.singletonList(authentication.getName()));
        attributes.put("mail", Collections.singletonList(dirContextOperations.getStringAttribute("mail")));
        attributes.put("dn", Collections.singletonList(dirContextOperations.getDn().toString()));

        LdapAuthority ldapAuthority = new LdapAuthority("user", dirContextOperations.getDn().toString(), attributes);

        List<LdapAuthority> authorities = Arrays.asList(ldapAuthority);

        User newPrincipal = new User(principal.getUsername(), principal.getPassword(), authorities);

        HeaderAuthenticationToken headerAuthenticationToken = new HeaderAuthenticationToken(
                newPrincipal,
                authentication.getCredentials(),
                authorities
        );

        this.userService.saveCachedUser(headerAuthenticationToken);

        return headerAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return HeaderAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
