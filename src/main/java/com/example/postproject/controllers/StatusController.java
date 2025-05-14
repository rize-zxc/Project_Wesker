package com.example.postproject.controllers;

import com.example.postproject.services.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**status controller.*/
@Tag(name = "Status Controller", description = "API для проверки статуса сервиса")
@Controller
public class StatusController {
    private final StatusService statusService;

    @SuppressWarnings({"checkstyle:MissingJavadocMethod"})
    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    /**main page.*/
    @Operation(summary = "Главная страница", description = "Возвращает главную страницу приложения")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Главная страница"),
      @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @GetMapping("/")
    public String index(Model model) {
        if (!statusService.isServerAvailable()) {
            model.addAttribute("message", "Сервис временно недоступен. Иди меняй статус.");
            return "error";
        }
        return "index";
    }

    /**get status of service.*/
    @Operation(summary = "Проверить/изменить статус", description = "Возвращает текущий статус сервиса или изменяет его")
    @ApiResponse(responseCode = "200", description = "Текущий статус сервиса")
    @GetMapping("/status")
    @ResponseBody
    public Map<String, String> checkStatus(
            @Parameter(description = "Новый статус сервиса (опционально)")
            @RequestParam(name = "status", required = false) String status) {
        return statusService.updateAndGetStatus(status);
    }
}