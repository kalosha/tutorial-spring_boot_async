package com.epam.community.middlesvc.clients.responses;

import java.util.List;

public record StateResponse(int id,
                            String code,
                            int priceLimit,
                            List<DiscountResponse> discounts) {
}
