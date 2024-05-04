package com.epam.community.middlesvc.services;

import com.epam.community.middlesvc.clients.DealerClient;
import com.epam.community.middlesvc.clients.ManufacturerClient;
import com.epam.community.middlesvc.clients.StateClient;
import com.epam.community.middlesvc.models.CarFullTypeEnum;
import com.epam.community.middlesvc.models.CarModel;
import com.epam.community.middlesvc.models.CarTypeEnum;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class CarService {

    private final DealerClient dealerClient;
    private final StateClient stateClient;
    private final ManufacturerClient manufacturerClient;

    public CarService(final DealerClient dealerClient,
                      final StateClient stateClient,
                      final ManufacturerClient manufacturerClient) {
        this.dealerClient = dealerClient;
        this.stateClient = stateClient;
        this.manufacturerClient = manufacturerClient;
    }


    public List<CarModel> getCheapestCarsInState(final String stateCode,
                                                 final CarTypeEnum carType,
                                                 final CarFullTypeEnum carFullType,
                                                 final int maxCars) {
        log.info("Getting 3 cheapest cars in State: {} CarType: {}, CarFullType: {}", stateCode, carType, carFullType);

        // DATA collecting stage
        val carModels = new HashMap<String, CarModel>();
        val stateInfo = this.stateClient.getStateInformation(stateCode); // Downstream call 0

        this.stateClient.getDealersByState(stateCode) // Downstream call 1
                .forEach(dealer -> { // FIRST iteration
                    val dealerInfo = this.dealerClient.getDealerInfo(dealer.id()); // Downstream call 2
                    dealerInfo.cars().stream()
                            .filter(car -> ObjectUtils.isEmpty(carType) || (carType == car.type()))
                            .filter(car -> ObjectUtils.isEmpty(carFullType) || (carFullType == car.fullType()))
                            .forEach(car -> { // SECOND iteration
                                final int[] price = {this.manufacturerClient.getPriceByCarId(car.id())}; // Downstream call 3
                                if (price[0] > stateInfo.priceLimit()) {
                                    stateInfo.discounts().stream()
                                            .filter(discount -> discount.type().equalsIgnoreCase(car.fullType().name()))
                                            .findFirst()
                                            .ifPresent(discount -> price[0] -= ((price[0] * this.stateClient.getDiscountByType(stateCode, discount.type())) / 100)); // Downstream call 4
                                }

                                val carModel = new CarModel(
                                        car.id(),
                                        car.model(),
                                        car.year(),
                                        dealer.id(),
                                        dealer.name(),
                                        dealerInfo.getPriceWithOverhead(price[0]),
                                        car.manufacturer(),
                                        car.manufacturerId(),
                                        car.fullType(),
                                        car.type());

                                carModels.putIfAbsent(generateCarId(stateCode, dealer.id(), car.id()), carModel);
                            });
                });

        // DATA manipulation stage, in our case sorting and limiting
        return carModels.values()
                .stream()
                .sorted(Comparator.comparingInt(CarModel::price))
                .limit(maxCars)
                .toList();
    }

    private static String generateCarId(final String stateCode,
                                        final int dealerId,
                                        final int carId) {
        return String.format("%s-D%d-C%d", stateCode, dealerId, carId);
    }
}
