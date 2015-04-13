/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.starfishrespect.myconsumption.server;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
public class OAuth2ServerConfiguration {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServer extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                    // Just for laughs, apply OAuth protection to only 2 resources
                    .requestMatchers().antMatchers("/","/greeting").and()
                    .authorizeRequests()
                    .anyRequest().access("#oauth2.hasScope('read')");
            // @formatter:on
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId("sparklr");
        }

    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration
            extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
                // @formatter:off
                clients.inMemory()
                .withClient("my-trusted-client")
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                    .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                    .scopes("read", "write", "trust")
                    .resourceIds("sparklr")
                    .accessTokenValiditySeconds(60)
            .and()
                .withClient("my-client-with-registered-redirect")
                    .authorizedGrantTypes("authorization_code")
                    .authorities("ROLE_CLIENT")
                    .scopes("read", "trust")
                    .resourceIds("sparklr")
                    .redirectUris("http://anywhere?key=value")
            .and()
                .withClient("my-client-with-secret")
                    .authorizedGrantTypes("client_credentials", "password")
                    .authorities("ROLE_CLIENT")
                    .scopes("read")
                    .resourceIds("sparklr")
                    .secret("secret");
                // @formatter:on
        }
    }

}


//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
//
//@Configuration
//public class OAuth2ServerConfiguration {
//
//    private static final String RESOURCE_ID = "restservice";
//
//    @Configuration
//    @EnableResourceServer
//    protected static class ResourceServerConfiguration extends
//            ResourceServerConfigurerAdapter {
//
//        @Override
//        public void configure(ResourceServerSecurityConfigurer resources) {
//            // @formatter:off
//            resources
//                    .resourceId(RESOURCE_ID);
//            // @formatter:on
//        }
//
//        @Override
//        public void configure(HttpSecurity http) throws Exception {
//            // @formatter:off
//            http
//                    .authorizeRequests()
//                    .antMatchers("/users").hasRole("ADMIN")
//                    .antMatchers("/greeting").authenticated();
//                    // TODO
//            // @formatter:on
//        }
//
//    }
//
//    @Configuration
//    @EnableAuthorizationServer
//    protected static class AuthorizationServerConfiguration extends
//            AuthorizationServerConfigurerAdapter {
//
//        private TokenStore tokenStore = new InMemoryTokenStore();
//
//        @Autowired
//        @Qualifier("authenticationManagerBean")
//        private AuthenticationManager authenticationManager;
//
//        @Override
//        public void configure(AuthorizationServerEndpointsConfigurer endpoints)
//                throws Exception {
//            // @formatter:off
//            endpoints
//                    .tokenStore(this.tokenStore)
//                    .authenticationManager(this.authenticationManager);
//            // @formatter:on
//        }
//
//        @Override
//        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//            // @formatter:off
//            clients
//                    .inMemory()
//                    .withClient("clientapp")
////                    .authorizedGrantTypes("password", "refresh_token")
////                    .authorities("USER")
//                    .scopes("read", "write")
//                    .resourceIds(RESOURCE_ID)
//                    .secret("123456");
//            // @formatter:on
//        }
//
//        @Bean
//        @Primary
//        public DefaultTokenServices tokenServices() {
//            DefaultTokenServices tokenServices = new DefaultTokenServices();
//            tokenServices.setSupportRefreshToken(true);
//            tokenServices.setTokenStore(this.tokenStore);
//            return tokenServices;
//        }
//
//    }
//
//}
