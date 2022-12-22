package com.inventory.main.location;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface LocationRepository extends CrudRepository<Location, Integer> {

    Iterable<Location> findAll();

    Optional<Location> findById(Integer id);

    Location save(Location location);
}
