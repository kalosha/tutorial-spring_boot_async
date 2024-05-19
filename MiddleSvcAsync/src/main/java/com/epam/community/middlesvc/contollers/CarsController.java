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

/**
 * Controller class for handling car-related requests.
 * This class is annotated with @RestController, meaning it's a controller where every method returns a domain object instead of a view.
 * It's shorthand for @Controller and @ResponseBody rolled together.
 * The @RequestMapping annotation is used to map web requests onto specific handler classes and/or handler methods.
 */
@Slf4j
@RestController
@RequestMapping(RestConstants.CARS_ENDPOINT)
public class CarsController {

    private final CarAsyncService carAsyncService;

    /**
     * Constructor for the CarsController.
     *
     * @param carAsyncService the CarAsyncService
     */
    public CarsController(final CarAsyncService carAsyncService) {
        this.carAsyncService = carAsyncService;
    }

    /**
     * Get cars by state code.
     * This method is mapped to a GET request to the path "/straightforward/{stateCode}/cars".
     * @param stateCode the state code
     * @param carType the car type (optional)
     * @param carFullType the car full type (optional)
     * @param maxCars the maximum number of cars to return (optional, default is 3)
     * @return a ResponseEntity containing a list of CarModel
     */
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
