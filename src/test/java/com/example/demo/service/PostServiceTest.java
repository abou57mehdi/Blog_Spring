package com.example.demo.service;

        import com.example.demo.model.Post;
        import com.example.demo.model.User;
        import com.example.demo.repository.PostRepository;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;
        import org.junit.jupiter.api.extension.ExtendWith;
        import org.mockito.InjectMocks;
        import org.mockito.Mock;
        import org.mockito.junit.jupiter.MockitoExtension;

        import java.util.*;

        import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
        import static org.mockito.Mockito.*;

        @ExtendWith(MockitoExtension.class)
        class PostServiceTest {

            @Mock
            private PostRepository postRepository;

            @InjectMocks
            private PostService postService;

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
                testPost.setLikedBy(new HashSet<>());
            }

            @Test
            void getAllPosts() {
                when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(testPost));

                List<Post> posts = postService.getAllPosts();

                assertNotNull(posts);
                assertEquals(1, posts.size());
                assertEquals("Test Title", posts.get(0).getTitle());
            }

            @Test
            void getPostById_Success() {
                when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

                Post post = postService.getPostById(1L);

                assertNotNull(post);
                assertEquals(1L, post.getId());
            }

            @Test
            void getPostById_NotFound() {
                when(postRepository.findById(99L)).thenReturn(Optional.empty());

                assertThrows(RuntimeException.class, () -> {
                    postService.getPostById(99L);
                });
            }

            @Test
            void createPost() {
                when(postRepository.save(any(Post.class))).thenReturn(testPost);

                Post newPost = new Post();
                newPost.setTitle("New Title");
                newPost.setContent("New Content");

                Post created = postService.createPost(newPost, testUser);

                assertNotNull(created);
                assertEquals(testUser, created.getAuthor());
            }

            @Test
            void updatePost_Success() {
                when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
                when(postRepository.save(any(Post.class))).thenReturn(testPost);

                Post updatedPost = new Post();
                updatedPost.setTitle("Updated Title");
                updatedPost.setContent("Updated Content");

                Post result = postService.updatePost(updatedPost, 1L, testUser);

                assertNotNull(result);
                verify(postRepository).save(any(Post.class));
            }

            @Test
            void updatePost_NotAuthor() {
                when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

                User otherUser = new User();
                otherUser.setId(2L);

                Post updatedPost = new Post();
                updatedPost.setTitle("Updated Title");
                updatedPost.setContent("Updated Content");

                assertThrows(RuntimeException.class, () -> {
                    postService.updatePost(updatedPost, 1L, otherUser);
                });
            }

            @Test
            void toggleLike_AddLike() {
                when(postRepository.save(any(Post.class))).thenReturn(testPost);

                User liker = new User();
                liker.setId(2L);

                postService.toggleLike(testPost, liker);

                assertTrue(testPost.getLikedBy().contains(liker));
            }

            @Test
            void toggleLike_RemoveLike() {
                User liker = new User();
                liker.setId(2L);
                testPost.getLikedBy().add(liker);

                when(postRepository.save(any(Post.class))).thenReturn(testPost);

                postService.toggleLike(testPost, liker);

                assertFalse(testPost.getLikedBy().contains(liker));
            }

            @Test
            void deletePost() {
                when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

                postService.deletePost(1L);

                verify(postRepository).delete(testPost);
            }
        }