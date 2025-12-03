package com.zula.swagger.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Provides a default Springfox Docket so /v3/api-docs/api and Swagger UI load
 * when Springfox is on the classpath.
 */
@Configuration
@EnableSwagger2WebMvc
@ConditionalOnClass(Docket.class)
@ConditionalOnProperty(name = "zula.swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SpringfoxSwaggerConfig {

    @Bean
    @ConditionalOnMissingBean
    public Docket apiDocket(SwaggerProperties properties) {
        return new Docket(DocumentationType.OAS_30)
                .groupName("api")
                .select()
                .apis(RequestHandlerSelectors.basePackage(properties.getBasePackage()))
                .paths(PathSelectors.any())
                .build();
    }
}
