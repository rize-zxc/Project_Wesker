package com.example.postproject.exceptions;

/**class of response.*/
public class ErrorResponse {
    /**private messages and details.*/
    private String message;
    private String details;

    /**pointers.*/
    public ErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }

    /**create post.*/
    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}