package com.epam.community.downstreamserver.data;

import com.epam.community.downstreamserver.generated.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@Getter
public class DataRepository {

    private List<Car> cars;
    private List<Dealer> dealers;
    private List<Discount> discounts;
    private List<Manufacturer> manufacturers;
    private List<State> states;

    @PostConstruct
    public void init() {
        this.cars = loadFromJsonFile("cars.json", new TypeReference<>() {
        });

        this.dealers = loadFromJsonFile("dealers.json", new TypeReference<>() {
        });
        this.discounts = loadFromJsonFile("discounts.json", new TypeReference<>() {
        });
        this.manufacturers = loadFromJsonFile("manufacturers.json", new TypeReference<>() {
        });
        this.states = loadFromJsonFile("states.json", new TypeReference<>() {
        });
    }

    private static <T> T loadFromJsonFile(final String fileName,
                                          final TypeReference<T> typeReference) {
        val mapper = new ObjectMapper();
        try (val inputStream = new ClassPathResource(fileName).getInputStream()) {
            return mapper.readValue(inputStream, typeReference);

        } catch (IOException e) {
            log.error("Error while reading file {}", fileName, e);
            throw new RuntimeException("Problem reading json file=" + fileName, e);
        }
    }
}
