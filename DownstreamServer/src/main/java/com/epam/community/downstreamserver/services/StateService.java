package com.epam.community.downstreamserver.services;

import com.epam.community.downstreamserver.data.DataRepository;
import com.epam.community.downstreamserver.generated.Discount;
import com.epam.community.downstreamserver.generated.State;
import com.epam.community.downstreamserver.models.DiscountModel;
import com.epam.community.downstreamserver.models.IdNameModel;
import com.epam.community.downstreamserver.models.StateModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class StateService {

    private final DataRepository dataRepository;
    private final DiscountService discountService;

    public StateService(final DataRepository dataRepository,
                        final DiscountService discountService) {
        this.dataRepository = dataRepository;
        this.discountService = discountService;
    }

    public List<IdNameModel> getStates() {
        log.debug("Getting states");
        return dataRepository.getStates()
                .stream().map(state -> new IdNameModel(state.getId(), state.getCode()))
                .toList();
    }


    public StateModel getStateById(final int stateId) {
        log.debug("Getting state by ID: {}", stateId);
        return this.dataRepository.getStates()
                .stream()
                .filter(state -> state.getId() == stateId)
                .findFirst()
                .map(state -> new StateModel(state.getId(), state.getCode(), state.getPriceLimit(), discountService.getDiscounts(state.getDiscounts())))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "State not found by ID: " + stateId));
    }

    public StateModel getStateByCode(final String code) {
        log.debug("Getting state by code: {}", code);
        return this.dataRepository.getStates()
                .stream()
                .filter(state -> state.getCode().equalsIgnoreCase(code))
                .findFirst()
                .map(state -> new StateModel(state.getId(), state.getCode(), state.getPriceLimit(), discountService.getDiscounts(state.getDiscounts())))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "State not found by code: " + code));
    }

    public Integer getDiscountIdByCodeAndType(final String code,
                                              final String type) {
        log.debug("Getting discount ID by code: {} and type: {}", code, type);
        return this.getStateByCode(code).discounts()
                .stream()
                .filter(discount -> discount.type().equalsIgnoreCase(type))
                .findFirst()
                .map(discount -> this.dataRepository.getDiscounts()
                        .stream()
                        .filter(f -> f.getId().equals(discount.id()))
                        .findFirst()
                        .map(Discount::getPercent)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount not found by ID: " + discount.id()))
                )
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount not found by code: " + code + " and type: " + type));
    }

    public List<DiscountModel> getDiscountsByStateId(final int stateId) {
        log.debug("Getting discounts by state ID: {}", stateId);
        return this.getStateById(stateId).discounts();
    }

    public List<IdNameModel> getDealersByStateId(final int stateId) {
        log.debug("Getting dealers by state ID: {}", stateId);
        return this.dataRepository.getDealers()
                .stream()
                .filter(dealer -> dealer.getStates().contains(stateId))
                .map(dealer -> new IdNameModel(dealer.getId(), dealer.getName()))
                .toList();
    }

    public List<IdNameModel> getDealersByStateCode(final String code) {
        log.debug("Getting dealers by state Code: {}", code);
        return this.dataRepository.getDealers()
                .stream()
                .filter(dealer -> dealer.getStates().contains(
                        this.dataRepository.getStates()
                                .stream()
                                .filter(state -> state.getCode().equalsIgnoreCase(code.trim()))
                                .findFirst()
                                .map(State::getId)
                                .orElse(-1)
                ))
                .map(dealer -> new IdNameModel(dealer.getId(), dealer.getName()))
                .toList();
    }

}