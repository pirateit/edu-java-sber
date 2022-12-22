package com.inventory.main.location;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LocationByIdConverter implements Converter<Integer, Location> {

    private LocationRepository locationRepo;

    public LocationByIdConverter(LocationRepository locationRepo) {
        this.locationRepo = locationRepo;
    }

    @Override
    public Location convert(Integer id) {
        return locationRepo.findById(id).orElse(null);
    }
}
