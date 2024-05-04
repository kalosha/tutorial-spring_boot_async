package com.epam.community.downstreamserver.models;

import java.util.List;

public record StateModel(int id, String code, int priceLimit, List<DiscountModel> discounts) {
}
