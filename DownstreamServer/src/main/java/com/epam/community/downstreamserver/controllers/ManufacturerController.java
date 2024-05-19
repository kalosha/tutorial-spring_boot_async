package com.epam.community.downstreamserver.controllers;

import com.epam.community.downstreamserver.models.IdNameModel;
import com.epam.community.downstreamserver.services.ManufacturerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ManufacturerController is a REST controller that handles requests related to manufacturers.
 * It is annotated with @RestController to indicate that it is a REST controller.
 * It is also annotated with @Slf4j, a Lombok annotation to provide a logger for the class.
 * The class is tagged with "Manufacturer Endpoint" for Swagger documentation.
 */
@RestController
@Slf4j
@RequestMapping(RestConstants.ENDPOINT_MANUFACTURER)
@Tag(name = "Manufacturer Endpoint", description = "Manufacturer endpoint for demo application")
public class ManufacturerController {
    @Value("${com.epam.sleepTime:10}")
    private long sleepTime;
    private final ManufacturerService manufacturerService;

    /**
     * Constructor for ManufacturerController.
     * It initializes the manufacturerService.
     *
     * @param manufacturerService the ManufacturerService to be used by the controller.
     */
    public ManufacturerController(final ManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    /**
     * This method handles GET requests to retrieve all manufacturers.
     * It is annotated with @Operation to provide Swagger documentation.
     *
     * @return a ResponseEntity containing a list of IdNameModel.
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    @Operation(
            summary = "getManufacturers",
            description = "Get all manufacturers",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "We feel not good"),
            }
    )
    @GetMapping()
    public ResponseEntity<List<IdNameModel>> get() throws InterruptedException {
        log.info("Retrieving GET request all Manufacturers, sleeping for {} ms", this.sleepTime);
        Thread.sleep(this.sleepTime); // Simulate some work..
        return ResponseEntity.ok(this.manufacturerService.getManufacturers());
    }

    /**
     * This method handles GET requests to retrieve a car price by car ID.
     * It is annotated with @Operation to provide Swagger documentation.
     *
     * @param carId the ID of the car to retrieve the price for.
     * @return a ResponseEntity containing the price of the car.
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    @Operation(
            summary = "getPriceCarById",
            description = "Get Car Price by CarID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "We feel not good"),
            }
    )
    @GetMapping("/{carId}")
    public ResponseEntity<Integer> getPriceByCarsId(@PathVariable int carId) throws InterruptedException {
        log.info("Retrieving GET request price by car ID: {}, sleeping for {} ms", carId, this.sleepTime);
        Thread.sleep(this.sleepTime); // Simulate some work..
        return ResponseEntity.ok(this.manufacturerService.getPriceByCarId(carId));
    }
}
