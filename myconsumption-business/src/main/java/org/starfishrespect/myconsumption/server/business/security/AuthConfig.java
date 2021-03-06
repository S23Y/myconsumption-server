package org.starfishrespect.myconsumption.server.business.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.starfishrespect.myconsumption.server.business.repositories.UserRepository;

import java.util.List;

/**
 * Match spring user with our user implementation
 * (so that it is recognized during the authentication process)
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
@Configuration
public class AuthConfig extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    UserRepository mUserRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                org.starfishrespect.myconsumption.server.business.entities.User account = mUserRepository.getUser(username);

                if(account != null) {
                    List<GrantedAuthority> auth = AuthorityUtils
                            .commaSeparatedStringToAuthorityList("ROLE_USER");
                    return new User(account.getName(), account.getPassword(), auth);
                } else {
                    throw new UsernameNotFoundException("could not find the user '"
                            + username + "'");
                }
            }

        };
    }
}