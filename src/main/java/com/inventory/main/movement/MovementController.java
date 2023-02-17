package com.inventory.main.movement;

import com.inventory.main.MainController;
import com.inventory.main.item.Item;
import com.inventory.main.item.ItemService;
import com.inventory.main.location.Location;
import com.inventory.main.location.LocationService;
import com.inventory.main.user.User;
import com.inventory.main.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/movements")
public class MovementController extends MainController {

  private final MovementService movementService;
  private final ItemService itemService;
  private final LocationService locationService;
  private final CoordinationService coordinationService;
  private final UserService userService;

  public MovementController(
    MovementService movementService,
    ItemService itemService,
    LocationService locationService,
    CoordinationService coordinationService,
    UserService userService) {
    super(movementService);
    this.itemService = itemService;
    this.movementService = movementService;
    this.locationService = locationService;
    this.coordinationService = coordinationService;
    this.userService = userService;
  }

  @GetMapping
  public String getMovements(
    @RequestParam(name = "page", required = false) Integer page,
    @RequestParam(name = "size", required = false) Integer size,
    @RequestParam(name = "type", required = false) Movement.Type type,
    @RequestParam(name = "itemTitle", required = false) String itemTitle,
    @RequestParam(name = "locationsFrom", required = false) Set<Integer> locationsFrom,
    @RequestParam(name = "locationsTo", required = false) Set<Integer> locationsTo,
    @RequestParam(name = "user", required = false) Integer requestedUser,
    @RequestParam(name = "status", required = false) String status,
    Model model,
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {
    if (request.isUserInRole(User.Role.OWNER.name()) || request.isUserInRole(User.Role.ADMIN.name())) {
      model.addAttribute("movements", movementService.getAll(page, size, null, type, itemTitle, locationsFrom, locationsTo, requestedUser, status));
      model.addAttribute("locations", locationService.getAllActive());
      model.addAttribute("users", userService.getAllActive());
    } else {
      Set<Location> userLocations = locationService.getUserLocations(user.getId());
      Set<Integer> userLocationsIds = userLocations.stream().map(Location::getId).collect(Collectors.toSet());
      Set<Integer> userLocationsFrom = new HashSet<>();
      Set<Integer> userLocationsTo = new HashSet<>();

      if (locationsFrom != null && !locationsFrom.isEmpty()) {
        userLocationsFrom.addAll(locationsFrom);
        userLocationsFrom.retainAll(userLocationsIds);
      }

      if (locationsTo != null && !locationsTo.isEmpty()) {
        userLocationsTo.addAll(locationsTo);
        userLocationsTo.retainAll(userLocationsIds);
      }

      model.addAttribute("movements", movementService.getAll(page, size, userLocationsIds, type, itemTitle, userLocationsFrom, userLocationsTo, requestedUser, status));
      model.addAttribute("locations", userLocations);
      model.addAttribute("users", userService.getUsersByLocations(userLocationsIds));
    }

    model.addAttribute("fType", type);
    model.addAttribute("fItemTitle", itemTitle);
    model.addAttribute("fLocationsFrom", locationsFrom);
    model.addAttribute("fLocationsTo", locationsTo);
    model.addAttribute("fUser", requestedUser);
    model.addAttribute("query", new MovementFiltersForm(type, itemTitle, locationsFrom, locationsTo, requestedUser, status).toQueryString());
    model.addAttribute("title", "Перемещения");

    return "movements/index";
  }

  @GetMapping("/waiting")
  public String getWaitingMovements(
    @RequestParam(name = "page", required = false) Integer page,
    @RequestParam(name = "size", required = false) Integer size,
    Model model,
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {
    if (request.isUserInRole(User.Role.OWNER.name()) || request.isUserInRole(User.Role.ADMIN.name())) {
      model.addAttribute("movements", movementService.getAll(page, size, null, null, null, null, null, null, "active"));
    } else {
      model.addAttribute("movements", movementService.getWaiting(page, size, user.getId()));
    }

    model.addAttribute("title", "Перемещения");

    return "movements/index";
  }

  @GetMapping("/{id}")
  public String getMovement(@PathVariable("id") Integer id, Model model, @AuthenticationPrincipal User user, HttpServletRequest request) {
    Optional<Movement> movement = movementService.getById(id);

    if (movement.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    Set<Location> userLocations = locationService.getUserLocations(user.getId());

    if (userLocations.contains(movement.get().getLocationFrom())
      || userLocations.contains(movement.get().getLocationTo())
      || request.isUserInRole(User.Role.OWNER.name())
      || request.isUserInRole(User.Role.ADMIN.name())) {
      Optional<Coordination> lastCoordination = coordinationService.getLastByMovementId(id);

      lastCoordination.ifPresent(coordination -> model.addAttribute("lastCoordination", coordination));

      model.addAttribute("userLocations", userLocations);
      model.addAttribute("movement", movement.get());
      model.addAttribute("title", "Перемещение №" + id);

      return "movements/movement";
    }

    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  }

  @PostMapping("/{id}/coordinations")
  public String createCoordination(
    @PathVariable("id") Integer id,
    Coordination coordination,
    @AuthenticationPrincipal User user) {
    Optional<Movement> movement = movementService.getById(id);

    if (movement.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    Optional<Coordination> lastCoordination = coordinationService.getLastByMovementId(id);

    if (lastCoordination.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    switch (coordination.getStatus()) {
      case REFUSED -> {
        if (!lastCoordination.get().getChiefUserId().equals(user.getId())) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        lastCoordination.get().setStatus(coordination.getStatus());
        lastCoordination.get().setComment(coordination.getComment());

        movement.get().setStatus(Movement.Status.CANCELLED);

        movementService.update(movement.get());
      }
      case COORDINATED -> {
        if (!lastCoordination.get().getChiefUserId().equals(user.getId())) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        lastCoordination.get().setStatus(coordination.getStatus());

        lastCoordination.get().setComment(coordination.getComment());

        if (user.getLocation().getParentId() == null) {
          movement.get().setStatus(Movement.Status.APPROVED);

          movementService.update(movement.get());
        } else {
          Integer chiefUserId = null;
          Location parentLocation = lastCoordination.get().getChief().getLocation().getParent();

          while (chiefUserId == null) {
            chiefUserId = parentLocation.getResponsibleUserId();
            parentLocation = parentLocation.getParent();
          }

          coordinationService.create(id, chiefUserId);
        }
      }
      case SENT -> {
        Set<Location> userLocations = locationService.getUserLocations(user.getId());
//        !movement.get().getRequestedUserId().equals(user.getId())
        if (!userLocations.contains(movement.get().getLocationFrom())) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        coordinationService.create(id, user.getId(), Coordination.Status.SENT, coordination.getComment());

        movement.get().setStatus(Movement.Status.SENT);

        movementService.update(movement.get());
      }
      case ACCEPTED -> {
        if (!movement.get().getLocationTo().getResponsibleUserId().equals(user.getId())) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Item item = movement.get().getItem();

        if (item.getQuantity() - movement.get().getQuantity() > 0 && movement.get().getType() == Movement.Type.MOVEMENT) {
          Optional<Item> lastItem = itemService.getLastInCategory(item.getCategoryId());
          Item newItem = new Item(
            item.getCategory().getPrefix() == null ? "" : item.getCategory().getPrefix(),
            lastItem.get().getNumber() + 1, item.getTitle(), movement.get().getQuantity(), item.getCategoryId(), movement.get().getLocationToId());

          item.setQuantity(item.getQuantity() - movement.get().getQuantity());
          itemService.create(newItem, user);
        } else if (item.getQuantity() - movement.get().getQuantity() > 0 && movement.get().getType() == Movement.Type.WRITE_OFF) {
          item.setQuantity(item.getQuantity() - movement.get().getQuantity());
        } else if (movement.get().getType() == Movement.Type.WRITE_OFF) {
          itemService.delete(item.getId());
        } else {
          item.setLocationId(user.getLocationId());
        }

        movement.get().setStatus(Movement.Status.SUCCESS);

        coordinationService.create(id, user.getId(), Coordination.Status.ACCEPTED, coordination.getComment());
        movementService.update(movement.get());
      }
    }

    return "redirect:/movements/" + id;
  }

}
