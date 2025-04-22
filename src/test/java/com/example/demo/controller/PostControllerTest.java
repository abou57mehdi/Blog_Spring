package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.service.CommentService;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;
import com.example.demo.config.ThymeleafUtils;
import com.example.demo.config.MockTemplateConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@Import(MockTemplateConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    @MockBean
    private CommentService commentService;
    
    @MockBean
    private ThymeleafUtils thymeleafUtils;

    private Post testPost;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Title");
        testPost.setContent("Test Content");
        testPost.setAuthor(testUser);
        testPost.setCreatedAt(LocalDateTime.now());
        
        // Mock ThymeleafUtils behavior
        when(thymeleafUtils.fragmentExists(anyString())).thenReturn(false);
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldShowCreateForm() throws Exception {
        mockMvc.perform(get("/posts/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/create"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldCreatePost() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(postService.createPost(any(Post.class), any(User.class))).thenReturn(testPost);

        mockMvc.perform(post("/posts/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Test Title")
                .param("content", "Test Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldViewPost() throws Exception {
        when(postService.getPostById(1L)).thenReturn(testPost);

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/view"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("comment"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldShowEditForm() throws Exception {
        when(postService.getPostById(1L)).thenReturn(testPost);
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/posts/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/edit"))
                .andExpect(model().attributeExists("post"));
    }
}