package com.inventory.main.item;

import com.inventory.main.location.Location;
import com.inventory.main.location.LocationRepository;
import com.inventory.main.movement.*;
import com.inventory.main.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/items")
public class ItemMvcController {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final MovementRepository movementRepository;
    private final CoordinationRepository coordinationRepository;

    private int pageSize = 10;

    @Autowired
    public ItemMvcController(
            ItemRepository itemRepository,
            CategoryRepository categoryRepository,
            LocationRepository locationRepository,
            MovementRepository movementRepository,
            CoordinationRepository coordinationRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.movementRepository = movementRepository;
        this.coordinationRepository = coordinationRepository;
    }

    @GetMapping
    @Transactional
    public String getItems(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "locations", required = false) List<Integer> locations,
            @RequestParam(name = "categories", required = false) List<Integer> categories,
            Model model,
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(
                (page != null) ? page - 1 : 0,
                (size != null) ? size : pageSize);
        if (locations == null) locations = new ArrayList<>();
        if (categories == null) categories = new ArrayList<>();

        if (request.isUserInRole(User.Role.OWNER.name()) || request.isUserInRole(User.Role.ADMIN.name())) {
            model.addAttribute("locations", locationRepository.findAllByDeletedAtIsNull());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("items", itemRepository.findAllWithFilters(title, locations, categories, pageable));
//            model.addAttribute("items", itemRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable));
            model.addAttribute("title", "Склад");

            return "items/index";
        }

        Set<Location> userLocations = new HashSet<>();
        List<Integer> userLocationsIds = null;
        Page<Item> userItems = null;
        Iterable<Location> responsibleUserLocations = locationRepository.findByResponsibleUserId(user.getId());

        responsibleUserLocations.forEach(location -> {
            locationRepository.findChildrenById(location.getId()).forEach(loc -> {
                userLocations.add(loc);
            });
        });

        userLocationsIds = userLocations
                .stream()
                .map(location -> location.getId())
                .collect(Collectors.toList());

        userItems = itemRepository.findByLocationIdInOrderByCreatedAtDesc(userLocationsIds, pageable);

        model.addAttribute("locations", userLocations);
        model.addAttribute("items", userItems);
        model.addAttribute("title", "Склад");

        return "items/index";

    }

    @GetMapping("/create")
    public String getItemsCreate(Model model) {
        model.addAttribute("title", "Новый предмет");
        model.addAttribute("categories", categoryRepository.findAll());

        return "items/create";
    }

    @PostMapping("/create")
    public String createItem(@Valid Item item, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Новый предмет");
            model.addAttribute("categories", categoryRepository.findAll());

            return "items/create";
        }

        item.setTitle(item.getTitle().trim());

        itemRepository.save(item);

        return "redirect:/items";
    }

    @GetMapping("/{id}")
    public String getItemUpdate(@PathVariable("id") Integer id, Model model, @AuthenticationPrincipal User user) {
        Optional<Item> item = itemRepository.findById(id);
        String inventoryNumber = item.get().getNumber().toString();

        if (item.isEmpty()) {
            return "redirect:/items";
        }

        if (item.get().getLocation().getResponsibleUserId() != user.getId()) {
            throw new AccessDeniedException("You have no access to this object");
        }

        if (item.get().getPrefix().length() != 0) {
            inventoryNumber = item.get().getPrefix() + "-" + inventoryNumber;
        }

        model.addAttribute("item", item.get());
        model.addAttribute("movements", movementRepository.findByItemIdOrderByCreatedAtAsc(id));
        model.addAttribute("title", item.get().getTitle() + " (" + inventoryNumber + ")");

        return "items/update";
    }

    @PostMapping("/{id}")
    public String updateItem(@PathVariable("id") Integer id, @Valid Item newItem, Errors errors, Model model, @AuthenticationPrincipal User user) {
        Optional<Item> item = itemRepository.findById(id);
        String inventoryNumber = item.get().getNumber().toString();

        if (item.isEmpty()) {
            return "redirect:/items";
        }

        if (item.get().getLocation().getResponsibleUserId() != user.getId()) {
            throw new AccessDeniedException("You have no access to this object");
        }

        if (errors.hasErrors()) {
            if (item.get().getPrefix().length() != 0) {
                inventoryNumber = item.get().getPrefix() + "-" + inventoryNumber;
            }

            model.addAttribute("item", item.get());
            model.addAttribute("movements", movementRepository.findByItemIdOrderByCreatedAtAsc(id));
            model.addAttribute("title", item.get().getTitle() + " (" + inventoryNumber + ")");

            return "items/update";
        }

        newItem.setTitle(newItem.getTitle().trim());

        itemRepository.save(newItem);

        return "redirect:/items";
    }

    @PostMapping("/{id}/delete")
    public String updateItem(@PathVariable("id") Integer id) {
        itemRepository.deleteById(id);

        return "redirect:/items";
    }

    @GetMapping("/{id}/move")
    public String getItemMove(@PathVariable("id") Integer id, Model model, @AuthenticationPrincipal User user) {
        Optional<Item> item = itemRepository.findById(id);
        String inventoryNumber = item.get().getNumber().toString();

        if (item.isEmpty()) {
            return "redirect:/items";
        }

        if (item.get().getLocation().getResponsibleUserId() != user.getId()) {
            throw new AccessDeniedException("You have no access to this object");
        }

        if (item.get().getPrefix().length() != 0) {
            inventoryNumber = item.get().getPrefix() + "-" + inventoryNumber;
        }

        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("item", item.get());
        model.addAttribute("movement", new Movement());
        model.addAttribute("title", "Перемещение предмета - " + item.get().getTitle() + " (" + inventoryNumber + ")");

        return "items/move";
    }

    @Transactional
    @PostMapping("/{id}/move")
    public String moveItem(@PathVariable("id") Integer id, @Valid Movement movement, Errors errors, Model model, @AuthenticationPrincipal User user) {
        Optional<Item> item = itemRepository.findById(id);
        String inventoryNumber = item.get().getNumber().toString();

        if (item.get().getLocation().getResponsibleUserId() != user.getId()) {
            throw new AccessDeniedException("You have no access to this object");
        }

        if (item.get().getPrefix().length() != 0) {
            inventoryNumber = item.get().getPrefix() + "-" + inventoryNumber;
        }

        if (errors.hasErrors() || item.isEmpty()) {
            model.addAttribute("locations", locationRepository.findAll());
            model.addAttribute("item", item.get());
            model.addAttribute("movement", new Movement());
            model.addAttribute("title", "Перемещение предмета - " + item.get().getTitle() + " (" + inventoryNumber + ")");

            return "items/move";
        }

        movement.setItemId(id);
        movement.setLocationFromId(item.get().getLocationId());
        movement.setRequestedUserId(user.getId());
        movement.setStatus(Movement.Status.UNDER_APPROVAL);

        Integer movementId = movementRepository.save(movement).getId();

        Coordination coordination = new Coordination();
        CoordinationKey ck = new CoordinationKey(movementId);

        if (user.getLocationId() != item.get().getLocation().getResponsibleUserId()) {
            ck.setChiefUserId(item.get().getLocation().getResponsibleUserId());
        } else {
            ck.setChiefUserId(item.get().getLocation().getParentId());
        }

        coordination.setId(ck);
        coordination.setComment(movement.getComment());

        coordinationRepository.save(coordination);

        return "redirect:/movements";
    }

}
