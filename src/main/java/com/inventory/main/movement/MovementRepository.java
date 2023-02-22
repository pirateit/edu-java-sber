package com.inventory.main.movement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MovementRepository extends CrudRepository<Movement, Integer> {

  @Query(value = "SELECT * FROM movements " +
    "LEFT OUTER JOIN items ON items.id = movements.item_id " +
    "WHERE (COALESCE(:userLocationsIds) IS NULL OR (location_from_id IN (:userLocationsIds) OR location_to_id IN (:userLocationsIds))) " +
    "AND (COALESCE(:locationsFrom) IS NULL OR location_from_id IN (:locationsFrom)) " +
    "AND (COALESCE(:locationsTo) IS NULL OR location_to_id IN (:locationsTo)) " +
    "AND (:type IS NULL OR type = :type) " +
    "AND (:itemTitle IS NULL OR items.title ILIKE CONCAT('%', :itemTitle, '%')) " +
    "AND (:requestedUserId IS NULL OR requested_user_id = :requestedUserId) " +
    "AND (COALESCE(:statuses) IS NULL OR status IN (:statuses)) " +
    "ORDER BY movements.id DESC",
    countQuery = "SELECT COUNT(*) FROM movements " +
      "LEFT OUTER JOIN items ON items.id = movements.item_id " +
      "WHERE (COALESCE(:userLocationsIds) IS NULL OR (location_from_id IN (:userLocationsIds) OR location_to_id IN (:userLocationsIds))) " +
      "AND (COALESCE(:locationsFrom) IS NULL OR location_from_id IN (:locationsFrom)) " +
      "AND (COALESCE(:locationsTo) IS NULL OR location_to_id IN (:locationsTo)) " +
      "AND (:type IS NULL OR type = :type) " +
      "AND (:itemTitle IS NULL OR items.title ILIKE CONCAT('%', :itemTitle, '%')) " +
      "AND (:requestedUserId IS NULL OR requested_user_id = :requestedUserId) " +
      "AND (COALESCE(:statuses) IS NULL OR status IN (:statuses))", nativeQuery = true)
  Page<Movement> findAllWithFilters(
    @Param("userLocationsIds") Set<Integer> userLocationsIds,
    @Param("type") String type,
    @Param("itemTitle") String itemTitle,
    @Param("locationsFrom") Set<Integer> locationsFrom,
    @Param("locationsTo") Set<Integer> locationsTo,
    @Param("requestedUserId") Integer requestedUserId,
    @Param("statuses") List<String> statuses,
    Pageable pageable
  );

  @Query(value = "SELECT * FROM movements " +
    "LEFT OUTER JOIN (SELECT * FROM coordinations ORDER BY created_at) cds ON cds.movement_id = movements.id " +
    "LEFT OUTER JOIN locations ON locations.id = movements.location_to_id " +
    "WHERE ((movements.requested_user_id = :userId AND cds.status = 'COORDINATED') " +
    "OR (cds.chief_user_id = :userId AND cds.status = 'WAITING') " +
    "OR (locations.responsible_user_id = :userId AND cds.status = 'SENT')) " +
    "AND movements.status IN ('UNDER_APPROVAL', 'APPROVED', 'SENT') " +
    "ORDER BY movements.id DESC",
    countQuery = "SELECT * FROM movements " +
      "LEFT OUTER JOIN (SELECT * FROM coordinations ORDER BY created_at) cds ON cds.movement_id = movements.id " +
      "LEFT OUTER JOIN locations ON locations.id = movements.location_to_id " +
      "WHERE ((movements.requested_user_id = :userId AND cds.status = 'COORDINATED') " +
      "OR (cds.chief_user_id = :userId AND cds.status = 'WAITING') " +
      "OR (locations.responsible_user_id = :userId AND cds.status = 'SENT')) " +
      "AND movements.status IN ('UNDER_APPROVAL', 'APPROVED', 'SENT')", nativeQuery = true)
  Page<Movement> findAllUserWaiting(@Param("userId") Integer userId, Pageable pageable);

  Optional<Movement> findOneByItemIdAndStatusIn(int itemId, List<Movement.Status> statusList);

  Set<Movement> findAllByItemId(int ItemId);

  @Query(value = "SELECT COUNT(DISTINCT movements.id) FROM movements " +
    "LEFT OUTER JOIN (SELECT * FROM coordinations ORDER BY created_at) cds ON cds.movement_id = movements.id " +
    "LEFT OUTER JOIN locations ON locations.id = movements.location_to_id " +
    "WHERE ((movements.requested_user_id = :userId AND cds.status = 'COORDINATED') " +
    "OR (cds.chief_user_id = :userId AND cds.status = 'WAITING') " +
    "OR (locations.responsible_user_id = :userId AND cds.status = 'SENT')) " +
    "AND movements.status IN ('UNDER_APPROVAL', 'APPROVED', 'SENT')", nativeQuery = true)
  int countAllUserWaiting(@Param("userId") int userId);

}
