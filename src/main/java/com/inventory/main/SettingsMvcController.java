package com.inventory.main;

import com.inventory.main.item.CategoryRepository;
import com.inventory.main.location.Location;
import com.inventory.main.location.LocationRepository;
import com.inventory.main.user.User;
import com.inventory.main.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/settings")
public class SettingsMvcController {

    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public int pageSize = 20;

    @Autowired
    public SettingsMvcController(
            CategoryRepository categoryRepository,
            LocationRepository locationRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String getSettings() {
        return "redirect:settings/users";
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userRepository.findAllByDeletedAtIsNullOrderByLastNameAscFirstNameAsc());
        model.addAttribute("title", "Сотрудники");

        return "settings/users/index";
    }

    @GetMapping("/users/deleted")
    public String getDeletedUsers(Model model) {
        model.addAttribute("users", userRepository.findAllByDeletedAtIsNotNullOrderByLastNameAscFirstNameAsc());
        model.addAttribute("title", "Удалённые сотрудники");

        return "settings/users/deleted";
    }

    @GetMapping("/users/create")
    public String getUserCreate(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("locations", locationRepository.findAllByDeletedAtIsNull());
        model.addAttribute("title", "Новый пользователь");

        return "settings/users/create";
    }

    @PostMapping("/users")
    public String createUser(@Valid User user, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("locations", locationRepository.findAllByDeletedAtIsNull());
            model.addAttribute("title", "Новый пользователь");

            return "settings/users/create";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        return "redirect:/settings/users";
    }

    @GetMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") int id) {
        userRepository.deleteById(id);

        return "redirect:/settings/users";
    }

    @GetMapping("/users/{id}/restore")
    @Transactional
    public String restoreUser(@PathVariable("id") int id) {
        userRepository.restoreById(id);

        return "redirect:/settings/users";
    }

    @GetMapping("/users/{id}")
    public String getUser(@PathVariable("id") int id, Model model) {
        Optional<User> user = userRepository.findById(id);

        model.addAttribute("user", user.get());
        model.addAttribute("locations", locationRepository.findAllByDeletedAtIsNull());
        model.addAttribute("title", "Редактирование пользователя - " + user.get().getLastName() + " " + user.get().getFirstName());

        return "settings/users/update";
    }

    @PostMapping("/users/{id}")
    public String updateUser(
            @PathVariable("id") int id,
            @Valid User newUser,
            Errors errors,
            Model model) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return "redirect:/settings/users";
        }

        if (errors.hasErrors()) {
            model.addAttribute("user", user.get());
            model.addAttribute("locations", locationRepository.findAllByDeletedAtIsNull());
            model.addAttribute("title", "Редактирование пользователя - " + user.get().getLastName() + " " + user.get().getFirstName());

            return "settings/users/update";
        }

        if (!newUser.getPassword().isBlank()) {
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        } else {
            newUser.setPassword(user.get().getPassword());
        }

        newUser.setCreatedAt(user.get().getCreatedAt());

        userRepository.save(newUser);

        return "redirect:/settings/users";
    }

    @GetMapping("/locations")
    public String getLocations(Model model) {
        model.addAttribute("locations", getLocationChildren(locationRepository.findById(1).get()));
        model.addAttribute("title", "Подразделения");

        return "settings/locations/index";
    }

    @GetMapping("/locations/deleted")
    public String getDeletedLocations(Model model) {
        model.addAttribute("locations", locationRepository.findAllByDeletedAtIsNotNullOrderByTitleAsc());
        model.addAttribute("title", "Удалённые подразделения");

        return "settings/locations/deleted";
    }

    @GetMapping("/locations/create")
    public String getLocationCreate(Model model) {
        model.addAttribute("location", new Location());
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("users", userRepository.findAllByDeletedAtIsNullOrderByLastNameAscFirstNameAsc());
        model.addAttribute("title", "Новое подразделение");

        return "settings/locations/create";
    }

    @PostMapping("/locations/create")
    public String createLocation(@Valid Location location, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Новое подразделение");

            return "settings/locations/create";
        }

        Integer parentDepth = locationRepository.findById(location.getParentId()).get().getDepth();

        if (parentDepth != null) {
            location.setDepth(parentDepth + 1);
        } else {
            location.setDepth(1);
        }

        location.setTitle(location.getTitle().trim());

        locationRepository.save(location);

        return "redirect:/settings/locations";
    }

    @GetMapping("/locations/{id}")
    public String getLocationUpdate(@PathVariable("id") Integer id, Model model) {
        Optional<Location> location = locationRepository.findById(id);

        if (location.isEmpty()) {
            return "redirect:/settings/locations";
        }

        model.addAttribute("location", location.get());
        model.addAttribute("users", userRepository.findAllByDeletedAtIsNullOrderByLastNameAscFirstNameAsc());
        model.addAttribute("title", "Изменение подразделения - " + location.get().getTitle());

        return "settings/locations/update";
    }

    @PostMapping("/locations/{id}")
    public String updateLocation(@PathVariable("id") Integer id, @Valid Location newLocation, Errors errors, Model model) {
        Optional<Location> location = locationRepository.findById(id);

        if (location.isEmpty()) {
            return "redirect:/settings/locations";
        }

        if (errors.hasErrors()) {
            model.addAttribute("location", location.get());
            model.addAttribute("users", userRepository.findAllByDeletedAtIsNullOrderByLastNameAscFirstNameAsc());
            model.addAttribute("title", "Изменение подразделения - " + location.get().getTitle());

            return "settings/locations/update";
        }

        if (location.get().getParentId() != null) {
            Integer parentDepth = locationRepository.findById(location.get().getParentId()).get().getDepth();

            if (parentDepth != null) {
                newLocation.setDepth(parentDepth + 1);
            } else {
                newLocation.setDepth(1);
            }
        } else {
            newLocation.setDepth(null);
        }

        newLocation.setParentId(location.get().getParentId());

        locationRepository.save(newLocation);

        return "redirect:/settings/locations";
    }

    @GetMapping("/locations/{id}/delete")
    public String deleteLocation(@PathVariable("id") Integer id) {
        locationRepository.deleteById(id);

        return "redirect:/settings/locations";
    }

    @GetMapping("/locations/{id}/restore")
    @Transactional
    public String restoreLocation(@PathVariable("id") int id) {
        locationRepository.restoreById(id);

        return "redirect:/settings/locations";
    }

    private Location getLocationChildren(Location parent) {
        parent.setChildren(locationRepository.findByParentIdAndDeletedAtIsNullOrderByTitleAsc(parent.getId()));

        if (!parent.getChildren().isEmpty()) {
            parent.getChildren().forEach(this::getLocationChildren);
        }

        return parent;
    }

}
