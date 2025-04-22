package com.example.demo.controller;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.example.demo.config.ThymeleafUtils;
import com.example.demo.config.MockTemplateConfig;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomErrorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MockTemplateConfig.class)
class CustomErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ThymeleafUtils thymeleafUtils;
    
    @BeforeEach
    void setUp() {
        // Mock ThymeleafUtils behavior
        when(thymeleafUtils.fragmentExists(anyString())).thenReturn(false);
    }

    @Test
    void shouldHandleNotFoundError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorMessage", "Page non trouvée"));
    }

    @Test
    void shouldHandleServerError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorMessage", "Erreur interne du serveur"));
    }

    @Test
    void shouldHandleGenericError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorMessage", "Une erreur s'est produite"));
    }
}