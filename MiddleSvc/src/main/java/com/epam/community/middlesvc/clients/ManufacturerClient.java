package com.epam.community.middlesvc.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * This class is a client for interacting with the Dealer service.
 * It uses Spring's RestTemplate to make HTTP requests.
 */
@Component
@Slf4j
public class ManufacturerClient {
    @Value("${com.epam.community.endpoints.manufacturers.price}")
    private String url;
    private final RestTemplate restTemplate;

    /**
     * Constructs a new ManufacturerClient.
     *
     * @param restTemplate the RestTemplate to use for HTTP requests
     */
    public ManufacturerClient(@Qualifier("defaultRestTemplate") final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Retrieves the price of a car from the Manufacturer service by its ID.
     *
     * @param id the ID of the car
     * @return the price of the car
     */
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
