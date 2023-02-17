package com.inventory.main.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {

  Optional<Item> findByPrefixAndNumber(String prefix, Long number);

  @Query(value = "SELECT * FROM items " +
    "WHERE (COALESCE(:userLocationsIds) IS NULL OR location_id IN (:userLocationsIds)) " +
    "AND (:title IS NULL OR title ILIKE CONCAT('%', :title, '%')) " +
    "AND (COALESCE(:locations) IS NULL OR location_id IN (:locations)) " +
    "AND (COALESCE(:categories) IS NULL OR category_id IN (:categories)) " +
    "AND deleted_at IS NULL ", nativeQuery = true)
  Page<Item> findAllActiveWithFilters(
    @Param("userLocationsIds") Set<Integer> userLocationsIds,
    @Param("title") String title,
    @Param("locations") Set<Integer> locations,
    @Param("categories") Set<Integer> categories,
    Pageable pageable);

  @Query(value = "SELECT * FROM items " +
    "WHERE (:title IS NULL OR title ILIKE CONCAT('%', :title, '%')) " +
    "AND (COALESCE(:locations) IS NULL OR location_id IN (:locations)) " +
    "AND (COALESCE(:categories) IS NULL OR category_id IN (:categories)) " +
    "AND deleted_at IS NOT NULL ", nativeQuery = true)
  Page<Item> findAllInactiveWithFilters(
    @Param("title") String title,
    @Param("locations") Set<Integer> locations,
    @Param("categories") Set<Integer> categories,
    Pageable pageable);

  Optional<Item> findTopByCategoryIdOrderByIdDesc(int categoryId);

  @Override
  @Modifying
  @Query(value = "UPDATE items SET deleted_at = NOW() WHERE id = :id ;", nativeQuery = true)
  void deleteById(@Param("id") Integer id);

  boolean existsByCategoryId(int locationId);

}
