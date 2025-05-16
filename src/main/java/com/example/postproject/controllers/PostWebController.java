package com.example.postproject.controllers;

import com.example.postproject.models.Post;
import com.example.postproject.models.User;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

/**MVC-controller.*/
@Controller
@RequestMapping("/postsbyuser")
public class PostWebController {
    private static final Logger logger = LoggerFactory.getLogger(PostWebController.class);
    private final RestTemplate restTemplate;


    public PostWebController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    private final String BASE_URL = "http://localhost:8080";

    /**GET.*/
    @GetMapping
    public String getPostsByUser(@RequestParam(required = false) String username, Model model) {
        try {
            ResponseEntity<User[]> userResponse = restTemplate.getForEntity(BASE_URL + "/users", User[].class);
            List<User> users = Arrays.asList(userResponse.getBody());
            model.addAttribute("users", users);

            if (username != null && !username.isEmpty()) {
                ResponseEntity<Post[]> postResponse = restTemplate.getForEntity(BASE_URL + "/posts/byuser/" + username, Post[].class);
                List<Post> posts = Arrays.asList(postResponse.getBody());
                model.addAttribute("posts", posts);
                model.addAttribute("selectedUsername", username);
            }

        } catch (Exception e) {
            logger.error("Ошибка при загрузке данных: {}", e.getMessage(), e);
            model.addAttribute("message", "Ошибка при загрузке данных: " + e.getMessage());
            model.addAttribute("success", false);
        }
        return "postsbyuser";
    }

    /**POST.*/
    @PostMapping("/create")
    public String createPost(@RequestParam String username,
                             @RequestParam String title,
                             @RequestParam String text,
                             Model model) {
        try {
            
            ResponseEntity<User[]> response = restTemplate.getForEntity(BASE_URL + "/users", User[].class);
            Long userId = Arrays.stream(response.getBody())
                    .filter(u -> u.getUsername().equals(username))
                    .map(User::getId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

            Post post = new Post();
            post.setTitle(title);
            post.setText(text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Post> entity = new HttpEntity<>(post, headers);

            restTemplate.postForEntity(BASE_URL + "/posts/create?userId=" + userId, entity, Post.class);
            model.addAttribute("message", "Пост успешно создан");
            model.addAttribute("success", true);
        } catch (Exception e) {
            logger.error("Ошибка при создании поста: {}", e.getMessage(), e);
            model.addAttribute("message", "Ошибка при создании поста: " + e.getMessage());
            model.addAttribute("success", false);
        }
        return getPostsByUser(username, model);
    }

    /**POST-update.*/
    @PostMapping("/update")
    public String updatePost(@RequestParam String username,
                             @RequestParam Long postId,
                             @RequestParam String title,
                             @RequestParam String text,
                             Model model) {
        try {

            Post post = new Post();
            post.setTitle(title);
            post.setText(text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Post> entity = new HttpEntity<>(post, headers);

            restTemplate.exchange(BASE_URL + "/posts/" + postId, HttpMethod.PUT, entity, Void.class);
            model.addAttribute("message", "Пост успешно обновлён");
            model.addAttribute("success", true);
        } catch (Exception e) {
            logger.error("Ошибка при обновлении поста: {}", e.getMessage(), e);
            model.addAttribute("message", "Ошибка при обновлении поста: " + e.getMessage());
            model.addAttribute("success", false);
        }
        return getPostsByUser(username, model);
    }

    /**POST-delete.*/
    @PostMapping("/delete")
    public String deletePost(@RequestParam String username,
                             @RequestParam Long postId,
                             Model model) {
        try {
            restTemplate.delete(BASE_URL + "/posts/" + postId);
            model.addAttribute("message", "Пост успешно удалён");
            model.addAttribute("success", true);
        } catch (Exception e) {
            logger.error("Ошибка при удалении поста: {}", e.getMessage(), e);
            model.addAttribute("message", "Ошибка при удалении поста: " + e.getMessage());
            model.addAttribute("success", false);
        }
        return getPostsByUser(username, model);
    }
}
