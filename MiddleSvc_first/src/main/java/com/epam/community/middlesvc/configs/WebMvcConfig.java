package com.epam.community.middlesvc.configs;

import com.epam.community.middlesvc.contollers.RestConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This is a configuration class for setting up the Web MVC.
 * It implements Spring's WebMvcConfigurer interface to customize Spring MVC's configuration.
 */
@Slf4j
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * This method adds view controllers to the registry.
     * It sets up a redirect from the API delimiter to the Swagger UI.
     *
     * @param registry The ViewControllerRegistry to add view controllers to.
     */
    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addRedirectViewController(RestConstants.API_DELIMITER, RestConstants.SWAGGER_UI);
    }
}
