package org.test.task.telegrambotcryptocurrency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.task.telegrambotcryptocurrency.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUserId(Long userId);

    Optional<User> findByUserId(Long userId);

    long deleteByUserId(Long userId);

}
