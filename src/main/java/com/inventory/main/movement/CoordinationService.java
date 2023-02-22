package com.inventory.main.movement;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class CoordinationService {

  private final CoordinationRepository coordinationRepository;

  CoordinationService(CoordinationRepository coordinationRepository) {
    this.coordinationRepository = coordinationRepository;
  }

  Optional<Coordination> getLastByMovementId(int movementId) {
    return coordinationRepository.findTopByMovementIdOrderByCreatedAtDesc(movementId);
  }

}
