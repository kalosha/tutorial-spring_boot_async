package com.epam.community.downstreamserver.controllers;

import com.epam.community.downstreamserver.models.DealerModel;
import com.epam.community.downstreamserver.models.IdNameModel;
import com.epam.community.downstreamserver.services.DealerService;
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
 * DealerController is a REST controller that handles requests related to dealers.
 * It is annotated with @RestController to indicate that it is a REST controller.
 * It is also annotated with @Slf4j, a Lombok annotation to provide a logger for the class.
 * The class is tagged with "Dealer Endpoint" for Swagger documentation.
 */
@RestController
@RequestMapping(RestConstants.ENDPOINT_DEALER)
@Slf4j
@Tag(name = "Dealer Endpoint", description = "Dealer endpoint for demo application")
public class DealerController {

    @Value("${com.epam.sleepTime:10}")
    private long sleepTime;
    private final DealerService dealerService;

    /**
     * Constructor for DealerController.
     * It initializes the dealerService.
     *
     * @param dealerService the DealerService to be used by the controller.
     */
    public DealerController(final DealerService dealerService) {
        this.dealerService = dealerService;
    }

    /**
     * This method handles GET requests to retrieve all dealers.
     * It is annotated with @Operation to provide Swagger documentation.
     *
     * @return a ResponseEntity containing a list of IdNameModel.
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    @Operation(
            summary = "getDealers",
            description = "Get all dealers",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "We feel not good"),
            }
    )
    @GetMapping
    public ResponseEntity<List<IdNameModel>> get() throws InterruptedException {
        log.info("Retrieving GET request all Dealers, sleeping for {} ms", this.sleepTime);
        Thread.sleep(this.sleepTime); // Simulate some work..
        return ResponseEntity.ok(this.dealerService.getDealers());
    }

    /**
     * This method handles GET requests to retrieve a dealer by ID.
     * It is annotated with @Operation to provide Swagger documentation.
     *
     * @param dealerId the ID of the dealer to be retrieved.
     * @return a ResponseEntity containing a DealerModel.
     * @throws InterruptedException if the thread sleep is interrupted.
     */
    @Operation(
            summary = "getDealerById",
            description = "Get dealer by ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "We feel not good"),
            }
    )
    @GetMapping("/{dealerId}")
    public ResponseEntity<DealerModel> getDealerById(@PathVariable int dealerId) throws InterruptedException {
        log.info("Retrieving GET request dealer by ID: {}, sleeping for {} ms", dealerId, this.sleepTime);
        Thread.sleep(this.sleepTime); // Simulate some work..
        return ResponseEntity.ok(this.dealerService.getDealerById(dealerId));
    }

}
