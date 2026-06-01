package org.example.votingsytsem.user;

import lombok.RequiredArgsConstructor;
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
        return user.modify(newUserInfo);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
