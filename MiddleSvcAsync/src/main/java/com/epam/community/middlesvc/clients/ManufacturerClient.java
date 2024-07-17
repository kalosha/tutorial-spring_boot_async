package com.epam.community.middlesvc.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This is a client class for interacting with the manufacturer service.
 * It uses the RestTemplate to make HTTP requests.
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
     * This method retrieves the price of a car from the manufacturer service.
     * It makes an asynchronous GET request to the manufacturer service and returns a CompletableFuture of Integer.
     *
     * @param id The ID of the car to retrieve the price for.
     * @return A CompletableFuture of Integer containing the price of the car.
     */
    @Async("generalAsyncExecutor")
    public CompletableFuture<Integer> getPriceByCarId(final int id) {
        return CompletableFuture.completedFuture(this.request(id));
    }

    private Integer request(final int id) {
        log.info("Getting price from downstream service by car ID: {}", id);
        return this.restTemplate.getForObject(
                this.url,
                Integer.class,
                Map.of("id", id)
        );
    }
}
