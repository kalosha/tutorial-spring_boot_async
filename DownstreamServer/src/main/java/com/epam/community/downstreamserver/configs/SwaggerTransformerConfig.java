package com.epam.community.downstreamserver.configs;

import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Configuration
public class SwaggerTransformerConfig {
    private final Environment environment;

    private final String profile;

    public SwaggerTransformerConfig(final Environment environment,
                                    @Value("${com.epam.community.profile:dev}") final String profile) {
        this.environment = environment;
        this.profile = profile;
    }

    @Bean
    public SwaggerIndexTransformer swaggerIndexTransformer(
            final SwaggerUiConfigProperties swaggerUiConfig,
            final SwaggerUiOAuthProperties swaggerUiOAuthProperties,
            final SwaggerUiConfigParameters swaggerUiConfigParameters,
            final SwaggerWelcomeCommon swaggerWelcomeCommon,
            final ObjectMapperProvider objectMapperProvider) {
        return new TransformSwaggerCSS(swaggerUiConfig,
                swaggerUiOAuthProperties,
                swaggerUiConfigParameters,
                swaggerWelcomeCommon,
                objectMapperProvider, this.environment);
    }


    class TransformSwaggerCSS extends SwaggerIndexPageTransformer {
        private final Environment environment;

        public TransformSwaggerCSS(final SwaggerUiConfigProperties swaggerUiConfig,
                                   final SwaggerUiOAuthProperties swaggerUiOAuthProperties,
                                   final SwaggerUiConfigParameters swaggerUiConfigParameters,
                                   final SwaggerWelcomeCommon swaggerWelcomeCommon,
                                   final ObjectMapperProvider objectMapperProvider,
                                   final Environment environment) {
            super(swaggerUiConfig,
                    swaggerUiOAuthProperties,
                    swaggerUiConfigParameters,
                    swaggerWelcomeCommon,
                    objectMapperProvider);
            this.environment = environment;
        }

        @Override
        public Resource transform(final HttpServletRequest request,
                                  final Resource resource,
                                  final ResourceTransformerChain transformer)
                throws IOException {
            if (profile.equals(environment.getActiveProfiles()[0])
                    && resource.toString().contains("swagger-ui.css")) {
                final InputStream inputStream = resource.getInputStream();
                final InputStreamReader isr = new InputStreamReader(inputStream);
                try (BufferedReader br = new BufferedReader(isr)) {
                    String css = br.lines().collect(Collectors.joining());
                    final byte[] transformedContent = (css + getCustomCss()).getBytes();
                    return new TransformedResource(resource, transformedContent);
                } // AutoCloseable br > isr > is
            }
            return super.transform(request, resource, transformer);
        }
    }

    private String getCustomCss() {
        return "body { background-color: rgba(0, 255, 0, 0.50) !important; }";
    }
}
