//package com.example.postproject.controllers;
//
//import com.example.postproject.models.User;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Controller
//@RequestMapping("/postsbyuser")
//public class PostWebController {
//    private final RestTemplate restTemplate;
//    private final String BASE_URL = "http://localhost:8080";
//
//    public PostWebController(RestTemplateBuilder builder) {
//        this.restTemplate = builder.build();
//    }
//
//    @GetMapping
//    public String showPostsPage(@RequestParam(required = false) String username, Model model) {
//        try {
//            // Получаем список пользователей для выпадающего списка
//            ResponseEntity<User[]> response = restTemplate.getForEntity(BASE_URL + "/users", User[].class);
//            List<User> users = Arrays.asList(response.getBody());
//            model.addAttribute("users", users);
//
//            if (username != null && !username.isEmpty()) {
//                model.addAttribute("selectedUsername", username);
//            }
//        } catch (Exception e) {
//            model.addAttribute("message", "Ошибка при загрузке данных: " + e.getMessage());
//            model.addAttribute("success", false);
//        }
//        return "postsbyuser";
//    }
//}