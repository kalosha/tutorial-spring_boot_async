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

/**
 * StateService is a service class that provides methods related to states.
 * It is annotated with @Service to indicate that it is a Spring Service.
 * It is also annotated with @Slf4j, a Lombok annotation to provide a logger for the class.
 * The class has two fields: dataRepository and discountService.
 */
@Service
@Slf4j
public class StateService {

    private final DataRepository dataRepository;
    private final DiscountService discountService;

    /**
     * Constructor for StateService.
     * It initializes the dataRepository and discountService.
     *
     * @param dataRepository  the DataRepository to be used by the service.
     * @param discountService the DiscountService to be used by the service.
     */
    public StateService(final DataRepository dataRepository,
                        final DiscountService discountService) {
        this.dataRepository = dataRepository;
        this.discountService = discountService;
    }

    /**
     * This method retrieves all states and returns them as a list of IdNameModel.
     * It uses the dataRepository to get the states, and then maps each state to an IdNameModel.
     *
     * @return a list of IdNameModel representing all states.
     */
    public List<IdNameModel> getStates() {
        log.debug("Getting states");
        return dataRepository.getStates()
                .stream().map(state -> new IdNameModel(state.getId(), state.getCode()))
                .toList();
    }

    /**
     * This method retrieves a state by its ID and returns it as a StateModel.
     * It uses the dataRepository to get the states, filters them by the provided ID, and then maps the state to a StateModel.
     * If the state is not found, it throws a ResponseStatusException with a status of NOT_FOUND.
     *
     * @param stateId the ID of the state to be retrieved.
     * @return a StateModel representing the state with the provided ID.
     * @throws ResponseStatusException if the state is not found.
     */
    public StateModel getStateById(final int stateId) {
        log.debug("Getting state by ID: {}", stateId);
        return this.dataRepository.getStates()
                .stream()
                .filter(state -> state.getId() == stateId)
                .findFirst()
                .map(state -> new StateModel(state.getId(), state.getCode(), state.getPriceLimit(), discountService.getDiscounts(state.getDiscounts())))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "State not found by ID: " + stateId));
    }

    /**
     * This method retrieves a state by its code and returns it as a StateModel.
     * It uses the dataRepository to get the states, filters them by the provided code, and then maps the state to a StateModel.
     * If the state is not found, it throws a ResponseStatusException with a status of NOT_FOUND.
     *
     * @param code the code of the state to be retrieved.
     * @return a StateModel representing the state with the provided code.
     * @throws ResponseStatusException if the state is not found.
     */
    public StateModel getStateByCode(final String code) {
        log.debug("Getting state by code: {}", code);
        return this.dataRepository.getStates()
                .stream()
                .filter(state -> state.getCode().equalsIgnoreCase(code))
                .findFirst()
                .map(state -> new StateModel(state.getId(), state.getCode(), state.getPriceLimit(), discountService.getDiscounts(state.getDiscounts())))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "State not found by code: " + code));
    }

    /**
     * This method retrieves the discount ID by its code and type.
     * It uses the dataRepository to get the state by code, filters the discounts by the provided type, and then returns the discount ID.
     * If the discount is not found, it throws a ResponseStatusException with a status of NOT_FOUND.
     *
     * @param code the code of the discount to be retrieved.
     * @param type the type of the discount to be retrieved.
     * @return the ID of the discount with the provided code and type.
     * @throws ResponseStatusException if the discount is not found.
     */
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

    /**
     * This method retrieves the discounts of a state by its ID and returns them as a list of DiscountModel.
     * It uses the getStateById method to get the state by the provided ID, and then retrieves the discounts of the state.
     *
     * @param stateId the ID of the state whose discounts are to be retrieved.
     * @return a list of DiscountModel representing the discounts of the state with the provided ID.
     */
    public List<DiscountModel> getDiscountsByStateId(final int stateId) {
        log.debug("Getting discounts by state ID: {}", stateId);
        return this.getStateById(stateId).discounts();
    }

    /**
     * This method retrieves the dealers of a state by its ID and returns them as a list of IdNameModel.
     * It uses the dataRepository to get the dealers, filters them by the provided state ID, and then maps each dealer to an IdNameModel.
     *
     * @param stateId the ID of the state whose dealers are to be retrieved.
     * @return a list of IdNameModel representing the dealers of the state with the provided ID.
     */
    public List<IdNameModel> getDealersByStateId(final int stateId) {
        log.debug("Getting dealers by state ID: {}", stateId);
        return this.dataRepository.getDealers()
                .stream()
                .filter(dealer -> dealer.getStates().contains(stateId))
                .map(dealer -> new IdNameModel(dealer.getId(), dealer.getName()))
                .toList();
    }

    /**
     * This method retrieves the dealers of a state by its code and returns them as a list of IdNameModel.
     * It uses the dataRepository to get the dealers, filters them by the provided state code, and then maps each dealer to an IdNameModel.
     *
     * @param code the code of the state whose dealers are to be retrieved.
     * @return a list of IdNameModel representing the dealers of the state with the provided code.
     */
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