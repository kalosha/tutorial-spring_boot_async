package com.epam.community.middlesvc.clients.responses;

import java.util.List;

public record DealerResponse(int id, String name, int overhead, List<CarResponse> cars) {
}
