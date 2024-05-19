package com.epam.community.middlesvc.clients;

import com.epam.community.middlesvc.clients.responses.IdNameResponse;
import com.epam.community.middlesvc.clients.responses.StateResponse;
import com.epam.community.middlesvc.models.CarFullTypeEnum;
import com.epam.community.middlesvc.models.DiscountModel;
import com.epam.community.middlesvc.models.IdNameModel;
import com.epam.community.middlesvc.models.StateModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * This is a client class for interacting with the state service.
 * It uses the RestTemplate to make HTTP requests.
 */
@Component
@Slf4j
public class StateClient {

    @Value("${com.epam.community.endpoints.states.list}")
    private String statesUrl;
    @Value("${com.epam.community.endpoints.states.list}/code/{code}")
    private String stateInfoUrl;
    @Value("${com.epam.community.endpoints.states.discount}")
    private String stateDiscountUrl;
    @Value("${com.epam.community.endpoints.states.dealersByCode}")
    private String dealersByCodeUrl;

    private final RestTemplate restTemplate;

    /**
     * Constructor for the StateClient class.
     *
     * @param restTemplate The RestTemplate to be used for making HTTP requests.
     */
    public StateClient(@Qualifier("defaultRestTemplate") final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * This method retrieves state codes from the state service.
     * It makes an asynchronous GET request to the state service and returns a CompletableFuture of List of String.
     * @return A CompletableFuture of List of String containing the state codes.
     */
    @Async("generalAsyncExecutor")
    public CompletableFuture<List<String>> getStateCodes() {
        log.info("Getting state codes from downstream service");
        return CompletableFuture.completedFuture(
                Objects.requireNonNull(
                                this.restTemplate.exchange(
                                        this.statesUrl,
                                        HttpMethod.GET,
                                        null,
                                        new ParameterizedTypeReference<List<IdNameResponse>>() {
                                        }
                                ).getBody())
                        .stream()
                        .map(IdNameResponse::name)
                        .toList()
        );
    }

    /**
     * This method retrieves dealers by state from the state service.
     * It makes an asynchronous GET request to the state service and returns a CompletableFuture of List of IdNameModel.
     * @param code The code of the state to retrieve dealers for.
     * @return A CompletableFuture of List of IdNameModel containing the dealers by state.
     */
    @Async("generalAsyncExecutor")
    public CompletableFuture<List<IdNameModel>> getDealersByState(final String code) {
        log.info("Getting dealers from downstream service by state: {}", code);
        return CompletableFuture.completedFuture(
                Objects.requireNonNull(
                                this.restTemplate.exchange(
                                        this.dealersByCodeUrl,
                                        HttpMethod.GET,
                                        null,
                                        new ParameterizedTypeReference<List<IdNameResponse>>() {
                                        },
                                        Map.of("code", code)
                                ).getBody()).stream()
                        .map(response -> IdNameModel.builder()
                                .id(response.id())
                                .name(response.name())
                                .build())
                        .toList()
        );
    }

    /**
     * This method retrieves state information from the state service.
     * It makes an asynchronous GET request to the state service and returns a CompletableFuture of StateModel.
     * @param code The code of the state to retrieve information for.
     * @return A CompletableFuture of StateModel containing the state information.
     */
    @Async("generalAsyncExecutor")
    public CompletableFuture<StateModel> getStateInformation(final String code) {
        log.info("Getting state information from downstream service by state: {}", code);
        return CompletableFuture.completedFuture(
                Objects.requireNonNull(this.restTemplate.exchange(
                                this.stateInfoUrl,
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<StateResponse>() {
                                },
                                Map.of("code", code)
                        ).getBody()
                )
        ).thenApply(stateResponse ->
                StateModel.builder()
                        .id(stateResponse.id())
                        .code(stateResponse.code())
                        .priceLimit(stateResponse.priceLimit())
                        .discounts(stateResponse.discounts().stream()
                                .map(discount -> DiscountModel.builder()
                                        .id(discount.id())
                                        .name(discount.name())
                                        .percent(discount.percent())
                                        .fullType(CarFullTypeEnum.fromString(discount.type()))
                                        .build())
                                .toList())
                        .build()
        );
    }

    /**
     * This method retrieves the discount by type from the state service.
     * It makes an asynchronous GET request to the state service and returns a CompletableFuture of Integer.
     * @param stateCode The code of the state to retrieve the discount for.
     * @param type The type of the car to retrieve the discount for.
     * @return A CompletableFuture of Integer containing the discount by type.
     */
    @Async("generalAsyncExecutor")
    public CompletableFuture<Integer> getDiscountByType(final String stateCode,
                                                        final CarFullTypeEnum type) {
        log.info("Getting discount by type from downstream service by state: {} and type: {}", stateCode, type.name());
        return CompletableFuture.completedFuture(
                this.restTemplate.exchange(
                        this.stateDiscountUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Integer>() {
                        },
                        Map.of("code", stateCode, "type", type.name())
                ).getBody()
        );
    }
}
