package com.epam.community.middlesvc.models;

import java.util.List;

public record DealerModel(
        int id,
        String name,
        int overhead,
        List<DealerCarModel> cars) {

    /**
     * This method calculates the price of a car with the dealer's overhead.
     *
     * @param price the original price of the car.
     * @return the price of the car with the dealer's overhead.
     */
    public int getPriceWithOverhead(final int price) {
        return price + ((price * this.overhead()) / 100);
    }
}
