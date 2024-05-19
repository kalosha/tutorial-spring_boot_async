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
 * This is a controller class for handling requests related to cars.
 * It uses the CarService to retrieve car data.
 */
@Slf4j
@RestController
@RequestMapping(RestConstants.CARS_ENDPOINT)
public class CarsController {

    private final CarService carService;

    /**
     * Constructor for the CarsController class.
     *
     * @param carService The CarService to be used for retrieving car data.
     */
    public CarsController(final CarService carService) {
        this.carService = carService;
    }

    /**
     * This method handles GET requests to retrieve cars by state.
     * It uses the CarService to retrieve the data and returns it in the response.
     * @param stateCode The code of the state to retrieve cars for.
     * @param carType The type of the car to retrieve.
     * @param carFullType The full type of the car to retrieve.
     * @param maxCars The maximum number of cars to retrieve.
     * @return A ResponseEntity containing a List of CarModel objects.
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
