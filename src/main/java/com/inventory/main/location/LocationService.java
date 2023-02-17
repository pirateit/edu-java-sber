package com.inventory.main.location;

import com.inventory.main.user.User;
import com.inventory.main.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
public class LocationService {

  private final LocationRepository locationRepository;
  private final UserRepository userRepository;

  public LocationService(LocationRepository locationRepository, UserRepository userRepository) {
    this.locationRepository = locationRepository;
    this.userRepository = userRepository;
  }

  public Optional<Location> getById(int locationId) {
    return this.locationRepository.findById(locationId);
  }

  public Set<Location> getAll() {
    return locationRepository.findAll();
  }

  public Set<Location> getAllActive() {
    return locationRepository.findAll();
  }

  public Set<Location> getParents() {
    return locationRepository.findAllByParentIdIsNullOrderByIdAsc();
  }

//  public Location getChildren(Location parent) {
//    parent.setChildren(locationRepository.findByParentIdOrderByTitleAsc(parent.getId()));
//
//    if (!parent.getChildren().isEmpty()) {
//      parent.getChildren().forEach(this::getChildren);
//    }
//
//    return parent;
//  }

  public Set<Location> getUserLocations(int userId) {
    Set<Location> responsibleUserLocations = locationRepository.findByResponsibleUserId(userId);
    Set<Location> userLocations = new HashSet<>();

    if (responsibleUserLocations.isEmpty()) {
      Optional<User> user = userRepository.findById(userId);
      Optional<Location> userLocation = locationRepository.findById(user.get().getLocationId());

      return Collections.singleton(userLocation.get());
    }

    responsibleUserLocations.forEach(location -> userLocations.addAll(locationRepository.findChildrenById(location.getId())));

    return userLocations;
  }

  public Location create(Location location) {
    if (location.getParentId() != null) {
      location.setDepth(location.getParent().getDepth() + 1);
    }

    location.setTitle(location.getTitle().trim());

    return locationRepository.save(location);
  }

  public Location update(Location location, Location newLocation) {
    location.setTitle(newLocation.getTitle());
    location.setParentId(newLocation.getParentId());
    location.setResponsibleUserId(newLocation.getResponsibleUserId());

    return locationRepository.save(location);
  }

  @Transactional
  public void delete(int locationId) {
    locationRepository.deleteById(locationId);
  }

  @Transactional
  public void restore(int locationId) {
    locationRepository.restoreById(locationId);
  }

}
