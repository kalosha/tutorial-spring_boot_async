package com.epam.community.downstreamserver.services;

import com.epam.community.downstreamserver.data.DataRepository;
import com.epam.community.downstreamserver.models.CarModel;
import com.epam.community.downstreamserver.models.IdNameModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CarService is a service class that provides methods related to cars.
 * It is annotated with @Service to indicate that it is a Spring Service.
 * It is also annotated with @Slf4j, a Lombok annotation to provide a logger for the class.
 * The class has two fields: dataRepository and manufacturerService.
 */
@Service
@Slf4j
public class CarService {

    private final DataRepository dataRepository;
    private final ManufacturerService manufacturerService;

    /**
     * Constructor for CarService.
     * It initializes the dataRepository and manufacturerService.
     *
     * @param dataRepository      the DataRepository to be used by the service.
     * @param manufacturerService the ManufacturerService to be used by the service.
     */
    public CarService(final DataRepository dataRepository,
                      final ManufacturerService manufacturerService) {
        this.dataRepository = dataRepository;
        this.manufacturerService = manufacturerService;
    }

    /**
     * This method retrieves all cars and returns them as a list of IdNameModel.
     * It uses the dataRepository to get the cars, and then maps each car to an IdNameModel.
     *
     * @return a list of IdNameModel representing all cars.
     */
    public List<IdNameModel> getCars() {
        return dataRepository.getCars()
                .stream().map(car -> new IdNameModel(car.getId(), car.getModel()))
                .toList();
    }

    /**
     * This method retrieves cars by their IDs and returns them as a list of CarModel.
     * It uses the dataRepository to get the cars, filters them by the provided IDs, and then maps each car to a CarModel.
     *
     * @param ids a list of IDs of the cars to be retrieved.
     * @return a list of CarModel representing the cars with the provided IDs.
     */
    public List<CarModel> getCarsByIds(List<Integer> ids) {
        return dataRepository.getCars()
                .stream()
                .filter(car -> ids.contains(car.getId()))
                .map(car -> new CarModel(car.getId(),
                        car.getModel(),
                        this.manufacturerService.getManufacturerById(car.getManufacturerId()),
                        car.getYear(),
                        car.getFuelType().name(),
                        car.getType().name()))
                .toList();
    }
}
