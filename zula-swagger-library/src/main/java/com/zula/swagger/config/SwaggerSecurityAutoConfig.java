package com.zula.swagger.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Dedicated security filter chain for Swagger/OpenAPI endpoints so they stay publicly accessible
 * even when the host application registers its own security configuration.
 */
@Configuration
@EnableWebSecurity
@ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
    @ConditionalOnProperty(name = "zula.swagger.security.enabled", havingValue = "true", matchIfMissing = true)
    public class SwaggerSecurityAutoConfig {

    private static final String[] SWAGGER_WHITELIST = {
            "/",
            "/error",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/webjars/**",
            "/v3/api-docs/**",
            "/actuator/**"
    };

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatchers(matchers -> matchers.requestMatchers(SWAGGER_WHITELIST))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .httpBasic(customizer -> {})
                .formLogin(form -> form.disable())
                .build();
    }
}
