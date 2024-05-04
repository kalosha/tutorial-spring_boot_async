package com.epam.community.downstreamserver.models;

import java.util.List;

public record DealerModel(int id, String name, int overhead, List<CarModel> cars) {
}
