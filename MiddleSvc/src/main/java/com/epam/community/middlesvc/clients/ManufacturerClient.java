package com.epam.community.middlesvc.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Slf4j
public class ManufacturerClient {
    private final String url;

    private final RestTemplate restTemplate;

    public ManufacturerClient(@Value("${com.epam.community.endpoints.manufacturers.price}") final String url,
                              @Qualifier("defaultRestTemplate") final RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public Integer getPriceByCarId(int id) {
        log.info("Getting price from downstream service by car ID: {}", id);
        return this.restTemplate.exchange(
                this.url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Integer>() {
                },
                Map.of("id", id)
        ).getBody();
    }
}
