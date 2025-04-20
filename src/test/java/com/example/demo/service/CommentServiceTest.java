package com.example.demo.service;

import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

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
    void getCommentsByPostId() {
        when(commentRepository.findByPostId(1L)).thenReturn(Arrays.asList(testComment));

        List<Comment> comments = commentService.getCommentsByPostId(1L);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("Test Comment", comments.get(0).getContent());
    }

    @Test
    void addComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        Comment comment = commentService.addComment("New Comment", testPost, testUser);

        assertNotNull(comment);
        assertEquals("Test Comment", comment.getContent());
        assertEquals(testUser, comment.getUser());
        assertEquals(testPost, comment.getPost());
    }

    @Test
    void getCommentById_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        Comment comment = commentService.getCommentById(1L);

        assertNotNull(comment);
        assertEquals(1L, comment.getId());
    }

    @Test
    void getCommentById_NotFound() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            commentService.getCommentById(99L);
        });
    }

    @Test
    void deleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        commentService.deleteComment(1L);

        verify(commentRepository).delete(testComment);
    }
}