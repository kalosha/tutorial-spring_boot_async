package com.epam.community.middlesvc.clients;

import com.epam.community.middlesvc.clients.responses.DealerResponse;
import com.epam.community.middlesvc.models.CarFullTypeEnum;
import com.epam.community.middlesvc.models.CarTypeEnum;
import com.epam.community.middlesvc.models.DealerCarModel;
import com.epam.community.middlesvc.models.DealerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * This is a client class for interacting with the dealer service.
 * It uses the RestTemplate to make HTTP requests.
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
     * This method retrieves dealer information from the dealer service.
     * It makes an asynchronous GET request to the dealer service and returns a CompletableFuture of DealerModel.
     * @param id The ID of the dealer to retrieve information for.
     * @return A CompletableFuture of DealerModel containing the dealer information.
     */
    @Async("generalAsyncExecutor")
    public CompletableFuture<DealerModel> getDealerInfo(final int id) {
        log.info("Getting dealer from downstream service by ID: {}", id);
        return CompletableFuture.completedFuture(Objects.requireNonNull(
                        this.restTemplate.exchange(
                                this.url,
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<DealerResponse>() {
                                },
                                Map.of("id", id)
                        ).getBody()
                )
        ).thenApply(dealerResponse ->
                DealerModel.builder()
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
                                ).toList())
                        .build()
        );
    }
}
