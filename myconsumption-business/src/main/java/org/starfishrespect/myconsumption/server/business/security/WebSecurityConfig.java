package org.starfishrespect.myconsumption.server.business.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Tells which resources of the REST service need to be protected and how.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * Define the security policy for the REST services.
     * BASIC authentication is supported.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users/**").permitAll() // needed to create a user on the first launch of the app
                .antMatchers(HttpMethod.POST, "/users/**/sensor/**").authenticated()
                .antMatchers("/configs/**").permitAll() // this resource does not need to be protected
                .anyRequest().authenticated()
                .and().httpBasic()
                .and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}


