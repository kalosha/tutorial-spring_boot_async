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

/**
 * DataRepository is a component that loads data from JSON files.
 * It is annotated with @Component to indicate that it is a Spring Bean.
 * It is also annotated with @Slf4j, a Lombok annotation to provide a logger for the class.
 * The class has getters for the loaded data, provided by the @Getter annotation.
 */
@Component
@Slf4j
@Getter
public class DataRepository {

    private List<Car> cars;
    private List<Dealer> dealers;
    private List<Discount> discounts;
    private List<Manufacturer> manufacturers;
    private List<State> states;

    /**
     * This method is annotated with @PostConstruct, so it is executed after dependency injection is done.
     * It initializes the lists of cars, dealers, discounts, manufacturers, and states by loading data from JSON files.
     */
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

    /**
     * This method loads data from a JSON file and returns it as an object of the specified type.
     * It uses the Jackson library to parse the JSON.
     *
     * @param fileName      the name of the JSON file to load.
     * @param typeReference a TypeReference that represents the type of the object to return.
     * @param <T>           the type of the object to return.
     * @return the data loaded from the JSON file.
     * @throws RuntimeException if there is an error reading the file.
     */
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
