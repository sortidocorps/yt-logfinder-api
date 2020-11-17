package com.log.finder.logfinder.config.security;

import com.log.finder.logfinder.config.security.filters.JwtAuthenticationFilter;
import com.log.finder.logfinder.config.security.filters.JwtLoginFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {


    public static final String AUTH_HEADER="Authorization";
    public static final String BEARER="Bearer";
    public static final String TOKEN_AUTH="X-Auth-Token";

    private String url;

    private AuthenticationManager authenticationManager;
    private ApplicationContext applicationContext;

    public JwtConfig(String url, AuthenticationManager authenticationManager, ApplicationContext applicationContext) {
        this.url = url;
        this.authenticationManager = authenticationManager;
        this.applicationContext = applicationContext;
    }

    @Override
    public void configure(HttpSecurity http) {

        JwtLoginFilter loginFilter = new JwtLoginFilter(url, authenticationManager, this.applicationContext);
        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(new JwtAuthenticationFilter(this.applicationContext), UsernamePasswordAuthenticationFilter.class);

    }








}
