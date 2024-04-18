package com.edw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;

/**
 * <pre>
 *     com.edw.config.SecurityConfiguration
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 21 Mar 2023 20:16
 * 
 * Modified by Osama Oransa
 * 
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${server.port}")
    private String serverPort;
    @Value("${sso.server.endpoint}")
    private String ssoEndPoint;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .oauth2Client()
                    .and()
                .oauth2Login()
                .tokenEndpoint()
                    .and()
                .userInfoEndpoint();

        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS);

        http
                .authorizeHttpRequests()
                            .requestMatchers("/public","/oauth2/**", "/login/**").permitAll()
                            .anyRequest()
                                .fullyAuthenticated()
                .and()
                    .logout()
                    .logoutSuccessUrl(ssoEndPoint+"/realms/external/protocol/openid-connect/logout?redirect_uri=http://localhost:"+serverPort+"/public");

        return http.build();
    }
}
