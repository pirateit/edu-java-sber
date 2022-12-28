package com.inventory.main.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/locations")
public class LocationMvcController {

    @GetMapping
    public String getLocations(Principal principal) {
        return "locations/index";
//        return principal != null ? "locations/index" : "login";
    }

    @GetMapping("/create")
    public String getLocationCreate() {
        return "locations/create";
    }

}
