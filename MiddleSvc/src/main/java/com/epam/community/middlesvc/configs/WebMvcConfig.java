package com.epam.community.middlesvc.configs;

import com.epam.community.middlesvc.contollers.RestConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addRedirectViewController(RestConstants.API_DELIMITER, RestConstants.SWAGGER_UI);
    }
}
