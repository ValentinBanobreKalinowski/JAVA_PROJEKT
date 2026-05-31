package org.example.votingsytsem.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public User modifyUser(Long id, User newUserInfo) {
        User user = findById(id);

        user.setId(newUserInfo.getId());
        user.setName(newUserInfo.getName());
        user.setSurname(newUserInfo.getSurname());
        user.setDateOfBirth(newUserInfo.getDateOfBirth());
        user.setPesel(newUserInfo.getPesel());

        return user;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
