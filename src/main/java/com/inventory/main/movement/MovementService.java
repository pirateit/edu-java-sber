package com.inventory.main.movement;

import com.inventory.main.item.Item;
import com.inventory.main.item.ItemRepository;
import com.inventory.main.location.Location;
import com.inventory.main.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MovementService {

  private final MovementRepository movementRepository;
  private final CoordinationRepository coordinationRepository;
  private final ItemRepository itemRepository;

  private final int pageSize = 20;

  public MovementService(MovementRepository movementRepository, CoordinationRepository coordinationRepository, ItemRepository itemRepository) {
    this.movementRepository = movementRepository;
    this.coordinationRepository = coordinationRepository;
    this.itemRepository = itemRepository;
  }

  public Page<Movement> getAll(Integer page, Integer size, Set<Integer> userLocationsIds, Movement.Type type, String itemTitle, Set<Integer> locationsFrom, Set<Integer> locationsTo, Integer requestedUserId, String status) {
    Pageable pageable = PageRequest.of((page != null) ? page - 1 : 0, (size != null) ? size : pageSize);

    if (userLocationsIds == null) {
      userLocationsIds = new HashSet<>();
    }

    if (locationsFrom == null) {
      locationsFrom = new HashSet<>();
    }

    if (locationsTo == null) {
      locationsTo = new HashSet<>();
    }

    if (status != null && status.equalsIgnoreCase("active")) {
      return movementRepository.findAllWithFilters(userLocationsIds, type == null ? null : type.name(), itemTitle, locationsFrom, locationsTo, requestedUserId, Arrays.asList(Movement.Status.UNDER_APPROVAL.name(), Movement.Status.APPROVED.name(), Movement.Status.SENT.name()), pageable);
    }

    return movementRepository.findAllWithFilters(userLocationsIds, type == null ? null : type.name(), itemTitle, locationsFrom, locationsTo, requestedUserId, new ArrayList<>(), pageable);
  }

  public Page<Movement> getWaiting(Integer page, Integer size, int userId) {
    Pageable pageable = PageRequest.of((page != null) ? page - 1 : 0, (size != null) ? size : pageSize);

    return movementRepository.findAllUserWaiting(userId, pageable);
  }

  public Optional<Movement> getById(int movementId) {
    return movementRepository.findById(movementId);
  }

  public Optional<Movement> getActiveByItemId(int itemId) {
    return movementRepository.findOneByItemIdAndStatusIn(itemId, Arrays.asList(Movement.Status.APPROVED, Movement.Status.UNDER_APPROVAL, Movement.Status.SENT));
  }

  public int getUserWaitingCount(int userId) {
    return movementRepository.countAllUserWaiting(userId);
  }

  @Transactional
  public Movement createWithCoordination(Movement movement, Item item, User user) {
    movement.setItemId(item.getId());
    movement.setLocationFromId(item.getLocationId());
    movement.setRequestedUserId(user.getId());
    movement.setStatus(Movement.Status.UNDER_APPROVAL);

    if (movement.getType() == Movement.Type.WRITE_OFF) {
      movement.setLocationToId(null);
    }

    movement = movementRepository.save(movement);

    Coordination coordination = new Coordination();

    Integer chiefUserId = null;
    Location parentLocation = user.getLocation();

    while (chiefUserId == null) {
      if (parentLocation.getResponsibleUserId() != null && parentLocation.getResponsibleUserId().equals(user.getId())) {
        chiefUserId = parentLocation.getParent().getResponsibleUserId();
      } else if (parentLocation.getResponsibleUserId() != null) {
        chiefUserId = parentLocation.getResponsibleUserId();
      }

      parentLocation = parentLocation.getParent();
    }

    coordination.setMovementId(movement.getId());
    coordination.setChiefUserId(chiefUserId);

    coordinationRepository.save(coordination);

    return movement;
  }

  @Transactional
  public Movement createWithoutCoordination(Movement movement, Item item, User user) {
    movement.setItemId(item.getId());
    movement.setLocationFromId(item.getLocationId());
    movement.setRequestedUserId(user.getId());
    movement.setStatus(Movement.Status.SUCCESS);

    if (movement.getType() == Movement.Type.WRITE_OFF) {
      movement.setLocationToId(null);
    }

    movement = movementRepository.save(movement);

    item.setLocationId(movement.getLocationToId());

    itemRepository.save(item);

    return movement;
  }

  @Transactional
  public void coordinate(Movement movement, Coordination coordination, User user) {
    switch (coordination.getStatus()) {
      case REFUSED -> {
        movement.getCoordinations().last().setStatus(coordination.getStatus());
        movement.getCoordinations().last().setComment(coordination.getComment());
        movement.setStatus(Movement.Status.CANCELLED);

        movementRepository.save(movement);
      }
      case COORDINATED -> {
        movement.getCoordinations().last().setStatus(coordination.getStatus());
        movement.getCoordinations().last().setComment(coordination.getComment());

        if (user.getLocation().getParentId() == null) {
          movement.setStatus(Movement.Status.APPROVED);

          movementRepository.save(movement);
        } else {
          Integer chiefUserId = null;
          Location parentLocation = movement.getCoordinations().last().getChief().getLocation().getParent();

          while (chiefUserId == null) {
            chiefUserId = parentLocation.getResponsibleUserId();
            parentLocation = parentLocation.getParent();
          }

          Coordination nextCoordination = new Coordination(movement.getId(), chiefUserId, Coordination.Status.WAITING, null);

          coordinationRepository.save(nextCoordination);
        }
      }
      case SENT -> {
        Coordination nextCoordination = new Coordination(movement.getId(), user.getId(), Coordination.Status.SENT, coordination.getComment());

        coordinationRepository.save(nextCoordination);

        movement.setStatus(Movement.Status.SENT);

        movementRepository.save(movement);
      }
      case ACCEPTED -> {
        Item item = movement.getItem();

        if (item.getQuantity() - movement.getQuantity() > 0 && movement.getType() == Movement.Type.MOVEMENT) {
          Optional<Item> lastItem = itemRepository.findTopByCategoryIdOrderByIdDesc(item.getCategoryId());

          item.setQuantity(item.getQuantity() - movement.getQuantity());

          itemRepository.save(item);

          Item newItem = itemRepository.save(new Item(
            item.getCategory().getPrefix() == null ? "" : item.getCategory().getPrefix(),
            lastItem.get().getNumber() + 1,
            item.getTitle(),
            movement.getQuantity(),
            item.getCategoryId(),
            movement.getLocationToId()));

          Movement newItemFirstMovement = new Movement(Movement.Type.MOVEMENT, newItem.getId(), newItem.getQuantity(), newItem.getLocationId(), user.getId(), Movement.Status.SUCCESS);

          movementRepository.save(newItemFirstMovement);
        } else if (item.getQuantity() - movement.getQuantity() > 0 && movement.getType() == Movement.Type.WRITE_OFF) {
          item.setQuantity(item.getQuantity() - movement.getQuantity());
        } else if (movement.getType() == Movement.Type.WRITE_OFF) {
          itemRepository.deleteById(item.getId());
        } else {
          item.setLocationId(user.getLocationId());

          itemRepository.save(item);
        }

        movement.setStatus(Movement.Status.SUCCESS);

        Coordination newCoordination = new Coordination(movement.getId(), user.getId(), Coordination.Status.ACCEPTED, coordination.getComment());

        coordinationRepository.save(newCoordination);

        movementRepository.save(movement);
      }
    }
  }

}
