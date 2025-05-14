package com.example.postproject.services;

import com.example.postproject.cache.SimpleCache;
import com.example.postproject.models.Post;
import com.example.postproject.models.User;
import com.example.postproject.repository.PostRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private SimpleCache cache;

    @Mock
    private Post mockPost;

    @Mock
    private User mockUser;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getUsername()).thenReturn("testuser");

        when(mockPost.getId()).thenReturn(1L);
        when(mockPost.getTitle()).thenReturn("Test Title");
        when(mockPost.getText()).thenReturn("Test Content");
        when(mockPost.getUser()).thenReturn(mockUser);
    }

    @Test
    void createPost() {
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        Post result = postService.createPost(mockPost, mockUser);

        assertNotNull(result);
        assertEquals(mockPost.getId(), result.getId());
        verify(postRepository).save(mockPost);
        verify(cache).remove("user_posts_testuser");
    }

    @Test
    void bulkCreatePosts() {
        List<Post> posts = Arrays.asList(mockPost);
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        List<Post> result = postService.bulkCreatePosts(posts, mockUser);

        assertEquals(1, result.size());
        verify(postRepository).save(mockPost);
        verify(cache).remove("user_posts_testuser");
    }

    @Test
    void getPostById() {
        when(cache.get("post_1")).thenReturn(Optional.empty());
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        Optional<Post> result = postService.getPostById(1L);

        assertTrue(result.isPresent());
        assertEquals(mockPost.getId(), result.get().getId());
        verify(cache).put("post_1", mockPost);
    }

    @Test
    void getPostsByUsername() {
        List<Post> cachedPosts = Arrays.asList(mockPost);
        when(cache.get("user_posts_testuser")).thenReturn(Optional.of(cachedPosts));

        List<Post> result = postService.getPostsByUsername("testuser");

        assertEquals(1, result.size());
        verify(cache).get("user_posts_testuser");
        verify(postRepository, never()).findPostsByUsername(anyString());
    }

    @Test
    void updatePost() {
        Post updatedData = mock(Post.class);
        when(updatedData.getTitle()).thenReturn("Updated Title");
        when(updatedData.getText()).thenReturn("Updated Content");

        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        Post result = postService.updatePost(1L, updatedData);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Content", result.getText());
        verify(cache).put("post_1", mockPost);
        verify(cache).remove("user_posts_testuser");
    }

    @Test
    void deletePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        postService.deletePost(1L);

        verify(postRepository).deleteById(1L);
        verify(cache).remove("post_1");
        verify(cache).remove("user_posts_testuser");
    }
}
