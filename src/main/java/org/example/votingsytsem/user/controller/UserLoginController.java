package org.example.votingsytsem.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.votingsytsem.user.model.User;
import org.example.votingsytsem.user.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class UserLoginController {

    private final UserService userService;

    @GetMapping("/register")
    public String registration() {
        return "welcome.html";
    }

    @GetMapping("/login")
    public String login() {
        return "loginsite.html";
    }

    @GetMapping("/users/dashboard")
    public String logedIn() {
        return "userdashboard.html";
    }

    @PostMapping("/users")
    public String registerUser(User newUser) {
        userService.addUser(newUser);
        return "redirect:/login";
    }
}
