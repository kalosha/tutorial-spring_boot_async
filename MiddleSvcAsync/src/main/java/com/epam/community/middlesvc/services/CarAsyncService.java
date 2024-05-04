package com.epam.community.middlesvc.services;

import com.epam.community.middlesvc.clients.DealerClient;
import com.epam.community.middlesvc.clients.ManufacturerClient;
import com.epam.community.middlesvc.clients.StateClient;
import com.epam.community.middlesvc.models.*;
import io.micrometer.tracing.Tracer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CarAsyncService {

    private final DealerClient dealerClient;
    private final StateClient stateClient;
    private final ManufacturerClient manufacturerClient;
    private final Tracer tracer;

    public CarAsyncService(final DealerClient dealerClient,
                           final StateClient stateClient,
                           final ManufacturerClient manufacturerClient,
                           final Tracer tracer) {
        this.dealerClient = dealerClient;
        this.stateClient = stateClient;
        this.manufacturerClient = manufacturerClient;
        this.tracer = tracer;
    }


    public List<CarModel> getCheapestCarsInState(final String stateCode,
                                                 final CarTypeEnum carType,
                                                 final CarFullTypeEnum carFullType,
                                                 final int maxCars) {
        log.info("Getting 3 cheapest cars in State: {} CarType: {}, CarFullType: {}", stateCode, carType, carFullType);

        log.info("traceId: {}", this.tracer.currentSpan().context().traceId());

        // DATA collecting stage
        val carModels = new HashMap<String, CarModel>();
        val stateInfo = this.stateClient.getStateInformation(stateCode).join(); // Downstream call 0
        val collectedFeatures = this.stateClient.getDealersByState(stateCode)  // Downstream call 1
                .thenApplyAsync(dealers -> dealers.stream()
                        .map(
                                dealer -> {
                                    val dealerInfo = this.dealerClient.getDealerInfo(dealer.id()) // Downstream call 2
                                            .thenApplyAsync(dealerModel -> {
                                                        val manufacturerCollectedFeatures = dealerModel.cars().stream()
                                                                .filter(car -> ObjectUtils.isEmpty(carType) || (carType == car.type()))
                                                                .filter(car -> ObjectUtils.isEmpty(carFullType) || (carFullType == car.fullType()))
                                                                .map(car -> this.collectInformation(dealerModel, stateInfo, car)
                                                                ).toList();
                                                        return manufacturerCollectedFeatures.stream().map(CompletableFuture::join)
                                                                .toList();
                                                    }
                                            );
                                    return dealerInfo.join().stream().toList();
                                })
                        .toList());

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

    private static String generateCarId(final String stateCode,
                                        final int dealerId,
                                        final int carId) {
        return String.format("%s-D%d-C%d", stateCode, dealerId, carId);
    }

    private CompletableFuture<CollectedData> collectInformation(final DealerModel dealerModel,
                                                                final StateModel stateModel,
                                                                final DealerCarModel carModel) {
        val priceFeature = this.manufacturerClient.getPriceByCarId(carModel.id()); // Downstream call 3

        val discountFeature = priceFeature.thenComposeAsync(price -> {
            if ((price > stateModel.priceLimit()) &&
                    stateModel.discounts().stream()
                            .anyMatch(discount -> discount.fullType() == carModel.fullType())) {
                return this.stateClient.getDiscountByType(stateModel.code(), carModel.fullType());  // Downstream call 4
            }
            return CompletableFuture.completedFuture(0);
        });
        return CompletableFuture.allOf(priceFeature, discountFeature)
                .thenApplyAsync(voidResult ->
                        CollectedData.builder()
                                .dealer(dealerModel)
                                .carModel(carModel)
                                .manufacturerPrice(priceFeature.join())
                                .stateDiscountPercent(discountFeature.join())
                                .build()
                );
    }


    @Builder
    private record CollectedData(DealerModel dealer,
                                 DealerCarModel carModel,
                                 Integer manufacturerPrice,
                                 Integer stateDiscountPercent) {
    }

}
