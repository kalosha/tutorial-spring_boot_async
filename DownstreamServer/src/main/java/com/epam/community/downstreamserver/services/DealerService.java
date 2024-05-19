package com.epam.community.downstreamserver.services;

import com.epam.community.downstreamserver.data.DataRepository;
import com.epam.community.downstreamserver.models.DealerModel;
import com.epam.community.downstreamserver.models.IdNameModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * DealerService is a service class that provides methods related to dealers.
 * It is annotated with @Service to indicate that it is a Spring Service.
 * It is also annotated with @Slf4j, a Lombok annotation to provide a logger for the class.
 * The class has two fields: dataRepository and carService.
 */
@Service
@Slf4j
public class DealerService {
    private final DataRepository dataRepository;
    private final CarService carService;

    /**
     * Constructor for DealerService.
     * It initializes the dataRepository and carService.
     *
     * @param dataRepository the DataRepository to be used by the service.
     * @param carService     the CarService to be used by the service.
     */
    public DealerService(final DataRepository dataRepository,
                         final CarService carService) {
        this.dataRepository = dataRepository;
        this.carService = carService;
    }

    /**
     * This method retrieves all dealers and returns them as a list of IdNameModel.
     * It uses the dataRepository to get the dealers, and then maps each dealer to an IdNameModel.
     *
     * @return a list of IdNameModel representing all dealers.
     */
    public List<IdNameModel> getDealers() {
        log.debug("Getting dealers");
        return dataRepository.getDealers()
                .stream().map(dealer -> new IdNameModel(dealer.getId(), dealer.getName()))
                .toList();
    }

    /**
     * This method retrieves a dealer by its ID and returns it as a DealerModel.
     * It uses the dataRepository to get the dealers, filters them by the provided ID, and then maps the dealer to a DealerModel.
     * If the dealer is not found, it throws a ResponseStatusException with a status of NOT_FOUND.
     *
     * @param dealerId the ID of the dealer to be retrieved.
     * @return a DealerModel representing the dealer with the provided ID.
     * @throws ResponseStatusException if the dealer is not found.
     */
    public DealerModel getDealerById(int dealerId) {
        log.debug("Getting dealer by ID: {}", dealerId);
        return this.dataRepository.getDealers()
                .stream()
                .filter(dealer -> dealer.getId() == dealerId)
                .findFirst()
                .map(dealer -> new DealerModel(dealer.getId(), dealer.getName(), dealer.getOverhead(), this.carService.getCarsByIds(dealer.getModels())))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dealer not found by ID: " + dealerId));
    }
}
