package org.starfishrespect.myconsumption.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Created by thibaud on 13.05.15.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    /**
//     * This section defines the user accounts which can be used for
//     * authentication as well as the roles each user has.
//     */
//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//
//        auth.inMemoryAuthentication()
//                .withUser("greg").password("turnquist").roles("USER").and()
//                .withUser("ollie").password("gierke").roles("USER", "ADMIN");
//    }

    /**
     * This section defines the security policy for the app.
     * - BASIC authentication is supported (enough for this REST-based demo)
     * - /employees is secured using URL security shown below
     * - CSRF headers are disabled since we are only testing the REST interface,
     *   not a web one.
     *
     * NOTE: GET is not shown which defaults to permitted.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users/**").permitAll()
                .antMatchers(HttpMethod.GET, "/users/**").permitAll()
                .antMatchers(HttpMethod.POST, "/users/**/sensor/**").authenticated()
                .anyRequest().authenticated()
                .and().httpBasic()
                .and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//        http.authorizeRequests().antMatchers("/sensors").authenticated();
//        http.csrf().disable().
//                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
//        http.formLogin().successHandler(authenticationSuccessHandler);
//        http.formLogin().failureHandler(authenticationFailureHandler);


//                .httpBasic().and()
//                .authorizeRequests()
//                .antMatchers("/admin/**").hasRole("ADMIN")
//                .anyRequest().fullyAuthenticated()
//                .and()
//                .formLogin().loginPage("/login").failureUrl("/login?error").permitAll()
//                .and()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.GET, "/sensors").hasRole("USER");
//                .antMatchers(HttpMethod.PUT, "/employees/**").hasRole("ADMIN")
//                .antMatchers(HttpMethod.PATCH, "/employees/**").hasRole("ADMIN")
//                .and()
//                .csrf().disable();
    }
}


