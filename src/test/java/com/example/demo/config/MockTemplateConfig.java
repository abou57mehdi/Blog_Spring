package com.example.demo.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@TestPropertySource(properties = {
    "spring.thymeleaf.prefix=classpath:/templates-test/",
    "spring.thymeleaf.check-template-location=false"
})
public class MockTemplateConfig {

    @Bean
    @Primary
    public ThymeleafUtils thymeleafUtils() {
        ThymeleafUtils mockThymeleafUtils = Mockito.mock(ThymeleafUtils.class);
        Mockito.when(mockThymeleafUtils.fragmentExists(Mockito.anyString())).thenReturn(false);
        return mockThymeleafUtils;
    }
} 