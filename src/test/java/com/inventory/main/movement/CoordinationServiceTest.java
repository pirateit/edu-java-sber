package com.inventory.main.movement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CoordinationServiceTest {

  @Resource
  private CoordinationService coordinationService;

  @Test
  @DisplayName("Получение последнего согласования по ID перемещения")
  void getLastByMovementId() {
    Coordination coordination = coordinationService.getLastByMovementId(26).orElse(null);

    assertNotNull(coordination);
  }

}
