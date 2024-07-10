package com.epam.community.middlesvc.services;

import com.epam.community.middlesvc.clients.DealerClient;
import com.epam.community.middlesvc.clients.ManufacturerClient;
import com.epam.community.middlesvc.clients.StateClient;
import com.epam.community.middlesvc.models.*;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * This is a service class for handling operations related to cars.
 * It uses the DealerClient, StateClient, and ManufacturerClient to retrieve car data.
 */
@Service
@Slf4j
public class CarService {

    private final DealerClient dealerClient;
    private final StateClient stateClient;
    private final ManufacturerClient manufacturerClient;
    private final Executor generalAsyncExecutor;

    /**
     * Constructor for the CarService class.
     *
     * @param dealerClient         The DealerClient to be used for retrieving dealer data.
     * @param stateClient          The StateClient to be used for retrieving state data.
     * @param manufacturerClient   The ManufacturerClient to be used for retrieving manufacturer data.
     * @param generalAsyncExecutor The Executor to be used for asynchronous operations.
     */
    public CarService(final DealerClient dealerClient,
                      final StateClient stateClient,
                      final ManufacturerClient manufacturerClient,
                      @Qualifier("generalAsyncExecutor") final Executor generalAsyncExecutor) {
        this.dealerClient = dealerClient;
        this.stateClient = stateClient;
        this.manufacturerClient = manufacturerClient;
        this.generalAsyncExecutor = generalAsyncExecutor;
    }

    /**
     * This method retrieves the cheapest cars in a state.
     * It uses the DealerClient, StateClient, and ManufacturerClient to retrieve the data.
     * @param stateCode The code of the state to retrieve cars for.
     * @param carType The type of the car to retrieve.
     * @param carFullType The full type of the car to retrieve.
     * @param maxCars The maximum number of cars to retrieve.
     * @return A List of CarModel objects representing the cheapest cars in the state.
     */
    public List<CarModel> getCheapestCarsInState(final String stateCode,
                                                 final CarTypeEnum carType,
                                                 final CarFullTypeEnum carFullType,
                                                 final int maxCars) {
        log.info("Getting 3 cheapest cars in State: {} CarType: {}, CarFullType: {}", stateCode, carType, carFullType);

        // DATA collecting stage
        val carModels = new HashMap<String, CarModel>();
        val stateInfo = this.getStateInformationFuture(stateCode).join();
        val collectedFeatures = this.getDealersByStateFuture(stateCode)
                .thenApplyAsync(dealers -> dealers.stream()
                        .map(
                                dealer -> {
                                    val dealerInfoFuture = this.getDealerInfoFuture(dealer.id())
                                            .thenApplyAsync(dealerMode -> {
                                                        val manufacturerCollectedFeatures = dealerMode.cars().stream()
                                                                .filter(car -> ObjectUtils.isEmpty(carType) || (carType == car.type()))
                                                                .filter(car -> ObjectUtils.isEmpty(carFullType) || (carFullType == car.fullType()))
                                                                .map(car -> this.collectInformationFuture(dealerMode, stateInfo, car))
                                                                .toList();
                                                        return manufacturerCollectedFeatures.stream().map(CompletableFuture::join)
                                                                .toList();
                                                    },
                                                    this.generalAsyncExecutor
                                            );
                                    return dealerInfoFuture.join().stream().toList();
                                })
                                .toList(),
                        this.generalAsyncExecutor);

        collectedFeatures.join().stream()
                .flatMap(Collection::stream)
                .forEach(collectedInfo -> {
                    val carModel = CarModel.builder()
                            .id(collectedInfo.carModel().id())
                            .model(collectedInfo.carModel().model())
                            .year(collectedInfo.carModel().year())
                            .dealerId(collectedInfo.dealer().id())
                            .dealer(collectedInfo.dealer().name())
                            .price(collectedInfo.dealer().getPriceWithOverhead(
                                    collectedInfo.manufacturerPrice() - ((collectedInfo.manufacturerPrice() * collectedInfo.stateDiscountPercent()) / 100)
                            ))
                            .manufacturer(collectedInfo.carModel().manufacturer())
                            .manufacturerId(collectedInfo.carModel().manufacturerId())
                            .fullType(collectedInfo.carModel().fullType())
                            .type(collectedInfo.carModel().type())
                            .build();
                    carModels.put(generateCarId(stateCode, carModel.dealerId(), carModel.id()), carModel);
                });

        // DATA manipulation stage, in our case sorting and limiting
        return carModels.values()
                .stream()
                .sorted(Comparator.comparingInt(CarModel::price))
                .limit(maxCars)
                .toList();
    }

    private CompletableFuture<StateModel> getStateInformationFuture(final String stateCode) {
        return supplyAsync(() -> this.stateClient.getStateInformation(stateCode),
                this.generalAsyncExecutor);
    }

    private CompletableFuture<List<IdNameModel>> getDealersByStateFuture(final String stateCode) {
        return supplyAsync(() -> this.stateClient.getDealersByState(stateCode),
                this.generalAsyncExecutor);
    }

    private CompletableFuture<DealerModel> getDealerInfoFuture(final int id) {
        return supplyAsync(() -> this.dealerClient.getDealerInfo(id),
                this.generalAsyncExecutor);
    }

    private CompletableFuture<Integer> getPriceByCarIdFuture(final int id) {
        return supplyAsync(() -> this.manufacturerClient.getPriceByCarId(id),
                this.generalAsyncExecutor);
    }

    private CompletableFuture<Integer> getDiscountByTypeFuture(final String stateCode, final String type) {
        return supplyAsync(() -> this.stateClient.getDiscountByType(stateCode, type),
                this.generalAsyncExecutor);
    }

    private CompletableFuture<CollectedData> collectInformationFuture(final DealerModel dealerModel,
                                                                      final StateModel stateModel,
                                                                      final DealerCarModel carModel) {
        val priceFeature = this.getPriceByCarIdFuture(carModel.id()); // Downstream call 3

        val discountFeature = priceFeature.thenComposeAsync(price -> {
            if ((price > stateModel.priceLimit()) &&
                    stateModel.discounts().stream()
                            .anyMatch(discount -> discount.fullType() == carModel.fullType())) {
                return this.getDiscountByTypeFuture(stateModel.code(), carModel.fullType().name());  // Downstream call 4
            }
            return CompletableFuture.completedFuture(0);
        }, this.generalAsyncExecutor);

        return CompletableFuture.allOf(priceFeature, discountFeature)
                .thenApplyAsync(voidResult ->
                                CollectedData.builder()
                                        .dealer(dealerModel)
                                        .carModel(carModel)
                                        .manufacturerPrice(priceFeature.join())
                                        .stateDiscountPercent(discountFeature.join())
                                        .build(),
                        this.generalAsyncExecutor);
    }


    private static String generateCarId(final String stateCode,
                                        final int dealerId,
                                        final int carId) {
        return String.format("%s-D%d-C%d", stateCode, dealerId, carId);
    }

    @Builder
    private record CollectedData(DealerModel dealer,
                                 DealerCarModel carModel,
                                 Integer manufacturerPrice,
                                 Integer stateDiscountPercent) {
    }
}
