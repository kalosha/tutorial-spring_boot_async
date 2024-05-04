package com.epam.community.downstreamserver.services;

import com.epam.community.downstreamserver.data.DataRepository;
import com.epam.community.downstreamserver.models.DiscountModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class DiscountService {
    private final DataRepository dataRepository;

    public DiscountService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

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
