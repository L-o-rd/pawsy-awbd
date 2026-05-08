package com.awbd.pawsy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

@Slf4j
@ControllerAdvice
public class PawsyExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleEntityNotFound(RuntimeException ex, Model model) {
        log.error("Runtime error", ex);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/5xx";
    }
}
