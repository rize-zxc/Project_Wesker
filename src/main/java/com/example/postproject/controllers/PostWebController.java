package com.example.postproject.controllers;

import com.example.postproject.models.Post;
import com.example.postproject.models.User;
import com.example.postproject.services.PostService;
import com.example.postproject.services.StatusService;
import com.example.postproject.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PostWebController {
    private static final Logger logger = LoggerFactory.getLogger(PostWebController.class);
    private final PostService postService;
    private final UserService userService;
    private final StatusService statusService;

    public PostWebController(PostService postService, UserService userService, StatusService statusService) {
        this.postService = postService;
        this.userService = userService;
        this.statusService = statusService;
    }

    @GetMapping("/postsbyuser")
    public String getPostsByUser(@RequestParam(name = "username", required = false) String username, Model model) {
        logger.info("Processing request for posts by user: {}", username);
        if (!statusService.isServerAvailable()) {
            logger.warn("Сервис временно недоступен. Пожалуйста, попробуйте позже.");
            model.addAttribute("message", "Сервис временно недоступен. Пожалуйста, попробуйте позже.");
            model.addAttribute("success", false);
            return "postsbyuser";
        }

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        logger.debug("Loaded {} users for dropdown", users.size());

        if (username != null && !username.isEmpty()) {
            try {
                logger.info("Fetching posts for username: {}", username);
                List<?> posts = postService.getPostsByUsername(username);
                model.addAttribute("posts", posts);
                model.addAttribute("selectedUsername", username);
                logger.info("Successfully fetched {} posts for user: {}", posts.size(), username);
            } catch (Exception e) {
                logger.error("Error fetching posts for username {}: {}", username, e.getMessage(), e);
                model.addAttribute("message", "Ошибка при загрузке постов: " + e.getMessage());
                model.addAttribute("success", false);
            }
        } else {
            logger.debug("No username provided, showing user selection form");
        }

        return "postsbyuser";
    }

    @PostMapping("/postsbyuser/create")
    public String createPost(
            @RequestParam String username,
            @RequestParam String title,
            @RequestParam String text,
            Model model) {
        logger.info("Creating post for user: {}", username);
        if (!statusService.isServerAvailable()) {
            logger.warn("Server is unavailable");
            model.addAttribute("message", "Сервис временно недоступен. Пожалуйста, попробуйте позже.");
            model.addAttribute("success", false);
            return reloadPosts(username, model);
        }

        try {
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + username));
            Post post = new Post();
            post.setTitle(title.trim());
            post.setText(text.trim());
            postService.createPost(post, user);
            model.addAttribute("message", "Пост успешно создан");
            model.addAttribute("success", true);
        } catch (Exception e) {
            logger.error("Error creating post for user {}: {}", username, e.getMessage(), e);
            model.addAttribute("message", "Ошибка при создании поста: " + e.getMessage());
            model.addAttribute("success", false);
        }

        return reloadPosts(username, model);
    }

    @PostMapping("/postsbyuser/update")
    public String updatePost(
            @RequestParam String username,
            @RequestParam Long postId,
            @RequestParam String title,
            @RequestParam String text,
            Model model) {
        logger.info("Updating post ID {} for user: {}", postId, username);
        if (!statusService.isServerAvailable()) {
            logger.warn("Server is unavailable");
            model.addAttribute("message", "Сервис временно недоступен. Пожалуйста, попробуйте позже.");
            model.addAttribute("success", false);
            return reloadPosts(username, model);
        }

        try {
            Post postDetails = new Post();
            postDetails.setTitle(title.trim());
            postDetails.setText(text.trim());
            postService.updatePost(postId, postDetails);
            model.addAttribute("message", "Пост успешно обновлен");
            model.addAttribute("success", true);
        } catch (Exception e) {
            logger.error("Error updating post ID {} for user {}: {}", postId, username, e.getMessage(), e);
            model.addAttribute("message", "Ошибка при обновлении поста: " + e.getMessage());
            model.addAttribute("success", false);
        }

        return reloadPosts(username, model);
    }

    @PostMapping("/postsbyuser/delete")
    public String deletePost(
            @RequestParam String username,
            @RequestParam Long postId,
            Model model) {
        logger.info("Deleting post ID {} for user: {}", postId, username);
        if (!statusService.isServerAvailable()) {
            logger.warn("Server is unavailable");
            model.addAttribute("message", "Сервис временно недоступен. Пожалуйста, попробуйте позже.");
            model.addAttribute("success", false);
            return reloadPosts(username, model);
        }

        try {
            postService.deletePost(postId);
            model.addAttribute("message", "Пост успешно удален");
            model.addAttribute("success", true);
        } catch (Exception e) {
            logger.error("Error deleting post ID {} for user {}: {}", postId, username, e.getMessage(), e);
            model.addAttribute("message", "Ошибка при удалении поста: " + e.getMessage());
            model.addAttribute("success", false);
        }

        return reloadPosts(username, model);
    }

    private String reloadPosts(String username, Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        if (username != null && !username.isEmpty()) {
            try {
                List<?> posts = postService.getPostsByUsername(username);
                model.addAttribute("posts", posts);
                model.addAttribute("selectedUsername", username);
            } catch (Exception e) {
                logger.error("Error reloading posts for user {}: {}", username, e.getMessage(), e);
                model.addAttribute("message", "Ошибка при загрузке постов: " + e.getMessage());
                model.addAttribute("success", false);
            }
        }
        return "postsbyuser";
    }
}

