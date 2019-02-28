package se.vgregion.ifeed.service.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import se.vgregion.ifeed.repository.UserRepository;
import se.vgregion.ifeed.types.CachedUser;

import java.util.Iterator;

public class CustomLdapAuthenticationProvider extends LdapAuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLdapAuthenticationProvider.class);

    private UserRepository userRepository;

    public CustomLdapAuthenticationProvider(UserRepository userRepository, LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
        super(authenticator, authoritiesPopulator);
        this.userRepository = userRepository;
    }

    public CustomLdapAuthenticationProvider(UserRepository userRepository, LdapAuthenticator authenticator) {
        super(authenticator);
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            Authentication authenticate = super.authenticate(authentication);

            if (authenticate.getPrincipal() != null) {
                saveCachedUser(authenticate);
            }

            return authenticate;
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    private void saveCachedUser(Authentication authenticate) {
        authenticate.getCredentials();

        CachedUser cachedUser = new CachedUser();

        LdapUserDetails principal = (LdapUserDetails) authenticate.getPrincipal();
        String credentials = (String) authenticate.getCredentials();
        Iterator<? extends GrantedAuthority> iterator = principal.getAuthorities().iterator();

        if (iterator.hasNext()) {
            GrantedAuthority next = iterator.next();

            if (next instanceof LdapAuthority) {
                LdapAuthority ldapAuthority = (LdapAuthority) next;

                String displayName = ldapAuthority.getFirstAttributeValue("displayName");
                cachedUser.setDisplayName(displayName);

                cachedUser.setMail(ldapAuthority.getFirstAttributeValue("mail"));
                cachedUser.setDn(ldapAuthority.getFirstAttributeValue("dn"));
            }
        }

        cachedUser.setId(principal.getUsername());
        cachedUser.setPasswordHash(new BCryptPasswordEncoder().encode(credentials));

        userRepository.merge(cachedUser);
    }
}
