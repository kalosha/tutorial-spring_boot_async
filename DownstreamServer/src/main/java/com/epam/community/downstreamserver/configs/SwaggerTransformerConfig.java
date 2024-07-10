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

    @Value("${com.epam.community.profile:dev}")
    private String profile;

    /**
     * Constructor for the SwaggerTransformerConfig class.
     *
     * @param environment The environment in which the application is running.
     */
    public SwaggerTransformerConfig(final Environment environment) {
        this.environment = environment;
    }

    /**
     * Bean for transforming the Swagger index page.
     *
     * @param swaggerUiConfig           The configuration properties for the Swagger UI.
     * @param swaggerUiOAuthProperties  The OAuth properties for the Swagger UI.
     * @param swaggerUiConfigParameters The configuration parameters for the Swagger UI.
     * @param swaggerWelcomeCommon      The common welcome page for the Swagger UI.
     * @param objectMapperProvider      The provider for the ObjectMapper.
     * @return A SwaggerIndexTransformer that transforms the Swagger index page.
     */
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

    /**
     * Class for transforming the Swagger CSS.
     */
    class TransformSwaggerCSS extends SwaggerIndexPageTransformer {
        private final Environment environment;

        /**
         * Constructor for the TransformSwaggerCSS class.
         *
         * @param swaggerUiConfig The configuration properties for the Swagger UI.
         * @param swaggerUiOAuthProperties The OAuth properties for the Swagger UI.
         * @param swaggerUiConfigParameters The configuration parameters for the Swagger UI.
         * @param swaggerWelcomeCommon The common welcome page for the Swagger UI.
         * @param objectMapperProvider The provider for the ObjectMapper.
         * @param environment The environment in which the application is running.
         */
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

        /**
         * Transforms the resource.
         *
         * @param request The HTTP request.
         * @param resource The resource to be transformed.
         * @param transformer The resource transformer chain.
         * @return The transformed resource.
         * @throws IOException If an I/O error occurs.
         */
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

    /**
     * Returns the custom CSS.
     *
     * @return The custom CSS.
     */
    private String getCustomCss() {
        return "body { background-color: rgba(255, 0, 0, 0.10) !important; }";
    }
}
