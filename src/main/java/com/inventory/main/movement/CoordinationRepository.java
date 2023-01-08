package com.inventory.main.movement;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoordinationRepository extends CrudRepository<Coordination, CoordinationKey> {

    Iterable<Coordination> findAllByMovementIdOrderByIdAsc(Integer movementId);

}