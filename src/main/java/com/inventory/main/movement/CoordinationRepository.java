package com.inventory.main.movement;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface CoordinationRepository extends CrudRepository<Coordination, CoordinationKey> {

  Optional<Coordination> findTopByMovementIdOrderByCreatedAtDesc(int movementId);

}
