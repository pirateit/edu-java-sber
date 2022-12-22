package com.inventory.main.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationRepository locationRepo;

    public LocationRepository(LocationRepository locationRepo) {
        this.locationRepo = locationRepo;
    }

    @PostMapping
    public void create(@Valid Location location, Errors errors) {
        if (errors.hasErrors()) {
            log.error("locations POST validation error");
            System.out.println("locations POST validation error");
        }
    }
}
