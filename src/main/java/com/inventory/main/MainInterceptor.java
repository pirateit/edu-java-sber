package com.inventory.main;

import com.inventory.main.movement.MovementService;
import com.inventory.main.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MainInterceptor implements HandlerInterceptor {

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,  ModelAndView modelAndView) throws Exception {
//    User user = (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());

//    modelAndView.addObject("userName", user.getLastName());
//    modelAndView.getModelMap().addAttribute("movementsWaiting", movementService.getUserWaitingCount(user.getId()));
  }

}
