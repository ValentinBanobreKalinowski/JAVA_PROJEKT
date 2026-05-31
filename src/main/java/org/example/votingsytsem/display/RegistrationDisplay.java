package org.example.votingsytsem.display;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class RegistrationDisplay {

    @GetMapping("/register")
    public String registration() {
        return "welcome.html";
    }
}
