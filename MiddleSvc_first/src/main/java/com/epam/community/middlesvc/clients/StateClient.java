package com.epam.community.middlesvc.clients;

import com.epam.community.middlesvc.clients.responses.IdNameResponse;
import com.epam.community.middlesvc.clients.responses.StateResponse;
import com.epam.community.middlesvc.models.DiscountModel;
import com.epam.community.middlesvc.models.IdNameModel;
import com.epam.community.middlesvc.models.StateModel;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<String> getStateCodes() {
        log.info("Getting state codes from downstream service");
        return Objects.requireNonNull(
                        this.restTemplate.exchange(
                                this.statesUrl,
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<List<IdNameResponse>>() {
                                }
                        ).getBody())
                .stream()
                .map(IdNameResponse::name)
                .toList();
    }

    public List<IdNameModel> getDealersByState(final String code) {
        log.info("Getting dealers from downstream service by state: {}", code);
        return Objects.requireNonNull(
                        this.restTemplate.exchange(
                                this.dealersByCodeUrl,
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<List<IdNameResponse>>() {
                                },
                                Map.of("code", code)
                        ).getBody()).stream()
                .map(response -> new IdNameModel(response.id(), response.name()))
                .toList();
    }

    public StateModel getStateInformation(final String code) {
        log.info("Getting state information from downstream service by state: {}", code);
        val stateResponse = Objects.requireNonNull(this.restTemplate.exchange(
                        this.stateInfoUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<StateResponse>() {
                        },
                        Map.of("code", code)
                ).getBody()
        );

        return new StateModel(stateResponse.id(),
                stateResponse.code(),
                stateResponse.priceLimit(),
                stateResponse.discounts()
                        .stream()
                        .map(discount -> new DiscountModel(discount.id(), discount.name(), discount.type()))
                        .toList()
        );
    }

    public int getDiscountByType(final String stateCode, final String type) {
        log.info("Getting discount by type from downstream service by state: {} and type: {}", stateCode, type);
        try {
            return this.restTemplate.exchange(
                    this.stateDiscountUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Integer>() {
                    },
                    Map.of("code", stateCode, "type", type)
            ).getBody().intValue();
        } catch (Exception e) {
            log.error("Error getting discount by type from downstream service by state: {} and type: {}", stateCode, type, e);
            return 0;
        }
    }
}
