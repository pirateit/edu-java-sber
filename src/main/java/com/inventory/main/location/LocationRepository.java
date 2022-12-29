package com.inventory.main.location;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends CrudRepository<Location, Integer> {

    @Override
    @Query(value = "SELECT * FROM locations WHERE deleted_at IS NULL ORDER BY parent_id ASC NULLS FIRST, title ASC;", nativeQuery = true)
    Iterable<Location> findAll();

//    @Query("WITH RECURSIVE rectree AS (" +
//            "SELECT * " +
//            "FROM locations " +
//            "WHERE id = 1 " +
//            "UNION ALL " +
//            "SELECT t.* " +
//            "FROM locations t " +
//            "JOIN rectree " +
//            "ON t.parent_id = rectree.id" +
//            ") SELECT * FROM rectree WHERE deleted_at IS NULL ORDER BY depth ASC NULLS FIRST, title ASC;")
//    Iterable<Location> findChildrenById(@Param("id") Integer id);
//
//    @Query("WITH RECURSIVE rectree AS (" +
//            "SELECT * " +
//            "FROM locations " +
//            "WHERE id = :id " +
//            "UNION " +
//            "SELECT t.* " +
//            "FROM locations t " +
//            "JOIN rectree " +
//            "ON t.id = rectree.parent_id" +
//            ") SELECT * FROM rectree WHERE deleted_at IS NULL ORDER BY depth ASC NULLS FIRST;")
//    Iterable<Location> findParentsById(@Param("id") Integer id);

}