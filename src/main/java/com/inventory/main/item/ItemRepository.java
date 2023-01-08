package com.inventory.main.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {

    @Query(value = "SELECT * FROM items " +
            "WHERE (:title IS NULL OR title ILIKE CONCAT('%', :title, '%')) " +
            "AND (COALESCE(:locations) IS NULL OR location_id IN (:locations)) " +
            "AND (COALESCE(:categories) IS NULL OR category_id IN (:categories)) " +
            "ORDER BY created_at DESC", nativeQuery = true)
    Page<Item> findAllWithFilters(
            @Param("title") String title,
            @Param("locations") List<Integer> locations,
            @Param("categories") List<Integer> categories,
            Pageable pageable);

    Page<Item> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    Page<Item> findByLocationIdInOrderByCreatedAtDesc(List<Integer> locationId, Pageable pageable);

    @Override
    @Modifying
    @Query(value = "UPDATE items SET deleted_at = now() WHERE id = :id ;", nativeQuery = true)
    void deleteById(@Param("id") Integer id);

}
