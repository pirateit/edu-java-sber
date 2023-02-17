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

  Coordination create(int movementId, int chiefUserId) {
    Coordination coordination = new Coordination();

    coordination.setMovementId(movementId);
    coordination.setChiefUserId(chiefUserId);
    coordination.setStatus(Coordination.Status.WAITING);

    return coordinationRepository.save(coordination);
  }

  Coordination create(int movementId, int chiefUserId, Coordination.Status status) {
    Coordination coordination = new Coordination();

    coordination.setMovementId(movementId);
    coordination.setChiefUserId(chiefUserId);
    coordination.setStatus(status);

    return coordinationRepository.save(coordination);
  }

  Coordination create(int movementId, int chiefUserId, Coordination.Status status, String comment) {
    Coordination coordination = new Coordination();

    coordination.setMovementId(movementId);
    coordination.setChiefUserId(chiefUserId);
    coordination.setStatus(status);

    if (!comment.isBlank()) {
      coordination.setComment(comment);
    }

    return coordinationRepository.save(coordination);
  }

}
