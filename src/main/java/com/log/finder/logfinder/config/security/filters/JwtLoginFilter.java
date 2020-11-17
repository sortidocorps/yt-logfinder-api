package com.log.finder.logfinder.config.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.log.finder.logfinder.config.security.JwtConfig;
import com.log.finder.logfinder.config.security.services.TokenService;
import com.log.finder.logfinder.dto.JwtToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {

    private ApplicationContext applicationContext;

    private TokenService tokenService;

    private ThreadLocal<LoginFilterUserCreds> loginFilterUserCredentialsThreadLocal = new ThreadLocal<>();

    private static final Logger log = LoggerFactory.getLogger(JwtLoginFilter.class);


    public JwtLoginFilter(String url, AuthenticationManager authManager, ApplicationContext applicationContext) {

        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);

        this.tokenService = applicationContext.getBean(TokenService.class);
        this.applicationContext = applicationContext;
    }


        @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
            // {"username":"toto","password":"bonjour"}
            LoginFilterUserCreds creds = new ObjectMapper()
                    .readValue(httpServletRequest.getInputStream(), LoginFilterUserCreds.class);

            loginFilterUserCredentialsThreadLocal.set(creds);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            Collections.emptyList()
                    )
            );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        try {

            LoginFilterUserCreds creds = loginFilterUserCredentialsThreadLocal.get();

            boolean rememberMe = false;
            if (creds != null) {
                rememberMe = creds.isRememberMe();
            }

            String jwt = this.tokenService.createToken(authResult, rememberMe);

            response.addHeader(JwtConfig.AUTH_HEADER, JwtConfig.TOKEN_AUTH + " " + jwt);
            response.setHeader("Content-type", "application/json");

            ObjectMapper obj = new ObjectMapper();
            obj.writeValue(response.getOutputStream(), new JwtToken(jwt, JwtConfig.BEARER.trim()));


        } catch (IOException e) {
            log.error("Filter login error ",e);
        }
    }
}
