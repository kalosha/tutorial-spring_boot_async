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
 * This is a client class for interacting with the Dealer service.
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
     * @param restTemplate The RestTemplate to be used for making HTTP requests.
     */
    public DealerClient(@Qualifier("defaultRestTemplate") final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * This method retrieves dealer information from the Dealer service.
     * @param id The ID of the dealer to retrieve.
     * @return A DealerModel object containing the dealer's information.
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

        return DealerModel.builder()
                .id(dealerResponse.id())
                .name(dealerResponse.name())
                .overhead(dealerResponse.overhead())
                .cars(dealerResponse.cars().stream()
                        .map(car -> DealerCarModel.builder()
                                .id(car.id())
                                .model(car.model())
                                .year(car.year())
                                .manufacturer(car.manufacturer().name())
                                .manufacturerId(car.manufacturer().id())
                                .fullType(CarFullTypeEnum.fromString(car.fullType()))
                                .type(CarTypeEnum.fromString(car.type()))
                                .build()
                        ).toList()
                ).build();
    }
}
