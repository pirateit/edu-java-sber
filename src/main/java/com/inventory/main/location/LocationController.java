package com.inventory.main.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationRepository locationRepo;

    @Autowired
    public LocationController(LocationRepository locationRepo) {
        this.locationRepo = locationRepo;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> findOne(@PathVariable("id") Integer id) {
        Optional<Location> location = locationRepo.findById(id);

        if (location.isPresent()) {
            return new ResponseEntity<>(location.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}/children")
    public Iterable<Location> findChildrenTree(@PathVariable("id") Integer id) {
        Iterable<Location> locations = locationRepo.findChildrenById(id);

        return locations;
    }

    @GetMapping("/{id}/parents")
    public Iterable<Location> findParentsTree(@PathVariable("id") Integer id) {
        Iterable<Location> locations = locationRepo.findParentsById(id);

        return locations;
    }

    @PostMapping
    public Location create(@Valid Location location, Errors errors) {
        if (errors.hasErrors()) {
            log.error("locations POST validation error");

            return null;
        }

        return locationRepo.save(location);
    }
}
