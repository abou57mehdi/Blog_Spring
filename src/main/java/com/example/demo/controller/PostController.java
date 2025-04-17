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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "post/create";
    }

    @PostMapping("/create")
    public String createPost(@Valid @ModelAttribute("post") Post post,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "post/create";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        postService.createPost(post, currentUser);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("comment", new Comment());
        return "post/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/posts/" + id + "?error=unauthorized";
        }

        model.addAttribute("post", post);
        return "post/edit";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id,
                           @Valid @ModelAttribute("post") Post post,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "post/edit";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        try {
            postService.updatePost(post, id, currentUser);
            return "redirect:/posts/" + id;
        } catch (RuntimeException e) {
            return "redirect:/posts/" + id + "?error=unauthorized";
        }
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id,
                           @RequestParam("content") String content) {
        Post post = postService.getPostById(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        commentService.addComment(content, post, currentUser);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/like")
    public String likePost(@PathVariable Long id) {
        Post post = postService.getPostById(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        postService.toggleLike(post, currentUser);
        return "redirect:/posts/" + id;
    }
}