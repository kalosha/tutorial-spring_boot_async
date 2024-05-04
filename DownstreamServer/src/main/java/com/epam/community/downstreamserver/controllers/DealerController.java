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

@RestController
@RequestMapping(RestConstants.ENDPOINT_DEALER)
@Slf4j
@Tag(name = "Dealer Endpoint", description = "Dealer endpoint for demo application")
public class DealerController {

    private final long sleepTime;
    private final DealerService dealerService;

    public DealerController(@Value("${com.epam.sleepTime:10}") final long sleepTime,
                            final DealerService dealerService) {
        this.sleepTime = sleepTime;
        this.dealerService = dealerService;
    }


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
