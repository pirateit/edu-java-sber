package com.inventory.main.location;

import com.inventory.main.user.User;
import com.inventory.main.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  public Set<Location> getAllActive() {
    return this.locationRepository.findAllByDeletedAtIsNull();
  }

  public Set<Location> getAllInactive() {
    return this.locationRepository.findAllByDeletedAtIsNotNull();
  }

  public Set<Location> getParents() {
    return this.locationRepository.findAllByParentIdIsNullAndDeletedAtIsNullOrderByIdAsc();
  }

  public Set<Location> getUserLocations(int userId) {
    Set<Location> responsibleUserLocations = this.locationRepository.findByResponsibleUserId(userId);
    Set<Location> userLocations = new HashSet<>();

    if (responsibleUserLocations.isEmpty()) {
      Optional<User> user = this.userRepository.findById(userId);
      Optional<Location> userLocation = this.locationRepository.findById(user.get().getLocationId());

      return Collections.singleton(userLocation.get());
    }

    responsibleUserLocations.forEach(location -> userLocations.addAll(this.locationRepository.findChildrenById(location.getId())));

    return userLocations;
  }

  public Location create(Location location) {
    if (location.getParentId() != null) {
      Location parent = this.locationRepository.findById(location.getParentId()).get();

      location.setDepth(parent.getDepth() + 1);
    }

    location.setTitle(location.getTitle().trim());

    return this.locationRepository.save(location);
  }

  public Location update(Location location, Location newLocation) {
    location.setTitle(newLocation.getTitle());
    location.setResponsibleUserId(newLocation.getResponsibleUserId());

    return this.locationRepository.save(location);
  }

  public void delete(int locationId) {
    this.locationRepository.deleteById(locationId);
  }

  @Transactional
  public void restore(int locationId) {
    this.locationRepository.restoreById(locationId);
  }

}
