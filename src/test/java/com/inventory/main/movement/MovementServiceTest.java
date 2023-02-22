package com.inventory.main.movement;

import com.inventory.main.item.Item;
import com.inventory.main.item.ItemService;
import com.inventory.main.user.User;
import com.inventory.main.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
  void createWithoutCoordination() {
    Item item = itemService.getById(21).get();
    Movement movement = new Movement(Movement.Type.MOVEMENT, item.getId(), 1, 15, 11, Movement.Status.SUCCESS, "Comment");
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

}
