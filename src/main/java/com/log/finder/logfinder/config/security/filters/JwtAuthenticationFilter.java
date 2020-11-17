package com.log.finder.logfinder.config.security.filters;

import com.log.finder.logfinder.config.security.JwtConfig;
import com.log.finder.logfinder.config.security.services.TokenService;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilterBean {

    private TokenService tokenService;

    public JwtAuthenticationFilter(ApplicationContext context){
        this.tokenService = context.getBean(TokenService.class);
    }



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String jwt = getTokenFromAuthHeader(httpServletRequest);
        if (jwt != null && !jwt.equals("") && this.tokenService.validateToken(jwt)) {
            Authentication authentication = this.tokenService.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String refreshedToken = tokenService.refreshToken(jwt);
            ((HttpServletResponse) servletResponse).setHeader(JwtConfig.TOKEN_AUTH, refreshedToken);

        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private static String getTokenFromAuthHeader(HttpServletRequest request)

    {
        String bearerToken = request.getHeader(JwtConfig.AUTH_HEADER);
        if (bearerToken != null && !bearerToken.equals("") && bearerToken.startsWith(JwtConfig.BEARER)) {
            return bearerToken.substring(JwtConfig.BEARER.length(), bearerToken.length());
        }
        return null;
    }



}
