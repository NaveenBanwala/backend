package com.social_portfolio_db.demo.naveen.WebConfiq;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
}

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve all uploaded images from the uploads directory
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/");
        
        // Serve project images specifically
        registry.addResourceHandler("/images/projects/**")
                .addResourceLocations("file:uploads/projects/");
        
        // Serve profile images specifically
        registry.addResourceHandler("/images/profiles/**")
                .addResourceLocations("file:uploads/profiles/");
        
        // Fallback for any other image requests
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        }

        @Bean
        public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
        }
}

