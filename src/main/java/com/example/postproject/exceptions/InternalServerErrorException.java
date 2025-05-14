package com.example.postproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**class of InternalServerErrorException.*/
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {
    /**InternalServerErrorException.*/
        public InternalServerErrorException(String message) {
        super(message);
    }
}