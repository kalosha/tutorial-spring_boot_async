package com.epam.community.middlesvc.models;

import lombok.Builder;

import java.util.List;

@Builder
public record DealerModel(
        int id,
        String name,
        int overhead,
        List<DealerCarModel> cars) {

    public int getPriceWithOverhead(final int price) {
        return price + ((price * this.overhead()) / 100);
    }
}
