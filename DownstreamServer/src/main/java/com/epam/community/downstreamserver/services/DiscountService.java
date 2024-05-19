package com.epam.community.downstreamserver.services;

import com.epam.community.downstreamserver.data.DataRepository;
import com.epam.community.downstreamserver.models.DiscountModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * DiscountService is a service class that provides methods related to discounts.
 * It is annotated with @Service to indicate that it is a Spring Service.
 * It is also annotated with @Slf4j, a Lombok annotation to provide a logger for the class.
 * The class has one field: dataRepository.
 */
@Service
@Slf4j
public class DiscountService {
    private final DataRepository dataRepository;

    /**
     * Constructor for DiscountService.
     * It initializes the dataRepository.
     *
     * @param dataRepository the DataRepository to be used by the service.
     */
    public DiscountService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * This method retrieves a discount by its ID and returns it as a DiscountModel.
     * It uses the dataRepository to get the discounts, filters them by the provided ID, and then maps the discount to a DiscountModel.
     * If the discount is not found, it throws a ResponseStatusException with a status of NOT_FOUND.
     *
     * @param discountId the ID of the discount to be retrieved.
     * @return a DiscountModel representing the discount with the provided ID.
     * @throws ResponseStatusException if the discount is not found.
     */
    public DiscountModel getDiscountById(int discountId) {
        log.debug("Getting discount by ID: {}", discountId);
        return this.dataRepository.getDiscounts()
                .stream()
                .filter(discount -> discount.getId() == discountId)
                .findFirst()
                .map(discount -> new DiscountModel(discount.getId(),
                        discount.getName(),
                        discount.getType()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount not found by ID: " + discountId));
    }

    /**
     * This method retrieves discounts by their IDs and returns them as a list of DiscountModel.
     * It uses the dataRepository to get the discounts, filters them by the provided IDs, and then maps each discount to a DiscountModel.
     *
     * @param ids a list of IDs of the discounts to be retrieved.
     * @return a list of DiscountModel representing the discounts with the provided IDs.
     */
    public List<DiscountModel> getDiscounts(List<Integer> ids) {
        log.debug("Getting discounts by IDs: {}", ids);
        return this.dataRepository.getDiscounts()
                .stream()
                .filter(discount -> ids.contains(discount.getId()))
                .map(discount -> new DiscountModel(discount.getId(),
                        discount.getName(),
                        discount.getType()))
                .toList();
    }
}
