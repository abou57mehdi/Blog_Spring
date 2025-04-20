package com.example.demo.controller;

        import jakarta.servlet.RequestDispatcher;
        import org.junit.jupiter.api.Test;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
        import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
        import org.springframework.test.web.servlet.MockMvc;
        import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

        @WebMvcTest(CustomErrorController.class)
        @AutoConfigureMockMvc(addFilters = false)
        class CustomErrorControllerTest {

            @Autowired
            private MockMvc mockMvc;

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