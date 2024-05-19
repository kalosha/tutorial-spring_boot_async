package com.epam.community.downstreamserver.configs;

import com.epam.community.downstreamserver.controllers.RestConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfig is a configuration class that implements WebMvcConfigurer.
 * It is annotated with @Configuration to indicate that it is a source of bean definitions.
 * It is also annotated with @Slf4j, a Lombok annotation to provide a logger for the class.
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * This method is overridden from WebMvcConfigurer interface.
     * It is used to add view controllers to the registry.
     * In this case, it adds a redirect view controller that redirects from API_DELIMITER to SWAGGER_UI.
     *
     * @param registry the ViewControllerRegistry to which the redirect view controller is added.
     */
    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addRedirectViewController(RestConstants.API_DELIMITER, RestConstants.SWAGGER_UI);
    }
}
