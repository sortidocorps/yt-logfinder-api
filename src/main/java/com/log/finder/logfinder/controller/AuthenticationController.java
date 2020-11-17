package com.log.finder.logfinder.controller;

import com.log.finder.logfinder.config.security.JwtConfig;
import com.log.finder.logfinder.config.security.services.TokenService;
import com.log.finder.logfinder.dto.Creds;
import com.log.finder.logfinder.dto.JwtToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationController(TokenService tokenService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("v1/authenticate")
    public ResponseEntity<JwtToken> authorize(@RequestBody Creds credentials) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                credentials.getLogin(), credentials.getPassword());

        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenService.createToken(authentication, credentials.isRememberMe());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtConfig.TOKEN_AUTH, jwt);
        return new ResponseEntity<>(new JwtToken(jwt, JwtConfig.BEARER.trim()), httpHeaders,
                HttpStatus.CREATED);
    }

}
