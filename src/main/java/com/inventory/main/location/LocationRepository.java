package com.inventory.main.location;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface LocationRepository extends CrudRepository<Location, Integer> {

    Iterable<Location> findAllByDeletedAtIsNull();

    Iterable<Location> findAllByDeletedAtIsNotNullOrderByTitleAsc();

    Set<Location> findByParentIdAndDeletedAtIsNullOrderByTitleAsc(Integer id);

    @Override
    @Query(value = "SELECT * FROM locations WHERE deleted_at IS NULL ORDER BY id ASC NULLS FIRST, title ASC;", nativeQuery = true)
    Iterable<Location> findAll();

    Set<Location> findByResponsibleUserId(Integer id);

    @Override
    @Modifying
    @Query(value = "UPDATE locations SET deleted_at = now() WHERE id = :id ;", nativeQuery = true)
    void deleteById(@Param("id") Integer id);

    @Modifying
    @Query(value = "UPDATE locations SET deleted_at = NULL WHERE id = :id ;", nativeQuery = true)
    void restoreById(@Param("id") int id);

    @Query(value = "WITH RECURSIVE rectree AS (" +
            "SELECT * " +
            "FROM locations " +
            "WHERE id = :id " +
            "UNION ALL " +
            "SELECT t.* " +
            "FROM locations t " +
            "JOIN rectree " +
            "ON t.parent_id = rectree.id" +
            ") SELECT * FROM rectree WHERE deleted_at IS NULL ORDER BY depth ASC NULLS FIRST, title ASC;", nativeQuery = true)
    Set<Location> findChildrenById(@Param("id") Integer id);

    @Query(value = "WITH RECURSIVE rectree AS (" +
            "SELECT * " +
            "FROM locations " +
            "WHERE id = :id " +
            "UNION " +
            "SELECT t.* " +
            "FROM locations t " +
            "JOIN rectree " +
            "ON t.id = rectree.parent_id" +
            ") SELECT * FROM rectree WHERE deleted_at IS NULL ORDER BY depth ASC NULLS FIRST;", nativeQuery = true)
    Iterable<Location> findParentsById(@Param("id") Integer id);

}