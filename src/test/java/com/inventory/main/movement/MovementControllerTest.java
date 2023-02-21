package com.inventory.main.movement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/import.sql"})
public class MovementControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void getMovementsPage() throws Exception {
    this.mvc.perform(get("/movements"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithUserDetails("afanasiy18011985@mail.ru")
  void getMovementsPageByUser() throws Exception {
    this.mvc.perform(get("/movements"))
      .andExpect(status().isOk())
      .andExpect(view().name("movements/index"));
  }

  @Test
  @WithUserDetails("afanasiy18011985@mail.ru")
  void getMovementsPageWithParameters() throws Exception {
    this.mvc.perform(get("/movements")
        .queryParam("page", "2")
        .queryParam("type", Movement.Type.MOVEMENT.name())
        .queryParam("itemTitle", "мышь")
        .queryParam("status", Movement.Status.SUCCESS.name()))
      .andExpect(status().isOk())
      .andExpect(view().name("movements/index"));
  }

  @Test
  void getWaitingMovementsPage() throws Exception {
    this.mvc.perform(get("/movements"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithUserDetails("afanasiy18011985@mail.ru")
  void getWaitingMovementsPageByUser() throws Exception {
    this.mvc.perform(get("/movements"))
      .andExpect(status().isOk())
      .andExpect(view().name("movements/index"));
  }

  @Test
  @WithUserDetails("veronika03081978@rambler.ru")
  void getMovementPage() throws Exception {
    this.mvc.perform(get("/movements/{id}", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("viktoriya5412@mail.ru")
  void getMovementPageByGuest() throws Exception {
    this.mvc.perform(get("/movements/{id}", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("daniil6777@mail.ru")
  void getMovementPageByOwner() throws Exception {
    this.mvc.perform(get("/movements/{id}", 1))
      .andExpect(status().isOk())
      .andExpect(view().name("movements/movement"));
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getMovementPageByAdmin() throws Exception {
    this.mvc.perform(get("/movements/{id}", 1))
      .andExpect(status().isOk())
      .andExpect(view().name("movements/movement"));
  }

  @Test
  @DisplayName("Согласование без данных")
  @WithUserDetails("afanasiy18011985@mail.ru")
  void createCoordination() throws Exception {
    this.mvc.perform(post("/movements/{id}/coordinations", 26))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/movements/26"));
  }

  @Test
  @DisplayName("Согласование от согласовывающего")
  @WithUserDetails("elizaveta2042@ya.ru")
  void createCoordinationByOwner() throws Exception {
    this.mvc.perform(post("/movements/{id}/coordinations", 27)
        .param("status", Coordination.Status.COORDINATED.name()))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/movements/27"));
  }

  @Test
  @DisplayName("Согласование от постороннего")
  @WithUserDetails("katerina1969@ya.ru")
  void createCoordinationByOther() throws Exception {
    this.mvc.perform(post("/movements/{id}/coordinations", 27)
        .param("status", Coordination.Status.COORDINATED.name()))
      .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Отправка от собственника")
  @WithUserDetails("nikita91@ya.ru")
  void createSendCoordinationByOwner() throws Exception {
    this.mvc.perform(post("/movements/{id}/coordinations", 28)
        .param("status", Coordination.Status.SENT.name()))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/movements/28"));
  }

  @Test
  @DisplayName("Отправка от постороннего")
  @WithUserDetails("katerina1969@ya.ru")
  void createSendCoordinationByOther() throws Exception {
    this.mvc.perform(post("/movements/{id}/coordinations", 28)
        .param("status", Coordination.Status.SENT.name()))
      .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Приём от постороннего")
  @WithUserDetails("katerina1969@ya.ru")
  void createAcceptCoordinationByOther() throws Exception {
    this.mvc.perform(post("/movements/{id}/coordinations", 26)
        .param("status", Coordination.Status.ACCEPTED.name()))
      .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Приём от собственника")
  @WithUserDetails("admin@example.com")
  void createAcceptCoordinationByOwner() throws Exception {
    this.mvc.perform(post("/movements/{id}/coordinations", 26)
        .param("status", Coordination.Status.ACCEPTED.name()))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/movements/26"));
  }

}
