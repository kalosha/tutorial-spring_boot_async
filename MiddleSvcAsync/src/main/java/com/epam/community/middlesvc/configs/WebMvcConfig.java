package com.epam.community.middlesvc.configs;

import com.epam.community.middlesvc.contollers.RestConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for setting up the Web MVC.
 * This class is annotated with @Configuration to indicate that it is a source of bean definitions.
 * The @EnableWebMvc annotation switches on Spring’s MVC features.
 * This class implements WebMvcConfigurer to provide callback methods to customize the Java-based configuration for Spring MVC.
 */
@Slf4j
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Add view controllers to the registry.
     * This method overrides the addViewControllers method from the WebMvcConfigurer interface.
     * It adds a redirect view controller that redirects requests to the API delimiter to the Swagger UI.
     *
     * @param registry the ViewControllerRegistry
     */
    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addRedirectViewController(RestConstants.API_DELIMITER, RestConstants.SWAGGER_UI);
    }
}
