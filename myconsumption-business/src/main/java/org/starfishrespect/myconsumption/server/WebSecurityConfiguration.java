package org.starfishrespect.myconsumption.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.starfishrespect.myconsumption.server.repositories.UserRepository;

import java.util.List;

/**
 * Created by thibaud on 13.05.15.
 */
@Configuration
public class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    UserRepository mUserRepository;



    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence cs) {
                return org.starfishrespect.myconsumption.server.entities.User.hashAndSalt();
            }

            @Override
            public boolean matches(CharSequence cs, String str) {
                return false;
            }
        });
        authProvider.setUserDetailsService(userDetailsService());
        ReflectionSaltSource saltSource = new ReflectionSaltSource();
        saltSource.setUserPropertyToUse("salt");
        authProvider.setSaltSource(saltSource);
        auth.authenticationProvider(authProvider);
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                org.starfishrespect.myconsumption.server.entities.User account = mUserRepository.getUser(username);

                if(account != null) {
                    List<GrantedAuthority> auth = AuthorityUtils
                            .commaSeparatedStringToAuthorityList("ROLE_USER");
                    //return new User(account.getName(), account.getPassword(), false, false, false, false, auth);
                    return new UserWithSalt(account.getName(), account.getSalt(), account.getPassword(), auth);
                } else {
                    throw new UsernameNotFoundException("could not find the user '"
                            + username + "'");
                }
            }

        };
    }
}