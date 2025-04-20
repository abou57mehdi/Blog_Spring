package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)  // Désactive les filtres de sécurité
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
    }

    @Test
    void shouldShowLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void shouldShowLogoutPage() throws Exception {
        mockMvc.perform(get("/logout-page"))
                .andExpect(status().isOk())
                .andExpect(view().name("logout"));
    }

    @Test
    void shouldShowRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        // Correction : utilisez when().thenReturn() au lieu de doNothing()
        when(userService.registerNewUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "newuser")
                .param("email", "new@example.com")
                .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success"));
    }

    @Test
    void shouldShowErrorOnRegistrationFailure() throws Exception {
        when(userService.registerNewUser(any(User.class)))
            .thenThrow(new RuntimeException("Username already exists"));

        mockMvc.perform(post("/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "existinguser")
                .param("email", "existing@example.com")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(view().name("register"));
    }
}