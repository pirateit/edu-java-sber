package com.inventory.main.item;

import com.inventory.main.movement.Movement;
import com.inventory.main.movement.MovementRepository;
import com.inventory.main.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ItemService {

  private final ItemRepository itemRepository;
  private final MovementRepository movementRepository;

  private int pageSize = 10;

  public ItemService(ItemRepository itemRepository, MovementRepository movementRepository) {
    this.itemRepository = itemRepository;
    this.movementRepository = movementRepository;
  }

  public Page<Item> getAllActive(Integer page, Integer size, Set<Integer> userLocationsIds, String title, Set<Integer> locations, Set<Integer> categories) {
    Pageable pageable = PageRequest.of(page != null ? page - 1 : 0, size != null ? size : pageSize, Sort.by("id"));

    if (userLocationsIds == null) userLocationsIds = new HashSet<>();
    if (locations == null) locations = new HashSet<>();
    if (categories == null) categories = new HashSet<>();

    return itemRepository.findAllActiveWithFilters(userLocationsIds, title, locations, categories, pageable);
  }

  public Page<Item> getAllInactive(Integer page, Integer size, String title, Set<Integer> locations, Set<Integer> categories) {
    Pageable pageable = PageRequest.of(page != null ? page - 1 : 0, size != null ? size : pageSize, Sort.by("id"));
    if (locations == null) locations = new HashSet<>();
    if (categories == null) categories = new HashSet<>();

    return itemRepository.findAllInactiveWithFilters(title, locations, categories, pageable);
  }

  public Optional<Item> getById(int id) {
    return itemRepository.findById(id);
  }

  public Optional<Item> getByInventoryNumber(String prefix, Long number) {
    return itemRepository.findByPrefixAndNumber(prefix, number);
  }

  public Optional<Item> getLastInCategory(int categoryId) {
    return itemRepository.findTopByCategoryIdOrderByIdDesc(categoryId);
  }

  @Transactional
  public Item create(Item item, User user) {
    item = itemRepository.save(item);

    Movement movement = new Movement(Movement.Type.MOVEMENT, item.getId(), item.getQuantity(), item.getLocationId(), user.getId(), Movement.Status.SUCCESS);

    movementRepository.save(movement);

    return item;
  }

  public Item update(Item item) {
    return itemRepository.save(item);
  }

}
