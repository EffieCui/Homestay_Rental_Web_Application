package com.project.staybooking.config;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleGeoCodingConfig {

    @Value("${geocoding.apikey}")
    private String apiKey;

    @Bean  // context最好是singleton
    public GeoApiContext geoApiContext() {
        // Google geo文档上有
        return new GeoApiContext.Builder().apiKey(apiKey).build();
    }
}
