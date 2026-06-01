package org.example.votingsytsem.display;

import lombok.RequiredArgsConstructor;
import org.example.votingsytsem.user.LoginValidation;
import org.example.votingsytsem.user.User;
import org.example.votingsytsem.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class RegistrationDisplay {

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
        return "userdashboard.html";
    }
}
