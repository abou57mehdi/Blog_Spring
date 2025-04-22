package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.service.PostService;
import com.example.demo.config.ThymeleafUtils;
import com.example.demo.config.MockTemplateConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MockTemplateConfig.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;
    
    @MockBean
    private ThymeleafUtils thymeleafUtils;

    private Post testPost;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setContent("Test Content");
        testPost.setCreatedAt(LocalDateTime.now());
        
        // Mock ThymeleafUtils behavior
        when(thymeleafUtils.fragmentExists(anyString())).thenReturn(false);
    }

    @Test
    void shouldShowHomePage() throws Exception {
        List<Post> posts = Arrays.asList(testPost);
        when(postService.getAllPosts()).thenReturn(posts);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts"));
    }
}