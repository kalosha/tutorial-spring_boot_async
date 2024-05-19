package com.epam.community.middlesvc.contollers;


import com.epam.community.middlesvc.models.CarFullTypeEnum;
import com.epam.community.middlesvc.models.CarModel;
import com.epam.community.middlesvc.models.CarTypeEnum;
import com.epam.community.middlesvc.services.CarService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This class is responsible for handling HTTP requests related to cars.
 * It uses the CarService for business logic.
 */
@Slf4j
@RestController
@RequestMapping(RestConstants.CARS_ENDPOINT)
public class CarsController {

    private final CarService carService;

    /**
     * Constructor for the CarsController class.
     *
     * @param carService the CarService to be used for business logic.
     */
    public CarsController(final CarService carService) {
        this.carService = carService;
    }

    /**
     * This method handles GET requests to fetch cars by state.
     * It optionally filters the cars by car type, car full type, and limits the number of cars returned.
     * @param stateCode the state code.
     * @param carType the car type (optional).
     * @param carFullType the car full type (optional).
     * @param maxCars the maximum number of cars to return (optional, default is 3).
     * @return a ResponseEntity containing a list of CarModel objects.
     */
    @GetMapping("/straightforward/{stateCode}/cars")
    public ResponseEntity<List<CarModel>> getCarsByState(
            @NonNull @PathVariable final String stateCode,
            @RequestParam(value = "carType", required = false) final CarTypeEnum carType,
            @RequestParam(value = "carFullType", required = false) final CarFullTypeEnum carFullType,
            @RequestParam(value = "maxCars", required = false, defaultValue = "3") @Min(0L) @Max(20L) final int maxCars) {
        log.info("Getting cars by state code: {}", stateCode);
        return ResponseEntity.ok(this.carService.getCheapestCarsInState(stateCode, carType, carFullType, maxCars));
    }
}
