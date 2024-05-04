package com.epam.community.downstreamserver.controllers;

import com.epam.community.downstreamserver.models.IdNameModel;
import com.epam.community.downstreamserver.models.StateModel;
import com.epam.community.downstreamserver.services.StateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping(RestConstants.ENDPOINT_STATE)
@Slf4j
@Tag(name = "State Endpoint", description = "State endpoint for demo application")
public class StateController {

    private final long sleepTime;
    private final StateService stateService;

    public StateController(@Value("${com.epam.sleepTime:10}") final long sleepTime,
                           final StateService stateService) {
        this.sleepTime = sleepTime;
        this.stateService = stateService;
    }


    @Operation(
            summary = "getStates",
            description = "Get all states",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "We feel not good"),
            }
    )
    @GetMapping()
    public ResponseEntity<List<IdNameModel>> get() throws InterruptedException {
        log.info("Retrieving GET request all States, sleeping for {} ms", this.sleepTime);
        Thread.sleep(this.sleepTime); // Simulate some work..
        return ResponseEntity.ok(this.stateService.getStates());
    }


    @Operation(
            summary = "getStateById",
            description = "Get state by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "We feel not good"),
            }
    )
    @GetMapping("/id/{stateId}")
    public ResponseEntity<StateModel> getById(@PathVariable final int stateId) throws InterruptedException {
        log.info("Retrieving GET request State by ID={}, sleeping for {} ms", stateId, this.sleepTime);
        Thread.sleep(this.sleepTime); // Simulate some work..
        return ResponseEntity.ok(this.stateService.getStateById(stateId));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<StateModel> getByCode(@PathVariable final String code) throws InterruptedException {
        log.info("Retrieving GET request State by CODE={}, sleeping for {} ms", code, this.sleepTime);
        Thread.sleep(this.sleepTime); // Simulate some work..
        return ResponseEntity.ok(this.stateService.getStateByCode(code));
    }

    @GetMapping("/discount/{code}/{type}")
    public ResponseEntity<Integer> getDiscountByCodeAndType(@PathVariable final String code,
                                                            @PathVariable final String type) throws InterruptedException {
        log.info("Retrieving GET request Discount by CODE={} and TYPE={}, sleeping for {} ms", code, type, this.sleepTime);
        Thread.sleep(this.sleepTime); // Simulate some work..
        return ResponseEntity.ok(this.stateService.getDiscountIdByCodeAndType(code, type));
    }

    @Operation(
            summary = "getDealersByStateId",
            description = "Get dealers by state ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "We feel not good"),
            }
    )
    @GetMapping("/dealersById/{stateId}")
    public ResponseEntity<List<IdNameModel>> getDealersByStateId(@PathVariable final int stateId) throws InterruptedException {
        log.info("Retrieving GET request Dealers by State ID={}, sleeping for {} ms", stateId, this.sleepTime);
        Thread.sleep(this.sleepTime); // Simulate some work..
        return ResponseEntity.ok(this.stateService.getDealersByStateId(stateId));
    }

    @Operation(
            summary = "getDealersByCode",
            description = "Get dealers by State Code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "We feel not good"),
            }
    )
    @GetMapping("/dealersByCode/{stateCode}")
    public ResponseEntity<List<IdNameModel>> getDealersByStateId(@PathVariable final String stateCode) throws InterruptedException {
        log.info("Retrieving GET request Dealers by State code={}, sleeping for {} ms", stateCode, this.sleepTime);
        Thread.sleep(this.sleepTime); // Simulate some work..
        return ResponseEntity.ok(this.stateService.getDealersByStateCode(stateCode));
    }
}
