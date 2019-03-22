package se.vgregion.ifeed.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import se.vgregion.ifeed.filter.HeaderAuthenticationFilter;
import se.vgregion.ifeed.service.ifeed.UserService;
import se.vgregion.ifeed.service.spring.CustomLdapAuthenticationProvider;
import se.vgregion.ifeed.service.spring.CustomUserDetailsService;
import se.vgregion.ifeed.service.spring.HeaderAuthenticationProvider;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.base}")
    private String ldapBase;

    @Value("${ldap.userDn}")
    private String ldapUserDn;

    @Value("${ldap.password}")
    private String ldapPassword;

    @Value("${rememberMe.token}")
    private String rememeberMeToken;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch("OU=usr-Pers",
                "(cn={0})", contextSource());
        http
                .logout().invalidateHttpSession(true).deleteCookies("JSESSIONID").logoutSuccessUrl("/logout.html").and()
                .authorizeRequests()
                .antMatchers("/js/**", "/css/**", "/images/**", "/fonts/**", "/javax.faces.resource/**", "/view/login.xhtml*", "/logout.html", "/favicon.ico")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new HeaderAuthenticationFilter(), BasicAuthenticationFilter.class)
                .csrf().disable()
                .rememberMe()
                .userDetailsService(new LdapUserDetailsService(userSearch, getLdapAuthoritiesPopulator()))
                .tokenRepository(new InMemoryTokenRepositoryImpl())
                .key(rememeberMeToken)
                .rememberMeCookieName("remember-me")
                .tokenValiditySeconds(60 * 60 * 24 * 30)
                .and()
                .formLogin()
                .loginPage("/view/login.xhtml")
                .permitAll();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        BindAuthenticator authenticator = new BindAuthenticator(contextSource());
        authenticator.setUserSearch(new FilterBasedLdapUserSearch("OU=usr-Pers", "(cn={0})", contextSource()));

        auth.authenticationProvider(new HeaderAuthenticationProvider(userService, contextSource(),"(cn={0})", "OU=usr-Pers"));
        auth.authenticationProvider(new CustomLdapAuthenticationProvider(userService, authenticator, getLdapAuthoritiesPopulator()));
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        PasswordEncoder passwordEncoder = passwordEncoder();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);

        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LdapAuthoritiesPopulator getLdapAuthoritiesPopulator() {
        return new LdapAuthoritiesPopulator() {

            @Override
            public Collection<? extends GrantedAuthority> getGrantedAuthorities(
                    DirContextOperations dirContextOperations,
                    String cn) {

                HashMap<String, List<String>> attributes = new HashMap<>();

                attributes.put("displayName", Collections.singletonList(
                        dirContextOperations.getStringAttribute("displayName")));

                attributes.put("userId", Collections.singletonList(
                        dirContextOperations.getStringAttribute("cn")));

                attributes.put("mail", Collections.singletonList(
                        dirContextOperations.getStringAttribute("mail")));

                attributes.put("dn", Collections.singletonList(dirContextOperations.getDn().toString()));

                GrantedAuthority authority = new LdapAuthority("user", cn, attributes);

                return Collections.singletonList(authority);
            }
        };
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(ldapUrl);

        contextSource.setBaseEnvironmentProperties(Collections.singletonMap("com.sun.jndi.ldap.connect.timeout", "1000"));

        contextSource.setBase(ldapBase);
        contextSource.setUserDn(ldapUserDn);
        contextSource.setPassword(ldapPassword);
        contextSource.setReferral("follow");

        return contextSource;
    }

}