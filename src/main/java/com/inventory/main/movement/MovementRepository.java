package com.inventory.main.movement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MovementRepository extends CrudRepository<Movement, Integer> {

    Page<Movement> findAllByRequestedUserIdOrderByCreatedAtDesc(Integer id, Pageable pageable);

//                "ORDER BY movements.created_at DESC " +
//                        "LIMIT :limit " +
//                        "OFFSET :page * :limit ;
    @Query(value = "SELECT * " +
            "FROM movements " +
            "LEFT JOIN coordinations " +
            "ON movements.id = coordinations.movement_id " +
            "WHERE coordinations.chief_user_id = :chiefUserId " +
            "ORDER BY movements.created_at DESC ", nativeQuery = true)
    Page<Movement> findByCoordinationsChiefUserId(@Param("chiefUserId") Integer chiefUserId, Pageable pageable);

    @Query(value = "SELECT COUNT(id) " +
            "FROM movements " +
            "LEFT JOIN coordinations " +
            "ON movements.id = coordinations.movement_id " +
            "WHERE coordinations.chief_user_id = :chiefUserId ;", nativeQuery = true)
    Long countByCoordinationsChiefUserId(@Param("chiefUserId") Integer chiefUserId);

    Movement findByItemIdOrderByCreatedAtAsc(Integer itemId);

}