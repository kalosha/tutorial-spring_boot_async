package com.epam.community.middlesvc.configs;

import com.epam.community.middlesvc.contollers.RestConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class is responsible for configuring the MVC part of the application.
 * It implements the WebMvcConfigurer interface which provides callback methods to customize the Java-based configuration for Spring MVC.
 */
@Slf4j
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * This method is used to add view controllers to the registry.
     * In this case, it adds a redirect view controller that redirects requests from the API delimiter to the Swagger UI.
     *
     * @param registry the ViewControllerRegistry to which the view controller is to be added.
     */
    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addRedirectViewController(RestConstants.API_DELIMITER, RestConstants.SWAGGER_UI);
    }
}
