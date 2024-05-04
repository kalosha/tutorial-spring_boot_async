package com.epam.community.downstreamserver.services;

import com.epam.community.downstreamserver.data.DataRepository;
import com.epam.community.downstreamserver.models.DealerModel;
import com.epam.community.downstreamserver.models.IdNameModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class DealerService {
    private final DataRepository dataRepository;

    private final CarService carService;

    public DealerService(final DataRepository dataRepository,
                         final CarService carService) {
        this.dataRepository = dataRepository;
        this.carService = carService;
    }

    public List<IdNameModel> getDealers() {
        log.debug("Getting dealers");
        return dataRepository.getDealers()
                .stream().map(dealer -> new IdNameModel(dealer.getId(), dealer.getName()))
                .toList();
    }

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
