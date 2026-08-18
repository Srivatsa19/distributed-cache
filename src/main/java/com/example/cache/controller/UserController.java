package com.example.cache.controller;

import com.example.cache.model.User;
import com.example.cache.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable long id, @RequestBody UpdateUserRequest request) {
        userService.updateUser(id, request.name());
    }

    public record UpdateUserRequest(String name) {
    }

}
