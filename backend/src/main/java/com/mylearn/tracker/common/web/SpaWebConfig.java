package com.mylearn.tracker.common.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Serves the Vue build (bundled into {@code classpath:/static} by the {@code ui} Maven profile) and
 * falls back to {@code index.html} for client-side routes such as {@code /plans/3}, so a page
 * refresh works. API and actuator paths are never answered with the SPA.
 *
 * <p>Without a bundled UI (dev mode, Docker backend container) this has no effect.
 */
@Configuration
public class SpaWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String path, Resource location) throws IOException {
                        Resource requested = location.createRelative(path);
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        if (path.startsWith("api/") || path.startsWith("actuator/") || path.contains(".")) {
                            return null; // real 404 for unknown API paths and missing files
                        }
                        Resource index = new ClassPathResource("/static/index.html");
                        return index.exists() ? index : null;
                    }
                });
    }
}
