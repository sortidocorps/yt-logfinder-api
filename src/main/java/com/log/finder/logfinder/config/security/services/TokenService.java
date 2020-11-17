package com.log.finder.logfinder.config.security.services;

import com.log.finder.logfinder.model.UserApp;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TokenService {

    private SecretKey secretKey;

    private long tokenValidityInMilliseconds;

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);


    @Value("${jwt.secret}")
    private String theSecret;

    @PostConstruct
    public void init(){
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.theSecret));
        int second = 120;
        this.tokenValidityInMilliseconds = 1000 * second;
    }

    public String createToken(Authentication authentication, Boolean rememberMe) {
        Date now = new Date();
        Date validity;

        validity = new Date(now.getTime() + this.tokenValidityInMilliseconds);

        return createToken(authentication, false, now, validity);
    }

    private String createToken(Authentication authentication, boolean databaseCheck, Date issuedAt, Date validity) {

        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());




        String result = String.join(";", authorities);
        // ou String result = authorities.stream().collect(Collectors.joining(";"));

        return createToken(authentication.getName(), "", "", "", result, databaseCheck,
                issuedAt, validity);

    }

    public String createToken(String subject, String lastname, String firstname, String mail, String authorities,
                              boolean databaseCheck, Date issuedAt, Date expiration) {

        return Jwts.builder().setSubject(subject).setIssuedAt(issuedAt).claim("auth", authorities)
                .claim("dbCheck", databaseCheck).claim("lastName", lastname)
                .claim("firstName", firstname).claim("mail", mail).signWith(secretKey)
                .setExpiration(expiration).compact();
    }


    public Authentication getAuthentication(String token) {

        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities;
        if (claims.containsKey("auth")) {
            authorities = Arrays.stream(claims.get("auth").toString().split(";"))
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        } else {
            authorities = Collections.emptyList();
        }

        UserApp principal = new UserApp(claims.getSubject(), authorities);

        principal.setFirstName((String) claims.get("firstName"));
        principal.setLastName((String) claims.get("lastName"));
        principal.setEmail((String) claims.get("mail"));

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return true;
        } catch (Exception e){
            log.error("Validate token error ", e);
        }
        return false;
    }

    public String refreshToken(String token) {
        String ret = token;

        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        if (Boolean.FALSE.equals(claims.get("dbCheck"))
                && validExtendedDate(claims.getIssuedAt(), claims.getExpiration())) {

            // Update
            claims.setExpiration(new Date(new Date().getTime() + tokenValidityInMilliseconds));
            // Rebuild
            ret = Jwts.builder().setClaims(claims).signWith(secretKey).compact();
        }

        return ret;


    }

    private boolean validExtendedDate(Date issuedAt, Date expirationDate) {
        return expirationDate != null
                && issuedAt.getTime() > new Date().getTime()
                + tokenValidityInMilliseconds
                && expirationDate.getTime() < new Date().getTime() + tokenValidityInMilliseconds;

    }








}
