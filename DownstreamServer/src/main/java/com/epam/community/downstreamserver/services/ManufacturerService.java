package com.epam.community.downstreamserver.services;

import com.epam.community.downstreamserver.data.DataRepository;
import com.epam.community.downstreamserver.generated.Car;
import com.epam.community.downstreamserver.models.IdNameModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * ManufacturerService is a service class that provides methods related to manufacturers.
 * It is annotated with @Service to indicate that it is a Spring Service.
 * It is also annotated with @Slf4j, a Lombok annotation to provide a logger for the class.
 * The class has one field: dataRepository.
 */
@Service
@Slf4j
public class ManufacturerService {
    private final DataRepository dataRepository;

    /**
     * Constructor for ManufacturerService.
     * It initializes the dataRepository.
     *
     * @param dataRepository the DataRepository to be used by the service.
     */
    public ManufacturerService(final DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * This method retrieves all manufacturers and returns them as a list of IdNameModel.
     * It uses the dataRepository to get the manufacturers, and then maps each manufacturer to an IdNameModel.
     *
     * @return a list of IdNameModel representing all manufacturers.
     */
    public List<IdNameModel> getManufacturers() {
        log.debug("Getting manufacturers");
        return dataRepository.getManufacturers()
                .stream().map(manufacturer -> new IdNameModel(manufacturer.getId(), manufacturer.getName()))
                .toList();
    }

    /**
     * This method retrieves a manufacturer by its ID and returns it as a IdNameModel.
     * It uses the dataRepository to get the manufacturers, filters them by the provided ID, and then maps the manufacturer to a ManufacturerModel.
     * If the manufacturer is not found, it throws a ResponseStatusException with a status of NOT_FOUND.
     *
     * @param manufacturerId the ID of the manufacturer to be retrieved.
     * @return a IdNameModel representing the manufacturer with the provided ID.
     * @throws ResponseStatusException if the manufacturer is not found.
     */
    public IdNameModel getManufacturerById(int manufacturerId) {
        log.debug("Getting manufacturer by ID: {}", manufacturerId);
        return this.dataRepository.getManufacturers()
                .stream()
                .filter(manufacturer -> manufacturer.getId() == manufacturerId)
                .findFirst()
                .map(manufacturer -> new IdNameModel(manufacturer.getId(), manufacturer.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manufacturer not found by ID: " + manufacturerId));
    }

    /**
     * This method retrieves the price of a car by its ID.
     * It uses the dataRepository to get the cars, filters them by the provided ID, and then returns the price of the car.
     * If the car is not found, it throws a ResponseStatusException with a status of NOT_FOUND.
     *
     * @param carId the ID of the car to get the price for.
     * @return the price of the car with the provided ID.
     * @throws ResponseStatusException if the car is not found.
     */
    public Integer getPriceByCarId(int carId) {
        log.debug("Getting price by car ID: {}", carId);
        return this.dataRepository.getCars()
                .stream()
                .filter(car -> car.getId() == carId)
                .findFirst()
                .map(Car::getPrice)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found by ID: " + carId));
    }
}
