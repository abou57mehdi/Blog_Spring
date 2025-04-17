package com.example.demo.controller;

import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.service.CommentService;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id, Authentication authentication) {
        Comment comment = commentService.getCommentById(id);
        User currentUser = userService.findByUsername(authentication.getName());

        // Vérifier que l'utilisateur est l'auteur du commentaire
        if (comment.getUser().getId().equals(currentUser.getId())) {
            Long postId = comment.getPost().getId();
            commentService.deleteComment(id);
            return "redirect:/posts/" + postId;
        }

        return "redirect:/";
    }
}