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
 * This is a client class for interacting with the Manufacturer service.
 * It uses Spring's RestTemplate to make HTTP requests.
 */
@Component
@Slf4j
public class ManufacturerClient {
    @Value("${com.epam.community.endpoints.manufacturers.price}")
    private String url;
    private final RestTemplate restTemplate;

    /**
     * Constructor for the ManufacturerClient class.
     *
     * @param restTemplate The RestTemplate to be used for making HTTP requests.
     */
    public ManufacturerClient(@Qualifier("defaultRestTemplate") final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * This method retrieves the price of a car from the Manufacturer service by its ID.
     * @param id The ID of the car to retrieve the price for.
     * @return An Integer representing the price of the car.
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
