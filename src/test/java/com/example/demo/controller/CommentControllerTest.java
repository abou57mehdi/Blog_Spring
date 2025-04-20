package com.example.demo.controller;

import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.service.CommentService;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    private Comment testComment;
    private Post testPost;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setContent("Test Comment");
        testComment.setUser(testUser);
        testComment.setPost(testPost);
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldDeleteCommentWhenUserIsAuthor() throws Exception {
        when(commentService.getCommentById(1L)).thenReturn(testComment);
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(post("/comments/1/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));
    }

    @Test
    @WithMockUser(username = "otheruser")
    void shouldRedirectWhenUserIsNotAuthor() throws Exception {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otheruser");

        when(commentService.getCommentById(1L)).thenReturn(testComment);
        when(userService.findByUsername("otheruser")).thenReturn(otherUser);

        mockMvc.perform(post("/comments/1/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}