package com.inventory.main.location;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LocationServiceTest {

  @Resource
  private LocationService locationService;

  @Test
  @DisplayName("Получение подразделения по ID")
  void getLastByMovementId() {
    Location location = locationService.getById(1).orElse(null);

    assertNotNull(location);
  }

  @Test
  @DisplayName("Получение активных подразделений")
  void getAllActive() {
    Set<Location> locations = locationService.getAllActive();

    assertEquals(16, locations.size());
  }

  @Test
  @DisplayName("Получение удалённых подразделений")
  void getAllInactive() {
    Set<Location> locations = locationService.getAllInactive();

    assertEquals(2, locations.size());
  }

  @Test
  @DisplayName("Получение родительских подразделений")
  void getParents() {
    Set<Location> locations = locationService.getParents();

    assertEquals(2, locations.size());
  }

  @Test
  @DisplayName("Получение подразделений пользователя")
  void getUserLocations() {
    Set<Location> user2locations = locationService.getUserLocations(2);
    Set<Location> user3locations = locationService.getUserLocations(3);

    assertEquals(3, user2locations.size());
    assertEquals(1, user3locations.size());
  }

  @Test
  @DisplayName("Создание подразделения")
  void create() {
    Location location = new Location();
    location.setTitle("Location name");
    location.setParentId(8);
    location.setResponsibleUserId(11);

    Location newLocation = locationService.create(location);

    assertNotNull(newLocation);
    assertEquals(location.getTitle(), newLocation.getTitle());
    assertEquals(location.getParentId(), newLocation.getParentId());
    assertEquals(3, newLocation.getDepth());
    assertEquals(location.getResponsibleUserId(), newLocation.getResponsibleUserId());
  }

  @Test
  @DisplayName("Обновление подразделения")
  void update() {
    Location location = locationService.getById(1).get();
    Location newLocation = new Location();
    newLocation.setTitle("New location name");
    newLocation.setResponsibleUserId(11);

    Location updatedLocation = locationService.update(location, newLocation);

    assertNotNull(updatedLocation);
    assertEquals(newLocation.getTitle(), updatedLocation.getTitle());
    assertEquals(newLocation.getParentId(), updatedLocation.getParentId());
    assertNull(updatedLocation.getDepth());
    assertEquals(newLocation.getResponsibleUserId(), updatedLocation.getResponsibleUserId());
  }

  @Test
  @DisplayName("Удаление подразделения")
  void delete() {
    locationService.delete(17);

    Location location = locationService.getById(17).get();

    assertNotNull(location.getDeletedAt());
  }

  @Test
  @DisplayName("Восстановление подразделения")
  void restore() {
    locationService.restore(18);

    Location location = locationService.getById(18).get();

    assertNull(location.getDeletedAt());
  }

}
