package com.inventory.main.setting;

import com.inventory.main.MainController;
import com.inventory.main.category.Category;
import com.inventory.main.category.CategoryRepository;
import com.inventory.main.category.CategoryService;
import com.inventory.main.location.Location;
import com.inventory.main.location.LocationService;
import com.inventory.main.movement.MovementService;
import com.inventory.main.user.User;
import com.inventory.main.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/settings")
public class SettingController extends MainController {

  private final CategoryService categoryService;
  private final LocationService locationService;
  private final UserService userService;

  public SettingController(
    MovementService movementService,
    CategoryService categoryService,
    LocationService locationService,
    UserService userService) {
    super(movementService);
    this.categoryService = categoryService;
    this.locationService = locationService;
    this.userService = userService;
  }

  @GetMapping
  public String getSettings() {
    return "redirect:settings/users";
  }

  @GetMapping("/users")
  public String getUsers(Model model) {
    model.addAttribute("users", userService.getAllActive());
    model.addAttribute("title", "Сотрудники");

    return "settings/users/index";
  }

  @GetMapping("/users/deleted")
  public String getDeletedUsers(Model model) {
    model.addAttribute("users", userService.getAllInactive());
    model.addAttribute("title", "Сотрудники (Корзина)");

    return "settings/users/deleted";
  }

  @GetMapping("/users/create")
  public String getUserCreate(Model model) {
    model.addAttribute("user", new User());
    model.addAttribute("locations", locationService.getAllActive());
    model.addAttribute("title", "Новый пользователь");

    return "settings/users/create";
  }

  @PostMapping("/users")
  public String createUser(@Valid User user, Errors errors, Model model) {
    if (errors.hasErrors()) {
      model.addAttribute("user", user);
      model.addAttribute("locations", locationService.getAllActive());
      model.addAttribute("title", "Новый пользователь");

      return "settings/users/create";
    }

    userService.create(user);

    return "redirect:/settings/users";
  }

