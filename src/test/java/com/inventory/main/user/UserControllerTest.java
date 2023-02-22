package com.inventory.main.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/import.sql"})
public class UserControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void getProfilePage() throws Exception {
    this.mvc.perform(get("/profile"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithUserDetails("afanasiy18011985@mail.ru")
  void getProfilePageWithUserDetails() throws Exception {
    this.mvc.perform(get("/profile"))
      .andExpect(status().isOk())
      .andExpect(view().name("users/profile"));
  }

  @Test
  @WithUserDetails("afanasiy18011985@mail.ru")
  void updateUserProfileWithUserDetails() throws Exception {
    this.mvc.perform(post("/profile")
        .param("lastName", "Lastname")
        .param("firstName", "Firstname")
        .param("email", "test@mail.ru")
        .param("phone", "79157775577")
        .param("password", "1"))
      .andExpect(status().isOk())
      .andExpect(view().name("users/profile"))
      .andExpect(content().string(containsString("Lastname")))
      .andExpect(content().string(containsString("Firstname")))
      .andExpect(content().string(containsString("test@mail.ru")))
      .andExpect(content().string(containsString("79157775577")));
  }

}
