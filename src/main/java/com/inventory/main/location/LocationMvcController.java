package com.inventory.main.location;

import com.inventory.main.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/locations")
public class LocationMvcController {

    private final LocationRepository locationRepo;
    private final UserRepository userRepo;

    @Autowired
    public LocationMvcController(LocationRepository locationRepo, UserRepository userRepo) {
        this.locationRepo = locationRepo;
        this.userRepo = userRepo;
    }

    @ModelAttribute
    public void addToModel(Model model) {
        model.addAttribute("locations", locationRepo.findAll());
        model.addAttribute("users", userRepo.findAll());
    }

    @ModelAttribute(name = "location")
    public Location location() {
        return new Location();
    }

    @GetMapping
    public String getLocations(Model model) {
        model.addAttribute("title", "Подразделения");

        return "locations/index";
    }

    @GetMapping("/create")
    public String getLocationCreate(Model model) {
        model.addAttribute("title", "Новое подразделение");

        return "locations/create";
    }

    @PostMapping("/create")
    public String createLocation(@Valid Location location, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Новое подразделени");

            return "locations/create";
        }

        Integer parentDepth = locationRepo.findById(location.getParentId()).get().getDepth();

        if (parentDepth != null) {
            location.setDepth(parentDepth + 1);
        } else {
            location.setDepth(1);
        }

        location.setTitle(location.getTitle().trim());

        locationRepo.save(location);

        return "redirect:/locations";
    }

    @GetMapping("/{id}")
    public String getLocationUpdate(@PathVariable("id") Integer id, Model model) {
        Optional<Location> location = locationRepo.findById(id);

        if (location.isEmpty()) {
            return "redirect:/locations";
        }

        model.addAttribute("title", "Изменение подразделения - " + location.get().getTitle());
        model.addAttribute("location", location.get());

        return "locations/update";
    }

    @PostMapping("/{id}")
    public String updateLocation(@PathVariable("id") Integer id, @Valid Location newLocation, Errors errors, Model model) {
        Optional<Location> location = locationRepo.findById(id);

        if (location.isEmpty()) {
            return "redirect:/locations";
        }

        if (errors.hasErrors()) {
            model.addAttribute("title", "Изменение подразделения - " + location.get().getTitle());
            model.addAttribute("location", location.get());

            return "locations/update";
        }

        if (location.get().getParentId() != null) {
            Integer parentDepth = locationRepo.findById(location.get().getParentId()).get().getDepth();

            if (parentDepth != null) {
                newLocation.setDepth(parentDepth + 1);
            } else {
                newLocation.setDepth(1);
            }
        } else {
            newLocation.setDepth(null);
        }

        newLocation.setTitle(newLocation.getTitle().trim());
        newLocation.setParentId(location.get().getParentId());

        locationRepo.save(newLocation);

        return "redirect:/locations";
    }

    @PostMapping("/{id}/delete")
    public String updateLocation(@PathVariable("id") Integer id) {
        locationRepo.deleteById(id);

        return "redirect:/locations";
    }

}
