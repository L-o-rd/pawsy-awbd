package com.awbd.pawsy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

import javax.naming.ServiceUnavailableException;

@Slf4j
@ControllerAdvice
public class PawsyExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        log.error("Runtime error encountered", ex);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/5xx";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceUnavailableException.class)
    public String handleServiceUnavailableException(ServiceUnavailableException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/5xx";
    }
}
