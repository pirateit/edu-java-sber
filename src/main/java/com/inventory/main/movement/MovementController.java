package com.inventory.main.movement;

import com.inventory.main.MainController;
import com.inventory.main.item.Item;
import com.inventory.main.item.ItemService;
import com.inventory.main.location.Location;
import com.inventory.main.location.LocationService;
import com.inventory.main.user.User;
import com.inventory.main.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

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
  public String getMovementsPage(
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
  public String getWaitingMovementsPage(
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
  public String getMovementPage(@PathVariable("id") Integer id, Model model, @AuthenticationPrincipal User user, HttpServletRequest request) {
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

    Coordination lastCoordination = movement.get().getCoordinations().last();

    switch (coordination.getStatus()) {
      case REFUSED, COORDINATED -> {
        if (lastCoordination.getChiefUserId() != (user.getId())) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
      }
      case SENT -> {
        Set<Location> userLocations = locationService.getUserLocations(user.getId());
        if (!userLocations.contains(movement.get().getLocationFrom())) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
      }
      case ACCEPTED -> {
        if (!movement.get().getLocationTo().getResponsibleUserId().equals(user.getId())) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
      }
    }

    movementService.coordinate(movement.get(), coordination, user);

    return "redirect:/movements/" + id;
  }

}
