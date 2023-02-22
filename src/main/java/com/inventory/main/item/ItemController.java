package com.inventory.main.item;

import com.inventory.main.MainController;
import com.inventory.main.category.CategoryService;
import com.inventory.main.location.Location;
import com.inventory.main.location.LocationService;
import com.inventory.main.movement.*;
import com.inventory.main.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/items")
public class ItemController extends MainController {

  private final MovementService movementService;
  private final CategoryService categoryService;
  private final LocationService locationService;
  private final ItemService itemService;

  public ItemController(
    MovementService movementService,
    LocationService locationService,
    ItemService itemService,
    CategoryService categoryService) {
    super(movementService);
    this.locationService = locationService;
    this.itemService = itemService;
    this.categoryService = categoryService;
    this.movementService = movementService;
  }

  @GetMapping
  public String getActiveItemsPage(
    @RequestParam(name = "page", required = false) Integer page,
    @RequestParam(name = "size", required = false) Integer size,
    @RequestParam(name = "title", required = false) String title,
    @RequestParam(name = "locations", required = false) Set<Integer> locations,
    @RequestParam(name = "categories", required = false) Set<Integer> categories,
    Model model,
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {
    if (request.isUserInRole(User.Role.OWNER.name()) || request.isUserInRole(User.Role.ADMIN.name())) {
      model.addAttribute("locations", locationService.getAllActive());
      model.addAttribute("items", itemService.getAllActive(page, size, null, title, locations, categories));
    } else {
      Set<Location> userLocations = locationService.getUserLocations(user.getId());
      Set<Integer> userLocationsIds = userLocations.stream().map(Location::getId).collect(Collectors.toSet());
      Set<Integer> locationsFilter = new HashSet<>();

      if (locations != null && !locations.isEmpty()) {
        locationsFilter.addAll(locations);
        locationsFilter.retainAll(userLocationsIds);
      }

      model.addAttribute("locations", userLocations);
      model.addAttribute("items", itemService.getAllActive(page, size, userLocationsIds, title, locationsFilter, categories));
    }

    model.addAttribute("fTitle", title);
    model.addAttribute("fLocations", locations);
    model.addAttribute("fCategories", categories);
    model.addAttribute("categories", categoryService.getAll());
    model.addAttribute("query", new ItemFiltersForm(title, locations, categories).toQueryString());
    model.addAttribute("title", "Склад");

    return "items/index";
  }

  @GetMapping("/deleted")
  public String getInactiveItemsPage(
    @RequestParam(name = "page", required = false) Integer page,
    @RequestParam(name = "size", required = false) Integer size,
    @RequestParam(name = "title", required = false) String title,
    @RequestParam(name = "locations", required = false) Set<Integer> locations,
    @RequestParam(name = "categories", required = false) Set<Integer> categories,
    Model model,
    HttpServletRequest request) {
    if (!request.isUserInRole(User.Role.OWNER.name()) && !request.isUserInRole(User.Role.ADMIN.name())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    model.addAttribute("items", itemService.getAllInactive(page, size, title, locations, categories));
    model.addAttribute("categories", categoryService.getAll());
    model.addAttribute("locations", locationService.getAllActive());
    model.addAttribute("query", new ItemFiltersForm(title, locations, categories).toQueryString());
    model.addAttribute("title", "Склад (Корзина)");

    return "items/index";
  }

  @GetMapping("/create")
  public String getCreateItemPage(Model model) {
    model.addAttribute("item", new Item());
    model.addAttribute("categories", categoryService.getAll());
    model.addAttribute("locations", locationService.getAllActive());
    model.addAttribute("title", "Новый предмет");

    return "items/create";
  }

  @PostMapping("/create")
  public String createItem(@Valid Item item, Errors errors, Model model, @AuthenticationPrincipal User user) {
    Optional<Item> itemByPrefixAndNumber = itemService.getByInventoryNumber(item.getPrefix(), item.getNumber());

    if (errors.hasErrors() || itemByPrefixAndNumber.isPresent()) {
      model.addAttribute("item", item);
      model.addAttribute("categories", categoryService.getAll());
      model.addAttribute("locations", locationService.getAllActive());
      model.addAttribute("duplicateError", itemByPrefixAndNumber.isPresent());
      model.addAttribute("title", "Новый предмет");

      return "items/create";
    }

    itemService.create(item, user);

    return "redirect:/items";
  }

  @GetMapping("/{id}")
  public String getUpdateItemPage(
    @PathVariable("id") Integer id,
    Model model,
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {
    Optional<Item> item = itemService.getById(id);
    Set<Location> userLocations = locationService.getUserLocations(user.getId());

    if (item.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    if (!userLocations.contains(item.get().getLocation()) && (!request.isUserInRole(User.Role.OWNER.name()) && !request.isUserInRole(User.Role.ADMIN.name()))) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    Optional<Movement> activeMovement = movementService.getActiveByItemId(id);

    String inventoryNumber = item.get().getPrefix() == null || item.get().getPrefix().length() == 0 ? item.get().getNumber().toString() : item.get().getPrefix() + "-" + item.get().getNumber();

    model.addAttribute("item", item.get());
    model.addAttribute("canMove", activeMovement.isEmpty());
    model.addAttribute("canUpdate", request.isUserInRole(User.Role.OWNER.name()) || request.isUserInRole(User.Role.ADMIN.name()));
    model.addAttribute("title", item.get().getTitle() + " (" + inventoryNumber + ")");

    return "items/update";
  }

  @PostMapping("/{id}")
  public String updateItem(
    @PathVariable("id") Integer id,
    @Valid Item newItem,
    Errors errors,
    Model model,
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {
    Optional<Item> item = itemService.getById(id);
    Optional<Item> itemByPrefixAndNumber = itemService.getByInventoryNumber(newItem.getPrefix(), newItem.getNumber());
    Set<Location> userLocations = locationService.getUserLocations(user.getId());

    if (item.isEmpty() || !userLocations.contains(item.get().getLocation()) && (!request.isUserInRole(User.Role.OWNER.name()) && !request.isUserInRole(User.Role.ADMIN.name()))) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    if (errors.hasErrors() || (itemByPrefixAndNumber.isPresent() && item.get() != itemByPrefixAndNumber.get())) {
      List<Movement> activeMovements = item.get().getMovements()
        .stream()
        .filter(move -> move.getStatus() != Movement.Status.SUCCESS && move.getStatus() != Movement.Status.CANCELLED)
        .toList();

      String inventoryNumber = item.get().getPrefix().length() != 0 ? item.get().getPrefix() + "-" + item.get().getNumber() : item.get().getNumber().toString();

      model.addAttribute("item", newItem);
      model.addAttribute("canMove", activeMovements.isEmpty());
      model.addAttribute("canUpdate", request.isUserInRole(User.Role.OWNER.name()) || request.isUserInRole(User.Role.ADMIN.name()));
      model.addAttribute("duplicateError", itemByPrefixAndNumber.isPresent() && item.get() != itemByPrefixAndNumber.get());
      model.addAttribute("title", item.get().getTitle() + " (" + inventoryNumber + ")");

      return "items/update";
    }

    if (request.isUserInRole(User.Role.OWNER.name()) || request.isUserInRole(User.Role.ADMIN.name())) {
      item.get().setPrefix(newItem.getPrefix());
      item.get().setNumber(newItem.getNumber());
      item.get().setTitle(newItem.getTitle().trim());
    } else {
      item.get().setTitle(newItem.getTitle().trim());
    }

    itemService.update(item.get());

    return "redirect:/items/" + id;
  }

  @GetMapping("/{id}/move")
  public String getMoveItemPage(
    @PathVariable("id") Integer id,
    Model model,
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {
    Optional<Item> item = itemService.getById(id);
    Set<Location> userLocations = locationService.getUserLocations(user.getId());

    if (item.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    if (!userLocations.contains(item.get().getLocation()) && (!request.isUserInRole(User.Role.OWNER.name()) && !request.isUserInRole(User.Role.ADMIN.name()))) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    if (movementService.getActiveByItemId(id).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    String inventoryNumber = item.get().getPrefix().length() != 0 ? item.get().getPrefix() + "-" + item.get().getNumber() : item.get().getNumber().toString();

    model.addAttribute("locations", locationService.getAllActive());
    model.addAttribute("item", item.get());
    model.addAttribute("movement", new Movement());
    model.addAttribute("title", "Перемещение предмета - " + item.get().getTitle() + " (" + inventoryNumber + ")");

    return "items/move";
  }

  @PostMapping("/{id}/move")
  public String moveItem(
    @PathVariable("id") Integer id,
    @Valid Movement movement,
    Errors errors,
    Model model,
    @AuthenticationPrincipal User user,
    HttpServletRequest request) {
    Optional<Item> item = itemService.getById(id);
    Set<Location> userLocations = locationService.getUserLocations(user.getId());

    if (item.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    if (!userLocations.contains(item.get().getLocation()) && (!request.isUserInRole(User.Role.OWNER.name()) && !request.isUserInRole(User.Role.ADMIN.name()))) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    if (movementService.getActiveByItemId(id).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    if (errors.hasErrors() || (movement.getType() == Movement.Type.MOVEMENT && movement.getLocationToId() == null)) {
      String inventoryNumber = item.get().getPrefix().length() != 0 ? item.get().getPrefix() + "-" + item.get().getNumber() : item.get().getNumber().toString();

      model.addAttribute("locations", locationService.getAllActive());
      model.addAttribute("item", item.get());
      model.addAttribute("movement", movement);
      model.addAttribute("locationToError", movement.getType() == Movement.Type.MOVEMENT && movement.getLocationToId() == null);
      model.addAttribute("title", "Перемещение предмета - " + item.get().getTitle() + " (" + inventoryNumber + ")");

      return "items/move";
    }

    // TODO: новый объект не создаётся, а берётся из БД
    movement.setId(null);

    if (request.isUserInRole(User.Role.OWNER.name()) || request.isUserInRole(User.Role.ADMIN.name()) || user.getLocation().getDepth() == null) {
      movementService.createWithoutCoordination(movement, item.get(), user);
    } else {
      movementService.createWithCoordination(movement, item.get(), user);
    }

    return "redirect:/movements";
  }

}
