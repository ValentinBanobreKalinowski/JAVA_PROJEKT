package org.example.votingsystem.user.services;

import lombok.RequiredArgsConstructor;
import org.example.votingsystem.user.repository.UserRepository;
import org.example.votingsystem.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public List<User> searchUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getUsers();
        }
        return userRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                searchTerm, searchTerm, searchTerm);
    }

    public List<User> filterUsers(String name, String surname, Integer minAge, Integer maxAge) {
        List<User> users = getUsers();

        if (name != null && !name.trim().isEmpty()) {
            users = users.stream()
                    .filter(u -> u.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (surname != null && !surname.trim().isEmpty()) {
            users = users.stream()
                    .filter(u -> u.getSurname().toLowerCase().contains(surname.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (minAge != null) {
            users = users.stream()
                    .filter(u -> calculateAge(u.getDateOfBirth()) >= minAge)
                    .collect(Collectors.toList());
        }

        if (maxAge != null) {
            users = users.stream()
                    .filter(u -> calculateAge(u.getDateOfBirth()) <= maxAge)
                    .collect(Collectors.toList());
        }

        return users;
    }

    public void changeUserRole(Long userId, String newRole) {
        User user = findById(userId);
        if (!newRole.equals("ROLE_USER") && !newRole.equals("ROLE_MODERATOR") && !newRole.equals("ROLE_ADMIN")) {
            throw new IllegalArgumentException("Nieprawidłowa rola!");
        }
        user.setRole(newRole);
        userRepository.save(user);
    }
}

