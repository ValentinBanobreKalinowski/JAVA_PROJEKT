package org.example.votingsystem.user.services;

import lombok.RequiredArgsConstructor;
import org.example.votingsystem.user.repository.UserRepository;
import org.example.votingsystem.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User modifyUser(Long id, User newUserInfo) {
        User user = findById(id);

        if (!user.getEmail().equals(newUserInfo.getEmail()) && userRepository.existsByEmail(newUserInfo.getEmail())) {
            throw new IllegalArgumentException("Ten e-mail jest już zajęty przez innego użytkownika!");
        }

        if (!user.getPesel().equals(newUserInfo.getPesel()) && userRepository.existsByPesel(newUserInfo.getPesel())) {
            throw new IllegalArgumentException("Ten PESEL należy już do kogoś innego!");
        }
        User updatedUser = user.modify(newUserInfo);
        return userRepository.save(updatedUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPesel(String pesel) {
        return userRepository.existsByPesel(pesel);
    }
}

