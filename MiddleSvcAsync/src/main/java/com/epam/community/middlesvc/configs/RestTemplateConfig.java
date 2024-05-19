package com.epam.community.middlesvc.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;

import java.util.List;

/**
 * Configuration class for setting up the RestTemplate.
 * This class is annotated with @Configuration to indicate that it is a source of bean definitions.
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    @Value("${http.connection.timeout:10000}")
    private int httpConnectionTimeout;

    @Value("${http.connection.request.timeout:10000}")
    private int httpConnectionRequestTimeout;

    /**
     * Bean for the HttpComponentsClientHttpRequestFactory.
     * This method configures and initializes a HttpComponentsClientHttpRequestFactory with the properties defined above.
     *
     * @return a new instance of HttpComponentsClientHttpRequestFactory
     */
    @Bean
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {

        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(this.httpConnectionTimeout);
        factory.setConnectionRequestTimeout(this.httpConnectionRequestTimeout);

        return factory;
    }

    /**
     * Bean for the RestTemplateCustomizer.
     * This method configures and initializes a RestTemplateCustomizer.
     * @return a new instance of RestTemplateCustomizer
     */
    @Bean
    RestTemplateCustomizer restTemplateCustomizer() {
        return restTemplate -> {
            restTemplate.setRequestFactory(httpComponentsClientHttpRequestFactory());

            final DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
            defaultUriBuilderFactory.setEncodingMode(EncodingMode.VALUES_ONLY);
            restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);

            final List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            interceptors.add((request, body, execution) -> {
                log.info("Request: {} {}", request.getMethod(), request.getURI());
                return execution.execute(request, body);
            });

        };
    }

    /**
     * Bean for the RestTemplate.
     * This method configures and initializes a RestTemplate with the properties defined above.
     * @param restTemplateBuilder the RestTemplateBuilder
     * @return a new instance of RestTemplate
     */
    @Bean(name = "defaultRestTemplate")
    public RestTemplate currencyRestTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
