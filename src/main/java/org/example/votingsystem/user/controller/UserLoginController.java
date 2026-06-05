package org.example.votingsystem.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.votingsystem.user.model.User;
import org.example.votingsystem.user.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class UserLoginController {

    private final UserService userService;

    @GetMapping("/register")
    public String registration() {
        return "welcome";
    }

    @GetMapping("/users/dashboard")
    public String logedIn() {
        return "userdashboard";
    }

    @PostMapping("/users")
    public String registerUser(User newUser) {
        userService.addUser(newUser);
        return "redirect:/login";
    }

    @GetMapping("/register-admin")
    public String adminRegistration() {
        return "register-admin";
    }

    @PostMapping("/register-admin")
    public String registerAdmin(User newUser) {
        newUser.setRole("ROLE_ADMIN");
        userService.addUser(newUser);
        return "redirect:/login";
    }
}
