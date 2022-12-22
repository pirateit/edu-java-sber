package com.inventory.main;

import org.springframework.cglib.core.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LocationByIdConverter implements Converter<Integer, Location> {

    private Map<Integer, Location> locationMap = new HashMap<>();

    public LocationByIdConverter() {
        locationMap.put("")
    }
}
