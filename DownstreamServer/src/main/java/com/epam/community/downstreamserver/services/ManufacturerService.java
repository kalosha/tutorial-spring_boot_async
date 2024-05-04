package com.epam.community.downstreamserver.services;


import com.epam.community.downstreamserver.data.DataRepository;
import com.epam.community.downstreamserver.generated.Car;
import com.epam.community.downstreamserver.models.IdNameModel;
import com.epam.community.downstreamserver.models.ManufacturerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class ManufacturerService {
    private final DataRepository dataRepository;

    public ManufacturerService(final DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public List<IdNameModel> getManufacturers() {
        log.debug("Getting manufacturers");
        return dataRepository.getManufacturers()
                .stream().map(manufacturer -> new IdNameModel(manufacturer.getId(), manufacturer.getName()))
                .toList();
    }

    public ManufacturerModel getManufacturerById(int manufacturerId) {
        log.debug("Getting manufacturer by ID: {}", manufacturerId);
        return this.dataRepository.getManufacturers()
                .stream()
                .filter(manufacturer -> manufacturer.getId() == manufacturerId)
                .findFirst()
                .map(manufacturer -> new ManufacturerModel(manufacturer.getId(), manufacturer.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manufacturer not found by ID: " + manufacturerId));
    }

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
