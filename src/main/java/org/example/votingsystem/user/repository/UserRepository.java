package org.example.votingsystem.user.repository;

import org.example.votingsystem.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findFirstByPesel(Long pesel);
}
