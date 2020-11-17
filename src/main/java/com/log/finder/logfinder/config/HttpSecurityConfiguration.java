package com.log.finder.logfinder.config;


import com.log.finder.logfinder.config.security.JwtConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@Order(1)
public class HttpSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final ApplicationProperties applicationProperties;

    public HttpSecurityConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;;
    }

    @Override
    public void configure(WebSecurity web) {

        web.ignoring().regexMatchers("index\\.html", "favicon\\.ico", ".*styles\\..*\\.css", ".*runtime\\..*\\.js",
                ".*polyfills\\..*\\.js", ".*main\\..*\\.js");
    }

    private JwtConfig securityConfigurerAdapter() throws Exception {
        return new JwtConfig("/login", authenticationManager(), getApplicationContext());
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        if (!CollectionUtils.isEmpty(applicationProperties.cors.getAllowedOrigins())) {


            source.registerCorsConfiguration("/api/**", applicationProperties.cors);

        }

        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .cors().and().authorizeRequests().antMatchers("/").permitAll()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/authenticate").permitAll()
                .antMatchers("/v1/**").authenticated()
                .anyRequest().authenticated().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .apply(securityConfigurerAdapter()).and()
                .headers().frameOptions().disable();

    }




}
