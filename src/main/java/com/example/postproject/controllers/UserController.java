package com.example.postproject.controllers;

import com.example.postproject.models.User;
import com.example.postproject.services.StatusService;
import com.example.postproject.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**user controller.*/
@Tag(name = "User Controller", description = "API для управления пользователями")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final StatusService statusService;

    @SuppressWarnings({"checkstyle:MissingJavadocMethod"})
    public UserController(UserService userService, StatusService statusService) {
        this.userService = userService;
        this.statusService = statusService;
    }

    /**create post.*/
    @Operation(summary = "Создать пользователя", description = "Создает нового пользователя в системе")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Пользователь успешно создан",
                    content = @Content(schema = @Schema(implementation = User.class))),
      @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @PostMapping("/create")
    public ResponseEntity<?> createUser(
            @Parameter(description = "Данные пользователя", required = true)
            @RequestBody User user) {
        if (!statusService.isServerAvailable()) {
            return ResponseEntity.status(503).body("Сервис временно недоступен. Пожалуйста, попробуйте позже.");
        }
        return ResponseEntity.ok(userService.createUser(user));
    }

    /**get users.*/
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей",
            content = @Content(schema = @Schema(implementation = User.class)))
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        if (!statusService.isServerAvailable()) {
            return ResponseEntity.status(503).body("Сервис временно недоступен. Пожалуйста, попробуйте позже.");
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**get user by id.*/
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по указанному идентификатору")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(schema = @Schema(implementation = User.class))),
      @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
      @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id) {
        if (!statusService.isServerAvailable()) {
            return ResponseEntity.status(503).body("Сервис временно недоступен. Пожалуйста, попробуйте позже.");
        }
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**update user.*/
    @Operation(summary = "Обновить пользователя", description = "Обновляет данные пользователя по указанному ID")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен",
                    content = @Content(schema = @Schema(implementation = User.class))),
      @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
      @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные пользователя", required = true)
            @RequestBody User userDetails) {
        if (!statusService.isServerAvailable()) {
            return ResponseEntity.status(503).body("Сервис временно недоступен. Пожалуйста, попробуйте позже.");
        }
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    /**delete post.*/
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по указанному ID")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
      @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
      @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id) {
        if (!statusService.isServerAvailable()) {
            return ResponseEntity.status(503).body("Сервис временно недоступен. Пожалуйста, попробуйте позже.");
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}