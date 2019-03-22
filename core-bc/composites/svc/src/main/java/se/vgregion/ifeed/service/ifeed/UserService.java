package se.vgregion.ifeed.service.ifeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.userdetails.LdapAuthority;
import org.springframework.stereotype.Service;
import se.vgregion.ifeed.repository.UserRepository;
import se.vgregion.ifeed.types.CachedUser;

import java.util.Iterator;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void saveCachedUser(Authentication authenticate) {
        authenticate.getCredentials();

        CachedUser cachedUser = new CachedUser();

        UserDetails principal = (UserDetails) authenticate.getPrincipal();
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

        if (credentials != null && !"".equals(credentials)) {
            cachedUser.setPasswordHash(new BCryptPasswordEncoder().encode(credentials));
        }

        // Preserve admin
        CachedUser existentUser = userRepository.findUser(cachedUser.getId());
        if (existentUser != null) {
            cachedUser.setAdmin(existentUser.isAdmin());
        }

        userRepository.merge(cachedUser);
    }
}
