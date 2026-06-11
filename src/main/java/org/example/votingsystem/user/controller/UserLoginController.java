package org.example.votingsystem.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.votingsystem.user.dto.UserRegistrationDto;
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
        model.addAttribute("user", new UserRegistrationDto());
        return "register-user";
    }

    @GetMapping("/users/dashboard")
    public String logedIn() {
        return "userdashboard";
    }

    @PostMapping("/users")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto, BindingResult bindingResult) {

        if (registrationDto.getPassword() != null && !registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Podane hasła nie są identyczne");
        }

        if (userService.existsByEmail(registrationDto.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Ten email jest już zajęty");
        }
        if (userService.existsByPesel(registrationDto.getPesel())) {
            bindingResult.rejectValue("pesel", "error.user", "Ten PESEL jest już zarejestrowany");
        }

        if (bindingResult.hasErrors()) {
            return "register-user";
        }

        User newUser = new User();
        newUser.setName(registrationDto.getName());
        newUser.setSurname(registrationDto.getSurname());
        newUser.setEmail(registrationDto.getEmail());
        newUser.setPassword(registrationDto.getPassword());
        newUser.setDateOfBirth(registrationDto.getDateOfBirth());
        newUser.setPesel(registrationDto.getPesel());
        newUser.setRole("ROLE_USER");
        userService.addUser(newUser);
        return "redirect:/login";
    }

    @GetMapping("/register-admin")
    public String adminRegistration(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register-admin";
    }

    @PostMapping("/register-admin")
    public String registerAdmin(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto, BindingResult bindingResult) {

        if (registrationDto.getPassword() != null && !registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Podane hasła nie są identyczne");
        }

        if (userService.existsByEmail(registrationDto.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Ten email jest już zajęty");
        }
        if (userService.existsByPesel(registrationDto.getPesel())) {
            bindingResult.rejectValue("pesel", "error.user", "Ten PESEL jest już zarejestrowany");
        }

        if (bindingResult.hasErrors()) {
            return "register-admin";
        }

        User newAdmin = new User();
        newAdmin.setName(registrationDto.getName());
        newAdmin.setSurname(registrationDto.getSurname());
        newAdmin.setEmail(registrationDto.getEmail());
        newAdmin.setPassword(registrationDto.getPassword());
        newAdmin.setDateOfBirth(registrationDto.getDateOfBirth());
        newAdmin.setPesel(registrationDto.getPesel());
        newAdmin.setRole("ROLE_ADMIN");

        userService.addUser(newAdmin);
        return "redirect:/login";
    }
}