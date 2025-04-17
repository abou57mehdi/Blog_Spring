package com.example.demo.service;

import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post non trouvé"));
    }

    public Post createPost(Post post, User author) {
        post.setAuthor(author);
        return postRepository.save(post);
    }

    public Post updatePost(Post post, Long id, User currentUser) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post non trouvé"));

        // Vérifier que l'utilisateur est l'auteur
        if (!existingPost.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce post");
        }

        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());

        return postRepository.save(existingPost);
    }

    public void toggleLike(Post post, User user) {
        if (post.getLikedBy().contains(user)) {
            post.getLikedBy().remove(user);
        } else {
            post.getLikedBy().add(user);
        }
        postRepository.save(post);
    }
    // Et d'ajouter dans la classe PostService
    public void deletePost(Long id) {
        Post post = getPostById(id);
        postRepository.delete(post);
    }
}