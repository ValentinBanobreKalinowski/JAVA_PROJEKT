package org.example.votingsystem.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.votingsystem.user.model.User;
import org.example.votingsystem.user.services.UserService;
import org.springframework.stereotype.Controller;
    import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
@RequiredArgsConstructor
public class UserLoginController {

    private final UserService userService;

    @GetMapping("/register")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "welcome";
    }

    @GetMapping("/users/dashboard")
    public String logedIn() {
        return "userdashboard";
    }

    @PostMapping("/users")
    public String registerUser(@Valid User newUser, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) return "welcome";
        userService.addUser(newUser);
        return "redirect:/login";
    }

    @GetMapping("/register-admin")
    public String adminRegistration() {
        return "register-admin";
    }

    @PostMapping("/register-admin")
    public String registerAdmin(@Valid User newUser, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) return "register-admin";
        newUser.setRole("ROLE_ADMIN");
        userService.addUser(newUser);
        return "redirect:/login";
    }
}
