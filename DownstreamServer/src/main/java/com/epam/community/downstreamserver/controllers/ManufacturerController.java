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

@RestController
@Slf4j
@RequestMapping(RestConstants.ENDPOINT_MANUFACTURER)
@Tag(name = "Manufacturer Endpoint", description = "Manufacturer endpoint for demo application")
public class ManufacturerController {
    private final long sleepTime;
    private final ManufacturerService manufacturerService;

    public ManufacturerController(@Value("${com.epam.sleepTime:10}") final long sleepTime,
                                  final ManufacturerService manufacturerService) {
        this.sleepTime = sleepTime;
        this.manufacturerService = manufacturerService;
    }

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
