package com.inventory.main.location;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocationRepository extends CrudRepository<Location, Integer> {

    Iterable<Location> findAll();

    Optional<Location> findById(Integer id);

    Location save(Location location);

    @Query("WITH RECURSIVE rectree AS (" +
            "SELECT * " +
            "FROM locations " +
            "WHERE id = :id " +
            "UNION ALL " +
            "SELECT t.* " +
            "FROM locations t " +
            "JOIN rectree " +
            "ON t.parent_id = rectree.id" +
            ") SELECT * FROM rectree WHERE deleted_at IS NULL ORDER BY parent_id ASC NULLS FIRST, title ASC;")
    Iterable<Location> findChildrenById(@Param("id") Integer id);

    @Query("WITH RECURSIVE rectree AS (" +
            "SELECT * " +
            "FROM locations " +
            "WHERE id = :id " +
            "UNION " +
            "SELECT t.* " +
            "FROM locations t " +
            "JOIN rectree " +
            "ON t.id = rectree.parent_id" +
            ") SELECT * FROM rectree WHERE deleted_at IS NULL ORDER BY parent_id ASC NULLS FIRST;")
    Iterable<Location> findParentsById(@Param("id") Integer id);

}
