package se.vgregion.ifeed.service.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapAuthority;
import org.springframework.stereotype.Service;
import se.vgregion.ifeed.repository.UserRepository;
import se.vgregion.ifeed.types.CachedUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CachedUser user = userRepository.findUser(username);

        if (user != null) {
            Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

            HashMap<String, List<String>> attributes = new HashMap<>();
            attributes.put("mail", Collections.singletonList(user.getMail()));
            attributes.put("displayName", Collections.singletonList(user.getDisplayName()));
            attributes.put("userId", Collections.singletonList(user.getId()));
            GrantedAuthority grantedAuthority = new LdapAuthority("USER", user.getDn(), attributes);

            grantedAuthorities.add(grantedAuthority);

            if (user.getId() == null || user.getPasswordHash() == null) {
                throw new UsernameNotFoundException(
                        "User has never logged in via LDAP and thus hasn't any password hash cached."
                );
            }

            return new User(user.getId(), user.getPasswordHash(), grantedAuthorities);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
