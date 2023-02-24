package com.inventory.main.item;

import com.inventory.main.movement.Movement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
@Sql({"/tests.sql"})
public class ItemControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void getActiveItemsPage() throws Exception {
    this.mvc.perform(get("/items"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithUserDetails("afanasiy18011985@mail.ru")
  void getActiveItemsPageByUser() throws Exception {
    this.mvc.perform(get("/items"))
      .andExpect(status().isOk())
      .andExpect(view().name("items/index"));
  }

  @Test
  @WithUserDetails("afanasiy18011985@mail.ru")
  void getActiveItemsPageWithParameters() throws Exception {
    this.mvc.perform(get("/items")
        .queryParam("page", "2"))
      .andExpect(status().isOk())
      .andExpect(view().name("items/index"));
  }

  @Test
  void getInactiveItemsPage() throws Exception {
    this.mvc.perform(get("/items"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  void getCreateItemPage() throws Exception {
    this.mvc.perform(get("/items/create"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithUserDetails("veronika03081978@rambler.ru")
  void getCreateItemPageByUser() throws Exception {
    this.mvc.perform(get("/items/create"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getCreateItemPageByAdmin() throws Exception {
    this.mvc.perform(get("/items/create"))
      .andExpect(status().isOk())
      .andExpect(view().name("items/create"));
  }

  @Test
  void createItem() throws Exception {
    this.mvc.perform(post("/items/create"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithUserDetails("veronika03081978@rambler.ru")
  void createItemByUser() throws Exception {
    this.mvc.perform(post("/items/create"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("veronika03081978@rambler.ru")
  void createItemWithDataByUser() throws Exception {
    this.mvc.perform(post("/items/create")
        .param("number", "999")
        .param("title", "Item title"))
      .andExpect(status().isForbidden());
  }

  @Test
  void getUpdateItemPage() throws Exception {
    this.mvc.perform(get("/items/{id}", 8))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithUserDetails("katerina1969@ya.ru")
  void getUpdateItemPageByOther() throws Exception {
    this.mvc.perform(get("/items/{id}", 8))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("daniil6777@mail.ru")
  void getUpdateItemPageByOwner() throws Exception {
    this.mvc.perform(get("/items/{id}", 8))
      .andExpect(status().isOk())
      .andExpect(view().name("items/update"));
  }

  @Test
  void updateItem() throws Exception {
    this.mvc.perform(post("/items/{id}", 8))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithUserDetails("katerina1969@ya.ru")
  void updateItemByOther() throws Exception {
    this.mvc.perform(post("/items/{id}", 8))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("daniil6777@mail.ru")
  void updateItemByOwner() throws Exception {
    this.mvc.perform(post("/items/{id}", 8)
        .param("title", "New title")
        .param("number", "99"))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/items/8"));
  }

  @Test
  void getMoveItemPage() throws Exception {
    this.mvc.perform(get("/items/{id}/move", 8))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithUserDetails("daniil6777@mail.ru")
  void getMoveItemPageByOwner() throws Exception {
    this.mvc.perform(get("/items/{id}/move", 8))
      .andExpect(status().isOk())
      .andExpect(view().name("items/move"));
  }

  @Test
  @WithUserDetails("katerina1969@ya.ru")
  void getMoveItemPageByOther() throws Exception {
    this.mvc.perform(get("/items/{id}/move", 8))
      .andExpect(status().isForbidden());
  }

  @Test
  void moveItem() throws Exception {
    this.mvc.perform(post("/items/{id}/move", 8))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithUserDetails("katerina1969@ya.ru")
  void moveItemOther() throws Exception {
    this.mvc.perform(post("/items/{id}/move", 8))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("daniil6777@mail.ru")
  void moveItemWithoutDataByOwner() throws Exception {
    this.mvc.perform(post("/items/{id}/move", 8))
      .andExpect(status().is2xxSuccessful());
  }

  @Test
  @WithUserDetails("daniil6777@mail.ru")
  void moveItemWithDataByOwner() throws Exception {
    this.mvc.perform(post("/items/{id}/move", 8)
        .param("type", Movement.Type.MOVEMENT.name())
        .param("locationToId", "15"))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/movements"));
  }

}
