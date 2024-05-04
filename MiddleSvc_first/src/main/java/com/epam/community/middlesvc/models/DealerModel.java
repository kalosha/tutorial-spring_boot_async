package com.epam.community.middlesvc.models;

import java.util.List;

public record DealerModel(
        int id,
        String name,
        int overhead,
        List<DealerCarModel> cars) {

    public int getPriceWithOverhead(final int price) {
        return price + ((price * this.overhead()) / 100);
    }
}
