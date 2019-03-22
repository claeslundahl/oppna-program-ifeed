package se.vgregion.ifeed.service.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import se.vgregion.ifeed.service.ifeed.UserService;

public class CustomLdapAuthenticationProvider extends LdapAuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLdapAuthenticationProvider.class);

    private UserService userService;

    public CustomLdapAuthenticationProvider(UserService userService, LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
        super(authenticator, authoritiesPopulator);
        this.userService = userService;
    }

    public CustomLdapAuthenticationProvider(UserService userService, LdapAuthenticator authenticator) {
        super(authenticator);
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            Authentication authenticate = super.authenticate(authentication);

            if (authenticate.getPrincipal() != null) {
                this.userService.saveCachedUser(authenticate);
            }

            return authenticate;
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

}
