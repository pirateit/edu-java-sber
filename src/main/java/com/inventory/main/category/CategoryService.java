package com.inventory.main.category;

import com.inventory.main.item.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final ItemRepository itemRepository;

  public CategoryService(CategoryRepository categoryRepository, ItemRepository itemRepository) {
    this.categoryRepository = categoryRepository;
    this.itemRepository = itemRepository;
  }

  public Iterable<Category> getAll() {
    return categoryRepository.findAll();
  }

  public Set<Category> getParents() {
    return categoryRepository.findAllByDepthOrderByIdAsc(1);
  }

  public Optional<Category> getById(int categoryId) {
    return categoryRepository.findById(categoryId);
  }

  public Category create(Category category) {
    if (category.getParentId() != null) {
      Integer parentDepth = categoryRepository.findById(category.getParentId()).get().getDepth();

      if (parentDepth == null) {
        category.setDepth(1);
      } else {
        category.setDepth(parentDepth + 1);
      }
    }

    category.setTitle(category.getTitle().trim());

    return categoryRepository.save(category);
  }

  @Transactional
  public Category update(Category category, Category newCategory) {
    category.setTitle(newCategory.getTitle());
    category.setParentId(newCategory.getParentId());

    if (newCategory.getParentId() != null) {
      Integer parentDepth = category.getParent().getDepth();

      categoryRepository.updateTree(category.getId(), category.getDepth(), parentDepth + 1);
    } else {
      categoryRepository.updateTree(category.getId(), category.getDepth(), 1);
    }

    return categoryRepository.save(category);
  }

  public void delete(Category category) throws Exception {
    List<String> exceptions = new ArrayList<>();

    if (categoryRepository.existsByParentId(category.getId())) {
      exceptions.add("Категория имеет подкатегории");
    }

    if (itemRepository.existsByCategoryId(category.getId())) {
      exceptions.add("Категория имеет предметы");
    }

    if (!exceptions.isEmpty()) {
      throw new Exception(String.join(", ", exceptions));
    }

    categoryRepository.delete(category);
  }

}
