package org.example.votingsystem.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.votingsystem.user.model.User;
import org.example.votingsystem.user.services.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class

UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public User modifyUser(@PathVariable Long id, @Valid @RequestBody User newUserInfo) {
        return userService.modifyUser(id, newUserInfo);
    }
}
