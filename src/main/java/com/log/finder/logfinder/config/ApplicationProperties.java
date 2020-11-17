package com.log.finder.logfinder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    /**
     * Cors configuration
     */
    public final CorsConfiguration cors = new CorsConfiguration();

}
