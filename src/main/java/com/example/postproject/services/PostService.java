package com.example.postproject.services;

import com.example.postproject.cache.SimpleCache;
import com.example.postproject.exceptions.BadRequestException;
import com.example.postproject.exceptions.InternalServerErrorException;
import com.example.postproject.models.Post;
import com.example.postproject.models.User;
import com.example.postproject.repository.PostRepository;
import com.example.postproject.services.RequestCounter;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



/**class of PostService.*/
@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final SimpleCache cache;
    private final RequestCounter requestCounter;

    /**cache.*/
    public PostService(PostRepository postRepository, SimpleCache cache, RequestCounter requestCounter) {
        this.postRepository = postRepository;
        this.cache = cache;
        this.requestCounter = requestCounter;
    }

    private String getPostCacheKey(Long id) {
        return "post_" + id;
    }

    private String getUserPostsCacheKey(String username) {
        return "user_posts_" + username;
    }

    /**class for createPost.*/
    public Post createPost(Post post, User user) {
        int requestNumber = requestCounter.increment();
        try {
            if (post == null) {
                throw new BadRequestException("Post data cannot be null");
            }
            if (user == null) {
                throw new BadRequestException("User cannot be null");
            }
            if (post.getTitle() == null || post.getTitle().isEmpty()) {
                throw new BadRequestException("Post title cannot be empty");
            }
            if (post.getText() == null || post.getText().isEmpty()) {
                throw new BadRequestException("Post text cannot be empty");
            }

            post.setUser(user);
            Post createdPost = postRepository.save(post);
            cache.remove(getUserPostsCacheKey(user.getUsername()));
            logger.info("Post created successfully: ID={}, User={}", createdPost.getId(), user.getUsername());
            return createdPost;
        } catch (BadRequestException e) {
            logger.warn("Validation error in createPost: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to create post: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to create post");
        }
    }

    /**getAllPosts.*/
    public List<Post> getAllPosts() {
        int requestNumber = requestCounter.increment();
        try {
            List<Post> posts = postRepository.findAll();
            logger.info("Retrieved {} posts from database", posts.size());
            return posts;
        } catch (Exception e) {
            logger.error("Failed to fetch posts: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to fetch posts");
        }
    }

    /**get post by ID.*/
    public Optional<Post> getPostById(Long id) {
        int requestNumber = requestCounter.increment();
        try {
            if (id == null || id <= 0) {
                throw new BadRequestException("Invalid post ID");
            }

            String cacheKey = getPostCacheKey(id);
            Optional<Object> cachedPost = cache.get(cacheKey);

            if (cachedPost.isPresent()) {
                logger.debug("Retrieved post from cache: ID={}", id);
                return Optional.of((Post) cachedPost.get());
            }

            Optional<Post> post = postRepository.findById(id);
            if (post.isPresent()) {
                cache.put(cacheKey, post.get());
                logger.info("Retrieved post from database: ID={}", id);
            } else {
                logger.warn("Post not found: ID={}", id);
            }
            return post;
        } catch (BadRequestException e) {
            logger.warn("Invalid request in getPostById: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to fetch post by ID: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to fetch post");
        }
    }

    /**update post.*/
    public Post updatePost(Long id, Post postDetails) {
        int requestNumber = requestCounter.increment();
        try {
            if (id == null || id <= 0) {
                throw new BadRequestException("Invalid post ID");
            }
            if (postDetails == null) {
                throw new BadRequestException("Post details cannot be null");
            }

            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new BadRequestException("Post not found with ID: " + id));

            if (postDetails.getTitle() != null) {
                post.setTitle(postDetails.getTitle());
            }

            if (postDetails.getText() != null) {
                post.setText(postDetails.getText());
            }

            Post updatedPost = postRepository.save(post);
            cache.put(getPostCacheKey(id), updatedPost);
            if (post.getUser() != null) {
                cache.remove(getUserPostsCacheKey(post.getUser().getUsername()));
            }
            logger.info("Post updated successfully: ID={}", id);
            return updatedPost;
        } catch (BadRequestException e) {
            logger.warn("Validation error in updatePost: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to update post: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to update post");
        }
    }

    /**delete post method.*/
    public void deletePost(Long id) {
        int requestNumber = requestCounter.increment();
        try {
            if (id == null || id <= 0) {
                throw new BadRequestException("Invalid post ID");
            }

            Optional<Post> post = postRepository.findById(id);
            if (post.isEmpty()) {
                throw new BadRequestException("Post not found with ID: " + id);
            }

            postRepository.deleteById(id);
            cache.remove(getPostCacheKey(id));
            post.ifPresent(p -> {
                if (p.getUser() != null) {
                    cache.remove(getUserPostsCacheKey(p.getUser().getUsername()));
                }
            });
            logger.info("Post deleted successfully: ID={}", id);
        } catch (BadRequestException e) {
            logger.warn("Validation error in deletePost: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to delete post: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to delete post");
        }
    }

    /**bulk-create posts.*/
    public List<Post> bulkCreatePosts(List<Post> posts, User user) {
        int requestNumber = requestCounter.increment();
        try {
            if (posts == null || posts.isEmpty()) {
                throw new BadRequestException("Posts list cannot be null or empty");
            }
            if (user == null) {
                throw new BadRequestException("User cannot be null");
            }

            // Validate all posts before processing
            posts.forEach(post -> {
                if (post.getTitle() == null || post.getTitle().isEmpty()) {
                    throw new BadRequestException("Post title cannot be empty");
                }
                if (post.getText() == null || post.getText().isEmpty()) {
                    throw new BadRequestException("Post text cannot be empty");
                }
                post.setUser(user);
            });

            List<Post> createdPosts = posts.stream()
                    .map(postRepository::save)
                    .toList();

            cache.remove(getUserPostsCacheKey(user.getUsername()));
            logger.info("Bulk created {} posts for user: {}", createdPosts.size(), user.getUsername());
            return createdPosts;
        } catch (BadRequestException e) {
            logger.warn("Validation error in bulkCreatePosts: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to bulk create posts: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to bulk create posts");
        }
    }

    /**delete post method.*/
    public List<Post> getPostsByUsername(String username) {
        int requestNumber = requestCounter.increment();
        try {
            if (username == null || username.isEmpty()) {
                throw new BadRequestException("Username cannot be empty");
            }

            String cacheKey = getUserPostsCacheKey(username);
            Optional<Object> cachedPosts = cache.get(cacheKey);

            if (cachedPosts.isPresent()) {
                logger.debug("Retrieved posts from cache for user: {}", username);
                return (List<Post>) cachedPosts.get();
            }

            List<Post> posts = postRepository.findPostsByUsername(username);
            cache.put(cacheKey, posts);
            logger.info("Retrieved {} posts for user: {}", posts.size(), username);
            return posts;
        } catch (BadRequestException e) {
            logger.warn("Invalid request in getPostsByUsername: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to fetch posts by username: {}", e.getMessage(), e);
            throw new InternalServerErrorException("Failed to fetch posts");
        }
    }
}
