package com.inventory.main.movement;

import com.inventory.main.item.CategoryRepository;
import com.inventory.main.item.Item;
import com.inventory.main.item.ItemRepository;
import com.inventory.main.location.Location;
import com.inventory.main.location.LocationRepository;
import com.inventory.main.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/movements")
public class MovementMvcController {

    private final ItemRepository itemRepo;
    private final CategoryRepository categoryRepo;
    private final LocationRepository locationRepository;
    private final MovementRepository movementRepository;
    private final CoordinationRepository coordinationRepository;

    public int pageSize = 20;

    @Autowired
    public MovementMvcController(ItemRepository itemRepo, CategoryRepository categoryRepo, LocationRepository locationRepository, MovementRepository movementRepository, CoordinationRepository coordinationRepository) {
        this.itemRepo = itemRepo;
        this.categoryRepo = categoryRepo;
        this.locationRepository = locationRepository;
        this.movementRepository = movementRepository;
        this.coordinationRepository = coordinationRepository;
    }

    @GetMapping("/all")
    public String getMovements(
            @RequestParam(name = "status", required = false) Movement.Status status,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            Model model,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of((page != null) ? page - 1 : 0, (size != null) ? size : pageSize);
        model.addAttribute("title", "Перемещения");
        model.addAttribute("movementsToApprove", movementRepository.countByCoordinationsChiefUserId(user.getId()));

        if (status != null) {
            model.addAttribute("movements", movementRepository.findByCoordinationsChiefUserId(user.getId(), pageable));

            return "movements/index";
        }

        model.addAttribute("movements", movementRepository.findAllByRequestedUserIdOrderByCreatedAtDesc(user.getId(), pageable));

        return "movements/index";
    }

    @GetMapping("/{id}")
    public String getMovement(@PathVariable("id") Integer id, Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("movement", movementRepository.findById(id).get());
        model.addAttribute("coordinations", coordinationRepository.findAllByMovementIdOrderByIdAsc(id));
        model.addAttribute("movementsToApprove", movementRepository.countByCoordinationsChiefUserId(user.getId()));
        model.addAttribute("title", "Перемещение №" + id);

        return "movements/movement";
    }

}
