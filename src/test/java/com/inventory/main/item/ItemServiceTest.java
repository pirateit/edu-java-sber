package com.inventory.main.item;

import com.inventory.main.movement.Movement;
import com.inventory.main.movement.MovementService;
import com.inventory.main.user.User;
import com.inventory.main.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemServiceTest {

  @Resource
  private ItemService itemService;

  @Resource
  private UserService userService;

  @Resource
  private MovementService movementService;

  @Test
  @DisplayName("Получение всех активных предметов от администратора")
  void getAllActiveByAdmin() {
    Page<Item> items = itemService.getAllActive(null, null, null, null, null, null);

    assertEquals(25, items.getTotalElements());
  }

  @Test
  @DisplayName("Получение всех активных предметов c фильтрами от администратора")
  void getAllActiveWithFiltersByAdmin() {
    Page<Item> items = itemService.getAllActive(null, null, null, "ноутбук", new HashSet<>(Arrays.asList(5, 7, 8, 11)), Collections.singleton(3));

    assertEquals(4, items.getTotalElements());
  }

  @Test
  @DisplayName("Получение всех активных предметов от пользователя")
  void getAllActiveByUser() {
    Page<Item> items = itemService.getAllActive(null, null, null, null, Collections.singleton(5), null);

    assertEquals(2, items.getTotalElements());
  }

  @Test
  @DisplayName("Получение всех списанных предметов")
  void getAllInactive() {
    Page<Item> items = itemService.getAllInactive(null, null, null, null, null);

    assertEquals(1, items.getTotalElements());
  }

  @Test
  @DisplayName("Получение всех списанных предметов с фильтрами")
  void getAllInactiveWithFilters() {
    Page<Item> items = itemService.getAllInactive(null, null, "мон", Collections.singleton(13), Collections.singleton(11));

    assertEquals(1, items.getTotalElements());
  }

  @Test
  @DisplayName("Получение предмета по ID")
  void getById() {
    Item item = itemService.getById(1).orElse(null);

    assertNotNull(item);
  }

  @Test
  @DisplayName("Получение предмета по инвентарному номеру")
  void getByInventoryNumber() {
    Item item = itemService.getByInventoryNumber("МОН", (long) 5).orElse(null);

    assertNotNull(item);
  }

  @Test
  @DisplayName("Создание предмета")
  void create() {
    Item item = new Item();
    item.setPrefix("   МОН");
    item.setNumber((long) 99);
    item.setTitle("Мониторчик №99");
    item.setQuantity(1);
    item.setCategoryId(13);
    item.setLocationId(1);

    User user = userService.getById(11).get();

    Item newItem = itemService.create(item, user);

    Set<Movement> movements = movementService.getByItemId(newItem.getId());

    assertNotNull(newItem);
    assertEquals(item.getPrefix(), newItem.getPrefix());
    assertEquals(item.getNumber(), newItem.getNumber());
    assertEquals(item.getTitle(), newItem.getTitle());
    assertEquals(item.getQuantity(), newItem.getQuantity());
    assertEquals(item.getCategoryId(), newItem.getCategoryId());
    assertEquals(item.getLocationId(), newItem.getLocationId());

    assertEquals(1, movements.size());
  }

  @Test
  @DisplayName("Обновление предмета")
  void update() {
    Item item = new Item();
    item.setId(26);
    item.setPrefix("ТЕСТ   ");
    item.setNumber((long) 199);
    item.setTitle("Монитор");
    item.setCategoryId(13);

    Item newItem = itemService.update(item);

    assertNotNull(newItem);
    assertEquals(item.getPrefix(), newItem.getPrefix());
    assertEquals(item.getNumber(), newItem.getNumber());
    assertEquals(item.getTitle(), newItem.getTitle());
    assertEquals(item.getQuantity(), newItem.getQuantity());
    assertEquals(item.getCategoryId(), newItem.getCategoryId());
    assertEquals(item.getLocationId(), newItem.getLocationId());
  }

}
