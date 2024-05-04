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

@Component
@Slf4j
public class StateClient {

    private final String statesUrl;
    private final String stateInfoUrl;
    private final String stateDiscountUrl;
    private final String dealersByCodeUrl;

    private final RestTemplate restTemplate;

    public StateClient(@Value("${com.epam.community.endpoints.states.list}") final String statesUrl,
                       @Value("${com.epam.community.endpoints.states.list}/code/{code}") final String stateInfoUrl,
                       @Value("${com.epam.community.endpoints.states.discount}") final String stateDiscountUrl,
                       @Value("${com.epam.community.endpoints.states.dealersByCode}") final String dealersByCodeUrl,
                       @Qualifier("defaultRestTemplate") final RestTemplate restTemplate) {
        this.statesUrl = statesUrl;
        this.stateInfoUrl = stateInfoUrl;
        this.stateDiscountUrl = stateDiscountUrl;
        this.dealersByCodeUrl = dealersByCodeUrl;
        this.restTemplate = restTemplate;
    }

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
