package com.zula.swagger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zula.swagger")
public class SwaggerProperties {
    private boolean enabled = true;
    private String title = "Zula API";
    private String description = "Zula Microservices API";
    private String version = "1.0.0";
    private String basePackage = "com.zula";
    // When true, this starter forces AntPathMatcher to keep Springfox 3.0.0 working on Boot 2.6/2.7
    private boolean forceAntPathMatcher = true;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getBasePackage() { return basePackage; }
    public void setBasePackage(String basePackage) { this.basePackage = basePackage; }

    public boolean isForceAntPathMatcher() { return forceAntPathMatcher; }
    public void setForceAntPathMatcher(boolean forceAntPathMatcher) { this.forceAntPathMatcher = forceAntPathMatcher; }
}