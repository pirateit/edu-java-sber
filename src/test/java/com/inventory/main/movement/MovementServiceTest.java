package com.inventory.main.movement;

import com.inventory.main.item.Item;
import com.inventory.main.item.ItemService;
import com.inventory.main.user.User;
import com.inventory.main.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MovementServiceTest {

  @Resource
  private MovementService movementService;

  @Resource
  private CoordinationService coordinationService;

  @Resource
  private UserService userService;

  @Resource
  private ItemService itemService;

  @Test
  @DisplayName("Отмена перемещения")
  void createRefusedCoordination() {
    Optional<Movement> movement = movementService.getById(27);
    Coordination coordination = new Coordination(Coordination.Status.REFUSED, "Comment");
    Optional<User> user = userService.getById(1);

    movementService.coordinate(movement.get(), coordination, user.get());

    movement = movementService.getById(27);

    assertEquals(movement.get().getStatus(), Movement.Status.CANCELLED);
    assertEquals(movement.get().getCoordinations().last().getStatus(), Coordination.Status.REFUSED);
    assertEquals(movement.get().getCoordinations().last().getComment(), coordination.getComment());
  }

  @Test
  @DisplayName("Согласование перемещения")
  void createCoordinatedCoordination() {
    Optional<Movement> movement = movementService.getById(29);
    Coordination coordination = new Coordination(Coordination.Status.COORDINATED, "Comment");
    Optional<User> user = userService.getById(3);
    Coordination lastCoordination = movement.get().getCoordinations().last();

    movementService.coordinate(movement.get(), coordination, user.get());

    movement = movementService.getById(29);

    for (Coordination c : movement.get().getCoordinations()) {
      if (c.equals(lastCoordination)) {
        assertEquals(c.getComment(), coordination.getComment());
      }
    }

    lastCoordination = coordinationService.getLastByMovementId(29).get();

    assertEquals(movement.get().getStatus(), Movement.Status.UNDER_APPROVAL);
    assertEquals(lastCoordination.getStatus(), Coordination.Status.WAITING);
  }

  @Test
  @DisplayName("Последнее согласование перемещения")
  void createLastCoordinatedCoordination() {
    Optional<Movement> movement = movementService.getById(27);
    Coordination coordination = new Coordination(Coordination.Status.COORDINATED, "Comment");
    Optional<User> user = userService.getById(1);

    movementService.coordinate(movement.get(), coordination, user.get());

    movement = movementService.getById(27);

    assertEquals(Movement.Status.APPROVED, movement.get().getStatus());
    assertEquals(Coordination.Status.COORDINATED, movement.get().getCoordinations().last().getStatus());
    assertEquals(coordination.getComment(), movement.get().getCoordinations().last().getComment());
  }

  @Test
  @DisplayName("Отправка предмета")
  void createSentCoordination() {
    Movement movement = movementService.getById(28).get();
    Coordination coordination = new Coordination(Coordination.Status.SENT, "Comment");
    User user = userService.getById(4).get();

    movementService.coordinate(movement, coordination, user);

    movement = movementService.getById(28).get();

    Coordination lastCoordination = coordinationService.getLastByMovementId(28).get();

    assertEquals(Movement.Status.SENT, movement.getStatus());
    assertEquals(Coordination.Status.SENT, lastCoordination.getStatus());
    assertEquals(coordination.getComment(), coordination.getComment());
  }

  @Test
  @DisplayName("Получение предмета")
  void createAcceptedCoordination() {
    Movement movement = movementService.getById(26).get();
    Coordination coordination = new Coordination(Coordination.Status.ACCEPTED, "Comment");
    User user = userService.getById(11).get();

    movementService.coordinate(movement, coordination, user);

    movement = movementService.getById(26).get();

    Coordination lastCoordination = coordinationService.getLastByMovementId(26).get();

    Item item = itemService.getById(13).get();

    assertEquals(Movement.Status.SUCCESS, movement.getStatus());
    assertEquals(Coordination.Status.ACCEPTED, lastCoordination.getStatus());
    assertEquals(coordination.getComment(), coordination.getComment());
    assertEquals(movement.getLocationToId(), item.getLocationId());
  }

  // TODO: make tests with partial movement and writeoffs

  @Test
  @DisplayName("Создание перемещения без согласнования")
  void createMovementWithoutCoordination() {
    Item item = itemService.getById(21).get();
    Movement movement = new Movement(Movement.Type.MOVEMENT, 1, 15, "Comment");
    User user = userService.getById(11).get();

    movement = movementService.createWithoutCoordination(movement, item, user);

    item = itemService.getById(21).get();

    assertNotNull(movement);
    assertEquals(Movement.Type.MOVEMENT, movement.getType());
    assertEquals(item.getId(), movement.getItemId());
    assertEquals(1, movement.getQuantity());
    assertEquals(15, movement.getLocationToId());
    assertEquals(11, movement.getRequestedUserId());
    assertEquals(Movement.Status.SUCCESS, movement.getStatus());
    assertEquals("Comment", movement.getComment());
    assertEquals(movement.getLocationToId(), item.getLocationId());
  }

  @Test
  @DisplayName("Создание списания без согласнования")
  void createWriteoffWithoutCoordination() {
    Item item = itemService.getById(21).get();
    Movement movement = new Movement(Movement.Type.WRITE_OFF, 1, null, "Comment");
    User user = userService.getById(11).get();

    movement = movementService.createWithoutCoordination(movement, item, user);

    Item updatedItem = itemService.getById(21).get();

    assertNotNull(movement);
    assertEquals(Movement.Type.WRITE_OFF, movement.getType());
    assertEquals(item.getId(), movement.getItemId());
    assertEquals(1, movement.getQuantity());
    assertNull(movement.getLocationToId());
    assertEquals(11, movement.getRequestedUserId());
    assertEquals(Movement.Status.SUCCESS, movement.getStatus());
    assertEquals("Comment", movement.getComment());
  }

  @Test
  @DisplayName("Создание перемещения с согласованием")
  void createMovementWithCoordination() {
    Item item = itemService.getById(11).get();
    Movement movement = new Movement(Movement.Type.MOVEMENT, 1, 15, "Comment");
    User user = userService.getById(6).get();

    movement = movementService.createWithCoordination(movement, item, user);

    Coordination lastCoordination = coordinationService.getLastByMovementId(movement.getId()).get();

      assertNotNull(movement);
    assertEquals(Movement.Type.MOVEMENT, movement.getType());
    assertEquals(item.getId(), movement.getItemId());
    assertEquals(1, movement.getQuantity());
    assertEquals(15, movement.getLocationToId());
    assertEquals(6, movement.getRequestedUserId());
    assertEquals(Movement.Status.UNDER_APPROVAL, movement.getStatus());
    assertEquals("Comment", movement.getComment());
    assertEquals(Coordination.Status.WAITING, lastCoordination.getStatus());
    assertEquals(1, lastCoordination.getChiefUserId());
  }

  @Test
  @DisplayName("Создание списания с согласованием")
  void createWriteoffWithCoordination() {
    Item item = itemService.getById(11).get();
    Movement movement = new Movement(Movement.Type.WRITE_OFF, 1, 15, "Comment");
    User user = userService.getById(6).get();

    movement = movementService.createWithCoordination(movement, item, user);

    Coordination lastCoordination = coordinationService.getLastByMovementId(movement.getId()).get();

    assertNotNull(movement);
    assertEquals(Movement.Type.WRITE_OFF, movement.getType());
    assertEquals(item.getId(), movement.getItemId());
    assertEquals(1, movement.getQuantity());
    assertEquals(null, movement.getLocationToId());
    assertEquals(6, movement.getRequestedUserId());
    assertEquals(Movement.Status.UNDER_APPROVAL, movement.getStatus());
    assertEquals("Comment", movement.getComment());
    assertEquals(Coordination.Status.WAITING, lastCoordination.getStatus());
    assertEquals(1, lastCoordination.getChiefUserId());
  }

  @Test
  @DisplayName("Получение счётчика ожидающих перемещений")
  void getUserWaitingCount() {
    int user3Count = movementService.getUserWaitingCount(3);
    int user4Count = movementService.getUserWaitingCount(4);

    assertEquals(1, user3Count);
    assertEquals(1, user4Count);
  }

  @Test
  @DisplayName("Получение активного перемещения по предмету")
  void getActiveByItemId() {
    Movement itemWithMovement = movementService.getActiveByItemId(13).orElse(null);
    Movement itemWithoutMovement = movementService.getActiveByItemId(25).orElse(null);

    assertNotNull(itemWithMovement);
    assertNull(itemWithoutMovement);
  }

  @Test
  @DisplayName("Получение перемещения по ID")
  void getById() {
    Movement movement = movementService.getById(1).get();

    assertNotNull(movement);
  }

  @Test
  @DisplayName("Получение всех перемещений от пользователя")
  void getAllByUser() {
    Page<Movement> movements = movementService.getAll(null, null, Collections.singleton(5), null, null, null, null, null, null);

    assertEquals(4, movements.getTotalElements());
  }

  @Test
  @DisplayName("Получение всех перемещений от администратора")
  void getAllByAdmin() {
    Page<Movement> movements = movementService.getAll(null, null, null, null, null, null, null, null, null);

    assertEquals(29, movements.getTotalElements());
  }

  @Test
  @DisplayName("Получение всех списаний от администратора")
  void getAllWriteoffsByAdmin() {
    Page<Movement> movements = movementService.getAll(null, null, null, Movement.Type.WRITE_OFF, null, null, null, null, null);

    assertEquals(1, movements.getTotalElements());
  }

  @Test
  @DisplayName("Получение перемещений с фильтрами от администратора")
  void getAllMovementsWithFiltersByAdmin() {
    Page<Movement> movements = movementService.getAll(1, null, null, Movement.Type.MOVEMENT, "мышь", null, Collections.singleton(15), null, null);

    assertEquals(2, movements.getTotalElements());
    assertEquals(1, movements.getTotalPages());
  }

}
