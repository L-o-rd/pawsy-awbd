package com.awbd.pawsy.user.controller;

import com.awbd.pawsy.user.dto.UserCreateRequest;
import com.awbd.pawsy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody UserCreateRequest dto) {
        try {
            userService.registerUser(dto);
            return ResponseEntity.ok().build();
        } catch (Exception ignored) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/by-name/{username}")
    public ResponseEntity<?> getByUsername(@PathVariable String username) {
        var user = userService.getByUsername(username);
        return user.isPresent() ? ResponseEntity.ok().body(userService.summary(user.get())) :
                ResponseEntity.notFound().build();
    }
}
