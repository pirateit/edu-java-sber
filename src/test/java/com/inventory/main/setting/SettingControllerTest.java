package com.inventory.main.setting;

import com.inventory.main.category.Category;
import com.inventory.main.category.CategoryService;
import com.inventory.main.location.Location;
import com.inventory.main.location.LocationService;
import com.inventory.main.user.User;
import com.inventory.main.user.UserService;
import org.junit.jupiter.api.*;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/import.sql"})
public class SettingControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void getSettings() throws Exception {
    this.mvc.perform(get("/settings"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void getSettingsByUser() throws Exception {
    this.mvc.perform(get("/settings"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getSettingsByAdmin() throws Exception {
    this.mvc.perform(get("/settings"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  void getUsers() throws Exception {
    this.mvc.perform(get("/settings/users"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void getUsersByUser() throws Exception {
    this.mvc.perform(get("/settings/users"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getUsersByAdmin() throws Exception {
    this.mvc.perform(get("/settings/users"))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/users/index"));
  }

  @Test
  void getDeletedUsers() throws Exception {
    this.mvc.perform(get("/settings/users/deleted"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void getDeletedUsersByUser() throws Exception {
    this.mvc.perform(get("/settings/users/deleted"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getDeletedUsersByAdmin() throws Exception {
    this.mvc.perform(get("/settings/users/deleted"))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/users/deleted"));
  }

  @Test
  void getUserCreate() throws Exception {
    this.mvc.perform(get("/settings/users/create"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void getUserCreateByUser() throws Exception {
    this.mvc.perform(get("/settings/users/create"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getUserCreateByAdmin() throws Exception {
    this.mvc.perform(get("/settings/users/create"))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/users/create"));
  }

  @Test
  void createUser() throws Exception {
    this.mvc.perform(post("/settings/users"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void createUserByUser() throws Exception {
    this.mvc.perform(post("/settings/users"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void createUserWithoutDataByAdmin() throws Exception {
    this.mvc.perform(post("/settings/users"))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/users/create"));
  }

  @Test
  @WithUserDetails("admin@example.com")
  void createUserWithDataByAdmin() throws Exception {
    this.mvc.perform(post("/settings/users")
        .param("lastName", "Lastname")
        .param("firstName", "Firstname")
        .param("email", "test@mail.ru")
        .param("phone", "79157775577")
        .param("password", "123")
        .param("locationId", "2")
        .param("isActive", "true"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  void updateUser() throws Exception {
    this.mvc.perform(post("/settings/users/{id}", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void updateUserByUser() throws Exception {
    this.mvc.perform(post("/settings/users/{id}", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void updateUserWithoutRequiredFieldByAdmin() throws Exception {
    this.mvc.perform(post("/settings/users/{id}", 1)
        .param("lastName", "NewLastname")
        .param("firstName", "NewFirstname")
        .param("phone", "79156666666")
        .param("password", "123")
        .param("locationId", "1")
        .param("isActive", "false")
        .param("role", User.Role.ADMIN.name()))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/users/update"));
  }

  @Test
  @WithUserDetails("admin@example.com")
  void updateUserByAdmin() throws Exception {
    this.mvc.perform(post("/settings/users/{id}", 1)
        .param("lastName", "NewLastname")
        .param("firstName", "NewFirstname")
        .param("email", "test@mail.ru")
        .param("phone", "79156666666")
        .param("password", "123")
        .param("locationId", "1")
        .param("isActive", "false")
        .param("role", User.Role.ADMIN.name()))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  void getUser() throws Exception {
    this.mvc.perform(get("/settings/users/{id}", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void getUserByUser() throws Exception {
    this.mvc.perform(get("/settings/users/{id}", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getUserByAdmin() throws Exception {
    this.mvc.perform(get("/settings/users/{id}", 1))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(view().name("settings/users/update"));
  }

  @Test
  void deleteUser() throws Exception {
    this.mvc.perform(get("/settings/users/{id}/delete", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void deleteUserByUser() throws Exception {
    this.mvc.perform(get("/settings/users/{id}/delete", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void deleteUserByAdmin() throws Exception {
    this.mvc.perform(get("/settings/users/{id}/delete", 1))
      .andExpect(status().is2xxSuccessful());
  }

  @Test
  void restoreUser() throws Exception {
    this.mvc.perform(get("/settings/users/{id}/restore", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void restoreUserByUser() throws Exception {
    this.mvc.perform(get("/settings/users/{id}/restore", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void restoreUserByAdmin() throws Exception {
    this.mvc.perform(get("/settings/users/{id}/restore", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  void getLocations() throws Exception {
    this.mvc.perform(get("/settings/locations"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void getLocationsByUser() throws Exception {
    this.mvc.perform(get("/settings/locations"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getLocationsByAdmin() throws Exception {
    this.mvc.perform(get("/settings/locations"))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/locations/index"));
  }

  @Test
  void getLocationCreate() throws Exception {
    this.mvc.perform(get("/settings/locations/create"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void getLocationCreateByUser() throws Exception {
    this.mvc.perform(get("/settings/locations/create"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getLocationCreateByAdmin() throws Exception {
    this.mvc.perform(get("/settings/locations/create"))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/locations/create"));
  }

  @Test
  void createLocation() throws Exception {
    this.mvc.perform(post("/settings/locations/create"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void createLocationByUser() throws Exception {
    this.mvc.perform(post("/settings/locations/create"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void createLocationWithoutDataByAdmin() throws Exception {
    this.mvc.perform(post("/settings/locations/create"))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/locations/create"));
  }

  @Test
  @WithUserDetails("admin@example.com")
  void createLocationWithDataByAdmin() throws Exception {
    this.mvc.perform(post("/settings/locations/create")
        .param("title", "Test Location"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  void getUpdateLocationPage() throws Exception {
    this.mvc.perform(get("/settings/locations/{id}", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void getUpdateLocationPageByUser() throws Exception {
    this.mvc.perform(get("/settings/locations/{id}", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getUpdateLocationPageByAdmin() throws Exception {
    this.mvc.perform(get("/settings/locations/{id}", 1))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/locations/update"));
  }

  @Test
  void updateLocation() throws Exception {
    this.mvc.perform(post("/settings/locations/{id}", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void updateLocationByUser() throws Exception {
    this.mvc.perform(post("/settings/locations/{id}", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void updateLocationWithoutDataByAdmin() throws Exception {
    this.mvc.perform(post("/settings/locations/{id}", 1))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/locations/update"));
  }

  @Test
  @WithUserDetails("admin@example.com")
  void updateLocationWithDataByAdmin() throws Exception {
    this.mvc.perform(post("/settings/locations/{id}", 1)
        .param("title", "New Test Location"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  void deleteLocation() throws Exception {
    this.mvc.perform(get("/settings/locations/{id}/delete", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void deleteLocationByUser() throws Exception {
    this.mvc.perform(get("/settings/locations/{id}/delete", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void deleteLocationWithNestedByAdmin() throws Exception {
    this.mvc.perform(get("/settings/locations/{id}/delete", 1))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/locations/update"));
  }

  @Test
  @WithUserDetails("admin@example.com")
  void deleteLocationWithoutNestedByAdmin() throws Exception {
    this.mvc.perform(get("/settings/locations/{id}/delete", 15))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  void restoreLocation() throws Exception {
    this.mvc.perform(get("/settings/locations/{id}/restore", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void restoreLocationByUser() throws Exception {
    this.mvc.perform(get("/settings/locations/{id}/restore", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void restoreLocationByAdmin() throws Exception {
    this.mvc.perform(get("/settings/locations/{id}/restore", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  void getCategoriesPage() throws Exception {
    this.mvc.perform(get("/settings/categories"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void getCategoriesPageByUser() throws Exception {
    this.mvc.perform(get("/settings/categories"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getCategoriesPageByAdmin() throws Exception {
    this.mvc.perform(get("/settings/categories"))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/categories/index"));
  }

  @Test
  void getCreateCategoryPagePage() throws Exception {
    this.mvc.perform(get("/settings/categories/create"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void getCreateCategoryPageByUser() throws Exception {
    this.mvc.perform(get("/settings/categories/create"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getCreateCategoryPageByAdmin() throws Exception {
    this.mvc.perform(get("/settings/categories/create"))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/categories/create"));
  }

  @Test
  void createCategory() throws Exception {
    this.mvc.perform(post("/settings/categories/create"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void createCategoryByUser() throws Exception {
    this.mvc.perform(post("/settings/categories/create"))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void createCategoryWithoutDataByAdmin() throws Exception {
    this.mvc.perform(post("/settings/categories/create"))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/categories/create"));
  }

  @Test
  @WithUserDetails("admin@example.com")
  void createCategoryWithDataByAdmin() throws Exception {
    this.mvc.perform(post("/settings/categories/create")
        .param("title", "Test Category"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  void getUpdateCategoryPage() throws Exception {
    this.mvc.perform(get("/settings/categories/{id}", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void getUpdateCategoryPageByUser() throws Exception {
    this.mvc.perform(get("/settings/categories/{id}", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void getUpdateCategoryPageByAdmin() throws Exception {
    this.mvc.perform(get("/settings/categories/{id}", 1))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/categories/update"));
  }

  @Test
  void updateCategory() throws Exception {
    this.mvc.perform(post("/settings/categories/{id}", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void updateCategoryByUser() throws Exception {
    this.mvc.perform(post("/settings/categories/{id}", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void updateCategoryWithoutDataByAdmin() throws Exception {
    this.mvc.perform(post("/settings/categories/{id}", 1))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/categories/update"));
  }

  @Test
  @WithUserDetails("admin@example.com")
  void updateCategoryWithDataByAdmin() throws Exception {
    this.mvc.perform(post("/settings/categories/{id}", 1)
        .param("title", "New Test Category"))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  void deleteCategory() throws Exception {
    this.mvc.perform(get("/settings/categories/{id}/delete", 1))
      .andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser
  void deleteCategoryByUser() throws Exception {
    this.mvc.perform(get("/settings/categories/{id}/delete", 1))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails("admin@example.com")
  void deleteCategoryWithNestedByAdmin() throws Exception {
    this.mvc.perform(get("/settings/categories/{id}/delete", 1))
      .andExpect(status().isOk())
      .andExpect(view().name("settings/categories/update"));
  }

  @Test
  @WithUserDetails("admin@example.com")
  void deleteCategoryWithoutNestedByAdmin() throws Exception {
    this.mvc.perform(get("/settings/categories/{id}/delete", 5))
      .andExpect(status().is3xxRedirection());
  }

}
