package com.inventory.main;

import com.inventory.main.movement.MovementService;
import com.inventory.main.user.User;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;

public abstract class MainController {

  private final MovementService movementService;

  public MainController(MovementService movementService) {
    this.movementService = movementService;
  }

  @ModelAttribute("movementsWaiting")
  public int movementsWaitingCount(@AuthenticationPrincipal User user) {
    return movementService.getUserWaitingCount(user.getId());
  }

  @InitBinder
  void initBinder(final WebDataBinder binder) {
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
  }

}
