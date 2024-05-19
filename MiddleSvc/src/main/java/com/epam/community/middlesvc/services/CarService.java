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

/**
 * This class is responsible for handling business logic related to cars.
 * It uses the DealerClient, StateClient, and ManufacturerClient for data fetching.
 */
@Service
@Slf4j
public class CarService {

    private final DealerClient dealerClient;
    private final StateClient stateClient;
    private final ManufacturerClient manufacturerClient;

    /**
     * Constructor for the CarService class.
     *
     * @param dealerClient       the DealerClient to be used for fetching dealer data.
     * @param stateClient        the StateClient to be used for fetching state data.
     * @param manufacturerClient the ManufacturerClient to be used for fetching manufacturer data.
     */
    public CarService(final DealerClient dealerClient,
                      final StateClient stateClient,
                      final ManufacturerClient manufacturerClient) {
        this.dealerClient = dealerClient;
        this.stateClient = stateClient;
        this.manufacturerClient = manufacturerClient;
    }

    /**
     * This method fetches the cheapest cars in a state.
     * It optionally filters the cars by car type and car full type, and limits the number of cars returned.
     * @param stateCode the state code.
     * @param carType the car type (optional).
     * @param carFullType the car full type (optional).
     * @param maxCars the maximum number of cars to return.
     * @return a list of CarModel objects representing the cheapest cars in the state.
     */
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

    /**
     * This method generates a unique car ID.
     * @param stateCode the state code.
     * @param dealerId the dealer ID.
     * @param carId the car ID.
     * @return a string representing the unique car ID.
     */
    private static String generateCarId(final String stateCode,
                                        final int dealerId,
                                        final int carId) {
        return String.format("%s-D%d-C%d", stateCode, dealerId, carId);
    }
}
