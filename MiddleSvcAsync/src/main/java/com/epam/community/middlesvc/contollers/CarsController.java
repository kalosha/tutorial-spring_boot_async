package com.epam.community.middlesvc.contollers;


import com.epam.community.middlesvc.models.CarFullTypeEnum;
import com.epam.community.middlesvc.models.CarModel;
import com.epam.community.middlesvc.models.CarTypeEnum;
import com.epam.community.middlesvc.services.CarAsyncService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(RestConstants.CARS_ENDPOINT)
public class CarsController {

    private final CarAsyncService carAsyncService;

    public CarsController(final CarAsyncService carAsyncService) {
        this.carAsyncService = carAsyncService;
    }

    @GetMapping("/straightforward/{stateCode}/cars")
    public ResponseEntity<List<CarModel>> getCarsByState(
            @NonNull @PathVariable final String stateCode,
            @RequestParam(value = "carType", required = false) final CarTypeEnum carType,
            @RequestParam(value = "carFullType", required = false) final CarFullTypeEnum carFullType,
            @RequestParam(value = "maxCars", required = false, defaultValue = "3") @Min(0L) @Max(20L) final int maxCars) {
        log.info("Getting cars by state code: {}", stateCode);
        return ResponseEntity.ok(this.carAsyncService.getCheapestCarsInState(stateCode, carType, carFullType, maxCars));
    }
}
