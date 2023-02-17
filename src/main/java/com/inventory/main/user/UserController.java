package com.inventory.main.user;

import com.inventory.main.MainController;
import com.inventory.main.movement.MovementService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class UserController extends MainController {

  private final UserService userService;
  public UserController(MovementService movementService, UserService userService) {
    super(movementService);
    this.userService = userService;
  }

  @GetMapping("/profile")
  public String getProfilePage(Model model, @AuthenticationPrincipal User user) {
    model.addAttribute("user", user);
    model.addAttribute("title", "Мой профиль");

    return "users/profile";
  }

  @PostMapping("/profile")
  public String updateUserProfile(
    @Valid User newUserData,
    Errors errors,
    Model model,
    @AuthenticationPrincipal User user) {
    if (errors.hasErrors()) {
      model.addAttribute("user", newUserData);
      model.addAttribute("title", "Мой профиль");

      return "users/profile";
    }

    userService.updateProfile(user, newUserData);

    return "redirect:/profile";
  }

}
