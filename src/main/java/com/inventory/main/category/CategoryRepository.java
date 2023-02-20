package com.inventory.main.category;

import com.inventory.main.location.Location;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {

  Optional<Category> findByTitle(String title);

  Set<Category> findAllByDepthOrderByIdAsc(int depth);

  @Modifying
  @Query(value = "WITH RECURSIVE rectree AS (" +
    "SELECT * " +
    "FROM categories " +
    "WHERE id = :id " +
    "UNION ALL " +
    "SELECT t.* " +
    "FROM categories t " +
    "JOIN rectree " +
    "ON t.parent_id = rectree.id" +
    ") UPDATE categories set depth = depth - :oldDepth + :newDepth WHERE id IN (SELECT id FROM rectree);", nativeQuery = true)
  void updateTree(@Param("id") Integer id, @Param("oldDepth") int oldDepth, @Param("newDepth") int newDepth);

  boolean existsByParentId(int parentId);

}
