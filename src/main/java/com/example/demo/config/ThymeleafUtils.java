package com.example.demo.config;

import org.springframework.stereotype.Component;
import org.springframework.core.io.ResourceLoader;

@Component
public class ThymeleafUtils {

    private final ResourceLoader resourceLoader;

    public ThymeleafUtils(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public boolean fragmentExists(String fragmentPath) {
        try {
            return resourceLoader.getResource("classpath:templates/" + fragmentPath + ".html").exists();
        } catch (Exception e) {
            return false;
        }
    }
}