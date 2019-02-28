package se.vgregion.ifeed.backingbeans;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * @author Patrik Bergström
 */
@Component(value = "userBackingBean")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserBackingBean {

    private String displayName;
    private String userId;
    private String email;

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Authentication userPrincipal = (Authentication) request.getUserPrincipal();

        if (userPrincipal != null && userPrincipal.getPrincipal() != null) {
            Iterator<? extends GrantedAuthority> iterator = ((UserDetails) userPrincipal.getPrincipal()).getAuthorities().iterator();

            if (iterator.hasNext()) {
                LdapAuthority authority = (LdapAuthority) iterator.next();
                this.displayName = authority.getFirstAttributeValue("displayName");
                this.userId = authority.getFirstAttributeValue("userId");
                this.email = authority.getFirstAttributeValue("mail");
            }
        }
    }

    public String getDisplayName() {
        if (displayName == null) {
            init();
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
