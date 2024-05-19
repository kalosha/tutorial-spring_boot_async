package com.epam.community.middlesvc.clients;

import com.epam.community.middlesvc.clients.responses.DealerResponse;
import com.epam.community.middlesvc.models.CarFullTypeEnum;
import com.epam.community.middlesvc.models.CarTypeEnum;
import com.epam.community.middlesvc.models.DealerCarModel;
import com.epam.community.middlesvc.models.DealerModel;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

/**
 * This class is a client for interacting with the Dealer service.
 * It uses Spring's RestTemplate to make HTTP requests.
 */
@Component
@Slf4j
public class DealerClient {

    @Value("${com.epam.community.endpoints.dealers.cars}")
    private String url;
    private final RestTemplate restTemplate;

    /**
     * Constructor for the DealerClient class.
     *
     * @param restTemplate the RestTemplate instance to use for making HTTP requests
     */
    public DealerClient(@Qualifier("defaultRestTemplate") final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * This method retrieves a dealer by ID from the Dealer service.
     *
     * @param id the ID of the dealer to retrieve
     * @return a DealerModel object representing the dealer
     */
    public DealerModel getDealerInfo(int id) {
        log.info("Getting dealer from downstream service by ID: {}", id);
        val dealerResponse = Objects.requireNonNull(
                this.restTemplate.exchange(
                        this.url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<DealerResponse>() {
                        },
                        Map.of("id", id)
                ).getBody()
        );

        return new DealerModel(
                dealerResponse.id(),
                dealerResponse.name(),
                dealerResponse.overhead(),
                dealerResponse.cars().stream()
                        .map(car -> new DealerCarModel(
                                car.id(),
                                car.model(),
                                car.year(),
                                car.manufacturer().name(),
                                car.manufacturer().id(),
                                CarFullTypeEnum.fromString(car.fullType()),
                                CarTypeEnum.fromString(car.type())
                        )).toList()
        );
    }
}
