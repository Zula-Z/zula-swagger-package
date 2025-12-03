package com.zula.swagger.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.util.AntPathMatcher;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@ConditionalOnClass(Docket.class)
@ConditionalOnProperty(name = "zula.swagger.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public Docket api(SwaggerProperties properties) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(properties.getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo(properties));
    }

    // Force AntPathMatcher when enabled to avoid Springfox NPE on Boot 2.6/2.7
    @Bean
    @ConditionalOnClass(WebMvcConfigurer.class)
    @ConditionalOnProperty(prefix = "zula.swagger", name = "force-ant-path-matcher", havingValue = "true", matchIfMissing = true)
    public WebMvcConfigurer swaggerPathMatchingConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void configurePathMatch(PathMatchConfigurer configurer) {
                configurer.setPathMatcher(new AntPathMatcher());
            }
        };
    }

    private ApiInfo apiInfo(SwaggerProperties properties) {
        return new ApiInfoBuilder()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion())
                .build();
    }
}