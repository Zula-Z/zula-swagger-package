package com.zula.swagger.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Springfox 3.0 + Spring Boot 2.6+ can blow up with NPE in PatternsRequestCondition
 * when the new PathPatternParser is used. Force it to use only handler mappings
 * that still rely on AntPathMatcher.
 */
@Configuration
@ConditionalOnClass(name = "springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider")
@ConditionalOnProperty(name = "zula.swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SpringfoxPathMatchingConfig {

    private static final String WEB_MVC_HANDLER_PROVIDER_CLASS =
            "springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider";

    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (isWebMvcRequestHandlerProvider(bean)) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }
        };
    }

    private static boolean isWebMvcRequestHandlerProvider(Object bean) {
        try {
            Class<?> providerClass = ClassUtils.forName(
                    WEB_MVC_HANDLER_PROVIDER_CLASS, SpringfoxPathMatchingConfig.class.getClassLoader());
            return providerClass.isInstance(bean);
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static void customizeSpringfoxHandlerMappings(List<RequestMappingInfoHandlerMapping> mappings) {
        List<RequestMappingInfoHandlerMapping> filtered = mappings.stream()
                .filter(mapping -> mapping.getPatternParser() == null)
                .collect(Collectors.toList());
        mappings.clear();
        mappings.addAll(filtered);
    }

    @SuppressWarnings("unchecked")
    private static List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
        Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
        if (field == null) {
            return List.of();
        }
        ReflectionUtils.makeAccessible(field);
        try {
            return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
