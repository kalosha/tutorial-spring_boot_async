package com.epam.community.middlesvc.clients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ManufacturerClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ManufacturerClient manufacturerClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(manufacturerClient, "url", "http://test:test");
    }

    @Test
    void getPriceByCarId_returnsPrice() throws ExecutionException, InterruptedException {
        when(restTemplate.getForObject(any(String.class), eq(Integer.class), anyMap()))
                .thenReturn(20000);

        CompletableFuture<Integer> price1 = manufacturerClient.getPriceByCarId(1);
        CompletableFuture<Integer> price2 = manufacturerClient.getPriceByCarId(2);
        CompletableFuture<Integer> price = manufacturerClient.getPriceByCarId(3);
        CompletableFuture<Integer> price4 = manufacturerClient.getPriceByCarId(4);

        CompletableFuture d = CompletableFuture.allOf(price1, price2, price, price4)
                .thenApplyAsync(v ->
                        price1.join() + price2.join() + price.join() + price4.join()
                );

        Object h = d.join();

        assertEquals(20000, price.join());
    }

    @Test
    void getPriceByCarId_returnsZeroWhenNoPrice() throws ExecutionException, InterruptedException {
        when(restTemplate.getForObject(any(String.class), any(), anyMap()))
                .thenReturn(null);

        CompletableFuture<Integer> price = manufacturerClient.getPriceByCarId(1);

        assertNull(price.join());
    }
}