package com.inventory.main.error;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

  @RequestMapping("/error")
  public String getErrorPage(HttpServletRequest request, Model model) {
    String errorMsg = "";
    int httpErrorCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

    switch (httpErrorCode) {
      case 400 -> errorMsg = "Сервер обнаружил ошибку в запросе клиента";
      case 403 -> errorMsg = "Нет доступа к указанному ресурсу";
      case 404 -> errorMsg = "Указанный ресурс не найден";
      case 500 -> errorMsg = "Внутрення ошибка сервера";
      default -> errorMsg = "Неизвестная ошибка";
    }

    model.addAttribute("errorMessage", errorMsg);

    return "error";
  }

}