  @GetMapping("/users/{id}")
  public String getUser(@PathVariable("id") int id, Model model) {
    Optional<User> user = userService.getById(id);

    if (user.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    model.addAttribute("user", user.get());
    model.addAttribute("locations", locationService.getAllActive());
    model.addAttribute("title", "Редактирование пользователя - " + user.get().getLastName() + " " + user.get().getFirstName());

    return "settings/users/update";
  }

  @PostMapping("/users/{id}")
  public String updateUser(
    @PathVariable("id") int id,
    @Valid User newUserData,
    Errors errors,
    Model model) {
    Optional<User> user = userService.getById(id);

    if (user.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    if (errors.hasErrors()) {
      model.addAttribute("user", newUserData);
      model.addAttribute("locations", locationService.getAllActive());
      model.addAttribute("title", "Редактирование пользователя - " + user.get().getLastName() + " " + user.get().getFirstName());

      return "settings/users/update";
    }

    userService.update(user.get(), newUserData);

    return "redirect:/settings/users";
  }

  @GetMapping("/users/{id}/delete")
  public String deleteUser(@PathVariable("id") int id, Model model) {
    Optional<User> user = userService.getById(id);

    if (user.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    try {
      userService.delete(user.get());
    } catch (Exception err) {
      model.addAttribute("user", user.get());
      model.addAttribute("locations", locationService.getAllActive());
      model.addAttribute("deleteError", err.getMessage());
      model.addAttribute("title", "Редактирование пользователя - " + user.get().getLastName() + " " + user.get().getFirstName());

      return "settings/users/update";
    }

    return "redirect:/settings/users";
  }

  @GetMapping("/users/{id}/restore")
  public String restoreUser(@PathVariable("id") int id) {
    userService.restore(id);

    return "redirect:/settings/users";
  }

  @GetMapping("/locations")
  public String getLocations(Model model) {
    model.addAttribute("locations", locationService.getParents());
    model.addAttribute("title", "Подразделения");

    return "settings/locations/index";
  }

  @GetMapping("/locations/create")
  public String getLocationCreate(Model model) {
    model.addAttribute("location", new Location());
    model.addAttribute("locations", locationService.getAllActive());
    model.addAttribute("users", userService.getAllActive());
    model.addAttribute("title", "Новое подразделение");

    return "settings/locations/create";
  }

  @PostMapping("/locations/create")
  public String createLocation(@Valid Location location, Errors errors, Model model) {
    if (errors.hasErrors()) {
      model.addAttribute("location", location);
      model.addAttribute("locations", locationService.getAllActive());
      model.addAttribute("users", userService.getAllActive());
      model.addAttribute("title", "Новое подразделение");

      return "settings/locations/create";
    }

    locationService.create(location);

    return "redirect:/settings/locations";
  }

  @GetMapping("/locations/{id}")
  public String getUpdateLocationPage(@PathVariable("id") Integer id, Model model) {
    Optional<Location> location = locationService.getById(id);

    if (location.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    model.addAttribute("location", location.get());
    model.addAttribute("users", userService.getAllActive());
    model.addAttribute("title", "Изменение подразделения - " + location.get().getTitle());

    return "settings/locations/update";
  }

  @PostMapping("/locations/{id}")
  public String updateLocation(@PathVariable("id") Integer id, @Valid Location newLocation, Errors errors, Model model) {
    Optional<Location> location = locationService.getById(id);

    if (location.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    if (errors.hasErrors()) {
      model.addAttribute("location", newLocation);
      model.addAttribute("users", userService.getAllActive());
      model.addAttribute("title", "Изменение подразделения - " + location.get().getTitle());

      return "settings/locations/update";
    }

    locationService.update(location.get(), newLocation);

    return "redirect:/settings/locations";
  }

  @GetMapping("/locations/{id}/delete")
  public String deleteLocation(@PathVariable("id") Integer id, Model model) {
    Optional<Location> location = locationService.getById(id);

    if (location.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    if (!location.get().getChildren().isEmpty() || !location.get().getItems().isEmpty()) {
      model.addAttribute("location", location.get());
      model.addAttribute("users", userService.getAllActive());
      model.addAttribute("error", "Подразделение имеет инвентаризационные предметы или дочерние подразделения");
      model.addAttribute("title", "Изменение подразделения - " + location.get().getTitle());

      return "settings/locations/update";
    }

    locationService.delete(id);

    return "redirect:/settings/locations";
  }

  @GetMapping("/locations/{id}/restore")
  public String restoreLocation(@PathVariable("id") int id) {
    Optional<Location> location = locationService.getById(id);

    if (location.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    locationService.restore(id);

    return "redirect:/settings/locations";
  }

  @GetMapping("/categories")
  public String getCategoriesPage(Model model) {
    model.addAttribute("categories", categoryService.getParents());
    model.addAttribute("title", "Категории");

    return "settings/categories/index";
  }

  @GetMapping("/categories/create")
  public String getCreateCategoryPage(Model model) {
    model.addAttribute("category", new Category());
    model.addAttribute("categories", categoryService.getAll());
    model.addAttribute("title", "Новая категория");

    return "settings/categories/create";
  }

  @PostMapping("/categories/create")
  public String createCategory(@Valid Category category, Errors errors, Model model) {
    if (errors.hasErrors()) {
      model.addAttribute("category", category);
      model.addAttribute("categories", categoryService.getAll());
      model.addAttribute("title", "Новая категория");

      return "settings/categories/create";
    }

    categoryService.create(category);

    return "redirect:/settings/categories";
  }

  @GetMapping("/categories/{id}")
  public String getUpdateCategoryPage(@PathVariable("id") Integer id, Model model) {
    Optional<Category> category = categoryService.getById(id);

    if (category.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    model.addAttribute("category", category.get());
    model.addAttribute("categories", categoryService.getAll());
    model.addAttribute("title", "Изменение категории - " + category.get().getTitle());

    return "settings/categories/update";
  }

  @PostMapping("/categories/{id}")
  public String updateCategory(@PathVariable("id") Integer id, @Valid Category newCategory, Errors errors, Model model) {
    Optional<Category> category = categoryService.getById(id);

    if (category.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    if (errors.hasErrors()) {
      model.addAttribute("category", newCategory);
      model.addAttribute("categories", categoryService.getAll());
      model.addAttribute("title", "Изменение категории - " + category.get().getTitle());

      return "settings/categories/update";
    }

    categoryService.update(category.get(), newCategory);

    return "redirect:/settings/categories";
  }

  @GetMapping("/categories/{id}/delete")
  public String deleteCategory(@PathVariable("id") Integer id, Model model) {
    Optional<Category> category = categoryService.getById(id);

    if (category.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    try {
      categoryService.delete(category.get());
    } catch (Exception err) {
      model.addAttribute("category", category.get());
      model.addAttribute("categories", categoryService.getAll());
      model.addAttribute("deleteError", err.getMessage());
      model.addAttribute("title", "Изменение категории - " + category.get().getTitle());

      return "settings/categories/update";
    }

    return "redirect:/settings/categories";
  }

}
