package com.zula.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnClass({OpenAPI.class, GroupedOpenApi.class})
@ConditionalOnProperty(name = "zula.swagger.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfig {

    private static final String SECURITY_SCHEME_NAME = "JWT Auth";

    @Bean
    @ConditionalOnMissingBean
    public GroupedOpenApi groupedOpenApi(SwaggerProperties properties) {
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder().group("api");
        if (StringUtils.hasText(properties.getBasePackage())) {
            builder.packagesToScan(properties.getBasePackage());
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenAPI customOpenAPI(SwaggerProperties properties) {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .info(new Info()
                        .title(properties.getTitle())
                        .description(properties.getDescription())
                        .version(properties.getVersion()));
    }
}
