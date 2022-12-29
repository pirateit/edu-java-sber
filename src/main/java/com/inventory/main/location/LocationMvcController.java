package com.inventory.main.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/locations")
public class LocationMvcController {

    private final LocationRepository locationRepo;

    @Autowired
    public LocationMvcController(LocationRepository locationRepo) {
        this.locationRepo = locationRepo;
    }

//    @ModelAttribute
//    public void location(Model model) {
//        model.addAttribute("title", "Shit title from Model");
//    }

    @GetMapping
    public String getLocations(Principal principal, Model model) {
        model.addAttribute("title", "Локации");
        return "locations/index";
//        return principal != null ? "locations/index" : "login";
    }

    @GetMapping("/create")
    public String getLocationCreate(Model model) {
        model.addAttribute("title", "Добавление локации");
        Iterable<Location> locations = locationRepo.findAll();
        model.addAttribute("locations", "");
        System.out.println(locations);

        return "locations/create";
    }

}
