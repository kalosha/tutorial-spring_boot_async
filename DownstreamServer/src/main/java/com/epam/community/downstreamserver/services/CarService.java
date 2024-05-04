package com.epam.community.downstreamserver.services;

import com.epam.community.downstreamserver.data.DataRepository;
import com.epam.community.downstreamserver.models.CarModel;
import com.epam.community.downstreamserver.models.IdNameModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CarService {

    private final DataRepository dataRepository;

    private final ManufacturerService manufacturerService;

    public CarService(final DataRepository dataRepository,
                      final ManufacturerService manufacturerService) {
        this.dataRepository = dataRepository;
        this.manufacturerService = manufacturerService;
    }

    public List<IdNameModel> getCars() {
        return dataRepository.getCars()
                .stream().map(car -> new IdNameModel(car.getId(), car.getModel()))
                .toList();
    }

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
