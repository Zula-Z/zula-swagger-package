package com.zula.swagger.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security filter chain that permits Swagger/OpenAPI endpoints when Spring Security is present.
 * Guarded so it won't override a user-defined SecurityFilterChain.
 */
@Configuration
@EnableWebSecurity
@ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
@ConditionalOnProperty(name = "zula.swagger.security.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerSecurityAutoConfig {

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/error"),
                                new AntPathRequestMatcher("/swagger-ui/**"),
                                new AntPathRequestMatcher("/swagger-ui.html"),
                                new AntPathRequestMatcher("/v3/api-docs/**"),
                                new AntPathRequestMatcher("/actuator/**")
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .httpBasic()
                .formLogin().disable();

        return http.build();
    }
}
