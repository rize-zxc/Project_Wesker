package com.example.postproject.controllers;

import com.example.postproject.models.Post;
import com.example.postproject.models.User;
import com.example.postproject.services.PostService;
import com.example.postproject.services.StatusService;
import com.example.postproject.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**post controller.*/
@Tag(name = "Post Controller", description = "API для управления постами")
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final StatusService statusService;

    @SuppressWarnings({"checkstyle:MissingJavadocMethod"})
    public PostController(PostService postService, UserService userService,
                          StatusService statusService) {
        this.postService = postService;
        this.userService = userService;
        this.statusService = statusService;
    }

    /**create post.*/
    @Operation(summary = "Создать пост", description = "Создает новый пост для указанного пользователя")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Пост успешно создан",
                    content = @Content(schema = @Schema(implementation = Post.class))),
      @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
      @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @Parameter(description = "Данные поста", required = true)
            @RequestBody Post post,
            @Parameter(description = "ID пользователя", required = true)
            @RequestParam Long userId) {
        if (!statusService.isServerAvailable()) {
            return ResponseEntity.status(503).body("Сервис временно недоступен. Пожалуйста, попробуйте позже.");
        }
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return ResponseEntity.ok(postService.createPost(post, user));
    }

    /**get posts.*/
    @Operation(summary = "Получить все посты", description = "Возвращает список всех постов")
    @ApiResponse(responseCode = "200", description = "Список постов",
            content = @Content(schema = @Schema(implementation = Post.class)))
    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        if (!statusService.isServerAvailable()) {
            return ResponseEntity.status(503).body("Сервис временно недоступен. Пожалуйста, попробуйте позже.");
        }
        return ResponseEntity.ok(postService.getAllPosts());
    }

    /**get post by id.*/
    @Operation(summary = "Получить пост по ID", description = "Возвращает пост по указанному идентификатору")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Пост найден",
                    content = @Content(schema = @Schema(implementation = Post.class))),
      @ApiResponse(responseCode = "404", description = "Пост не найден"),
      @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(
            @Parameter(description = "ID поста", required = true)
            @PathVariable Long id) {
        if (!statusService.isServerAvailable()) {
            return ResponseEntity.status(503).body("Сервис временно недоступен. Пожалуйста, попробуйте позже.");
        }
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**update post.*/
    @Operation(summary = "Обновить пост", description = "Обновляет данные поста по указанному ID")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Пост успешно обновлен",
                    content = @Content(schema = @Schema(implementation = Post.class))),
      @ApiResponse(responseCode = "404", description = "Пост не найден"),
      @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @Parameter(description = "ID поста", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные поста", required = true)
            @RequestBody Post postDetails) {
        if (!statusService.isServerAvailable()) {
            return ResponseEntity.status(503).body("Сервис временно недоступен. Пожалуйста, попробуйте позже.");
        }
        return ResponseEntity.ok(postService.updatePost(id, postDetails));
    }

    /**delete post.*/
    @Operation(summary = "Удалить пост", description = "Удаляет пост по указанному ID")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Пост успешно удален"),
      @ApiResponse(responseCode = "404", description = "Пост не найден"),
      @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @Parameter(description = "ID поста", required = true)
            @PathVariable Long id) {
        if (!statusService.isServerAvailable()) {
            return ResponseEntity.status(503).body("Сервис временно недоступен. Пожалуйста, попробуйте позже.");
        }
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    /**Bulk create posts.*/
    @Operation(summary = "Создать несколько постов", description = "Создает несколько постов для указанного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Посты успешно созданы",
                    content = @Content(schema = @Schema(implementation = Post.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные постов"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @PostMapping("/bulk-create")
    public ResponseEntity<?> bulkCreatePosts(
            @Parameter(description = "Список постов", required = true)
            @RequestBody List<Post> posts,
            @Parameter(description = "ID пользователя", required = true)
            @RequestParam Long userId) {
        if (!statusService.isServerAvailable()) {
            return ResponseEntity.status(503).body("Сервис временно недоступен. Пожалуйста, попробуйте позже.");
        }
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return ResponseEntity.ok(postService.bulkCreatePosts(posts, user));
    }

    /**get post by username.*/
    @Operation(summary = "Получить посты пользователя", description = "Возвращает все посты указанного пользователя")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список постов пользователя",
                    content = @Content(schema = @Schema(implementation = Post.class))),
      @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @GetMapping("/by-user/{username}")
    public ResponseEntity<?> getPostsByUser(
            @Parameter(description = "Имя пользователя", required = true)
            @PathVariable String username) {
        if (!statusService.isServerAvailable()) {
            return ResponseEntity.status(503).body("Сервис временно недоступен");
        }
        List<Post> posts = postService.getPostsByUsername(username);
        return ResponseEntity.ok(posts);
    }
}