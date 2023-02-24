package com.inventory.main.location;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LocationRepository extends CrudRepository<Location, Integer> {

  Set<Location> findAllByDeletedAtIsNull();

  Set<Location> findAllByDeletedAtIsNotNull();

  Set<Location> findAllByParentIdIsNullAndDeletedAtIsNullOrderByIdAsc();

  Set<Location> findByResponsibleUserId(int id);

  @Query(value = "WITH RECURSIVE rectree AS (" +
    "SELECT * " +
    "FROM locations " +
    "WHERE id = :id " +
    "UNION " +
    "SELECT t.* " +
    "FROM locations t " +
    "JOIN rectree " +
    "ON t.id = rectree.parent_id" +
    ") SELECT * FROM rectree WHERE deleted_at IS NULL;", nativeQuery = true)
  Iterable<Location> findParentsById(@Param("id") Integer id);

  @Query(value = "" +
    "WITH RECURSIVE rectree AS (" +
    "   SELECT * FROM locations WHERE id = :id " +
    "   UNION ALL " +
    "   SELECT t.* " +
    "   FROM locations t " +
    "   JOIN rectree ON t.parent_id = rectree.id) " +
    "SELECT * FROM rectree WHERE deleted_at IS NULL;", nativeQuery = true)
  Set<Location> findChildrenById(@Param("id") Integer id);

  @Query(value = "WITH RECURSIVE rectree AS (" +
    "SELECT * " +
    "FROM locations " +
    "WHERE responsible_user_id = :id " +
    "UNION ALL " +
    "SELECT t.* " +
    "FROM locations t " +
    "JOIN rectree " +
    "ON t.parent_id = rectree.id" +
    ") SELECT * FROM rectree WHERE deleted_at IS NULL;", nativeQuery = true)
  Set<Location> findChildrenByResponsibleUserId(@Param("id") Integer id);

  @Modifying
  @Query(value = "UPDATE locations SET deleted_at = NOW() WHERE id = :id ;", nativeQuery = true)
  void deleteById(@Param("id") int id);

  @Modifying
  @Query(value = "UPDATE locations SET deleted_at = NULL WHERE id = :id ;", nativeQuery = true)
  void restoreById(@Param("id") int id);

}
