package org.example.votingsystem.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "loginsite";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}
